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
	private String file = "src/main/resources/starwars-episode-1-interactions.json"; // ��J�nŪ�J��json�ɮ׸��|
	private String msg = "Star Wars 1" ;   // �]�m����������r  ��r�N��{�b�OŪ�Jstar wars �����@��
	private final static int width = 1200, height = 650;  // �]�m�����j�p 
	
	JSONObject data ;  //data �Ψ� loadJSONObject(���P��json�ɮ�) ;
	JSONArray nodes ,links;   // ��JSONArray �Ψ��x�sjson�ɮ׸̭������P�}�C 
	private int[] inside = new int[50];
	private int index;  
	private float angle,perangle,numOfcircle;  //�����k��j��ΤW���X�Ӫ��� �����C�Ӫ��󪺬۹j����
	private ArrayList<Character> characters;  // ���}�C�O�U json��nodes�}�C�����Ҧ����
	private ArrayList<Character> targets ;   //  ���}�C�O�Ujson��links�}�C�����Ҧ����
	private ControlP5 cp5;					// ���s
	
	public void setup() {				// �Ҧ��Ȫ���l��
		size(width, height);           // �]�m�����j�p
		characters = new ArrayList<Character>();   // �ŧi�}�C
		targets = new ArrayList<Character>();
		this.numOfcircle = 0;				// �̤@�}�l�k�䪺�j�餤�S������
		for(int i=0;i<50;i++) inside[i]=0;   // �̤@�}�l�Ҧ������󳣦b���� �Y �Ҧ���inside[]�� = 0 
		smooth();
		loadData();  //Ū�����
		cp5 = new ControlP5(this);
		cp5.addButton("buttonA").setLabel("ADD all").setPosition(8*width/10, 1*height/10).setSize(200, 50); // ��l�ƥ��[�����s
		cp5.addButton("buttonB")  // ��l�ƥ����M�������s
		.setLabel("CLEAR ALL")
		.setPosition(8*width/10, 8*height/10)
		.setSize(200, 50);
		Ani.init(this);  // initial�ʵe
	}

	public void draw() {   // ��class�|�@������  �@����s����
		if(Client.ready == 0){
			background(100,100,100) ;
			cp5.setVisible(false) ;
			fill(0); // �]�m�r���C��
			textSize(26);
			text("Please Fucking Waiting For Another Client", width/4, height/16);
		}
		else {
			cp5.setVisible(true);
			background(255) ;
			fill(0); // �]�m�r���C��
			textSize(26);
			text(msg, width/2, height/16);  // ��ܥX�r ����r�O�b�����̤��� �ΨӪ�ܲ{�b�Ostar war���ĴX��
			strokeWeight(1);  // �]�m�k��j�骺�u���ʲ�
			fill(255,255,255);  
			ellipse(800,300,450,450);  // �e�X���
			for (Character character:characters){   
				character.display();	// ��ܥX�C�Ӫ���
			}
			for(int i=0;i<50;i++){ // ��for�j��O�ΨӰ����k�䪺���󤬬۬O�_�ݭn�s�u          inside�}�C���C�@�Ӫ���]�L�@��
				if(inside[i]>0){   // �Yinside�}�C������>0 �N������b�k�䪺�j��W 
					for (int ii = 0 ;ii < links.size();ii++){   // ��json�������s�u��ƪ��}�C���]�L�@��  �o�ˤ~���|����|���u
						if(i == links.getJSONObject(ii).getInt("source")){   // �Y�������n�b�k��j��W �B�]�O�s�u��Ƥ���source���� �h�N��i�঳���|���s�u
							int target = links.getJSONObject(ii).getInt("target");	  // �P�_ ���s�u��� ��target�O���Ӫ���	
								if(inside[target]>0){   // �Y�������n�]�b�k�䪺�j��W �h�i�H�s�u
									int lineweight = links.getJSONObject(ii).getInt("value") ;  // �A�N�s�u��ƪ�value����X�� �Y��Ӫ��󪺬����{��
									strokeWeight(lineweight);  // �N�⪫�󪺬����{�ק令�⪫��u���ʲ�
									// �N�i�H�e�u�F
									line(characters.get(i).getX(),characters.get(i).getY(),characters.get(target).getX(),characters.get(target).getY());
								}
						}
					}
				}
			}
		}
	}
	public void mouseMoved(){    // ��class �Φb  ��ƹ������ʮ�  ��class�i�H�����ƹ����ʮy�� �Y�ƹ��b����W �h��ܥX���󪺦r
		int mousex = mouseX ;    // �O���U�ƹ����ʮɪ���U�y��
		int mousey = mouseY ;
		/*
		 * �����H�Ufor�j��
		 * ���F���D�ƹ��{�b�O�b���Ӫ���W
		 * �ڥ�for�j��]�L�Ҧ�������
		 * �A�N����xy���аO�U��
		 * �ûP���ɪ��ƹ��y�к�Z��
		 * �Y�Z����X��<�Ӫ��󪺹ϧΥb�|�j�p
		 * �h�i�H���D�ƹ����b�Ӫ���W
		 */
		for(int i = 0 ;i < characters.size();i++){  
			float circlex = characters.get(i).x ; // �O�U����xy�y��
			float circley = characters.get(i).y ;
			float departx = mousex - circlex ;   // ��X�ƹ��P���󪺶Z��
			float departy = mousey - circley ;
			float distance = departx*departx + departy*departy ;
			double ans  = Math.sqrt(distance);
			if(ans <= 20){    // ��X�Z����Y�Z���p�󪫥�ϧΥb�| 
				characters.get(i).show();   // �h�N�Ӫ��󪺦W����ܥX��
			}
			else {   // �Y���b�Ӫ���ϧΥb�|�d��   
				characters.get(i).notshow();  // �h������ܪ���W��
			}
		}
	}
	public void mousePressed() { // ��class�����P�W�zmouseMoved()  ��ƹ����U����� �Y���b����W �h�i�H�}�l��Ԫ��󲾰�
		/*
		 * �����H�Ufor�j��
		 * ���F���D�ƹ������b���Ӫ���W
		 * �ڥ�for�j��]�L�Ҧ�������
		 * �A�N����xy���аO�U��
		 * �ûP���ɪ��ƹ��y�к�Z��
		 * �Y�Z����X��<�Ӫ��󪺹ϧΥb�|�j�p
		 * �h�i�H���D�ƹ��������b������W
		 */
		int mousex = mouseX ;   //�O�U��U�ƹ���xy�y��
		int mousey = mouseY ;
		for(int i = 0 ;i < characters.size();i++){   
			float circlex = characters.get(i).x ;// �O�U����xy�y��
			float circley = characters.get(i).y ;
			float departx = mousex - circlex ;// ��X�ƹ��P���󪺶Z��
			float departy = mousey - circley ;
			float distance = departx*departx + departy*departy ;
			double ans  = Math.sqrt(distance);
			if(ans <= 20){   // ��X�Z����Y�Z���p�󪫥�ϧΥb�| 
				characters.get(i).setEnable(true);  // �hsetEnable(true) �Y �ƹ����b�����b����W
				index = i;
				break ;  // �Y���ƹ��w�g�b����W �h�i�Hbreak�� ���ΦA�P�q���U�Ӫ�����
			}
		}
        redraw();
    }
	public void mouseDragged(){    // �ƹ����U��쪺�L�{
		if(characters.get(index).enable){   // �Y���ƹ������b����W
			characters.get(index).setX(mouseX);   // �h�ƹ����ʨ���� �h�Ӫ��󪺮y�дN�|�ήɧ�s �H�۷ƹ��ܰ�
			characters.get(index).setY(mouseY);
		}
	}
	public void mouseReleased(){   // ������ƹ��񱼮�   �Ӫ��󦳨S���b �k�䪺�j���W
		if(characters.get(index).enable){  // �Y������O���b�Q���������A  �Y���b�ηƹ��쪺�L�{  �h�i�H�P�_�e����m�èϧ_�ө�b�k���W
			characters.get(index).setEnable(false);
			float dist_x = mouseX - 800;   // �p�� ������P�k��j�骺�Z��
			float dist_y = mouseY - 300;
			float dist = dist_x*dist_x + dist_y*dist_y;
			double ans = Math.sqrt(dist);
			if(ans<=225){    // ��Z���p��k��j��骺�b�|��   �h�N��Ӫ��󥿦b�Ӷ�餺
				if(inside[index]==0){    // �Y������O�q����Ԩ�k�䪺
					inside[index]++;   // �h�O���U������q���䲾�ʨ�k��
					numOfcircle++;   // �åB���k��j���W�������`��++
				}
			}else if(ans>225){     // �Y�Z���j��k��j��骺�b�|��  �h�N��Ӱ����}�j���
				if(inside[index]==1){   // �Y�쥻����O�b�k��j���W
					inside[index] = 0 ;  // �h���Ӫ��󲾰ʨ쥪��
					numOfcircle--;   // �B���k��j��骺�����`��-1
					int row = index%10 ;    // �åB�O���U�쥻����b���䪺��m
					int col = index/10 ; 	//�]������񪫥�ɤ@��col��10�Ӫ���
					int posx = 30 + col*60 ;  // �B�̥��W���������m�O 30,30 
					int posy = 30 + row*60 ;  // �B�C�Ӭ۶Z 60   �̦���k�i�H��X�쥻����m �Yposx  posy
					Ani.to(characters.get(index),(float)2,"x",posx);  // �A�ΰʵe�C�C�]�^�h ���ʮɶ���2��
					Ani.to(characters.get(index),(float)2,"y",posy);
					inside[index] = 0 ;
				}
			}
			angle = (float) (360.0/numOfcircle);   // ���F�N����i�H������b�k�䪺�j���W  ��360.0/�����
			perangle = (float) ((angle*Math.PI)/180);  // �ñN���Ʀr�ഫ��������
			int ct = 1;    // �Ӫ���O�ĴX�ө�b�k��j���W��
			for(int i=0;i<50;i++){   // ��for�]�L�Ҧ����� �Yinside[i]>0�h�Ӫ���|�b�k��j���W
				if(inside[i]>0){
					characters.get(i).setX(800+225*cos(ct*perangle));  // �b�Ψ��קY�b�|�Y�i�H���X���󥭧����t�b�j��骺�P���W
					characters.get(i).setY(300+225*sin(ct*perangle));
					ct++;	
				}
			}
			//System.out.println("angle: " +angle);
		}
	}
	public void keyPressed(KeyEvent e){  // �������U �Ʀr 1~7 ��  �i�H�̧ǧ令 star wars�� json 1~7 
		characters.clear();   // �n��json�� �n���N���e�Ҧs�U���Ҧ���������M��
		for(int i=0;i<50;i++){
			inside[i]=0;    // �ê�l��  �N�Ҧ����󪺦�m���]�m�b����
		}
		numOfcircle=0;   //�N�k�䪫��ƶq ��l�� = 0 
		index= 0;
		if(e.getKeyCode()==49){   // ����U�Ʀr1 �ɷ|load episode 1
			file = "src/main/resources/starwars-episode-1-interactions.json";
			msg = "Star Wars 1" ; 
			loadData() ; //���sload�ɮ�
		}
		if(e.getKeyCode()==50){// ����U�Ʀr2 �ɷ|load episode 2
			file = "src/main/resources/starwars-episode-2-interactions.json";
			msg = "Star Wars 2" ; 
			loadData() ;//���sload�ɮ�
		}
		if(e.getKeyCode()==51){// ����U�Ʀr3 �ɷ|load episode 3
			file = "src/main/resources/starwars-episode-3-interactions.json";
			msg = "Star Wars 3" ; 
			loadData() ;//���sload�ɮ�
		}
		if(e.getKeyCode()==52){// ����U��4 �ɷ|load episode 4
			file = "src/main/resources/starwars-episode-4-interactions.json";
			msg = "Star Wars 4" ; 
			loadData() ;//���sload�ɮ�
		}
		if(e.getKeyCode()==53){// ����U�Ʀr5 �ɷ|load episode 5
			file = "src/main/resources/starwars-episode-5-interactions.json";
			msg = "Star Wars 5" ; 
			loadData() ;//���sload�ɮ�
		}
		if(e.getKeyCode()==54){// ����U�Ʀr6 �ɷ|load episode 6
			file = "src/main/resources/starwars-episode-6-interactions.json";
			msg = "Star Wars 6" ; 
			loadData() ;//���sload�ɮ�
		}
		if(e.getKeyCode()==55){// ����U�Ʀr7 �ɷ|load episode 7
			file = "src/main/resources/starwars-episode-7-interactions.json";
			msg = "Star Wars 7" ; 
			loadData() ;//���sload�ɮ�
		}
		if(e.getKeyCode()!=49&&e.getKeyCode()!=50&&e.getKeyCode()!=51&&
				e.getKeyCode()!=52&&e.getKeyCode()!=53&&
				e.getKeyCode()!=54&&e.getKeyCode()!=55) {  // �]�wdefault�� ����U��L�ץ�� �h�|Ū�� episode1
			file = "src/main/resources/starwars-episode-1-interactions.json";
			loadData() ;//���sload�ɮ�
		}
	}
	private void loadData(){   // Ū����� 
		data = loadJSONObject(file) ;  // ���N�ɮ�load�iJSONObject��
		nodes = data.getJSONArray("nodes"); // �Nnodes�̭�����Ʀs�J�}�C��
		links = data.getJSONArray("links");	//�Nlinks�̭�����Ʀs�J�}�C��
		float x =30, y=30 ;   // �]�m�̤@�}�l�������m
		for (int i = 0; i<nodes.size();i++){   // �]�L�C�@�Ӫ���
			String name = nodes.getJSONObject(i).getString("name");  //�N�}�C����name�s�U��
			String color = nodes.getJSONObject(i).getString("colour"); // �N�}�C����colour�s�U��
			String realcolor = color.substring(1, 9) ;
			int hi = unhex(realcolor) ;  // �ഫ�C�⪺String to integer ����b�⥦���fill()��
			Character charac = new Character(this,x,y,hi,name) ;  // �s�W�@�Ӫ���
			characters.add(charac) ;  // �⪫��[�Jcharacters�}�C��
			y +=60 ;  // �C�񧹤@�Ӫ���  �U�@�Ӫ��󪺩�m��m�N�|�V�U60
			if(y >= 600){   // �Y�ֶW�X �����d��   �h���@�C��ܪ���
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
	public void buttonA(){   //���sA ���U���sA ��i�H��Ҧ������󳣥[�J�k��j���
		for (int i =0 ;i < characters.size();i++){
			inside[i] = 1 ;   // ���Ҧ�������q���䲾�ʨ�k��
		}
		numOfcircle = characters.size() ;  //�k�䪺����ƶq = �`����ƶq
		angle = (float) (360.0/numOfcircle);   // ��X�C�Ӫ��󪺬ۮ樤��
		perangle = (float) ((angle*Math.PI)/180);
		int ct = 1; // �{�b�n��ĴX�Ӫ���
		for(int i=0;i<50;i++){
			if(inside[i]>0){   // �Ψ��קY�b�|�Y�i�H���X���󥭧����t�b�j��骺�P���W
				characters.get(i).setX(800+225*cos(ct*perangle));
				characters.get(i).setY(300+225*sin(ct*perangle));
				ct++;	
			}
		}
	}
	public void buttonB(){  //���sB�Y�N�Ҧ��k���W�����󳣲��쥪��
		for(int i = 0 ;i < characters.size();i++){ // �ˬd�Ҧ�������O�_�b�k��
			if(inside[i]==1){  // �Y�b�k��
				inside[i] = 0 ;
				int row = i%10 ;   // �åB�O���U�쥻����b���䪺��m
				int col = i/10 ;//�]������񪫥�ɤ@��col��10�Ӫ���
				int posx = 30 + col*60 ;// �B�̥��W���������m�O 30,30 
				int posy = 30 + row*60 ;// �B�C�Ӭ۶Z 60   �̦���k�i�H��X�쥻����m �Yposx  posy
				Ani.to(characters.get(i),(float)1.5,"x",posx,Ani.QUAD_OUT); // �C�Ӫ���^�h���ɭԦA�[�J�ʵe
				Ani.to(characters.get(i),(float)1.5,"y",posy,Ani.QUAD_OUT);
			}
		}
		for (int i =0 ;i < characters.size();i++){
			inside[i] = 0 ; // �N�Ҧ������󳣩�쥪��
		}
		numOfcircle=0;  //�q�^�̤@�}�l�����A
		index= 0;
	}
}
