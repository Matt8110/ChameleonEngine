package matt8110.mattengine.core;

import org.lwjgl.input.Mouse;

public class Input {

	private static boolean[] mouseButtons = new boolean[3];
	
	public static boolean mouseWasPressed(int button) {
		
		if (Mouse.isButtonDown(button)) {
			
			if(!mouseButtons[button]) {
				mouseButtons[button] = true;
				return true;
			}else {
				return false;
			}
		}else {
			mouseButtons[button] = false;
		}
		
		return false;
	}
	
}
