package denaro.nick.worldgen;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Random;
import java.util.function.IntBinaryOperator;

public class World
{
	public static final char[] LAND_TYPES = new char[]{'o', 'w', 'b', 'p', 'P',
			'h', 'H', 'm', 'M'};
	public static final double[] NORMAL_WORLD = new double[]{0.2, 0.1, 0.0, 0.1,
			0.1, 0.1, 0.1, 0.1, 0.2};
	public static final double[] MOUNTAIN_WORLD = new double[]{0.1, 0.0, 0.0,
			0.0, 0.0, 0.0, 0.1, 0.2, 0.6};
	public static final Color[] COLORS = new Color[]{Color.blue,
			Color.cyan.darker(), Color.orange,
			Color.green.darker().darker().darker(),
			Color.green.darker().darker(), Color.green.darker(), Color.green,
			Color.gray.darker(), Color.gray};
	
	public static final char[]  ROAD_TYPES = {'1','2','3','4','5','6'};
	public static final double[]  ROAD_FREQS = {0.4,0.0,0.075,0.05,0.075,0.4};
	public static final Color[] ROAD_COLORS = {Color.black,World.DK_BROWN,World.BROWN,Color.lightGray,World.PINK,Color.white};
	
	public static final int NUM_VILLAGES = 1;

	public static final double[] WORLD_TYPE = NORMAL_WORLD;

	public static final Color PINK = mixColor(Color.pink,
			Color.magenta.darker(), 0.05);
	public static final Color TEAL = mixColor(Color.cyan, Color.green, 0.66);
	public static final Color SCARLET = mixColor(Color.red, Color.orange, 0.2);
	public static final Color LT_BROWN = mixColor(Color.magenta, SCARLET, 0.93);
	public static final Color BROWN = mixColor(Color.magenta, SCARLET, 0.93)
			.darker();
	public static final Color DK_BROWN = mixColor(Color.magenta, SCARLET, 0.93)
			.darker().darker();
	public static final Color TAN = mixColor(Color.white, BROWN, 0.9);

	public static final int STARTING_SIZE = 4;
	public static final String WORLD_GEN = "rddsddsddssdsdsssss";
	public static final String BIOME_GEN = "rdsdszddssdsdsssssZ";
	public static final String BIG_BIOME_GEN = "rdszdsdsdsddssdsdsssssZ";
	public static final String CAVE_GEN = "rdddsdsssss";
	public static final String ROAD_GEN = "rdddsdsssss";

	public Random rand;
	private long seed;

	private String generation;
	private char[] landTypes;
	private double[] freqs;
	private Color[] colors;
	private World biome;
	private World cave;
	private World road;

	private char[][] land;

	private int startSize;
	private int width;
	private int height;

	private char[][] constructs;

	private int[][] zones;
	private int numZones;
	
	private int numVillages;
	private int minVillageRadius;
	private int varVillageRadius;

	public static void main(String[] args)
	{
		World world = new World(STARTING_SIZE, WORLD_GEN, LAND_TYPES, WORLD_TYPE, COLORS, NUM_VILLAGES, Village.VILLAGE_TYPE.minRadius, Village.VILLAGE_TYPE.varRadius);

		world.generate();

		new WorldFramer(world);
	}

	public World(int startSize, String technique, char[] landTypes,
			double[] frequencies, Color[] colors, int numVillages, int minVillageRadius, int varVillageRadius)
	{
		this.startSize = startSize;
		this.width = startSize;
		this.height = startSize;
		this.generation = technique;
		this.landTypes = landTypes;
		this.freqs = frequencies;
		this.colors = colors;
		this.land = new char[this.width][this.height];
		this.numVillages = numVillages;
		this.minVillageRadius = minVillageRadius;
		this.varVillageRadius = varVillageRadius;
		zones = null;// new int[this.width][this.height];

		seed = System.nanoTime();
		rand = new Random(seed * 492876847);

		for(int i = 0; i < technique.length(); i++)
		{
			switch(technique.charAt(i))
			{
			case 'r':
				fillRandom();
				break;
			case 'd':
				divide();
				break;
			case 'z':
				createZones();
				break;
			case 'Z':
				condenseZones();
				break;
			case 's':
				smooth();
				smoothZones();
				break;
			default:
				break;
			}
		}

		constructs = new char[this.width][this.height];
	}
	
