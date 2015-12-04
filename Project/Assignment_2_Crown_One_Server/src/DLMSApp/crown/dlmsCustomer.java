package DLMSApp.crown;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

public class dlmsCustomer {
	public static void showMenu() {
		System.out.println("\n****Welcome to DLMS****\n");
		System.out.println("Please select an option:");
		System.out.println("1. Open Account");
		System.out.println("2. Get Loan");
		System.out.println("3. Transfer Loan");
		System.out.println("4. Exit");
	}

	public static void main(String[] args) throws IOException, InvalidName, NotFound, CannotProceed,
			org.omg.CosNaming.NamingContextPackage.InvalidName {

		int userChoice = 0;
		Scanner keyboard = new Scanner(System.in);

		showMenu();

		while (true) {
			Boolean valid = false;

			// Enforces a valid integer input.
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

			switch (userChoice) {
			case 1: 
				System.out.println("Opening your account:");
				System.out.println("Please choose your bank number (1-3)...");
				int bankNumber = keyboard.nextInt();

				ORB orb = ORB.init(args, null);
				BufferedReader br = new BufferedReader(new FileReader("bank" + bankNumber + "IOR.txt"));
				String ior = br.readLine();
				br.close();
				org.omg.CORBA.Object o = orb.string_to_object(ior);
				dlms aDLMS = dlmsHelper.narrow(o);

				System.out.println("Please enter your first name...");
				String fn = keyboard.next();
				System.out.println("Please enter your last name...");
				String ln = keyboard.next();
				System.out.println("Please enter your e-mail address...");
				String email = keyboard.next();
				System.out.println("Please enter your phone number...");
				String pn = keyboard.next();
				System.out.println("Please enter your password (at least 6 characters)...");
				String pw = keyboard.next();
				String resultNumber = aDLMS.openAccount(Integer.toString(bankNumber), fn, ln, email, pn, pw);

				if (Integer.parseInt(resultNumber) > 0) {
					System.out.println("Please remember your account number: " + resultNumber + ".");
					System.out.println("Your new account has been created!");
				} else
					System.out.println("You already have an accout!");
				
				System.out.println("**********************************");
				showMenu();
				break;

			case 2:
				System.out.println("Getting loan:");
				System.out.println("Please choose your bank number (1-3)...");
				bankNumber = keyboard.nextInt();

				orb = ORB.init(args, null);
				br = new BufferedReader(new FileReader("bank" + bankNumber + "IOR.txt"));
				ior = br.readLine();
				br.close();
				o = orb.string_to_object(ior);
				aDLMS = dlmsHelper.narrow(o);

				System.out.println("Please enter your account number...");
				String a = keyboard.next();
				System.out.println("Please enter your password...");
				pw = keyboard.next();

				System.out.println("Please enter the loan amount...");
				String l = keyboard.next();
				String result = aDLMS.getLoan(Integer.toString(bankNumber), a, pw, l);
				if (result.equals("FAIL")) {
					System.out.println("Sorry, you have exceed the limit of your loan.");
				} else {
					System.out.println(result + " Client account number: " + a + " just made " + l + " loan.");
				}
				System.out.println("**********************************");
				showMenu();
				break;

			case 3:
				System.out.println("Transfer loan:");
				System.out.println("Please choose the current bank number (1-3)...");
				bankNumber = keyboard.nextInt();

				orb = ORB.init(args, null);
				br = new BufferedReader(new FileReader("bank" + bankNumber + "IOR.txt"));
				ior = br.readLine();
				br.close();
				o = orb.string_to_object(ior);
				aDLMS = dlmsHelper.narrow(o);

				System.out.println("Please enter the loan ID...");
				String lid = keyboard.next();
				System.out.println("Please enter the target bank number...");
				String tbn = keyboard.next();

				result = aDLMS.transferLoan(lid, Integer.toString(bankNumber), tbn);
				System.out.println(result);
				System.out.println("**********************************");
				showMenu();
				break;

			case 4:
				System.out.println("Have a nice day!");
				keyboard.close();
				System.exit(0);

			default:
				System.out.println("Invalid Input, please try again.");
			}
		}

	}

}
