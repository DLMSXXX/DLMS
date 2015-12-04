package DLMSApp.crown;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.omg.CORBA.ORB;

public class dlmsManager {

	public static void showMenu() {
		System.out.println("\n****Welcome to DLMS****\n");
		System.out.println("Please select an option:");
		System.out.println("1. Delay payment");
		System.out.println("2. Print information");
		System.out.println("3. Exit");
	}

	public static void main(String[] args) throws IOException {

		int userChoice = 0;
		Scanner keyboard = new Scanner(System.in);
		showMenu();
		while (true) {
			Boolean valid = false;
			while (!valid) {
				try {
					userChoice = keyboard.nextInt();
					valid = true;
				} catch (Exception e) {
					System.out.println("Invalid Input, please enter an Integer");
					valid = false;
					keyboard.nextLine();
				}
			}
			// Manage user selection.
			switch (userChoice) {
			case 1:
				System.out.println("Delay payment:");
				System.out.println("Please choose the bank number (1-3)...");
				int bankNumber = keyboard.nextInt();

				ORB orb = ORB.init(args, null);
				BufferedReader br = new BufferedReader(new FileReader("bank" + bankNumber + "IOR.txt"));
				String ior = br.readLine();
				br.close();
				org.omg.CORBA.Object o = orb.string_to_object(ior);
				dlms aDLMS = dlmsHelper.narrow(o);

				System.out.println("Please enter the loan ID...");
				String lID = keyboard.next();
				System.out.println("Please enter the current date...");
				String cDate = keyboard.next();
				System.out.println("Please enter the new date...");
				String nDate = keyboard.next();
				if (aDLMS.delayPayment(Integer.toString(bankNumber), lID, cDate, nDate).equals("FAIL")) {
					System.out.println("Not success");
				} else
					System.out.println("The due date has been changed to " + nDate + ".");
				showMenu();
				break;
			case 2:
				System.out.println("Please choose the bank number (1-3)...");
				bankNumber = keyboard.nextInt();

				orb = ORB.init(args, null);
				br = new BufferedReader(new FileReader("bank" + bankNumber + "IOR.txt"));
				ior = br.readLine();
				br.close();
				o = orb.string_to_object(ior);
				aDLMS = dlmsHelper.narrow(o);

				System.out.println("The customer and loan information in bank " + bankNumber + ":");
				String info = aDLMS.printCustomerInfo(Integer.toString(bankNumber));
				System.out.println("Customers as list below: ");
				System.out.println(info);
				showMenu();
				break;
			case 3:
				System.out.println("Have a nice day!");
				keyboard.close();
				System.exit(0);
			default:
				System.out.println("Invalid Input, please try again.");
			}
		}
	}

}
