package code;

import java.util.ArrayList;
import java.util.LinkedList;

public class PCB {     //PCB��

	private int proID;     //���̱��
	private int priority;     //�������ȼ�
	private int createTimes;     //���̴���ʱ��
	private int endTimes;     //���̽���ʱ��
	private int runTimes;     //��������ʱ��
	private int turnTimes;     //������תʱ��
	private int instrucNum;     //���̰�����ָ����Ŀ
	private int size;     //����ָ���С
	private int pageTableStartAddress;     //����ҳ����ʼ��ַ
	private int pcbStartAddress;     //��ǰPCB��ŵ���ʼ��ַ
	private int pc = 1;     //��һ��Ҫִ�е�ָ����
	private int ir = 0;     //��ǰ����ִ�е�ָ����
	private int psw;     //���̵�ǰ״̬��1-����  2-����  3-����  4-����  5-������
	private boolean delete = false;     //���̳�����־
	
	private static LinkedList<PCB> readyList = new LinkedList<PCB>();     //��������
	private static LinkedList<PCB> blockList1 = new LinkedList<PCB>();     //��������1
	private static LinkedList<PCB> blockList2 = new LinkedList<PCB>();     //��������2
	private static LinkedList<PCB> blockList3 = new LinkedList<PCB>();     //��������3
	private static LinkedList<PCB> hangUpList = new LinkedList<PCB>();     //�������
	private static LinkedList<PCB> deleteList = new LinkedList<PCB>();     //��������
	
	private int rqNum;     //��������λ�ñ��
	private int rqTimes;     //�����������ʱ��
	private int bqNum1;     //��������1λ�ñ��
	private int bqTimes1;     //������������1��ʱ��
	private int bqNum2;     //��������2λ�ñ��
	private int bqTimes2;     //������������2��ʱ��
	private int bqNum3;     //��������3λ�ñ��
	private int bqTimes3;     //������������3��ʱ��
	private int hqNum;     //�������λ�ñ��
	private int hqTimes;     //���������е�ʱ��
	private int dqNum;     //��������λ�ñ��
	private int dqTimes;     //�����������е�ʱ��
	
	private int location;     //������ʱ�������У�1-�������� 2-��������1 3-��������2 4-��������3 5-�������У�
	private ArrayList<Block> hangUpBlocks =  new ArrayList<Block>();     //������������
	
	public PCB() {}
	
	public PCB(int proID, int priority, int createTimes, int instrucNum, int size) {
		this.proID = proID;
		this.priority = priority;
		this.createTimes = createTimes;
		this.instrucNum = instrucNum;
		this.size = size;
		create();
	}
	
	public int getProID() {     //��ȡ���̱��
		return this.proID;
	}
	
	public int getPriority() {     //��ȡ�������ȼ�
		return this.priority;
	}
	
	public int getCreateTimes() {     //��ȡ���̴���ʱ��
		return this.createTimes;
	}
	
	public int getEndTimes() {     //��ȡ���̽���ʱ��
		return this.endTimes;
	}
	
	public int getRunTimes() {     //��ȡ��������ʱ��
		return this.runTimes;
	}
	
	public void plusRunTimes() {     //��������ʱ��+1
		this.runTimes++;
	}
	
	public int getTurnTimes() {     //��ȡ������תʱ��
		return this.turnTimes;
	}
	
	public int getInstrucNum() {     //��ȡ���̰�����ָ����Ŀ
		return this.instrucNum;
	}
	
	public int getSize() {     //��ȡ����ָ���С
		return this.size;
	}
	
	public void setPageTableStartAddress(int pageTableStartAddress) {     //���ý���ҳ�����ʼ��ַ
		this.pageTableStartAddress = pageTableStartAddress;
	}
	
	public int getPageTableStartAddress() {     //��ȡ��ǰ����ҳ�����ʼ��ַ
		return this.pageTableStartAddress;
	}
	
	public void setPCBStartAddress(int pcbStartAddress) {     //���õ�ǰPCB��ŵ���ʼ��ַ
		this.pcbStartAddress = pcbStartAddress;
	}
	
	public int getPCBStartAddress() {     //��ȡ��ǰPCB��ŵ���ʼ��ַ
		return this.pcbStartAddress;
	}
	
	public int getPC() {     //��ȡ��һ��Ҫִ�е�ָ����
		return this.pc;
	}
	
	public void setPC(int pc) {     //���õ�ǰPC��ֵ
		this.pc = pc;
	}
	
	public void plusPC() {     //PC+1
		this.pc++;
	}
	
	public int getIR() {     //��ȡ��ǰ����ִ�е�ָ����
		return this.ir;
	}
	
	public void setIR(int ir) {     //���õ�ǰIR��ֵ
		this.ir = ir;
	}
	
	public void plusIR() {     //IR+1
		this.ir++;
	}
	
