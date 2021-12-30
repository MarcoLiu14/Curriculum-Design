package code;

import java.util.Stack;

public class CPU {     //CPU
	
	private static int pc;     //��һ��Ҫִ�е�ָ����
	private static int ir;     //��ǰ����ִ�е�ָ����
	private static int psw = 0;     //���̵�ǰ״̬��0-�û�̬ 1-�ں�̬��
	private static int clock;     //��ǰϵͳʱ��
	private static boolean cpu_State = false;     //CPU����״̬
	
//	private static int r1 = 0;     //�Ĵ���R1
//	private static int r2 = 0;     //�Ĵ���R2
	
	private static String currentFilePath1;     //��ǰҪ�����ļ�·��
	private static String currentFilePath2;     //��ǰҪд���ļ�·��
	private static String currentFilePath3;     //��ǰҪɾ�����ļ�·��
	
	private static boolean delete = false;     //�������̱�־
	private static boolean release1 = false;     //�ͷŻ�����1����
	private static boolean release2 = false;     //�ͷŻ�����2����
	
	private static Stack<PCB> userStack = new Stack<PCB>();     //�û�ջ
	private static Stack<PCB> coreStack = new Stack<PCB>();     //����ջ
	
	public static int getPC() {     //��ȡ��ǰPCֵ
		return pc;
	}
	
	public static void setPC(int tempPC) {     //���õ�ǰPCֵ
		pc = tempPC;
	}
	
	public static int getIR() {     //��ȡ��ǰIRֵ
		return ir;
	}
	
	public static void setIR(int tempIR) {     //���õ�ǰIRֵ
		ir = tempIR;
	}
	
	public static int getPSW() {     //��ȡ��ǰPSWֵ
		return psw;
	}
	
	public static void setPSW(int tempPSW) {     //���õ�ǰPSWֵ
		psw = tempPSW;
	}
	
	public static int getClock() {     //��ȡ��ǰϵͳʱ��
		return clock;
	}
	
	public static void setClock() {     //���µ�ǰϵͳʱ��
		clock++;
	}
	
	public static boolean getCpu_State() {     //��ȡ��ǰCPU����״̬
		return cpu_State;
	}
	
	public static void setCPU_State(boolean state) {     //���õ�ǰCPU����״̬
		cpu_State = state;
	}
	
//	public static int getR1() {     //��ȡ��ǰR1��ֵ
//		return r1;
//	}
//	
//	public static int getR2() {     //��ȡ��ǰR2��ֵ
//		return r2;
//	}
	
	public static String getCurrentFilePath1() {     //��ȡ��ǰҪ�����ļ�·��
		return currentFilePath1;
	}
	
	public static void setCurrentFilePath1(String currentfilepath) {     //���õ�ǰҪ�����ļ�·��
		currentFilePath1 = currentfilepath;
	}
	
	public static String getCurrentFilePath2() {     //��ȡ��ǰҪд���ļ�·��
		return currentFilePath2;
	}
	
	public static void setCurrentFilePath2(String currentfilepath) {     //���õ�ǰҪд���ļ�·��
		currentFilePath2 = currentfilepath;
	}
	
	public static String getCurrentFilePath3() {     //��ȡ��ǰҪɾ�����ļ�·��
		return currentFilePath3;
	}
	
	public static boolean getDelete() {     //��ȡ�������̱�־
		return delete;
	}
	
	public static void setDelete(boolean tempdelete) {     //���ó������̱�־
		delete = tempdelete;
	}
	
	public static boolean getRelease1() {     //��ȡ�ͷ�����1
		return release1;
	}
	
	public static void setRelease1(boolean temprelease) {     //�����ͷ�����1
		release1 = temprelease;
	}
	
	public static boolean getRelease2() {     //��ȡ�ͷ�����2
		return release2;
	}
	
	public static void setRelease2(boolean temprelease) {     //�����ͷ�����2
		release2 = temprelease;
	}
	
	public static Stack<PCB>getUserStack() {     //��ȡ�û�ջ
		return userStack;
	}
	
	public static void outUserInCore(PCB pcb) {     //PCB���û�ջ���������ջ
		coreStack.push(pcb);
	}
	
	public static PCB outCoreInUser() {     //PCB������ջ�����û�ջ
		return coreStack.pop();
	}
	
	public static void closeInterrupt(PCB pcb) {     //���жϣ����û�̬��ϵͳ̬�������ֳ�
		psw = 1;
		outUserInCore(pcb);
	}
	
	public static void openInterrupt(PCB pcb) {     //���жϣ���ϵͳ̬���û�̬���ָ��ֳ�
		psw = 0;
		pcb = outCoreInUser();
	}
	
