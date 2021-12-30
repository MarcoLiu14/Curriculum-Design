package code;

import java.util.ArrayList;

public class File {     //文件类

	private String path;     //文件路径
	private int iNodeID;     //iNode编号
	private ArrayList<FCB> fileMenu = new ArrayList<FCB>();     //文件目录
	
	public File(String path) {
		this.path = path;
	}
	
	public String getPath() {     //获取文件路径
		return this.path;
	}
	
	public int getINodeID() {     //获取iNode编号
		return this.iNodeID;
	}
	
	public ArrayList<FCB> getFileMenu() {     //获取文件目录
		return this.fileMenu;
	}
}
