package denaro.nick.roguelite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.HashMap;

import denaro.nick.core.Location;
import denaro.nick.core.Pair;
import denaro.nick.core.Sprite;
import denaro.nick.core.view.GameView2D;
import denaro.nick.worldgen.World;

public class WorldView extends GameView2D
{
	public Point viewPos;
	private HashMap<Character, Pair<String, Point>> landMap;
	private HashMap<Character, Pair<String, Point>> biomeMap;
	private HashMap<Character, Pair<String, Point>> constructMap;
	private HashMap<Character, Pair<String, Point>> caveMap;
	private HashMap<Character, Pair<String, Point>> roadMap;
	private HashMap<Character, Point> tileFloorConnection;
	private HashMap<Character, Point> tileWallConnection;
	
	public static final Point[] CURL = new Point[]{
			new Point(-1, -1),
			new Point(0, -1),
			new Point(1, -1),
			new Point(1, 0),
			new Point(1, 1),
			new Point(0, 1),
			new Point(-1, 1),
			new Point(-1, 0)
	};
	
	public WorldView(int width, int height, double hscale, double vscale)
	{
		super(width, height, hscale, vscale);
		viewPos = new Point(0,0);
		
		createLandMap();
		createBiomeMap();
		createConstructMap();
		createCaveMap();
		createRoadMap();
		
		//createGeneralMap();
		
		createFloorTileConnectionMap();
		createWallTileConnectionMap();
	}
	
	/*private void createGeneralMap()
	{
		
	}*/
	
	private String toBinary(char ch)
	{
		String s = "";
		for(int i = 0; i < 8; i++)
		{
			s = (ch % 2 == 0 ? 0 : 1) + s;
			ch /= 2;
		}
		
		return s;
	}
	
	private void createFloorTileConnectionMap()
	{
		tileFloorConnection = new HashMap<Character, Point>();
		
		tileFloorConnection.put((char) 0b00101000, new Point(0,0));
		tileFloorConnection.put((char) 0b10101000, new Point(1,0));
		tileFloorConnection.put((char) 0b10100000, new Point(2,0));
		tileFloorConnection.put((char) 0b00100000, new Point(3,0));
		tileFloorConnection.put((char) 0b00000000, new Point(5,0));
		tileFloorConnection.put((char) 0b00101010, new Point(0,1));
		tileFloorConnection.put((char) 0b10101010, new Point(1,1));
		tileFloorConnection.put((char) 0b10100010, new Point(2,1));
		tileFloorConnection.put((char) 0b00100010, new Point(3,1));
		tileFloorConnection.put((char) 0b00001000, new Point(4,1));
		tileFloorConnection.put((char) 0b10001000, new Point(5,1));
		tileFloorConnection.put((char) 0b10000000, new Point(6,1));
		tileFloorConnection.put((char) 0b00001010, new Point(0,2));
		tileFloorConnection.put((char) 0b10001010, new Point(1,2));
		tileFloorConnection.put((char) 0b10000010, new Point(2,2));
		tileFloorConnection.put((char) 0b00000010, new Point(3,2));
	}
	
	private void createWallTileConnectionMap()
	{
		tileWallConnection = new HashMap<Character, Point>();
		
		tileWallConnection.put((char) 0b00101000, new Point(0,0));
		tileWallConnection.put((char) 0b10001000, new Point(1,0));
		tileWallConnection.put((char) 0b10100000, new Point(2,0));
		tileWallConnection.put((char) 0b00000000, new Point(3,0));
		tileWallConnection.put((char) 0b10101000, new Point(4,0));
		tileWallConnection.put((char) 0b00100010, new Point(0,1));
		tileWallConnection.put((char) 0b00000010, new Point(1,1));
		tileWallConnection.put((char) 0b00001000, new Point(1,0));//
		tileWallConnection.put((char) 0b00100000, new Point(0,1));//
		tileWallConnection.put((char) 0b10000000, new Point(1,0));//
		tileWallConnection.put((char) 0b00101010, new Point(3,1));
		tileWallConnection.put((char) 0b10101010, new Point(4,1));
		tileWallConnection.put((char) 0b10100010, new Point(5,1));
		tileWallConnection.put((char) 0b00001010, new Point(0,2));
		tileWallConnection.put((char) 0b10000010, new Point(2,2));
		tileWallConnection.put((char) 0b10001010, new Point(4,2));
	}
	
	private void createLandMap()
	{
		landMap = new HashMap<Character,Pair<String, Point>>();
		
		landMap.put('o', new Pair("PitTiles", new Point(0, 14)));
		landMap.put('w', new Pair("PitTiles", new Point(0, 8)));
		landMap.put('b', new Pair("FloorTiles", new Point(0, 15)));
		landMap.put('p', new Pair("FloorTiles", new Point(7, 6)));
		/*landMap.put('P', Sprite.sprite("FloorTiles").subimage(0, 0));
		landMap.put('h', Sprite.sprite("FloorTiles").subimage(0, 0));
		landMap.put('H', Sprite.sprite("FloorTiles").subimage(0, 0));*/
		landMap.put('m', new Pair("FloorTiles", new Point(0, 24)));
		landMap.put('M', new Pair("FloorTiles", new Point(14, 24)));
	}
	