	private int calcWorldSizeDiff(int startSize, String gen1, String gen2)
	{
		int count = 0;
		for(int i = 0; i < gen1.length(); i++)
		{
			if(gen1.charAt(i) == 'd')
			{
				count++;
			}
		}
		for(int i = 0; i < gen2.length(); i++)
		{
			if(gen2.charAt(i) == 'd')
			{
				count--;
			}
		}
		
		return (int) (count > 0 ? startSize * Math.pow(2, count) : startSize / Math.pow(2, count));
	}

	public void generate()
	{
		System.out.println("Generating biomes");
		generateBiomes();

		System.out.println("Generating caves");
		generateCaves();

		System.out.println("Generating roads");
		generateRoads();
		
		System.out.println("Populating world");
		populate();

		// createZones();
	}

	public void generateBiomes()
	{
		biome = new World(calcWorldSizeDiff(startSize,generation,BIOME_GEN), BIOME_GEN, Biomes.BIOME_TYPES,
				Biomes.BIOME_FREQS, Biomes.BIOME_COLORS, 0, 0, 0);
		// biome=new World(4,4,BIG_BIOME_GEN,Biomes.BIOME_TYPES,
		// Biomes.BIOME_FREQS, Biomes.BIOME_COLORS);
		System.out.println("Number of zones: " + biome.countZones());
	}

	private int countZones()
	{
		LinkedList<Integer> seen = new LinkedList<Integer>();
		for(int h = 0; h < height; h++)
		{
			for(int w = 0; w < width; w++)
			{
				if(!seen.contains(zones[w][h]))
				{
					seen.push(zones[w][h]);
				}
			}
		}
		return seen.size();
	}

	public void generateCaves()
	{
		cave = new World(calcWorldSizeDiff(startSize,generation,CAVE_GEN), CAVE_GEN, Cave.CAVE_TYPES, Cave.CAVE_FREQS,
				Cave.CAVE_COLORS, 0, 0, 0);
	}
	
	public void generateRoads()
	{
		road = new World(calcWorldSizeDiff(startSize,generation,CAVE_GEN), ROAD_GEN, ROAD_TYPES, ROAD_FREQS,
				ROAD_COLORS, 0, 0, 0);
	}

	public void populate()
	{		
		createVillages();

		createCliffs();
		
		createCaveEntrances();

		createTrees(new char[]{'D', 'N', 'F', 'J'},
				new double[]{0, 0.0005, 0.05, 0.2});
		
		removeRoadsOutsideVillages();
		
		createStairs();
		
		removeVillageOutlines();
	}

	public void createVillages()
	{
		//int numPlains = countLandType('p') + countLandType('h');

		//while(numPlains * 1.0 / this.width / this.height > villageRatio)
		for(int i = 0; i < numVillages; i++)
		{
			int w = 0;
			int h = 0;
			w = rand.nextInt(this.width);
			h = rand.nextInt(this.height);
			while(land[w][h] != 'P' && land[w][h] != 'h')
			{
				w = rand.nextInt(this.width);
				h = rand.nextInt(this.height);
			}
			new Village(this, w, h, Village.VILLAGE_TYPE);
			//numPlains -= villageRatio * this.width * this.height;
		}
	}
	
	public void removeRoadsOutsideVillages()
	{
		for(int h = 0; h < height; h++)
		{
			for(int w = 0; w < width; w++)
			{
				if(!isConstructOfType(w, h, "VFWDC"))
					road.land[w][h] = (char) 0;
			}
		}
	}
	
	public void removeVillageOutlines()
	{
		for(int h = 0; h < height; h++)
		{
			for(int w = 0; w < width; w++)
			{
				if(constructs[w][h] == 'V')
					constructs[w][h] = (char) 0;
			}
		}
	}

