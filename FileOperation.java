package code;

import java.util.ArrayList;

public class FileOperation extends Thread {     //磁盘文件操作线程
	
	private static FileOperation fileOperation = new FileOperation();     //文件操作类对象
	
	private String currentPath;     //当前路径
	private static String string = "";     //保存文件路径
	private static String info = "";     //文件数据
	private static boolean ifRead = false;     //读文件标志位
	private static boolean ifWrite = false;     //写文件标志位
	private static boolean ifDelete = false;     //删除文件标志位

	public void run() {
		while(true) {
			Global.lock.lock();     //请求锁 
			if(ifRead) {     //执行读文件操作，并将文件内容输出到屏幕
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
			if(ifWrite) {     //执行写文件操作，并将内容写入文件
				Dispatch.setIfHangUp2(false);
				writeFile(CPU.getCurrentFilePath2(), 1);
				Memory.emptyBufferZone2();
				ifWrite = false;
				CPU.setRelease2(true);
				Dispatch.setIfHangUp2(true);
			}
			if(ifDelete) {     //执行删除文件操作
				Dispatch.setIfHangUp2(false);
				deleteFile(CPU.getCurrentFilePath3());
				ifDelete = false;
				Dispatch.setIfHangUp2(true);
			}
			Global.lock.unlock();     //释放锁
		}
	}
	
	public static FileOperation getFileOperation() {     //获取文件操作类的对象
		return fileOperation;
	}
	
	public String getCurrentPath() {     //获取当前路径
		return this.currentPath;
	}
	
	public void setCurrentPath(PCB pcb) {     //设置当前路径
		this.currentPath = "root/pro" + String.valueOf(pcb.getProID());
	}
	
	public static void setIfRead(boolean ifread) {     //设置读文件标志位
		ifRead = ifread;
	}
	
	public static void setIfWrite(boolean ifwrite) {     //设置写文件标志位
		ifWrite = ifwrite;
	}
	
	public static void setIfDelete(boolean ifdelete) {     //设置删除文件标志位
		ifDelete = ifdelete;
	}
	
	public void createFile(String filePath, String point) {     //创建文件
		int length;
		String[] s2 = filePath.split("/");
		String newFilePath;
		while(true) {
			//逐级确定要创建的文件
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
			
			//逐级创建文件
			for(int i=0; i<Global.iNodeNum; i++) {
				if(FileVolume.getSuperBlock().getEmptyINode()[i] == 0) {
					FileVolume.getSuperBlock().getEmptyINode()[i] = 1;
					FileVolume.getINodeList().add(new INode(i));     //分配iNode
					createDataBlock(i);     //分配数据块
					File file = new File(newFilePath);     //生成新的文件对象
					Global.fileList.add(file);     //将文件加入系统文件表
					FCB fcb = new FCB(i, s2[length]);     //生成文件目录项
					if(!s2[length].equals("root")) {
						updateFileMenu(newFilePath, fcb);     //更新文件目录
					}
					break;
				}  
			}
			
			//各级文件均创建完成后便跳出死循环
			if(point.split("/").length == s2.length) {
				break;
			}
		}
	}
	
	public void updateFileMenu(String filePath, FCB fcb) {     //更新指定文件目录
		//生成上一级目录文件路径
		String path = "";
		String[] strings = filePath.split("/");
		for(int i=0; i<strings.length-1; i++) {
			path += strings[i];
			if(i != strings.length - 2) {
				path += "/";
			}
		}
		
		//向文件目录中加入新的FCB
		for(File file : Global.fileList) {
			if(file.getPath().equals(path)) {
				file.getFileMenu().add(fcb);
				break;
			}
		}
	}
	
	public void deleteFile(String filePath) {     //删除文件
		//更新系统文件表和FCB
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
				System.out.println("删除文件" + filePath);
				GUI.console_textArea.append("删除文件" + filePath + "\n");
				Global.processResult += "删除文件" + filePath + "\n";
				break;
			}
			if(Global.fileList.indexOf(file) == Global.fileList.size() - 1) {
				System.out.println("文件" + filePath + "不存在，无法删除！");
				GUI.console_textArea.append("文件" + filePath + "不存在，无法删除！\n");
				Global.processResult += "文件" + filePath + "不存在，无法删除！\n";
			}
		}
	}
	
	public void readFile(String filePath) {     //读文件
		int temp = find(splitFilePath(filePath));
		if(temp == -1) {     //要读的文件不存在，输出报错信息
			Memory.setBufferZone1("文件" + filePath + "不存在！");
			return;
		}
		else if(temp >= 0) {     //文件存在，读文件
			for(INode iNode : FileVolume.getINodeList()) {
				if(temp == iNode.getId()) {     //找到文件对应的iNode
					for(Integer i : iNode.getBlockNumList()) {     //根据iNode中存储的数据块的编号查找数据块
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
			output();     //进行输出操作
		}
	}
	
	public void writeFile(String filePath, int type) {     //写文件
		int temp = find(splitFilePath(filePath));
		if(temp == -1) {     //要写的文件不存在，则创建该文件
			System.out.println("文件不存在，创建文件" + filePath);
			GUI.console_textArea.append("文件不存在，创建文件" + filePath + "\n");
			Global.processResult += "文件不存在，创建文件" + filePath + "\n";
			createFile(filePath, string);
		}
		else if(temp >= 0) {     //要写的文件存在，执行写文件操作
			System.out.println("文件存在，写文件" + filePath);
			GUI.console_textArea.append("文件存在，写文件" + filePath + "\n");
			Global.processResult += "文件存在，写文件" + filePath + "\n";
			Memory.setBufferZone2("file_Info:" + filePath + "\n");
			for(INode iNode : FileVolume.getINodeList()) {
				if(temp == iNode.getId()) {     //找到文件对应的iNode
					for(Integer i : iNode.getBlockNumList()) {     //根据iNode中存储的数据块的编号查找数据块
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
	
	public void createDataBlock(int iNodeID) {     //为文件创建新的数据块
		FileVolume.getDataBlockList().add(new DataBlock(FileVolume.getSuperBlock().distributeBlock(iNodeID), iNodeID, ""));
	}
	
	public ArrayList<String> splitFilePath(String filePath) {     //分解文件路径
		String[] s = filePath.split("/");
		ArrayList<String> tempList = new ArrayList<String>();
		for(String string : s) {
			tempList.add(string);
		}
		return tempList;
	}
	
	public int find(ArrayList<String> s) {     //遍历文件目录，若文件存在，则返回其iNode编号；若不存在，则返回-1
		string = "";
		while(s.size() > 0) {
			if(Global.fileList.size() == 0) {     //若此时系统内没有文件，直接返回-1
				string = "-1";
				return -1;
			}
			else {
				String temString = string;
				string += s.get(0);
				s.remove(0);
				for(File file : Global.fileList) {
					if(file.getPath().equals(string)) {     //找到该路径对应的文件
						if(file.getFileMenu().size() != 0) {     //若该文件是目录文件
							for(FCB fcb : file.getFileMenu()) {
								if(fcb.getfileName().equals(s.get(0))) {
									if(s.size() > 1) {     //若还没有遍历结束，则继续进行下一轮查找
										string += "/";	
									}
									else if(s.size() == 1) {     //若此时已经遍历结束，且文件存在，返回文件的iNode编号
										return fcb.getINodeID();
									}
									break;
								}
								if(file.getFileMenu().indexOf(fcb) == file.getFileMenu().size() - 1) {     //若该文件目录中没有下一级文件，返回-1
									return -1;
								}
							}
						}
						
						else {     //若该文件不是目录文件
							if(s.size() != 0) {     //文件不存在
								return -1;
							}
						}
						break;
					}
					
					if(Global.fileList.indexOf(file) == Global.fileList.size() - 1) {     //若该路径对应的文件不存在，返回-1
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
	
	public void output() {     //输出文件数据
		Memory.setBufferZone1("文件存在，其内容为：");
		Memory.setBufferZone1(info);
		info = "";
	}
}
