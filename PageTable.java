package code;

import java.util.ArrayList;

public class PageTable {     //ҳ����

	private int startAddress;     //ҳ����ʼ��ַ
	private int proID;     //ҳ���Ӧ�Ľ��̱��
	private ArrayList<Entry> entryList = new ArrayList<Entry>();     //ҳ�����б�
	
	public PageTable(int startAddress, int proID) {
		this.startAddress = startAddress;
		this.proID = proID;
	}

	public int getStartAddress() {     //��ȡҳ����ʼ��ַ
		return this.startAddress;
	}
	
	public int getProID() {     //��ȡ��ҳ���Ӧ�Ľ��̱��
		return this.proID;
	}
	
	public ArrayList<Entry> getEntryList() {     //��ȡҳ�����б�
		return this.entryList;
	}
	
	public void addEntry(int pageNum, int blockNum) {     //��ҳ�������ҳ����
		entryList.add(new Entry(pageNum, blockNum));
	}
}

class Entry {     //ҳ������
	
	private int pageNum;     //ҳ��
	private int blockNum;     //���
	
	public Entry(int pageNum, int blockNum) {
		this.pageNum = pageNum;
		this.blockNum = blockNum;
	}
	
	public int getPageNum() {     //��ȡ��ҳ�����ҳ��
		return this.pageNum;
	}
	
	public void setPageNum(int pageNum) {     //���ô�ҳ�����ҳ��
		this.pageNum = pageNum;
	}
	
	public int getBlockNum() {     //��ȡ��ҳ����Ŀ��
		return this.blockNum;
	}
}
