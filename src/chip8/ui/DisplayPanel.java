package chip8.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import chip8.emulator.Chip8;
import chip8.emulator.Display;

@SuppressWarnings("serial")
public class DisplayPanel extends JPanel implements Display {
	
	private Chip8 system;
	
	private int scaleFactor = 1;
	
	public static final Color SCREEN_PIXEL_ON = Color.WHITE;
	
	public static final Color SCREEN_PIXEL_OFF = Color.BLACK;

	/**Creates a new 32 * 64 pixel visualization for the supplied Chip8 system.
	 * @param chip8 The emulated Chip8 system that this display visualizes.
	 */
	public DisplayPanel(Chip8 chip8) {
		this.system = chip8;
	}//end constructor method
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(scaleFactor * Chip8.DISPLAY_WIDTH, scaleFactor * Chip8.DISPLAY_HEIGHT);
	}//end method getPreferredSize

	/**Updates the pixels of the display based on the current state of the display buffer of the associated CHIP-8 system*/
	public void update() {
		repaint();
	}//end method update
	
	/**Scales the display by the provided factor.
	 * @param factor The factor to scale the display pixels.
	 */
	public void scale(int factor) {
		this.scaleFactor = factor;
		repaint();
	}//end method scale
	
	/**
	 * Paints rectangular colored pixels based on the current state of the associated CHIP-8's display memory buffer.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		boolean[][] currDisplayBuffer = this.system.getCurrentDisplayBuffer();
		
		for(int column = 0; column < Chip8.DISPLAY_WIDTH; ++column)
			for(int row = 0; row < Chip8.DISPLAY_HEIGHT; ++row) {
				if(currDisplayBuffer[column][row])
					g.setColor(SCREEN_PIXEL_ON);
				else
					g.setColor(SCREEN_PIXEL_OFF);
				
				g.fillRect(column * scaleFactor , row * scaleFactor, scaleFactor, scaleFactor);
			}//end for
		
	}//end method paintComponent

}//end class DisplayVisual