	public void createCliffs()
	{
		for(int h = 0; h < height; h++)
		{
			for(int w = 0; w < width; w++)
			{
				char center = land[w][h];
				int ind = "pPhHmM".indexOf(center);
				if(ind != -1)
				{
					for(int j = -1; j < 2; j++)
					{
						for(int i = -1; i < 2; i++)
						{
							if(w + i < 0 || w + i >= width || h + j < 0
									|| h + j >= height)
								continue;
							if(ind > "pPhHmM".indexOf(land[w + i][h + j]))
							{
								constructs[w][h] = 'C';
							}
						}
					}
				}
				if(w == 0 || h == 0 || w == width - 1 || h == height - 1)
				{
					if("ow".indexOf(land[w][h]) != -1)
					{
						constructs[w][h] = 'R';
					}
					else
					{
						constructs[w][h] = 'C';
					}
				}
			}
		}
	}

	public boolean[][] cliffStairAdjacency(int w, int h)
	{
		boolean[][] c = new boolean[3][3];
		for(int j = -1; j < 2; j++)
		{
			for(int i = -1; i < 2; i++)
			{
				if(!isInBounds(w + i, h + j))
				{
					continue;
				}
				char constr = constructs[w + i][h + j];
				c[i + 1][j + 1] = (constr == 'C' || constr == 'S') && (land[w][h] != 'm' && land[w][h] != 'M');
			}
		}
		
		return c;
	}
	
	public boolean[][] roadAdjacency(int w, int h)
	{
		boolean[][] c = new boolean[3][3];
		for(int j = -1; j < 2; j++)
		{
			for(int i = -1; i < 2; i++)
			{
				if(!isInBounds(w + i, h + j))
				{
					continue;
				}
				char constr = road.land[w + i][h + j];
				c[i + 1][j + 1] = constr == '4';
			}
		}
		
		return c;
	}
	
	public boolean[][] villageAdjacency(int w, int h)
	{
		boolean[][] c = new boolean[3][3];
		for(int j = -1; j < 2; j++)
		{
			for(int i = -1; i < 2; i++)
			{
				if(!isInBounds(w + i, h + j))
				{
					continue;
				}
				char constr = constructs[w + i][h + j];
				c[i + 1][j + 1] = constr == 'V';
			}
		}
		
		return c;
	}
	
	public void createCaveEntrances()
	{
		LinkedList<Integer> cliffs = new LinkedList<Integer>();
		//gather the cliffs
		for(int h = 1; h < height - 1; h++)
		{
			for(int w = 1; w < width - 1; w++)
			{
				if(cave.land[w][h] != 'M' || land[w][h] != 'm')
				{
					continue;
				}
				boolean[][] c = new boolean[3][3];
				for(int j = -1; j < 2; j++)
				{
					for(int i = -1; i < 2; i++)
					{
						if(!isInBounds(w + i, h + j))
						{
							continue;
						}
						char constr = constructs[w + i][h + j];
						c[i + 1][j + 1] = constr == 'C';
					}
				}
				
				boolean[][] c2 = new boolean[3][3];
				for(int j = -1; j < 2; j++)
				{
					for(int i = -1; i < 2; i++)
					{
						if(!isInBounds(w + i, h + j))
						{
							continue;
						}
						c2[i + 1][j + 1] = land[w + i][h + j] == 'm' && cave.isLandOfType(w + i, h + j, "mM");
					}
				}
				
				if((c[1][0] && c[1][1] && c[1][2] && !c[0][1] && !c[2][1] && (c2[0][1] || c2[2][1]))
					|| (c[0][1] && c[1][1] && c[2][1] && !c[1][0] && !c[1][2] && (c2[1][0] || c2[1][2])))
				{
					//constructs[w][h] = 'S';
					cliffs.push(w + h * width);
				}
			}
		}
		
		//select some to place entrances
		System.out.println("number of places: " + cliffs.size());
		
		if(cliffs.isEmpty())
		{
			return;
		}
		
		int minStairs = (int) (cliffs.size() * 0.1);
		
		int stairCount = minStairs + rand.nextInt((int) (minStairs * 0.5));
		
		LinkedList<Integer> stairs = new LinkedList<Integer>();
		
		for(int i = 0; i < stairCount; i++)
		{
			int c = rand.nextInt(cliffs.size());
			
			int pos = cliffs.remove(c);
			constructs[pos % width][pos / width] = 'E';
			stairs.push(pos);
		}
		
		//TODO: search for caves, that may be inaccessible without a new entrance and add a path to it
		
	}
	
