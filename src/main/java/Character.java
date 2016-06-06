package main.java;

import java.awt.Color;
import java.util.ArrayList;

import processing.core.PApplet;

/**
* This class is used to store states of the characters in the program.
* You will need to declare other variables depending on your implementation.
*/
public class Character {  // 此class可以代表顯示出的每一個小圈圈就是一個character,也就是一個object
	
	public MainApplet parent;   // 這邊宣告每一個物件所會用到及需要用到的變數
	public float x , y ;  // x,y記下每個物件當下的x座標以及y座標
	public boolean enable; // enable == 1 表示滑鼠按壓在此物件上
	public int hi ;   // hi 此物件的顏色
	private String name ;  // name是 此物件的名稱
	private int show = 0 ;  // show = 1 表示顯示出此物件的字,當滑鼠在此物件上時show才會=1 且會顯示出此物件的name 
	
	public Character(MainApplet parent,float x ,float y ,int hi,String name){  // 初始化每一個物件
		this.parent = parent;
		this.x = x ;
		this.y = y ;
		this.hi = hi ;
		this.name = name ;
	}
	public void setX(float x){  // 可以即時設定此物件的x座標
		this.x = x;
	}
	public void setY(float y){  // 可以即時設定此物件的y座標
		this.y = y;
	}
	public float getX(){   // 可以取得此物件的當下y座標
		return x ;
	}
	public float getY(){  // 可以取得此物件的當下y座標
		return y ;
	}
	public void setEnable(boolean x){ // 當滑鼠按下按鍵時 且 按下位置在物件上時 則此時此物件的setenable會是1
		this.enable = x;
	}
	public void show(){  // 當滑鼠移動到此物件上時 會讓此物件的show == 1   則display時會顯示出該物件的名稱
		this.show = 1 ;
	}
	public void notshow(){   // 當滑鼠移動時 且滑鼠當下沒有只到任何物件 則show=0 不會顯示出 物件名稱
		this.show = 0 ;
	}
	public void display(){   // 顯示出物件 要顯示的東西
		this.parent.fill(hi);   // 加入美個物件的顏色
		this.parent.ellipse(x,y,40,40); // 將每個物件都顯示成圓形
		if(show == 1){  // 當show == 1 時  則代表滑鼠在物件上 
			this.parent.text(name, x+40, y+40); // 則會顯示出物件的字
		}
	}
}
