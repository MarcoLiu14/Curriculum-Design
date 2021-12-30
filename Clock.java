package code;

public class Clock extends Thread {     //ϵͳʱ���߳�
	
	private static boolean stop = false;     //�ж�ʱ���Ƿ�ֹͣ�ı�־
	
	public static void setStop(boolean s) {     //����ʱ���Ƿ�ֹͣ�ı�־											
		stop = s;
	}
	
	public void run() {     //ÿ�뷢��һ���ж��ź�
		while(true) {
			Global.lock.lock();     //������
			if(CPU.getClock() != 0 && !stop) {
				//���¿��ӻ�����
				refresh();
				showFileList();
				showTLB();
				showUserBlockList();
				showExchangeSectionList();
				showPageTable();
				IO.setSyn(true);
				
				//����ʱ��
				System.out.println("---------------------------------------------------------------------");
				System.out.println("��" + CPU.getClock() + "��");
				GUI.clock_textArea.append(String.valueOf(CPU.getClock()));
				GUI.console_textArea.append("---------------------------------------------------------------------\n");
				GUI.console_textArea.append("��" + CPU.getClock() + "��\n");
				Global.processResult += "---------------------------------------------------------------------\n";
				Global.processResult += "��" + CPU.getClock() + "��\n";
				
				//�������
				PV.testDeadLock();
				if(Global.deadLock) {
					GUI.deadLock_textArea.append("��");
					System.out.println("����������");
					GUI.console_textArea.append("����������\n");
					Global.processResult += "����������\n";
				}
				else {
					GUI.deadLock_textArea.append("��");
				}
				
				//���ƹ������Ľ�����ʼ������ײ�
				GUI.file_textArea.setCaretPosition(GUI.file_textArea.getText().length());
				GUI.console_textArea.setCaretPosition(GUI.console_textArea.getText().length());
				
				Global.condition.signalAll();     //�������м����߳�
			}
			Global.lock.unlock();     //�ͷ���
			
			if(!stop) {     //��ʱ����������ʱ��ÿ������һ��
				try {
					Thread.sleep(1000);
					CPU.setClock();
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void refresh() {     //ˢ���ı���
		GUI.clock_textArea.setText("");
		GUI.deadLock_textArea.setText("");
		GUI.pc_textArea.setText("");
		GUI.ir_textArea.setText("");
		GUI.psw_textArea.setText("");
		GUI.cpuState_textArea.setText("");
		GUI.interrupt_textArea.setText("");
		GUI.request_textArea.setText("");
		GUI.ready_textArea.setText("");
		GUI.block1_textArea.setText("");
		GUI.block2_textArea.setText("");
		GUI.block3_textArea.setText("");
		GUI.hangUp_textArea.setText("");
		GUI.delete_textArea.setText("");
		GUI.instruc_textArea.setText("");
		GUI.buffer1_textArea.setText("");
		GUI.buffer2_textArea.setText("");
		GUI.p1_textArea.setText("");
		GUI.v1_textArea.setText("");
		GUI.p2_textArea.setText("");
		GUI.v2_textArea.setText("");
		GUI.mutex1_textArea.setText("");
		GUI.mutex2_textArea.setText("");
		GUI.file_textArea.setText("");
		GUI.tlb_textArea.setText("");
		GUI_1.textArea.setText("");
		GUI_2.textArea.setText("");
		GUI_2.textArea_1.setText("");
		GUI_2.textArea_2.setText("");
		GUI_3.textArea.setText("");
	}
	
	public void showFileList() {     //��ʾϵͳ�ļ��б�
		for(File file : Global.fileList) {
			GUI.file_textArea.append(file.getPath() + "\n");
		}
	}
	
	public void showTLB() {     //��ʾϵͳ���
		GUI.tlb_textArea.append("���\t�߼�ҳ��\t������\t���̱��\n");
		for(Item item : MMU.getTlb().getItemList()) {
			GUI.tlb_textArea.append(MMU.getTlb().getItemList().indexOf(item) + "\t" + item.getPageNum() + "\t" + item.getBlockNum() + "\t" + item.getProID() + "\n");
		}
	}
	
	public void showUserBlockList() {     //��ʾ�ڴ��û���ÿ��������ʹ�����
		GUI_1.textArea.append("���\t��ʼ��ַ\t���̺�\tҳ��\n");
		for(int i=0; i<Global.userBlockNum; i++) {
			if(Memory.getBlockList().size() == 0) {
				GUI_1.textArea.append(String.valueOf(i+Global.userBlockNum) + "\t0x" + Integer.toHexString((i+Global.userBlockNum)<<9) + "\t-1\t-1\n");
			}
			else {
				for(Block block : Memory.getBlockList()) {
					if((block.getStartAddress() >> 9) == (i + Global.userBlockNum)) {
						GUI_1.textArea.append(String.valueOf(block.getStartAddress()>>9) + "\t0x" + Integer.toHexString(block.getStartAddress()) + "\t" + block.getProID() + "\t" + block.getPageNum() + "\n");
						break;
					}
					if(Memory.getBlockList().indexOf(block) == Memory.getBlockList().size() - 1) {
						GUI_1.textArea.append(String.valueOf(i+Global.userBlockNum) + "\t0x" + Integer.toHexString((i+Global.userBlockNum)<<9) + "\t-1\t-1\n");
					}
				}
			}
		}
		
		//��ʾ�ڴ��û�����ʹ�ý�����
		int count = 0;
		for(int i=0; i<Global.userBlockNum; i++) {
			if(Memory.getUserEmptyBlock()[i] == 1) {
				count++;
			}
		}
		GUI.userProgressBar.setValue(count);
	}
	
	public void showExchangeSectionList() {     //��ʾ���̽�����ÿ��������ʹ�����
		GUI_2.textArea.append("�ŵ���\t������\t���̺�\tҳ��\n");
		GUI_2.textArea_1.append("�ŵ���\t������\t���̺�\tҳ��\n");
		GUI_2.textArea_2.append("�ŵ���\t������\t���̺�\tҳ��\n");
		for(int i=0; i<Global.sectionNum; i++) {
			if(MMU.getTrack0().getSectionList().size() == 0) {
				if(i < Global.sectionNum / 2) {
					GUI_2.textArea.append("0\t" + i + "\t-1\t-1\n");
				}
				else {
					GUI_2.textArea_1.append("0\t" + i + "\t-1\t-1\n");
				}
			}
			else {
				for(Section section0 : MMU.getTrack0().getSectionList()) {
					if(section0.getSectionID() == i) {
						if(i < Global.sectionNum / 2) {
							GUI_2.textArea.append("0\t" + section0.getSectionID() + "\t" + section0.getProID() + "\t" + section0.getPageNum() + "\n");
						}
						else {
							GUI_2.textArea_1.append("0\t" + section0.getSectionID() + "\t" + section0.getProID() + "\t" + section0.getPageNum() + "\n");
						}
						break;
					}
					if(MMU.getTrack0().getSectionList().indexOf(section0) == MMU.getTrack0().getSectionList().size() - 1) {
						if(i < Global.sectionNum / 2) {
							GUI_2.textArea.append("0\t" + i + "\t-1\t-1\n");
						}
						else {
							GUI_2.textArea_1.append("0\t" + i + "\t-1\t-1\n");
						}
					}
				}
			}
		}
		
		for(int j=0; j<Global.sectionNum/2; j++) {
			if(MMU.getTrack1().getSectionList().size() == 0) {
				GUI_2.textArea_2.append("1\t" + j + "\t-1\t-1\n");
			}
			else {
				for(Section section1 : MMU.getTrack1().getSectionList()) {
					if(section1.getSectionID() == j) {
						GUI_2.textArea_2.append("1\t" + section1.getSectionID() + "\t" + section1.getProID() + "\t" + section1.getPageNum() + "\n");
						break;
					}
					if(MMU.getTrack1().getSectionList().indexOf(section1) == MMU.getTrack1().getSectionList().size() - 1) {
						GUI_2.textArea_2.append("1\t" + j + "\t-1\t-1\n");
					}
				}
			}
		}
		
		//��ʾ���̽�������ʹ�ý�����
		int count = 0;
		for(int i=0; i<Global.exchangeSectionNum; i++) {
			if(HardDisk.getEmptySection()[0][i] == 1) {
				count++;
			}
		}
		GUI.exchangeProgressBar.setValue(count);
	}
	
	public void showPageTable() {     //��ʾ����ҳ��
		GUI_3.textArea.append("�߼�ҳ��\t������\n");
		for(PageTable pageTable : Memory.getPageTableList()) {
			GUI_3.textArea.append("--------����" + pageTable.getProID() + "--------\n");
			for(Entry entry : pageTable.getEntryList()) {
				GUI_3.textArea.append(entry.getPageNum() + "\t" + entry.getBlockNum() + "\n");
			}
		}
	}
}
