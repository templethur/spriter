package com.brashmonkey.spriter.tests.backend;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Image;

import com.brashmonkey.spriter.Drawer;
import com.brashmonkey.spriter.FileReference;
import com.brashmonkey.spriter.Loader;
import com.brashmonkey.spriter.Player;
import com.brashmonkey.spriter.SCMLReader;
import com.brashmonkey.spriter.Data;
import com.brashmonkey.spriter.Timeline.Key.Object;

public class Slick2DTest extends BasicGame{
	
    float scale = 1;
    private Drawer<Image> drawer;
	Player player;
    
    public Slick2DTest()
    {
        super("Spriter test for Slick2D");
    }
 
    @Override
    public void init(final GameContainer gc) throws SlickException {
    	
		Data data = new SCMLReader("assets/monster/basic_002.scml").getData();
		player = new Player(data.getEntity(0));
		player.setPosition(640, 480);
    	final Graphics g = gc.getGraphics();
		
    	Loader<Image> loader = new Loader<Image>(data){

			@Override
			protected Image loadResource(FileReference ref) {
				try {
					return new Image(super.root+"/"+data.getFile(ref).name);
				} catch (SlickException e) {
					e.printStackTrace();
				}
				return null;
			}
    		
    	};
    	
    	loader.load("assets/monster");
    	
    	this.drawer = new Drawer<Image>(loader){
			@Override
			public void setColor(float r, float g, float b, float a) {
			}

			@Override
			public void line(float x1, float y1, float x2, float y2) {
			}

			@Override
			public void rectangle(float x, float y, float width,
					float height) {
			}

			@Override
			public void circle(float x, float y, float radius) {
			}

			@Override
			public void draw(Object object) {
				Image image = loader.get(object.ref);
				float newPivotX = (image.getWidth() * object.pivot.x);
				float newX = object.position.x - newPivotX*Math.signum(object.scale.x);;

				float newPivotY = (image.getHeight() * object.pivot.y);
				float newY = (gc.getHeight() - object.position.y) - (image.getHeight() - newPivotY)*Math.signum(object.scale.y);
				
				g.rotate(object.position.x, (gc.getHeight() - object.position.y), - object.angle);
				image.setAlpha(object.alpha);
				image.draw(newX, newY,image.getWidth()*object.scale.x,image.getHeight()*object.scale.y);
				g.resetTransform();
			}
    		
    	};
    }
 
    @Override
    public void update(GameContainer gc, int delta)
			throws SlickException
    {
    	player.update();
    }
 
    public void render(GameContainer gc, Graphics g) throws SlickException
    {
    	
    	drawer.draw(player);
    }
 
    public static void main(String[] args) throws SlickException
    {
         AppGameContainer app = new AppGameContainer( new Slick2DTest() );
         app.setDisplayMode(1280, 720, false);
         app.setTargetFrameRate(60);
         app.start();
    }
}
