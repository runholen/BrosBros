package brosbros;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.InetAddress;

import javax.imageio.ImageIO;
import javax.swing.*;

public class GameFrame extends JFrame implements WindowListener{ //This class paints to the screen.

	GameLoop gameLoop;
	FrodePanel frodePanel;
	DebugPanel debugPanel;
	Heart hearth = new Heart();
	static int width = 1216; //1180;
	static int height = 944; //940;
	static int screenWidth = width;
	static int screenHeight = height;
	BufferedImage buffer = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
	
	public GameFrame(GameLoop gameLoop) throws Exception{
		super("BrosBros v0.1.2");
		BufferedImage img = ImageIO.read(getClass().getResourceAsStream("/resources/"+"icon.png"));
		setIconImage(img.getScaledInstance(64, 64, BufferedImage.SCALE_DEFAULT));
		this.gameLoop = gameLoop;
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(frodePanel = new FrodePanel(),BorderLayout.CENTER);
		p.add(debugPanel = new DebugPanel(),BorderLayout.EAST);
		if (isDeveloperPc() == false){
			debugPanel.setVisible(false);
		}
		setContentPane(p);
		int actualHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		//actualHeight = 800; //For debugging
		if (actualHeight <= 880){
			screenWidth = 927;
			screenHeight = 720;
		}
		else if (actualHeight <= 1000){
			screenWidth = 1095;
			screenHeight = 850;
		}
		frodePanel.setPreferredSize(new Dimension(screenWidth,screenHeight));
		pack();
		setVisible(true);
	}
	
	public static boolean isDeveloperPc() {
		try	{
		    String name = InetAddress.getLocalHost().getHostName();
		    System.out.println(name);
		    if (name.equalsIgnoreCase("RUNARLAP")) return true;
		    if (name.equalsIgnoreCase("FRODEMASKIN")) return true;
		}
		catch (Exception ex)
		{
		    System.out.println("Hostname can not be resolved");
		}
		return false;
	}

	public class FrodePanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener{
		Collition clicked = null;
		PlayerObject dragging = null;
		
