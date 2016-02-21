package denaro.nick.worldgen;

import java.util.LinkedList;

import denaro.nick.worldgen.Cave.Direction;

public class Village
{
	public static final HouseOptions SMALL_HOUSES = new HouseOptions(HouseOptions.MIN_BUILDING_WIDTH, HouseOptions.MIN_BUILDING_HEIGHT, HouseOptions.VAR_BUILDING_WIDTH, HouseOptions.VAR_BUILDING_HEIGHT);
	public static final VillageOptions SMALL_VILLAGES = new VillageOptions(VillageOptions.MIN_VILLAGE_RADIUS, VillageOptions.VAR_VILLAGE_RADIUS, VillageOptions.MIN_VILLAGE_DENSITY, VillageOptions.VAR_VILLAGE_DENSITY, VillageOptions.MIN_SPACE_BETWEEN_STRUCTURES, SMALL_HOUSES); 
	public static final VillageOptions LARGE_VILLAGES = new VillageOptions(200, 300, 500, 1000, VillageOptions.MIN_SPACE_BETWEEN_STRUCTURES, SMALL_HOUSES);
	public static final VillageOptions VILLAGE_TYPE = LARGE_VILLAGES;
	
	private int x;
	private int y;
	private int radius;
	private VillageOptions villageOptions;
	
	public Village(World world, int x, int y, VillageOptions villageOptions)
	{
		this.x = x;
		this.y = y;
		this.villageOptions = villageOptions;
		this.radius = villageOptions.minRadius + world.rand.nextInt(villageOptions.varRadius);
		
		/*for(int j = -radius; j < radius; j++)
		{
			for(int i = -radius; i < radius; i++)
			{
				if(x + i < 0 || x + i >= world.getWidth() || y + j < 0 || y + j >= world.getHeight())
					continue;
				
				if(i*i + j*j < radius*radius)
				{
					char land = world.getLand(x+i, y+j);
					if("mMbwo".indexOf(land) == -1)
					{
						world.setConstruct(x+i, y+j, 'V');
					}
				}
			}
		}*/
		
		setInRadius(world, "mMbwo", -1, 'V');
		
		LinkedList<Tuple<Integer,Direction>> roadsides = new LinkedList<Tuple<Integer,Direction>>();
		
		for(int j = -radius; j < radius; j++)
		{
			for(int i = -radius; i < radius; i++)
			{
				if(!world.isInBounds(x + i, y + j))
				{
					continue;
				}
				
				if(world.getConstruct(x + i, y + j) == 'V')
				{
					boolean r[][] = world.roadAdjacency(x + i, y + j);
					/*if((r[1][0] && r[1][1] && r[1][2] && !r[0][1] && !r[2][1])
							|| (r[0][1] && r[1][1] && r[2][1] && !r[1][0] && !r[1][2]))*/
					if(!r[1][1] && !r[0][1] && !r[2][1] && r[1][2])
					{
						roadsides.push(new Tuple<Integer,Direction>((x + i) + (y + j) * world.getWidth(),Direction.NORTH));
					}
					if(!r[1][1] && !r[1][1] && !r[1][2] && r[2][1])
					{
						roadsides.push(new Tuple<Integer,Direction>((x + i) + (y + j) * world.getWidth(),Direction.WEST));
					}
					if(!r[1][1] && !r[0][1] && !r[2][1] && r[1][0])
					{
						roadsides.push(new Tuple<Integer,Direction>((x + i) + (y + j) * world.getWidth(),Direction.SOUTH));
					}
					if(!r[1][1] && !r[1][1] && !r[1][2] && r[0][1])
					{
						roadsides.push(new Tuple<Integer,Direction>((x + i) + (y + j) * world.getWidth(),Direction.EAST));
					}
				}
			}
		}
		
		int density = villageOptions.minDensity + world.rand.nextInt(villageOptions.varDensity);
		
		//System.out.println("number of possible houses: " + roadsides.size());
		
		for(int i = 0; i < density; i++)
		{
			spawnBuilding(world, roadsides);
		}
	}
	
	public void setInRadius(World world, String types, int condition, char placed)
	{
		for(int j = -radius; j < radius; j++)
		{
			for(int i = -radius; i < radius; i++)
			{
				if(x + i < 0 || x + i >= world.getWidth() || y + j < 0 || y + j >= world.getHeight())
					continue;
				
				if(i*i + j*j < radius*radius)
				{
					char land = world.getLand(x+i, y+j);
					if(types.indexOf(land) == condition)
					{
						world.setConstruct(x+i, y+j, placed);
					}
				}
			}
		}
	}
	
