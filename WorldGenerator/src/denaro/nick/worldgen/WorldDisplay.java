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
	public boolean showCave;
	public boolean showConstructs;
	public boolean showZone;
	public boolean drawing;
	
	public WorldDisplay(World world)
	{
		this.world = world;
		this.setPreferredSize(new Dimension(world.getWidth(), world.getHeight()));
		showBiome = false;
		showCave = true;
		showConstructs = true;
		showZone = false;
		
		drawing = false;
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
				if(g.getColor() != Color.magenta)
				{
					g.fillRect(w, h, 1, 1);
				}
			}
		}
	}
	
	public void drawZone(Graphics g1)
	{
		Graphics2D g = (Graphics2D) g1;
		for(int h = 0; h < world.getHeight(); h++)
		{
			for(int w = 0; w < world.getWidth(); w++)
			{
				g.setColor(world.getZoneColor(w,h));
				if(g.getColor() != Color.magenta)
				{
					g.fillRect(w, h, 1, 1);
				}
			}
		}
	}
	
	public void drawCave(Graphics g1)
	{
		Graphics2D g = (Graphics2D) g1;
		for(int h = 0; h < world.getHeight(); h++)
		{
			for(int w = 0; w < world.getWidth(); w++)
			{
				g.setColor(world.getCaveColor(w,h));
				if(g.getColor() != Color.magenta)
				{
					g.fillRect(w, h, 1, 1);
				}
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
		drawing = true;
		drawWorld(g);
		
		if(showBiome)
		{
			drawBiome(g);
		}
		
		if(showCave)
		{
			drawCave(g);
		}
		
		if(showConstructs)
		{
			drawConstructs(g);
		}
		
		if(showZone)
		{
			drawZone(g);
		}
		
		g.setColor(Color.white);
		
		g.drawString(WorldFramer.mouseX+", "+WorldFramer.mouseY, 0, 100);
		g.drawString("biome: " + world.getBiome(WorldFramer.mouseX, WorldFramer.mouseY), 0, 112);
		g.drawString("zone: " + world.getZone(WorldFramer.mouseX, WorldFramer.mouseY), 0, 124);
		drawing = false;
	}
}
