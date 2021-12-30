package code;

import java.io.IOException;
import java.util.ArrayList;

public class Request extends Thread {     //作业请求线程
	
	private String fileName = "input/19218106-jobs-input.txt";

	public void run() {
		while(true) {
			Global.lock.lock();     //请求锁
			try {
				Global.condition.await();     //线程进入等待状态
				try {
					if(CPU.getClock() % 5 == 0) {     //每5秒检查一次是否有新作业请求，并判断是否创建新进程
						checkIfCreate();
						JCB.showRequestList();
					}
				}catch (IOException e) {
					e.printStackTrace();
				}
			}catch (InterruptedException e) {
				e.printStackTrace();
			}finally {
				Global.lock.unlock();     //释放锁
			}	
		}
	}
	
	public ArrayList<int[]> receiveInputFile(String fileName) throws IOException {     
		//接收读取的并发作业请求文件信息并返回
		ArrayList<String[]> readString = new ArrayList<String[]>();
		ArrayList<int[]> readIntegers = new ArrayList<int[]>();
		readString = IOFile.readFile(fileName);
		
		for(String[] i: readString) {
			int[] temp = new int[5];
			for(int j=0; j<5; j++) {
				temp[j] = Integer.parseInt(i[j]);
			}
			readIntegers.add(temp);
		}
		
		return readIntegers;
	}
	
	public static void sortRequestList() {     //根据优先级对作业请求队列进行排序
		JCB jcb;
		for(int i=0; i<JCB.getRequestList().size()-1; i++) {  
			for(int j=0; j<JCB.getRequestList().size()-1-i; j++) {
				if(JCB.getRequestList().get(j).getPriority() > JCB.getRequestList().get(j+1).getPriority()) {
					jcb = JCB.getRequestList().get(j);
					JCB.getRequestList().set(j, JCB.getRequestList().get(j+1));
					JCB.getRequestList().set(j+1, jcb);
				}
			}
		}
	}
	
	public void checkIfCreate() throws IOException {     //判断此时是否有新作业请求
		ArrayList<int[]> readInput = new ArrayList<int[]>();
		readInput = receiveInputFile(fileName);
		
		int time = CPU.getClock();     //获取当前时间
		for(int[] i : readInput) {
			if(i[2] > time-5 && i[2] <= time) {     //若当前时间是某作业的提交时间，则生成新作业请求，并将其放入请求队列和系统作业表
				JCB jcb = new JCB(i[0], i[1], i[2], i[3], i[4]);
				JCB.getRequestList().add(jcb);
				Global.jobList.add(jcb);
				System.out.println("作业" + jcb.getJobID() + "提交，优先级为" + jcb.getPriority());
				GUI.console_textArea.append("作业" + jcb.getJobID() + "提交，优先级为" + jcb.getPriority() + "\n");
				Global.processResult += "作业" + jcb.getJobID() + "提交，优先级为" + jcb.getPriority() + "\n";
			}
		}
	
		//创建新进程并为其分配存储空间
		malloc();
	}
	
	public void malloc() throws IOException {     //创建新进程并为其分配存储空间
		ArrayList<String[]> readInstruction = new ArrayList<String[]>();
		
		sortRequestList();     //对作业请求队列进行排序
		while(JCB.getRequestList().size() != 0) {
			if(Memory.ifPcbListOk() && HardDisk.getLeftExchangeSectionNum() >= JCB.getRequestList().get(0).getSize() && Memory.getPcbList().size() < Global.maxPcbNum) {
			    //若此时PCB池与交换区的空间都还充裕
				//创建新进程
				JCB temp = JCB.getRequestList().poll();
				String instruc_fileName = "input/" + Integer.toString(temp.getJobID()) + ".txt";
				readInstruction = IOFile.readInstruc(instruc_fileName);     //读取指令文件
				new Process(temp.getJobID(), temp.getPriority(), temp.getInTimes(), temp.getInstrucNum(), temp.getSize(), PCB.init_Instruction(readInstruction));
				
				//生成页表
				for(PCB pcb : Memory.getPcbList()) {
					if(pcb.getProID() == temp.getJobID()) {
						pcb.setPageTableStartAddress(Memory.getPageTableList().size()*Global.maxEntryNum);
					}
				}
				Memory.getPageTableList().add(new PageTable(Memory.getPageTableList().size()*Global.maxEntryNum, temp.getJobID()));
				
				//将程序段存入外存虚存区
				MMU.allocateExchangeMemory(temp);
			}
			else {
				System.out.println("无法创建新进程！");
				GUI.console_textArea.append("无法创建新进程！\n");
				Global.processResult += "无法创建新进程！\n";
				break;
			}
		}
	}
}
