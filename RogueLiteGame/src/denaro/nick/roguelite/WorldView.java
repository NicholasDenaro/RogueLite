package denaro.nick.roguelite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.HashMap;

import denaro.nick.core.Location;
import denaro.nick.core.Sprite;
import denaro.nick.core.view.GameView2D;
import denaro.nick.worldgen.World;

public class WorldView extends GameView2D
{
	public Point viewPos;
	private HashMap<Character, Image> landMap;
	private HashMap<Character, Image> biomeMap;
	private HashMap<Character, Image> constructMap;
	private HashMap<Character, Image> caveMap;
	private HashMap<Character, Image> roadMap;
	
	public WorldView(int width, int height, double hscale, double vscale)
	{
		super(width, height, hscale, vscale);
		viewPos = new Point(0,0);
		
		createLandMap();
		createBiomeMap();
		createConstructMap();
		createCaveMap();
		createRoadMap();
	}
	
	private void createLandMap()
	{
		landMap = new HashMap<Character,Image>();
		
		landMap.put('o', Sprite.sprite("PitTiles").subimage(1, 15));
		landMap.put('w', Sprite.sprite("PitTiles").subimage(1, 9));
		landMap.put('b', Sprite.sprite("FloorTiles").subimage(1, 16));
		landMap.put('p', Sprite.sprite("FloorTiles").subimage(8, 7));
		landMap.put('P', Sprite.sprite("FloorTiles").subimage(0, 0));
		landMap.put('h', Sprite.sprite("FloorTiles").subimage(0, 0));
		landMap.put('H', Sprite.sprite("FloorTiles").subimage(0, 0));
		landMap.put('m', Sprite.sprite("FloorTiles").subimage(1, 25));
		landMap.put('M', Sprite.sprite("FloorTiles").subimage(15, 25));
	}
	
	private void createBiomeMap()
	{
		biomeMap = new HashMap<Character,Image>();
		
		biomeMap.put('D', Sprite.sprite("FloorTiles").subimage(15, 7));
		biomeMap.put('G', Sprite.sprite("FloorTiles").subimage(8, 7));
		biomeMap.put('F', Sprite.sprite("FloorTiles").subimage(8, 10));
		biomeMap.put('J', Sprite.sprite("FloorTiles").subimage(8, 13));
	}
	
	private void createConstructMap()
	{
		constructMap = new HashMap<Character,Image>();
		
		constructMap.put('T', Sprite.sprite("TreeTiles").subimage(3, 3));
		constructMap.put('C', Sprite.sprite("WallTiles").subimage(3, 18));

		constructMap.put('F', Sprite.sprite("FloorTiles").subimage(8, 19));
		constructMap.put('D', Sprite.sprite("DoorTiles").subimage(0, 0));
		constructMap.put('W', Sprite.sprite("WallTiles").subimage(8, 3));
	}
	
	private void createCaveMap()
	{
		caveMap = new HashMap<Character,Image>();
		
		caveMap.put('m', Sprite.sprite("FloorTiles").subimage(1, 22));
		caveMap.put('M', Sprite.sprite("FloorTiles").subimage(1, 22));
	}
	
	private void createRoadMap()
	{
		roadMap = new HashMap<Character,Image>();
		
		roadMap.put('4', Sprite.sprite("FloorTiles").subimage(1, 7));
	}
	
	private void fixPosition(World world)
	{
		if(viewPos.x < 0)
			viewPos.x = 0;
		if(viewPos.y < 0)
			viewPos.y = 0;
		
		if(viewPos.x + width() / 16 > world.getWidth())
			viewPos.x = world.getWidth() - width() / 16;
		if(viewPos.y + height() / 16 > world.getHeight())
			viewPos.y = world.getHeight() - height() / 16;
	}

	@Override
	public void drawLocation(Location currentLocation, Graphics2D g)
	{
		if(currentLocation instanceof WorldLocation)
		{
			WorldLocation loc = (WorldLocation) currentLocation;
			World world = loc.getWorld();
			
			fixPosition(world);
			
			for(int h = 0; h < height() / 16; h++)
			{
				for(int w = 0; w < width() / 16; w++)
				{
					char land = world.getLand(viewPos.x + w, viewPos.y + h);
					if(land == 'o' || land == 'w' || land == 'b' || land == 'm' || land == 'M')
					{
						g.drawImage(landMap.get(land), w * 16, h * 16, null);
						
						if(land == 'm' || land == 'M')
						{
							char cave = world.getCave(viewPos.x + w, viewPos.y + h);
							if(cave == 'm' || cave == 'M')
							{
								g.drawImage(caveMap.get(world.getCave(viewPos.x + w, viewPos.y + h)), w * 16, h * 16, null);
							}
						}
						
						g.drawImage(constructMap.get(world.getConstruct(viewPos.x + w, viewPos.y + h)), w * 16, h * 16, null);
					}
					else
					{
						g.drawImage(biomeMap.get(world.getBiome(viewPos.x + w, viewPos.y + h)), w * 16, h * 16, null);
						
						if(world.getRoad(viewPos.x + w, viewPos.y + h) != 0)
						{
							g.drawImage(roadMap.get(world.getRoad(viewPos.x + w, viewPos.y + h)), w * 16, h * 16, null);
						}
						g.drawImage(constructMap.get(world.getConstruct(viewPos.x + w, viewPos.y + h)), w * 16, h * 16, null);
					}
				}
			}
		}
		else
		{
			g.drawString("Incorrect location", 2, 12);
		}
	}
}