	public void createStairs()
	{
		for(int h = 1; h < height - 1; h++)
		{
			for(int w = 1; w < width - 1; w++)
			{
				if(constructs[w][h] == 'C' && road.land[w][h] =='4')
				{
					constructs[w][h] = (char) 0;
					boolean[][] v = villageAdjacency(w, h);
					boolean adjacent = false;
					for(int i = 0 ; i < 9 && !adjacent; i++)
					{
						if(v[i % 3][i / 3])
						{
							adjacent = true;
						}
					}
					
					if(!adjacent)
					{
						road.land[w][h] = (char) 0;
					}
				}
			}
		}
	}
	
	public void createStairsOLD()
	{
		LinkedList<Integer> cliffs = new LinkedList<Integer>();
		//gather the cliffs
		for(int h = 1; h < height - 1; h++)
		{
			for(int w = 1; w < width - 1; w++)
			{
				boolean[][] c = cliffStairAdjacency(w, h);
				
				if((c[1][0] && c[1][1] && c[1][2] && !c[0][1] && !c[2][1])
					|| (c[0][1] && c[1][1] && c[2][1] && !c[1][0] && !c[1][2]))
				{
					//constructs[w][h] = 'S';
					cliffs.push(w + h * width);
				}
			}
		}
		
		//select some to place stairs
		
		int minStairs = (int) (cliffs.size() * 0.1);
		
		int stairCount = minStairs + rand.nextInt((int) (minStairs * 0.5));
		
		LinkedList<Integer> stairs = new LinkedList<Integer>();
		
		for(int i = 0; i < stairCount; i++)
		{
			int c = rand.nextInt(cliffs.size());
			
			int pos = cliffs.remove(c);
			constructs[pos % width][pos / width] = 'S';
			stairs.push(pos);
		}
		
		//expand some of the stairs if possible
		while(!stairs.isEmpty())
		{
			int pos = stairs.pop();
			int w = pos % width;
			int h = pos / width;
			
			boolean[][] c = cliffStairAdjacency(w, h);
			
			if(c[1][0] && c[1][1] && c[1][2] && !c[0][1] && !c[2][1]) // |
			{
				boolean[][] cUp = cliffStairAdjacency(w, h - 1);
				boolean[][] cDown = cliffStairAdjacency(w, h + 1);
				
				if(cUp[1][0] && cUp[1][1] && cUp[1][2] && !cUp[0][1] && !cUp[2][1]) // |
				{
					constructs[w][h - 1] = 'S';
				}
				if(cDown[1][0] && cDown[1][1] && cDown[1][2] && !cDown[0][1] && !cDown[2][1]) // |
				{
					constructs[w][h + 1] = 'S';
				}
			}
			else if(c[0][1] && c[1][1] && c[2][1] && !c[1][0] && !c[1][2]) // -
			{
				boolean[][] cLeft = cliffStairAdjacency(w - 1, h);
				boolean[][] cRight = cliffStairAdjacency(w + 1, h);
				
				if(cLeft[0][1] && cLeft[1][1] && cLeft[2][1] && !cLeft[1][0] && !cLeft[1][2]) // -
				{
					constructs[w - 1][h] = 'S';
				}
				if(cRight[0][1] && cRight[1][1] && cRight[2][1] && !cRight[1][0] && !cRight[1][2]) // -
				{
					constructs[w + 1][h] = 'S';
				}
			}
			
			constructs[pos % width][pos / width] = 'S';
		}
		
		
		//TODO: search for places, like houses, that may be inaccessible without stairs and add a path to it
		
	}

