package code;

import java.util.HashMap;

public class MissingPage extends Thread {     //ȱҳ�ж��߳�

	public void run() {
		while(true) {
			Global.lock.lock();     //������
			try {
				Global.condition.await();     //�߳̽���ȴ�״̬
				if(MMU.getIfMissingPage()) {
					if(MMU.getInterruptPcb().getPSW() == 4) {
						if(PCB.getBlockList1().isEmpty()) {
							MMU.setIfMissingPage(false);
						}
					}
				}
				
				if(MMU.getIfMissingPage()) {
					Dispatch.setIfHangUp4(false);
					System.out.println("����ȱҳ�ж�");
					GUI.console_textArea.append("����ȱҳ�ж�\n");
					Global.processResult += "����ȱҳ�ж�\n";
					
					PCB temPcb = PCB.getBlockList1().get(0);
					dealMissingPage(temPcb, MMU.getPageNum(), MMU.getOffset());
					temPcb.wake();
					MMU.setIfDone(true);
					MMU.setIfMissingPage(false);
					Dispatch.setIfHangUp4(true);
				}
			}catch(InterruptedException e) {
				e.printStackTrace();
			}finally {
				Global.lock.unlock();     //�ͷ���
			}
		}
	}
	
	private static int physicalAddress;     //�����ַ
	
	public static int getPhysicalAddress() {     //��ȡ�����ַ
		return physicalAddress;
	}
	
	public synchronized void dealMissingPage(PCB pcb, int pageNum, int offset) {     //����ȱҳ�ж�
		int blockNum;
		for(int i=0; i<Global.userBlockNum; i++) {
			//���ڴ��û������п�������飬��ֱ�ӷ���
			if(Memory.getUserEmptyBlock()[i] == 0) {
				blockNum = Global.userBlockNum + i;
				Memory.getBlockList().add(new Block(blockNum<<9, pcb.getProID(), pageNum));
				Memory.getUserEmptyBlock()[i] = 1;
			
				for(PageTable pageTable : Memory.getPageTableList()) {     //����ҳ��
					if(pageTable.getStartAddress() == pcb.getPageTableStartAddress()) {
						pageTable.getEntryList().add(new Entry(pageNum, Global.userBlockNum+i));
						break;
					}
				}
				MMU.updateTLB(pcb, pageNum, Global.userBlockNum+i);     //���¿��
				
				physicalAddress = blockNum + offset;     //�ϳ��µ������ַ
				
				//ҳ���û�
				for(Section tempSection : MMU.getTrack0().getSectionList()) {
					if(tempSection.getPageNum() == pageNum && tempSection.getProID() == pcb.getProID() && tempSection.getSectionID() != -1) {
						exchange1(tempSection, pageNum, blockNum);
						break;
					}
				}
				for(Section tempSection : MMU.getTrack1().getSectionList()) {
					if(tempSection.getPageNum() == pageNum && tempSection.getProID() == pcb.getProID() && tempSection.getSectionID() != -1) {
						exchange1(tempSection, pageNum, blockNum);
						break;
					}
				}
				
				break;
			}
			
			if(i == Global.userBlockNum - 1) {
				System.out.println("����LRU�㷨������ҳ���ȣ�");
				GUI.console_textArea.append("����LRU�㷨������ҳ���ȣ�\n");
				Global.processResult += "����LRU�㷨������ҳ���ȣ�\n";
				
				//���޿�������飬�����LRU�㷨�����û�
				blockNum = MMU.LRU();
				physicalAddress = (blockNum << 9) + offset;     //�ϳ��µ������ַ
				
				for(PageTable pageTable : Memory.getPageTableList()) {     //����ҳ��
					if(pageTable.getStartAddress() == pcb.getPageTableStartAddress()) {
						for(Entry entry : pageTable.getEntryList()) {
							if(entry.getBlockNum() == blockNum) {
								entry.setPageNum(pageNum);
								break;
							}
						}
					}
				}
					
				MMU.updateTLB(pcb, pageNum, blockNum);     //���¿��
				
				//ҳ���û�
				for(Section tempSection : MMU.getTrack0().getSectionList()) {
					if(tempSection.getPageNum() == pageNum && tempSection.getProID() == pcb.getProID() && tempSection.getSectionID() != -1) {
						exchange2(pcb.getProID(), tempSection, pageNum, blockNum);
						break;
					}
				}
				for(Section tempSection : MMU.getTrack1().getSectionList()) {
					if(tempSection.getPageNum() == pageNum && tempSection.getProID() == pcb.getProID() && tempSection.getSectionID() != -1) {
						exchange2(pcb.getProID(), tempSection, pageNum, blockNum);
						break;
					}
				}
			}
		}
	}
	
	public void exchange1(Section tempSection, int pageNum, int blockNum) {     //ҳ���û�
		//��ָ������ڴ�
		for(Block block : Memory.getBlockList()) {
			if(block.getStartAddress() >> 9 == blockNum) {
				for(Integer i : tempSection.getInstructionsAndNumbers().keySet()) {
					block.getInstructionsAndNumbers().put((blockNum<<9)+(i&0x1ff), tempSection.getInstructionsAndNumbers().get(i));
				}
				Memory.getUserEmptyBlock()[(block.getStartAddress() >> 9) - Global.userBlockNum] = 1;
				break;
			}
		}
		
		//�޸�λʾͼ
		if(MMU.getTrack0().getSectionList().contains(tempSection)) {
			HardDisk.getEmptySection()[0][tempSection.getSectionID()] = 0;
			tempSection.setSectionID(-1);
		}
		else if(MMU.getTrack1().getSectionList().contains(tempSection)) {
			HardDisk.getEmptySection()[0][tempSection.getSectionID()+Global.sectionNum] = 0;
			tempSection.setSectionID(-1);
		}
	}
	
	public void exchange2(int proID, Section tempSection, int pageNum, int blockNum) {     //LRUҳ���û�
		HashMap<Integer, String> tempMap = new HashMap<Integer, String>();
		
		//�Ի�����ҳ���ָ�
		for(Block block : Memory.getBlockList()) {
			if(block.getStartAddress() >> 9 == blockNum) {
				//�ݴ��������Ҫ��������ָ�
				for(Integer i : block.getInstructionsAndNumbers().keySet()) {
					tempMap.put(i, block.getInstructionsAndNumbers().get(i));
				}
				//�������е�ָ�����
				for(Integer i : tempSection.getInstructionsAndNumbers().keySet()) {
					block.getInstructionsAndNumbers().put((blockNum<<9)+(i&0x1ff), tempSection.getInstructionsAndNumbers().get(i));
				}
				
				//�����������ֵ
				tempSection.setProID(block.getProID());
				tempSection.setPageNum(block.getPageNum());
				block.setProID(proID);
				block.setPageNum(pageNum);
			}
		}
		//��������е�ָ�����
		tempSection.getInstructionsAndNumbers().clear();
		for(Integer i : tempMap.keySet()) {
			tempSection.getInstructionsAndNumbers().put(i, tempMap.get(i));
		}
	}
}
