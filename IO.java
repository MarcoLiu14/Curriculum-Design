package code;

public class IO extends Thread {     //��������߳�
	
	private static boolean syn = false;     //ͬ����־
	private static boolean input = false;     //ϵͳ�����־
	private static boolean output = false;     //ϵͳ�����־
	
	public void run() {
		while(true) {
			Global.lock.lock();     //������
			if(syn) {
				//���뼰�ͷŻ���������
				apply();
				if(CPU.getRelease1()) {
					release1();
				}
				if(CPU.getRelease2()) {
					release2();
				}
				
				//���ӻ�������ʾ�ź�����ֵ
				GUI.mutex1_textArea.append(String.valueOf(PV.getMutex1()));
				GUI.mutex2_textArea.append(String.valueOf(PV.getMutex2()));
				
				//ϵͳ�����������
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
			Global.lock.unlock();     //�ͷ���
		}
	}
	
	public static void setSyn(boolean tempsyn) {     //����ͬ����־
		syn = tempsyn;
	}
	
	public static void setInput(boolean tempInput) {     //����ϵͳ�����־
		input = tempInput;
	}
	
	public static void setOutput(boolean tempOutput) {     //����ϵͳ�����־
		output = tempOutput;
	}
	
	public synchronized void apply() {     //���뻺����
		if(PV.getMutex1() == 1) {     //��������1���У��鿴���޽�������
			if(PCB.getBlockList2().size() > 0) {
				PCB temPcb = PCB.getBlockList2().get(0);
				System.out.println("���뻺����1�ɹ���" + "��ǰʹ�û�����1����" + temPcb.getProID() + "�Ž���");
				GUI.console_textArea.append("���뻺����1�ɹ���" + "��ǰʹ�û�����1����" + temPcb.getProID() + "�Ž���\n");
				GUI.buffer1_textArea.append(String.valueOf(temPcb.getProID()));
				Global.processResult += "���뻺����1�ɹ���" + "��ǰʹ�û�����1����" + temPcb.getProID() + "�Ž���\n";
				PV.P1(temPcb);
			}
			else {
				System.out.println("������1���У�");
				GUI.console_textArea.append("������1���У�\n");
				Global.processResult += "������1���У�\n";
			}
		}
		if(PV.getMutex2() == 1) {     //��������2���У��鿴���޽�������
			if(PCB.getBlockList3().size() > 0) {
				PCB temPcb = PCB.getBlockList3().get(0);
				System.out.println("���뻺����2�ɹ���" + "��ǰʹ�û�����2����" + temPcb.getProID() + "�Ž���");
				GUI.console_textArea.append("���뻺����2�ɹ���" + "��ǰʹ�û�����2����" + temPcb.getProID() + "�Ž���\n");
				GUI.buffer2_textArea.append(String.valueOf(temPcb.getProID()));
				Global.processResult += "���뻺����2�ɹ���" + "��ǰʹ�û�����2����" + temPcb.getProID() + "�Ž���\n";
				PV.P2(temPcb);
			}
			else {
				System.out.println("������2���У�");
				GUI.console_textArea.append("������2���У�\n");
				Global.processResult += "������2���У�\n";
			}
		}
	}
	
	public synchronized void release1() {     //�ͷŻ�����1
		PCB temPcb = PCB.getBlockList2().get(0);
		GUI.v1_textArea.append(String.valueOf(temPcb.getProID()));
		PV.V1();
		
		if(temPcb.getDelete()) {     //���ͷ�֮��ý���Ҫ��������ִ�г�������
			temPcb.setDelete(false);
			temPcb.delete();
		}
		else {
			temPcb.wake();
		}
		
		System.out.println("�ͷŻ�����1��");
		GUI.console_textArea.append("�ͷŻ�����1��\n");
		Global.processResult += "�ͷŻ�����1��\n";
		CPU.setRelease1(false);
	}
	
	public synchronized void release2() {     //�ͷŻ�����2
		PCB temPcb = PCB.getBlockList3().get(0);
		GUI.v2_textArea.append(String.valueOf(temPcb.getProID()));
		PV.V2();
		
		if(temPcb.getDelete()) {     //���ͷ�֮��ý���Ҫ��������ִ�г�������
			temPcb.setDelete(false);
			temPcb.delete();
		}
		else {
			temPcb.wake();
		}
		
		System.out.println("�ͷŻ�����2��");
		GUI.console_textArea.append("�ͷŻ�����2��\n");
		Global.processResult += "�ͷŻ�����2��\n";
		CPU.setRelease2(false);
	}
	

	public void systemInput() {     //ϵͳ����
		System.out.println("ִ��ϵͳ���룡");
		GUI.console_textArea.append("ִ��ϵͳ���룡\n");
		Global.processResult += "ִ��ϵͳ���룡\n";
		CPU.setRelease2(true);
	}
	
	public void systemOutput() {     //ϵͳ���
		System.out.println("ִ��ϵͳ�����");
		GUI.console_textArea.append("ִ��ϵͳ�����\n");
		Global.processResult += "ִ��ϵͳ���\n";
		CPU.setRelease1(true);
	}
}