	public void condenseZones()
	{
		LinkedList<Integer> currentZones = new LinkedList<Integer>();

		// collect zones
		for(int h = 0; h < height; h++)
		{
			for(int w = 0; w < width; w++)
			{
				if(!currentZones.contains(zones[w][h]))
				{
					//System.out.println("found zone: " + zones[w][h]);
					currentZones.push(zones[w][h]);
				}
			}
		}
		int replace = 1;

		while(!currentZones.isEmpty())
		{
			int z = currentZones.pollLast();

			for(int h = 0; h < height; h++)
			{
				for(int w = 0; w < width; w++)
				{
					if(zones[w][h] == z)
					{
						zones[w][h] = replace;
					}
				}
			}
			replace++;
		}
		numZones = replace - 1;

		/*for(int h = 0; h < height; h++)
		{
			for(int w = 0; w < width; w++)
			{
				if(!currentZones.contains(zones[w][h]))
				{
					currentZones.push(zones[w][h]);
				}
			}
		}
		while(!currentZones.isEmpty())
		{
			System.out.println("new zone: " + currentZones.pop());
		}*/
	}

	public void createZones()
	{
		zones = new int[width][height];
		int zoneCounter = 1;
		boolean finishZone = false;
		// while(!filledWithZones())
		{
			// finishZone = false;
			for(int h = 0; h < height && !finishZone; h++)
			{
				for(int w = 0; w < width && !finishZone; w++)
				{
					if(zones[w][h] == 0)
					{
						zones[w][h] = zoneCounter;
						spreadZone(w, h, land[w][h], zoneCounter);
						// finishZone = true;
						System.out.println("Finished zone: " + zoneCounter);
						zoneCounter++;
					}
				}
			}
			// System.out.println("Finished zone: "+zoneCounter);
			// zoneCounter++;
		}
		System.out.println("Zones: " + zoneCounter);
		numZones = zoneCounter - 1;
	}

	private boolean filledWithZones()
	{
		for(int h = 0; h < height; h++)
		{
			for(int w = 0; w < width; w++)
			{
				if(zones[w][h] == 0 && !isLandOfType(w, h, "owbmM"))
				{
					return false;
				}
			}
		}

		return true;
	}

	private int spreadZone(int x, int y, char b, int z)
	{
		boolean changed = true;
		int count = 0;

		while(changed)
		{
			changed = false;
			for(int h = 0; h < height; h++)
			{
				for(int w = 0; w < width; w++)
				{
					if(zones[w][h] == 0 && land[w][h] == b)
					{
						if(isZoneNextTo(w, h, z) && isNextTo(land, w, h, b))
						{
							zones[w][h] = z;
							changed = true;
							count++;
						}
					}
				}
			}
		}
		System.out.println("Filled " + count + " for zone " + z);
		return count;
	}

	public void createCaves()
	{
		double caveRatio = 0.3;

		int count = 10;

		while(countConstructType('c') * 1.0
				/ (countLandType('m') + countLandType('M')) < caveRatio
				&& count-- > 0)
		{
			LinkedList<Integer> cliffBlocks = new LinkedList<Integer>();
			for(int h = 1; h < height - 1; h++)
			{
				for(int w = 1; w < width - 1; w++)
				{
					boolean valid = true;

					char north = constructs[w][h - 1];
					char west = constructs[w - 1][h];
					char south = constructs[w][h + 1];
					char east = constructs[w + 1][h];

					if((north == east && north == 'C')
							|| (north == west && north == 'C')
							|| (south == east && south == 'C')
							|| (south == west && south == 'C'))
					{
						valid = false;
					}

					if(valid)
					{
						int[] ord = null;
						for(int j = -1; j < 2 && ord == null; j++)
						{
							for(int i = -1; i < 2 && ord == null; i++)
							{
								if((i + j) % 2 != 0)
								{
									if(!isLandOfType(w + i, h + j, "mM"))
									{
										if("m".indexOf(land[w][h]) != -1
												&& "C".indexOf(
														constructs[w][h]) != -1)
										{
											cliffBlocks.add(w + h * width);
										}
									}
								}
							}
						}
					}
				}
			}

			int r = rand.nextInt(cliffBlocks.size());

			int block = cliffBlocks.get(r);

			new Cave(this, block % width, block / width);
		}

	}

