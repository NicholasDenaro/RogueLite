package denaro.nick.worldgen;

import java.awt.Color;
import java.util.LinkedList;

public class Cave
{
	public static final char[]  CAVE_TYPES = {'L','l','m','M','t','T'};
	public static final double[]  CAVE_FREQS = {0.2,0.2,0.1,0.1,0.1,0.3};
	public static final Color[] CAVE_COLORS = {Color.black,World.DK_BROWN,World.BROWN,World.LT_BROWN,World.PINK,Color.white};
	
	public static enum Paths {STRAIGHT, L_LEFT, L_RIGHT, T, CROSS, STRAIGHT_LEFT, STRAIGHT_RIGHT};
	public static enum Direction {NORTH, WEST, SOUTH, EAST};
	public static final int[] HALL_LENGTHS = {5, 10, 20};
	public static final int[] HALL_WIDTHS = {1,3};//{1, 2, 3};
	public static final int[] ROOM_EXITS = {0, 1, 2, 3};
	public static final double[] ROOM_PERCENT = {0.9, 0.05, 0.03, 0.019, 0.001};
	public static final int[][] ROOMS = {
			null,
			{10,10},
			{10,40},
			{40,20},
			{80,80}
		};
	
	public Cave(World world, int x, int y)
	{
		//System.out.println("Cave: " + x + ", " + y);
		
		world.setConstruct(x, y, 'E');
		
		LinkedList<Paths> path = new LinkedList<Paths>();
		path.push(Paths.STRAIGHT);
		
		//Determine the direction to start in.
		
		int[] ord = null;
		
		for(int j = -1; j < 2 && ord == null; j++)
		{
			for(int i = -1; i < 2 && ord == null; i++)
			{
				if((i + j) % 2 != 0)
				{
					if(!world.isLandOfType(x + i, y + j, "mM"))
					{
						ord = new int[]{i, j};
					}
				}
			}
		}
		
		/*System.out.println(x + ", " + y);
		System.out.println(" " + world.getLand(x, y - 1));
		System.out.println(world.getLand(x - 1, y) + "" + world.getLand(x, y) + "" + world.getLand(x + 1, y));
		System.out.println(" " + world.getLand(x, y + 1));*/
		
		Direction d = turnLeft(turnLeft(ordinalsToDirection(ord)));
		
		LinkedList<Direction> dir = new LinkedList<Direction>();
		dir.push(d);
		
		
		buildPath(world, x - ord[0], y - ord[1], d, 25);
	}
	
	private void buildPath(World world, int x, int y, Direction direction, int depth)
	{
		if(depth == 0)
		{
			return;
		}
		
		int[] pos = new int[]{x,y};
		int[] split = new int[]{x,y};
		
		int[] room = randomRoom(world);
		
		if(room != null)
		{
			//int numExits = randomExits(world);
			
			carveRoom(world, direction, pos[0], pos[1], room[0], room[1]);
			return;
		}
		
		Paths path = randomPath(world);
		
		int length, width;
		
		switch(path)
		{
			default: // case STRAIGHT:
				length = randomLength(world);
				width = randomWidth(world);
				pos = carvePath(world, direction, pos[0], pos[1], length, width);
				if(pos != null)
				{
					buildPath(world,pos[0],pos[1],direction,depth - 1);
				}
				return;
			case L_LEFT:
				length = randomLength(world);
				width = randomWidth(world);
				pos = carvePath(world, direction, pos[0], pos[1], length, width);
				if(pos == null)
				{
					return;
				}
				pos = carvePath(world, turnLeft(direction), pos[0], pos[1], length, width);
				if(pos != null)
				{
					buildPath(world,pos[0],pos[1],turnLeft(direction),depth - 1);
				}
			return;
			case L_RIGHT:
				length = randomLength(world);
				width = randomWidth(world);
				pos = carvePath(world, direction, pos[0], pos[1], length, width);
				if(pos == null)
				{
					return;
				}
				pos = carvePath(world, turnRight(direction), pos[0], pos[1], length, width);
				if(pos != null)
				{
					buildPath(world,pos[0],pos[1],turnRight(direction),depth - 1);
				}
			return;
			case T:
				length = randomLength(world);
				width = randomWidth(world);
				split = carvePath(world, direction, pos[0], pos[1], length, width);
				if(split == null)
				{
					return;
				}
				
				pos = carvePath(world, turnLeft(direction), split[0], split[1], length, width);
				if(pos == null)
				{
					return;
				}
				buildPath(world,pos[0],pos[1],turnLeft(direction),depth - 1);
				
				pos = carvePath(world, turnRight(direction), split[0], split[1], length, width);
				if(pos == null)
				{
					return;
				}
				buildPath(world,pos[0],pos[1],turnRight(direction),depth - 1);
			return;
			case CROSS:
				length = randomLength(world);
				width = randomWidth(world);
				split = carvePath(world, direction, pos[0], pos[1], length, width);
				if(split == null)
				{
					return;
				}
				
				pos = carvePath(world, direction, split[0], split[1], length, width);
				if(pos == null)
				{
					return;
				}
				buildPath(world,pos[0],pos[1],direction,depth - 1);
				
				pos = carvePath(world, turnLeft(direction), split[0], split[1], length, width);
				if(pos == null)
				{
					return;
				}
				buildPath(world,pos[0],pos[1],turnLeft(direction),depth - 1);
				
				pos = carvePath(world, turnRight(direction), split[0], split[1], length, width);
				if(pos == null)
				{
					return;
				}
				buildPath(world,pos[0],pos[1],turnRight(direction),depth - 1);
			break;
			case STRAIGHT_LEFT:
				length = randomLength(world);
				width = randomWidth(world);
				split = carvePath(world, direction, pos[0], pos[1], length, width);
				if(split == null)
				{
					return;
				}
				
				pos = carvePath(world, direction, split[0], split[1], length, width);
				if(pos == null)
				{
					return;
				}
				buildPath(world,pos[0],pos[1],direction,depth - 1);
				
				pos = carvePath(world, turnLeft(direction), split[0], split[1], length, width);
				if(pos == null)
				{
					return;
				}
				buildPath(world,pos[0],pos[1],turnLeft(direction),depth - 1);
			return;
			case STRAIGHT_RIGHT:
				length = randomLength(world);
				width = randomWidth(world);
				split = carvePath(world, direction, pos[0], pos[1], length, width);
				if(split == null)
				{
					return;
				}
				
				pos = carvePath(world, direction, split[0], split[1], length, width);
				if(pos == null)
				{
					return;
				}
				buildPath(world,pos[0],pos[1],direction,depth - 1);
				
				pos = carvePath(world, turnRight(direction), split[0], split[1], length, width);
				if(pos == null)
				{
					return;
				}
				buildPath(world,pos[0],pos[1],turnRight(direction),depth - 1);
			return;
		}
	}
	
