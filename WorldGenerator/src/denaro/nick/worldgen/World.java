package denaro.nick.worldgen;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Random;
import java.util.function.IntBinaryOperator;

public class World
{
	public static final char[] LAND_TYPES = new char[]{'o','w','b','p','P','h','H','m','M'};
	public static final double[] NORMAL_WORLD = new double[]{0.2,0.1,0.0,0.1,0.1,0.1,0.1,0.1,0.2};
	public static final double[] MOUNTAIN_WORLD = new double[]{0.1,0.0,0.0,0.0,0.0,0.0,0.1,0.2,0.6};
	public static final Color[] COLORS = new Color[]{Color.blue,Color.cyan.darker(),Color.orange,Color.green.darker().darker().darker(),Color.green.darker().darker(),Color.green.darker(),Color.green,Color.gray.darker(),Color.gray};
	public static final int MIN_VILLAGE_RADIUS = 20;
	public static final int VAR_VILLAGE_RADIUS = 30;
	
	public static final double[] WORLD_TYPE = NORMAL_WORLD;
	
	public static final Color PINK = mixColor(Color.pink, Color.magenta.darker(), 0.05);
	public static final Color TEAL = mixColor(Color.cyan, Color.green,0.66);
	public static final Color SCARLET = mixColor(Color.red, Color.orange,0.2);
	public static final Color LT_BROWN = mixColor(Color.magenta, SCARLET, 0.93);
	public static final Color BROWN = mixColor(Color.magenta, SCARLET, 0.93).darker();
	public static final Color DK_BROWN = mixColor(Color.magenta, SCARLET, 0.93).darker().darker();
	public static final Color TAN = mixColor(Color.white, BROWN, 0.9);
	
	public static final String WORLD_GEN = "rddsddsddssdsdsssss";
	public static final String BIOME_GEN = "rdsdsddssdsdsssss";
	public static final String CAVE_GEN = "rdddsdsssss";
	
	public Random rand = new Random();
	
	private char[] landTypes;
	private double[] freqs;
	private Color[] colors;
	private World biome;
	private World cave;
	
	private char[][] land;
	
	private int width;
	private int height;
	
	private char[][] constructs;
	
	private int[][] zones;
	private int numZones;
	
	public static void main(String[] args)
	{
		System.out.println(-1/2);
		
		World world = new World(4,4,WORLD_GEN,LAND_TYPES,WORLD_TYPE,COLORS);
		
		world.generate();
		
		new WorldFramer(world);

	}
	
