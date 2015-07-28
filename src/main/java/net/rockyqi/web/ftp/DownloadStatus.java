package net.rockyqi.web.ftp;

public enum DownloadStatus {

	Remote_File_Noexist, // 远端文件不存在
	Local_Bigger_Remote, // 本地文件大于远端文件
	Download_Success, // 下载成功
	Download_From_Break_Failed, // 断点续传失败
	Download_New_Failed; // 新下载成功

}