	private void spawnBuilding(World world, LinkedList<Tuple<Integer,Direction>> positions)
	{
		int width = villageOptions.houseOptions.minBuildingWidth + world.rand.nextInt(villageOptions.houseOptions.varBuildingWidth);
		int height = villageOptions.houseOptions.minBuildingHeight + world.rand.nextInt(villageOptions.houseOptions.varBuildingHeight);
		
		//System.out.println("width: "+width);
		//System.out.println("height: "+height);
		
		//int doorPos = 1 + world.rand.nextInt(width - 2);
		int doorPos = width / 2;
		
		//System.out.println("doorPos:" + doorPos);
		
		boolean open = true;
		
		int doorX;
		int doorY;
		
		int tries = VillageOptions.MAX_TRY_COUNT;
		
		Tuple<Integer,Direction> t;
		
		do
		{
			open = true;
			//int r = world.rand.nextInt(this.radius);
			//int direction = world.rand.nextInt(360);
			
			//doorX = (int) (x + Math.cos(Math.toRadians(direction)) * r);
			//doorY = (int) (y + Math.sin(Math.toRadians(direction)) * r);
			
			int r = world.rand.nextInt(positions.size());
			
			t = positions.get(r);
			doorX = t.first % world.getWidth();
			doorY = t.first / world.getWidth();
			
			//System.out.println(doorX+","+doorY);
			
			open = createBuilding(world,t.second,doorX,doorY, height, doorPos, width - doorPos, true);
			
		}while(!open && --tries > 0 && !positions.isEmpty());
		
		if(tries == 0)
			return;
		
		//System.out.println("open: "+open);
		
		//System.out.println("tries: "+tries);
		
		char doorLand = world.getLand(doorX, doorY);
		
		//Build foundation
		
		layFoundation(world, t.second, doorX, doorY, height, doorPos, width - doorPos, world.getLand(doorX, doorY));
		
		/*for(int j = -2 ; j < height + 1; j++)
		{
			for(int i = -doorPos - 1; i < width - doorPos + 1; i++)
			{
				if(!(doorX + i <= 0 || doorY - j <= 0 || doorX + i >= world.getWidth() - 1 || doorY - j >= world.getHeight() - 1))
				{
					world.setLand(doorX+i,doorY-j,doorLand);
				}
			}
		}*/
		
		createBuilding(world, t.second, doorX, doorY, height, doorPos, width - doorPos, false);
		
		
		/*for(int j = 0 ; j < height; j++)
		{
			for(int i = -doorPos ; i < width - doorPos; i++)
			{
				if(i==0 && j == 0)
					world.setConstruct(doorX+i,doorY-j,'D');
				else if(i == -doorPos || i == width - doorPos - 1 || j == 0 || j == height - 1)
					world.setConstruct(doorX+i,doorY-j,'W');
				else
					world.setConstruct(doorX+i,doorY-j,'F');
				
				
			}
		}*/
		
		world.setConstruct(doorX, doorY, 'D');
		
	}
	
	private void layFoundation(World world, Direction direction, int x, int y, int length, int leftWidth, int rightWidth, char type)
	{
		int[] ords = Cave.directionToOrdinals(direction);
		int len = ords[0] == 0 ? 1 : 0;
		int wid = ords[0] == 0 ? 0 : 1;
		
		//boolean exit = false;
		
		//System.out.println("len: "+len+", wid: "+wid);
		for(int w = -leftWidth - 1; w < rightWidth / 2 + 1 + 1; w++)
		{
			for(int l = -1; l < length + 1; l++)
			{
				int xx = x + (len == 0 ? ords[len] * l : w);
				int yy = y + (len == 0 ? w : ords[len] * l);
				
				//System.out.println("c "+xx+", "+yy);
				
				if(world.isInBounds(xx, yy, 1))
				{
					world.setLand(xx, yy, type);
				}
			}
		}
	}
	
	private boolean createBuilding(World world, Direction direction, int x, int y, int length, int leftWidth, int rightWidth, boolean test)
	{
		/*System.out.println("Carving:");
		System.out.println(x+", "+y);
		System.out.println(direction);
		System.out.println("length: "+length+", width: "+width);*/
		
		
		int[] ords = Cave.directionToOrdinals(direction);
		int len = ords[0] == 0 ? 1 : 0;
		int wid = ords[0] == 0 ? 0 : 1;
		
		//boolean exit = false;
		
		//System.out.println("len: "+len+", wid: "+wid);
		for(int w = -leftWidth; w < rightWidth; w++)
		{
			for(int l = 0; l < length; l++)
			{
				int xx = x + (len == 0 ? ords[len] * l : w);
				int yy = y + (len == 0 ? w : ords[len] * l);
				
				//System.out.println("c "+xx+", "+yy);
				
				if(world.isInBounds(xx, yy, 1))
				{
					if(!world.isConstructOfType(xx, yy, "V") || world.getRoad(xx, yy) == '4')
					{
						//world.setConstruct(xx, yy, 'c');
						return false;
					}
					else if(!test)
					{
						if(w == -leftWidth)
						{
							world.setConstruct(xx, yy, 'W');
						}
						else if(w == rightWidth - 1)
						{
							world.setConstruct(xx, yy, 'W');
						}
						else if(l == 0)
						{
							world.setConstruct(xx, yy, 'W');
						}
						else if(l == length - 1)
						{
							world.setConstruct(xx, yy, 'W');
						}
						else
						{
							world.setConstruct(xx, yy, 'F');
						}
					}
				}
			}
		}
		return true;
	}
}
