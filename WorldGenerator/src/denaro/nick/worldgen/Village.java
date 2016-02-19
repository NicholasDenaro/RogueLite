package denaro.nick.worldgen;

import java.util.LinkedList;

public class Village
{
	public static final int MIN_BUILDING_WIDTH = 4;
	public static final int MIN_BUILDING_HEIGHT = 3;
	public static final int VAR_BUILDING_WIDTH = 7;
	public static final int VAR_BUILDING_HEIGHT = 4;
	
	public static final int MIN_SPACE_BETWEEN_STRUCTURES = 3;
	
	public static final int MIN_DENSITY = 5;
	public static final int VAR_DENSITY = 10;
	
	//public static final int MIN_DENSITY = 200;
	//public static final int VAR_DENSITY = 300;
	
	public static final int MAX_TRY_COUNT = 10;
	
	private int x;
	private int y;
	private int radius;
	
	public Village(World world, int x, int y, int minRadius, int varRadius, int minDensity, int varDensity)
	{
		this.x = x;
		this.y = y;
		this.radius = minRadius + world.rand.nextInt(varRadius);
		
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
		
		LinkedList<Integer> roadsides = new LinkedList<Integer>();
		
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
						roadsides.push((x + i) + (y + j) * world.getWidth());
					}
				}
			}
		}
		
		int density = minDensity + world.rand.nextInt(varDensity);
		
		System.out.println("number of possible houses: " + roadsides.size());
		
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
	
	private void spawnBuilding(World world, LinkedList<Integer> positions)
	{
		int width = MIN_BUILDING_WIDTH + world.rand.nextInt(VAR_BUILDING_WIDTH);
		int height = MIN_BUILDING_HEIGHT + world.rand.nextInt(VAR_BUILDING_HEIGHT);
		
		//System.out.println("width: "+width);
		//System.out.println("height: "+height);
		
		int doorPos = 1 + world.rand.nextInt(width - 2);
		
		//System.out.println("doorPos:" + doorPos);
		
		boolean open = true;
		
		int doorX;
		int doorY;
		
		int tries = MAX_TRY_COUNT;
		
		do
		{
			open = true;
			//int r = world.rand.nextInt(this.radius);
			//int direction = world.rand.nextInt(360);
			
			//doorX = (int) (x + Math.cos(Math.toRadians(direction)) * r);
			//doorY = (int) (y + Math.sin(Math.toRadians(direction)) * r);
			
			int r = world.rand.nextInt(positions.size());
			
			int pos = positions.get(r);
			doorX = pos % world.getWidth();
			doorY = pos / world.getWidth();
			
			System.out.println(doorX+","+doorY);
			
			if(doorX - doorPos <= 0 || doorY - height <= 0 || doorX - doorPos + width >= world.getWidth() - 1 || doorY >= world.getHeight() - 1)
			{
				open = false;
			}
			
			for(int j = -MIN_SPACE_BETWEEN_STRUCTURES ; j < height + MIN_SPACE_BETWEEN_STRUCTURES && open; j++)
			{
				for(int i = -doorPos - MIN_SPACE_BETWEEN_STRUCTURES ; i < width - doorPos + MIN_SPACE_BETWEEN_STRUCTURES && open; i++)
				{
					if(!(doorX + i <= 0 || doorY - j <= 0 || doorX + i >= world.getWidth() - 1 || doorY - j >= world.getHeight() - 1))
					{
						char land = world.getConstruct(doorX + i, doorY - j);
						if(land != 'V' || world.getRoad(doorX + i, doorY - j) == '4')
						{
							open = false;
						}
					}
					
				}
			}
			
		}while(!open && --tries > 0 && !positions.isEmpty());
		
		if(tries == 0)
			return;
		
		//System.out.println("open: "+open);
		
		//System.out.println("tries: "+tries);
		
		char doorLand = world.getLand(doorX, doorY);
		
		//Build foundation
		
		for(int j = -2 ; j < height + 1; j++)
		{
			for(int i = -doorPos - 1; i < width - doorPos + 1; i++)
			{
				if(!(doorX + i <= 0 || doorY - j <= 0 || doorX + i >= world.getWidth() - 1 || doorY - j >= world.getHeight() - 1))
				{
					world.setLand(doorX+i,doorY-j,doorLand);
				}
			}
		}
		
		for(int j = 0 ; j < height; j++)
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
		}
		
	}
}
