package main.java;

import java.util.ArrayList;

import controlP5.ControlP5;
import de.looksgood.ani.Ani;
import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;

/**
* This class is for sketching outcome using Processing
* You can do major UI control and some visualization in this class.  
*/
@SuppressWarnings("serial")
public class MainApplet extends PApplet{
	private String path = "main/resources/";
	private String file = "src/main/resources/starwars-episode-1-interactions.json"; // 放入要讀入的json檔案路徑
	private String msg = "Star Wars 1" ;   // 設置視窗中的文字  文字代表現在是讀入star wars 的哪一集
	private final static int width = 1200, height = 650;  // 設置視窗大小 
	
	JSONObject data ;  //data 用來 loadJSONObject(不同的json檔案) ;
	JSONArray nodes ,links;   // 此JSONArray 用來儲存json檔案裡面的不同陣列 
	private int[] inside = new int[50];
	private int index;  
	private float angle,perangle,numOfcircle;  //紀錄右邊大圓形上有幾個物件 紀錄每個物件的相隔角度
	private ArrayList<Character> characters;  // 此陣列記下 json中nodes陣列中的所有資料
	private ArrayList<Character> targets ;   //  此陣列記下json中links陣列中的所有資料
	private ControlP5 cp5;					// 按鈕
	
	public void setup() {				// 所有值的初始化
		size(width, height);           // 設置視窗大小
		characters = new ArrayList<Character>();   // 宣告陣列
		targets = new ArrayList<Character>();
		this.numOfcircle = 0;				// 最一開始右邊的大圈中沒有物件
		for(int i=0;i<50;i++) inside[i]=0;   // 最一開始所有的物件都在左邊 即 所有的inside[]都 = 0 
		smooth();
		loadData();  //讀取資料
		cp5 = new ControlP5(this);
		cp5.addButton("buttonA").setLabel("ADD all").setPosition(8*width/10, 1*height/10).setSize(200, 50); // 初始化全加的按鈕
		cp5.addButton("buttonB")  // 初始化全部清除的按鈕
		.setLabel("CLEAR ALL")
		.setPosition(8*width/10, 8*height/10)
		.setSize(200, 50);
		Ani.init(this);  // initial動畫
	}

