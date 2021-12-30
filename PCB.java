package code;

import java.util.ArrayList;
import java.util.LinkedList;

public class PCB {     //PCB类

	private int proID;     //进程编号
	private int priority;     //进程优先级
	private int createTimes;     //进程创建时间
	private int endTimes;     //进程结束时间
	private int runTimes;     //进程运行时间
	private int turnTimes;     //进程周转时间
	private int instrucNum;     //进程包含的指令数目
	private int size;     //进程指令集大小
	private int pageTableStartAddress;     //进程页表起始地址
	private int pcbStartAddress;     //当前PCB存放的起始地址
	private int pc = 1;     //下一条要执行的指令编号
	private int ir = 0;     //当前正在执行的指令编号
	private int psw;     //进程当前状态（1-就绪  2-运行  3-阻塞  4-挂起  5-撤销）
	private boolean delete = false;     //进程撤销标志
	
	private static LinkedList<PCB> readyList = new LinkedList<PCB>();     //就绪队列
	private static LinkedList<PCB> blockList1 = new LinkedList<PCB>();     //阻塞队列1
	private static LinkedList<PCB> blockList2 = new LinkedList<PCB>();     //阻塞队列2
	private static LinkedList<PCB> blockList3 = new LinkedList<PCB>();     //阻塞队列3
	private static LinkedList<PCB> hangUpList = new LinkedList<PCB>();     //挂起队列
	private static LinkedList<PCB> deleteList = new LinkedList<PCB>();     //消亡队列
	
	private int rqNum;     //就绪队列位置编号
	private int rqTimes;     //进入就绪队列时间
	private int bqNum1;     //阻塞队列1位置编号
	private int bqTimes1;     //进入阻塞队列1的时间
	private int bqNum2;     //阻塞队列2位置编号
	private int bqTimes2;     //进入阻塞队列2的时间
	private int bqNum3;     //阻塞队列3位置编号
	private int bqTimes3;     //进入阻塞队列3的时间
	private int hqNum;     //挂起队列位置编号
	private int hqTimes;     //进入挂起队列的时间
	private int dqNum;     //消亡队列位置编号
	private int dqTimes;     //进入消亡队列的时间
	
	private int location;     //被挂起时所处队列（1-就绪队列 2-阻塞队列1 3-阻塞队列2 4-阻塞队列3 5-消亡队列）
	private ArrayList<Block> hangUpBlocks =  new ArrayList<Block>();     //被挂起的物理块
	
	public PCB() {}
	
	public PCB(int proID, int priority, int createTimes, int instrucNum, int size) {
		this.proID = proID;
		this.priority = priority;
		this.createTimes = createTimes;
		this.instrucNum = instrucNum;
		this.size = size;
		create();
	}
	
	public int getProID() {     //获取进程编号
		return this.proID;
	}
	
	public int getPriority() {     //获取进程优先级
		return this.priority;
	}
	
	public int getCreateTimes() {     //获取进程创建时间
		return this.createTimes;
	}
	
	public int getEndTimes() {     //获取进程结束时间
		return this.endTimes;
	}
	
	public int getRunTimes() {     //获取进程运行时间
		return this.runTimes;
	}
	
	public void plusRunTimes() {     //进程运行时间+1
		this.runTimes++;
	}
	
	public int getTurnTimes() {     //获取进程周转时间
		return this.turnTimes;
	}
	
	public int getInstrucNum() {     //获取进程包含的指令数目
		return this.instrucNum;
	}
	
	public int getSize() {     //获取进程指令集大小
		return this.size;
	}
	
	public void setPageTableStartAddress(int pageTableStartAddress) {     //设置进程页表的起始地址
		this.pageTableStartAddress = pageTableStartAddress;
	}
	
	public int getPageTableStartAddress() {     //获取当前进程页表的起始地址
		return this.pageTableStartAddress;
	}
	
	public void setPCBStartAddress(int pcbStartAddress) {     //设置当前PCB存放的起始地址
		this.pcbStartAddress = pcbStartAddress;
	}
	
	public int getPCBStartAddress() {     //获取当前PCB存放的起始地址
		return this.pcbStartAddress;
	}
	
	public int getPC() {     //获取下一条要执行的指令编号
		return this.pc;
	}
	
	public void setPC(int pc) {     //设置当前PC的值
		this.pc = pc;
	}
	
	public void plusPC() {     //PC+1
		this.pc++;
	}
	
	public int getIR() {     //获取当前正在执行的指令编号
		return this.ir;
	}
	
	public void setIR(int ir) {     //设置当前IR的值
		this.ir = ir;
	}
	
	public void plusIR() {     //IR+1
		this.ir++;
	}
	
	public int getPSW() {     //获取进程当前状态
		return this.psw;
	}
	
