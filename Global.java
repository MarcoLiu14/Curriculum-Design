package code;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Global {     //ȫ�ֱ���

	public static ReentrantLock lock = new ReentrantLock();     //������
	public static Condition condition = lock.newCondition();
	
	public static ArrayList<JCB> jobList = new ArrayList<JCB>();     //ϵͳ��ҵ��
	public static ArrayList<Process> processList = new ArrayList<Process>();     //ϵͳ���̱�
	public static ArrayList<File> fileList = new ArrayList<File>();     //ϵͳ�ļ���
	
	public static String processResult = "";     //�����ļ�����
	
	public static boolean deadLock = false;     //�����ź�
	
	//�ڴ�������
	public static int kernelBlockNum = 1;     //ϵͳ�ں�ռ1��
	public static int pcbBlockNum = 31;     //PCB��ռ31��
	public static int userBlockNum = 32;     //�û���ռ32��
	
	//���̷������
	public static int exchangeSectionNum = 96;     //���̽�����ռ96������
	public static int systemFileSectionNum = 32;     //ϵͳ�ļ���ռ32������
	public static int userFileSectionNum = 20352;     //�ļ���ռ20352������
	public static int sectionNum = 64;     //ÿ���ŵ���64������
	
	public static int size = 0x200;     //ҳ������顢�����Ĵ�СΪ512B 
	public static int maxPageTableNum = 32;     //���洢32��ҳ��
	public static int maxPcbNum = 15;     //PCB�������洢15��PCB
	public static int maxEntryNum = 8;     //ҳ�������洢8��ҳ����
	public static int maxItemNum = 5;     //��������洢5������
	
	//�����ļ���������
	public static int iNodeNum = 64;     //iNode��ռ64��
}
