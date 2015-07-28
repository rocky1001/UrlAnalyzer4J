package net.rockyqi.web.ftp;

public enum UploadStatus {
	Create_Directory_Fail, // 创建目录失败
	Create_Directory_Success, // 创建目录成功
	Upload_New_File_Success, // 上传新文件成功
	Upload_New_File_Failed, // 上传新文件失败
	File_Exits, // 文件已存在
	Remote_Bigger_Local, // 远端文件大于本地
	Upload_From_Break_Success, // 断点续传成功
	Upload_From_Break_Failed, // 断点续传失败
	Delete_Remote_Faild; // 删除远端文件失败
}
