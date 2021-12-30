package code;

public class Instruction {     //指令类
	
	private int instruc_ID;     //指令编号
	private int instruc_State;     //指令类型
	private int instruc_LogicalAddress;     //指令逻辑地址
	private String instruc_Info;     //指令内容
	
	public Instruction(int instruc_ID, int instruc_State, int instruc_LogicalAddress, String instruc_Info) {
		this.instruc_ID = instruc_ID;
		this.instruc_State = instruc_State;
		this.instruc_LogicalAddress = instruc_LogicalAddress;
		this.instruc_Info = instruc_Info;
	}
	
	public int getInstruc_ID() {     //获取指令编号
		return this.instruc_ID;
	}
	
	public int getInstruc_State() {     //获取指令类型
		return this.instruc_State;
	}
	
	public int getInstruc_LogicalAddress() {     //获取指令逻辑地址
		return this.instruc_LogicalAddress;
	}
	
	public String getInstruc_Info() {     //获取指令内容
		return this.instruc_Info;
	}
}
