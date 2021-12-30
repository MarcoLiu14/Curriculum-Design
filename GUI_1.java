package code;

import javax.swing.JFrame;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class GUI_1 {     //内存用户区界面

	private JFrame frame;
	public static JTextArea textArea = new JTextArea();
	
	public JFrame getFrame() {
		return this.frame;
	}

	public GUI_1() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setTitle("内存用户区");
		frame.setBounds(50, 10, 300, 650);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		scrollPane.setViewportView(textArea);
	}

}
