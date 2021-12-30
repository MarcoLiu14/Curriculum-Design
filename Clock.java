package code;

public class Clock extends Thread {     //系统时钟线程
	
	private static boolean stop = false;     //判断时钟是否停止的标志
	
	public static void setStop(boolean s) {     //设置时钟是否停止的标志											
		stop = s;
	}
	
	public void run() {     //每秒发送一次中断信号
		while(true) {
			Global.lock.lock();     //请求锁
			if(CPU.getClock() != 0 && !stop) {
				//更新可视化界面
				refresh();
				showFileList();
				showTLB();
				showUserBlockList();
				showExchangeSectionList();
				showPageTable();
				IO.setSyn(true);
				
				//更新时间
				System.out.println("---------------------------------------------------------------------");
				System.out.println("第" + CPU.getClock() + "秒");
				GUI.clock_textArea.append(String.valueOf(CPU.getClock()));
				GUI.console_textArea.append("---------------------------------------------------------------------\n");
				GUI.console_textArea.append("第" + CPU.getClock() + "秒\n");
				Global.processResult += "---------------------------------------------------------------------\n";
				Global.processResult += "第" + CPU.getClock() + "秒\n";
				
				//死锁检测
				PV.testDeadLock();
				if(Global.deadLock) {
					GUI.deadLock_textArea.append("√");
					System.out.println("发生死锁！");
					GUI.console_textArea.append("发生死锁！\n");
					Global.processResult += "发生死锁！\n";
				}
				else {
					GUI.deadLock_textArea.append("×");
				}
				
				//控制滚动面板的进度条始终在最底部
				GUI.file_textArea.setCaretPosition(GUI.file_textArea.getText().length());
				GUI.console_textArea.setCaretPosition(GUI.console_textArea.getText().length());
				
				Global.condition.signalAll();     //唤醒所有加锁线程
			}
			Global.lock.unlock();     //释放锁
			
			if(!stop) {     //当时钟正常运行时，每秒休眠一次
				try {
					Thread.sleep(1000);
					CPU.setClock();
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void refresh() {     //刷新文本框
		GUI.clock_textArea.setText("");
		GUI.deadLock_textArea.setText("");
		GUI.pc_textArea.setText("");
		GUI.ir_textArea.setText("");
		GUI.psw_textArea.setText("");
		GUI.cpuState_textArea.setText("");
		GUI.interrupt_textArea.setText("");
		GUI.request_textArea.setText("");
		GUI.ready_textArea.setText("");
		GUI.block1_textArea.setText("");
		GUI.block2_textArea.setText("");
		GUI.block3_textArea.setText("");
		GUI.hangUp_textArea.setText("");
		GUI.delete_textArea.setText("");
		GUI.instruc_textArea.setText("");
		GUI.buffer1_textArea.setText("");
		GUI.buffer2_textArea.setText("");
		GUI.p1_textArea.setText("");
		GUI.v1_textArea.setText("");
		GUI.p2_textArea.setText("");
		GUI.v2_textArea.setText("");
		GUI.mutex1_textArea.setText("");
		GUI.mutex2_textArea.setText("");
		GUI.file_textArea.setText("");
		GUI.tlb_textArea.setText("");
		GUI_1.textArea.setText("");
		GUI_2.textArea.setText("");
		GUI_2.textArea_1.setText("");
		GUI_2.textArea_2.setText("");
		GUI_3.textArea.setText("");
	}
	
	public void showFileList() {     //显示系统文件列表
		for(File file : Global.fileList) {
			GUI.file_textArea.append(file.getPath() + "\n");
		}
	}
	
	public void showTLB() {     //显示系统快表
		GUI.tlb_textArea.append("序号\t逻辑页号\t物理块号\t进程编号\n");
		for(Item item : MMU.getTlb().getItemList()) {
			GUI.tlb_textArea.append(MMU.getTlb().getItemList().indexOf(item) + "\t" + item.getPageNum() + "\t" + item.getBlockNum() + "\t" + item.getProID() + "\n");
		}
	}
	
	public void showUserBlockList() {     //显示内存用户区每个物理块的使用情况
		GUI_1.textArea.append("块号\t起始地址\t进程号\t页号\n");
		for(int i=0; i<Global.userBlockNum; i++) {
			if(Memory.getBlockList().size() == 0) {
				GUI_1.textArea.append(String.valueOf(i+Global.userBlockNum) + "\t0x" + Integer.toHexString((i+Global.userBlockNum)<<9) + "\t-1\t-1\n");
			}
			else {
				for(Block block : Memory.getBlockList()) {
					if((block.getStartAddress() >> 9) == (i + Global.userBlockNum)) {
						GUI_1.textArea.append(String.valueOf(block.getStartAddress()>>9) + "\t0x" + Integer.toHexString(block.getStartAddress()) + "\t" + block.getProID() + "\t" + block.getPageNum() + "\n");
						break;
					}
					if(Memory.getBlockList().indexOf(block) == Memory.getBlockList().size() - 1) {
						GUI_1.textArea.append(String.valueOf(i+Global.userBlockNum) + "\t0x" + Integer.toHexString((i+Global.userBlockNum)<<9) + "\t-1\t-1\n");
					}
				}
			}
		}
		
		//显示内存用户区的使用进度条
		int count = 0;
		for(int i=0; i<Global.userBlockNum; i++) {
			if(Memory.getUserEmptyBlock()[i] == 1) {
				count++;
			}
		}
		GUI.userProgressBar.setValue(count);
	}
	
	public void showExchangeSectionList() {     //显示磁盘交换区每个扇区的使用情况
		GUI_2.textArea.append("磁道号\t扇区号\t进程号\t页号\n");
		GUI_2.textArea_1.append("磁道号\t扇区号\t进程号\t页号\n");
		GUI_2.textArea_2.append("磁道号\t扇区号\t进程号\t页号\n");
		for(int i=0; i<Global.sectionNum; i++) {
			if(MMU.getTrack0().getSectionList().size() == 0) {
				if(i < Global.sectionNum / 2) {
					GUI_2.textArea.append("0\t" + i + "\t-1\t-1\n");
				}
				else {
					GUI_2.textArea_1.append("0\t" + i + "\t-1\t-1\n");
				}
			}
			else {
				for(Section section0 : MMU.getTrack0().getSectionList()) {
					if(section0.getSectionID() == i) {
						if(i < Global.sectionNum / 2) {
							GUI_2.textArea.append("0\t" + section0.getSectionID() + "\t" + section0.getProID() + "\t" + section0.getPageNum() + "\n");
						}
						else {
							GUI_2.textArea_1.append("0\t" + section0.getSectionID() + "\t" + section0.getProID() + "\t" + section0.getPageNum() + "\n");
						}
						break;
					}
					if(MMU.getTrack0().getSectionList().indexOf(section0) == MMU.getTrack0().getSectionList().size() - 1) {
						if(i < Global.sectionNum / 2) {
							GUI_2.textArea.append("0\t" + i + "\t-1\t-1\n");
						}
						else {
							GUI_2.textArea_1.append("0\t" + i + "\t-1\t-1\n");
						}
					}
				}
			}
		}
		
		for(int j=0; j<Global.sectionNum/2; j++) {
			if(MMU.getTrack1().getSectionList().size() == 0) {
				GUI_2.textArea_2.append("1\t" + j + "\t-1\t-1\n");
			}
			else {
				for(Section section1 : MMU.getTrack1().getSectionList()) {
					if(section1.getSectionID() == j) {
						GUI_2.textArea_2.append("1\t" + section1.getSectionID() + "\t" + section1.getProID() + "\t" + section1.getPageNum() + "\n");
						break;
					}
					if(MMU.getTrack1().getSectionList().indexOf(section1) == MMU.getTrack1().getSectionList().size() - 1) {
						GUI_2.textArea_2.append("1\t" + j + "\t-1\t-1\n");
					}
				}
			}
		}
		
		//显示磁盘交换区的使用进度条
		int count = 0;
		for(int i=0; i<Global.exchangeSectionNum; i++) {
			if(HardDisk.getEmptySection()[0][i] == 1) {
				count++;
			}
		}
		GUI.exchangeProgressBar.setValue(count);
	}
	
	public void showPageTable() {     //显示进程页表
		GUI_3.textArea.append("逻辑页号\t物理块号\n");
		for(PageTable pageTable : Memory.getPageTableList()) {
			GUI_3.textArea.append("--------进程" + pageTable.getProID() + "--------\n");
			for(Entry entry : pageTable.getEntryList()) {
				GUI_3.textArea.append(entry.getPageNum() + "\t" + entry.getBlockNum() + "\n");
			}
		}
	}
}
