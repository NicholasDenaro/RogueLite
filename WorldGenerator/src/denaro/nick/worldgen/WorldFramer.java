package denaro.nick.worldgen;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

public class WorldFramer implements KeyListener, MouseMotionListener
{
	private JFrame frame;
	private World world;
	private WorldDisplay display;
	
	public static int mouseX = 0;
	public static int mouseY = 0;
	public static boolean redraw = false;
	
	public WorldFramer(World w)
	{
		this.world = w;
		this.frame = new JFrame();
		this.display = new WorldDisplay(w);
		frame.add(display);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		frame.addKeyListener(this);
		display.addKeyListener(this);
		display.addMouseMotionListener(this);
	}

	@Override
	public void keyTyped(KeyEvent ke)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent ke)
	{
		if(ke.getKeyCode()==KeyEvent.VK_ENTER)
		{
			System.out.println("---NEW WORLD---");
			world = new World(4,4,World.WORLD_GEN,World.LAND_TYPES,World.WORLD_TYPE,World.COLORS);
			world.generate();
			display.setWorld(world);
			display.repaint();
		}
		else if(ke.getKeyCode()==KeyEvent.VK_SPACE)
		{
			world.smooth();
		}
		else if(ke.getKeyCode()==KeyEvent.VK_SHIFT)
		{
			display.showBiome = !display.showBiome;
		}
		else if(ke.getKeyCode()==KeyEvent.VK_Z)
		{
			display.showCave = !display.showCave;
		}
		else if(ke.getKeyCode()==KeyEvent.VK_CONTROL)
		{
			display.showConstructs = !display.showConstructs;
		}
		else if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			display.showZone = !display.showZone;
		}
		else if(ke.getKeyCode()==KeyEvent.VK_M)
		{
			redraw = true;
		}
		
		if(!display.drawing && !redraw)
		{
			display.repaint();
		}
	}

	@Override
	public void keyReleased(KeyEvent ke)
	{
		if(ke.getKeyCode()==KeyEvent.VK_M)
		{
			redraw = false;
		}
	}

	@Override
	public void mouseDragged(MouseEvent me)
	{
		
	}

	@Override
	public void mouseMoved(MouseEvent me)
	{
		mouseX = me.getX();
		mouseY = me.getY();
		if(redraw)
		{
			display.repaint();
		}
	}

}
