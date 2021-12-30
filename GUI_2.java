package code;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JSplitPane;

public class GUI_2 {     //磁盘交换区界面

	private JFrame frame;
	public static JTextArea textArea = new JTextArea();
	public static JTextArea textArea_1 = new JTextArea();
	public static JTextArea textArea_2 = new JTextArea();

	public JFrame getFrame() {
		return this.frame;
	}

	public GUI_2() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setTitle("磁盘交换区");
		frame.setBounds(380, 10, 900, 650);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setBounds(0, 0, 886, 613);
		splitPane.setDividerLocation(292);
		frame.getContentPane().add(splitPane);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);
		
		scrollPane.setViewportView(textArea);
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setDividerLocation(292);
		splitPane.setRightComponent(splitPane_1);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane_1.setLeftComponent(scrollPane_1);
		
		scrollPane_1.setViewportView(textArea_1);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		splitPane_1.setRightComponent(scrollPane_2);
		
		scrollPane_2.setViewportView(textArea_2);
	}
}
