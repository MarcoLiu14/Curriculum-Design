package code;

public class FCB {     //�ļ�Ŀ¼����
	
	private int iNodeID;     //�ļ�iNode���
	private String fileName;     //�ļ���
	
	public FCB(int iNodeID, String fileName) {
		this.iNodeID = iNodeID;
		this.fileName = fileName;
	}
	
	public int getINodeID() {     //��ȡ�ļ�iNode���
		return this.iNodeID;
	}
	
	public String getfileName() {     //��ȡ�ļ���
		return this.fileName;
	}
}
