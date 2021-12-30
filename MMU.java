package code;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MMU {     //MMU
	
	private static PCB currentPcb;     //����Ѱַ�Ľ���PCB
	private static PCB interruptPcb;     //���ڴ���ȱҳ�жϵĽ���PCB
	private static int pageNum;     //ҳ��
	private static int offset;     //ƫ��
	private static boolean ifMissingPage = false;     //ȱҳ�жϵı�־
	private static boolean ifDone = false;     //ȱҳ�ж��Ƿ�����ɵı�־
	
	private static TLB tlb = new TLB();     //ϵͳ���
	
	public static PCB getCurrentPcb() {     //��ȡ����Ѱַ�Ľ���PCB
		return currentPcb;
	}
	
	public static PCB getInterruptPcb() {     //��ȡ���ڴ���ȱҳ�жϵ�PCB
		return interruptPcb;
	}
	
	public static int getPageNum() {     //��ȡҳ��
		return pageNum;
	}
	
	public static int getOffset() {     //��ȡƫ��
		return offset;
	}
	
	public static boolean getIfMissingPage() {     //��ȡȱҳ�жϵı�־
		return ifMissingPage;
	}
	
	public static void setIfMissingPage(boolean ifmissingpage) {     //����ȱҳ�жϵı�־
		ifMissingPage = ifmissingpage;
	}
	
	public static boolean getIfDone() {     //��ȡȱҳ�ж��Ƿ���ɵı�־
		return ifDone;
	}
	
	public static void setIfDone(boolean ifdone) {     //����ȱҳ�ж��Ƿ���ɵı�־
		ifDone = ifdone;
	}
	
	public static TLB getTlb() {     //��ȡ���̿��
		return tlb;
	}
	
	public static void savePCB(PCB pcb) {     //��PCB����PCB����
		for(int i=0; i<Global.maxPcbNum; i++) {
			if(Memory.getPCBEmptyBlock()[i] == 0) {
				Memory.getPcbList().add(pcb);
				pcb.setPCBStartAddress(((i+1)<<9));
				Memory.getPCBEmptyBlock()[i] = 1;
				System.out.println("PCB�����ʼַΪ0x" + Integer.toHexString((i+1)<<9) + "���������");
				GUI.console_textArea.append("PCB�����ʼַΪ0x" + Integer.toHexString((i+1)<<9) + "���������\n");
				Global.processResult += "PCB�����ʼַΪ0x" + Integer.toHexString((i+1)<<9) + "���������\n";
				break;
			}
		}
	}
	
	public static void createPageTable(JCB jcb) {     //����ҳ��
		for(int i=0; i<Global.maxPageTableNum; i++) {
			if(Memory.getPageTableEmptyBlock()[i] == 0) {
				Memory.getPageTableList().add(new PageTable(i*16, jcb.getJobID()));
				Memory.getPageTableEmptyBlock()[i] = 1;
				System.out.println("ҳ������0��������0x" + Integer.toHexString(i*16) + "ƫ�ƴ�");
				GUI.console_textArea.append("ҳ������0��������0x" + Integer.toHexString(i*16) + "ƫ�ƴ�\n");
				Global.processResult += "ҳ������0��������0x" + Integer.toHexString(i*16) + "ƫ�ƴ�\n";
			}
		}
	}
	
	private static Cylinder cylinder0 = new Cylinder(0);     //0������
	private static Track track0 = new Track(0);     //0�Ŵŵ�
	private static Track track1 = new Track(1);     //1�Ŵŵ�
	
	public static Cylinder getCylinder0() {     //��ȡ0������
		return cylinder0;
	}
	
	public static Track getTrack0() {     //��ȡ0�Ŵŵ�
		return track0;
	}
	
	public static Track getTrack1() {     //��ȡ1�Ŵŵ�
		return track1;
	}
	
	public static void allocateExchangeMemory(JCB jcb) throws IOException {     //Ϊ���̷�����̽�����
		System.out.println("Ϊ���̷�����̽������ռ䣬������λ��Ϊ��");
		GUI.console_textArea.append("Ϊ���̷�����̽������ռ䣬������λ��Ϊ��\n");
		Global.processResult += "Ϊ���̷�����̽������ռ䣬������λ��Ϊ��\n";
		
		//ȷ���ý��̵�ָ������ҳ��
		ArrayList<Integer> pageList = new ArrayList<Integer>();
		for(int m=0; m<IOFile.readInstruc("input/" + Integer.toString(jcb.getJobID()) + ".txt").size(); m++) {
			int page = Integer.parseInt(IOFile.readInstruc("input/" + Integer.toString(jcb.getJobID()) + ".txt").get(m)[2]) >> 9;
			if(!pageList.contains(page)) {
				pageList.add(page);
			}
		}
		
		//Ϊ���̷�������
		for(int m=0; m<pageList.size(); m++) {
			for(int i=0; i<Global.exchangeSectionNum; i++) {
				if(HardDisk.getEmptySection()[0][i] == 0) {
					if(!HardDisk.getCylinderList().contains(cylinder0)) {
						HardDisk.getCylinderList().add(cylinder0);
					}
					
					if(i / 64 == 0) {
						if(!cylinder0.getTrackList().contains(track0)) {
							cylinder0.getTrackList().add(track0);
						}
						Section section = new Section(0, i%64, jcb.getJobID(), pageList.get(m));     //��������
						track0.getSectionList().add(section);
					}
					else {
						if(!cylinder0.getTrackList().contains(track1)) {
							cylinder0.getTrackList().add(track1);
						}
						Section section = new Section(1, i%64, jcb.getJobID(), pageList.get(m));     //��������
						track1.getSectionList().add(section);
					}
					HardDisk.getEmptySection()[0][i] = 1;
					System.out.println(0 + "������ " + i/64 + "�Ŵŵ� " + i%64 + "������");
					GUI.console_textArea.append(0 + "������ " + i/64 + "�Ŵŵ� " + i%64 + "������\n");
					Global.processResult += 0 + "������ " + i/64 + "�Ŵŵ� " + i%64 + "������\n";
					break;
				}
			}
		}
		
		//����ָ�������
		for(Track track : cylinder0.getTrackList()) {
			for(Section section : track.getSectionList()) {
				if(section.getProID() == jcb.getJobID()) {
					saveInstructionsAndNumbers(jcb, section);
				}
			}
		}
	}
	
	public static void saveInstructionsAndNumbers(JCB jcb, Section tempSection) {     //�������д���ָ�������
		Random random = new Random();
		//����ָ��
		for(Process process : Global.processList) {
			if(process.getProID() == jcb.getJobID()) {
				for(Instruction instruction : process.getCode_Segment()) {
					if(instruction.getInstruc_LogicalAddress() >> 9 == tempSection.getPageNum()) {
						tempSection.getInstructionsAndNumbers().put(instruction.getInstruc_LogicalAddress(), instruction.getInstruc_Info());
					}
				}
				break;
			}
		}
		//��������
		for(int i=0; i<512; i++) {
			if(!tempSection.getInstructionsAndNumbers().containsKey((tempSection.getPageNum()<<9)+i)) {
				tempSection.getInstructionsAndNumbers().put((tempSection.getPageNum()<<9)+i, Integer.toString(random.nextInt(1000)));
			}
		}
	}
	
	public static void updateTLB(PCB pcb, int pageNum, int blockNum) {     //���¿��
		if(tlb.getItemList().size() == Global.maxItemNum) {     //������������������Ƴ���һ������
			tlb.getItemList().remove(0);
		}
		tlb.getItemList().add(new Item(pageNum, blockNum, pcb.getProID()));     //�����µı���
	}
	
	public static void deleteItem(PCB pcb) {     //ɾ�������ڿ���еı���
		for(int i=0; i<tlb.getItemList().size(); i++) {
			if(tlb.getItemList().get(i).getProID() == pcb.getProID()) {
				tlb.getItemList().remove(i);
				i = -1;
			}
		}
	}
	
	public static void recycleBlock(PCB pcb) {     //�����ڴ������
		//���ս���ҳ��
		for(PageTable pageTable : Memory.getPageTableList()) {
			if(pageTable.getProID() == pcb.getProID()) {
				Memory.getPageTableList().remove(pageTable);
				Memory.getPageTableEmptyBlock()[pageTable.getStartAddress()/16] = 0;
				break;
			}
		}
		
		//����PCB
		Memory.getPcbList().remove(pcb);
		Memory.getPCBEmptyBlock()[(pcb.getPCBStartAddress()>>9)-1] = 0;
		
		//�����û��������
		for(int i=0; i<Memory.getBlockList().size(); i++) {
			if(Memory.getBlockList().get(i).getProID() == pcb.getProID()) {
				Memory.getUserEmptyBlock()[(Memory.getBlockList().get(i).getStartAddress() >> 9) - Global.userBlockNum] = 0;
				Memory.getBlockList().get(i).getInstructionsAndNumbers().clear();
				Memory.getBlockList().remove(i);
				i = -1;
			}
		}
	}
	
	public static void recycleSection(PCB pcb) {     //���մ��̽���������
		for(int i=0; i<track0.getSectionList().size(); i++) {
			if(track0.getSectionList().get(i).getProID() == pcb.getProID() && track0.getSectionList().get(i).getSectionID() != -1) {
				HardDisk.getEmptySection()[0][track0.getSectionList().get(i).getSectionID()] = 0;
				track0.getSectionList().get(i).getInstructionsAndNumbers().clear();
				track0.getSectionList().remove(i);
				i = -1;
			}
		}
		for(int i=0; i<track1.getSectionList().size(); i++) {
			if(track1.getSectionList().get(i).getProID() == pcb.getProID() && track0.getSectionList().get(i).getSectionID() != -1) {
				HardDisk.getEmptySection()[0][track1.getSectionList().get(i).getSectionID()+Global.sectionNum] = 0;
				track1.getSectionList().get(i).getInstructionsAndNumbers().clear();
				track1.getSectionList().remove(i);
				i = -1;
			}
		}
	}
	
	public static String visitStorage(PCB pcb, int logicalAddress) {     //�ô�
		System.out.println("����" + pcb.getProID() + "�ô�ȡָ��...");
		GUI.console_textArea.append("����" + pcb.getProID() + "�ô�ȡָ��...\n");
		Global.processResult += "����" + pcb.getProID() + "�ô�ȡָ��...\n";
		currentPcb = pcb;
		int physicalAddress = convert(pcb, logicalAddress);     //�����߼���ַ�õ������ַ
		
		if(!ifMissingPage) {
			System.out.println("�߼���ַ��0x" + Integer.toHexString(logicalAddress));
			System.out.println("�����ַ��0x" + Integer.toHexString(physicalAddress));
			GUI.console_textArea.append("�߼���ַ��0x" + Integer.toHexString(logicalAddress) + "\n");
			GUI.console_textArea.append("�����ַ��0x" + Integer.toHexString(physicalAddress) + "\n");
			GUI.instruc_textArea.append("�߼���ַ��0x" + Integer.toHexString(logicalAddress) + "�������ַ��0x" + Integer.toHexString(physicalAddress) + "\n");
			Global.processResult += "�߼���ַ��0x" + Integer.toHexString(logicalAddress) + "�������ַ��0x" + Integer.toHexString(physicalAddress) + "\n";
		}
		
		int blockNum = physicalAddress >> 9;     //���
		String instruction = "-1";
		for(Block block : Memory.getBlockList()) {
			if(block.getStartAddress() == blockNum << 9) {
				block.setVisitTime(CPU.getClock());
				
				instruction = block.getInstructionsAndNumbers().get(physicalAddress);
				return instruction;
			}
		}
		return instruction;
	}

	public static int convert(PCB pcb, int logicalAddress) {     //���߼���ַת��Ϊ�����ַ
		pageNum = logicalAddress >> 9;     //ҳ��
		offset = logicalAddress & 0x01ff;     //ƫ����
		int physicalAddress;     //�����ַ
		
		//���ȷ��ʿ��
		for(Item item : tlb.getItemList()) {
			if(item.getPageNum() == pageNum && item.getProID() == pcb.getProID()) {     //��������У����������ַ
				System.out.println("�������");
				GUI.console_textArea.append("�������\n");
				Global.processResult += "�������\n";
				physicalAddress = (item.getBlockNum() << 9) + offset;
				return physicalAddress;
			}
		}
		
		//�����δ���У������ҳ��
		for(PageTable pageTable : Memory.getPageTableList()) {
			if(pcb.getPageTableStartAddress() == pageTable.getStartAddress()) {
				for(Entry entry : pageTable.getEntryList()) {
					if(entry.getPageNum() == pageNum) {     //��ҳ�����У����������ַ
						System.out.println("ҳ������");
						GUI.console_textArea.append("ҳ������\n");
						Global.processResult += "ҳ������\n";
						physicalAddress = (entry.getBlockNum() << 9) + offset;
						updateTLB(pcb, pageNum, entry.getBlockNum());     //���¿��
						return physicalAddress;
					}
				}
			}
		}
		
		//��ҳ��Ҳδ���У�����ȱҳ�ж�
		System.out.println("����ȱҳ�жϣ�");
		GUI.console_textArea.append("����ȱҳ�жϣ�\n");
		Global.processResult += "����ȱҳ�жϣ�\n";
		ifMissingPage = true;     //�޸ı�־λ
		GUI.interrupt_textArea.append("��");
		pcb.block();     //����PCB������������1
		interruptPcb = PCB.getBlockList1().get(0);     //�������ж�ͷ��PCB��Ϊ���ڴ���ȱҳ�жϵ�PCB
		if(ifDone) {
			ifDone = false;
			return MissingPage.getPhysicalAddress();
		}
		
		return -1;
	}
	
	public static int LRU() {     //����LRU�㷨ȷ��ҳ���滻���ڴ�����ڵ�������
		int time = CPU.getClock();
		Block outBlock = new Block();
		
		for(Block block : Memory.getBlockList()) {     //ȷ���滻�ĸ������
			if(block.getVisitTime() < time) {
				time = block.getVisitTime();
				outBlock = block;
			}
		}
		
		return outBlock.getStartAddress() >> 9;     //����������
	}
}
