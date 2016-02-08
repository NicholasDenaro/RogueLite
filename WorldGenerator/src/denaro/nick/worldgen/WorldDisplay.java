package denaro.nick.worldgen;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class WorldDisplay extends Canvas
{
	private World world;
	public boolean showBiome;
	public boolean showConstructs;
	
	public WorldDisplay(World world)
	{
		this.world = world;
		this.setPreferredSize(new Dimension(world.getWidth(), world.getHeight()));
		showBiome = false;
		showConstructs = true;
	}
	
	public void setWorld(World world)
	{
		this.world = world;
	}
	
	public void drawWorld(Graphics g1)
	{
		Graphics2D g = (Graphics2D) g1;
		for(int h = 0; h < world.getHeight(); h++)
		{
			for(int w = 0; w < world.getWidth(); w++)
			{
				g.setColor(world.getLandColor(w,h));
				g.fillRect(w, h, 1, 1);
			}
		}
	}
	
	public void drawBiome(Graphics g1)
	{
		Graphics2D g = (Graphics2D) g1;
		for(int h = 0; h < world.getHeight(); h++)
		{
			for(int w = 0; w < world.getWidth(); w++)
			{
				g.setColor(world.getBiomeColor(w,h));
				g.fillRect(w, h, 1, 1);
			}
		}
	}
	
	public void drawConstructs(Graphics g1)
	{
		Graphics2D g = (Graphics2D) g1;
		for(int h = 0; h < world.getHeight(); h++)
		{
			for(int w = 0; w < world.getWidth(); w++)
			{
				g.setColor(world.getConstructColor(w,h));
				if(g.getColor() != Color.magenta)
				{
					g.fillRect(w, h, 1, 1);
				}
			}
		}
	}
	
	@Override
	public void paint(Graphics g)
	{
		if(!showBiome)
		{
			drawWorld(g);
		}
		else
		{
			drawBiome(g);
		}
		if(showConstructs)
		{
			drawConstructs(g);
		}
		
		g.setColor(Color.white);
		
		g.drawString(WorldFramer.mouseX+", "+WorldFramer.mouseY, 0, 100);
	}
}