	public void createTrees(char[] terrain, double[] percent)
	{
		for(int t = 0; t < terrain.length; t++)
		{
			LinkedList<Integer> biomeBlocks = new LinkedList<Integer>();

			for(int h = 0; h < height; h++)
			{
				for(int w = 0; w < width; w++)
				{
					if(biome.land[w][h] == terrain[t]
							&& "owbmM".indexOf(land[w][h]) == -1
							&& "VWFDC".indexOf(constructs[w][h]) == -1)
					{
						biomeBlocks.add(w + h * width);
					}
				}
			}

			int count = (int) (biomeBlocks.size() * percent[t]);
			System.out.println("Adding " + count + " trees.");

			long start = System.currentTimeMillis();
			for(int c = 0; c < count; c++)
			{
				int r = rand.nextInt(biomeBlocks.size());

				int block = biomeBlocks.get(r);

				int x = block % width;
				int y = block / width;

				biomeBlocks.remove(r);

				constructs[x][y] = 'T';

			}
			System.out.println("Time to fill " + terrain[t] + ": "
					+ (System.currentTimeMillis() - start) + "ms");
		}
	}

	public boolean isZoneNextTo(int w, int h, int z)
	{
		return (isInBounds(w, h - 1) && zones[w][h - 1] == z)
				|| (isInBounds(w - 1, h) && zones[w - 1][h] == z)
				|| (isInBounds(w, h + 1) && zones[w][h + 1] == z)
				|| (isInBounds(w + 1, h) && zones[w + 1][h] == z);
	}

	public boolean isBiomeNextTo(int w, int h, char l)
	{
		return isNextTo(biome.land, w, h, l);
	}

	public boolean isNextTo(char[][] type, int w, int h, char l)
	{
		return (isInBounds(w, h - 1) && type[w][h - 1] == l)
				|| (isInBounds(w - 1, h) && type[w - 1][h] == l)
				|| (isInBounds(w, h + 1) && type[w][h + 1] == l)
				|| (isInBounds(w + 1, h) && type[w + 1][h] == l);
	}

	public int countLandType(char l)
	{
		return countType(land, l);
	}

	public int countConstructType(char l)
	{
		return countType(constructs, l);
	}

	public int countType(char[][] type, char l)
	{
		int count = 0;
		for(int h = 0; h < height; h++)
		{
			for(int w = 0; w < width; w++)
			{
				if(type[w][h] == l)
					count++;
			}
		}
		return count;
	}

	public void setLand(int w, int h, char l)
	{
		land[w][h] = l;
	}

	public void setConstruct(int w, int h, char l)
	{
		constructs[w][h] = l;
	}

	public Color getLandColor(int w, int h)
	{
		return getLandColor(land[w][h]);
	}

	public Color getConstructColor(int w, int h)
	{
		return getLandColor(constructs[w][h]);
	}

	public Color getBiomeColor(int w, int h)
	{
		if(isLandOfType(w, h, "owbmM"))
		{
			return Color.magenta;
		}
		return getBiomeColor(biome.land[w][h]);
	}

	public Color getBiomeColor(char l)
	{
		for(int i = 0; i < biome.landTypes.length; i++)
		{
			if(biome.landTypes[i] == l)
				return biome.colors[i];
		}
		return Color.magenta;
	}
	
	public Color getRoadColor(int w, int h)
	{
		if(!isLandOfType(w, h, "owbmM") && road.isLandOfType(w, h, "4"))
		{
			return getRoadColor(road.land[w][h]);
		}
		else
		{
			return Color.magenta;
		}
	}
	
	public Color getRoadColor(char l)
	{
		for(int i = 0; i < road.landTypes.length; i++)
		{
			if(road.landTypes[i] == l)
				return road.colors[i];
		}
		return Color.magenta;
	}


	public Color getCaveColor(int w, int h)
	{
		if(isLandOfType(w, h, "mM") && isCaveOfType(w, h, "mM"))
		{
			return getCaveColor(cave.land[w][h]);
		}
		else
		{
			return Color.magenta;
		}
	}

	public Color getCaveColor(char l)
	{
		for(int i = 0; i < cave.landTypes.length; i++)
		{
			if(cave.landTypes[i] == l)
				return cave.colors[i];
		}
		return Color.magenta;
	}

