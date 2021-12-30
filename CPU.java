package code;

import java.util.Stack;

public class CPU {     //CPU
	
	private static int pc;     //下一条要执行的指令编号
	private static int ir;     //当前正在执行的指令编号
	private static int psw = 0;     //进程当前状态（0-用户态 1-内核态）
	private static int clock;     //当前系统时间
	private static boolean cpu_State = false;     //CPU工作状态
	
//	private static int r1 = 0;     //寄存器R1
//	private static int r2 = 0;     //寄存器R2
	
	private static String currentFilePath1;     //当前要读的文件路径
	private static String currentFilePath2;     //当前要写的文件路径
	private static String currentFilePath3;     //当前要删除的文件路径
	
	private static boolean delete = false;     //撤销进程标志
	private static boolean release1 = false;     //释放缓冲区1请求
	private static boolean release2 = false;     //释放缓冲区2请求
	
	private static Stack<PCB> userStack = new Stack<PCB>();     //用户栈
	private static Stack<PCB> coreStack = new Stack<PCB>();     //核心栈
	
	public static int getPC() {     //获取当前PC值
		return pc;
	}
	
	public static void setPC(int tempPC) {     //设置当前PC值
		pc = tempPC;
	}
	
	public static int getIR() {     //获取当前IR值
		return ir;
	}
	
	public static void setIR(int tempIR) {     //设置当前IR值
		ir = tempIR;
	}
	
	public static int getPSW() {     //获取当前PSW值
		return psw;
	}
	
	public static void setPSW(int tempPSW) {     //设置当前PSW值
		psw = tempPSW;
	}
	
	public static int getClock() {     //获取当前系统时间
		return clock;
	}
	
	public static void setClock() {     //更新当前系统时间
		clock++;
	}
	
	public static boolean getCpu_State() {     //获取当前CPU工作状态
		return cpu_State;
	}
	
	public static void setCPU_State(boolean state) {     //设置当前CPU工作状态
		cpu_State = state;
	}
	
//	public static int getR1() {     //获取当前R1的值
//		return r1;
//	}
//	
//	public static int getR2() {     //获取当前R2的值
//		return r2;
//	}
	
	public static String getCurrentFilePath1() {     //获取当前要读的文件路径
		return currentFilePath1;
	}
	
	public static void setCurrentFilePath1(String currentfilepath) {     //设置当前要读的文件路径
		currentFilePath1 = currentfilepath;
	}
	
	public static String getCurrentFilePath2() {     //获取当前要写的文件路径
		return currentFilePath2;
	}
	
	public static void setCurrentFilePath2(String currentfilepath) {     //设置当前要写的文件路径
		currentFilePath2 = currentfilepath;
	}
	
	public static String getCurrentFilePath3() {     //获取当前要删除的文件路径
		return currentFilePath3;
	}
	
	public static boolean getDelete() {     //获取撤销进程标志
		return delete;
	}
	
	public static void setDelete(boolean tempdelete) {     //设置撤销进程标志
		delete = tempdelete;
	}
	
	public static boolean getRelease1() {     //获取释放请求1
		return release1;
	}
	
	public static void setRelease1(boolean temprelease) {     //设置释放请求1
		release1 = temprelease;
	}
	
	public static boolean getRelease2() {     //获取释放请求2
		return release2;
	}
	
	public static void setRelease2(boolean temprelease) {     //设置释放请求2
		release2 = temprelease;
	}
	
	public static Stack<PCB>getUserStack() {     //获取用户栈
		return userStack;
	}
	
	public static void outUserInCore(PCB pcb) {     //PCB出用户栈，进入核心栈
		coreStack.push(pcb);
	}
	
	public static PCB outCoreInUser() {     //PCB出核心栈，入用户栈
		return coreStack.pop();
	}
	
	public static void closeInterrupt(PCB pcb) {     //关中断，从用户态到系统态，保护现场
		psw = 1;
		outUserInCore(pcb);
	}
	
	public static void openInterrupt(PCB pcb) {     //开中断，从系统态到用户态，恢复现场
		psw = 0;
		pcb = outCoreInUser();
	}
	