	private void createBiomeMap()
	{
		biomeMap = new HashMap<Character,Pair<String, Point>>();
		
		biomeMap.put('D', new Pair("FloorTiles", new Point(14, 6)));
		biomeMap.put('G', new Pair("FloorTiles", new Point(7, 6)));
		biomeMap.put('F', new Pair("FloorTiles", new Point(7, 9)));
		biomeMap.put('J', new Pair("FloorTiles", new Point(7, 12)));
	}
	
	private void createConstructMap()
	{
		constructMap = new HashMap<Character,Pair<String, Point>>();
		
		constructMap.put('T', new Pair("TreeTiles", new Point(3, 3)));
		constructMap.put('C', new Pair("WallTiles", new Point(0, 18)));

		constructMap.put('F', new Pair("FloorTiles", new Point(7, 18)));
		constructMap.put('D', new Pair("DoorTiles", new Point(0, 0)));
		constructMap.put('W', new Pair("WallTiles", new Point(7, 3)));
	}
	
	private void createCaveMap()
	{
		caveMap = new HashMap<Character,Pair<String, Point>>();
		
		caveMap.put('m', new Pair("FloorTiles", new Point(0, 21)));
		caveMap.put('M', new Pair("FloorTiles", new Point(0, 21)));
	}
	
	private void createRoadMap()
	{
		roadMap = new HashMap<Character,Pair<String, Point>>();
		
		roadMap.put('4', new Pair("FloorTiles", new Point(0, 6)));
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
	
	private char getCaveAdjacency(World world, int w, int h)
	{
		char c = 0;
		
		char l = world.getCave(w, h);
		
		for(int i = CURL.length - 1; i >= 0; i--)
		{
			c = (char) (c << 1);
			Point p = CURL[i];
			if(!world.isInBounds(w + p.x, h + p.y) || i % 2 == 0)
			{
				continue;
			}
			char adj = world.getCave(w + p.x, h + p.y);
			
			c = (char) (c | ((l == adj) ? 1 : 0));
		}
		
		return c;
	}
	
	private char getConstructAdjacency(World world, int w, int h)
	{
		char c = 0;
		
		char l = world.getConstruct(w, h);
		
		for(int i = CURL.length - 1; i >= 0; i--)
		{
			c = (char) (c << 1);
			Point p = CURL[i];
			if(!world.isInBounds(w + p.x, h + p.y) || i % 2 == 0)
			{
				continue;
			}
			char adj = world.getConstruct(w + p.x, h + p.y);
			
			c = (char) (c | ((l == adj) ? 1 : 0));
		}
		
		return c;
	}
	
	private char getLandAdjacency(World world, int w, int h)
	{
		char c = 0;
		
		char l = world.getLand(w, h);
		
		for(int i = CURL.length - 1; i >= 0; i--)
		{
			c = (char) (c << 1);
			Point p = CURL[i];
			if(!world.isInBounds(w + p.x, h + p.y) || i % 2 == 0)
			{
				continue;
			}
			char adj = world.getLand(w + p.x, h + p.y);
			
			c = (char) (c | ((l == adj) ? 1 : 0));
		}
		
		return c;
	}
	
	private char getBiomeAdjacency(World world, int w, int h)
	{
		char c = 0;
		
		char l = world.getBiome(w, h);
		
		for(int i = CURL.length - 1; i >= 0; i--)
		{
			c = (char) (c << 1);
			Point p = CURL[i];
			if(!world.isInBounds(w + p.x, h + p.y) || i % 2 == 0)
			{
				continue;
			}
			char adj = world.getBiome(w + p.x, h + p.y);
			
			c = (char) (c | ((l == adj) ? 1 : 0));
		}
		
		return c;
	}
	
	private char getCliffAdjacency(World world, int w, int h)
	{
		char c = 0;
		
		char l = world.getConstruct(w, h);
		
		for(int i = CURL.length - 1; i >= 0; i--)
		{
			c = (char) (c << 1);
			Point p = CURL[i];
			if(!world.isInBounds(w + p.x, h + p.y) || i % 2 == 0)
			{
				continue;
			}
			char adj = world.getConstruct(w + p.x, h + p.y);
			
			c = (char) (c | ((l == adj) ? 1 : 0));
		}
		
		return c;
	}
	
	private char getRoadAdjacency(World world, int w, int h)
	{
		char c = 0;
		
		char l = world.getRoad(w, h);
		
		for(int i = CURL.length - 1; i >= 0; i--)
		{
			c = (char) (c << 1);
			Point p = CURL[i];
			if(!world.isInBounds(w + p.x, h + p.y) || i % 2 == 0)
			{
				continue;
			}
			char adj = world.getRoad(w + p.x, h + p.y);
			
			c = (char) (c | ((l == adj) ? 1 : 0));
		}
		
		return c;
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
						Pair<String,Point> sprite = landMap.get(land);
						Point connector = tileFloorConnection.get(getLandAdjacency(world, viewPos.x + w, viewPos.y + h));
						//System.out.println(sprite.first());
						//System.out.println(sprite.second());
						//System.out.println(Sprite.sprite(sprite.first()));
						//System.out.println(toBinary(getLandAdjacency(world, viewPos.x + w, viewPos.y + h)));
						//System.out.println(connector);
						Image img = Sprite.sprite(sprite.first()).subimage(sprite.second().x + connector.x, sprite.second().y + connector.y);
						g.drawImage(img, w * 16, h * 16, null);
						
						if(land == 'm' || land == 'M')
						{
							char cave = world.getCave(viewPos.x + w, viewPos.y + h);
							if(cave == 'm' || cave == 'M')
							{
								sprite = caveMap.get(world.getCave(viewPos.x + w, viewPos.y + h));
								connector = tileFloorConnection.get(getCaveAdjacency(world, viewPos.x + w, viewPos.y + h));
								//System.out.println(sprite.first());
								//System.out.println(sprite.second());
								//System.out.println(Sprite.sprite(sprite.first()));
								//System.out.println(toBinary(getLandAdjacency(world, viewPos.x + w, viewPos.y + h)));
								//System.out.println(connector);
								img = Sprite.sprite(sprite.first()).subimage(sprite.second().x + connector.x, sprite.second().y + connector.y);
								g.drawImage(img, w * 16, h * 16, null);
							}
						}
						
						sprite = constructMap.get(world.getConstruct(viewPos.x + w, viewPos.y + h));
						if(sprite != null)
						{
							if(world.getConstruct(viewPos.x + w, viewPos.y + h) == 'C')
							{
								connector = tileWallConnection.get(getCliffAdjacency(world, viewPos.x + w, viewPos.y + h));
							}
							else
							{
								connector = new Point(0, 0);
							}
							//System.out.println(sprite.first());
							//System.out.println(sprite.second());
							//System.out.println(Sprite.sprite(sprite.first()));
							//System.out.println(toBinary(getLandAdjacency(world, viewPos.x + w, viewPos.y + h)));
							//System.out.println(connector);
							img = Sprite.sprite(sprite.first()).subimage(sprite.second().x + connector.x, sprite.second().y + connector.y);
							g.drawImage(img, w * 16, h * 16, null);
						}
					}
					else
					{
						Pair<String, Point> sprite = biomeMap.get(world.getBiome(viewPos.x + w, viewPos.y + h));
						Point connector = tileFloorConnection.get(getBiomeAdjacency(world, viewPos.x + w, viewPos.y + h));
						//System.out.println(sprite.first());
						//System.out.println(sprite.second());
						//System.out.println(Sprite.sprite(sprite.first()));
						//System.out.println(toBinary(getBiomeAdjacency(world, viewPos.x + w, viewPos.y + h)));
						//System.out.println(connector);
						Image img = Sprite.sprite(sprite.first()).subimage(sprite.second().x + connector.x, sprite.second().y + connector.y);
						g.drawImage(img, w * 16, h * 16, null);
						
						if(world.getRoad(viewPos.x + w, viewPos.y + h) == '4')
						{
							sprite = roadMap.get(world.getRoad(viewPos.x + w, viewPos.y + h));
							connector = tileFloorConnection.get(getRoadAdjacency(world, viewPos.x + w, viewPos.y + h));
							//System.out.println(sprite.first());
							//System.out.println(sprite.second());
							//System.out.println(Sprite.sprite(sprite.first()));
							//System.out.println(toBinary(getRoadAdjacency(world, viewPos.x + w, viewPos.y + h)));
							//System.out.println(connector);
							img = Sprite.sprite(sprite.first()).subimage(sprite.second().x + connector.x, sprite.second().y + connector.y);
							g.drawImage(img, w * 16, h * 16, null);
						}
						sprite = constructMap.get(world.getConstruct(viewPos.x + w, viewPos.y + h));
						if(sprite != null)
						{
							//System.out.println(sprite.first());
							//System.out.println(sprite.second());
							//System.out.println(Sprite.sprite(sprite.first()));
							char constr = world.getConstruct(viewPos.x + w, viewPos.y + h);
							if(constr == 'C' || constr == 'W')
							{
								connector = tileWallConnection.get(getCliffAdjacency(world, viewPos.x + w, viewPos.y + h));
								//System.out.println(toBinary(getCliffAdjacency(world, viewPos.x + w, viewPos.y + h)));
							}
							else if(constr == 'W')
							{
								connector = tileWallConnection.get(getConstructAdjacency(world, viewPos.x + w, viewPos.y + h));								//System.out.println(toBinary(getCliffAdjacency(world, viewPos.x + w, viewPos.y + h)));
							}
							else if(constr == 'F')
							{
								connector = tileFloorConnection.get(getConstructAdjacency(world, viewPos.x + w, viewPos.y + h));
								//System.out.println(toBinary(getCliffAdjacency(world, viewPos.x + w, viewPos.y + h)));
							}
							else
							{
								connector = new Point(0, 0);
							}
							//System.out.println(connector);
							img = Sprite.sprite(sprite.first()).subimage(sprite.second().x + connector.x, sprite.second().y + connector.y);
							g.drawImage(img, w * 16, h * 16, null);
						}
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
