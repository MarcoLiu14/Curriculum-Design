package code;

import java.util.ArrayList;

public class TLB {     //���

	private ArrayList<Item> itemList = new ArrayList<Item>();     //������б�

	public ArrayList<Item> getItemList() {     //��ȡ������б�
		return this.itemList;
	}
}

class Item {     //�����
	
	private int pageNum;     //ҳ��
	private int blockNum;     //���
	private int proID;     //������Ӧ�Ľ��̱��
	
	public Item(int pageNum, int blockNum, int proid) {
		this.pageNum = pageNum;
		this.blockNum = blockNum;
		this.proID = proid;
	}
	
	public int getPageNum() {     //��ȡҳ��
		return this.pageNum;
	}
	
	public int getBlockNum() {     //��ȡ���
		return this.blockNum;
	}
	
	public int getProID() {     //��ȡ������Ӧ�Ľ��̱��
		return this.proID;
	}
}
