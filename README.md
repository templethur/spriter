Spriter (WIP)
=============
A Generic Java library for Spriter animation files.

[Checkout the features video.](http://www.youtube.com/watch?v=i_OxqopvMH0)


Basic usage
-----------
The library is meant to be generic, so to use it you have to implement some backend specific classes on your own:
*   A specific file loader ([Loader](https://github.com/Trixt0r/spriter/blob/master/Spriter/src/com/brashmonkey/spriter/Loader.java "Loader")) class which loads all needed sprites/images/textures into memory and stores them in a reference map.
*   A specific drawer ([Drawer](https://github.com/Trixt0r/spriter/blob/master/Spriter/src/com/brashmonkey/spriter/Drawer.java "Drawer")) class which is repsonsible for drawing the animation data and also to debug draw an animation.

As you see, you have to implement all abstract methods.
If you do not know how to implement them, have a look at the examples provided in the [SpriterTests project](https://github.com/Trixt0r/spriter/tree/master/SpriterTests "SpriterTests project"). There are already examples for: LWJGL, Slick2D, Java2D and LibGDX. The examples, except the LibGDX one, are not optimized since I have no need and no time to optimize them.

After implementing those classes, the next steps should be rather easy.
Create a [SCMLReader](https://github.com/Trixt0r/spriter/blob/master/Spriter/src/com/brashmonkey/spriter/SCMLReader.java) instance which takes care of parsing the SCML file and creating a new [Data](https://github.com/Trixt0r/spriter/blob/master/Spriter/src/com/brashmonkey/spriter/Data.java) containing all necessary data to play back an animation of an entity:
```
SCMLReader reader = new SCMLReader("Path to your SCML file");
Data data = reader.getData();
```
At this point you gathered all animation data and you could create a [Player](https://github.com/Trixt0r/spriter/blob/master/Spriter/src/com/brashmonkey/spriter/Player.java) instance:
```
Player yourPlayer = new Player(data.getEntity(entityIndex));
//or
Player yourPlayer = new Player(data.getEntity("entity name"));
```
This will create a player which will play the animations of your current set entity. The animation after instantiation is the first one occuring in the entity.

Now you also need the sprites which have to be rendered. This means you have to create a loader and a drawer instance:
```
Loader loader = new YourLoaderImplementaion(data);
loader.load("Path to the root folder of your SCML file"); //Load all sprites
//or
File scmlFile = new File("Path to your SCML file");
Loader loader = new YourLoaderImplementaion(data);
loader.load(scmlFile);

Drawer drawer = new YourDrawerImplementation(loader);
```
How you call the constructors depends of course on your implementations.

After instantiating a Loader and a Drawer, you are able to run and draw the desired animation:
```
//update the player
yourPlayer.update();
//and draw it:
drawer.draw(yourPlayer);
```
This will draw the current set animation at the point (0,0).

That's it! Now read further if you want to know what other abilities the library has.

Manipulate a Player at runtime
------------------------------
You have the ability to change the animation and speed of your player.
Changing animation:
```
player.setAnimation(anAnimationIndex);//Set the animation by an index.
player.setAnimation("animation name");//Set the animation by a name.
```
Note: All methods will throw a SpriterException if the given animation index or name does not exist.

Changing speed and current time:
```
player.speed = 15;
//This will set the frame speed to 15. This means that in every update step the frame will jump 15 frames further

player.setTime(100);
//This will set the current time to 100.
//This method will not allow you to jump below or above the bounds of your current animation,
//i.e. the time will always be between zero and the length of the animation.
```

Changing position, rotation, offset, scale and set flipping:
```
player.setPosition(x,y);//Will set the position of the player to (x,y)
player.setAngle(anggle);//Will set the angle of the player to the angle in degrees
player.setScale(2f);//Will set the scale of the player to 200%
player.setPivot(xOffset, yOffset);//Will set the origin of the player
player.flipX();//Will flip the player around the x-axis
player.flipY();//Will flip the player around the y-axis
```
That is the basic transformation usage for a player. There are also methods like `player.translatePosition(x,y)` which should be self explanatory.

Listening for player events
---------------------------
It is quite common to observe an animation at runtime to e.g. switch the animation to another one if the current ends.
For this purpose I added a [PlayerListener](https://github.com/Trixt0r/spriter/blob/master/Spriter/src/com/brashmonkey/spriter/Player.java#L998) interface which can be used to listen for such events.
To register listeners on a player one can just call `player.addListener(yourListener)`. To remove a listener just call `player.removeListener(yourListener)`.


Manipulating bones and objects at runtime
-----------------------------------------
Letting your player look at a specific point in the scene or scaling a specific part of the body is quite useful and does not force your animator to create new animations for every new usecase.
Changing values of a bone or object is rather easy. What you need is the name of the desired bone or object and you will be able to manipulate them:
```
player.update();//First update
//Now you are able to manipulate the objects and bones
player.setBone("name of your bone", angle);//Change the angle
player.setBone("name of your bone", x,y);//Change the position

player.setObject("name of your object", newAlphaValue, folderIndex, fileIndex);//Changes the transparency and the reference sprite of the object

drawer.draw(player);//Finally draw
```
The methods for object and bone manipulation are almost the same, with some extra methods for objects since they have some more attributes.
As you may notice the manipulation takes place between the update and drawing call. This is very important to call the manipulation methods between those two calls, otherwise you will not see any desired results.
```
