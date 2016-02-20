package denaro.nick.worldgen;

public class VillageOptions
{
	public static final int MIN_SPACE_BETWEEN_STRUCTURES = 3;
	
	public static final int MAX_TRY_COUNT = 10;
	
	public static final int NUM_VILLAGES = 2;
	public static final int MIN_VILLAGE_RADIUS = 20;
	public static final int VAR_VILLAGE_RADIUS = 10;
	
	public static final int MIN_VILLAGE_DENSITY = 5;
	public static final int VAR_VILLAGE_DENSITY = 10;
	
	public int minSpace;
	public HouseOptions houseOptions;
	public int minRadius;
	public int varRadius;
	public int minDensity;
	public int varDensity;
	
	public VillageOptions(int minRadius, int varRadius, int minDensity, int varDensity, int minSpace, HouseOptions houseOptions)
	{
		this.minRadius = minRadius;
		this.varRadius = varRadius;
		this.minDensity = minDensity;
		this.varDensity = varDensity;
		this.minSpace = minSpace;
		this.houseOptions = houseOptions;
	}
}
