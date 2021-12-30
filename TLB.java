package code;

import java.util.ArrayList;

public class TLB {     //快表

	private ArrayList<Item> itemList = new ArrayList<Item>();     //快表项列表

	public ArrayList<Item> getItemList() {     //获取快表项列表
		return this.itemList;
	}
}

class Item {     //快表项
	
	private int pageNum;     //页号
	private int blockNum;     //块号
	private int proID;     //快表项对应的进程编号
	
	public Item(int pageNum, int blockNum, int proid) {
		this.pageNum = pageNum;
		this.blockNum = blockNum;
		this.proID = proid;
	}
	
	public int getPageNum() {     //获取页号
		return this.pageNum;
	}
	
	public int getBlockNum() {     //获取块号
		return this.blockNum;
	}
	
	public int getProID() {     //获取快表项对应的进程编号
		return this.proID;
	}
}
