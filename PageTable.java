package code;

import java.util.ArrayList;

public class PageTable {     //页表类

	private int startAddress;     //页表起始地址
	private int proID;     //页表对应的进程编号
	private ArrayList<Entry> entryList = new ArrayList<Entry>();     //页表项列表
	
	public PageTable(int startAddress, int proID) {
		this.startAddress = startAddress;
		this.proID = proID;
	}

	public int getStartAddress() {     //获取页表起始地址
		return this.startAddress;
	}
	
	public int getProID() {     //获取此页表对应的进程编号
		return this.proID;
	}
	
	public ArrayList<Entry> getEntryList() {     //获取页表项列表
		return this.entryList;
	}
	
	public void addEntry(int pageNum, int blockNum) {     //向页表中添加页表项
		entryList.add(new Entry(pageNum, blockNum));
	}
}

class Entry {     //页表项类
	
	private int pageNum;     //页号
	private int blockNum;     //块号
	
	public Entry(int pageNum, int blockNum) {
		this.pageNum = pageNum;
		this.blockNum = blockNum;
	}
	
	public int getPageNum() {     //获取此页表项的页号
		return this.pageNum;
	}
	
	public void setPageNum(int pageNum) {     //设置此页表项的页号
		this.pageNum = pageNum;
	}
	
	public int getBlockNum() {     //获取此页表项的块号
		return this.blockNum;
	}
}
