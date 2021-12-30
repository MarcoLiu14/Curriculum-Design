package code;

import java.util.ArrayList;
import java.util.HashMap;

public class Memory {     //�ڴ�

	private static int[] pageTableEmptyBlock = new int[Global.maxPageTableNum];     //�ڴ�ҳ��λʾͼ
	private static int[] pcbEmptyBlock = new int[Global.maxPcbNum];     //�ڴ�PCB��λʾͼ
	private static int[] userEmptyBlock = new int[Global.userBlockNum];     //�ڴ��û���λʾͼ
	private static ArrayList<PageTable> pageTableList = new ArrayList<PageTable>();     //ҳ���б�
	private static ArrayList<PCB> pcbList = new ArrayList<PCB>();     //PCB��
	private static ArrayList<Block> blockList = new ArrayList<Block>();     //�û����ѷ����������б�
	private static String bufferZone1 = "";     //������1
	private static String bufferZone2 = "";     //������2
	
	public static int[] getPageTableEmptyBlock() {     //��ȡ�ڴ�ҳ��λʾͼ
		return pageTableEmptyBlock;
	}
	
	public static int[] getPCBEmptyBlock() {     //��ȡ�ڴ�PCB��λʾͼ
		return pcbEmptyBlock;
	}
	
	public static int[] getUserEmptyBlock() {     //��ȡ�ڴ���λʾͼ
		return userEmptyBlock;
	}
	
	public static ArrayList<PageTable> getPageTableList() {     //��ȡ��ʱ��ҳ���б�
		return pageTableList;
	}
	
	public static ArrayList<PCB> getPcbList() {     //��ȡPCB��
		return pcbList;
	}
	
	public static ArrayList<Block> getBlockList() {     //��ȡ��ʱ�û����ѷ����������б�
		return blockList;
	}
	
	public static String getBufferZone1() {     //��ȡ������1������
		return bufferZone1;
	}
	
	public static void setBufferZone1(String str) {     //���û�����1������
		bufferZone1 += str;
	}
	
	public static void emptyBufferZone1() {     //��ջ�����1
		bufferZone1 = "";
	}
	
	public static String getBufferZone2() {     //��ȡ������2������
		return bufferZone2;
	}
	
	public static void setBufferZone2(String str) {     //���û�����2������
		bufferZone2 += str;
	}
	
	public static void emptyBufferZone2() {     //��ջ�����2
		bufferZone2 = "";
	}
	
	public static boolean ifPcbListOk() {     //�ж�PCB���Ƿ��пռ�
		if(pcbList.size() < Global.maxPcbNum) {
			return true;
		}
		else {
			return false;
		}
	}
}

class Block {     //�������
	
	private int startAddress;     //��������ʼ��ַ
	private int proID;     //�������������̵ı��
	private int pageNum;     //�����������ָ���ҳ��
	private int visitTime;     //���һ�α�����ʱ��
	private HashMap<Integer, String> instructionsAndNumbers = new HashMap<Integer, String>();     //�û���������д洢��ָ�������
	
	public Block() {} 
	
	public Block(int startAddress, int proID, int pageNum) {
		this.startAddress = startAddress;
		this.proID = proID;
		this.pageNum = pageNum;
	}
	
	public int getStartAddress() {     //��ȡ����������ʼ��ַ
		return this.startAddress;
	}
	
	public int getProID() {     //��ȡ���������������̵ı��
		return proID;
	}
	
	public void setProID(int proID) {     //���ô��������������̵ı��
		this.proID = proID;
	}
	
	public int getPageNum() {     //��ȡ�������������ָ���ҳ��
		return pageNum;
	}
	
	public void setPageNum(int pageNum) {     //���ô������������ָ���ҳ��
		this.pageNum = pageNum;
	}
	
	public int getVisitTime() {     //��ȡ����������һ�α����ʵ�ʱ��
		return this.visitTime;
	}
	
	public void setVisitTime(int visitTime) {     //���ô���������һ�α����ʵ�ʱ��
		this.visitTime = visitTime;
	}
	
	public HashMap<Integer, String> getInstructionsAndNumbers() {     //��ȡ������д洢��ָ�������
		return this.instructionsAndNumbers;
	}
	
	public void setInstructionsAndNumbers(HashMap<Integer, String> tempInstructionsAndNumbers) {     //����������д洢��ָ�������
		for(Integer integer : tempInstructionsAndNumbers.keySet()) {
			instructionsAndNumbers.put(integer, tempInstructionsAndNumbers.get(integer));
		}
	}
}
