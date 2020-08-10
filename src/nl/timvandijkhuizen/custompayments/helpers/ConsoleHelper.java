package nl.timvandijkhuizen.custompayments.helpers;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import java.util.logging.Level;

public class ConsoleHelper {

	public static void printInfo(String message) {
		CustomPayments.getInstance().getLogger().log(Level.INFO, message);
	}
	
	public static void printError(String message) {
		printError(message, null);
	}
	
	public static void printError(String message, Throwable error) {
		CustomPayments.getInstance().getLogger().log(Level.WARNING, message);
		
		if(error != null) {
			error.printStackTrace();
		}
	}
	
}