	public int getPSW() {     //��ȡ���̵�ǰ״̬
		return this.psw;
	}
	
	public void setPSW(int psw) {     //���õ�ǰPSW��ֵ
		this.psw = psw;
	}
	
	public boolean getDelete() {     //��ȡ��ǰ�Ľ��̳�����־
		return this.delete;
	}
	
	public void setDelete(boolean delete) {     //���ý��̳�����־
		this.delete = delete;
	}

	public static LinkedList<PCB> getReadyList() {     //��ȡ��������
		return readyList;
	}

	public static LinkedList<PCB> getBlockList1() {     //��ȡ��������1
		return blockList1;
	}

	public static LinkedList<PCB> getBlockList2() {     //��ȡ��������2
		return blockList2;
	}

	public static LinkedList<PCB> getBlockList3() {     //��ȡ��������3
		return blockList3;
	}

	public static LinkedList<PCB> getHangUpList() {     //��ȡ�������
		return hangUpList;
	}
	
	public static LinkedList<PCB> getDeleteList() {     //��ȡ��������
		return deleteList;
	}

	public int getRqNum() {     //��ȡPCB�ھ��������е�λ��
		return rqNum;
	}

	public void setRqNum(int rqNum) {     //����PCB�ھ��������е�λ��
		this.rqNum = rqNum;
	}

	public int getRqTimes() {     //��ȡPCB����������е�ʱ��
		return rqTimes;
	}

	public void setRqTimes(int rqTimes) {     //����PCB����������е�ʱ��
		this.rqTimes = rqTimes;
	}
	
	public int getBqNum1() {     //��ȡPCB����������1�е�λ��
		return bqNum1;
	}
	
	public void setBqNum1(int bqNum) {     //����PCB����������1�е�λ��
		this.bqNum1 = bqNum;
	}
	
	public int getBqTimes1() {     //��ȡPCB������������1��ʱ��
		return bqTimes1;
	}
	
	public void setBqTimes1(int bqTime) {     //����PCB������������1��ʱ��
		this.bqTimes1 = bqTime;
	}
	
	public int getBqNum2() {     //��ȡPCB����������2�е�λ��
		return bqNum2;
	}
	
	public void setBqNum2(int bqNum) {     //����PCB����������2�е�λ��
		this.bqNum2 = bqNum;
	}
	
	public int getBqTimes2() {     //��ȡPCB������������2��ʱ��
		return bqTimes2;
	}
	
	public void setBqTimes2(int bqTime) {     //����PCB������������2��ʱ��
		this.bqTimes2 = bqTime;
	}
	
	public int getBqNum3() {     //��ȡPCB����������3�е�λ��
		return bqNum3;
	}
	
	public void setBqNum3(int bqNum) {     //����PCB����������3�е�λ��
		this.bqNum3 = bqNum;
	}
	
	public int getBqTimes3() {     //��ȡPCB������������3��ʱ��
		return bqTimes3;
	}
	
	public void setBqTimes3(int bqTime) {     //����PCB������������3��ʱ��
		this.bqTimes3 = bqTime;
	}
	
	public int getHqNum() {     //��ȡPCB�ڹ�������е�λ��
		return this.hqNum;
	}
	
	public void setHqNum(int hqNum) {     //����PCB�ڹ�������е�λ��
		this.hqNum = hqNum;
	}
	
	public int getHqTimes() {     //��ȡPCB���������е�ʱ��
		return this.hqTimes;
	}
	
	public void setHqTimes(int hqTimes) {     //����PCB���������е�ʱ��
		this.hqTimes = hqTimes;
	}
	
	public int getDqNum() {     //��ȡPCB�����������е�λ��
		return this.dqNum;
	}
	
	public void setDqNum(int dqNum) {     //����PCB�����������е�λ��
		this.dqNum = dqNum;
	}
	
	public int getDqTimes() {     //��ȡPCB�����������е�ʱ��
		return this.dqTimes;
	}
	
	public void setDqTimes(int dqTimes) {     //����PCB�����������е�ʱ��
		this.dqTimes = dqTimes;
	}
	
	public int getLocation() {     //��ȡ������ʱ��������
		return this.location;
	}
	
	public void setLocation(int location) {     //���ñ�����ʱ��������
		this.location = location;
	}
	
	public ArrayList<Block> getHangUpBlocks() {     //��ȡ������������
		return this.hangUpBlocks;
	}
	
	public void setHangUpBlocks(ArrayList<Block> tempBlocks) {     //���ñ�����������
		hangUpBlocks.clear();
		for(Block block : tempBlocks) {
			this.hangUpBlocks.add(block);
		}
	}
	
	public static void inReadyList(PCB pcb) {     //��PCB�����������
		readyList.offer(pcb);
		pcb.setRqNum(readyList.indexOf(pcb));
		pcb.setRqTimes(CPU.getClock());
	}
	