	public static void execute(PCB pcb) {     //指令执行
		String instruc_Info = "";
		for(Process process : Global.processList) {
			if(pcb.getProID() == process.getProID()) {
				if(pcb.getIR() == pcb.getInstrucNum()) {     //若此进程已执行完毕，则撤销该进程
					pcb.delete();
				}
				
				pc = pcb.getPC();     //同步修改CPU的PC
				pcb.setIR(pcb.getPC());     //此时的IR等于上一秒的PC
				ir = pcb.getIR();     //同步修改CPU的IR
				
				if(pcb.getIR() <= process.getInstrucNum()) {     //确保IR没有超出范围
					cpu_State = true;     //CPU变为工作状态
					pcb.plusRunTimes();     //进程运行时间+1
					Dispatch.cutTimeSlice();     //时间片-1
					
					//取指令内容
					for(Process temProcess : Global.processList) {
						if(temProcess.getProID() == pcb.getProID()) {
							instruc_Info = temProcess.getCode_Segment().get(pcb.getIR()-1).getInstruc_Info();
							break;
						}
					}
					
					//根据不同的指令类型执行相应操作
					switch(process.getInstruc_State(pcb)) {
						case 0:{     //运算赋值型指令
							fetchInstruction(pcb, process.getInstruc_LogicalAddress(pcb));
							if(!ifInterrupet(pcb)) {     //若取指成功，则执行指令
								runInstruc_0(process, pcb);
							}
							break;
						}
						case 1:{     //读文件指令
							fetchInstruction(pcb, process.getInstruc_LogicalAddress(pcb));
							if(!ifInterrupet(pcb)) {     //若取指成功，则执行指令
								runInstruc_1(process, pcb, instruc_Info);
							}
							break;
						}
						case 2:{     //写文件指令
							fetchInstruction(pcb, process.getInstruc_LogicalAddress(pcb));
							if(!ifInterrupet(pcb)) {     //若取指成功，则执行指令
								runInstruc_2(process, pcb, instruc_Info);
							}
							break;
						}
						case 3:{     //删除文件指令
							fetchInstruction(pcb, process.getInstruc_LogicalAddress(pcb));
							if(!ifInterrupet(pcb)) {     //若取指成功，则执行指令
								runInstruc_3(process, pcb, instruc_Info);
							}
							break;
						}
						case 4:{     //系统输入指令
							fetchInstruction(pcb, process.getInstruc_LogicalAddress(pcb));
							if(!ifInterrupet(pcb)) {     //若取指成功，则执行指令
								runInstruc_4(process, pcb);
							}
							break;
						}
						case 5:{     //系统输出指令
							fetchInstruction(pcb, process.getInstruc_LogicalAddress(pcb));
							if(!ifInterrupet(pcb)) {     //若取指成功，则执行指令
								runInstruc_5(process, pcb);
							}
							break;
						}
						default:{
							break;
						}
					}
					
					if(ifDelete(pcb)) {     //若撤销标志为真，则撤销该进程
						pcb.delete();
						cpu_State = false;
					}
					
					pcb.plusPC();     //PC+1
					pc = pcb.getPC();     //同步修改CPU的PC
				}
				break;
			}
		}
	}
	
	public static String fetchInstruction(PCB pcb, int logicalAddress) {     //获取指令内容
		return MMU.visitStorage(pcb, logicalAddress);
	}
	
	public static boolean ifInterrupet(PCB pcb) {     //判断取指是否发生缺页中断
		if(MMU.getIfMissingPage()) {     //若发生缺页中断，则处理一秒
			cpu_State = false;     //CPU置为空闲状态
			pcb.setPC(pc - 1);
			return true;
		}
		else {
			return false;
		}
	}
	
	public static void runInstruc_0(Process process, PCB pcb) {     //执行0型指令
		//处理指令内容
//		String[] s = instruc_Info.split(" ");
//		String string = "";
		
		//赋值型指令MOV
//		if(s[0].equals("0")) {
//			string += "MOV ";
//			if(s[1].equals("1")) {
//				string += "R1,";
//				string += s[2];
//				r1 = Integer.parseInt(s[2]);
//			}
//			else if(s[1].equals("2")) {
//				string += "R2,";
//				string += s[2];
//				r2 = Integer.parseInt(s[2]);
//			}
//		}
//		
//		//加法指令ADD
//		else if(s[0].equals("1")) {
//			string += "ADD ";
//			if(s[1].equals("1")) {
//				string += "R1,";
//				if(s[2].equals("1")) {
//					string += "R1";
//					r1 += r1;
//				}
//				else if(s[2].equals("2")) {
//					string += "R2";
//					r1 += r2;
//				}
//				else {
//					string += s[2];
//					r1 += Integer.parseInt(s[2]);
//				}
//			}
//			else if(s[1].equals("2")) {
//				string += "R2,";
//				if(s[2].equals("1")) {
//					string += "R1";
//					r2 += r1;
//				}
//				else if(s[2].equals("2")) {
//					string += "R2";
//					r2 += r2;
//				}
//				else {
//					string += s[2];
//					r2 += Integer.parseInt(s[2]);
//				}
//			}
//		}
//		
//		//减法指令SUB
//		else if(s[0].equals("2")) {
//			string += "SUB ";
//			if(s[1].equals("1")) {
//				string += "R1,";
//				if(s[2].equals("1")) {
//					string += "R1";
//					r1 -= r1;
//				}
//				else if(s[2].equals("2")) {
//					string += "R2";
//					r1 -= r2;		
//				}
//				else {
//					string += s[2];
//					r1 -= Integer.parseInt(s[2]);
//				}
//			}
//			else if(s[1].equals("2")) {
//				string += "R2,";
//				if(s[2].equals("1")) {
//					string += "R1";
//					r2 -= r1;
//				}
//				else if(s[2].equals("2")) {
//					string += "R2";
//					r2 -= r2;
//				}
//				else {
//					string += s[2];
//					r2 -= Integer.parseInt(s[2]);
//				}
//			}
//		}
		
		//输出相关信息
		System.out.println("CPU处于用户态，执行进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令，指令类型为运算赋值型");
		GUI.console_textArea.append("CPU处于用户态，执行进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令，指令类型为运算赋值型" + "\n");
		GUI.instruc_textArea.append("进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令\n");
		GUI.instruc_textArea.append("指令类型：运算赋值指令\n");
		Global.processResult += "CPU处于用户态，执行进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令，指令类型为运算赋值型" + "\n";
	}
	
