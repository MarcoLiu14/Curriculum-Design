package code;

import java.util.ArrayList;

public class FileOperation extends Thread {     //�����ļ������߳�
	
	private static FileOperation fileOperation = new FileOperation();     //�ļ����������
	
	private String currentPath;     //��ǰ·��
	private static String string = "";     //�����ļ�·��
	private static String info = "";     //�ļ�����
	private static boolean ifRead = false;     //���ļ���־λ
	private static boolean ifWrite = false;     //д�ļ���־λ
	private static boolean ifDelete = false;     //ɾ���ļ���־λ

	public void run() {
		while(true) {
			Global.lock.lock();     //������ 
			if(ifRead) {     //ִ�ж��ļ������������ļ������������Ļ
				Dispatch.setIfHangUp2(false);
				readFile(CPU.getCurrentFilePath1());
				System.out.println(Memory.getBufferZone1());
				GUI.console_textArea.append(Memory.getBufferZone1() + "\n");
				Global.processResult += Memory.getBufferZone1() + "\n";
				Memory.emptyBufferZone1();
				ifRead = false;
				CPU.setRelease1(true);
				Dispatch.setIfHangUp2(true);
			}
			if(ifWrite) {     //ִ��д�ļ���������������д���ļ�
				Dispatch.setIfHangUp2(false);
				writeFile(CPU.getCurrentFilePath2(), 1);
				Memory.emptyBufferZone2();
				ifWrite = false;
				CPU.setRelease2(true);
				Dispatch.setIfHangUp2(true);
			}
			if(ifDelete) {     //ִ��ɾ���ļ�����
				Dispatch.setIfHangUp2(false);
				deleteFile(CPU.getCurrentFilePath3());
				ifDelete = false;
				Dispatch.setIfHangUp2(true);
			}
			Global.lock.unlock();     //�ͷ���
		}
	}
	
	public static FileOperation getFileOperation() {     //��ȡ�ļ�������Ķ���
		return fileOperation;
	}
	
	public String getCurrentPath() {     //��ȡ��ǰ·��
		return this.currentPath;
	}
	
	public void setCurrentPath(PCB pcb) {     //���õ�ǰ·��
		this.currentPath = "root/pro" + String.valueOf(pcb.getProID());
	}
	
	public static void setIfRead(boolean ifread) {     //���ö��ļ���־λ
		ifRead = ifread;
	}
	
	public static void setIfWrite(boolean ifwrite) {     //����д�ļ���־λ
		ifWrite = ifwrite;
	}
	
	public static void setIfDelete(boolean ifdelete) {     //����ɾ���ļ���־λ
		ifDelete = ifdelete;
	}
	
	public void createFile(String filePath, String point) {     //�����ļ�
		int length;
		String[] s2 = filePath.split("/");
		String newFilePath;
		while(true) {
			//��ȷ��Ҫ�������ļ�
			if(point.equals("-1")) {
				point = "";
				length = 0;
				newFilePath = s2[length];
			}
			else {
				String[] s1 = point.split("/");
				length = s1.length;
				newFilePath = point + "/" + s2[length];
			}
			point = newFilePath; 
			
			//�𼶴����ļ�
			for(int i=0; i<Global.iNodeNum; i++) {
				if(FileVolume.getSuperBlock().getEmptyINode()[i] == 0) {
					FileVolume.getSuperBlock().getEmptyINode()[i] = 1;
					FileVolume.getINodeList().add(new INode(i));     //����iNode
					createDataBlock(i);     //�������ݿ�
					File file = new File(newFilePath);     //�����µ��ļ�����
					Global.fileList.add(file);     //���ļ�����ϵͳ�ļ���
					FCB fcb = new FCB(i, s2[length]);     //�����ļ�Ŀ¼��
					if(!s2[length].equals("root")) {
						updateFileMenu(newFilePath, fcb);     //�����ļ�Ŀ¼
					}
					break;
				}  
			}
			
			//�����ļ���������ɺ��������ѭ��
			if(point.split("/").length == s2.length) {
				break;
			}
		}
	}
	
	public void updateFileMenu(String filePath, FCB fcb) {     //����ָ���ļ�Ŀ¼
		//������һ��Ŀ¼�ļ�·��
		String path = "";
		String[] strings = filePath.split("/");
		for(int i=0; i<strings.length-1; i++) {
			path += strings[i];
			if(i != strings.length - 2) {
				path += "/";
			}
		}
		
		//���ļ�Ŀ¼�м����µ�FCB
		for(File file : Global.fileList) {
			if(file.getPath().equals(path)) {
				file.getFileMenu().add(fcb);
				break;
			}
		}
	}
	
	public void deleteFile(String filePath) {     //ɾ���ļ�
		//����ϵͳ�ļ����FCB
		String[] c = filePath.split("/");
		for(File file : Global.fileList) {
			if(file.getPath().equals(filePath)) {
				for(FCB fcb : file.getFileMenu()) {
					if(fcb.getfileName().equals(c[c.length])) {
						file.getFileMenu().remove(fcb);
						break;
					}
				}
				Global.fileList.remove(file);
				System.out.println("ɾ���ļ�" + filePath);
				GUI.console_textArea.append("ɾ���ļ�" + filePath + "\n");
				Global.processResult += "ɾ���ļ�" + filePath + "\n";
				break;
			}
			if(Global.fileList.indexOf(file) == Global.fileList.size() - 1) {
				System.out.println("�ļ�" + filePath + "�����ڣ��޷�ɾ����");
				GUI.console_textArea.append("�ļ�" + filePath + "�����ڣ��޷�ɾ����\n");
				Global.processResult += "�ļ�" + filePath + "�����ڣ��޷�ɾ����\n";
			}
		}
	}
	