	public static PCB outReadyList() {     //���������ж�ͷPCB����
		Dispatch.sortReadyList();     //�Ծ������а��վ�̬���ȼ���������
		PCB temp = readyList.poll();     //����ͷPCB����
		for(PCB i: readyList) {     //���¾���������PCB��λ��
			i.setRqNum(readyList.indexOf(i));
		}
		return temp;
	}
	
	public static void inBlockList1(PCB pcb) {     //��PCB������������1
		blockList1.offer(pcb);
		pcb.bqNum1 = blockList1.indexOf(pcb);
		pcb.bqTimes1 = CPU.getClock();
	}
	
	public static void inBlockList2(PCB pcb) {     //��PCB������������2
		blockList2.offer(pcb);
		pcb.bqNum2 = blockList2.indexOf(pcb);
		pcb.bqTimes2 = CPU.getClock();
	}
	
	public static void inBlockList3(PCB pcb) {     //��PCB������������3
		blockList3.offer(pcb);
		pcb.bqNum3 = blockList3.indexOf(pcb);
		pcb.bqTimes3 = CPU.getClock();
	}
	
	public static void inHangUpList(PCB pcb) {     //��PCB����������
		pcb.psw = 4;
		hangUpList.offer(pcb);
		pcb.hqNum = hangUpList.indexOf(pcb);
		pcb.hqTimes = CPU.getClock();
	}
	
	public static void inDeleteList(PCB pcb) {     //��PCB������������
		deleteList.offer(pcb);
		pcb.dqNum = deleteList.indexOf(pcb);
		pcb.dqTimes = CPU.getClock();
	}
	
	public void outCurrentList() {     //��PCB�ӵ�ǰ�����������Ƴ�
		if(this.psw == 1) {
			readyList.remove(this);
			for(PCB pcb : readyList) {
				pcb.rqNum = readyList.indexOf(pcb);
			}
		}
		else if(this.psw == 3) {
			if(blockList1.contains(this)) {
				blockList1.remove(this);
				for(PCB pcb : blockList1) {
					pcb.bqNum1 = blockList1.indexOf(pcb);
				}
			}
			else if(blockList2.contains(this)) {
				blockList2.remove(this);
				for(PCB pcb : blockList2) {
					pcb.bqNum2 = blockList2.indexOf(pcb);
				}
			}
			else if(blockList3.contains(this)) {
				blockList3.remove(this);
				for(PCB pcb : blockList3) {
					pcb.bqNum3 = blockList3.indexOf(pcb);
				}
			}
		}
	}
	
	public static void showReadyList() {     //չʾ������������
		System.out.print("�������У�");
		Global.processResult += "�������У�";
		for(int i=0; i<readyList.size(); i++) {
			System.out.print(readyList.get(i).proID + " ");
			GUI.ready_textArea.append(readyList.get(i).proID + " ");
			Global.processResult += readyList.get(i).proID + " ";
		}
		System.out.println();
		Global.processResult += "\n";
	}
	
	public static void showBlockList1() {     //չʾ��������1����
		System.out.print("��������1��");
		Global.processResult += "��������1��";
		for(PCB pcb : blockList1) {
			System.out.print(pcb.proID + " ");
			GUI.block1_textArea.append(pcb.proID + " ");
			Global.processResult += pcb.proID + " ";
		}
		System.out.println();
		Global.processResult += "\n";
	}
	
	public static void showBlockList2() {     //չʾ��������2����
		System.out.print("��������2��");
		Global.processResult += "��������2��";
		for(PCB pcb : blockList2) {
			System.out.print(pcb.proID + " ");
			GUI.block2_textArea.append(pcb.proID + " ");
			Global.processResult += pcb.proID + " ";
		}
		System.out.println();
		Global.processResult += "\n";
	}
	
	public static void showBlockList3() {     //չʾ��������3����
		System.out.print("��������3��");
		Global.processResult += "��������3��";
		for(PCB pcb : blockList3) {
			System.out.print(pcb.proID + " ");
			GUI.block3_textArea.append(pcb.proID + " ");
			Global.processResult += pcb.proID + " ";
		}
		System.out.println();
		Global.processResult += "\n";
	}
	
	public static void showHangUpList() {     //չʾ�����������
		System.out.print("������У�");
		Global.processResult += "������У�";
		for(PCB pcb : hangUpList) {
			System.out.print(pcb.proID + " ");
			GUI.hangUp_textArea.append(pcb.proID + " ");
			Global.processResult += pcb.proID + " ";
		}
		System.out.println();
		Global.processResult += "\n";
	}
	
