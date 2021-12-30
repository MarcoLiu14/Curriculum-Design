package code;

import java.util.ArrayList;

public class File {     //�ļ���

	private String path;     //�ļ�·��
	private int iNodeID;     //iNode���
	private ArrayList<FCB> fileMenu = new ArrayList<FCB>();     //�ļ�Ŀ¼
	
	public File(String path) {
		this.path = path;
	}
	
	public String getPath() {     //��ȡ�ļ�·��
		return this.path;
	}
	
	public int getINodeID() {     //��ȡiNode���
		return this.iNodeID;
	}
	
	public ArrayList<FCB> getFileMenu() {     //��ȡ�ļ�Ŀ¼
		return this.fileMenu;
	}
}
