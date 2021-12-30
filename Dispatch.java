package code;

import java.util.ArrayList;

public class Dispatch extends Thread {     //���̵�����

	private static int timeSlice = 2;     //ʱ��Ƭ
	private static PCB currentProcess = new PCB();     //��ǰ�������еĽ���
	private static boolean ifHangUp1 = false;     //�����м����ȵı�־1
	private static boolean ifHangUp2 = true;     //�����м����ȵı�־2
	private static boolean ifHangUp3 = false;     //�����м����ȵı�־3
	private static boolean ifHangUp4 = true;     //�����м����ȵı�־4
	
	private ArrayList<Block> tempBlockList = new ArrayList<Block>();     //�洢���������������
	
	public void run() {
		while(true) {
			Global.lock.lock();     //������
			try {
				Global.condition.await();     //�߳̽���ȴ�״̬
				//ÿ�����һ�εͼ�����
				timeSliceRound();
				ifHangUp3 = true;
					
				//�����м����ȵ�����
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
				
			    //�����м�����
				if(ifHangUp1 && ifHangUp2 && ifHangUp3 && ifHangUp4) {
					middleSchedule();
					ifHangUp1 = false;
					ifHangUp3 = false;
				}
				
			    //ÿ15�����һ�μ������
				if(CPU.getClock() % 15 == 0) {
					activate();
				}
				
				//��������Ϣ
				GUI.pc_textArea.append(String.valueOf(CPU.getPC()));
				GUI.ir_textArea.append(String.valueOf(CPU.getIR()));
				GUI.psw_textArea.append(String.valueOf(CPU.getPSW()));
				if(CPU.getCpu_State()) {
					GUI.cpuState_textArea.append("������");
				}
				else {
					GUI.cpuState_textArea.append("����");
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
				Global.lock.unlock();     //�ͷ���
			}	
		}
	}
	
	public static PCB getCurrentProcess() {     //���ص�ǰ����ִ�еĽ���
		return currentProcess;
	}
	
	public static void setIfHangUp2(boolean ifhangup2) {     //���ù����־2
		ifHangUp2 = ifhangup2;
	}
	
	public static void setIfHangUp4(boolean ifhangup4) {     //���ù����־4
		ifHangUp4 = ifhangup4;
	}
	
	public static void sortReadyList() {     //�������ȼ��Ծ������н�������
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
	
	public static void cutTimeSlice() {     //ʱ��Ƭ��1
		timeSlice--;
	}
	
	public static void switchProcess() {     //�����������л�
		//����̬->����̬
		currentProcess.setPSW(3);
		
		//����̬->����̬
		currentProcess.setPSW(1);
		PCB.inReadyList(currentProcess);
		
		//�л��½���
		currentProcess = PCB.outReadyList();
		CPU.setPC(currentProcess.getPC());
		CPU.setIR(currentProcess.getIR());
		CPU.setPSW(currentProcess.getPSW());
		
		timeSlice = 2;     //����ʱ��Ƭ
		CPU.execute(currentProcess);     //CPUִ���µĽ���
	}
	
	//�ͼ�����
	public void timeSliceRound() {
		if(CPU.getCpu_State()) {     //����ǰCPU���ڹ���״̬
			if(timeSlice > 0) {     //��ʱ��Ƭ����ʣ�࣬�����ִ�е�ǰ����
				CPU.execute(currentProcess);
	 		}
			else {     //��ʱ��Ƭ�Ѿ����꣬��ִ�н����������л�
				switchProcess();
			}
		}
		else {     //����ǰCPU���ڿ���״̬
			if(PCB.getReadyList().size() != 0) {     //����ǰ�������в�Ϊ��
				timeSlice = 2;     //����ʱ��Ƭ
				currentProcess = PCB.outReadyList();     //ִ�ж�ͷ����
				CPU.execute(currentProcess);
			}
			else {     //����ǰ��������Ϊ�գ���CPU����
				System.out.println("CPU���У�");
				GUI.console_textArea.append("CPU���У�\n");
				Global.processResult += "CPU���У�\n";
			}
		}
	}
	
	public int getLeftExchangeSections() {     //��ȡ���̽�����ʣ��������
		int count = 0;
		for(int i=0; i<Global.exchangeSectionNum; i++) {
			if(HardDisk.getEmptySection()[0][i] == 0) {
				count++;
			}
		}
		return count;
	}
	
	//�м�����
	public void middleSchedule() {
		int proID = -1;
		int count = 0;
		tempBlockList.clear();
		
		//ȷ��Ҫ����Ľ��̱��
		int blockNum = MMU.LRU();
		for(Block block : Memory.getBlockList()) {
			if(block.getStartAddress() >> 9 == blockNum) {
				proID = block.getProID();
				break;
			}
		}
		
		//ȷ��Ҫ��������������
		for(int i=0; i<Memory.getBlockList().size(); i++) {
			if(Memory.getBlockList().get(i).getProID() == proID) {
				count++;
			}
		}
		
	     //�����̿ռ��㹻���򽫽��̹���
		if(getLeftExchangeSections() >= count) {
			//ȷ��Ҫ����Ľ��̱�ź��û��������
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
				
			//ɾ����Ӧ��ҳ����
			for(PageTable pageTable : Memory.getPageTableList()) {
				if(pageTable.getProID() == proID) {
					pageTable.getEntryList().clear();
					break;
				}
			}
			
			//ɾ����Ӧ�Ŀ����
			for(int i=0; i<MMU.getTlb().getItemList().size(); i++) {
				if(MMU.getTlb().getItemList().get(i).getProID() == proID) {
					MMU.getTlb().getItemList().remove(i);
					i = -1;
				}
			}
				
			//����ӦPCB���������У�����¼��ָ�λ��
			for(PCB pcb : Memory.getPcbList()) {
				if(pcb.getProID() == proID) {
					if(pcb.getPSW() == 1 || pcb.getPSW() == 3) {     //�����̴��ھ���̬������̬
						if(PCB.getBlockList2().indexOf(pcb) == 0) {     //����������������2����
							if(CPU.getRelease1()) {     //�������뵽���������򽫻������ͷ�
								GUI.v1_textArea.append(String.valueOf(pcb.getProID()));
								System.out.println("�ͷŻ�����1��");
								GUI.console_textArea.append("�ͷŻ�����1��\n");
								Global.processResult += "�ͷŻ�����1��\n";
								CPU.setRelease1(false);
								
								PV.V1();
								PCB.getBlockList2().remove(pcb);
								if(pcb.getIR() < pcb.getInstrucNum()) {     //���ý���δִ�����
									pcb.setLocation(1);     //����󽫻ָ�����������
								}
								else if(pcb.getIR() == pcb.getInstrucNum()) {     //���ý�����ִ�����
									pcb.setLocation(5);     //����󽫻ָ�����������
								}
							}
							else {     //����û���뵽����������ֱ���Ƴ���������2
								PCB.getBlockList2().remove(pcb);
								pcb.setLocation(3);     //�����ָ�����������2
							}
						}
						else if(PCB.getBlockList3().indexOf(pcb) == 0) {     //����������������2����
							if(CPU.getRelease2()) {     //�������뵽���������򽫻������ͷ�
								GUI.v2_textArea.append(String.valueOf(pcb.getProID()));
								System.out.println("�ͷŻ�����2��");
								GUI.console_textArea.append("�ͷŻ�����2��\n");
								Global.processResult += "�ͷŻ�����2��\n";
								CPU.setRelease2(false);
								
								PV.V2();
								PCB.getBlockList3().remove(pcb);
								if(pcb.getIR() < pcb.getInstrucNum()) {     //���ý���δִ�����
									pcb.setLocation(1);     //����󽫻ָ�����������
								}
								else if(pcb.getIR() == pcb.getInstrucNum()) {     //���ý�����ִ�����
									pcb.setLocation(5);     //����󽫻ָ�����������
								}
							}
							else {     //����û���뵽����������ֱ���Ƴ���������3
								PCB.getBlockList3().remove(pcb);
								pcb.setLocation(4);     //�����ָ�����������3
							}
						}
						
						else if(PCB.getReadyList().contains(pcb)) {     //�������ھ������У��򼤻��ָ�����������
							PCB.getReadyList().remove(pcb);
							pcb.setLocation(1);
						}
						else if(PCB.getBlockList1().contains(pcb)) {     //����������������1���򼤻��ָ�����������1
							PCB.getBlockList1().remove(pcb);
							pcb.setLocation(2);
						}
						else if(PCB.getBlockList2().contains(pcb)) {     //����������������2���򼤻��ָ�����������2
							PCB.getBlockList2().remove(pcb);
							pcb.setLocation(3);
						}
						else if(PCB.getBlockList3().contains(pcb)) {     //����������������3���򼤻��ָ�����������3
							PCB.getBlockList3().remove(pcb);
							pcb.setLocation(4);
						}
						else if(currentProcess.equals(pcb)) {     //��������������
							CPU.setCPU_State(false);
							pcb.setLocation(1);
						}
					}
					
					PCB.inHangUpList(pcb);     //��PCB����������
					pcb.setPSW(4);
					System.out.println("�������" + pcb.getProID());
					GUI.console_textArea.append("�������" + pcb.getProID() + "\n");
					Global.processResult += "�������" + pcb.getProID() + "\n";
				}
			}
			
		}
			
		//���ռ䲻�������޷����й���
		else {
			System.out.println("��潻����ʣ��ռ䲻�㣬�޷�����");
			GUI.console_textArea.append("��潻����ʣ��ռ䲻�㣬�޷�����\n");
			Global.processResult += "��潻����ʣ��ռ䲻�㣬�޷�����\n";
		}
	}
	
	public void sortHangUpList() {     //�Թ�����а������ȼ���������
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
	
	public int getLeftUserBlocks() {     //��ȡ�ڴ��û���ʣ���������
		int count = 0;
		for(int i=0; i<Global.userBlockNum; i++) {
			if(Memory.getUserEmptyBlock()[i] == 0) {
				count++;
			}
		}
		System.out.println("��ǰ�ڴ��û������������" + count + "��");
		GUI.console_textArea.append("��ǰ�ڴ��û������������" + count + "��\n");
		Global.processResult += "��ǰ�ڴ��û������������" + count + "��\n";
		return count;
	}
	
	public void activate() {     //�������
		//���ȸ������ȼ��Թ�����н�������
		sortHangUpList();
		
		//���м������
		for(PCB pcb : PCB.getHangUpList()) {
			if(getLeftUserBlocks() >= pcb.getHangUpBlocks().size()) {     //���ڴ��û���ʣ����������㹻ʱ���ܼ���
				for(Block block : pcb.getHangUpBlocks()) {
					//���������ݴ��������
					for(int i=0; i<Global.userBlockNum; i++) {
						if(Memory.getUserEmptyBlock()[i] == 0) {
							Block block2 = new Block((Global.userBlockNum+i)<<9, block.getProID(), block.getPageNum());
							block2.setInstructionsAndNumbers(block.getInstructionsAndNumbers());
							Memory.getBlockList().add(block2);
							Memory.getUserEmptyBlock()[i] = 1;
							break;
						}
					}
					
					//����ҳ��
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
					
					//�����̽���������Ӧ�������
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
				
				//��PCB�ƻ���Ӧ����
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
				PCB.getHangUpList().remove(pcb);     //��PCB�Ƴ��������
				
				System.out.println("�������" + pcb.getProID());
				GUI.console_textArea.append("�������" + pcb.getProID() + "\n");
				Global.processResult += "�������" + pcb.getProID() + "\n";
				break;
			}
		}
	}
}
