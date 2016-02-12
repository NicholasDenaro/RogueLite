package denaro.nick.worldgen;

import java.awt.Color;

public class Biomes
{
	public static final char[]  BIOME_TYPES = {'D','G','F','J'};
	public static final double[]  BIOME_FREQS = {0.1,0.5,0.1,0.3};
	public static final Color[] BIOME_COLORS = {World.LT_BROWN,Color.green,Color.green.darker(),Color.cyan.darker().darker()};
	
	private int width;
	private int height;
	private char[][] land;
	private Color colors;
	
	public Biomes(int width, int height, char[] types)
	{
		
	}
}
