package code;

import java.util.ArrayList;

public class Process extends PCB {     //进程类

	private ArrayList<Instruction> code_Segment = new ArrayList<Instruction>();     //程序段
	
	public Process(int proID, int priority, int inTimes, int instrucNum, int size, ArrayList<Instruction> code_Segment) {
		super(proID, priority, inTimes, instrucNum, size);     //构造PCB
		this.code_Segment = code_Segment;     //构造程序段
		Global.processList.add(this);     //将此进程添加到系统进程表中
	}
	
	public int getInstruc_ID() {     //获取指令的编号
		return getIR();
	}
	
	public int getInstruc_State(PCB pcb) {     //获取当前指令的状态
		return code_Segment.get(pcb.getIR() - 1).getInstruc_State();
	}
	
	public int getInstruc_LogicalAddress(PCB pcb) {     //获取当前指令的逻辑地址
		return code_Segment.get(pcb.getIR() - 1).getInstruc_LogicalAddress();
	}
	
	public ArrayList<Instruction> getCode_Segment() {     //获取程序段
		return this.code_Segment;
	}
}