	public static void runInstruc_1(Process process, PCB pcb, String instruc_Info) {     //执行1型指令
		//执行指令
		closeInterrupt(pcb);
		pcb.block();
		cpu_State = false;
		openInterrupt(pcb);
		
		//输出相关信息
		System.out.println("CPU处于系统态，执行进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令，指令功能为读文件" + "，文件路径为：" + instruc_Info);
		GUI.console_textArea.append("CPU处于系统态，执行进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令，指令功能为读文件" + "，文件路径为：" + instruc_Info + "\n");
		GUI.instruc_textArea.append("进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令\n");
		GUI.instruc_textArea.append("指令类型：读文件\n");
		GUI.instruc_textArea.append("文件地址：" + instruc_Info);
		Global.processResult += "CPU处于系统态，执行进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令，指令功能为读文件" + "，文件路径为：" + instruc_Info + "\n";
	}
	
	public static void runInstruc_2(Process process, PCB pcb, String instruc_Info) {     //执行2型指令
		//执行指令
		closeInterrupt(pcb);
		pcb.block();
		cpu_State = false;
		openInterrupt(pcb);
		
		//输出相关信息
		System.out.println("CPU处于系统态，执行进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令，指令功能为写文件" + "，文件路径为：" + instruc_Info);
		GUI.console_textArea.append("CPU处于系统态，执行进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令，指令功能为写文件" + "，文件路径为：" + instruc_Info + "\n");
		GUI.instruc_textArea.append("进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令\n");
		GUI.instruc_textArea.append("指令类型：写文件\n");
		GUI.instruc_textArea.append("文件地址：" + instruc_Info);
		Global.processResult += "CPU处于系统态，执行进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令，指令功能为写文件" + "，文件路径为：" + instruc_Info + "\n";
	}
	
	public static void runInstruc_3(Process process, PCB pcb, String instruc_Info) {     //执行3型指令
		FileOperation.setIfDelete(true);
		currentFilePath3 = instruc_Info;
		
		//输出相关信息
		System.out.println("CPU处于用户态，执行进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令，指令功能为删除文件" + "，文件路径为：" + instruc_Info);
		GUI.console_textArea.append("CPU处于用户态，执行进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令，指令功能为删除文件" + "，文件路径为：" + instruc_Info + "\n");
		GUI.instruc_textArea.append("进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令\n");
		GUI.instruc_textArea.append("指令类型：删除文件\n");
		GUI.instruc_textArea.append("文件地址：" + instruc_Info);
		Global.processResult += "CPU处于用户态，执行进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令，指令功能为删除文件" + "，文件路径为：" + instruc_Info + "\n";
	}
	
	public static void runInstruc_4(Process process, PCB pcb) {     //执行4型指令
		//执行指令
		closeInterrupt(pcb);
		pcb.block();
		cpu_State = false;
		openInterrupt(pcb);
		
		//输出相关信息
		System.out.println("CPU处于系统态，执行进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令，指令功能为系统输入");
		GUI.console_textArea.append("CPU处于系统态，执行进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令，指令功能为系统输入" + "\n");
		GUI.instruc_textArea.append("进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令\n");
		GUI.instruc_textArea.append("指令类型：系统输入\n");
		Global.processResult += "CPU处于系统态，执行进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令，指令功能为系统输入" + "\n";
	}
	
	public static void runInstruc_5(Process process, PCB pcb) {     //执行5型指令
		//执行指令
		closeInterrupt(pcb);
		pcb.block();
		cpu_State = false;
		openInterrupt(pcb);
		
		//输出相关信息
		System.out.println("CPU处于系统态，执行进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令，指令功能为系统输出");
		GUI.console_textArea.append("CPU处于系统态，执行进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令，指令功能为系统输出" + "\n");
		GUI.instruc_textArea.append("进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令\n");
		GUI.instruc_textArea.append("指令类型：系统输出\n");
		Global.processResult += "CPU处于系统态，执行进程" + pcb.getProID() + "的" + process.getInstruc_ID() + "号指令，指令功能为系统输出"+ "\n";
	}
	
	public static boolean ifDelete(PCB pcb) {     //判断此进程是否要被撤销
		if(pcb.getIR() == pcb.getInstrucNum()) {     //当前进程已执行完最后一条指令
			if(pcb.getPSW() != 3) {     //若该进程不处于阻塞态，则直接撤销
				return true;
			}
			else {     //若该进程处于阻塞态，则等其释放资源后再撤销
				pcb.setDelete(true);
				return false;
			}
		}
		else {
			return false;
		}
	}
}
