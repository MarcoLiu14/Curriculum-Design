package code;

public class IO extends Thread {     //输入输出线程
	
	private static boolean syn = false;     //同步标志
	private static boolean input = false;     //系统输入标志
	private static boolean output = false;     //系统输出标志
	
	public void run() {
		while(true) {
			Global.lock.lock();     //请求锁
			if(syn) {
				//申请及释放缓冲区操作
				apply();
				if(CPU.getRelease1()) {
					release1();
				}
				if(CPU.getRelease2()) {
					release2();
				}
				
				//可视化界面显示信号量的值
				GUI.mutex1_textArea.append(String.valueOf(PV.getMutex1()));
				GUI.mutex2_textArea.append(String.valueOf(PV.getMutex2()));
				
				//系统输入输出操作
				if(input) {
					systemInput();
					CPU.setRelease2(true);
					input = false;
				}
				if(output) {
					systemOutput();
					CPU.setRelease1(true);
					output = false;
				}
				
				syn = false;
			}
			Global.lock.unlock();     //释放锁
		}
	}
	
	public static void setSyn(boolean tempsyn) {     //设置同步标志
		syn = tempsyn;
	}
	
	public static void setInput(boolean tempInput) {     //设置系统输入标志
		input = tempInput;
	}
	
	public static void setOutput(boolean tempOutput) {     //设置系统输出标志
		output = tempOutput;
	}
	
	public synchronized void apply() {     //申请缓冲区
		if(PV.getMutex1() == 1) {     //若缓冲区1空闲，查看有无进程申请
			if(PCB.getBlockList2().size() > 0) {
				PCB temPcb = PCB.getBlockList2().get(0);
				System.out.println("申请缓冲区1成功！" + "当前使用缓冲区1的是" + temPcb.getProID() + "号进程");
				GUI.console_textArea.append("申请缓冲区1成功！" + "当前使用缓冲区1的是" + temPcb.getProID() + "号进程\n");
				GUI.buffer1_textArea.append(String.valueOf(temPcb.getProID()));
				Global.processResult += "申请缓冲区1成功！" + "当前使用缓冲区1的是" + temPcb.getProID() + "号进程\n";
				PV.P1(temPcb);
			}
			else {
				System.out.println("缓冲区1空闲！");
				GUI.console_textArea.append("缓冲区1空闲！\n");
				Global.processResult += "缓冲区1空闲！\n";
			}
		}
		if(PV.getMutex2() == 1) {     //若缓冲区2空闲，查看有无进程申请
			if(PCB.getBlockList3().size() > 0) {
				PCB temPcb = PCB.getBlockList3().get(0);
				System.out.println("申请缓冲区2成功！" + "当前使用缓冲区2的是" + temPcb.getProID() + "号进程");
				GUI.console_textArea.append("申请缓冲区2成功！" + "当前使用缓冲区2的是" + temPcb.getProID() + "号进程\n");
				GUI.buffer2_textArea.append(String.valueOf(temPcb.getProID()));
				Global.processResult += "申请缓冲区2成功！" + "当前使用缓冲区2的是" + temPcb.getProID() + "号进程\n";
				PV.P2(temPcb);
			}
			else {
				System.out.println("缓冲区2空闲！");
				GUI.console_textArea.append("缓冲区2空闲！\n");
				Global.processResult += "缓冲区2空闲！\n";
			}
		}
	}
	
	public synchronized void release1() {     //释放缓冲区1
		PCB temPcb = PCB.getBlockList2().get(0);
		GUI.v1_textArea.append(String.valueOf(temPcb.getProID()));
		PV.V1();
		
		if(temPcb.getDelete()) {     //若释放之后该进程要撤销，则执行撤销操作
			temPcb.setDelete(false);
			temPcb.delete();
		}
		else {
			temPcb.wake();
		}
		
		System.out.println("释放缓冲区1！");
		GUI.console_textArea.append("释放缓冲区1！\n");
		Global.processResult += "释放缓冲区1！\n";
		CPU.setRelease1(false);
	}
	
	public synchronized void release2() {     //释放缓冲区2
		PCB temPcb = PCB.getBlockList3().get(0);
		GUI.v2_textArea.append(String.valueOf(temPcb.getProID()));
		PV.V2();
		
		if(temPcb.getDelete()) {     //若释放之后该进程要撤销，则执行撤销操作
			temPcb.setDelete(false);
			temPcb.delete();
		}
		else {
			temPcb.wake();
		}
		
		System.out.println("释放缓冲区2！");
		GUI.console_textArea.append("释放缓冲区2！\n");
		Global.processResult += "释放缓冲区2！\n";
		CPU.setRelease2(false);
	}
	

	public void systemInput() {     //系统输入
		System.out.println("执行系统输入！");
		GUI.console_textArea.append("执行系统输入！\n");
		Global.processResult += "执行系统输入！\n";
		CPU.setRelease2(true);
	}
	
	public void systemOutput() {     //系统输出
		System.out.println("执行系统输出！");
		GUI.console_textArea.append("执行系统输出！\n");
		Global.processResult += "执行系统输出\n";
		CPU.setRelease1(true);
	}
}
