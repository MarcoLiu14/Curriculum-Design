package code;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JProgressBar;

public class GUI {     //主界面

	private JFrame frame;
	public static JTextArea clock_textArea = new JTextArea();
	public static JTextArea deadLock_textArea = new JTextArea();
	public static JTextArea pc_textArea = new JTextArea();
	public static JTextArea ir_textArea = new JTextArea();
	public static JTextArea psw_textArea = new JTextArea();
//	public static JTextArea r1_textArea = new JTextArea();
//	public static JTextArea r2_textArea = new JTextArea();
	public static JTextArea cpuState_textArea = new JTextArea();
	public static JTextArea interrupt_textArea = new JTextArea();
	public static JTextArea request_textArea = new JTextArea();
	public static JTextArea run_textArea = new JTextArea();
	public static JTextArea ready_textArea = new JTextArea();
	public static JTextArea block1_textArea = new JTextArea();
	public static JTextArea block2_textArea = new JTextArea();
	public static JTextArea block3_textArea = new JTextArea();
	public static JTextArea hangUp_textArea = new JTextArea();
	public static JTextArea delete_textArea = new JTextArea();
	public static JTextArea instruc_textArea = new JTextArea();
	public static JTextArea buffer1_textArea = new JTextArea();
	public static JTextArea buffer2_textArea = new JTextArea();
	public static JTextArea p1_textArea = new JTextArea();
	public static JTextArea v1_textArea = new JTextArea();
	public static JTextArea p2_textArea = new JTextArea();
	public static JTextArea v2_textArea = new JTextArea();
	public static JTextArea mutex1_textArea = new JTextArea();
	public static JTextArea mutex2_textArea = new JTextArea();
	public static JTextArea file_textArea = new JTextArea();
	public static JTextArea tlb_textArea = new JTextArea();
	public static JTextArea console_textArea = new JTextArea();
	
	public static JProgressBar userProgressBar = new JProgressBar(0, Global.userBlockNum);
	public static JProgressBar exchangeProgressBar = new JProgressBar(0, Global.exchangeSectionNum);
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public GUI() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setTitle("19218106-刘子健");
		frame.setBounds(120, 10, 1028, 675);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton startButton = new JButton("开始仿真");
		startButton.setBounds(26, 605, 93, 23);
		frame.getContentPane().add(startButton);
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("系统启动！");
				console_textArea.append("系统启动！\n");
				new Clock().start();
				new Request().start();
				new Dispatch().start();
				new MissingPage().start();
				new FileOperation().start();
				new IO().start();
				
