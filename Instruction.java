package code;

public class Instruction {     //ָ����
	
	private int instruc_ID;     //ָ����
	private int instruc_State;     //ָ������
	private int instruc_LogicalAddress;     //ָ���߼���ַ
	private String instruc_Info;     //ָ������
	
	public Instruction(int instruc_ID, int instruc_State, int instruc_LogicalAddress, String instruc_Info) {
		this.instruc_ID = instruc_ID;
		this.instruc_State = instruc_State;
		this.instruc_LogicalAddress = instruc_LogicalAddress;
		this.instruc_Info = instruc_Info;
	}
	
	public int getInstruc_ID() {     //��ȡָ����
		return this.instruc_ID;
	}
	
	public int getInstruc_State() {     //��ȡָ������
		return this.instruc_State;
	}
	
	public int getInstruc_LogicalAddress() {     //��ȡָ���߼���ַ
		return this.instruc_LogicalAddress;
	}
	
	public String getInstruc_Info() {     //��ȡָ������
		return this.instruc_Info;
	}
}
