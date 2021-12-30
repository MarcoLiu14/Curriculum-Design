package code;

public class PV {     //PV����
	
	private static int mutex1 = 1;     //�ź���1
	private static int mutex2 = 1;     //�ź���2
	
	public static int getMutex1() {     //��ȡ��ǰ�ź���1��ֵ
		return mutex1;
	}
	
	public static int getMutex2() {     //��ȡ��ǰ�ź���2��ֵ
		return mutex2;
	}

	public synchronized static void P1(PCB pcb) {     //���뻺����1
		GUI.p1_textArea.append(String.valueOf(pcb.getProID()));
		mutex1--;
		useBufferZone(pcb);
	}
	
	public synchronized static void V1() {     //�ͷŻ�����1
		mutex1++;
	}
	
	public synchronized static void P2(PCB pcb) {     //���뻺����2
		GUI.p2_textArea.append(String.valueOf(pcb.getProID()));
		mutex2--;
		useBufferZone(pcb);
	}
	
	public synchronized static void V2() {     //�ͷŻ�����2
		mutex2++;
	}
	
	public static void useBufferZone(PCB pcb) {     //ʹ�û�����
		for(Process process : Global.processList) {
			if(process.getProID() == pcb.getProID()) {
				if(process.getInstruc_State(pcb) == 1) {     //���ļ�
					CPU.setCurrentFilePath1(process.getCode_Segment().get(pcb.getIR()-1).getInstruc_Info());
					FileOperation.setIfRead(true);
				}
				else if(process.getInstruc_State(pcb) == 2) {     //д�ļ�
					CPU.setCurrentFilePath2(process.getCode_Segment().get(pcb.getIR()-1).getInstruc_Info());
					FileOperation.setIfWrite(true);
				}
				else if(process.getInstruc_State(pcb) == 4) {     //ϵͳ����
					IO.setInput(true);
				}
				else if(process.getInstruc_State(pcb) == 5) {     //ϵͳ���
					IO.setOutput(true);
				}
				break;
			}
		}
	}
	
	public static void testDeadLock() {     //�������
		//���н���ռ�û�����1��ͬʱ���뻺����2����ͬʱ�н���ռ�û�����2��ͬʱ���뻺����1����������
		if(PCB.getBlockList2().size() > 0 && PCB.getBlockList3().size() > 0) {
			if(PCB.getBlockList2().contains(PCB.getBlockList3().get(0)) && PCB.getBlockList3().contains(PCB.getBlockList2().get(0))) {
				Global.deadLock = true;
			}
		}
	}
}
