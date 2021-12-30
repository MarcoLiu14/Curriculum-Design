package code;

import java.util.ArrayList;

public class Dispatch extends Thread {     //进程调度类

	private static int timeSlice = 2;     //时间片
	private static PCB currentProcess = new PCB();     //当前正在运行的进程
	private static boolean ifHangUp1 = false;     //进行中级调度的标志1
	private static boolean ifHangUp2 = true;     //进行中级调度的标志2
	private static boolean ifHangUp3 = false;     //进行中级调度的标志3
	private static boolean ifHangUp4 = true;     //进行中级调度的标志4
	
	private ArrayList<Block> tempBlockList = new ArrayList<Block>();     //存储挂起的物理块的数组
	
	public void run() {
		while(true) {
			Global.lock.lock();     //请求锁
			try {
				Global.condition.await();     //线程进入等待状态
				//每秒进行一次低级调度
				timeSliceRound();
				ifHangUp3 = true;
					
				//设置中级调度的条件
				if(JCB.getRequestList().size() >= 8) {
					ifHangUp1 = true;
				}
				else if(PCB.getBlockList1().size() >= 2) {
					ifHangUp1 = true;
				}
				else if(PCB.getBlockList2().size() >= 2) {
					ifHangUp1 = true;
				}
				else if(PCB.getBlockList3().size() >= 2) {
					ifHangUp1 = true;
				}
				
			    //进行中级调度
				if(ifHangUp1 && ifHangUp2 && ifHangUp3 && ifHangUp4) {
					middleSchedule();
					ifHangUp1 = false;
					ifHangUp3 = false;
				}
				
			    //每15秒进行一次激活操作
				if(CPU.getClock() % 15 == 0) {
					activate();
				}
				
				//输出相关信息
				GUI.pc_textArea.append(String.valueOf(CPU.getPC()));
				GUI.ir_textArea.append(String.valueOf(CPU.getIR()));
				GUI.psw_textArea.append(String.valueOf(CPU.getPSW()));
				if(CPU.getCpu_State()) {
					GUI.cpuState_textArea.append("工作中");
				}
				else {
					GUI.cpuState_textArea.append("空闲");
				}
				
				System.out.println("PC=" + CPU.getPC() + ",IR=" + CPU.getIR() + ",PSW=" + CPU.getPSW());
				Global.processResult += "PC=" + CPU.getPC() + ",IR=" + CPU.getIR() + ",PSW=" + CPU.getPSW() + "\n";
				
				PCB.showReadyList();
				PCB.showBlockList1();
				PCB.showBlockList2();
				PCB.showBlockList3();
				PCB.showHangUpList();
				PCB.showDeleteList();
			}catch(InterruptedException e) {
				e.printStackTrace();
			}finally {
				Global.lock.unlock();     //释放锁
			}	
		}
	}
	
	public static PCB getCurrentProcess() {     //返回当前正在执行的进程
		return currentProcess;
	}
	
	public static void setIfHangUp2(boolean ifhangup2) {     //设置挂起标志2
		ifHangUp2 = ifhangup2;
	}
	
	public static void setIfHangUp4(boolean ifhangup4) {     //设置挂起标志4
		ifHangUp4 = ifhangup4;
	}
	
	public static void sortReadyList() {     //根据优先级对就绪队列进行排序
		PCB temp;
		for(int i=0; i<PCB.getReadyList().size(); i++) {  
			for(int j=0; j<PCB.getReadyList().size()-1; j++) {
				if(PCB.getReadyList().get(i).getPriority() < PCB.getReadyList().get(j).getPriority()) {
					temp = PCB.getReadyList().get(i);
					PCB.getReadyList().set(i, PCB.getReadyList().get(j));
					PCB.getReadyList().set(j, temp);
				}
			}
		}
	}
	
	public static void cutTimeSlice() {     //时间片减1
		timeSlice--;
	}
	
	public static void switchProcess() {     //进程上下文切换
		//运行态->阻塞态
		currentProcess.setPSW(3);
		
		//阻塞态->就绪态
		currentProcess.setPSW(1);
		PCB.inReadyList(currentProcess);
		
		//切换新进程
		currentProcess = PCB.outReadyList();
		CPU.setPC(currentProcess.getPC());
		CPU.setIR(currentProcess.getIR());
		CPU.setPSW(currentProcess.getPSW());
		
		timeSlice = 2;     //重置时间片
		CPU.execute(currentProcess);     //CPU执行新的进程
	}
	
