package main.java;


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.ConnectException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class Client extends JFrame {
	private String destinationIPAddr;	//目標的IP位置
	private int destinationPortNum;		//目標的port number
	private Socket socket;		//宣告socket
	public static PrintWriter writer;	//宣告writer,用來傳送訊息給server
	public static int ready,Index,waitans,solve,init,wrong;	//宣告會用到之相關變數
	public static int begin ;
	
	public Client() {	//初始化
		waitans = 0;
		ready = 0;
		Index = 0;
		solve = 0;
		init = 0;
		wrong = 0;
		begin = 1 ;
	}
	public Client(String IPAddress, int portNum) {	//初始化設定
		this();
		this.destinationIPAddr = IPAddress;
		this.destinationPortNum = portNum;
	}
	public Client setIPAddress(String IPAddress) {	//設定IP位置
		this.destinationIPAddr = IPAddress;
		return this;
	}
	public Client setPort(int portNum) {	//設定port number
		this.destinationPortNum = portNum;
		return this;
	}
	public void connect() {	
		try{	//create a socket
			this.socket = new Socket(this.destinationIPAddr,this.destinationPortNum);
			writer = new PrintWriter(new 							///writer 用來傳訊息給server
					OutputStreamWriter(socket.getOutputStream()));	
			BufferedReader reader = new BufferedReader(new		///reader 用來接收server之訊息
					InputStreamReader(socket.getInputStream()));
			ClientThread thread = new ClientThread(reader);	//開一個thread，用來接收server訊息
			thread.start();		//讓Thread開始
		}catch(UnknownHostException e){
			e.printStackTrace();
		}catch(ConnectException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	class ClientThread extends Thread{		//為inner class，此為接受訊息之thread
		private BufferedReader reader;		//宣告reader
		public ClientThread(BufferedReader reader){
			this.reader = reader;
		}
		public void run(){
			while(true){	//要隨時監視server是否有傳訊息過來
				try{
					String line = this.reader.readLine();	//若有server傳訊息，則line為server傳之訊息
					if(line.equals("ready")){	//若server傳的訊息是ready
						ready = 1;	//則代表client有兩人已加入，且需要初始化
						init = 1;	
					}
					else if(line.equals("solve")){	//若server傳的訊息是solve
						solve = 1;	//則代表client已解決問題
						waitans = 0;    //不須再等待另方輸入答案
						init = 1;
					}else if(line.equals("wrong")){	//若server傳的訊息是wrong
						wrong = 1;	//則代表client兩方輸入的不同，需重新輸入
						waitans = 0;	//不須再等待另方輸入答案
						init = 1;
					}else{		//其他情況則為server傳送要使用圖片之index來
						Index = Integer.parseInt(line);
					}
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		Client client = new Client();	//新創一個client
		
		client.setIPAddress("127.0.0.1").setPort(8000).connect();	//connect to server
		Main game = new Main() ;
		game.start();
		
	}

}