	public static void execute(PCB pcb) {     //ָ��ִ��
		String instruc_Info = "";
		for(Process process : Global.processList) {
			if(pcb.getProID() == process.getProID()) {
				if(pcb.getIR() == pcb.getInstrucNum()) {     //���˽�����ִ����ϣ������ý���
					pcb.delete();
				}
				
				pc = pcb.getPC();     //ͬ���޸�CPU��PC
				pcb.setIR(pcb.getPC());     //��ʱ��IR������һ���PC
				ir = pcb.getIR();     //ͬ���޸�CPU��IR
				
				if(pcb.getIR() <= process.getInstrucNum()) {     //ȷ��IRû�г�����Χ
					cpu_State = true;     //CPU��Ϊ����״̬
					pcb.plusRunTimes();     //��������ʱ��+1
					Dispatch.cutTimeSlice();     //ʱ��Ƭ-1
					
					//ȡָ������
					for(Process temProcess : Global.processList) {
						if(temProcess.getProID() == pcb.getProID()) {
							instruc_Info = temProcess.getCode_Segment().get(pcb.getIR()-1).getInstruc_Info();
							break;
						}
					}
					
					//���ݲ�ͬ��ָ������ִ����Ӧ����
					switch(process.getInstruc_State(pcb)) {
						case 0:{     //���㸳ֵ��ָ��
							fetchInstruction(pcb, process.getInstruc_LogicalAddress(pcb));
							if(!ifInterrupet(pcb)) {     //��ȡָ�ɹ�����ִ��ָ��
								runInstruc_0(process, pcb);
							}
							break;
						}
						case 1:{     //���ļ�ָ��
							fetchInstruction(pcb, process.getInstruc_LogicalAddress(pcb));
							if(!ifInterrupet(pcb)) {     //��ȡָ�ɹ�����ִ��ָ��
								runInstruc_1(process, pcb, instruc_Info);
							}
							break;
						}
						case 2:{     //д�ļ�ָ��
							fetchInstruction(pcb, process.getInstruc_LogicalAddress(pcb));
							if(!ifInterrupet(pcb)) {     //��ȡָ�ɹ�����ִ��ָ��
								runInstruc_2(process, pcb, instruc_Info);
							}
							break;
						}
						case 3:{     //ɾ���ļ�ָ��
							fetchInstruction(pcb, process.getInstruc_LogicalAddress(pcb));
							if(!ifInterrupet(pcb)) {     //��ȡָ�ɹ�����ִ��ָ��
								runInstruc_3(process, pcb, instruc_Info);
							}
							break;
						}
						case 4:{     //ϵͳ����ָ��
							fetchInstruction(pcb, process.getInstruc_LogicalAddress(pcb));
							if(!ifInterrupet(pcb)) {     //��ȡָ�ɹ�����ִ��ָ��
								runInstruc_4(process, pcb);
							}
							break;
						}
						case 5:{     //ϵͳ���ָ��
							fetchInstruction(pcb, process.getInstruc_LogicalAddress(pcb));
							if(!ifInterrupet(pcb)) {     //��ȡָ�ɹ�����ִ��ָ��
								runInstruc_5(process, pcb);
							}
							break;
						}
						default:{
							break;
						}
					}
					
					if(ifDelete(pcb)) {     //��������־Ϊ�棬�����ý���
						pcb.delete();
						cpu_State = false;
					}
					
					pcb.plusPC();     //PC+1
					pc = pcb.getPC();     //ͬ���޸�CPU��PC
				}
				break;
			}
		}
	}
	
	public static String fetchInstruction(PCB pcb, int logicalAddress) {     //��ȡָ������
		return MMU.visitStorage(pcb, logicalAddress);
	}
	
	public static boolean ifInterrupet(PCB pcb) {     //�ж�ȡָ�Ƿ���ȱҳ�ж�
		if(MMU.getIfMissingPage()) {     //������ȱҳ�жϣ�����һ��
			cpu_State = false;     //CPU��Ϊ����״̬
			pcb.setPC(pc - 1);
			return true;
		}
		else {
			return false;
		}
	}
	