	public Color getLandColor(char l)
	{
		for(int i = 0; i < landTypes.length; i++)
		{
			if(landTypes[i] == l)
				return colors[i];
		}

		if(l == 'F')
			return Color.magenta.darker();
		if(l == 'W')
			return Color.orange.brighter();
		if(l == 'D')
			return Color.red;
		if(l == 'T')
			return DK_BROWN;
		if(l == 'V')
			return PINK;
		if(l == 'C')
			return DK_BROWN;
		if(l == 'S')
			return Color.white;
		if(l == 'c')
			return PINK;
		if(l == 'R')
			return TEAL;
		if(l == 'E')
			return Color.white;

		return Color.magenta;
	}

	public Color getZoneColor(int w, int h)
	{
		double percent = (biome.zones[w][h] - 1) * 1.0 / biome.numZones * 4;
		if(biome.zones[w][h] == 0)
		{
			return Color.magenta;
		}
		if(percent < 0.5)
		{
			return mixColor(Color.red, Color.green, percent / 0.5);
		}
		else
		{
			return mixColor(Color.green, Color.blue, (percent - 0.5) / 0.5);
		}
	}

	public static Color mixColor(Color c1, Color c2, double weight)
	{
		float[] hsb1 = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(),
				null);
		float[] hsb2 = Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(),
				null);
		float[] hsbMix = new float[]{
				(float) (hsb1[0] * (1 - weight) + hsb2[0] * weight),
				(float) (hsb1[1] * (1 - weight) + hsb2[1] * weight),
				(float) (hsb1[2] * (1 - weight) + hsb2[2] * weight)};

		return new Color(Color.HSBtoRGB(hsbMix[0], hsbMix[1], hsbMix[2]));
	}

	private void fillRandom()
	{
		applyAll((int w, int h) -> {
			// land[w][h] = rand.nextInt(percentLand+percentWater) < percentLand
			// ? types[types.length - 1] : types[0];
			double p = rand.nextDouble();
			double c = 0;
			for(int i = 0; i < freqs.length; i++)
			{
				p -= freqs[i];
				if(p <= 0)
				{
					land[w][h] = landTypes[i];
					return 0;
				}
			}
			return 0;
		});
	}

	private void applyAll(IntBinaryOperator f)
	{
		for(int h = 0; h < height; h++)
		{
			for(int w = 0; w < width; w++)
			{
				f.applyAsInt(w, h);
			}
		}
	}

	public void smooth()
	{
		char[][] temp = new char[width][height]; // Remove the temp for the
													// maze;

		applyAll((int w, int h) -> {
			temp[w][h] = land[w][h];
			return 0;
		});

		applyAll((int w, int h) -> {
			double[] count = new double[landTypes.length];
			int total = 0;
			for(int j = -1; j < 2; j++)
			{
				for(int i = -1; i < 2; i++)
				{
					if(!(w + i < 0 || h + j < 0 || w + i >= width
							|| h + j >= height /* || (i == j && i == 0) */))
					{
						// count += temp[w+i][h+j] == 'l' ? 1 : 0;
						count[indexOfType(temp[w + i][h + j])]++;
						// count += land[w+i][h+j] == 'l' ? 1 : 0; // eh
						total++;
					}
				}
			}

			double avg = 0;
			for(int i = 0; i < landTypes.length; i++)
			{
				avg += count[i] / total * i;
			}

			land[w][h] = landTypes[(int) Math.round(avg)];

			return 0;
		});
	}

	public void smoothZones()
	{
		if(zones == null)
		{
			return;
		}
		int[][] temp = new int[width][height]; // Remove the temp for the maze;

		applyAll((int w, int h) -> {
			temp[w][h] = zones[w][h];
			return 0;
		});

		applyAll((int w, int h) -> {
			double[] count = new double[numZones];
			int total = 0;
			for(int j = -1; j < 2; j++)
			{
				for(int i = -1; i < 2; i++)
				{
					if(!(w + i < 0 || h + j < 0 || w + i >= width
							|| h + j >= height /* || (i == j && i == 0) */))
					{
						// count += temp[w+i][h+j] == 'l' ? 1 : 0;
						count[temp[w + i][h + j] - 1]++;
						// count += land[w+i][h+j] == 'l' ? 1 : 0; // eh
						total++;
					}
				}
			}

			int max = 0;
			for(int i = 0; i < numZones; i++)
			{
				if(count[i] > count[max])
					max = i;
			}

			zones[w][h] = max + 1;

			return 0;
		});
	}

	private int indexOfType(char l)
	{
		for(int i = -0; i < landTypes.length; i++)
			if(landTypes[i] == l)
				return i;
		return -1;
	}

	private double[] smoothDensity(double[] density)
	{
		double[] smoother = {0.1, 0.8, 0.1};
		double[] out = new double[density.length];
		for(int i = 0; i < out.length; i++)
			out[i] = 0;

		out[0] = (smoother[0] + smoother[1]) * density[0]
				+ smoother[2] * density[1];
		for(int i = 1; i < density.length - 1; i++)
		{
			out[i] += smoother[0] * density[i - 1] + smoother[1] * density[i]
					+ smoother[2] * density[i + 1];
		}

		out[out.length - 1] += smoother[0] * density[out.length - 2]
				+ (smoother[1] + smoother[2]) * density[out.length - 1];

		return out;
	}

	public void divide()
	{
		char[][] temp = new char[width * 2][height * 2];
		int[][] tempZones = new int[width * 2][height * 2];
		width *= 2;
		height *= 2;

		applyAll((int w, int h) -> {
			temp[w][h] = land[w / 2][h / 2];
			return 0;
		});

		if(zones != null)
		{
			applyAll((int w, int h) -> {
				tempZones[w][h] = zones[w / 2][h / 2];
				return 0;
			});
		}

		land = new char[width][height];
		if(zones != null)
		{
			zones = new int[width][height];
		}

		applyAll((int w, int h) -> {
			// int count = 0;
			double[] count = new double[landTypes.length];
			double[] countZones = new double[numZones];
			int total = 0;
			for(int j = -1; j < 2; j++)
			{
				for(int i = -1; i < 2; i++)
				{
					if(!(w + i < 0 || h + j < 0 || w + i >= width
							|| h + j >= height || (i == j && i == 0)))
					{
						// count += temp[w+i][h+j] == 'l' ? 1 : 0;
						count[indexOfType(temp[w + i][h + j])]++;
						if(zones != null)
						{
							countZones[tempZones[w + i][h + j] - 1]++;
						}
						total++;
					}
				}
			}

			double r = rand.nextDouble() * total;
			double r2 = r;
			double c = 0;

			count = smoothDensity(count);
			// countZones = smoothDensity(countZones);

			/*
			 * double t = 0; for(int i = 0; i < count.length; i++) t+=count[i];
			 */

			// System.out.println(t+":"+total);

			for(int i = 0; i < count.length; i++)
			{
				r -= count[i];
				if(r < 0)
				{
					land[w][h] = landTypes[i];
					break;
				}
			}

			if(zones != null)
			{
				for(int i = 0; i < countZones.length; i++)
				{
					r2 -= countZones[i];
					if(r2 < 0)
					{
						zones[w][h] = i + 1;
						break;
					}
				}
			}

			// land[w][h] = rand.nextInt(total) < count ? 'l' : 'w';

			return 0;
		});
	}

	public boolean isInBounds(int w, int h)
	{
		return isInBounds(w, h, 0);
	}

	public boolean isInBounds(int w, int h, int b)
	{
		return(w >= b && h >= b & w <= width - 1 - b && h <= height - 1 - b);
	}

	public boolean isLandOfType(int w, int h, String list)
	{
		return isOfType(land, w, h, list);
	}

	public boolean isBiomeOfType(int w, int h, String list)
	{
		return isOfType(biome.land, w, h, list);
	}

	public boolean isCaveOfType(int w, int h, String list)
	{
		return isOfType(cave.land, w, h, list);
	}

	public boolean isConstructOfType(int w, int h, String list)
	{
		return isOfType(constructs, w, h, list);
	}

	public boolean isOfType(char[][] type, int w, int h, String list)
	{
		return list.indexOf(type[w][h]) != -1;
	}

	public char getLand(int w, int h)
	{
		return land[w][h];
	}

	public char getConstruct(int w, int h)
	{
		return constructs[w][h];
	}

	public char getRoad(int w, int h)
	{
		return road.land[w][h];
	}
	
	public char getBiome(int w, int h)
	{
		return biome.land[w][h];
	}
	
	public int getZone(int w, int h)
	{
		return biome.zones[w][h];
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}
}
