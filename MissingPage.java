package code;

import java.util.HashMap;

public class MissingPage extends Thread {     //缺页中断线程

	public void run() {
		while(true) {
			Global.lock.lock();     //请求锁
			try {
				Global.condition.await();     //线程进入等待状态
				if(MMU.getIfMissingPage()) {
					if(MMU.getInterruptPcb().getPSW() == 4) {
						if(PCB.getBlockList1().isEmpty()) {
							MMU.setIfMissingPage(false);
						}
					}
				}
				
				if(MMU.getIfMissingPage()) {
					Dispatch.setIfHangUp4(false);
					System.out.println("处理缺页中断");
					GUI.console_textArea.append("处理缺页中断\n");
					Global.processResult += "处理缺页中断\n";
					
					PCB temPcb = PCB.getBlockList1().get(0);
					dealMissingPage(temPcb, MMU.getPageNum(), MMU.getOffset());
					temPcb.wake();
					MMU.setIfDone(true);
					MMU.setIfMissingPage(false);
					Dispatch.setIfHangUp4(true);
				}
			}catch(InterruptedException e) {
				e.printStackTrace();
			}finally {
				Global.lock.unlock();     //释放锁
			}
		}
	}
	
	private static int physicalAddress;     //物理地址
	
	public static int getPhysicalAddress() {     //获取物理地址
		return physicalAddress;
	}
	
	public synchronized void dealMissingPage(PCB pcb, int pageNum, int offset) {     //处理缺页中断
		int blockNum;
		for(int i=0; i<Global.userBlockNum; i++) {
			//若内存用户区还有空闲物理块，则直接放入
			if(Memory.getUserEmptyBlock()[i] == 0) {
				blockNum = Global.userBlockNum + i;
				Memory.getBlockList().add(new Block(blockNum<<9, pcb.getProID(), pageNum));
				Memory.getUserEmptyBlock()[i] = 1;
			
				for(PageTable pageTable : Memory.getPageTableList()) {     //更新页表
					if(pageTable.getStartAddress() == pcb.getPageTableStartAddress()) {
						pageTable.getEntryList().add(new Entry(pageNum, Global.userBlockNum+i));
						break;
					}
				}
				MMU.updateTLB(pcb, pageNum, Global.userBlockNum+i);     //更新快表
				
				physicalAddress = blockNum + offset;     //合成新的物理地址
				
				//页面置换
				for(Section tempSection : MMU.getTrack0().getSectionList()) {
					if(tempSection.getPageNum() == pageNum && tempSection.getProID() == pcb.getProID() && tempSection.getSectionID() != -1) {
						exchange1(tempSection, pageNum, blockNum);
						break;
					}
				}
				for(Section tempSection : MMU.getTrack1().getSectionList()) {
					if(tempSection.getPageNum() == pageNum && tempSection.getProID() == pcb.getProID() && tempSection.getSectionID() != -1) {
						exchange1(tempSection, pageNum, blockNum);
						break;
					}
				}
				
				break;
			}
			
			if(i == Global.userBlockNum - 1) {
				System.out.println("调用LRU算法进行虚页调度！");
				GUI.console_textArea.append("调用LRU算法进行虚页调度！\n");
				Global.processResult += "调用LRU算法进行虚页调度！\n";
				
				//若无空闲物理块，则调用LRU算法进行置换
				blockNum = MMU.LRU();
				physicalAddress = (blockNum << 9) + offset;     //合成新的物理地址
				
				for(PageTable pageTable : Memory.getPageTableList()) {     //更新页表
					if(pageTable.getStartAddress() == pcb.getPageTableStartAddress()) {
						for(Entry entry : pageTable.getEntryList()) {
							if(entry.getBlockNum() == blockNum) {
								entry.setPageNum(pageNum);
								break;
							}
						}
					}
				}
					
				MMU.updateTLB(pcb, pageNum, blockNum);     //更新快表
				
				//页面置换
				for(Section tempSection : MMU.getTrack0().getSectionList()) {
					if(tempSection.getPageNum() == pageNum && tempSection.getProID() == pcb.getProID() && tempSection.getSectionID() != -1) {
						exchange2(pcb.getProID(), tempSection, pageNum, blockNum);
						break;
					}
				}
				for(Section tempSection : MMU.getTrack1().getSectionList()) {
					if(tempSection.getPageNum() == pageNum && tempSection.getProID() == pcb.getProID() && tempSection.getSectionID() != -1) {
						exchange2(pcb.getProID(), tempSection, pageNum, blockNum);
						break;
					}
				}
			}
		}
	}
	
	public void exchange1(Section tempSection, int pageNum, int blockNum) {     //页面置换
		//将指令集移入内存
		for(Block block : Memory.getBlockList()) {
			if(block.getStartAddress() >> 9 == blockNum) {
				for(Integer i : tempSection.getInstructionsAndNumbers().keySet()) {
					block.getInstructionsAndNumbers().put((blockNum<<9)+(i&0x1ff), tempSection.getInstructionsAndNumbers().get(i));
				}
				Memory.getUserEmptyBlock()[(block.getStartAddress() >> 9) - Global.userBlockNum] = 1;
				break;
			}
		}
		
		//修改位示图
		if(MMU.getTrack0().getSectionList().contains(tempSection)) {
			HardDisk.getEmptySection()[0][tempSection.getSectionID()] = 0;
			tempSection.setSectionID(-1);
		}
		else if(MMU.getTrack1().getSectionList().contains(tempSection)) {
			HardDisk.getEmptySection()[0][tempSection.getSectionID()+Global.sectionNum] = 0;
			tempSection.setSectionID(-1);
		}
	}
	
	public void exchange2(int proID, Section tempSection, int pageNum, int blockNum) {     //LRU页面置换
		HashMap<Integer, String> tempMap = new HashMap<Integer, String>();
		
		//对换两个页面的指令集
		for(Block block : Memory.getBlockList()) {
			if(block.getStartAddress() >> 9 == blockNum) {
				//暂存物理块中要被换出的指令集
				for(Integer i : block.getInstructionsAndNumbers().keySet()) {
					tempMap.put(i, block.getInstructionsAndNumbers().get(i));
				}
				//将扇区中的指令集换入
				for(Integer i : tempSection.getInstructionsAndNumbers().keySet()) {
					block.getInstructionsAndNumbers().put((blockNum<<9)+(i&0x1ff), tempSection.getInstructionsAndNumbers().get(i));
				}
				
				//更新相关属性值
				tempSection.setProID(block.getProID());
				tempSection.setPageNum(block.getPageNum());
				block.setProID(proID);
				block.setPageNum(pageNum);
			}
		}
		//将物理块中的指令集换出
		tempSection.getInstructionsAndNumbers().clear();
		for(Integer i : tempMap.keySet()) {
			tempSection.getInstructionsAndNumbers().put(i, tempMap.get(i));
		}
	}
}
