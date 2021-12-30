package code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class IOFile {     //�ļ���д

	public static ArrayList<String[]> readFile(String FileName) throws IOException {
		//�ҵ�������ҵ�����ļ���ָ���к�����зָ����
		BufferedReader br = new BufferedReader(new FileReader(FileName));
		String line = null;
		int count = 0;
		String[] temp = new String[5];     //�������������洢�ĸ���
		ArrayList<String[]> tempList = new ArrayList<String[]>();     //���ٶ�̬���������洢����temp
		String delimeter = ",";     //���÷ָ���
		
		while((line = br.readLine()) != null) {
			if(count != 0) {     //������һ��Ŀ¼
				temp = line.split(delimeter);     //�ָ��ַ���
				tempList.add(temp);     //����temp������붯̬������
			}
			count++;	
		}
			
		br.close();
		return tempList;
	}
	
	public static ArrayList<String[]> readInstruc(String FileName) throws IOException {
		//�ҵ�ָ����ҵ��Ӧ��ָ�����ָ���ź����ͷ���
		BufferedReader br = new BufferedReader(new FileReader(FileName));
		String line = null;
		int count = 0;
		String[] temp = new String[4];     //�������������洢��������
		ArrayList<String[]> tempList = new ArrayList<String[]>();     //���ٶ�̬���������洢����temp
		String delimeter = ",";     //���÷ָ���
		
		while((line = br.readLine()) != null) {
			if(count != 0) {     //������һ��Ŀ¼
				temp = line.split(delimeter);     //�ָ��ַ���
				tempList.add(temp);     //��temp������붯̬������
			}
			count++;	
		}
			
		br.close();
		return tempList;
	}
	
	public static void writeToFile(String text, String FileName){
		//дָ���ļ�
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(FileName);
			fileWriter.write(text);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			}
			catch (IOException e){
				e.printStackTrace();
			}
		}
	}
	
	public static void writeToJobsFile(String text) {
		//������ҵ����Ϣд��input�ļ�
		byte[] buff = new byte[]{};
		try {
			File file = new File("input/19218106-jobs-input.txt");
			buff = text.getBytes();
			FileOutputStream outputStream = new FileOutputStream(file, true);
			outputStream.write(buff);
			outputStream.flush();
			outputStream.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
