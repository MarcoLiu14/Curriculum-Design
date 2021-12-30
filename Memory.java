package code;

import java.util.ArrayList;
import java.util.HashMap;

public class Memory {     //内存

	private static int[] pageTableEmptyBlock = new int[Global.maxPageTableNum];     //内存页表位示图
	private static int[] pcbEmptyBlock = new int[Global.maxPcbNum];     //内存PCB池位示图
	private static int[] userEmptyBlock = new int[Global.userBlockNum];     //内存用户区位示图
	private static ArrayList<PageTable> pageTableList = new ArrayList<PageTable>();     //页表列表
	private static ArrayList<PCB> pcbList = new ArrayList<PCB>();     //PCB池
	private static ArrayList<Block> blockList = new ArrayList<Block>();     //用户区已分配的物理块列表
	private static String bufferZone1 = "";     //缓冲区1
	private static String bufferZone2 = "";     //缓冲区2
	
	public static int[] getPageTableEmptyBlock() {     //获取内存页表位示图
		return pageTableEmptyBlock;
	}
	
	public static int[] getPCBEmptyBlock() {     //获取内存PCB池位示图
		return pcbEmptyBlock;
	}
	
	public static int[] getUserEmptyBlock() {     //获取内存区位示图
		return userEmptyBlock;
	}
	
	public static ArrayList<PageTable> getPageTableList() {     //获取此时的页表列表
		return pageTableList;
	}
	
	public static ArrayList<PCB> getPcbList() {     //获取PCB池
		return pcbList;
	}
	
	public static ArrayList<Block> getBlockList() {     //获取此时用户区已分配的物理块列表
		return blockList;
	}
	
	public static String getBufferZone1() {     //获取缓冲区1的内容
		return bufferZone1;
	}
	
	public static void setBufferZone1(String str) {     //设置缓冲区1的内容
		bufferZone1 += str;
	}
	
	public static void emptyBufferZone1() {     //清空缓冲区1
		bufferZone1 = "";
	}
	
	public static String getBufferZone2() {     //获取缓冲区2的内容
		return bufferZone2;
	}
	
	public static void setBufferZone2(String str) {     //设置缓冲区2的内容
		bufferZone2 += str;
	}
	
	public static void emptyBufferZone2() {     //清空缓冲区2
		bufferZone2 = "";
	}
	
	public static boolean ifPcbListOk() {     //判断PCB池是否还有空间
		if(pcbList.size() < Global.maxPcbNum) {
			return true;
		}
		else {
			return false;
		}
	}
}

class Block {     //物理块类
	
	private int startAddress;     //物理块的起始地址
	private int proID;     //物理块中所存进程的编号
	private int pageNum;     //物理块中所存指令的页号
	private int visitTime;     //最近一次被访问时间
	private HashMap<Integer, String> instructionsAndNumbers = new HashMap<Integer, String>();     //用户区物理块中存储的指令和数据
	
	public Block() {} 
	
	public Block(int startAddress, int proID, int pageNum) {
		this.startAddress = startAddress;
		this.proID = proID;
		this.pageNum = pageNum;
	}
	
	public int getStartAddress() {     //获取此物理块的起始地址
		return this.startAddress;
	}
	
	public int getProID() {     //获取此物理块中所存进程的编号
		return proID;
	}
	
	public void setProID(int proID) {     //设置此物理块中所存进程的编号
		this.proID = proID;
	}
	
	public int getPageNum() {     //获取此物理块中所存指令的页号
		return pageNum;
	}
	
	public void setPageNum(int pageNum) {     //设置此物理块中所存指令的页号
		this.pageNum = pageNum;
	}
	
	public int getVisitTime() {     //获取此物理块最近一次被访问的时间
		return this.visitTime;
	}
	
	public void setVisitTime(int visitTime) {     //设置此物理块最近一次被访问的时间
		this.visitTime = visitTime;
	}
	
	public HashMap<Integer, String> getInstructionsAndNumbers() {     //获取物理块中存储的指令和数据
		return this.instructionsAndNumbers;
	}
	
	public void setInstructionsAndNumbers(HashMap<Integer, String> tempInstructionsAndNumbers) {     //设置物理块中存储的指令和数据
		for(Integer integer : tempInstructionsAndNumbers.keySet()) {
			instructionsAndNumbers.put(integer, tempInstructionsAndNumbers.get(integer));
		}
	}
}