				FileOperation.getFileOperation().createFile("root", "-1");     //创建根目录
			}
		});
		
		JButton stopButton = new JButton("暂停仿真");
		stopButton.setBounds(169, 605, 93, 23);
		frame.getContentPane().add(stopButton);
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Clock.setStop(true);
			}
		});
		
		JButton skipButton = new JButton("继续仿真");
		skipButton.setBounds(312, 605, 93, 23);
		frame.getContentPane().add(skipButton);
		skipButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Clock.setStop(false);
			}
		});
		
		JButton saveButton = new JButton("保存并退出");
		saveButton.setBounds(455, 605, 100, 23);
		frame.getContentPane().add(saveButton);
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				IOFile.writeToFile(Global.processResult, "output/Process.txt");
				Clock.setStop(true);
				frame.dispose();
			}
		});
		
		JButton userButton = new JButton("内存用户区");
		userButton.setBounds(598, 605, 100, 23);
		frame.getContentPane().add(userButton);
		userButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							GUI_1 window = new GUI_1();
							window.getFrame().setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		
		JButton exchangeButton = new JButton("磁盘交换区");
		exchangeButton.setBounds(741, 605, 100, 23);
		frame.getContentPane().add(exchangeButton);
		exchangeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							GUI_2 window = new GUI_2();
							window.getFrame().setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		
		JButton pageTableButton = new JButton("进程页表");
		pageTableButton.setBounds(885, 605, 93, 23);
		frame.getContentPane().add(pageTableButton);
		pageTableButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							GUI_3 window = new GUI_3();
							window.getFrame().setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		
		JButton createProcessButton = new JButton("随机生成新作业");
		createProcessButton.setBounds(112, 39, 134, 23);
		frame.getContentPane().add(createProcessButton);
		createProcessButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					createNewProcess();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JLabel clock_Label = new JLabel("系统时钟");
		clock_Label.setBounds(287, 560, 85, 29);
		clock_Label.setFont(new Font("宋体", Font.BOLD+Font.ITALIC, 20));
		clock_Label.setForeground(Color.red);
		frame.getContentPane().add(clock_Label);
		
		clock_textArea.setBounds(382, 552, 64, 43);
		clock_textArea.setFont(new Font("宋体", Font.BOLD, 30));
		clock_textArea.setForeground(Color.red);
		frame.getContentPane().add(clock_textArea);
		
		JLabel deadLock_Label = new JLabel("死锁检测");
		deadLock_Label.setForeground(Color.BLUE);
		deadLock_Label.setFont(new Font("宋体", Font.BOLD+Font.ITALIC, 20));
		deadLock_Label.setBounds(469, 560, 93, 29);
		frame.getContentPane().add(deadLock_Label);
		
		deadLock_textArea.setForeground(Color.BLUE);
		deadLock_textArea.setFont(new Font("宋体", Font.BOLD, 30));
		deadLock_textArea.setBounds(564, 552, 64, 43);
		frame.getContentPane().add(deadLock_textArea);
		
		JLabel pc_Label = new JLabel("PC");
		pc_Label.setBounds(10, 10, 54, 15);
		frame.getContentPane().add(pc_Label);
		pc_textArea.setForeground(Color.BLACK);
		
		pc_textArea.setBounds(30, 5, 21, 23);
		frame.getContentPane().add(pc_textArea);
		
		JLabel ir_Label = new JLabel("IR");
		ir_Label.setBounds(65, 10, 54, 15);
		frame.getContentPane().add(ir_Label);
		
		ir_textArea.setBounds(85, 5, 21, 23);
		frame.getContentPane().add(ir_textArea);
		
		JLabel psw_Label = new JLabel("PSW");
		psw_Label.setBounds(120, 10, 54, 15);
		frame.getContentPane().add(psw_Label);
		
		psw_textArea.setBounds(155, 5, 21, 23);
		frame.getContentPane().add(psw_textArea);
		
