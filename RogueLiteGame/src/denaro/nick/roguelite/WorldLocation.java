package denaro.nick.roguelite;
import denaro.nick.core.Location;
import denaro.nick.worldgen.World;

public class WorldLocation extends Location
{
	private World world;
	
	public WorldLocation(World world)
	{
		super();
		this.world = world;
	}
	
	public World getWorld()
	{
		return world;
	}
}
