package chip8;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import chip8.emulator.Chip8;
import chip8.emulator.Keypad;

public class Driver {

	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.println("Usage: java " + Driver.class.getSimpleName() + " \"ROM\"");
			System.exit(0);
		}//end if
		
		Chip8 emulator = new Chip8();
		try {
			FileInputStream fs = new FileInputStream(args[0]);
			byte[] fileContents = fs.readAllBytes();
			fs.close();
			
			emulator.loadROM(fileContents);
			emulator.attachKeypad(new Keypad());
			
			while(true)
				emulator.cycle();
		}//end try
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}//end catch
		catch (IOException e) {
			e.printStackTrace();
		}//end catch
		
	}//end method main

}//end class Driver
