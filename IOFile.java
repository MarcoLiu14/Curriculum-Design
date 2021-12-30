package code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class IOFile {     //文件读写

	public static ArrayList<String[]> readFile(String FileName) throws IOException {
		//找到并发作业请求文件的指定行后将其进行分割并返回
		BufferedReader br = new BufferedReader(new FileReader(FileName));
		String line = null;
		int count = 0;
		String[] temp = new String[5];     //开辟数组用来存储四个数
		ArrayList<String[]> tempList = new ArrayList<String[]>();     //开辟动态数组用来存储数组temp
		String delimeter = ",";     //设置分隔符
		
		while((line = br.readLine()) != null) {
			if(count != 0) {     //跳过第一行目录
				temp = line.split(delimeter);     //分割字符串
				tempList.add(temp);     //将此temp数组加入动态数组中
			}
			count++;	
		}
			
		br.close();
		return tempList;
	}
	
	public static ArrayList<String[]> readInstruc(String FileName) throws IOException {
		//找到指定作业对应的指令并将其指令编号和类型返回
		BufferedReader br = new BufferedReader(new FileReader(FileName));
		String line = null;
		int count = 0;
		String[] temp = new String[4];     //开辟数组用来存储四项内容
		ArrayList<String[]> tempList = new ArrayList<String[]>();     //开辟动态数组用来存储数组temp
		String delimeter = ",";     //设置分隔符
		
		while((line = br.readLine()) != null) {
			if(count != 0) {     //跳过第一行目录
				temp = line.split(delimeter);     //分割字符串
				tempList.add(temp);     //将temp数组加入动态数组中
			}
			count++;	
		}
			
		br.close();
		return tempList;
	}
	
	public static void writeToFile(String text, String FileName){
		//写指定文件
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
		//将新作业的信息写入input文件
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
