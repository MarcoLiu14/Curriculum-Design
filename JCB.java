package code;

import java.util.LinkedList;

public class JCB {     //JCB类
	
	private int jobID;     //作业编号
	private int priority;     //作业优先级
	private int inTimes;     //作业提交时间
	private int instrucNum;     //作业包含的指令数目
	private int size;     //作业指令集大小
	
	private static LinkedList<JCB> requestList = new LinkedList<JCB>();     //作业请求队列
	
	public JCB(int jobID, int priority, int inTimes, int instrucNum, int size) {
		this.jobID = jobID;
		this.priority = priority;
		this.inTimes = inTimes;
		this.instrucNum = instrucNum;
		this.size = size;
	}
	
	public int getJobID() {     //获取当前作业的编号
		return this.jobID;
	}
	
	public int getPriority() {     //获取当前作业的优先级
		return this.priority;
	}
	
	public int getInTimes() {     //获取当前作业的提交时间
		return this.inTimes;
	}
	
	public int getInstrucNum() {     //获取当前作业包含的指令数目
		return this.instrucNum;
	}
	
	public int getSize() {     //获取当前作业指令集的大小
		return this.size;
	}
	
	public static LinkedList<JCB> getRequestList(){     //获取当前的作业请求队列
		return requestList;
	}
	
	public static void showRequestList() {     //展示当前的作业请求队列
		System.out.print("作业请求队列：");
		Global.processResult += "作业请求队列：";
		for(JCB jcb : requestList) {
			System.out.print(jcb.getJobID() + " ");
			GUI.request_textArea.append(jcb.getJobID() + " ");
			Global.processResult += jcb.getJobID() + " ";
		}
		System.out.println();
		Global.processResult += "\n";
	}
}