	public void readFile(String filePath) {     //���ļ�
		int temp = find(splitFilePath(filePath));
		if(temp == -1) {     //Ҫ�����ļ������ڣ����������Ϣ
			Memory.setBufferZone1("�ļ�" + filePath + "�����ڣ�");
			return;
		}
		else if(temp >= 0) {     //�ļ����ڣ����ļ�
			for(INode iNode : FileVolume.getINodeList()) {
				if(temp == iNode.getId()) {     //�ҵ��ļ���Ӧ��iNode
					for(Integer i : iNode.getBlockNumList()) {     //����iNode�д洢�����ݿ�ı�Ų������ݿ�
						for(DataBlock dataBlock : FileVolume.getDataBlockList()) {
							if(dataBlock.getBlockID() == i) {
								info += dataBlock.getData();
								break;
							}
						}
					}
					break;
				}
			}
			output();     //�����������
		}
	}
	
	public void writeFile(String filePath, int type) {     //д�ļ�
		int temp = find(splitFilePath(filePath));
		if(temp == -1) {     //Ҫд���ļ������ڣ��򴴽����ļ�
			System.out.println("�ļ������ڣ������ļ�" + filePath);
			GUI.console_textArea.append("�ļ������ڣ������ļ�" + filePath + "\n");
			Global.processResult += "�ļ������ڣ������ļ�" + filePath + "\n";
			createFile(filePath, string);
		}
		else if(temp >= 0) {     //Ҫд���ļ����ڣ�ִ��д�ļ�����
			System.out.println("�ļ����ڣ�д�ļ�" + filePath);
			GUI.console_textArea.append("�ļ����ڣ�д�ļ�" + filePath + "\n");
			Global.processResult += "�ļ����ڣ�д�ļ�" + filePath + "\n";
			Memory.setBufferZone2("file_Info:" + filePath + "\n");
			for(INode iNode : FileVolume.getINodeList()) {
				if(temp == iNode.getId()) {     //�ҵ��ļ���Ӧ��iNode
					for(Integer i : iNode.getBlockNumList()) {     //����iNode�д洢�����ݿ�ı�Ų������ݿ�
						for(DataBlock dataBlock : FileVolume.getDataBlockList()) {
							if(dataBlock.getBlockID() == i) {
								if(type == 1) {
									dataBlock.setData(Memory.getBufferZone2());
								}
								else if(type == 2) {
									dataBlock.addData(Memory.getBufferZone2());
								}
								if(dataBlock.ifBlockFull()) {
									createDataBlock(iNode.getId());
								}
								break;
							}
						}
					}
					break;
				}
			}
		}
	}
	
	public void createDataBlock(int iNodeID) {     //Ϊ�ļ������µ����ݿ�
		FileVolume.getDataBlockList().add(new DataBlock(FileVolume.getSuperBlock().distributeBlock(iNodeID), iNodeID, ""));
	}
	
	public ArrayList<String> splitFilePath(String filePath) {     //�ֽ��ļ�·��
		String[] s = filePath.split("/");
		ArrayList<String> tempList = new ArrayList<String>();
		for(String string : s) {
			tempList.add(string);
		}
		return tempList;
	}
	
	public int find(ArrayList<String> s) {     //�����ļ�Ŀ¼�����ļ����ڣ��򷵻���iNode��ţ��������ڣ��򷵻�-1
		string = "";
		while(s.size() > 0) {
			if(Global.fileList.size() == 0) {     //����ʱϵͳ��û���ļ���ֱ�ӷ���-1
				string = "-1";
				return -1;
			}
			else {
				String temString = string;
				string += s.get(0);
				s.remove(0);
				for(File file : Global.fileList) {
					if(file.getPath().equals(string)) {     //�ҵ���·����Ӧ���ļ�
						if(file.getFileMenu().size() != 0) {     //�����ļ���Ŀ¼�ļ�
							for(FCB fcb : file.getFileMenu()) {
								if(fcb.getfileName().equals(s.get(0))) {
									if(s.size() > 1) {     //����û�б��������������������һ�ֲ���
										string += "/";	
									}
									else if(s.size() == 1) {     //����ʱ�Ѿ��������������ļ����ڣ������ļ���iNode���
										return fcb.getINodeID();
									}
									break;
								}
								if(file.getFileMenu().indexOf(fcb) == file.getFileMenu().size() - 1) {     //�����ļ�Ŀ¼��û����һ���ļ�������-1
									return -1;
								}
							}
						}
						
						else {     //�����ļ�����Ŀ¼�ļ�
							if(s.size() != 0) {     //�ļ�������
								return -1;
							}
						}
						break;
					}
					
					if(Global.fileList.indexOf(file) == Global.fileList.size() - 1) {     //����·����Ӧ���ļ������ڣ�����-1
						if(temString.isEmpty()) {
							string = "-1";
						}
						else {
							string = temString;
						}
						return -1;
					}
				}
			}
		}
		return 0;
	}
	
	public void output() {     //����ļ�����
		Memory.setBufferZone1("�ļ����ڣ�������Ϊ��");
		Memory.setBufferZone1(info);
		info = "";
	}
}
