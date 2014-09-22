package com.brashmonkey.spriter.tests.backend;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.brashmonkey.spriter.Data;
import com.brashmonkey.spriter.Drawer;
import com.brashmonkey.spriter.FileReference;
import com.brashmonkey.spriter.Loader;
import com.brashmonkey.spriter.Player;
import com.brashmonkey.spriter.SCMLReader;
import com.brashmonkey.spriter.Timeline.Key.Object;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Java2DTest extends JFrame{
	private static final long serialVersionUID = 1L;
	
	Player player;
	BufferedImageDrawer drawer;
	Loader<BufferedImage> loader;
	AffineTransform identity = new AffineTransform();
	
	public Java2DTest(){
                String xmlSCML = null;
                try {
                    xmlSCML = new String(Files.readAllBytes(Paths.get("SpriterTests/assets/monster/basic_002.scml")));
                } catch (IOException ex) {
                    Logger.getLogger(LwjglTest.class.getName()).log(Level.SEVERE, null, ex);
                }
		Data data = new SCMLReader(xmlSCML).getData();
		player = new Player(data.getEntity(0));
		player.setPosition(640, 360);
		this.loader = new Loader<BufferedImage>(data) {
			
			@Override
			protected BufferedImage loadResource(FileReference ref) {
				try {
					return ImageIO.read(new File(super.root+"/"+data.getFile(ref).name));
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		this.loader.load("SpriterTests/assets/monster");
		
		MainLoop loop = new MainLoop();
		loop.setDoubleBuffered(true);
		drawer = new BufferedImageDrawer(loader);
		
		super.getContentPane().add(loop);
		super.setSize(new Dimension(1280,720));
		super.setTitle("Java2D test");
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		super.setLocationRelativeTo(null);
		super.setVisible(true);
		
		new Thread(loop).start();
	}
	
	private class MainLoop extends JPanel implements Runnable{
		
		private static final long serialVersionUID = 1L;

		@Override
		public void run() {
			while(super.isVisible()){
				try{
					Thread.sleep(15);
				} catch(InterruptedException e){
					System.err.println("Thread got interrupted!");
				}
				
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						
						@Override
						public void run() {
							player.update();
							repaint();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		public void paintComponent(Graphics g){
			drawer.graphics = (Graphics2D)g;
			g.clearRect(0, 0, getWidth(), getHeight());
			if(player.getCurrentKey() != null){
				drawer.draw(player);
				drawer.graphics.setTransform(identity);
				drawer.graphics.scale(1, -1);
				drawer.graphics.translate(0, -getContentPane().getHeight());
				drawer.drawBones(player);
				drawer.drawBoxes(player);
			}
		}
		
	}
	
	private class BufferedImageDrawer extends Drawer<BufferedImage>{
		
		public Graphics2D graphics;
		
		public BufferedImageDrawer(Loader<BufferedImage> loader) {
			super(loader);
		}

		@Override
		public void setColor(float r, float g, float b, float a) {
			graphics.setColor(new Color(r,g,b,a));
		}
		
		@Override
		public void rectangle(float x, float y, float width, float height) {
			graphics.drawRect((int)x, (int)y, (int)width, (int)height);
		}
		
		@Override
		public void line(float x1, float y1, float x2, float y2) {
			graphics.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
		}
		
		@Override
		public void draw(Object object) {
			graphics.setTransform(identity);
			drawer.graphics.scale(1, -1);
			drawer.graphics.translate(0, -getContentPane().getHeight());			

			BufferedImage sprite = loader.get(object.ref);
			float newPivotX = sprite.getWidth() * object.pivot.x;
			float newX = object.position.x - newPivotX*Math.signum(object.scale.x);
			float newPivotY = sprite.getHeight() * object.pivot.y;
			float newY = object.position.y - newPivotY*Math.signum(object.scale.y);
			graphics.rotate(Math.toRadians(object.angle), object.position.x, object.position.y);
			int height = -(int)(sprite.getHeight()*object.scale.y);
			graphics.drawImage(sprite, (int)newX, (int)newY-height, (int)(sprite.getWidth()*object.scale.x), height, null);
		}
		
		@Override
		public void circle(float x, float y, float radius) {
			getGraphics().drawOval((int)(x-radius), (int)(y-radius), (int)radius, (int)radius);
		}
	}
	
	public static void main(String[] args){
		new Java2DTest();
	}

}