	//低级调度
	public void timeSliceRound() {
		if(CPU.getCpu_State()) {     //若当前CPU处于工作状态
			if(timeSlice > 0) {     //若时间片还有剩余，则继续执行当前进程
				CPU.execute(currentProcess);
	 		}
			else {     //若时间片已经用完，则执行进程上下文切换
				switchProcess();
			}
		}
		else {     //若当前CPU处于空闲状态
			if(PCB.getReadyList().size() != 0) {     //若当前就绪队列不为空
				timeSlice = 2;     //重置时间片
				currentProcess = PCB.outReadyList();     //执行队头进程
				CPU.execute(currentProcess);
			}
			else {     //若当前就绪队列为空，则CPU空闲
				System.out.println("CPU空闲！");
				GUI.console_textArea.append("CPU空闲！\n");
				Global.processResult += "CPU空闲！\n";
			}
		}
	}
	
	public int getLeftExchangeSections() {     //获取磁盘交换区剩余扇区数
		int count = 0;
		for(int i=0; i<Global.exchangeSectionNum; i++) {
			if(HardDisk.getEmptySection()[0][i] == 0) {
				count++;
			}
		}
		return count;
	}
	
	//中级调度
	public void middleSchedule() {
		int proID = -1;
		int count = 0;
		tempBlockList.clear();
		
		//确定要挂起的进程编号
		int blockNum = MMU.LRU();
		for(Block block : Memory.getBlockList()) {
			if(block.getStartAddress() >> 9 == blockNum) {
				proID = block.getProID();
				break;
			}
		}
		
		//确定要挂起的物理块数量
		for(int i=0; i<Memory.getBlockList().size(); i++) {
			if(Memory.getBlockList().get(i).getProID() == proID) {
				count++;
			}
		}
		
	     //若磁盘空间足够，则将进程挂起
		if(getLeftExchangeSections() >= count) {
			//确定要挂起的进程编号和用户区物理块
			for(int i=0; i<Memory.getBlockList().size(); i++) {
				if(Memory.getBlockList().get(i).getProID() == proID) {
					tempBlockList.add(Memory.getBlockList().get(i));
					Memory.getBlockList().remove(i);
					i = -1;
				}
			}
			
			for(PCB pcb : Memory.getPcbList()) {
				if(pcb.getProID() == proID) {
					pcb.setHangUpBlocks(tempBlockList);
					break;
				}
			}
			
			for(Block block : tempBlockList) {
				Memory.getUserEmptyBlock()[(block.getStartAddress() >> 9) - Global.userBlockNum] = 0;
				for(int i=0; i<Global.exchangeSectionNum; i++) {
					if(HardDisk.getEmptySection()[0][i] == 0) {
						if(i / 64 == 0) {
							Section section = new Section(0, i%64, block.getProID(), block.getPageNum());
							MMU.getTrack0().getSectionList().add(section);
							section.setInstructionsAndNumbers(block.getInstructionsAndNumbers());
						}
						else {
							Section section = new Section(1, i%64, block.getProID(), block.getPageNum());
							MMU.getTrack1().getSectionList().add(section);
							section.setInstructionsAndNumbers(block.getInstructionsAndNumbers());
						}
						HardDisk.getEmptySection()[0][i] = 1;
						break;
					}
				}
			}
				
			//删除对应的页表项
			for(PageTable pageTable : Memory.getPageTableList()) {
				if(pageTable.getProID() == proID) {
					pageTable.getEntryList().clear();
					break;
				}
			}
			
			//删除对应的快表项
			for(int i=0; i<MMU.getTlb().getItemList().size(); i++) {
				if(MMU.getTlb().getItemList().get(i).getProID() == proID) {
					MMU.getTlb().getItemList().remove(i);
					i = -1;
				}
			}
				
			//将相应PCB加入挂起队列，并记录其恢复位置
			for(PCB pcb : Memory.getPcbList()) {
				if(pcb.getProID() == proID) {
					if(pcb.getPSW() == 1 || pcb.getPSW() == 3) {     //若进程处于就绪态或阻塞态
						if(PCB.getBlockList2().indexOf(pcb) == 0) {     //若进程在阻塞队列2队首
							if(CPU.getRelease1()) {     //若已申请到缓冲区，则将缓冲区释放
								GUI.v1_textArea.append(String.valueOf(pcb.getProID()));
								System.out.println("释放缓冲区1！");
								GUI.console_textArea.append("释放缓冲区1！\n");
								Global.processResult += "释放缓冲区1！\n";
								CPU.setRelease1(false);
								
								PV.V1();
								PCB.getBlockList2().remove(pcb);
								if(pcb.getIR() < pcb.getInstrucNum()) {     //若该进程未执行完毕
									pcb.setLocation(1);     //激活后将恢复到就绪队列
								}
								else if(pcb.getIR() == pcb.getInstrucNum()) {     //若该进程已执行完毕
									pcb.setLocation(5);     //激活后将恢复到消亡队列
								}
							}
							else {     //若还没申请到缓冲区，则直接移出阻塞队列2
								PCB.getBlockList2().remove(pcb);
								pcb.setLocation(3);     //激活后恢复到阻塞队列2
							}
						}
						else if(PCB.getBlockList3().indexOf(pcb) == 0) {     //若进程在阻塞队列2队首
							if(CPU.getRelease2()) {     //若已申请到缓冲区，则将缓冲区释放
								GUI.v2_textArea.append(String.valueOf(pcb.getProID()));
								System.out.println("释放缓冲区2！");
								GUI.console_textArea.append("释放缓冲区2！\n");
								Global.processResult += "释放缓冲区2！\n";
								CPU.setRelease2(false);
								
								PV.V2();
								PCB.getBlockList3().remove(pcb);
								if(pcb.getIR() < pcb.getInstrucNum()) {     //若该进程未执行完毕
									pcb.setLocation(1);     //激活后将恢复到就绪队列
								}
								else if(pcb.getIR() == pcb.getInstrucNum()) {     //若该进程已执行完毕
									pcb.setLocation(5);     //激活后将恢复到消亡队列
								}
							}
							else {     //若还没申请到缓冲区，则直接移出阻塞队列3
								PCB.getBlockList3().remove(pcb);
								pcb.setLocation(4);     //激活后恢复到阻塞队列3
							}
						}
						
						else if(PCB.getReadyList().contains(pcb)) {     //若进程在就绪队列，则激活后恢复到就绪队列
							PCB.getReadyList().remove(pcb);
							pcb.setLocation(1);
						}
						else if(PCB.getBlockList1().contains(pcb)) {     //若进程在阻塞队列1，则激活后恢复到阻塞队列1
							PCB.getBlockList1().remove(pcb);
							pcb.setLocation(2);
						}
						else if(PCB.getBlockList2().contains(pcb)) {     //若进程在阻塞队列2，则激活后恢复到阻塞队列2
							PCB.getBlockList2().remove(pcb);
							pcb.setLocation(3);
						}
						else if(PCB.getBlockList3().contains(pcb)) {     //若进程在阻塞队列3，则激活后恢复到阻塞队列3
							PCB.getBlockList3().remove(pcb);
							pcb.setLocation(4);
						}
						else if(currentProcess.equals(pcb)) {     //若进程正在运行
							CPU.setCPU_State(false);
							pcb.setLocation(1);
						}
					}
					
					PCB.inHangUpList(pcb);     //将PCB加入挂起队列
					pcb.setPSW(4);
					System.out.println("挂起进程" + pcb.getProID());
					GUI.console_textArea.append("挂起进程" + pcb.getProID() + "\n");
					Global.processResult += "挂起进程" + pcb.getProID() + "\n";
				}
			}
			
		}
			
		//若空间不够，则无法进行挂起
		else {
			System.out.println("外存交换区剩余空间不足，无法挂起！");
			GUI.console_textArea.append("外存交换区剩余空间不足，无法挂起！\n");
			Global.processResult += "外存交换区剩余空间不足，无法挂起！\n";
		}
	}
	
