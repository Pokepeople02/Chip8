package chip8.gui;


import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;

import chip8.emulator.Chip8;

@SuppressWarnings("serial")
public class DisplayVisual extends Container {
	
	private Chip8 system;
	private JPanel[][] pixels;
	private GridLayout gridManager;
	private int scaleFactor;
	
	public static final Color SCREEN_PIXEL_ON = Color.WHITE;
	public static final Color SCREEN_PIXEL_OFF = Color.BLACK;

	/**Creates a new 32 * 64 pixel visualization for the supplied Chip8 system.
	 * @param chip8 The emulated Chip8 system that this display visualizes.
	 */
	public DisplayVisual(Chip8 chip8) {
		this.scaleFactor = 1;
		this.system = chip8;
		this.gridManager = new GridLayout(Chip8.DISPLAY_HEIGHT, Chip8.DISPLAY_WIDTH);
		this.pixels = new JPanel[Chip8.DISPLAY_WIDTH][Chip8.DISPLAY_HEIGHT];
		this.setLayout(gridManager);
		
		initPixels();
		
		this.setPreferredSize(new Dimension(Chip8.DISPLAY_WIDTH * scaleFactor, Chip8.DISPLAY_HEIGHT * scaleFactor));
		this.update();
	}//end constructor method

	/** Initializes the contained pixels of the display with an initial scale factor of 1*/
	private void initPixels() {
		for(int x = 0; x < Chip8.DISPLAY_WIDTH; ++x) {
			for(int y = 0; y < Chip8.DISPLAY_HEIGHT; ++y) {
				pixels[x][y] = new JPanel();
				pixels[x][y].setOpaque(true);
				pixels[x][y].setPreferredSize(new Dimension(scaleFactor, scaleFactor));
				this.add(pixels[x][y]);
			}//end for
			
		}//end for
		
	}//end method initPixels

	/**Updates the status of each pixel in the display and repaints them*/
	public void update() {
		boolean[][] currBuffer = this.system.getCurrentDisplayBuffer();
		
		for(int x = 0; x < Chip8.DISPLAY_WIDTH; ++x)
			for(int y = 0; y < Chip8.DISPLAY_HEIGHT; ++y) {
				//Update pixel status based on status of 
				this.pixels[x][y].setBackground(currBuffer[x][y] ? DisplayVisual.SCREEN_PIXEL_ON : DisplayVisual.SCREEN_PIXEL_OFF);
				this.pixels[x][y].repaint();
			}//end for
			
	}//end method update
	
	/**Scales the display by the provided factor.
	 * @param factor The factor to scale the display pixels.
	 */
	public void scale(int factor) {
		this.scaleFactor = factor;
		initPixels();
		update();
	}//end method scale

}//end class DisplayVisual