		public FrodePanel(){
			setFocusable(true);
			addKeyListener(this);
			addMouseListener(this);
			addMouseMotionListener(this);
		}
		public void paint(Graphics g){ //Each time this is called, it redraws the whole screen.
			updateBuffer();
			g.drawImage(buffer,0,0,screenWidth,screenHeight,null);
		}
		private void updateBuffer(){
			Graphics2D g = buffer.createGraphics();
			if (gameLoop.level.background != null){ //First, the background/level
				g.drawImage(gameLoop.level.background, 0, 0, null);
			}
			else{
				g.setColor(gameLoop.level.backgroundColor);
				g.fillRect(0,0,width,height);
			}
			if (gameLoop.level instanceof Intro){
				((Intro)gameLoop.level).paintIntro(g);
				return;
			}
			if (debugPanel.overlay.isSelected()){ //Debug
				for (int x = 0; x < gameLoop.level.levelData.length; x++){
					for (int y = 0; y < gameLoop.level.levelData[0].length; y++){
						byte b = gameLoop.level.levelData[x][y];
						if (b == 1){
							g.setColor(Color.red);
							g.fillRect(x*2, y*2, 2, 2);
						}
					}	
				}
			}
			for (int t = 0; t < gameLoop.players.length; t++){ //For each player (Frode, Fritjof, Liv)
				PlayerObject player = gameLoop.players[t];
				if (player.useFlipped){ //Player facing left
					if (gameLoop.level.isUnderWater) g.drawImage(player.swimming, player.x, player.y, null);
					else if (gameLoop.level.gravitySwapped) g.drawImage(player.upsideDown, player.x, player.y, null);
					else g.drawImage(player.flipped,player.x,player.y,null);
				}
				else{ //The normal player image, facing right
					if (gameLoop.level.isUnderWater) g.drawImage(player.swimmingFlipped, player.x, player.y, null);
					else if (gameLoop.level.gravitySwapped) g.drawImage(player.upsideDownFlipped, player.x, player.y, null);
					else g.drawImage(player.image,player.x,player.y,null);
				}
				for (int lives = 0; lives < player.lives; lives++){ //Paint the number of lives left
					hearth.x = lives*16;
					hearth.y = 230+t*16;
					g.drawImage(hearth.image,hearth.x,hearth.y,null);
				}
				if (debugPanel.viewCollitions.isSelected()){ //Debug
					//paintCollition(g,player.previousCollition,player,Color.red);
					paintCollition(g,player.ghostCollition,player,new Color(255,255,0,150));
				}
				for (int tt = 0; tt < gameLoop.players.length; tt++){
					if (t == tt) continue;
					PlayerObject other = gameLoop.players[tt];
					if (player.isDying()){ 
						if (gameLoop.level.levelNr == 35) g.setColor(Color.white);
						else g.setColor(Color.black);
						g.setFont(g.getFont().deriveFont(Font.BOLD,(float)14));
						g.drawString("AAAH!", player.x+player.getWidth()/2-10, player.y-5);
					}
					if (player.collidesWith(other)){ //If players are touching, the say something.
						g.setColor(Color.black);
						g.setFont(g.getFont().deriveFont(Font.BOLD,(float)14));
						String s = "!?";
						if (gameLoop.level.isUnderWater) s = "(Blub)";
						g.drawString(s, player.x+player.getWidth()/2-3, player.y-5);
					}
					if (gameLoop.level.numberofkeys > 0 && player.enters(null, gameLoop.door)){
						g.setColor(Color.black);
						g.setFont(g.getFont().deriveFont(Font.BOLD,(float)14));
						g.drawString("The door is locked!", player.x+player.getWidth()/2-20, player.y-15);
						g.drawString("We need a key!", player.x+player.getWidth()/2-20, player.y-5);
					}
					if (debugPanel.viewJumps.isSelected() && player.jumping){ //Debug
						g.setColor(Color.black);
						g.setFont(g.getFont().deriveFont(Font.BOLD,(float)14));
						g.drawString("Jump", player.x+player.getWidth()/2-13, player.y-5);
					}
				}
			}
			if (clicked != null && debugPanel.viewCollitions.isSelected()){
				paintCollition(g,clicked,new GameObject(),new Color(255,255,0,150));
			}
			if (gameLoop.level.levelNr == 5){
				g.drawImage(gameLoop.door.alternateImage1,gameLoop.door.x,gameLoop.door.y,null);				
			}
			else if (gameLoop.level.levelNr == 10){
				g.drawImage(gameLoop.door.alternateImage2,gameLoop.door.x,gameLoop.door.y,null);				
			}
			else if (gameLoop.level.levelNr == 25){
				g.drawImage(gameLoop.door.alternateImage3,gameLoop.door.x,gameLoop.door.y,null);				
			}
			else if (gameLoop.level.levelNr == 30){
				g.drawImage(gameLoop.door.alternateImage4,gameLoop.door.x,gameLoop.door.y,null);				
			}
			else if (gameLoop.level.levelNr == 34){
				g.drawImage(gameLoop.door.alternateImage5,gameLoop.door.x,gameLoop.door.y,null);				
			}
			else{
				g.drawImage(gameLoop.door.image,gameLoop.door.x,gameLoop.door.y,null);
			}
			if (gameLoop.level.numberofkeys == 1){
				if (gameLoop.level.levelNr==16) g.drawImage(gameLoop.blackKey.image,gameLoop.blackKey.x,gameLoop.blackKey.y,null);
				else g.drawImage(gameLoop.yellowKey.image,gameLoop.yellowKey.x,gameLoop.yellowKey.y,null);
			}
			for (BossObject boss : gameLoop.bosses) {
				if (boss.useFlipped){
					g.drawImage(boss.flipped,boss.x,boss.y,null);
				}
				else{
					g.drawImage(boss.image,boss.x,boss.y,null);
				}
			}
			if (gameLoop.level.portalPair1IsShowing){
				g.drawImage(gameLoop.portal1.image,gameLoop.portal1.x,gameLoop.portal1.y,null);
				g.drawImage(gameLoop.portal2.flipped,gameLoop.portal2.x,gameLoop.portal2.y,null);
			}
			if (gameLoop.level.portalPair2IsShowing){
				g.drawImage(gameLoop.portal3.image, gameLoop.portal3.x, gameLoop.portal3.y,null);
				g.drawImage(gameLoop.portal4.flipped, gameLoop.portal4.x, gameLoop.portal4.y,null);
			}
			if (gameLoop.nextLevelCounter > 0){ //Level complete animation
				g.setFont(g.getFont().deriveFont((float)gameLoop.nextLevelCounter));
				g.setColor(Color.blue);
				if (gameLoop.level.levelNr == 10){
					g.drawString("Ejected",GameFrame.width/2-gameLoop.nextLevelCounter*2,GameFrame.height/2-gameLoop.nextLevelCounter/2);
				}
				else{
					g.drawString("Level Complete",GameFrame.width/2-gameLoop.nextLevelCounter*2,GameFrame.height/2-gameLoop.nextLevelCounter/2);
				}
			}
			if (gameLoop.level.maySwapGravity) {
				if (gameLoop.level.gravitySwapped) {
					g.setColor(Color.black);
					g.drawString("SWAPPED", 20, 20);
				}
				else {
					g.setColor(Color.black);
					g.drawString(""+gameLoop.level.gravitySwapCounter, 20, 20);
				}
			}
			for (Bullet bullet : gameLoop.bullets) {
				g.drawImage(bullet.image,bullet.x,bullet.y,null);
				//g.setColor(Color.blue);
				//g.drawString(bullet.x+","+bullet.y, bullet.x, bullet.y);
			}
			if (gameLoop.pickupHeart.x > 0) {
				g.drawImage(gameLoop.pickupHeart.image, gameLoop.pickupHeart.x, gameLoop.pickupHeart.y, null);
			}
		}
		//For debugging
		private void paintCollition(Graphics g, Collition c, GameObject player, Color color) {
			if (c == null) return;
			g.setColor(color);
			if (c.bottom) g.setColor(color); else g.setColor(new Color(0,255,0,150));
			g.fillRect(c.x,c.y+55,player.getWidth(),5);
			if (c.top) g.setColor(color); else g.setColor(new Color(0,255,0,150));
			g.fillRect(c.x,c.y,player.getWidth(),5);
			if (c.left) g.setColor(color); else g.setColor(new Color(0,255,0,150));
			g.fillRect(c.x,c.y,5,player.getHeight());
			if (c.right) g.setColor(color); else g.setColor(new Color(0,255,0,150));
			g.fillRect(c.x+35,c.y,5,player.getHeight());
		}
		@Override
		public void keyTyped(KeyEvent e) {
		}
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_SPACE && gameLoop.level.levelNr == 0){
				gameLoop.nextLevel();
			}
			//System.out.println(e.getKeyCode()+" pressed");
			for (PlayerObject player : gameLoop.players){
				player.checkInput(e.getKeyCode(), true);
			}
			if (e.getKeyCode() == KeyEvent.VK_L && gameLoop.level.levelNr == 0 && new File("brosbros.save").exists()) {
				try {
					BufferedReader in = new BufferedReader(new FileReader("brosbros.save"));
					String s = in.readLine();
					in.close();
					s = s.substring(2);
					String[] el = s.split(",");
					gameLoop.setLoadData(Integer.parseInt(el[0]), Integer.parseInt(el[1]), Integer.parseInt(el[2]), Integer.parseInt(el[3]));
				}catch(Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(this, "Something went wrong loading save-file");
				}
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {
			//System.out.println(e.getKeyCode()+" released");
			for (PlayerObject player : gameLoop.players){
				player.checkInput(e.getKeyCode(), false);
			}
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			requestFocusInWindow();
		}
		@Override
		public void mousePressed(MouseEvent e) {
			int x = e.getX()*width/screenWidth;
			int y = e.getY()*height/screenHeight;
			if (debugPanel.viewCollitions.isSelected()){
				clicked = gameLoop.collidesBackground(new GameObject(), x, y, true);
			}
			if (debugPanel.isVisible()){
				for (PlayerObject player : gameLoop.players){
					if (new GameObject(x,y,1,1).collidesWith(player)){
						dragging = player;
					}
				}
			}
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			dragging = null;
		}
		@Override
		public void mouseEntered(MouseEvent e) {
		}
		@Override
		public void mouseExited(MouseEvent e) {
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			if (dragging != null){
				int x = e.getX()*width/screenWidth;
				int y = e.getY()*height/screenHeight;
				dragging.x = x-dragging.getWidth()/2;
				dragging.y = y-dragging.getHeight()/2;
			}
		}
		@Override
		public void mouseMoved(MouseEvent e) {
		}
	}
	
