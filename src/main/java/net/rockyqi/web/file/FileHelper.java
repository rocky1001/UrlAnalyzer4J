package net.rockyqi.web.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

public class FileHelper {

	private static final Logger logger = Logger.getLogger(FileHelper.class.getName());

	public static List<String> readFileIntoLineList(String filePath) {
		File file = new File(filePath);
		if (!file.isFile() || !file.canRead()) {
			logger.error("File can not read:" + filePath);
			return null;
		}
		List<String> lineList = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			String lineString;
			lineList = new ArrayList<String>();
			while ((lineString = br.readLine()) != null) {
				// lineString = formatUrl(lineString);
				if (null != lineString && !"".equals(lineString)) {
					lineList.add(lineString);
				}
			}
		} catch (IOException e) {
			logger.error(e, e);
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					logger.error(e, e);
				}
			}
		}
		return lineList;
	}

	public static Vector<String> readFileIntoLineVector(String filePath) {
		File file = new File(filePath);
		if (!file.isFile() || !file.canRead()) {
			return null;
		}
		Vector<String> lineVector = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			String lineString;
			lineVector = new Vector<String>();
			while ((lineString = br.readLine()) != null) {
				// lineString = formatUrl(lineString);
				// if (null != lineString) {
				lineVector.add(lineString);
				// }
			}
		} catch (IOException e) {
			logger.error(e);
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
		return lineVector;
	}

	public static boolean writeDataIntoLocalFile(List<String> dataStringList, String localFileAbPath) {
		if (null == dataStringList || dataStringList.size() < 1) {
			logger.error("Data to write is empty:" + localFileAbPath);
			return false;
		}
		File file = new File(localFileAbPath);
		if (file.exists()) {
			logger.info("File already exist:" + localFileAbPath + ", now do delete.");
			file.delete();
		}
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
			for (String lineString : dataStringList) {
				if (null == lineString || "".equals(lineString)) {
					continue;
				}
				bw.write(lineString);
				bw.write("\n");
			}
			return true;
		} catch (IOException e) {
			logger.error(e);
			return false;
		} finally {
			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
	}

	public static void main(String[] args) {
		System.out.println("begin");
		long beginTime = System.currentTimeMillis();

		// read url from log file into urllist
		List<String> lineList = readFileIntoLineList("D:/temp/2013-06-01requestDAPURL.txt");
		if (null == lineList) {
			System.out.println("read failed!");
		} else {
			System.out.println("read succeed!");
		}
		long readTime = System.currentTimeMillis();
		System.out.println("finished READ in " + (readTime - beginTime) + " ms.");

		// write result to result txt file
		long writeTime = System.currentTimeMillis();
		boolean result = writeDataIntoLocalFile(lineList, "d:/temp/result.txt");
		if (false == result) {
			System.out.println("write failed!");
		} else {
			System.out.println("write succeed!");
		}
		long endTime = System.currentTimeMillis();
		System.out.println("finished WRITE in " + (endTime - writeTime) + " ms.");

	}

}
