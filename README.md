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
To see this all in action, checkout out: [AnimationSpeedTest](https://github.com/Trixt0r/spriter/blob/master/SpriterTests/src/com/brashmonkey/spriter/tests/AnimationSpeedTest.java), [AnimationSwitchTest](https://github.com/Trixt0r/spriter/blob/master/SpriterTests/src/com/brashmonkey/spriter/tests/AnimationSwitchTest.java) and [TransformationTest](https://github.com/Trixt0r/spriter/blob/master/SpriterTests/src/com/brashmonkey/spriter/tests/TransformationTest.java)


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
As you may notice the manipulation takes place between the update and drawing call. This is very important to do the manipulation methods between those two calls, otherwise you will not see any desired results.
See [ObjectManipulationTest](https://github.com/Trixt0r/spriter/blob/master/SpriterTests/src/com/brashmonkey/spriter/tests/ObjectManipulationTest.java) for more information.

Bounding boxes and collision tests
----------------------------------
The library offers you a way to calculate bounding boxes for specific parts of an animation or for the whole animation.
The only thing you have to do is:
```
Rectangle bbox = player.getBoundingRectangle(null);
```
this will return you the surrounding rectangle of the current animation state.
You could also pass a bone as an argument to calculate the bounding box for a specific part of the sekelton.
The [Rectangle](https://github.com/Trixt0r/spriter/blob/master/Spriter/src/com/brashmonkey/spriter/Rectangle.java) class has some usefull methods like intersection checking or merging two rectangles to a bigger one.

Checking if a certain point lies in the bounding box of a bone or object can also be usefull, if you want e.g. pick the head and drag it around or check if the sword during an attack animation is hitting something. All you need to do is:
```
boolean hits = player.collidesFor(swordObject, x, y);
if(hits){
  //cut off the balls...
}
```
Checking if the object collides with a rectangular are is also possible:
```
Rectangle area = new Rectangle(0,0, 500, 500);
//...
boolean hits = player.collidesFor(swordObject, area);
if(hits){
  //cut off the balls...
}
```
Have a look at the [CollisionTest](https://github.com/Trixt0r/spriter/blob/master/SpriterTests/src/com/brashmonkey/spriter/tests/CollisionTest.java) and [CullingTest](https://github.com/Trixt0r/spriter/blob/master/SpriterTests/src/com/brashmonkey/spriter/tests/CullingTest.java) to see the feature in action.


Interpolation and animation composition
----------------------------------------
It is quite common in many games that animation switches of a character are not instant. In most games there is a smooth transition between the end of e.g. idle animation and the beginning of a walking animation.
This library is able to such stuff. You can create a [PlayerTweener](https://github.com/Trixt0r/spriter/blob/master/Spriter/src/com/brashmonkey/spriter/PlayerTweener.java) which relies on two other Player instances. You can either create a PlayerTweener with or without assigning it the Player instances. In any case, you will have access to them. A PlayerTweener will interpolate the bones and objects of two Player instances. Instantiating a PlayerTweener is the same as instantiating a normal Player:
```
PlayerTweener tweener = new PlayerTweener(data.getEntity(index));
```
This will create internally two Player instances. You can access them with `tweener.getFirstPlayer()` or `tweener.getSecondPlayer()`.
The tweener will update the players with its `update()` method. You can turn off the auto update by setting `tweener.updatePlayers = false;` but then you have to update them on your own.
As a default value the interplation weight between the two players is at 50%. Setting and getting the weight works like this:
```
tweener.setWeight(.75f);//Sets the weight to 75%, i.e. the second player will have more influence than the first
tweener.getWeight();//Returns the weight of the player
```
A weight of 0% means that only the first player will be played back, a weight of 100% means that the second player will be played back.
Setting the animation of a player is not possible because it makes no sense and it uses its own interpolated animation internally.
Transforming the tweener works in the same way as for a normal player object. Transforming the internal players of a tweener will have no effect on the interpolation, since only the tweened realtive transformations are taken into consideration.
Note that tweening two animations makes only sense if both animations have a similar structure. I recommend that you create your animations with the same bone naming structure, since this will give you the best tweening result.

Check out the [InterpolationTest](https://github.com/Trixt0r/spriter/blob/master/SpriterTests/src/com/brashmonkey/spriter/tests/InterpolationTest.java) for more information.

You can also force the tweener to only interpolate bones and objects starting from a specific root. This can be useful if you want e.g. play a shooting animation while your character is running.

Let's say you have an animation called "walk" and one called "shoot". Then we would set the base animation of the tweener to "walk", the animation of the second player would also be "walk" and the animation of the first player would be "shoot". Then we could set the weight of the tweener to zero, indicating that only "shoot" will be played back. As a last step we need to specify what the name of the root bone is. The tweener will then tween all bones starting from the given root bone. Then all bones and objects which do not occur in the children list of the root bone will stay at the animation "walk". Here is the code snippet:
```
tweener.setBaseAnimation("walk");
tweener.getSecondPlayer().setAnimation("walk");
tweener.getFirstPlayer().setAnimation("shoot");
tweener.setWeight(0f);
```
Have a look at [CompositionTest](https://github.com/Trixt0r/spriter/blob/master/SpriterTests/src/com/brashmonkey/spriter/tests/CompositionTest.java).


Inverse kinematics
-------------------
Inverse kinematics is also supported by the library. The default inverse kinematics algorithm is [Cyclic Coordinate Descent](http://www.ryanjuckett.com/programming/cyclic-coordinate-descent-in-2d/), but the library is designed to have also other algorithms for ik. If you have a better algorithm, extend [IKResolver](https://github.com/Trixt0r/spriter/blob/master/Spriter/src/com/brashmonkey/spriter/IKResolver.java).
The basic usage for inverse kinematics is rather simple. Everything you have to do is mapping specific [IKObjects](https://github.com/Trixt0r/spriter/blob/master/Spriter/src/com/brashmonkey/spriter/IKRObject.java) to your resolver and update the resolver after your player got updated.
Here is a snippet for  the basic usage:
```
//Create a resolver
IKResolver resolver = new CCDResolver();

//Create an ik object and map it to the resolver
IKObject ikObject = new IKObject(0, 0, 2, 5); //Creates an ik object at (0,0), with a chain length of 2 and forces the resolver to apply the algorithm at most 5 times
resolver.mapIKObject(ikObject, yourBone);


//During main loop
player.update();
resolver.resolve();

drawer.draw(player);
```
Since applying inverse kinematics is the same as manipulating objects of a player you have to call the resolve method between update and draw.
See it in action in the [InverseKinematicsTest](https://github.com/Trixt0r/spriter/blob/master/SpriterTests/src/com/brashmonkey/spriter/tests/InverseKinematicsTest.java).