	public void sortHangUpList() {     //对挂起队列按照优先级进行排序
		PCB temp;
		for(int i=0; i<PCB.getHangUpList().size(); i++) {
			for(int j=0; j<PCB.getHangUpList().size()-1; j++) {
				if(PCB.getHangUpList().get(i).getPriority() < PCB.getHangUpList().get(j).getPriority()) {
					temp = PCB.getHangUpList().get(i);
					PCB.getHangUpList().set(i, PCB.getHangUpList().get(j));
					PCB.getHangUpList().set(j, temp);
				}
			}
		}
	}
	
	public int getLeftUserBlocks() {     //获取内存用户区剩余物理块数
		int count = 0;
		for(int i=0; i<Global.userBlockNum; i++) {
			if(Memory.getUserEmptyBlock()[i] == 0) {
				count++;
			}
		}
		System.out.println("当前内存用户区空闲物理块" + count + "个");
		GUI.console_textArea.append("当前内存用户区空闲物理块" + count + "个\n");
		Global.processResult += "当前内存用户区空闲物理块" + count + "个\n";
		return count;
	}
	
	public void activate() {     //激活进程
		//首先根据优先级对挂起队列进行排序
		sortHangUpList();
		
		//进行激活操作
		for(PCB pcb : PCB.getHangUpList()) {
			if(getLeftUserBlocks() >= pcb.getHangUpBlocks().size()) {     //当内存用户区剩余物理块数足够时才能激活
				for(Block block : pcb.getHangUpBlocks()) {
					//将进程内容存入物理块
					for(int i=0; i<Global.userBlockNum; i++) {
						if(Memory.getUserEmptyBlock()[i] == 0) {
							Block block2 = new Block((Global.userBlockNum+i)<<9, block.getProID(), block.getPageNum());
							block2.setInstructionsAndNumbers(block.getInstructionsAndNumbers());
							Memory.getBlockList().add(block2);
							Memory.getUserEmptyBlock()[i] = 1;
							break;
						}
					}
					
					//更新页表
					for(PageTable pageTable : Memory.getPageTableList()) {
						if(pageTable.getProID() == pcb.getProID()) {
							for(Block block3 : Memory.getBlockList()) {
								if(block3.getProID() == pcb.getProID()) {
									pageTable.getEntryList().add(new Entry(block3.getPageNum(), block3.getStartAddress()>>9));
								}
							}
							break;
						}
					}
					
					//将磁盘交换区中相应扇区清空
					for(int i=0; i<MMU.getTrack0().getSectionList().size(); i++) {
						if(MMU.getTrack0().getSectionList().get(i).getProID() == block.getProID() && MMU.getTrack0().getSectionList().get(i).getPageNum() == block.getPageNum() && MMU.getTrack0().getSectionList().get(i).getSectionID() != -1) {
							HardDisk.getEmptySection()[0][MMU.getTrack0().getSectionList().get(i).getSectionID()] = 0;
							MMU.getTrack0().getSectionList().remove(i);
							i = -1;
						}
					}
					for(int i=0; i<MMU.getTrack1().getSectionList().size(); i++) {
						if(MMU.getTrack1().getSectionList().get(i).getProID() == block.getProID() && MMU.getTrack1().getSectionList().get(i).getPageNum() == block.getPageNum() && MMU.getTrack0().getSectionList().get(i).getSectionID() != -1) {
							HardDisk.getEmptySection()[0][MMU.getTrack1().getSectionList().get(i).getSectionID() + Global.sectionNum] = 0;
							MMU.getTrack1().getSectionList().remove(i);
							i = -1;
						}
					}
				}
				
				//将PCB移回相应队列
				if(pcb.getLocation() == 1) {
					PCB.inReadyList(pcb);
					pcb.setPSW(1);
				}
				else if(pcb.getLocation() == 2) {
					PCB.inBlockList1(pcb);
					MMU.setIfMissingPage(true);
					pcb.setPSW(3);
				}
				else if(pcb.getLocation() == 3) {
					PCB.inBlockList2(pcb);
					pcb.setPSW(3);
				}
				else if(pcb.getLocation() == 4) {
					PCB.inBlockList3(pcb);
					pcb.setPSW(3);
				}
				else if(pcb.getLocation() == 5) {
					pcb.delete();
				}
				PCB.getHangUpList().remove(pcb);     //将PCB移出挂起队列
				
				System.out.println("激活进程" + pcb.getProID());
				GUI.console_textArea.append("激活进程" + pcb.getProID() + "\n");
				Global.processResult += "激活进程" + pcb.getProID() + "\n";
				break;
			}
		}
	}
}