	public static void runInstruc_0(Process process, PCB pcb) {     //ִ��0��ָ��
		//����ָ������
//		String[] s = instruc_Info.split(" ");
//		String string = "";
		
		//��ֵ��ָ��MOV
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
//		//�ӷ�ָ��ADD
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
//		//����ָ��SUB
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
		
		//��������Ϣ
		System.out.println("CPU�����û�̬��ִ�н���" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ�ָ������Ϊ���㸳ֵ��");
		GUI.console_textArea.append("CPU�����û�̬��ִ�н���" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ�ָ������Ϊ���㸳ֵ��" + "\n");
		GUI.instruc_textArea.append("����" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ��\n");
		GUI.instruc_textArea.append("ָ�����ͣ����㸳ֵָ��\n");
		Global.processResult += "CPU�����û�̬��ִ�н���" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ�ָ������Ϊ���㸳ֵ��" + "\n";
	}
	
	public static void runInstruc_1(Process process, PCB pcb, String instruc_Info) {     //ִ��1��ָ��
		//ִ��ָ��
		closeInterrupt(pcb);
		pcb.block();
		cpu_State = false;
		openInterrupt(pcb);
		
		//��������Ϣ
		System.out.println("CPU����ϵͳ̬��ִ�н���" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ�ָ���Ϊ���ļ�" + "���ļ�·��Ϊ��" + instruc_Info);
		GUI.console_textArea.append("CPU����ϵͳ̬��ִ�н���" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ�ָ���Ϊ���ļ�" + "���ļ�·��Ϊ��" + instruc_Info + "\n");
		GUI.instruc_textArea.append("����" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ��\n");
		GUI.instruc_textArea.append("ָ�����ͣ����ļ�\n");
		GUI.instruc_textArea.append("�ļ���ַ��" + instruc_Info);
		Global.processResult += "CPU����ϵͳ̬��ִ�н���" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ�ָ���Ϊ���ļ�" + "���ļ�·��Ϊ��" + instruc_Info + "\n";
	}
	
	public static void runInstruc_2(Process process, PCB pcb, String instruc_Info) {     //ִ��2��ָ��
		//ִ��ָ��
		closeInterrupt(pcb);
		pcb.block();
		cpu_State = false;
		openInterrupt(pcb);
		
		//��������Ϣ
		System.out.println("CPU����ϵͳ̬��ִ�н���" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ�ָ���Ϊд�ļ�" + "���ļ�·��Ϊ��" + instruc_Info);
		GUI.console_textArea.append("CPU����ϵͳ̬��ִ�н���" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ�ָ���Ϊд�ļ�" + "���ļ�·��Ϊ��" + instruc_Info + "\n");
		GUI.instruc_textArea.append("����" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ��\n");
		GUI.instruc_textArea.append("ָ�����ͣ�д�ļ�\n");
		GUI.instruc_textArea.append("�ļ���ַ��" + instruc_Info);
		Global.processResult += "CPU����ϵͳ̬��ִ�н���" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ�ָ���Ϊд�ļ�" + "���ļ�·��Ϊ��" + instruc_Info + "\n";
	}
	
	public static void runInstruc_3(Process process, PCB pcb, String instruc_Info) {     //ִ��3��ָ��
		FileOperation.setIfDelete(true);
		currentFilePath3 = instruc_Info;
		
		//��������Ϣ
		System.out.println("CPU�����û�̬��ִ�н���" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ�ָ���Ϊɾ���ļ�" + "���ļ�·��Ϊ��" + instruc_Info);
		GUI.console_textArea.append("CPU�����û�̬��ִ�н���" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ�ָ���Ϊɾ���ļ�" + "���ļ�·��Ϊ��" + instruc_Info + "\n");
		GUI.instruc_textArea.append("����" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ��\n");
		GUI.instruc_textArea.append("ָ�����ͣ�ɾ���ļ�\n");
		GUI.instruc_textArea.append("�ļ���ַ��" + instruc_Info);
		Global.processResult += "CPU�����û�̬��ִ�н���" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ�ָ���Ϊɾ���ļ�" + "���ļ�·��Ϊ��" + instruc_Info + "\n";
	}
	
	public static void runInstruc_4(Process process, PCB pcb) {     //ִ��4��ָ��
		//ִ��ָ��
		closeInterrupt(pcb);
		pcb.block();
		cpu_State = false;
		openInterrupt(pcb);
		
		//��������Ϣ
		System.out.println("CPU����ϵͳ̬��ִ�н���" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ�ָ���Ϊϵͳ����");
		GUI.console_textArea.append("CPU����ϵͳ̬��ִ�н���" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ�ָ���Ϊϵͳ����" + "\n");
		GUI.instruc_textArea.append("����" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ��\n");
		GUI.instruc_textArea.append("ָ�����ͣ�ϵͳ����\n");
		Global.processResult += "CPU����ϵͳ̬��ִ�н���" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ�ָ���Ϊϵͳ����" + "\n";
	}
	
	public static void runInstruc_5(Process process, PCB pcb) {     //ִ��5��ָ��
		//ִ��ָ��
		closeInterrupt(pcb);
		pcb.block();
		cpu_State = false;
		openInterrupt(pcb);
		
		//��������Ϣ
		System.out.println("CPU����ϵͳ̬��ִ�н���" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ�ָ���Ϊϵͳ���");
		GUI.console_textArea.append("CPU����ϵͳ̬��ִ�н���" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ�ָ���Ϊϵͳ���" + "\n");
		GUI.instruc_textArea.append("����" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ��\n");
		GUI.instruc_textArea.append("ָ�����ͣ�ϵͳ���\n");
		Global.processResult += "CPU����ϵͳ̬��ִ�н���" + pcb.getProID() + "��" + process.getInstruc_ID() + "��ָ�ָ���Ϊϵͳ���"+ "\n";
	}
	
	public static boolean ifDelete(PCB pcb) {     //�жϴ˽����Ƿ�Ҫ������
		if(pcb.getIR() == pcb.getInstrucNum()) {     //��ǰ������ִ�������һ��ָ��
			if(pcb.getPSW() != 3) {     //���ý��̲���������̬����ֱ�ӳ���
				return true;
			}
			else {     //���ý��̴�������̬��������ͷ���Դ���ٳ���
				pcb.setDelete(true);
				return false;
			}
		}
		else {
			return false;
		}
	}
}
