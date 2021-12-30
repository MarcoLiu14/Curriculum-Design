package code;

import java.util.ArrayList;
import java.util.LinkedList;

public class FileVolume {     //�ļ���
	
	private static BootBlock bootBlock = new BootBlock();     //��0����������
	private static SuperBlock superBlock = new SuperBlock();     //��1���ǳ�����
	
	private static ArrayList<INode> iNodeList = new ArrayList<INode>();     //ϵͳiNode��
	private static ArrayList<DataBlock> dataBlockList = new ArrayList<DataBlock>();     //ϵͳ���ݿ��б�
	
	public static BootBlock getBootBlock() {     //��ȡ������
		return bootBlock;
	}
	
	public static SuperBlock getSuperBlock() {     //��ȡ������
		return superBlock;
	}
	
	public static ArrayList<INode> getINodeList() {     //��ȡϵͳiNode��
		return iNodeList;
	}
	
	public static ArrayList<DataBlock> getDataBlockList() {     //��ȡϵͳ���ݿ��б�
		return dataBlockList;
	}

}

class BootBlock {     //������
	
}

class SuperBlock {     //������
	
	private int[] emptyINode = new int[Global.iNodeNum];     //iNodeλʾͼ
	private int[] sFree = new int[50];     //���п���
	private int snFree = 50;     //������п���
	private int firstFreeNum = 0;     //�����һ�����п�ı��
	private int time = 0;     //��ȡ�µĿ��п���еĴ���
	private LinkedList<DataBlock> blockGroupList = new LinkedList<DataBlock>();     //�������ݿ�����
	
	public int[] getEmptyINode() {     //��ȡiNodeλʾͼ
		return this.emptyINode;
	}
	
	public int[] getSFree() {     //��ȡ���п���
		return this.sFree;
	}
	
	public int getSNFree() {     //��ȡ������п���
		return this.snFree;
	}
	
	public int getFirstFreeNum() {     //��ȡ�����һ�����п�ı��
		return this.firstFreeNum;
	}
	
	public LinkedList<DataBlock> getBlockGroupList() {     //��ȡ�������ݿ�����
		return this.blockGroupList;
	}
	
	public int distributeBlock(int iNodeNum) {     //����һ���������ݿ飬���ؿ��
		int temp;
		for(INode iNode : FileVolume.getINodeList()) {
			if(iNode.getId() == iNodeNum) {
				if(!ifHaveEmptyBlock()) {     //����ʱ���п������û�п������ݿ飬���ȡ�µĿ��п����
					moveNewBlockList();
				}
				temp = allocate();
				iNode.getBlockNumList().add(temp);
				return temp;
			}
		}
		return -1;
	}
	
	public boolean ifHaveEmptyBlock() {     //�жϵ�ǰ�����Ƿ��п������ݿ�
		if(snFree >= 1) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public int allocate() {     //���ط�������ݿ��
		for(int i=0; i<50; i++) {
			if(sFree[i] == 0) {
				sFree[i] = 1;
				snFree--;
				return i+firstFreeNum;
			}
		}
		return -1;
	}
	
	public void moveNewBlockList() {     //��ȡ�µĿ��п����
		time++;
		if(HardDisk.getLeftUserFileSectionNum() / 8 > 0) {
			for(int i=0; i<50; i++) {
				sFree[i] = 0;
			}
			snFree = 50;
			firstFreeNum = 50 * time;
		}
		else {
			System.out.println("��ʱ���޿������ݿ飡");
			GUI.console_textArea.append("��ʱ���޿������ݿ飡\n");
		}
	}
	
	public void findEmptySection(DataBlock dataBlock) {     //Ѱ�ҿ�������
		boolean ifBreak = false;
		for(int count=0; count<8; count++) {
			for(int i=194; i<2048; i++) {
				if(HardDisk.getEmptySection()[0][i] == 0) {
					HardDisk.getEmptySection()[0][i] = 1;
					dataBlock.getSectionList()[count] = new Section(0, i/32, i%32);
					break;
				}
				if(i == 2047) {
					for(int m=1; m<10; m++) {
						for(int n=0; n<2048; n++) {
							if(HardDisk.getEmptySection()[m][n] == 0) {
								HardDisk.getEmptySection()[m][n] = 1;
								dataBlock.getSectionList()[count] = new Section(m, n/32, n%32);
								ifBreak = true;
								break;
							}
						}
						if(ifBreak) {
							break;
						}
					}
					if(ifBreak) {
						ifBreak = false;
						break;
					}
				}
			}
		}
	}
}

class INode {     //iNode��
	
	private int id;     //iNode���
	private int uid;     //�ļ��������
	private int gid;     //�ļ�������
	private int mode;     //�ļ���дִ��Ȩ��
	private int createTime;     //�ļ�����ʱ��
	private int visitTime;     //�ļ�������ʱ��
	private int modifyTime;     //�ļ�����޸�ʱ��
	private int size;     //�ļ���С
	private ArrayList<Integer> blockNumList = new ArrayList<Integer>();     //�ļ���Ӧ���ݿ�ı��
	
	public INode(int id) {
		this.id = id;
	}
	
	public int getId() {     //��ȡiNode���
		return this.id;
	}
	
	public int getUid() {     //��ȡ�ļ��������
		return this.uid;
	}
	
	public int getGid() {     //��ȡ�ļ�������
		return this.gid;
	}
	
	public int getMode() {     //��ȡ�ļ���дִ��Ȩ��
		return this.mode;
	}
	
	public int getCreateTime() {     //��ȡ�ļ�����ʱ��
		return this.createTime;
	}
	
	public int getVisitTime() {     //��ȡ�ļ�������ʱ��
		return this.visitTime;
	}
	
	public int getModifyTime() {     //��ȡ�ļ�����޸�ʱ��
		return this.modifyTime;
	}
	
	public int getSize() {     //��ȡ�ļ���С
		return this.size;
	}
	
	public ArrayList<Integer> getBlockNumList() {     //��ȡ�ļ��������ݿ�ı��
		return this.blockNumList;
	}
}

class DataBlock {     //�ļ����ݿ���

	private int blockID;     //���ݿ���
	private int iNodeID;     //���ݿ�����iNode���
	private String data = "";     //�ļ�����
	private Section[] sectionList = new Section[8];     //��ɸ����ݿ��8������
	
	public DataBlock(int blockID, int iNodeID, String data) {
		this.blockID = blockID;
		this.iNodeID = iNodeID;
		this.data = data;
	}
	
	public int getBlockID() {     //��ȡ���ݿ���
		return this.blockID;
	}
	
	public int getINodeID() {     //��ȡ���ݿ�����iNode���
		return this.iNodeID;
	}
	
	public String getData() {     //��ȡ�����ݿ�洢������
		return this.data;
	}
	
	public void setData(String data) {     //���ø����ݿ�洢������
		this.data = data;
	}
	
	public void addData(String data) {     //��Ӹ����ݿ�洢������
		this.data += data;
	}
	
	public boolean ifBlockFull() {     //�жϴ�ʱ���ݿ��ڴ洢�������Ƿ񳬳�һ�����ݿ������
		if(data.length() > 2048) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public Section[] getSectionList() {     //��ȡ��ɸ����ݿ��8������
		return this.sectionList;
	}
}
