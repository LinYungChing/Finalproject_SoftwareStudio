package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class Server {
	private int Index,ansnum;  //宣告會用到之變數
	private static int num ;
	private int[] check = new int[51]; 	//此陣列為用來產生不重複之亂數
	private ServerSocket serverSocket;	//宣告server socket
	private List<ConnectionThread> connections = new ArrayList<ConnectionThread>();	//宣告client之array list
	private Random random = new Random();	//宣告Random
	private String [] ans = new String[2];	//宣告一陣列用來記住client傳回之答案
	public Server(int portNum) {	//做初始化之動作
		for(int i=0;i<51;i++) check[i]=0;	
		num = 0;
		Index = 0;
		ansnum = 0;
		try {
			this.serverSocket = new ServerSocket(portNum);	//創一server socket並設定port number
			System.out.printf("Server started !\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void sendindex(){	//此method為產生一不重複變數，並利用send message傳送要使用之圖片index給client
		Index = random.nextInt(51);
		while(check[Index]==1) Index = random.nextInt(51);
		check[Index]=1;
		
		connections.get(0).sendMessage(Integer.toString(Index));	//分別將index的值傳給client
		connections.get(1).sendMessage(Integer.toString(Index));
	}
	public void runForever() {	//此為會一直跑的method，用來接受client連線
		System.out.println("Server starts waiting for client.");
		boolean notready = true;	
		while(true){
			try{
				Socket connectionToClient = this.serverSocket.accept();	//若client連線到server，則會accept
				System.out.println("Server is connect!");	//印出一些server、及client之資訊
				System.out.println("Player"+(num+1)+"'s host name is 127.0.0.1");
				System.out.println("Player"+(num+1)+"'s IP Address is "+connectionToClient.getInetAddress());
				if(num==0) System.out.println("Server is waiting for client.");	
				ConnectionThread connThread = new ConnectionThread(connectionToClient);	//為每一個client新創一個thread，讓兩方可溝通
				connThread.start();	//讓connect thread開始
				connections.add(connThread);	//加到陣列中，方便管理
				num++;	//num代表連線的client數
				System.out.println("Server.num =  "+num);
				if(num>=2 && notready){	//若連線的client數大於等於兩個，則遊戲開始
					sendindex();	//將index傳送給client
					connections.get(0).sendMessage("ready");	//並將開始的資訊傳給每個client
					connections.get(1).sendMessage("ready");
					notready = false;
				}
			}catch(BindException e){
				//e.printStackTrace();
			}catch(IOException e){
				//e.printStackTrace();
			}
		}
	}
	
	class ConnectionThread extends Thread{	//此為connect thread，為一inner class
		private Socket socket;	//宣告會用到之變數
		private BufferedReader reader;
		private PrintWriter writer;
		
		public ConnectionThread(Socket socket){	//constructor
			this.socket = socket;
			try{
				this.writer = new PrintWriter(new 		///writer 用來傳訊息給client
						OutputStreamWriter(this.socket.getOutputStream()));
				this.reader = new BufferedReader(new	///reader 用來接收client之訊息
						InputStreamReader(this.socket.getInputStream()));
			}catch(IOException e){
				//e.printStackTrace();
			}
		}
		public void run(){	
			while(true){	//不斷監視client看有沒有傳送訊息來
				String line;
 				try {
					line = this.reader.readLine();	//若有傳訊息來，則會讀到line當中
					ans[ansnum] = new String(line);	//將client傳送之答案加到陣列中
					ansnum++;	//並增加傳送來的答案個數
					if(ansnum>=2){	//若兩方皆傳送答案， 則開始比對
						if(ans[0].equals(ans[1])){	//若一樣，則代表解決了
							Server.this.sendindex();	//將下一題的index傳給client
							connections.get(0).sendMessage("solve");	//並傳送solve的訊息給client
							connections.get(1).sendMessage("solve");
						}else{	//若錯的話，則傳送wrong的字樣給client
							connections.get(0).sendMessage("wrong");
							connections.get(1).sendMessage("wrong");
						}
						ansnum = 0;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		}
		public void sendIndex(int num){	//為傳送index給client之method
			this.writer.print(num);
			this.writer.flush();
		}
		public void sendMessage(String message){ //為傳送訊息給client之method
			this.writer.println(message);
			this.writer.flush();
		}
	}
	public static void main(String[] args) {
		Server server = new Server(8000);
		server.runForever();
	}
	
}