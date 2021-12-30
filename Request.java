package code;

import java.io.IOException;
import java.util.ArrayList;

public class Request extends Thread {     //��ҵ�����߳�
	
	private String fileName = "input/19218106-jobs-input.txt";

	public void run() {
		while(true) {
			Global.lock.lock();     //������
			try {
				Global.condition.await();     //�߳̽���ȴ�״̬
				try {
					if(CPU.getClock() % 5 == 0) {     //ÿ5����һ���Ƿ�������ҵ���󣬲��ж��Ƿ񴴽��½���
						checkIfCreate();
						JCB.showRequestList();
					}
				}catch (IOException e) {
					e.printStackTrace();
				}
			}catch (InterruptedException e) {
				e.printStackTrace();
			}finally {
				Global.lock.unlock();     //�ͷ���
			}	
		}
	}
	
	public ArrayList<int[]> receiveInputFile(String fileName) throws IOException {     
		//���ն�ȡ�Ĳ�����ҵ�����ļ���Ϣ������
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
	
	public static void sortRequestList() {     //�������ȼ�����ҵ������н�������
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
	
	public void checkIfCreate() throws IOException {     //�жϴ�ʱ�Ƿ�������ҵ����
		ArrayList<int[]> readInput = new ArrayList<int[]>();
		readInput = receiveInputFile(fileName);
		
		int time = CPU.getClock();     //��ȡ��ǰʱ��
		for(int[] i : readInput) {
			if(i[2] > time-5 && i[2] <= time) {     //����ǰʱ����ĳ��ҵ���ύʱ�䣬����������ҵ���󣬲��������������к�ϵͳ��ҵ��
				JCB jcb = new JCB(i[0], i[1], i[2], i[3], i[4]);
				JCB.getRequestList().add(jcb);
				Global.jobList.add(jcb);
				System.out.println("��ҵ" + jcb.getJobID() + "�ύ�����ȼ�Ϊ" + jcb.getPriority());
				GUI.console_textArea.append("��ҵ" + jcb.getJobID() + "�ύ�����ȼ�Ϊ" + jcb.getPriority() + "\n");
				Global.processResult += "��ҵ" + jcb.getJobID() + "�ύ�����ȼ�Ϊ" + jcb.getPriority() + "\n";
			}
		}
	
		//�����½��̲�Ϊ�����洢�ռ�
		malloc();
	}
	
	public void malloc() throws IOException {     //�����½��̲�Ϊ�����洢�ռ�
		ArrayList<String[]> readInstruction = new ArrayList<String[]>();
		
		sortRequestList();     //����ҵ������н�������
		while(JCB.getRequestList().size() != 0) {
			if(Memory.ifPcbListOk() && HardDisk.getLeftExchangeSectionNum() >= JCB.getRequestList().get(0).getSize() && Memory.getPcbList().size() < Global.maxPcbNum) {
			    //����ʱPCB���뽻�����Ŀռ䶼����ԣ
				//�����½���
				JCB temp = JCB.getRequestList().poll();
				String instruc_fileName = "input/" + Integer.toString(temp.getJobID()) + ".txt";
				readInstruction = IOFile.readInstruc(instruc_fileName);     //��ȡָ���ļ�
				new Process(temp.getJobID(), temp.getPriority(), temp.getInTimes(), temp.getInstrucNum(), temp.getSize(), PCB.init_Instruction(readInstruction));
				
				//����ҳ��
				for(PCB pcb : Memory.getPcbList()) {
					if(pcb.getProID() == temp.getJobID()) {
						pcb.setPageTableStartAddress(Memory.getPageTableList().size()*Global.maxEntryNum);
					}
				}
				Memory.getPageTableList().add(new PageTable(Memory.getPageTableList().size()*Global.maxEntryNum, temp.getJobID()));
				
				//������δ�����������
				MMU.allocateExchangeMemory(temp);
			}
			else {
				System.out.println("�޷������½��̣�");
				GUI.console_textArea.append("�޷������½��̣�\n");
				Global.processResult += "�޷������½��̣�\n";
				break;
			}
		}
	}
}
