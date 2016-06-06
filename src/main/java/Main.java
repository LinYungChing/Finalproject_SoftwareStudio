package main.java;
import javax.swing.JFrame;

/**
* This class is the main class of the program.
* You can customize different window width and height for your program here. 
*
* @author  Richo
* @version 1.0
* @since   2016-04-26 
*/
@SuppressWarnings("serial")
public class Main extends JFrame{ // 此class本次沒有用到
	
	private final static int windowWidth = 1200, windowHeight = 670;
	
	public void start(){
		
		
		MainApplet applet = new MainApplet();
		applet.init();
		applet.start();
		applet.setFocusable(true);
	
		JFrame window = new JFrame("Star Wars Network Analysis");
		window.setContentPane(applet);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(windowWidth, windowHeight);
		window.setVisible(true);
		
	}
}
