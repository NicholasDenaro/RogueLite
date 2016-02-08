package denaro.nick.worldgen;

import java.util.LinkedList;

public class Cave
{
	public static enum Paths {STRAIGHT, L_LEFT, L_RIGHT, T, CROSS, STRAIGHT_LEFT, STRAIGHT_RIGHT};
	public static enum Direction {NORTH, WEST, SOUTH, EAST};
	public static final int[] HALL_LENGTHS = {5, 8, 12};
	public static final int[] HALL_WIDTHS = {1, 2, 3};
	public static final int[] ROOM_EXISTS = {0, 1, 2, 3};
	public static enum HallsEnd {DOOR, OPEN, SECRET};
	public static final int[][] ROOMS = {
			{10,10},
			{10,40},
			{40,30}
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
		
		buildPath(world, x - ord[0], y - ord[1], path, dir, 100);
	}
	
	private void buildPath(World world, int x, int y, LinkedList<Paths> path, LinkedList<Direction> direction, int depth)
	{
		if(depth == 0 || path.isEmpty())
		{
			return;
		}
		
		int[] length = new int[0];
		int[] width = new int[0];
		
		Paths currentPath = path.pop();
		Direction currentDireciton = direction.pop();
		
		switch(currentPath)
		{
			default: // case STRAIGHT:
				length = new int[1];
				width = new int[1];
			break;
			case L_LEFT:
				length = new int[2];
				width = new int[1];
			break;
			case L_RIGHT:
				length = new int[2];
				width = new int[1];
			break;
			case T:
				length = new int[3];
				width = new int[1];
			break;
			case CROSS:
				length = new int[4];
				width = new int[1];
			break;
		}
		
		for(int i = 0; i < length.length; i++)
		{
			length[i] = randomLength(world);
		}
		for(int i = 0; i < width.length; i++)
		{
			width[i] = randomWidth(world);
		}
		
		int[] pos = new int[]{x,y};
		switch(currentPath)
		{
			default: // case STRAIGHT:
				pos = carvePath(world, currentDireciton, pos[0], pos[1], length[0], width[0]);
			break;
			case L_LEFT:
				pos = carvePath(world, currentDireciton, pos[0], pos[1], length[0], width[0]);
				currentDireciton = turnLeft(currentDireciton);
				pos = carvePath(world, currentDireciton, pos[0], pos[1], length[1], width[0]);
			break;
			case L_RIGHT:
				pos = carvePath(world, currentDireciton, pos[0], pos[1], length[0], width[0]);
				currentDireciton = turnRight(currentDireciton);
				pos = carvePath(world, currentDireciton, pos[0], pos[1], length[1], width[0]);
			break;
			case T:
				pos = carvePath(world, currentDireciton, pos[0], pos[1], length[0], width[0]);
				
				path.push(randomPath(world));
				direction.push(turnLeft(currentDireciton));
				
				currentDireciton = turnRight(currentDireciton);
				pos = carvePath(world, currentDireciton, pos[0], pos[1], length[1], width[0]);
			break;
			case CROSS:
				pos = carvePath(world, currentDireciton, pos[0], pos[1], length[0], width[0]);
				
				path.push(randomPath(world));
				direction.push(currentDireciton);
				
				path.push(randomPath(world));
				direction.push(turnLeft(currentDireciton));
				
				currentDireciton = turnRight(currentDireciton);
				pos = carvePath(world, currentDireciton, pos[0], pos[1], length[1], width[0]);
			break;
		}
		
		
		if(world.isInBounds(pos[0], pos[1]) && !world.isLandOfType(pos[0], pos[1], "mM"))
		{
			return;
		}
		
		Paths next = randomPath(world);
		
		path.push(next);
		direction.push(currentDireciton);
		
		buildPath(world, pos[0], pos[1], path, direction, depth - 1);
	}
	
	private int[] carvePath(World world, Direction direction, int x, int y, int length, int width)
	{
		/*System.out.println("Carving:");
		System.out.println(x+", "+y);
		System.out.println(direction);
		System.out.println("length: "+length+", width: "+width);*/
		
		
		int[] ords = directionToOrdinals(direction);
		int len = ords[0] == 0 ? 1 : 0;
		int wid = ords[0] == 0 ? 0 : 1;
		
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
					if(world.isLandOfType(xx, yy, "mM") && (!world.isConstructOfType(xx, yy, "C") || world.isLandOfType(xx, yy, "M")))
					{
						world.setConstruct(xx, yy, 'c');
					}
					else if(world.isLandOfType(xx, yy, "m") && world.isConstructOfType(xx, yy, "C") && width == 1)
					{
						world.setConstruct(xx, yy, 'E');
						return new int[]{x + (len == 0 ? ords[len] * l : 0), y + (len == 0 ? 0 : ords[len] * l)};
					}
				}
			}
		}
		
		//System.out.println();
		
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
