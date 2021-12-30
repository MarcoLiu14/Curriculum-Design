package code;

import java.util.LinkedList;

public class JCB {     //JCB��
	
	private int jobID;     //��ҵ���
	private int priority;     //��ҵ���ȼ�
	private int inTimes;     //��ҵ�ύʱ��
	private int instrucNum;     //��ҵ������ָ����Ŀ
	private int size;     //��ҵָ���С
	
	private static LinkedList<JCB> requestList = new LinkedList<JCB>();     //��ҵ�������
	
	public JCB(int jobID, int priority, int inTimes, int instrucNum, int size) {
		this.jobID = jobID;
		this.priority = priority;
		this.inTimes = inTimes;
		this.instrucNum = instrucNum;
		this.size = size;
	}
	
	public int getJobID() {     //��ȡ��ǰ��ҵ�ı��
		return this.jobID;
	}
	
	public int getPriority() {     //��ȡ��ǰ��ҵ�����ȼ�
		return this.priority;
	}
	
	public int getInTimes() {     //��ȡ��ǰ��ҵ���ύʱ��
		return this.inTimes;
	}
	
	public int getInstrucNum() {     //��ȡ��ǰ��ҵ������ָ����Ŀ
		return this.instrucNum;
	}
	
	public int getSize() {     //��ȡ��ǰ��ҵָ��Ĵ�С
		return this.size;
	}
	
	public static LinkedList<JCB> getRequestList(){     //��ȡ��ǰ����ҵ�������
		return requestList;
	}
	
	public static void showRequestList() {     //չʾ��ǰ����ҵ�������
		System.out.print("��ҵ������У�");
		Global.processResult += "��ҵ������У�";
		for(JCB jcb : requestList) {
			System.out.print(jcb.getJobID() + " ");
			GUI.request_textArea.append(jcb.getJobID() + " ");
			Global.processResult += jcb.getJobID() + " ";
		}
		System.out.println();
		Global.processResult += "\n";
	}
}
