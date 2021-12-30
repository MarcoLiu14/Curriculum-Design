package code;

import java.util.ArrayList;
import java.util.LinkedList;

public class FileVolume {     //文件卷
	
	private static BootBlock bootBlock = new BootBlock();     //第0块是引导块
	private static SuperBlock superBlock = new SuperBlock();     //第1块是超级块
	
	private static ArrayList<INode> iNodeList = new ArrayList<INode>();     //系统iNode表
	private static ArrayList<DataBlock> dataBlockList = new ArrayList<DataBlock>();     //系统数据块列表
	
	public static BootBlock getBootBlock() {     //获取引导块
		return bootBlock;
	}
	
	public static SuperBlock getSuperBlock() {     //获取超级块
		return superBlock;
	}
	
	public static ArrayList<INode> getINodeList() {     //获取系统iNode表
		return iNodeList;
	}
	
	public static ArrayList<DataBlock> getDataBlockList() {     //获取系统数据块列表
		return dataBlockList;
	}

}

class BootBlock {     //引导块
	
}

class SuperBlock {     //超级块
	
	private int[] emptyINode = new int[Global.iNodeNum];     //iNode位示图
	private int[] sFree = new int[50];     //空闲块组
	private int snFree = 50;     //该组空闲块数
	private int firstFreeNum = 0;     //该组第一个空闲块的编号
	private int time = 0;     //调取新的空闲块队列的次数
	private LinkedList<DataBlock> blockGroupList = new LinkedList<DataBlock>();     //空闲数据块链表
	
	public int[] getEmptyINode() {     //获取iNode位示图
		return this.emptyINode;
	}
	
	public int[] getSFree() {     //获取空闲块组
		return this.sFree;
	}
	
	public int getSNFree() {     //获取该组空闲块数
		return this.snFree;
	}
	
	public int getFirstFreeNum() {     //获取该组第一个空闲块的编号
		return this.firstFreeNum;
	}
	
	public LinkedList<DataBlock> getBlockGroupList() {     //获取空闲数据块链表
		return this.blockGroupList;
	}
	
	public int distributeBlock(int iNodeNum) {     //分配一个空闲数据块，返回块号
		int temp;
		for(INode iNode : FileVolume.getINodeList()) {
			if(iNode.getId() == iNodeNum) {
				if(!ifHaveEmptyBlock()) {     //若此时空闲块队列中没有空闲数据块，则调取新的空闲块队列
					moveNewBlockList();
				}
				temp = allocate();
				iNode.getBlockNumList().add(temp);
				return temp;
			}
		}
		return -1;
	}
	
	public boolean ifHaveEmptyBlock() {     //判断当前队列是否还有空闲数据块
		if(snFree >= 1) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public int allocate() {     //返回分配的数据块号
		for(int i=0; i<50; i++) {
			if(sFree[i] == 0) {
				sFree[i] = 1;
				snFree--;
				return i+firstFreeNum;
			}
		}
		return -1;
	}
	
	public void moveNewBlockList() {     //调取新的空闲块队列
		time++;
		if(HardDisk.getLeftUserFileSectionNum() / 8 > 0) {
			for(int i=0; i<50; i++) {
				sFree[i] = 0;
			}
			snFree = 50;
			firstFreeNum = 50 * time;
		}
		else {
			System.out.println("此时已无空闲数据块！");
			GUI.console_textArea.append("此时已无空闲数据块！\n");
		}
	}
	
	public void findEmptySection(DataBlock dataBlock) {     //寻找空闲扇区
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

class INode {     //iNode类
	
	private int id;     //iNode编号
	private int uid;     //文件属主编号
	private int gid;     //文件属组编号
	private int mode;     //文件读写执行权限
	private int createTime;     //文件创建时间
	private int visitTime;     //文件最后访问时间
	private int modifyTime;     //文件最后修改时间
	private int size;     //文件大小
	private ArrayList<Integer> blockNumList = new ArrayList<Integer>();     //文件对应数据块的编号
	
	public INode(int id) {
		this.id = id;
	}
	
	public int getId() {     //获取iNode编号
		return this.id;
	}
	
	public int getUid() {     //获取文件属主编号
		return this.uid;
	}
	
	public int getGid() {     //获取文件属组编号
		return this.gid;
	}
	
	public int getMode() {     //获取文件读写执行权限
		return this.mode;
	}
	
	public int getCreateTime() {     //获取文件创建时间
		return this.createTime;
	}
	
	public int getVisitTime() {     //获取文件最后访问时间
		return this.visitTime;
	}
	
	public int getModifyTime() {     //获取文件最后修改时间
		return this.modifyTime;
	}
	
	public int getSize() {     //获取文件大小
		return this.size;
	}
	
	public ArrayList<Integer> getBlockNumList() {     //获取文件所处数据块的编号
		return this.blockNumList;
	}
}

class DataBlock {     //文件数据块类

	private int blockID;     //数据块编号
	private int iNodeID;     //数据块所属iNode编号
	private String data = "";     //文件数据
	private Section[] sectionList = new Section[8];     //组成该数据块的8个扇区
	
	public DataBlock(int blockID, int iNodeID, String data) {
		this.blockID = blockID;
		this.iNodeID = iNodeID;
		this.data = data;
	}
	
	public int getBlockID() {     //获取数据块编号
		return this.blockID;
	}
	
	public int getINodeID() {     //获取数据块所属iNode编号
		return this.iNodeID;
	}
	
	public String getData() {     //获取该数据块存储的数据
		return this.data;
	}
	
	public void setData(String data) {     //设置该数据块存储的数据
		this.data = data;
	}
	
	public void addData(String data) {     //添加该数据块存储的数据
		this.data += data;
	}
	
	public boolean ifBlockFull() {     //判断此时数据块内存储的数据是否超出一个数据块的容量
		if(data.length() > 2048) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public Section[] getSectionList() {     //获取组成该数据块的8个扇区
		return this.sectionList;
	}
}