	public void draw() {   // 此class會一直執行  一直刷新視窗
		if(Client.ready == 0){
			background(100,100,100) ;
			cp5.setVisible(false) ;
			fill(0); // 設置字的顏色
			textSize(26);
			text("Please Fucking Waiting For Another Client", width/4, height/16);
		}
		else {
			cp5.setVisible(true);
			background(255) ;
			fill(0); // 設置字的顏色
			textSize(26);
			text(msg, width/2, height/16);  // 顯示出字 此文字是在視窗最中間 用來表示現在是star war的第幾集
			strokeWeight(1);  // 設置右邊大圈的線的粗細
			fill(255,255,255);  
			ellipse(800,300,450,450);  // 畫出圓圈
			for (Character character:characters){   
				character.display();	// 顯示出每個物件
			}
			for(int i=0;i<50;i++){ // 此for迴圈是用來偵測右邊的物件互相是否需要連線          inside陣列中每一個物件跑過一次
				if(inside[i]>0){   // 若inside陣列中的值>0 代表此物件在右邊的大圈上 
					for (int ii = 0 ;ii < links.size();ii++){   // 把json中紀錄連線資料的陣列都跑過一次  這樣才不會有遺漏的線
						if(i == links.getJSONObject(ii).getInt("source")){   // 若此物件剛好在右邊大圈上 且也是連線資料中的source的話 則代表可能有機會有連線
							int target = links.getJSONObject(ii).getInt("target");	  // 判斷 此連線資料 的target是哪個物件	
								if(inside[target]>0){   // 若此物件剛好也在右邊的大圈上 則可以連線
									int lineweight = links.getJSONObject(ii).getInt("value") ;  // 再將連線資料的value直抓出來 即兩個物件的相關程度
									strokeWeight(lineweight);  // 將兩物件的相關程度改成兩物件線的粗細
									// 就可以畫線了
									line(characters.get(i).getX(),characters.get(i).getY(),characters.get(target).getX(),characters.get(target).getY());
								}
						}
					}
				}
			}
		}
	}
	public void mouseMoved(){    // 此class 用在  當滑鼠有移動時  此class可以偵測滑鼠移動座標 若滑鼠在物件上 則顯示出物件的字
		int mousex = mouseX ;    // 記錄下滑鼠移動時的當下座標
		int mousey = mouseY ;
		/*
		 * 解釋以下for迴圈
		 * 為了知道滑鼠現在是在哪個物件上
		 * 我用for迴圈跑過所有的物件
		 * 再將物件的xy做標記下來
		 * 並與此時的滑鼠座標算距離
		 * 若距離算出來<該物件的圖形半徑大小
		 * 則可以知道滑鼠正在該物件上
		 */
		for(int i = 0 ;i < characters.size();i++){  
			float circlex = characters.get(i).x ; // 記下物件的xy座標
			float circley = characters.get(i).y ;
			float departx = mousex - circlex ;   // 算出滑鼠與物件的距離
			float departy = mousey - circley ;
			float distance = departx*departx + departy*departy ;
			double ans  = Math.sqrt(distance);
			if(ans <= 20){    // 算出距離後若距離小於物件圖形半徑 
				characters.get(i).show();   // 則將該物件的名稱顯示出來
			}
			else {   // 若不在該物件圖形半徑範圍內   
				characters.get(i).notshow();  // 則不用顯示物件名稱
			}
		}
	}
	public void mousePressed() { // 此class概念同上述mouseMoved()  當滑鼠按下按鍵時 若有在物件上 則可以開始拖拉物件移動
		/*
		 * 解釋以下for迴圈
		 * 為了知道滑鼠按壓在哪個物件上
		 * 我用for迴圈跑過所有的物件
		 * 再將物件的xy做標記下來
		 * 並與此時的滑鼠座標算距離
		 * 若距離算出來<該物件的圖形半徑大小
		 * 則可以知道滑鼠正按壓在此物件上
		 */
		int mousex = mouseX ;   //記下當下滑鼠的xy座標
		int mousey = mouseY ;
		for(int i = 0 ;i < characters.size();i++){   
			float circlex = characters.get(i).x ;// 記下物件的xy座標
			float circley = characters.get(i).y ;
			float departx = mousex - circlex ;// 算出滑鼠與物件的距離
			float departy = mousey - circley ;
			float distance = departx*departx + departy*departy ;
			double ans  = Math.sqrt(distance);
			if(ans <= 20){   // 算出距離後若距離小於物件圖形半徑 
				characters.get(i).setEnable(true);  // 則setEnable(true) 即 滑鼠正在按壓在物件上
				index = i;
				break ;  // 若找到滑鼠已經在物件上 則可以break掉 不用再判段接下來的物件
			}
		}
        redraw();
    }
	public void mouseDragged(){    // 滑鼠按下後拖的過程
		if(characters.get(index).enable){   // 若此滑鼠按壓在物件上
			characters.get(index).setX(mouseX);   // 則滑鼠移動到哪裡 則該物件的座標就會及時更新 隨著滑鼠變動
			characters.get(index).setY(mouseY);
		}
	}
	public void mouseReleased(){   // 偵測當滑鼠放掉時   該物件有沒有在 右邊的大圓圈上
		if(characters.get(index).enable){  // 若此物件是正在被按壓的狀態  即正在用滑鼠拖的過程  則可以判斷牠的位置並使否該放在右邊圈上
			characters.get(index).setEnable(false);
			float dist_x = mouseX - 800;   // 計算 此物件與右邊大園的距離
			float dist_y = mouseY - 300;
			float dist = dist_x*dist_x + dist_y*dist_y;
			double ans = Math.sqrt(dist);
			if(ans<=225){    // 當距離小於右邊大圓圈的半徑時   則代表該物件正在該圓圈內
				if(inside[index]==0){    // 若此物件是從左邊拉到右邊的
					inside[index]++;   // 則記錄下此物件從左邊移動到右邊
					numOfcircle++;   // 並且讓右邊大圓圈上的物件總數++
				}
			}else if(ans>225){     // 若距離大於右邊大圓圈的半徑時  則代表該圈離開大圓圈
				if(inside[index]==1){   // 若原本物件是在右邊大圓圈上
					inside[index] = 0 ;  // 則讓該物件移動到左邊
					numOfcircle--;   // 且讓右邊大圓圈的物件總數-1
					int row = index%10 ;    // 並且記錄下原本物件在左邊的位置
					int col = index/10 ; 	//因為左邊放物件時一個col有10個物件
					int posx = 30 + col*60 ;  // 且最左上角的物件位置是 30,30 
					int posy = 30 + row*60 ;  // 且每個相距 60   依此算法可以算出原本的位置 即posx  posy
					Ani.to(characters.get(index),(float)2,"x",posx);  // 再用動畫慢慢跑回去 移動時間為2秒
					Ani.to(characters.get(index),(float)2,"y",posy);
					inside[index] = 0 ;
				}
			}
			angle = (float) (360.0/numOfcircle);   // 為了將物件可以平均放在右邊的大圓圈上  讓360.0/物件數
			perangle = (float) ((angle*Math.PI)/180);  // 並將此數字轉換成為角度
			int ct = 1;    // 該物件是第幾個放在右邊大圓圈上的
			for(int i=0;i<50;i++){   // 用for跑過所有物件 若inside[i]>0則該物件會在右邊大圓圈上
				if(inside[i]>0){
					characters.get(i).setX(800+225*cos(ct*perangle));  // 在用角度即半徑即可以做出物件平均分配在大圓圈的周長上
					characters.get(i).setY(300+225*sin(ct*perangle));
					ct++;	
				}
			}
			//System.out.println("angle: " +angle);
		}
	}
	public void keyPressed(KeyEvent e){  // 當按鍵按下 數字 1~7 時  可以依序改成 star wars的 json 1~7 
		characters.clear();   // 要換json時 要先將先前所存下的所有物件全部清空
		for(int i=0;i<50;i++){
			inside[i]=0;    // 並初始化  將所有物件的位置都設置在左邊
		}
		numOfcircle=0;   //將右邊物件數量 初始化 = 0 
		index= 0;
		if(e.getKeyCode()==49){   // 當按下數字1 時會load episode 1
			file = "src/main/resources/starwars-episode-1-interactions.json";
			msg = "Star Wars 1" ; 
			loadData() ; //重新load檔案
		}
		if(e.getKeyCode()==50){// 當按下數字2 時會load episode 2
			file = "src/main/resources/starwars-episode-2-interactions.json";
			msg = "Star Wars 2" ; 
			loadData() ;//重新load檔案
		}
		if(e.getKeyCode()==51){// 當按下數字3 時會load episode 3
			file = "src/main/resources/starwars-episode-3-interactions.json";
			msg = "Star Wars 3" ; 
			loadData() ;//重新load檔案
		}
		if(e.getKeyCode()==52){// 當按下數4 時會load episode 4
			file = "src/main/resources/starwars-episode-4-interactions.json";
			msg = "Star Wars 4" ; 
			loadData() ;//重新load檔案
		}
		if(e.getKeyCode()==53){// 當按下數字5 時會load episode 5
			file = "src/main/resources/starwars-episode-5-interactions.json";
			msg = "Star Wars 5" ; 
			loadData() ;//重新load檔案
		}
		if(e.getKeyCode()==54){// 當按下數字6 時會load episode 6
			file = "src/main/resources/starwars-episode-6-interactions.json";
			msg = "Star Wars 6" ; 
			loadData() ;//重新load檔案
		}
		if(e.getKeyCode()==55){// 當按下數字7 時會load episode 7
			file = "src/main/resources/starwars-episode-7-interactions.json";
			msg = "Star Wars 7" ; 
			loadData() ;//重新load檔案
		}
		if(e.getKeyCode()!=49&&e.getKeyCode()!=50&&e.getKeyCode()!=51&&
				e.getKeyCode()!=52&&e.getKeyCode()!=53&&
				e.getKeyCode()!=54&&e.getKeyCode()!=55) {  // 設定default值 當按下其他案件時 則會讀取 episode1
			file = "src/main/resources/starwars-episode-1-interactions.json";
			loadData() ;//重新load檔案
		}
	}
	private void loadData(){   // 讀取資料 
		data = loadJSONObject(file) ;  // 先將檔案load進JSONObject中
		nodes = data.getJSONArray("nodes"); // 將nodes裡面的資料存入陣列中
		links = data.getJSONArray("links");	//將links裡面的資料存入陣列中
		float x =30, y=30 ;   // 設置最一開始的物件位置
		for (int i = 0; i<nodes.size();i++){   // 跑過每一個物件
			String name = nodes.getJSONObject(i).getString("name");  //將陣列中的name存下來
			String color = nodes.getJSONObject(i).getString("colour"); // 將陣列中的colour存下來
			String realcolor = color.substring(1, 9) ;
			int hi = unhex(realcolor) ;  // 轉換顏色的String to integer 之後在把它放到fill()中
			Character charac = new Character(this,x,y,hi,name) ;  // 新增一個物件
			characters.add(charac) ;  // 把物件加入characters陣列中
			y +=60 ;  // 每放完一個物件  下一個物件的放置位置就會向下60
			if(y >= 600){   // 若快超出 視窗範圍   則換一列顯示物件
				y = 30 ; x+=60 ;
			}
		}
	}
	public void addTarget(Character target){
		this.targets.add(target);
	}
	