	public World(int width, int height,String technique, char[] landTypes, double[] frequencies, Color[] colors)
	{
		this.width=width;
		this.height=height;
		this.landTypes = landTypes;
		this.freqs = frequencies;
		this.colors = colors;
		this.land = new char[width][height];
		
		
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
				case 's':
					smooth();
				break;
				default:
				break;
			}
		}
		
		constructs = new char[this.width][this.height];
		zones = new int[this.width][this.height];
	}
	
	public void generate()
	{
		generateBiomes();
		generateCaves();

		populate();
		
		createZones();
	}
	
	public void generateBiomes()
	{
		biome=new World(16,16,BIOME_GEN,Biomes.BIOME_TYPES, Biomes.BIOME_FREQS, Biomes.BIOME_COLORS);
	}
	
	public void generateCaves()
	{
		cave=new World(64,64,CAVE_GEN,Cave.CAVE_TYPES, Cave.CAVE_FREQS, Cave.CAVE_COLORS);
	}
	
	public void populate()
	{
		createVillages(0.2);
		
		createCliffs();
		
		//createCaves();
		
		createTrees(new char[]{'D','N','F','J'},new double[]{0,0.0005,0.05,0.2});
	}
	
	public void createVillages(double villageRatio)
	{
		int numPlains = countLandType('p') + countLandType('h');
		
		while(numPlains*1.0/this.width/this.height > villageRatio)
		{
			int w=0;
			int h=0;
			w=rand.nextInt(this.width);
			h=rand.nextInt(this.height);
			while(land[w][h]!='P' && land[w][h]!='h')
			{
				w=rand.nextInt(this.width);
				h=rand.nextInt(this.height);
			}
			new Village(this,w,h,MIN_VILLAGE_RADIUS,VAR_VILLAGE_RADIUS);
			numPlains-=villageRatio * this.width * this.height;
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
							if(w + i < 0 || w + i >= width || h + j < 0 || h + j >= height)
								continue;
							if(ind > "pPhHmM".indexOf(land[w+i][h+j]))
							{
								constructs[w][h]='C';
							}
						}
					}
				}
				if(w == 0 || h == 0 || w == width -1 || h == height -1)
				{
					if("ow".indexOf(land[w][h]) != -1)
					{
						constructs[w][h]='R';
					}
					else
					{
						constructs[w][h]='C';
					}
				}
			}
		}
	}
	
	public void createStairs()
	{
		
	}
	
	public void createZones()
	{
		int count = 0;
		int zoneCounter = 1;
		boolean finishZone = false;
		char cur = 0;
		while(!filledWithZones())
		{
			finishZone = false;
			for(int h = 0; h < height && !finishZone ; h++)
			{
				for(int w = 0; w < width && !finishZone; w++)
				{
					if(zones[w][h] == 0)
					{
						if(cur == 0 || (cur != 0 && isBiomeNextTo(w, h, cur)))
						{
							if(!isLandOfType(w,h,"owbmM"))
							{
								zones[w][h] = zoneCounter;
								count += spreadZone(w, h, biome.land[w][h], zoneCounter);
								if(count > 100000 || biome.land[w][h] == 'J')
								{
									System.out.println("count: " + count);
									System.out.println("biome: " + biome.land[w][h]);
									finishZone = true;
								}
							}
						}
					}
				}
			}
			System.out.println("Finished zone: "+zoneCounter);
			zoneCounter++;
			cur = 0;
			count = 0;
		}
		System.out.println("Zones: "+zoneCounter);
		numZones = zoneCounter - 1;
	}
	
	private boolean filledWithZones()
	{
		for(int h = 0; h < height ; h++)
		{
			for(int w = 0; w < width; w++)
			{
				if(zones[w][h] == 0 && !isLandOfType(w,h,"owbmM"))
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	private int spreadZone(int x, int y, char b, int z)
	{
		/*if(!isInBounds(w, h) || zones[w][h] != 0 || biome.land[w][h] != b || isLandOfType(w,h,"owbmM"))
		{
			return;
		}
		
		zones[w][h] = z;
		
		spreadZone(w ,h - 1, b, z);
		spreadZone(w - 1, h, b, z);
		spreadZone(w, h + 1, b, z);
		spreadZone(w + 1, h, b, z);*/
		
		boolean changed = true;
		int count = 0;
		
		final int MAX = 500000;
		
		while(changed && count < MAX)
		{
			changed = false;
			for(int h = 0; h < height && count < MAX; h++)
			{
				for(int w = 0; w < width && count < MAX; w++)
				{
					if(zones[w][h] == 0 && biome.land[w][h] == b && !isLandOfType(w,h,"owbmM"))
					{
						if(isZoneNextTo(w, h, z))
						{
							zones[w][h] = z;
							changed = true;
							count++;
						}
					}
				}
			}
		}
		System.out.println("Filled " + count + " for zone "+z);
		return count;
	}
	
	public void createCaves()
	{
		double caveRatio = 0.3;
		
		int count = 10;
		
		while(countConstructType('c') * 1.0 / (countLandType('m') + countLandType('M')) < caveRatio && count-- > 0)
		{
			LinkedList<Integer> cliffBlocks = new LinkedList<Integer>();
			for(int h = 1; h < height - 1; h++)
			{
				for(int w = 1; w < width - 1; w++)
				{
					boolean valid = true;
					
					char north = constructs[w][h-1];
					char west = constructs[w-1][h];
					char south = constructs[w][h+1];
					char east = constructs[w+1][h];
					
					if((north == east && north == 'C') || (north == west && north == 'C') || (south == east && south == 'C') || (south == west && south == 'C'))
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
										if("m".indexOf(land[w][h]) != -1 && "C".indexOf(constructs[w][h]) != -1)
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
			
			int block  = cliffBlocks.get(r);
			
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
					if(biome.land[w][h] == terrain[t] && "owbmM".indexOf(land[w][h]) == -1 && "VWFDC".indexOf(constructs[w][h]) == -1)
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
			System.out.println("Time to fill " + terrain[t] + ": " + (System.currentTimeMillis() - start) + "ms");
		}
	}
	
	public boolean isZoneNextTo(int w, int h, int z)
	{
		return (isInBounds(w, h - 1) && zones[w][h - 1] == z)||(isInBounds(w - 1, h) && zones[w - 1][h] == z)
				||(isInBounds(w, h + 1) && zones[w][h + 1] == z)||(isInBounds(w + 1, h) && zones[w + 1][h] == z);
	}
	
	public boolean isBiomeNextTo(int w, int h, char l)
	{
		return isNextTo(biome.land, w, h, l);
	}
	
	public boolean isNextTo(char[][] type, int w, int h, char l)
	{
		return (isInBounds(w, h - 1) && type[w][h - 1] == l)||(isInBounds(w - 1, h) && type[w - 1][h] == l)
				||(isInBounds(w, h + 1) && type[w][h + 1] == l)||(isInBounds(w + 1, h) && type[w + 1][h] == l);
	}
	
	public int countLandType(char l)
	{
		return countType(land,l);
	}
	
	public int countConstructType(char l)
	{
		return countType(constructs,l);
	}
	
	public int countType(char[][] type, char l)
	{
		int count=0;
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
	
	public Color getCaveColor(int w, int h)
	{
		if(isLandOfType(w, h, "mM") && isCaveOfType(w,h,"mM"))
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
		double percent = (zones[w][h] - 1) * 1.0 / numZones;
		if(zones[w][h] == 0 )
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
		float[] hsb1 = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);
		float[] hsb2 = Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), null);
		float[] hsbMix = new float[]{(float) (hsb1[0] * (1 - weight) + hsb2[0] * weight), (float) (hsb1[1] * (1 - weight)+ hsb2[1] * weight), (float) (hsb1[2] * (1 - weight) + hsb2[2] * weight)};
		
		return new Color(Color.HSBtoRGB(hsbMix[0], hsbMix[1], hsbMix[2]));
	}
	
	private void fillRandom()
	{
		applyAll((int w, int h) -> {
			//land[w][h] = rand.nextInt(percentLand+percentWater) < percentLand ? types[types.length - 1] : types[0];
			double p = rand.nextDouble();
			double c = 0;
			for(int i = 0; i < freqs.length; i++)
			{
				p-=freqs[i];
				if(p<=0)
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
		char[][] temp = new char[width][height]; // Remove the temp for the maze;
		
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
					if(!(w+i < 0 || h+j < 0 || w+i >= width || h+j >= height /*|| (i == j && i == 0)*/))
					{
						//count += temp[w+i][h+j] == 'l' ? 1 : 0;
						count[indexOfType(temp[w+i][h+j])]++;
						//count += land[w+i][h+j] == 'l' ? 1 : 0; // eh
						total++;
					}
				}
			}
			
			double avg = 0;
			for(int i = 0; i < landTypes.length; i++)
			{
				avg+=count[i] / total * i;
			}
			
			land[w][h] = landTypes[(int) Math.round(avg)];
			
			return 0;
		});
	}
	
	private int indexOfType(char l)
	{
		for(int i = -0; i < landTypes.length; i++)
			if(landTypes[i]==l)
				return i;
		return -1;
	}
	
	private double[] smoothDensity(double[] density)
	{
		double[] smoother = {0.1,0.8,0.1};
		double[] out = new double[density.length];
		for(int i = 0; i < out.length; i++)
			out[i]=0;
		
		out[0] = (smoother[0]+smoother[1]) * density[0] + smoother[2] * density[1];
		for(int i = 1; i < density.length - 1; i++)
		{
			out[i] +=smoother[0] * density[i-1] + smoother[1] * density[i] + smoother[2] * density[i+1];
		}
		
		out[out.length-1] += smoother[0] * density[out.length-2] + (smoother[1]+smoother[2]) * density[out.length-1];
		
		return out;
	}
	
	public void divide()
	{
		char[][] temp = new char[width*2][height*2];
		width *= 2;
		height *= 2;
		
		applyAll((int w, int h) -> {
			temp[w][h] = land[w/2][h/2];
			return 0;
		});
		
		land = new char[width][height];
		
		applyAll((int w, int h) -> {
			//int count = 0;
			double[] count = new double[landTypes.length];
			int total = 0;
			for(int j = -1; j < 2; j++)
			{
				for(int i = -1; i < 2; i++)
				{
					if(!(w+i < 0 || h+j < 0 || w+i >= width || h+j >= height || (i == j && i == 0)))
					{
						//count += temp[w+i][h+j] == 'l' ? 1 : 0;
						count[indexOfType(temp[w+i][h+j])]++;
						total++;
					}
				}
			}
			
			double r = rand.nextDouble()*total;
			double c = 0;
			
			count = smoothDensity(count);
			
			double t = 0;
			for(int i = 0; i < count.length; i++)
				t+=count[i];
			
			//System.out.println(t+":"+total);
			
			for(int i = 0; i < count.length; i++)
			{
				r-=count[i];
				if(r<0)
				{
					land[w][h] = landTypes[i];
					break;
				}
			}
			
			//land[w][h] = rand.nextInt(total) < count ? 'l' : 'w';
			
			return 0;
		});
	}
	
	public boolean isInBounds(int w, int h)
	{
		return isInBounds(w, h, 0);
	}
	
	public boolean isInBounds(int w, int h, int b)
	{
		return (w >= b && h >= b & w <= width - 1 - b && h <= height - 1 - b);
	}
	
	public boolean isLandOfType(int w, int h, String list)
	{
		return isOfType(land, w, h , list);
	}
	
	public boolean isBiomeOfType(int w, int h, String list)
	{
		return isOfType(biome.land, w, h , list);
	}
	
	public boolean isCaveOfType(int w, int h, String list)
	{
		return isOfType(cave.land, w, h , list);
	}
	
	public boolean isConstructOfType(int w, int h, String list)
	{
		return isOfType(constructs, w, h , list);
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
	
	public char getBiome(int w, int h)
	{
		return biome.land[w][h];
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