package code;

public class PV {     //PV操作
	
	private static int mutex1 = 1;     //信号量1
	private static int mutex2 = 1;     //信号量2
	
	public static int getMutex1() {     //获取当前信号量1的值
		return mutex1;
	}
	
	public static int getMutex2() {     //获取当前信号量2的值
		return mutex2;
	}

	public synchronized static void P1(PCB pcb) {     //申请缓冲区1
		GUI.p1_textArea.append(String.valueOf(pcb.getProID()));
		mutex1--;
		useBufferZone(pcb);
	}
	
	public synchronized static void V1() {     //释放缓冲区1
		mutex1++;
	}
	
	public synchronized static void P2(PCB pcb) {     //申请缓冲区2
		GUI.p2_textArea.append(String.valueOf(pcb.getProID()));
		mutex2--;
		useBufferZone(pcb);
	}
	
	public synchronized static void V2() {     //释放缓冲区2
		mutex2++;
	}
	
	public static void useBufferZone(PCB pcb) {     //使用缓冲区
		for(Process process : Global.processList) {
			if(process.getProID() == pcb.getProID()) {
				if(process.getInstruc_State(pcb) == 1) {     //读文件
					CPU.setCurrentFilePath1(process.getCode_Segment().get(pcb.getIR()-1).getInstruc_Info());
					FileOperation.setIfRead(true);
				}
				else if(process.getInstruc_State(pcb) == 2) {     //写文件
					CPU.setCurrentFilePath2(process.getCode_Segment().get(pcb.getIR()-1).getInstruc_Info());
					FileOperation.setIfWrite(true);
				}
				else if(process.getInstruc_State(pcb) == 4) {     //系统输入
					IO.setInput(true);
				}
				else if(process.getInstruc_State(pcb) == 5) {     //系统输出
					IO.setOutput(true);
				}
				break;
			}
		}
	}
	
	public static void testDeadLock() {     //死锁检测
		//当有进程占用缓冲区1的同时申请缓冲区2，且同时有进程占用缓冲区2的同时申请缓冲区1，则发生死锁
		if(PCB.getBlockList2().size() > 0 && PCB.getBlockList3().size() > 0) {
			if(PCB.getBlockList2().contains(PCB.getBlockList3().get(0)) && PCB.getBlockList3().contains(PCB.getBlockList2().get(0))) {
				Global.deadLock = true;
			}
		}
	}
}
