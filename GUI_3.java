package code;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.JTextArea;

public class GUI_3 {

	private JFrame frame;
	public static JTextArea textArea = new JTextArea();
	
	public JFrame getFrame() {
		return this.frame;
	}

	public GUI_3() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setTitle("½ø³ÌÒ³±í");
		frame.setBounds(100, 100, 200, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		scrollPane.setViewportView(textArea);
	}

}
