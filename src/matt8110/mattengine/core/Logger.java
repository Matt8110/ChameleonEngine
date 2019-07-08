package matt8110.mattengine.core;

public class Logger {
	
	public static void log(String text, LogType type) {
		
		switch(type) {
		case ERROR:
			System.err.println("Error: " + text);
			break;
		case WARNING:
			System.err.println("Warning: " + text);
			break;
		case INFO:
			System.out.println("Info: " + text);
			break;
		case WTF:
			System.err.println("What The Fuck...: " + text);
			break;
		}
		
	}
	
}