	public ArrayList<Character> getTargets(){
		return this.targets;
	}
	public void buttonA(){   //按鈕A 按下按鈕A 後可以把所有的物件都加入右邊大圓圈
		for (int i =0 ;i < characters.size();i++){
			inside[i] = 1 ;   // 讓所有的物件從左邊移動到右邊
		}
		numOfcircle = characters.size() ;  //右邊的物件數量 = 總物件數量
		angle = (float) (360.0/numOfcircle);   // 算出每個物件的相格角度
		perangle = (float) ((angle*Math.PI)/180);
		int ct = 1; // 現在要放第幾個物件
		for(int i=0;i<50;i++){
			if(inside[i]>0){   // 用角度即半徑即可以做出物件平均分配在大圓圈的周長上
				characters.get(i).setX(800+225*cos(ct*perangle));
				characters.get(i).setY(300+225*sin(ct*perangle));
				ct++;	
			}
		}
	}
	public void buttonB(){  //按鈕B即將所有右邊圈上的物件都移到左邊
		for(int i = 0 ;i < characters.size();i++){ // 檢查所有的物件是否在右邊
			if(inside[i]==1){  // 若在右邊
				inside[i] = 0 ;
				int row = i%10 ;   // 並且記錄下原本物件在左邊的位置
				int col = i/10 ;//因為左邊放物件時一個col有10個物件
				int posx = 30 + col*60 ;// 且最左上角的物件位置是 30,30 
				int posy = 30 + row*60 ;// 且每個相距 60   依此算法可以算出原本的位置 即posx  posy
				Ani.to(characters.get(i),(float)1.5,"x",posx,Ani.QUAD_OUT); // 每個物件回去的時候再加入動畫
				Ani.to(characters.get(i),(float)1.5,"y",posy,Ani.QUAD_OUT);
			}
		}
		for (int i =0 ;i < characters.size();i++){
			inside[i] = 0 ; // 將所有的物件都放到左邊
		}
		numOfcircle=0;  //從回最一開始的狀態
		index= 0;
	}
}