	public void setPSW(int psw) {     //设置当前PSW的值
		this.psw = psw;
	}
	
	public boolean getDelete() {     //获取当前的进程撤销标志
		return this.delete;
	}
	
	public void setDelete(boolean delete) {     //设置进程撤销标志
		this.delete = delete;
	}

	public static LinkedList<PCB> getReadyList() {     //获取就绪队列
		return readyList;
	}

	public static LinkedList<PCB> getBlockList1() {     //获取阻塞队列1
		return blockList1;
	}

	public static LinkedList<PCB> getBlockList2() {     //获取阻塞队列2
		return blockList2;
	}

	public static LinkedList<PCB> getBlockList3() {     //获取阻塞队列3
		return blockList3;
	}

	public static LinkedList<PCB> getHangUpList() {     //获取挂起队列
		return hangUpList;
	}
	
	public static LinkedList<PCB> getDeleteList() {     //获取消亡队列
		return deleteList;
	}

	public int getRqNum() {     //获取PCB在就绪队列中的位置
		return rqNum;
	}

	public void setRqNum(int rqNum) {     //设置PCB在就绪队列中的位置
		this.rqNum = rqNum;
	}

	public int getRqTimes() {     //获取PCB进入就绪队列的时间
		return rqTimes;
	}

	public void setRqTimes(int rqTimes) {     //设置PCB进入就绪队列的时间
		this.rqTimes = rqTimes;
	}
	
	public int getBqNum1() {     //获取PCB在阻塞队列1中的位置
		return bqNum1;
	}
	
	public void setBqNum1(int bqNum) {     //设置PCB在阻塞队列1中的位置
		this.bqNum1 = bqNum;
	}
	
	public int getBqTimes1() {     //获取PCB进入阻塞队列1的时间
		return bqTimes1;
	}
	
	public void setBqTimes1(int bqTime) {     //设置PCB进入阻塞队列1的时间
		this.bqTimes1 = bqTime;
	}
	
	public int getBqNum2() {     //获取PCB在阻塞队列2中的位置
		return bqNum2;
	}
	
	public void setBqNum2(int bqNum) {     //设置PCB在阻塞队列2中的位置
		this.bqNum2 = bqNum;
	}
	
	public int getBqTimes2() {     //获取PCB进入阻塞队列2的时间
		return bqTimes2;
	}
	
	public void setBqTimes2(int bqTime) {     //设置PCB进入阻塞队列2的时间
		this.bqTimes2 = bqTime;
	}
	
	public int getBqNum3() {     //获取PCB在阻塞队列3中的位置
		return bqNum3;
	}
	
	public void setBqNum3(int bqNum) {     //设置PCB在阻塞队列3中的位置
		this.bqNum3 = bqNum;
	}
	
	public int getBqTimes3() {     //获取PCB进入阻塞队列3的时间
		return bqTimes3;
	}
	
	public void setBqTimes3(int bqTime) {     //设置PCB进入阻塞队列3的时间
		this.bqTimes3 = bqTime;
	}
	
	public int getHqNum() {     //获取PCB在挂起队列中的位置
		return this.hqNum;
	}
	
	public void setHqNum(int hqNum) {     //设置PCB在挂起队列中的位置
		this.hqNum = hqNum;
	}
	
	public int getHqTimes() {     //获取PCB进入挂起队列的时间
		return this.hqTimes;
	}
	
	public void setHqTimes(int hqTimes) {     //设置PCB进入挂起队列的时间
		this.hqTimes = hqTimes;
	}
	
	public int getDqNum() {     //获取PCB在消亡队列中的位置
		return this.dqNum;
	}
	
	public void setDqNum(int dqNum) {     //设置PCB在消亡队列中的位置
		this.dqNum = dqNum;
	}
	
	public int getDqTimes() {     //获取PCB进入消亡队列的时间
		return this.dqTimes;
	}
	
	public void setDqTimes(int dqTimes) {     //设置PCB进入消亡队列的时间
		this.dqTimes = dqTimes;
	}
	
	public int getLocation() {     //获取被挂起时所处队列
		return this.location;
	}
	
	public void setLocation(int location) {     //设置被挂起时所处队列
		this.location = location;
	}
	
	public ArrayList<Block> getHangUpBlocks() {     //获取被挂起的物理块
		return this.hangUpBlocks;
	}
	
	public void setHangUpBlocks(ArrayList<Block> tempBlocks) {     //设置被挂起的物理块
		hangUpBlocks.clear();
		for(Block block : tempBlocks) {
			this.hangUpBlocks.add(block);
		}
	}
	
	public static void inReadyList(PCB pcb) {     //将PCB加入就绪队列
		readyList.offer(pcb);
		pcb.setRqNum(readyList.indexOf(pcb));
		pcb.setRqTimes(CPU.getClock());
	}
	
