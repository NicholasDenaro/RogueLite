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
			world = new World(4,4,World.WORLD_GEN,World.LAND_TYPES,World.FREQS,World.COLORS);
			world.generateBiomes();
			world.populate();
			display.setWorld(world);
		}
		else if(ke.getKeyCode()==KeyEvent.VK_SPACE)
		{
			world.smooth();
		}
		else if(ke.getKeyCode()==KeyEvent.VK_SHIFT)
		{
			display.showBiome = !display.showBiome;
		}
		else if(ke.getKeyCode()==KeyEvent.VK_CONTROL)
		{
			display.showConstructs = !display.showConstructs;
		}
		
		display.repaint();
	}

	@Override
	public void keyReleased(KeyEvent ke)
	{
		
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
		display.repaint();
	}

}
