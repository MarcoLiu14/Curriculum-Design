package code;

public class FCB {     //文件目录项类
	
	private int iNodeID;     //文件iNode编号
	private String fileName;     //文件名
	
	public FCB(int iNodeID, String fileName) {
		this.iNodeID = iNodeID;
		this.fileName = fileName;
	}
	
	public int getINodeID() {     //获取文件iNode编号
		return this.iNodeID;
	}
	
	public String getfileName() {     //获取文件名
		return this.fileName;
	}
}