	public static PCB outReadyList() {     //将就绪队列队头PCB出队
		Dispatch.sortReadyList();     //对就绪队列按照静态优先级进行排序
		PCB temp = readyList.poll();     //将队头PCB出队
		for(PCB i: readyList) {     //更新就绪队列中PCB的位置
			i.setRqNum(readyList.indexOf(i));
		}
		return temp;
	}
	
	public static void inBlockList1(PCB pcb) {     //将PCB加入阻塞队列1
		blockList1.offer(pcb);
		pcb.bqNum1 = blockList1.indexOf(pcb);
		pcb.bqTimes1 = CPU.getClock();
	}
	
	public static void inBlockList2(PCB pcb) {     //将PCB加入阻塞队列2
		blockList2.offer(pcb);
		pcb.bqNum2 = blockList2.indexOf(pcb);
		pcb.bqTimes2 = CPU.getClock();
	}
	
	public static void inBlockList3(PCB pcb) {     //将PCB加入阻塞队列3
		blockList3.offer(pcb);
		pcb.bqNum3 = blockList3.indexOf(pcb);
		pcb.bqTimes3 = CPU.getClock();
	}
	
	public static void inHangUpList(PCB pcb) {     //将PCB加入挂起队列
		pcb.psw = 4;
		hangUpList.offer(pcb);
		pcb.hqNum = hangUpList.indexOf(pcb);
		pcb.hqTimes = CPU.getClock();
	}
	
	public static void inDeleteList(PCB pcb) {     //将PCB加入消亡队列
		deleteList.offer(pcb);
		pcb.dqNum = deleteList.indexOf(pcb);
		pcb.dqTimes = CPU.getClock();
	}
	
	public void outCurrentList() {     //将PCB从当前所处队列中移出
		if(this.psw == 1) {
			readyList.remove(this);
			for(PCB pcb : readyList) {
				pcb.rqNum = readyList.indexOf(pcb);
			}
		}
		else if(this.psw == 3) {
			if(blockList1.contains(this)) {
				blockList1.remove(this);
				for(PCB pcb : blockList1) {
					pcb.bqNum1 = blockList1.indexOf(pcb);
				}
			}
			else if(blockList2.contains(this)) {
				blockList2.remove(this);
				for(PCB pcb : blockList2) {
					pcb.bqNum2 = blockList2.indexOf(pcb);
				}
			}
			else if(blockList3.contains(this)) {
				blockList3.remove(this);
				for(PCB pcb : blockList3) {
					pcb.bqNum3 = blockList3.indexOf(pcb);
				}
			}
		}
	}
	
	public static void showReadyList() {     //展示就绪队列内容
		System.out.print("就绪队列：");
		Global.processResult += "就绪队列：";
		for(int i=0; i<readyList.size(); i++) {
			System.out.print(readyList.get(i).proID + " ");
			GUI.ready_textArea.append(readyList.get(i).proID + " ");
			Global.processResult += readyList.get(i).proID + " ";
		}
		System.out.println();
		Global.processResult += "\n";
	}
	
	public static void showBlockList1() {     //展示阻塞队列1内容
		System.out.print("阻塞队列1：");
		Global.processResult += "阻塞队列1：";
		for(PCB pcb : blockList1) {
			System.out.print(pcb.proID + " ");
			GUI.block1_textArea.append(pcb.proID + " ");
			Global.processResult += pcb.proID + " ";
		}
		System.out.println();
		Global.processResult += "\n";
	}
	
	public static void showBlockList2() {     //展示阻塞队列2内容
		System.out.print("阻塞队列2：");
		Global.processResult += "阻塞队列2：";
		for(PCB pcb : blockList2) {
			System.out.print(pcb.proID + " ");
			GUI.block2_textArea.append(pcb.proID + " ");
			Global.processResult += pcb.proID + " ";
		}
		System.out.println();
		Global.processResult += "\n";
	}
	
	public static void showBlockList3() {     //展示阻塞队列3内容
		System.out.print("阻塞队列3：");
		Global.processResult += "阻塞队列3：";
		for(PCB pcb : blockList3) {
			System.out.print(pcb.proID + " ");
			GUI.block3_textArea.append(pcb.proID + " ");
			Global.processResult += pcb.proID + " ";
		}
		System.out.println();
		Global.processResult += "\n";
	}
	
	public static void showHangUpList() {     //展示挂起队列内容
		System.out.print("挂起队列：");
		Global.processResult += "挂起队列：";
		for(PCB pcb : hangUpList) {
			System.out.print(pcb.proID + " ");
			GUI.hangUp_textArea.append(pcb.proID + " ");
			Global.processResult += pcb.proID + " ";
		}
		System.out.println();
		Global.processResult += "\n";
	}
	
