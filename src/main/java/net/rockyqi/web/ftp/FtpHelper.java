package net.rockyqi.web.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import net.rockyqi.web.crawler.common.AppConstants;
import net.rockyqi.web.crawler.common.PropertyConst;
import net.rockyqi.web.crawler.common.PropertyMgr;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;


/** */
/**
 * FTP帮助类,提供FTP的下载,上传,删除文件等操作
 * 
 * @author qi.wenyuan@uniclick.cn
 * @date 2013-7-16
 */
public class FtpHelper {
	/**
	 * logger
	 */
	protected static final Logger logger = Logger.getLogger(FtpHelper.class.getName());

	public FTPClient ftpClient = new FTPClient();

	public FtpHelper() throws IOException {
		// init connection
		connect(PropertyMgr.getString(PropertyConst.FTP_SERVER), PropertyConst.FTP_PORT, PropertyConst.FTP_USER, PropertyConst.FTP_PWD);
	}

	/**
	 * 
	 * @Description:
	 * @author rockyqi1001@gmail.com
	 * @param hostname
	 * @param port
	 * @param username
	 * @param password
	 * @return
	 * @throws IOException
	 */
	private boolean connect(String hostname, int port, String username, String password) throws IOException {
		logger.debug("Start connect remote ftp:" + hostname);
		ftpClient.connect(hostname, port);
		logger.info("Finished connect remote ftp:" + hostname);
		ftpClient.setControlEncoding("GBK");
		if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
			logger.debug("Start login remote ftp:" + hostname);
			if (ftpClient.login(username, password)) {
				logger.info("Finished login remote ftp:" + hostname);
				return true;
			}
			logger.error("Failed login to remote ftp:" + hostname + ", reply code=" + ftpClient.getReplyCode());
		}
		disconnect();
		return false;
	}

	/**
	 * 
	 * @Description:
	 * @author rockyqi1001@gmail.com
	 * @param remotePath
	 * @return
	 * @throws IOException
	 */
	public String[] listFiles(String remotePath) throws IOException {
		// set binary file type
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		// list all file names under file path
		return ftpClient.listNames(new String(remotePath.getBytes("GBK"), "iso-8859-1"));
	}

	/**
	 * 
	 * @Description:
	 * @author rockyqi1001@gmail.com
	 * @param remotePath
	 * @return
	 * @throws IOException
	 */
	public boolean deleteFile(String remotePath) throws IOException {
		// set binary file type
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		// delete file
		return ftpClient.deleteFile(new String(remotePath.getBytes("GBK"), "iso-8859-1"));
	}

	/**
	 * 
	 * @Description:
	 * @author rockyqi1001@gmail.com
	 * @param remote
	 * @param local
	 * @return
	 * @throws IOException
	 */
	public DownloadStatus download(String remote, String local) throws IOException {
		logger.info("Start download file from " + remote);
		// set passive mode
		ftpClient.enterLocalActiveMode();
		// set binary file type
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		DownloadStatus result;

		// get file list
		FTPFile[] files = ftpClient.listFiles(new String(remote.getBytes("GBK"), "iso-8859-1"));
		if (files.length != 1) {
			logger.error("remote file:" + remote + " not exist.");
			return DownloadStatus.Remote_File_Noexist;
		}

		long lRemoteSize = files[0].getSize();
		File f = new File(local);
		// if file exist, need to start break download
		if (f.exists()) {
			long localSize = f.length();
			// local file is bigger than remote
			if (localSize >= lRemoteSize) {
				logger.info("local file is larger than remote one, now abort.");
				return DownloadStatus.Local_Bigger_Remote;
			}

			FileOutputStream out = new FileOutputStream(f, true);
			ftpClient.setRestartOffset(localSize);
			InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes("GBK"), "iso-8859-1"));
			byte[] bytes = new byte[1024];
			long step = lRemoteSize / 100;
			long process = localSize / step;
			int c;
			while ((c = in.read(bytes)) != -1) {
				out.write(bytes, 0, c);
				localSize += c;
				long nowProcess = localSize / step;
				if (nowProcess > process) {
					process = nowProcess;
					if (process % 10 == 0)
						logger.debug("download process:" + process);
				}
			}
			in.close();
			out.close();
			boolean isDo = ftpClient.completePendingCommand();
			if (isDo) {
				result = DownloadStatus.Download_Success;
			} else {
				result = DownloadStatus.Download_From_Break_Failed;
			}
		} else {
			OutputStream out = new FileOutputStream(f);
			InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes("GBK"), "iso-8859-1"));
			byte[] bytes = new byte[1024];
			long step = lRemoteSize / 100;
			long process = 0;
			long localSize = 0L;
			int c;
			while ((c = in.read(bytes)) != -1) {
				out.write(bytes, 0, c);
				localSize += c;
				long nowProcess = localSize / step;
				if (nowProcess > process) {
					process = nowProcess;
					if (process % 10 == 0)
						logger.debug("download process:" + process);
				}
			}
			in.close();
			out.close();
			boolean upNewStatus = ftpClient.completePendingCommand();
			if (upNewStatus) {
				result = DownloadStatus.Download_Success;
			} else {
				result = DownloadStatus.Download_New_Failed;
			}
		}
		logger.info("Finished download file to " + local);
		return result;
	}

	/**
	 * 
	 * @Description:
	 * @author rockyqi1001@gmail.com
	 * @param local
	 * @param remote
	 * @return
	 * @throws IOException
	 */
	public UploadStatus upload(String local, String remote) throws IOException {
		logger.info("Start upload file from " + local);
		// set passive mode
		ftpClient.enterLocalActiveMode();
		// set binary file type
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		ftpClient.setControlEncoding("GBK");
		UploadStatus result;
		// create remote file
		String remoteFileName = remote;
		if (remote.contains("/")) {
			remoteFileName = remote.substring(remote.lastIndexOf("/") + 1);
			// if create file failed, return failed cause
			if (CreateDirecroty(remote, ftpClient) == UploadStatus.Create_Directory_Fail) {
				return UploadStatus.Create_Directory_Fail;
			}
		}

		// list all remote files to check if local file to upload is exist, or remote file bigger than local  
		FTPFile[] files = ftpClient.listFiles(new String(remoteFileName.getBytes("GBK"), "iso-8859-1"));
		if (files.length == 1) {
			long remoteSize = files[0].getSize();
			File f = new File(local);
			long localSize = f.length();
			if (remoteSize == localSize) {
				return UploadStatus.File_Exits;
			} else if (remoteSize > localSize) {
				return UploadStatus.Remote_Bigger_Local;
			}

			// do upload file
			result = uploadFile(remoteFileName, f, ftpClient, remoteSize);

			// check and return result
			if (result == UploadStatus.Upload_From_Break_Failed) {
				if (!ftpClient.deleteFile(remoteFileName)) {
					return UploadStatus.Delete_Remote_Faild;
				}
				result = uploadFile(remoteFileName, f, ftpClient, 0);
			}
		} else {
			result = uploadFile(remoteFileName, new File(local), ftpClient, 0);
		}
		logger.info("Finished upload file to " + remote);
		return result;
	}

	/**
	 * 
	 * @Description:
	 * @author rockyqi1001@gmail.com
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		if (ftpClient.isConnected()) {
			ftpClient.disconnect();
		}
	}

	/**
	 * 
	 * @Description:
	 * @author rockyqi1001@gmail.com
	 * @param remote
	 * @param ftpClient
	 * @return
	 * @throws IOException
	 */
	public UploadStatus CreateDirecroty(String remote, FTPClient ftpClient) throws IOException {
		UploadStatus status = UploadStatus.Create_Directory_Success;
		String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
		if (!directory.equalsIgnoreCase("/") && !ftpClient.changeWorkingDirectory(new String(directory.getBytes("GBK"), "iso-8859-1"))) {
			ftpClient.changeWorkingDirectory("/");
			// create recursion directory
			int start = 0;
			int end = 0;
			if (directory.startsWith("/")) {
				start = 1;
			} else {
				start = 0;
			}
			end = directory.indexOf("/", start);
			while (true) {
				String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), "iso-8859-1");
				if (!ftpClient.changeWorkingDirectory(subDirectory)) {
					if (ftpClient.makeDirectory(subDirectory)) {
						ftpClient.changeWorkingDirectory(subDirectory);
					} else {
						logger.error("Create directory fail.");
						return UploadStatus.Create_Directory_Fail;
					}
				}

				start = end + 1;
				end = directory.indexOf("/", start);

				// if all sub-directory created then break 
				if (end <= start) {
					break;
				}
			}
		}
		return status;
	}

	/**
	 * 
	 * @Description:
	 * @author rockyqi1001@gmail.com
	 * @param remoteFile
	 * @param localFile
	 * @param ftpClient
	 * @param remoteSize
	 * @return
	 * @throws IOException
	 */
	public UploadStatus uploadFile(String remoteFile, File localFile, FTPClient ftpClient, long remoteSize) throws IOException {
		UploadStatus status;
		// do init
		long step = localFile.length() / 100;
		long process = 0;
		long localreadbytes = 0L;
		RandomAccessFile raf = new RandomAccessFile(localFile, "r");
		OutputStream out = ftpClient.appendFileStream(new String(remoteFile.getBytes("GBK"), "iso-8859-1"));
		// check remote size to find if need break upload
		if (remoteSize > 0) {
			ftpClient.setRestartOffset(remoteSize);
			process = remoteSize / step;
			raf.seek(remoteSize);
			localreadbytes = remoteSize;
		}
		byte[] bytes = new byte[1024];
		int c;
		while ((c = raf.read(bytes)) != -1) {
			out.write(bytes, 0, c);
			localreadbytes += c;
			if (localreadbytes / step != process) {
				process = localreadbytes / step;
				logger.debug("upload process:" + process);
			}
		}
		out.flush();
		raf.close();
		out.close();
		boolean result = ftpClient.completePendingCommand();
		if (remoteSize > 0) {
			status = result ? UploadStatus.Upload_From_Break_Success : UploadStatus.Upload_From_Break_Failed;
		} else {
			status = result ? UploadStatus.Upload_New_File_Success : UploadStatus.Upload_New_File_Failed;
		}
		return status;
	}
	
	public static String getLogFromFTP(String logName) {
		// 将去重后的URL日志通过FTP拷贝回本地用于分析
		logger.debug("Start get log file from ftp.");
		DownloadStatus downloadStatus = null;
		FtpHelper hadoopFtp = null;
		
		String remoteFile = PropertyMgr.getString(PropertyConst.FTP_DOWNLOAD_PATH) + logName;
		String tmpFolderPath = PropertyMgr.getString(PropertyConst.REAL_PATH) + AppConstants.TMP_FOLDER_NAME;
		String localFilePath = tmpFolderPath + File.separator + logName;
		
		File localFile = new File(localFilePath);
		if (localFile.exists()){
			localFile.delete();
		}
		try {
			hadoopFtp = new FtpHelper();
			// download remote file from remote ftp
			downloadStatus = hadoopFtp.download(remoteFile, localFilePath);
			// delete remote file after download, for saving the disk space
			//hadoopFtp.deleteFile(remoteFile);
		} catch (IOException e) {
			logger.error("Ftp error:" + e.getMessage());
			logger.error(e, e);
			return null;
		} finally {
			if (null != hadoopFtp) {
				try {
					hadoopFtp.disconnect();
				} catch (IOException e) {
					logger.error("Ftp diconnect error:" + e.getMessage());
				}
			}
			if (null == downloadStatus || DownloadStatus.Download_Success != downloadStatus) {
				logger.error("Ftp error:download failed.");
				return null;
			}
		}
		logger.debug("Finished get log file from ftp.");
		return localFilePath;
	}
	
	public static  boolean sendLogToFTP(String resultFilePath, String resultFileName) {
		// 将分析结果数据通过FTP传回hadoop平台供后续分析使用
		logger.debug("Start send log file to ftp.");
		boolean isSucceed = false;
		FtpHelper hadoopFtp = null;
		String remoteFile = PropertyMgr.getString(PropertyConst.FTP_UPLOAD_PATH) + resultFileName;
		
		try {
			hadoopFtp = new FtpHelper();
			// download remote file from remote ftp
			UploadStatus uploadStatus = hadoopFtp.upload(resultFilePath, remoteFile);
			// delete remote file after download, for saving the disk space
			//hadoopFtp.deleteFile(localFilePath);
			isSucceed = true;
			if (null == uploadStatus || UploadStatus.Upload_New_File_Success != uploadStatus) {
				logger.error("Ftp error:upload failed:" + uploadStatus.toString());
				isSucceed = false;
			} else {
				isSucceed = true;
			}
		} catch (IOException e) {
			logger.error("Ftp error:" + e.getMessage());
			logger.error(e, e);
		} finally {
			if (null != hadoopFtp) {
				try {
					hadoopFtp.disconnect();
				} catch (IOException e) {
					logger.error("Ftp diconnect error:" + e.getMessage());
				}
			}
		}
		logger.debug("Finished send log file to ftp.");
		return isSucceed;
	}

}
