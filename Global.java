package code;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Global {     //全局变量

	public static ReentrantLock lock = new ReentrantLock();     //重入锁
	public static Condition condition = lock.newCondition();
	
	public static ArrayList<JCB> jobList = new ArrayList<JCB>();     //系统作业表
	public static ArrayList<Process> processList = new ArrayList<Process>();     //系统进程表
	public static ArrayList<File> fileList = new ArrayList<File>();     //系统文件表
	
	public static String processResult = "";     //保存文件内容
	
	public static boolean deadLock = false;     //死锁信号
	
	//内存分配情况
	public static int kernelBlockNum = 1;     //系统内核占1块
	public static int pcbBlockNum = 31;     //PCB池占31块
	public static int userBlockNum = 32;     //用户区占32块
	
	//磁盘分配情况
	public static int exchangeSectionNum = 96;     //磁盘交换区占96个扇区
	public static int systemFileSectionNum = 32;     //系统文件区占32个扇区
	public static int userFileSectionNum = 20352;     //文件区占20352个扇区
	public static int sectionNum = 64;     //每个磁道有64个扇区
	
	public static int size = 0x200;     //页、物理块、扇区的大小为512B 
	public static int maxPageTableNum = 32;     //最多存储32个页表
	public static int maxPcbNum = 15;     //PCB池中最多存储15个PCB
	public static int maxEntryNum = 8;     //页表中最多存储8个页表项
	public static int maxItemNum = 5;     //快表中最多存储5个表项
	
	//磁盘文件卷分配情况
	public static int iNodeNum = 64;     //iNode共占64个
}