	public static void showDeleteList() {     //展示消亡队列内容
		System.out.print("消亡队列：");
		Global.processResult += "消亡队列：";
		for(PCB pcb : deleteList) {
			System.out.print(pcb.proID + " ");
			GUI.delete_textArea.append(pcb.proID + " ");
			Global.processResult += pcb.proID + " ";
		}
		System.out.println();
		Global.processResult += "\n";
	}

	//创建进程的两个子操作
	public synchronized void create() {     //初始化PCB，并将PCB加入就绪队列中
		this.pc = 1;     //初始化相关寄存器的值
		this.ir = 0;
		this.psw = 1;
		this.setRqNum(readyList.size());
		this.setRqTimes(CPU.getClock());
		MMU.savePCB(this);     //将PCB存入PCB池
		inReadyList(this);     //将此PCB加入就绪队列
		FileOperation.getFileOperation().createFile("root/pro"+String.valueOf(this.proID), "root");     //创建进程文件
		System.out.println("作业" + this.proID + "进入内存，创建进程" + this.proID + "，进入时间为" + this.createTimes + "，所占大小为" + this.size + "页");
		GUI.console_textArea.append("作业" + this.proID + "进入内存，创建进程" + this.proID + "，进入时间为" + this.createTimes + "，所占大小为" + this.size + "页\n");
		Global.processResult += "作业" + this.proID + "进入内存，创建进程" + this.proID + "，进入时间为" + this.createTimes + "，所占大小为" + this.size + "页\n";
	}
	
	public static ArrayList<Instruction> init_Instruction(ArrayList<String[]> readInstruction){
		//创建进程之初始化程序段
		ArrayList<Instruction> instructionList = new ArrayList<Instruction>();     //进程指令集
		for(String[] i : readInstruction) {
			instructionList.add(new Instruction(Integer.parseInt(i[0]), Integer.parseInt(i[1]), Integer.parseInt(i[2]), i[3]));
		}
		return instructionList;
	}
	
	public synchronized void delete() {     //撤销进程
		this.outCurrentList();
		this.psw = 5;
		this.endTimes = CPU.getClock();
		for(JCB jcb : Global.jobList) {
			if(this.proID == jcb.getJobID()) {
				Global.jobList.remove(jcb);     //将此作业从系统作业表中移除
				this.turnTimes = this.endTimes - jcb.getInTimes();     //计算周转时间
				break;
			}
		}
		for(Process process : Global.processList) {     //将此进程从系统进程表中删除
			if(this.getProID() == process.getProID()) {
				Global.processList.remove(process);
				break;
			}
		}
		inDeleteList(this);
		MMU.deleteItem(this);     //回收该进程在快表中的表项
		MMU.recycleBlock(this);     //回收该进程对应的内存物理块
		MMU.recycleSection(this);     //回收该进程的扇区
		System.out.println("撤销进程" + this.proID + "，其运行时间为" + this.runTimes + "，周转时间为" + this.turnTimes);
		GUI.console_textArea.append("撤销进程" + this.proID + "，其运行时间为" + this.runTimes + "，周转时间为" + this.turnTimes + "\n");
		Global.processResult += "撤销进程" + this.proID + "，其运行时间为" + this.runTimes + "，周转时间为" + this.turnTimes + "\n";
	}
	
	public synchronized void block() {     //阻塞进程
		this.psw = 3;     //进程变为阻塞态
		if(MMU.getIfMissingPage()) {     //缺页中断的进程PCB进入阻塞队列1
			this.setBqNum1(blockList1.size());
			this.setBqTimes1(CPU.getClock());
			inBlockList1(this);
			return;
		}
		
		for(Process process : Global.processList) {     //将进程加入到对应的阻塞队列中
			if(this.proID == process.getProID()) {
				if(process.getInstruc_State(this) == 1) {
					this.setBqNum2(blockList2.size());
					this.setBqTimes2(CPU.getClock());
					inBlockList2(this);
				}
				else if(process.getInstruc_State(this) == 2) {
					this.setBqNum3(blockList3.size());
					this.setBqTimes3(CPU.getClock());
					inBlockList3(this);
				}
				else if(process.getInstruc_State(this) == 4) {
					this.setBqNum3(blockList3.size());
					this.setBqTimes3(CPU.getClock());
					inBlockList3(this);
				}
				else if(process.getInstruc_State(this) == 5) {
					this.setBqNum2(blockList2.size());
					this.setBqTimes2(CPU.getClock());
					inBlockList2(this);
				}
			}
		}
	}
	
	public synchronized void wake() {     //唤醒进程
		this.outCurrentList();
		this.psw = 1;     //进程变为就绪态
		inReadyList(this);     //将此PCB加入就绪队列
		Dispatch.sortReadyList();     //对就绪队列进行排序
	}
}