	private int[] carvePath(World world, Direction direction, int x, int y, int length, int width)
	{
		return carve(world, direction, x, y, length, width, true);
	}
	
	private int[] carveRoom(World world, Direction direction, int x, int y, int length, int width)
	{
		return carve(world, direction, x, y, length, width, false);
	}
	
	private int[] carve(World world, Direction direction, int x, int y, int length, int width, boolean quit)
	{
		/*System.out.println("Carving:");
		System.out.println(x+", "+y);
		System.out.println(direction);
		System.out.println("length: "+length+", width: "+width);*/
		
		
		int[] ords = directionToOrdinals(direction);
		int len = ords[0] == 0 ? 1 : 0;
		int wid = ords[0] == 0 ? 0 : 1;
		
		boolean exit = false;
		
		//System.out.println("len: "+len+", wid: "+wid);
		for(int w = -width / 2; w < width / 2 + 1; w++)
		{
			for(int l = 0; l < length; l++)
			{
				int xx = x + (len == 0 ? ords[len] * l : w);
				int yy = y + (len == 0 ? w : ords[len] * l);
				
				//System.out.println("c "+xx+", "+yy);
				
				if(world.isInBounds(xx, yy, 1))
				{
					if(world.isLandOfType(xx, yy, "mM") && (!world.isConstructOfType(xx, yy, "CE") || world.isLandOfType(xx, yy, "M")) && !world.isConstructOfType(xx, yy, "c"))
					{
						world.setConstruct(xx, yy, 'c');
					}
					else if(world.isLandOfType(xx, yy, "m") && world.isConstructOfType(xx, yy, "C")/* && width == 1*/)
					{
						world.setConstruct(xx, yy, 'E');
						//return null;//new int[]{x + (len == 0 ? ords[len] * l : 0), y + (len == 0 ? 0 : ords[len] * l)};
						exit = true;
						continue;
					}
					else if(quit)
					{
						if(l > HALL_WIDTHS[HALL_WIDTHS.length - 1])
						{
							exit = true;
							continue;
							//return null; // we ran into the a cave
						}
					}
				}
			}
		}
		
		//System.out.println();
		
		if(exit)
		{
			return null;
		}
		
		return new int[]{x + (len == 0 ? ords[len] * length : 0), y + (len == 0 ? 0 : ords[len] * length)};
	}
	
	public int[] directionToOrdinals(Direction direction)
	{
		switch(direction)
		{
			case NORTH:
				return new int[]{0,-1};
			case WEST:
				return new int[]{-1,0};
			case SOUTH:
				return new int[]{0,1};
			case EAST:
				return new int[]{1,0};
			default:
				return new int[]{0,0};
		}
	}
	
	public Direction ordinalsToDirection(int[] ord)
	{
		if(ord[0] == 0 && ord[1] == -1)
			return Direction.NORTH;
		if(ord[0] == -1 && ord[1] == 0)
			return Direction.WEST;
		if(ord[0] == 0 && ord[1] == 1)
			return Direction.SOUTH;
		if(ord[0] == 1 && ord[1] == 0)
			return Direction.EAST;
		
		return null;
	}
	
	public Direction turnLeft(Direction d)
	{
		return turn(d,-1);
	}
	
	public Direction turnRight(Direction d)
	{
		return turn(d,1);
	}
	
	private Direction turn(Direction d, int way)
	{
		int index = -1;
		for(int i = 0; i < Direction.values().length && index == -1; i++)
		{
			if(Direction.values()[i] == d)
			{
				index = i;
			}
		}
		
		index = (index + way + Direction.values().length) % Direction.values().length;
		
		return Direction.values()[index];
	}
	
	private int randomExits(World world)
	{
		return ROOM_EXITS[world.rand.nextInt(ROOM_EXITS.length)];
	}
	
	private int[] randomRoom(World world)
	{
		double r = world.rand.nextDouble();
		
		double t = 0;
		int i = 0;
		
		while(i < ROOMS.length && (t+=ROOM_PERCENT[i]) < 1)
		{
			if(r < t)
			{
				return ROOMS[i];
			}
			i++;
		}
		
		return null;
	}
	
	private Paths randomPath(World world)
	{
		return Paths.values()[world.rand.nextInt(Paths.values().length)];
	}
	
	private int randomLength(World world)
	{
		return HALL_LENGTHS[world.rand.nextInt(HALL_LENGTHS.length)];
	}
	
	private int randomWidth(World world)
	{
		return HALL_WIDTHS[world.rand.nextInt(HALL_WIDTHS.length)];
	}
}
