package net.rockyqi.web.ftp;

public enum UploadStatus {
	Create_Directory_Fail, // ����Ŀ¼ʧ��
	Create_Directory_Success, // ����Ŀ¼�ɹ�
	Upload_New_File_Success, // �ϴ����ļ��ɹ�
	Upload_New_File_Failed, // �ϴ����ļ�ʧ��
	File_Exits, // �ļ��Ѵ���
	Remote_Bigger_Local, // Զ���ļ����ڱ���
	Upload_From_Break_Success, // �ϵ������ɹ�
	Upload_From_Break_Failed, // �ϵ�����ʧ��
	Delete_Remote_Faild; // ɾ��Զ���ļ�ʧ��
}
