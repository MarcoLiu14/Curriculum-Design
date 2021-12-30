package code;

import java.util.ArrayList;

public class Process extends PCB {     //������

	private ArrayList<Instruction> code_Segment = new ArrayList<Instruction>();     //�����
	
	public Process(int proID, int priority, int inTimes, int instrucNum, int size, ArrayList<Instruction> code_Segment) {
		super(proID, priority, inTimes, instrucNum, size);     //����PCB
		this.code_Segment = code_Segment;     //��������
		Global.processList.add(this);     //���˽�����ӵ�ϵͳ���̱���
	}
	
	public int getInstruc_ID() {     //��ȡָ��ı��
		return getIR();
	}
	
	public int getInstruc_State(PCB pcb) {     //��ȡ��ǰָ���״̬
		return code_Segment.get(pcb.getIR() - 1).getInstruc_State();
	}
	
	public int getInstruc_LogicalAddress(PCB pcb) {     //��ȡ��ǰָ����߼���ַ
		return code_Segment.get(pcb.getIR() - 1).getInstruc_LogicalAddress();
	}
	
	public ArrayList<Instruction> getCode_Segment() {     //��ȡ�����
		return this.code_Segment;
	}
}
