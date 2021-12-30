package code;

import java.util.ArrayList;
import java.util.HashMap;

public class HardDisk {     //硬盘

	private static int[][] emptySection = new int[10][2048];     //位示图
	private static ArrayList<Cylinder> cylinderList = new ArrayList<Cylinder>();     //柱面列表
	
	public static int[][] getEmptySection() {     //获取位示图
		return emptySection;
	}
	
	public static ArrayList<Cylinder> getCylinderList() {     //获取柱面列表
		return cylinderList;
	}
	
	public static int getLeftExchangeSectionNum() {     //获取此时交换区剩余的扇区数
		int count = 0;
		for(int i=0; i<Global.exchangeSectionNum; i++) {
			if(emptySection[0][i] == 0) {
				count++;
			}
		}
		return count;
	}
	
	public static int getLeftUserFileSectionNum() {     //获取此时用户文件区剩余的扇区数
		int count = 0;
		for(int i=194; i<2048; i++) {
			if(emptySection[0][i] == 0) {
				count++;
			}
		}
		for(int m=1; m<10; m++) {
			for(int n=0; n<2048; n++) {
				if(emptySection[m][n] == 0) {
					count++;
				}
			}
		}
		return count;
	}
}

class Cylinder {     //柱面类
	
	private int cylinderID;     //柱面编号
	private ArrayList<Track> trackList = new ArrayList<Track>();     //磁道列表
	
	public Cylinder(int cylinderID) {
		this.cylinderID = cylinderID;
	}
	
	public int getCylinderID() {     //获取柱面编号
		return this.cylinderID;
	}
	
	public ArrayList<Track> getTrackList() {     //获取磁道列表
		return this.trackList;
	}
}

class Track {     //磁道类
	
	private int trackID;     //磁道编号
	private ArrayList<Section> sectionList = new ArrayList<Section>();     //扇区列表
	
	public Track(int trackID) {
		this.trackID = trackID;
	}
	
	public int getTrackID() {     //获取磁道编号
		return this.trackID;
	}
	
	public ArrayList<Section> getSectionList() {     //获取扇区列表
		return this.sectionList;
	}
}

class Section {     //扇区类
	private int cylinderID;     //扇区所在柱面编号
	private int trackID;     //扇区所在磁道编号
	private int sectionID;     //扇区编号
	private int proID;     //扇区存储进程的编号
	private int pageNum;     //扇区存储进程的页号
	private HashMap<Integer, String> instructionsAndNumbers = new HashMap<Integer, String>();    //交换区中存储的指令和数据
	
	public Section(int trackID, int sectionID, int proID, int pageNum) {
		this.trackID = trackID;
		this.sectionID = sectionID;
		this.proID = proID;
		this.pageNum = pageNum;
	}
	
	public Section(int cylinderID, int trackID, int sectionID) {
		this.cylinderID = cylinderID;
		this.trackID = trackID;
		this.sectionID = sectionID;
	}
	
	public int getCylinderID() {     //获取扇区所在柱面编号
		return this.cylinderID;
	}
	
	public int getTrackID() {     //获取扇区所在磁道编号
		return this.trackID;
	}
	
	public int getSectionID() {     //获取扇区编号
		return this.sectionID;
	}
	
	public void setSectionID(int sectionID) {     //设置扇区编号
		this.sectionID = sectionID;
	}
	
	public int getProID() {     //获取扇区存储进程的编号
		return this.proID;
	}
	
	public void setProID(int proID) {     //设置扇区存储进程的编号
		this.proID = proID;
	}
	
	public int getPageNum() {     //获取扇区存储进程的页号
		return pageNum;
	}
	
	public void setPageNum(int pageNum) {     //设置扇区存储进程的页号
		this.pageNum = pageNum;
	}
	
	public HashMap<Integer, String> getInstructionsAndNumbers() {     //获取虚存区（交换区）中存储的指令和数据
		return instructionsAndNumbers;
	}
	
	public void setInstructionsAndNumbers(HashMap<Integer, String> tempInstructionsAndNumbers) {     //设置虚存区中存储的指令和数据
		for(Integer integer : tempInstructionsAndNumbers.keySet()) {
			instructionsAndNumbers.put(integer, tempInstructionsAndNumbers.get(integer));
		}
	}
}
