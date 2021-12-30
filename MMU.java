package code;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MMU {     //MMU
	
	private static PCB currentPcb;     //正在寻址的进程PCB
	private static PCB interruptPcb;     //正在处理缺页中断的进程PCB
	private static int pageNum;     //页号
	private static int offset;     //偏移
	private static boolean ifMissingPage = false;     //缺页中断的标志
	private static boolean ifDone = false;     //缺页中断是否处理完成的标志
	
	private static TLB tlb = new TLB();     //系统快表
	
	public static PCB getCurrentPcb() {     //获取正在寻址的进程PCB
		return currentPcb;
	}
	
	public static PCB getInterruptPcb() {     //获取正在处理缺页中断的PCB
		return interruptPcb;
	}
	
	public static int getPageNum() {     //获取页号
		return pageNum;
	}
	
	public static int getOffset() {     //获取偏移
		return offset;
	}
	
	public static boolean getIfMissingPage() {     //获取缺页中断的标志
		return ifMissingPage;
	}
	
	public static void setIfMissingPage(boolean ifmissingpage) {     //设置缺页中断的标志
		ifMissingPage = ifmissingpage;
	}
	
	public static boolean getIfDone() {     //获取缺页中断是否完成的标志
		return ifDone;
	}
	
	public static void setIfDone(boolean ifdone) {     //设置缺页中断是否完成的标志
		ifDone = ifdone;
	}
	
	public static TLB getTlb() {     //获取进程快表
		return tlb;
	}
	
	public static void savePCB(PCB pcb) {     //将PCB存入PCB池中
		for(int i=0; i<Global.maxPcbNum; i++) {
			if(Memory.getPCBEmptyBlock()[i] == 0) {
				Memory.getPcbList().add(pcb);
				pcb.setPCBStartAddress(((i+1)<<9));
				Memory.getPCBEmptyBlock()[i] = 1;
				System.out.println("PCB存放在始址为0x" + Integer.toHexString((i+1)<<9) + "的物理块中");
				GUI.console_textArea.append("PCB存放在始址为0x" + Integer.toHexString((i+1)<<9) + "的物理块中\n");
				Global.processResult += "PCB存放在始址为0x" + Integer.toHexString((i+1)<<9) + "的物理块中\n";
				break;
			}
		}
	}
	
	public static void createPageTable(JCB jcb) {     //生成页表
		for(int i=0; i<Global.maxPageTableNum; i++) {
			if(Memory.getPageTableEmptyBlock()[i] == 0) {
				Memory.getPageTableList().add(new PageTable(i*16, jcb.getJobID()));
				Memory.getPageTableEmptyBlock()[i] = 1;
				System.out.println("页表存放在0号物理块的0x" + Integer.toHexString(i*16) + "偏移处");
				GUI.console_textArea.append("页表存放在0号物理块的0x" + Integer.toHexString(i*16) + "偏移处\n");
				Global.processResult += "页表存放在0号物理块的0x" + Integer.toHexString(i*16) + "偏移处\n";
			}
		}
	}
	
	private static Cylinder cylinder0 = new Cylinder(0);     //0号柱面
	private static Track track0 = new Track(0);     //0号磁道
	private static Track track1 = new Track(1);     //1号磁道
	
	public static Cylinder getCylinder0() {     //获取0号柱面
		return cylinder0;
	}
	
	public static Track getTrack0() {     //获取0号磁道
		return track0;
	}
	
	public static Track getTrack1() {     //获取1号磁道
		return track1;
	}
	
	public static void allocateExchangeMemory(JCB jcb) throws IOException {     //为进程分配磁盘交换区
		System.out.println("为进程分配磁盘交换区空间，其所在位置为：");
		GUI.console_textArea.append("为进程分配磁盘交换区空间，其所在位置为：\n");
		Global.processResult += "为进程分配磁盘交换区空间，其所在位置为：\n";
		
		//确定该进程的指令所在页号
		ArrayList<Integer> pageList = new ArrayList<Integer>();
		for(int m=0; m<IOFile.readInstruc("input/" + Integer.toString(jcb.getJobID()) + ".txt").size(); m++) {
			int page = Integer.parseInt(IOFile.readInstruc("input/" + Integer.toString(jcb.getJobID()) + ".txt").get(m)[2]) >> 9;
			if(!pageList.contains(page)) {
				pageList.add(page);
			}
		}
		
		//为进程分配扇区
		for(int m=0; m<pageList.size(); m++) {
			for(int i=0; i<Global.exchangeSectionNum; i++) {
				if(HardDisk.getEmptySection()[0][i] == 0) {
					if(!HardDisk.getCylinderList().contains(cylinder0)) {
						HardDisk.getCylinderList().add(cylinder0);
					}
					
					if(i / 64 == 0) {
						if(!cylinder0.getTrackList().contains(track0)) {
							cylinder0.getTrackList().add(track0);
						}
						Section section = new Section(0, i%64, jcb.getJobID(), pageList.get(m));     //分配扇区
						track0.getSectionList().add(section);
					}
					else {
						if(!cylinder0.getTrackList().contains(track1)) {
							cylinder0.getTrackList().add(track1);
						}
						Section section = new Section(1, i%64, jcb.getJobID(), pageList.get(m));     //分配扇区
						track1.getSectionList().add(section);
					}
					HardDisk.getEmptySection()[0][i] = 1;
					System.out.println(0 + "号柱面 " + i/64 + "号磁道 " + i%64 + "号扇区");
					GUI.console_textArea.append(0 + "号柱面 " + i/64 + "号磁道 " + i%64 + "号扇区\n");
					Global.processResult += 0 + "号柱面 " + i/64 + "号磁道 " + i%64 + "号扇区\n";
					break;
				}
			}
		}
		
		//存入指令和数据
		for(Track track : cylinder0.getTrackList()) {
			for(Section section : track.getSectionList()) {
				if(section.getProID() == jcb.getJobID()) {
					saveInstructionsAndNumbers(jcb, section);
				}
			}
		}
	}
	
	public static void saveInstructionsAndNumbers(JCB jcb, Section tempSection) {     //向扇区中存入指令和数据
		Random random = new Random();
		//存入指令
		for(Process process : Global.processList) {
			if(process.getProID() == jcb.getJobID()) {
				for(Instruction instruction : process.getCode_Segment()) {
					if(instruction.getInstruc_LogicalAddress() >> 9 == tempSection.getPageNum()) {
						tempSection.getInstructionsAndNumbers().put(instruction.getInstruc_LogicalAddress(), instruction.getInstruc_Info());
					}
				}
				break;
			}
		}
		//存入数据
		for(int i=0; i<512; i++) {
			if(!tempSection.getInstructionsAndNumbers().containsKey((tempSection.getPageNum()<<9)+i)) {
				tempSection.getInstructionsAndNumbers().put((tempSection.getPageNum()<<9)+i, Integer.toString(random.nextInt(1000)));
			}
		}
	}
	
	public static void updateTLB(PCB pcb, int pageNum, int blockNum) {     //更新快表
		if(tlb.getItemList().size() == Global.maxItemNum) {     //若快表项已满，则先移出第一个表项
			tlb.getItemList().remove(0);
		}
		tlb.getItemList().add(new Item(pageNum, blockNum, pcb.getProID()));     //存入新的表项
	}
	
	public static void deleteItem(PCB pcb) {     //删除进程在快表中的表项
		for(int i=0; i<tlb.getItemList().size(); i++) {
			if(tlb.getItemList().get(i).getProID() == pcb.getProID()) {
				tlb.getItemList().remove(i);
				i = -1;
			}
		}
	}
	
	public static void recycleBlock(PCB pcb) {     //回收内存物理块
		//回收进程页表
		for(PageTable pageTable : Memory.getPageTableList()) {
			if(pageTable.getProID() == pcb.getProID()) {
				Memory.getPageTableList().remove(pageTable);
				Memory.getPageTableEmptyBlock()[pageTable.getStartAddress()/16] = 0;
				break;
			}
		}
		
		//回收PCB
		Memory.getPcbList().remove(pcb);
		Memory.getPCBEmptyBlock()[(pcb.getPCBStartAddress()>>9)-1] = 0;
		
		//回收用户区物理块
		for(int i=0; i<Memory.getBlockList().size(); i++) {
			if(Memory.getBlockList().get(i).getProID() == pcb.getProID()) {
				Memory.getUserEmptyBlock()[(Memory.getBlockList().get(i).getStartAddress() >> 9) - Global.userBlockNum] = 0;
				Memory.getBlockList().get(i).getInstructionsAndNumbers().clear();
				Memory.getBlockList().remove(i);
				i = -1;
			}
		}
	}
	
	public static void recycleSection(PCB pcb) {     //回收磁盘交换区扇区
		for(int i=0; i<track0.getSectionList().size(); i++) {
			if(track0.getSectionList().get(i).getProID() == pcb.getProID() && track0.getSectionList().get(i).getSectionID() != -1) {
				HardDisk.getEmptySection()[0][track0.getSectionList().get(i).getSectionID()] = 0;
				track0.getSectionList().get(i).getInstructionsAndNumbers().clear();
				track0.getSectionList().remove(i);
				i = -1;
			}
		}
		for(int i=0; i<track1.getSectionList().size(); i++) {
			if(track1.getSectionList().get(i).getProID() == pcb.getProID() && track0.getSectionList().get(i).getSectionID() != -1) {
				HardDisk.getEmptySection()[0][track1.getSectionList().get(i).getSectionID()+Global.sectionNum] = 0;
				track1.getSectionList().get(i).getInstructionsAndNumbers().clear();
				track1.getSectionList().remove(i);
				i = -1;
			}
		}
	}
	
	public static String visitStorage(PCB pcb, int logicalAddress) {     //访存
		System.out.println("进程" + pcb.getProID() + "访存取指令...");
		GUI.console_textArea.append("进程" + pcb.getProID() + "访存取指令...\n");
		Global.processResult += "进程" + pcb.getProID() + "访存取指令...\n";
		currentPcb = pcb;
		int physicalAddress = convert(pcb, logicalAddress);     //根据逻辑地址得到物理地址
		
		if(!ifMissingPage) {
			System.out.println("逻辑地址：0x" + Integer.toHexString(logicalAddress));
			System.out.println("物理地址：0x" + Integer.toHexString(physicalAddress));
			GUI.console_textArea.append("逻辑地址：0x" + Integer.toHexString(logicalAddress) + "\n");
			GUI.console_textArea.append("物理地址：0x" + Integer.toHexString(physicalAddress) + "\n");
			GUI.instruc_textArea.append("逻辑地址：0x" + Integer.toHexString(logicalAddress) + "，物理地址：0x" + Integer.toHexString(physicalAddress) + "\n");
			Global.processResult += "逻辑地址：0x" + Integer.toHexString(logicalAddress) + "，物理地址：0x" + Integer.toHexString(physicalAddress) + "\n";
		}
		
		int blockNum = physicalAddress >> 9;     //块号
		String instruction = "-1";
		for(Block block : Memory.getBlockList()) {
			if(block.getStartAddress() == blockNum << 9) {
				block.setVisitTime(CPU.getClock());
				
				instruction = block.getInstructionsAndNumbers().get(physicalAddress);
				return instruction;
			}
		}
		return instruction;
	}

	public static int convert(PCB pcb, int logicalAddress) {     //将逻辑地址转换为物理地址
		pageNum = logicalAddress >> 9;     //页号
		offset = logicalAddress & 0x01ff;     //偏移量
		int physicalAddress;     //物理地址
		
		//首先访问快表
		for(Item item : tlb.getItemList()) {
			if(item.getPageNum() == pageNum && item.getProID() == pcb.getProID()) {     //若快表命中，计算物理地址
				System.out.println("快表命中");
				GUI.console_textArea.append("快表命中\n");
				Global.processResult += "快表命中\n";
				physicalAddress = (item.getBlockNum() << 9) + offset;
				return physicalAddress;
			}
		}
		
		//若快表未命中，则访问页表
		for(PageTable pageTable : Memory.getPageTableList()) {
			if(pcb.getPageTableStartAddress() == pageTable.getStartAddress()) {
				for(Entry entry : pageTable.getEntryList()) {
					if(entry.getPageNum() == pageNum) {     //若页表命中，计算物理地址
						System.out.println("页表命中");
						GUI.console_textArea.append("页表命中\n");
						Global.processResult += "页表命中\n";
						physicalAddress = (entry.getBlockNum() << 9) + offset;
						updateTLB(pcb, pageNum, entry.getBlockNum());     //更新快表
						return physicalAddress;
					}
				}
			}
		}
		
		//若页表也未命中，则发生缺页中断
		System.out.println("发生缺页中断！");
		GUI.console_textArea.append("发生缺页中断！\n");
		Global.processResult += "发生缺页中断！\n";
		ifMissingPage = true;     //修改标志位
		GUI.interrupt_textArea.append("√");
		pcb.block();     //将此PCB加入阻塞队列1
		interruptPcb = PCB.getBlockList1().get(0);     //阻塞队列队头的PCB即为正在处理缺页中断的PCB
		if(ifDone) {
			ifDone = false;
			return MissingPage.getPhysicalAddress();
		}
		
		return -1;
	}
	
	public static int LRU() {     //利用LRU算法确定页面替换进内存后所在的物理块号
		int time = CPU.getClock();
		Block outBlock = new Block();
		
		for(Block block : Memory.getBlockList()) {     //确定替换哪个物理块
			if(block.getVisitTime() < time) {
				time = block.getVisitTime();
				outBlock = block;
			}
		}
		
		return outBlock.getStartAddress() >> 9;     //返回物理块号
	}
}