//		JLabel r1_Label = new JLabel("R1");
//		r1_Label.setBounds(182, 10, 54, 15);
//		frame.getContentPane().add(r1_Label);
//		
//		r1_textArea.setBounds(203, 5, 36, 23);
//		frame.getContentPane().add(r1_textArea);
//		
//		JLabel r2_Label = new JLabel("R2");
//		r2_Label.setBounds(246, 10, 54, 15);
//		frame.getContentPane().add(r2_Label);
//		
//		r2_textArea.setBounds(267, 5, 36, 23);
//		frame.getContentPane().add(r2_textArea);
		
		JLabel cpuState_Label = new JLabel("CPU工作状态");
		cpuState_Label.setBounds(185, 10, 74, 15);
		frame.getContentPane().add(cpuState_Label);
		
		cpuState_textArea.setBounds(265, 5, 47, 23);
		frame.getContentPane().add(cpuState_textArea);
		
		JLabel interrupt_Label = new JLabel("缺页中断");
		interrupt_Label.setBounds(10, 43, 54, 15);
		frame.getContentPane().add(interrupt_Label);
		
		interrupt_textArea.setBounds(65, 38, 21, 23);
		frame.getContentPane().add(interrupt_textArea);
		
		JLabel request_Label = new JLabel("后备作业");
		request_Label.setBounds(10, 83, 57, 15);
		frame.getContentPane().add(request_Label);
		
		request_textArea.setBounds(79, 78, 157, 23);
		frame.getContentPane().add(request_textArea);
		
		JLabel ready_Label = new JLabel("就绪队列");
		ready_Label.setBounds(10, 123, 54, 15);
		frame.getContentPane().add(ready_Label);
		
		ready_textArea.setBounds(79, 118, 157, 23);
		frame.getContentPane().add(ready_textArea);
		
		JLabel block1_Label = new JLabel("阻塞队列1");
		block1_Label.setBounds(10, 164, 64, 15);
		frame.getContentPane().add(block1_Label);
		
		block1_textArea.setBounds(79, 159, 157, 23);
		frame.getContentPane().add(block1_textArea);
		
		JLabel block2_Label = new JLabel("阻塞队列2");
		block2_Label.setBounds(10, 206, 77, 15);
		frame.getContentPane().add(block2_Label);
		
		block2_textArea.setBounds(79, 201, 157, 23);
		frame.getContentPane().add(block2_textArea);
		
		JLabel block3_Label = new JLabel("阻塞队列3");
		block3_Label.setBounds(10, 246, 77, 15);
		frame.getContentPane().add(block3_Label);
		
		block3_textArea.setBounds(79, 241, 157, 23);
		frame.getContentPane().add(block3_textArea);
		
		JLabel hangUp_Label = new JLabel("挂起队列");
		hangUp_Label.setBounds(10, 286, 54, 15);
		frame.getContentPane().add(hangUp_Label);
		
		hangUp_textArea.setBounds(79, 281, 157, 23);
		frame.getContentPane().add(hangUp_textArea);
		
		JLabel delete_Label = new JLabel("消亡队列");
		delete_Label.setBounds(10, 326, 64, 15);
		frame.getContentPane().add(delete_Label);
		
		delete_textArea.setBounds(79, 321, 157, 23);
		frame.getContentPane().add(delete_textArea);
		
		JLabel instruc_Label = new JLabel("CPU当前执行指令");
		instruc_Label.setBounds(87, 354, 106, 15);
		frame.getContentPane().add(instruc_Label);
		
		instruc_textArea.setBounds(13, 379, 249, 72);
		frame.getContentPane().add(instruc_textArea);
		
		JLabel buffer1_Label = new JLabel("缓冲区1进程ID");
		buffer1_Label.setBounds(13, 475, 95, 15);
		frame.getContentPane().add(buffer1_Label);
		
		buffer1_textArea.setBounds(103, 470, 21, 23);
		frame.getContentPane().add(buffer1_textArea);
		
		JLabel buffer2_Label = new JLabel("缓冲区2进程ID");
		buffer2_Label.setBounds(151, 475, 95, 15);
		frame.getContentPane().add(buffer2_Label);
		
		buffer2_textArea.setBounds(234, 470, 21, 23);
		frame.getContentPane().add(buffer2_textArea);
		
		JLabel p1_Label = new JLabel("P1操作进程ID");
		p1_Label.setBounds(13, 512, 95, 15);
		frame.getContentPane().add(p1_Label);
		
		p1_textArea.setBounds(103, 507, 21, 23);
		frame.getContentPane().add(p1_textArea);
		
		JLabel v1_Label = new JLabel("V1操作进程ID");
		v1_Label.setBounds(151, 512, 85, 15);
		frame.getContentPane().add(v1_Label);
		
		v1_textArea.setBounds(234, 507, 21, 23);
		frame.getContentPane().add(v1_textArea);
		
		JLabel p2_Label = new JLabel("P2操作进程ID");
		p2_Label.setBounds(13, 549, 85, 15);
		frame.getContentPane().add(p2_Label);
		
		p2_textArea.setBounds(103, 541, 21, 23);
		frame.getContentPane().add(p2_textArea);
		
		JLabel v2_Label = new JLabel("V2操作进程ID");
		v2_Label.setBounds(151, 549, 85, 15);
		frame.getContentPane().add(v2_Label);
		
		v2_textArea.setBounds(234, 540, 21, 23);
		frame.getContentPane().add(v2_textArea);
		
		JLabel mutex1_Label = new JLabel("当前mutex1值");
		mutex1_Label.setBounds(13, 580, 95, 15);
		frame.getContentPane().add(mutex1_Label);
		
		mutex1_textArea.setBounds(103, 575, 21, 23);
		frame.getContentPane().add(mutex1_textArea);
		
		JLabel mutex2_Label = new JLabel("当前mutex2值");
		mutex2_Label.setBounds(151, 580, 85, 15);
		frame.getContentPane().add(mutex2_Label);
		
		mutex2_textArea.setBounds(234, 575, 21, 23);
		frame.getContentPane().add(mutex2_textArea);
		
		JLabel file_Label = new JLabel("系统文件列表");
		file_Label.setBounds(436, 10, 85, 15);
		frame.getContentPane().add(file_Label);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(317, 35, 307, 306);
		frame.getContentPane().add(scrollPane);
	
		scrollPane.setViewportView(file_textArea);

		JLabel tlbLabel = new JLabel("快表TLB");
		tlbLabel.setBounds(436, 354, 54, 15);
		frame.getContentPane().add(tlbLabel);
		
		tlb_textArea.setBounds(317, 379, 307, 163);
		frame.getContentPane().add(tlb_textArea);
		
		JLabel console_Label = new JLabel("系统实时操作");
		console_Label.setBounds(785, 10, 74, 15);
		frame.getContentPane().add(console_Label);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(651, 35, 353, 506);
		frame.getContentPane().add(scrollPane_1);
		
		scrollPane_1.setViewportView(console_textArea);
		
		JLabel userLabel = new JLabel("内存用户区使用情况：");
		userLabel.setBounds(688, 549, 124, 15);
		frame.getContentPane().add(userLabel);
		
		userProgressBar.setBounds(810, 549, 146, 15);
		frame.getContentPane().add(userProgressBar);
		userProgressBar.setForeground(Color.BLUE);
		userProgressBar.setStringPainted(true);
		
		JLabel exchangeLabel = new JLabel("磁盘交换区使用情况：");
		exchangeLabel.setBounds(688, 580, 134, 15);
		frame.getContentPane().add(exchangeLabel);
		
		exchangeProgressBar.setBounds(810, 580, 146, 15);
		frame.getContentPane().add(exchangeProgressBar);
		exchangeProgressBar.setForeground(Color.BLUE);
		exchangeProgressBar.setStringPainted(true);
	}
	
	public void createNewProcess() throws IOException {     //随机生成新作业
		//确定作业编号
		int currentProNum = IOFile.readFile("input/19218106-jobs-input.txt").size();
		int jobsID = currentProNum + 1;
		
		//随机生成作业优先级、指令数目和指令集大小
		Random random = new Random();
		int priority = random.nextInt(5) + 1;
		int instrucNum = random.nextInt(6) + 45;
		int size = random.nextInt(5) + 11;
		
		//随机生成指令集
		int instruc_State;
		int pageNum;
		int offset;
		int logicalAddress;
		String instruc_Info = "";
		String code_Segment = "Instruc_ID ,Instruc_State,Instruc_LogicalAddress,Instruc_Info\n";
		
		for(int i=0; i<instrucNum; i++) {
			//随机生成指令类型
			instruc_State = random.nextInt(6);
			
			//随机生成指令的逻辑地址
			pageNum = random.nextInt(size);
			offset = random.nextInt(512);
			logicalAddress = (pageNum << 9) + offset;
			
			//随机生成指令内容
			if(instruc_State == 0) {     //赋值运算指令
				instruc_Info = " ";
				
//				int info1 = random.nextInt(3);
//				int info2 = random.nextInt(2) + 1;
//				int info3 = random.nextInt(201) - 100;
//				if(info1 == 0) {     //MOV指令
//					instruc_Info += "0 " + String.valueOf(info2) + " " + String.valueOf(info3);
//				}
//				else if(info1 == 1) {     //ADD指令
//					instruc_Info += "1 " + String.valueOf(info2) + " " + String.valueOf(info3);
//				}
//				else if(info1 == 2) {     //SUB指令
//					instruc_Info += "2 " + String.valueOf(info2) + " " + String.valueOf(info3);
//				}
			}
			else if(instruc_State == 1 || instruc_State == 2 || instruc_State == 3) {     //读写删除文件指令
				int num = random.nextInt(9) + 1;
				instruc_Info += "root/pro" + jobsID + "/file" + num;
			}
			else if(instruc_State == 4 || instruc_State == 5) {     //系统输入输出指令
				instruc_Info = " "; 
			}
			
			//将指令加入指令集中
			code_Segment += (i+1) + "," + instruc_State + "," + logicalAddress + "," + instruc_Info + "\n";
			instruc_Info = "";
		}
		
		//将新作业信息写入文件
		IOFile.writeToJobsFile(jobsID + "," + priority + "," + (CPU.getClock()+5) + "," + instrucNum + "," + size + "\n");
		IOFile.writeToFile(code_Segment, "input/" + jobsID + ".txt");
	}
}
