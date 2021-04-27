package chip8;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Driver {

	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.println("Usage: java " + Driver.class.getSimpleName() + " \"ROM\"");
			System.exit(0);
		}//end if
		
		Chip8 emulator = new Chip8();
		try {
			emulator.LoadROM(new File(args[0]));
		}//end try
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}//end catch
		catch (IOException e) {
			e.printStackTrace();
		}//end catch
		
	}//end method main

}//end class Driver
