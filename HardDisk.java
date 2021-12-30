package code;

import java.util.ArrayList;
import java.util.HashMap;

public class HardDisk {     //Ӳ��

	private static int[][] emptySection = new int[10][2048];     //λʾͼ
	private static ArrayList<Cylinder> cylinderList = new ArrayList<Cylinder>();     //�����б�
	
	public static int[][] getEmptySection() {     //��ȡλʾͼ
		return emptySection;
	}
	
	public static ArrayList<Cylinder> getCylinderList() {     //��ȡ�����б�
		return cylinderList;
	}
	
	public static int getLeftExchangeSectionNum() {     //��ȡ��ʱ������ʣ���������
		int count = 0;
		for(int i=0; i<Global.exchangeSectionNum; i++) {
			if(emptySection[0][i] == 0) {
				count++;
			}
		}
		return count;
	}
	
	public static int getLeftUserFileSectionNum() {     //��ȡ��ʱ�û��ļ���ʣ���������
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

class Cylinder {     //������
	
	private int cylinderID;     //������
	private ArrayList<Track> trackList = new ArrayList<Track>();     //�ŵ��б�
	
	public Cylinder(int cylinderID) {
		this.cylinderID = cylinderID;
	}
	
	public int getCylinderID() {     //��ȡ������
		return this.cylinderID;
	}
	
	public ArrayList<Track> getTrackList() {     //��ȡ�ŵ��б�
		return this.trackList;
	}
}

class Track {     //�ŵ���
	
	private int trackID;     //�ŵ����
	private ArrayList<Section> sectionList = new ArrayList<Section>();     //�����б�
	
	public Track(int trackID) {
		this.trackID = trackID;
	}
	
	public int getTrackID() {     //��ȡ�ŵ����
		return this.trackID;
	}
	
	public ArrayList<Section> getSectionList() {     //��ȡ�����б�
		return this.sectionList;
	}
}

class Section {     //������
	private int cylinderID;     //��������������
	private int trackID;     //�������ڴŵ����
	private int sectionID;     //�������
	private int proID;     //�����洢���̵ı��
	private int pageNum;     //�����洢���̵�ҳ��
	private HashMap<Integer, String> instructionsAndNumbers = new HashMap<Integer, String>();    //�������д洢��ָ�������
	
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
	
	public int getCylinderID() {     //��ȡ��������������
		return this.cylinderID;
	}
	
	public int getTrackID() {     //��ȡ�������ڴŵ����
		return this.trackID;
	}
	
	public int getSectionID() {     //��ȡ�������
		return this.sectionID;
	}
	
	public void setSectionID(int sectionID) {     //�����������
		this.sectionID = sectionID;
	}
	
	public int getProID() {     //��ȡ�����洢���̵ı��
		return this.proID;
	}
	
	public void setProID(int proID) {     //���������洢���̵ı��
		this.proID = proID;
	}
	
	public int getPageNum() {     //��ȡ�����洢���̵�ҳ��
		return pageNum;
	}
	
	public void setPageNum(int pageNum) {     //���������洢���̵�ҳ��
		this.pageNum = pageNum;
	}
	
	public HashMap<Integer, String> getInstructionsAndNumbers() {     //��ȡ����������������д洢��ָ�������
		return instructionsAndNumbers;
	}
	
	public void setInstructionsAndNumbers(HashMap<Integer, String> tempInstructionsAndNumbers) {     //����������д洢��ָ�������
		for(Integer integer : tempInstructionsAndNumbers.keySet()) {
			instructionsAndNumbers.put(integer, tempInstructionsAndNumbers.get(integer));
		}
	}
}
