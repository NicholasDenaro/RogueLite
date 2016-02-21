package denaro.nick.roguelite;
import java.awt.Point;
import java.io.IOException;

import denaro.nick.core.FixedTickType;
import denaro.nick.core.GameEngine;
import denaro.nick.core.GameEngineException;
import denaro.nick.core.GameFrame;
import denaro.nick.core.Sprite;
import denaro.nick.worldgen.World;
import denaro.nick.worldgen.WorldFramer;

public class Main
{
	public static void main(String[] args)
	{
		GameEngine engine;
		try
		{
			loadSprites();
			
			engine = GameEngine.instance(new FixedTickType(60), false);

			engine.view(new WorldView(160 * 4, 160 * 4, 1, 1));
			
			World world = World.getWorld();
			
			WorldFramer framer = new WorldFramer(world);
			framer.addDisplayMouseMotionListener(new DisplayUpdater());
			
			WorldLocation worldLoc = new WorldLocation(world);
			
			engine.location(worldLoc);
			
			new GameFrame("RogueLite",engine);
			
			engine.start();
		}
		catch(GameEngineException | IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	private static void loadSprites() throws IOException
	{
		new Sprite("FloorTiles", "DawnLike/Objects/Floor.png", 16, 16, new Point(0,0));
		new Sprite("TreeTiles", "DawnLike/Objects/Tree0.png", 16, 16, new Point(0,0));
		new Sprite("WallTiles", "DawnLike/Objects/Wall.png", 16, 16, new Point(0,0));
		new Sprite("PitTiles", "DawnLike/Objects/Pit0.png", 16, 16, new Point(0,0));
		new Sprite("DoorTiles", "DawnLike/Objects/Door0.png", 16, 16, new Point(0,0));
	}
}
