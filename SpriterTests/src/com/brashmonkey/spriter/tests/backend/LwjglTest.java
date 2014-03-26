package com.brashmonkey.spriter.tests.backend;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.io.IOException;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.ResourceLoader;

import com.brashmonkey.spriter.Drawer;
import com.brashmonkey.spriter.FileReference;
import com.brashmonkey.spriter.Loader;
import com.brashmonkey.spriter.Player;
import com.brashmonkey.spriter.SCMLReader;
import com.brashmonkey.spriter.Data;
import com.brashmonkey.spriter.Timeline.Key.Object;


public class LwjglTest {

	private Loader<Texture> loader;
	private Drawer<Texture> drawer;
	private Player player;
	
	public LwjglTest(){
		Data data = new SCMLReader("assets/monster/basic_002.scml").getData();
		player = new Player(data.getEntity(0));
		player.root.position.set(640, 360);
		loader = new Loader<Texture>(data){

			@Override
			protected Texture loadResource(FileReference ref) {
				try {
					return org.newdawn.slick.opengl.TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(super.root+"/"+data.getFile(ref).name));
				} catch (IOException e) {
					System.out.println("Failed to create texture "+super.root+"/"+data.getFile(ref).name);
					e.printStackTrace();
					return null;
				}
			}
			
		};
		drawer = new Drawer<Texture>(loader){

			@Override
			public void setColor(float r, float g, float b, float a) {
			}

			@Override
			public void line(float x1, float y1, float x2, float y2) {
			}

			@Override
			public void rectangle(float x, float y, float width, float height) {
			}

			@Override
			public void circle(float x, float y, float radius) {
			}

			@Override
			public void draw(Object object) {
				Texture texture = loader.get(object.ref);
				glLoadIdentity();
				glTranslatef(object.position.x, object.position.y, 0);
				glRotatef(object.angle, 0f, 0f, 1f);

				float original_width = texture.getTextureWidth();
				float original_height = texture.getTextureHeight();

				float textureDown = 0;
				float textureUp = textureDown + (1 / (original_height / texture.getImageHeight()));
				float textureLeft = 0;
				float textureRight = textureLeft + (1 / (original_width / texture.getImageWidth()));

				texture.bind();

				glBegin(GL_QUADS);

				glColor4f(1.0f, 1.0f, 1.0f, object.alpha);
				glTexCoord2f(textureLeft, textureUp); // Upper left
				glVertex2f(-((texture.getImageWidth()*object.scale.x) * object.pivot.x), -(((texture.getImageHeight()*object.scale.y) * object.pivot.y)));

				glColor4f(1.0f, 1.0f, 1.0f, object.alpha);
				glTexCoord2f(textureRight, textureUp); // Upper right
				glVertex2f((-((texture.getImageWidth()*object.scale.x) * object.pivot.x)) + (texture.getImageWidth()*object.scale.x),
						(-((texture.getImageHeight()*object.scale.y) * object.pivot.y)));

				glColor4f(1.0f, 1.0f, 1.0f, object.alpha);
				glTexCoord2f(textureRight, textureDown); // Lower right
				glVertex2f((-((texture.getImageWidth()*object.scale.x)* object.pivot.x)) + (texture.getImageWidth()*object.scale.x),
						(-((texture.getImageHeight()*object.scale.y)* object.pivot.y)) + (texture.getImageHeight()*object.scale.y));

				glColor4f(1.0f, 1.0f, 1.0f, object.alpha);
				glTexCoord2f(textureLeft, textureDown); // Lower left
				glVertex2f(-((texture.getImageWidth()*object.scale.x)* object.pivot.x),
						(-((texture.getImageHeight()*object.scale.y)* object.pivot.y)) + (texture.getImageHeight()*object.scale.y));

				glEnd();
			}
			
		};
		
		this.start();
	}
	
	public void draw(){
		player.update();
		drawer.draw(player);
	}
	

	
	/** time at last frame */
	private long lastFrame;
	/** frames per second */
	private int fps;
	/** last fps time */
	private long lastFPS;
	
	public void start() {
		
		try {
			Display.setDisplayMode(new DisplayMode(1280,720));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		initGL();
		loader.load("assets/monster");
		
		getDelta();
		lastFPS = getTime(); 
		//NanoBots nanoBots = new NanoBots();
		
		while (!Display.isCloseRequested()) {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			
			draw();
			
			Display.update();
			
			Display.sync(60);
			
			updateFPS();
			
		}
		
		Display.destroy();
	}
	
	
	public void initGL(){
	        GL11.glEnable(GL11.GL_TEXTURE_2D);
	        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
	        GL11.glMatrixMode(GL11.GL_PROJECTION); 
	        GL11.glLoadIdentity();
	        GLU.gluOrtho2D(0.0f,1280.0f,0,720.0f);
	        GL11.glMatrixMode(GL11.GL_MODELVIEW); 
	        //GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
			//GL11.glPolygonMode(GL11.GL_BACK, GL11.GL_LINE);
		        
	}
	
	public static void main(String[] argv) {
		new LwjglTest();
	}
	
	/** 
	 * Calculate how many milliseconds have passed 
	 * since last frame.
	 * 
	 * @return milliseconds passed since last frame 
	 */
	public int getDelta() {
	    long time = getTime();
	    int delta = (int) (time - lastFrame);
	    lastFrame = time;
 
	    return delta;
	}
 
	/**
	 * Get the accurate system time
	 * 
	 * @return The system time in milliseconds
	 */
	public long getTime() {
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	/**
	 * Calculate the FPS and set it in the title bar
	 */
	public void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			Display.setTitle("Spriter test for LWJGL - "+"FPS: " + fps);
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}
}
