package denaro.nick.worldgen;

public class HouseOptions
{
	public static final int MIN_BUILDING_WIDTH = 4;
	public static final int MIN_BUILDING_HEIGHT = 3;
	public static final int VAR_BUILDING_WIDTH = 7;
	public static final int VAR_BUILDING_HEIGHT = 4;
	
	public int minBuildingWidth;
	public int minBuildingHeight;
	public int varBuildingWidth;
	public int varBuildingHeight;
	
	public HouseOptions(int minBuildingWidth, int minBuildingHeight, int varBuildingWidth, int varBuildingHeight)
	{
		this.minBuildingWidth = minBuildingWidth;
		this.minBuildingHeight = minBuildingHeight;
		this.varBuildingWidth = varBuildingWidth;
		this.varBuildingHeight = varBuildingHeight;
	}
}