	public static void showDeleteList() {     //չʾ������������
		System.out.print("�������У�");
		Global.processResult += "�������У�";
		for(PCB pcb : deleteList) {
			System.out.print(pcb.proID + " ");
			GUI.delete_textArea.append(pcb.proID + " ");
			Global.processResult += pcb.proID + " ";
		}
		System.out.println();
		Global.processResult += "\n";
	}

	//�������̵������Ӳ���
	public synchronized void create() {     //��ʼ��PCB������PCB�������������
		this.pc = 1;     //��ʼ����ؼĴ�����ֵ
		this.ir = 0;
		this.psw = 1;
		this.setRqNum(readyList.size());
		this.setRqTimes(CPU.getClock());
		MMU.savePCB(this);     //��PCB����PCB��
		inReadyList(this);     //����PCB�����������
		FileOperation.getFileOperation().createFile("root/pro"+String.valueOf(this.proID), "root");     //���������ļ�
		System.out.println("��ҵ" + this.proID + "�����ڴ棬��������" + this.proID + "������ʱ��Ϊ" + this.createTimes + "����ռ��СΪ" + this.size + "ҳ");
		GUI.console_textArea.append("��ҵ" + this.proID + "�����ڴ棬��������" + this.proID + "������ʱ��Ϊ" + this.createTimes + "����ռ��СΪ" + this.size + "ҳ\n");
		Global.processResult += "��ҵ" + this.proID + "�����ڴ棬��������" + this.proID + "������ʱ��Ϊ" + this.createTimes + "����ռ��СΪ" + this.size + "ҳ\n";
	}
	
	public static ArrayList<Instruction> init_Instruction(ArrayList<String[]> readInstruction){
		//��������֮��ʼ�������
		ArrayList<Instruction> instructionList = new ArrayList<Instruction>();     //����ָ�
		for(String[] i : readInstruction) {
			instructionList.add(new Instruction(Integer.parseInt(i[0]), Integer.parseInt(i[1]), Integer.parseInt(i[2]), i[3]));
		}
		return instructionList;
	}
	
	public synchronized void delete() {     //��������
		this.outCurrentList();
		this.psw = 5;
		this.endTimes = CPU.getClock();
		for(JCB jcb : Global.jobList) {
			if(this.proID == jcb.getJobID()) {
				Global.jobList.remove(jcb);     //������ҵ��ϵͳ��ҵ�����Ƴ�
				this.turnTimes = this.endTimes - jcb.getInTimes();     //������תʱ��
				break;
			}
		}
		for(Process process : Global.processList) {     //���˽��̴�ϵͳ���̱���ɾ��
			if(this.getProID() == process.getProID()) {
				Global.processList.remove(process);
				break;
			}
		}
		inDeleteList(this);
		MMU.deleteItem(this);     //���ոý����ڿ���еı���
		MMU.recycleBlock(this);     //���ոý��̶�Ӧ���ڴ������
		MMU.recycleSection(this);     //���ոý��̵�����
		System.out.println("��������" + this.proID + "��������ʱ��Ϊ" + this.runTimes + "����תʱ��Ϊ" + this.turnTimes);
		GUI.console_textArea.append("��������" + this.proID + "��������ʱ��Ϊ" + this.runTimes + "����תʱ��Ϊ" + this.turnTimes + "\n");
		Global.processResult += "��������" + this.proID + "��������ʱ��Ϊ" + this.runTimes + "����תʱ��Ϊ" + this.turnTimes + "\n";
	}
	
	public synchronized void block() {     //��������
		this.psw = 3;     //���̱�Ϊ����̬
		if(MMU.getIfMissingPage()) {     //ȱҳ�жϵĽ���PCB������������1
			this.setBqNum1(blockList1.size());
			this.setBqTimes1(CPU.getClock());
			inBlockList1(this);
			return;
		}
		
		for(Process process : Global.processList) {     //�����̼��뵽��Ӧ������������
			if(this.proID == process.getProID()) {
				if(process.getInstruc_State(this) == 1) {
					this.setBqNum2(blockList2.size());
					this.setBqTimes2(CPU.getClock());
					inBlockList2(this);
				}
				else if(process.getInstruc_State(this) == 2) {
					this.setBqNum3(blockList3.size());
					this.setBqTimes3(CPU.getClock());
					inBlockList3(this);
				}
				else if(process.getInstruc_State(this) == 4) {
					this.setBqNum3(blockList3.size());
					this.setBqTimes3(CPU.getClock());
					inBlockList3(this);
				}
				else if(process.getInstruc_State(this) == 5) {
					this.setBqNum2(blockList2.size());
					this.setBqTimes2(CPU.getClock());
					inBlockList2(this);
				}
			}
		}
	}
	
	public synchronized void wake() {     //���ѽ���
		this.outCurrentList();
		this.psw = 1;     //���̱�Ϊ����̬
		inReadyList(this);     //����PCB�����������
		Dispatch.sortReadyList();     //�Ծ������н�������
	}
}