	public class DebugPanel extends JPanel implements ActionListener{
		JButton next = new JButton("Neste");
		JButton previous = new JButton("Forrige");
		JButton last = new JButton("Siste");
		JCheckBox overlay = new JCheckBox("Vis bakgrunn",false);
		JCheckBox viewCollitions = new JCheckBox("Vis kollisjoner",false);
		JCheckBox viewJumps = new JCheckBox("Vis hopp",false);
		public DebugPanel(){
			setLayout(new BorderLayout());
			JPanel north = new JPanel();
			north.setLayout(new GridLayout(7,1));
			north.add(next);
			north.add(previous);
			north.add(last);
			north.add(new JLabel());
			north.add(overlay);
			north.add(viewCollitions);
			north.add(viewJumps);
			add(north,BorderLayout.NORTH);
			add(new JLabel(),BorderLayout.CENTER);
			next.addActionListener(this);
			previous.addActionListener(this);
			last.addActionListener(this);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == next){
				gameLoop.nextLevel();
			}
			else if (e.getSource() == previous){
				gameLoop.previousLevel();
			}
			else if (e.getSource() == last){
				gameLoop.lastLevel();
			}
		}
	}
	
	public static BufferedImage scale(BufferedImage img){
		BufferedImage bi = new BufferedImage(2 * img.getWidth(null), 2 * img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D grph = (Graphics2D) bi.getGraphics();
		grph.scale(2, 2);
		// everything drawn with grph from now on will get scaled.
		grph.drawImage(img, 0, 0, null);
		grph.dispose();
		return bi;
	}

	public static BufferedImage flip(BufferedImage image) {
		 AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		 tx.translate(-image.getWidth(null), 0);
		 AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		 return op.filter(image, null);
	}
	
	public static BufferedImage rotateClockwise90(BufferedImage src) {
	    int width = src.getWidth();
	    int height = src.getHeight();
	    BufferedImage dest = new BufferedImage(height, width, src.getType());
	    Graphics2D graphics2D = dest.createGraphics();
	    graphics2D.translate((height - width) / 2, (height - width) / 2);
	    graphics2D.rotate(Math.PI / 2, height / 2, width / 2);
	    graphics2D.drawRenderedImage(src, null);
	    return dest;
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}
	@Override
	public void windowClosing(WindowEvent e) {
		if (gameLoop.level.levelNr <= 0 || gameLoop.level instanceof Intro) System.exit(0);
		else {
			int ok = JOptionPane.showConfirmDialog(this, "Do you want to save?");
			if (ok == JOptionPane.NO_OPTION) System.exit(0);
			if (ok == JOptionPane.OK_OPTION) {
				try {
					File f = new File("brosbros.save");
					BufferedWriter out = new BufferedWriter(new FileWriter(f));
					out.write("SV"+gameLoop.level.levelNr+","+gameLoop.player1.lives+","+gameLoop.player2.lives+","+gameLoop.player3.lives);
					out.flush();
					out.close();
				}catch(Exception ex) {
					ex.printStackTrace();
				}finally {
					System.exit(0);
				}
			}
		}
	}
	@Override
	public void windowClosed(WindowEvent e) {
	}
	@Override
	public void windowIconified(WindowEvent e) {
	}
	@Override
	public void windowDeiconified(WindowEvent e) {
	}
	@Override
	public void windowActivated(WindowEvent e) {	
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
	}
}
