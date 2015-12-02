package DLMS;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import org.omg.CORBA.ORB;

public class dlmsClientManager {

	public static void showMenu()
	{
		System.out.println("\n****Welcome to DLMS****\n");
		System.out.println("Please select an option:");
		System.out.println("1. Delay payment");
		System.out.println("2. Print information");
		System.out.println("3. Exit");
	}
	
	public static void main(String[] args) throws IOException {
		
		int userChoice=0;
		Scanner keyboard = new Scanner(System.in);
		
		System.out.println("Please enter the username:");
		if (keyboard.next().equals("Manager"))
		{
			System.out.println("Please enter the password:");
			if (keyboard.next().equals("Manager"))
				showMenu();
			else
			{
				System.out.println("Wrong password!");
				System.exit(0);
			}
		}
		else
		{
			System.out.println("Wrong username!");
			System.exit(0);
		}
		
		ORB orb = ORB.init(args, null);
    	BufferedReader br = new BufferedReader(new FileReader("FEOR.txt"));
		String ior = br.readLine();
		br.close();    		
		org.omg.CORBA.Object o = orb.string_to_object(ior);	
		dlms aDLMS = dlmsHelper.narrow(o);
		
		while(true)
		{
			Boolean valid = false;
			
			// Enforces a valid integer input.
			while(!valid)
			{
				try{
					userChoice=keyboard.nextInt();
					valid=true;
				}
				catch(Exception e)
				{
					System.out.println("Invalid Input, please enter an Integer");
					valid=false;
					keyboard.nextLine();
				}
			}
			
			
			// Manage user selection.
			switch(userChoice)
			{
			case 1: 
				System.out.println("Delay payment:");
				System.out.println("Please choose the bank number (1-3)...");
				int bankNumber = keyboard.nextInt();    	    		
				System.out.println("Please enter the loan ID...");
				String lID = keyboard.next();
				System.out.println("Please enter the current date...");
				String cDate = keyboard.next();
				System.out.println("Please enter the new date...");
				String nDate = keyboard.next();
				if(aDLMS.delayPayment(Integer.toString(bankNumber), lID, cDate, nDate).equals("TRUE"))
				{
					// write log file
					PrintWriter writer = null;
					try {
						writer = new PrintWriter(new FileWriter("Manager/Manager.txt",true));
						writer.println("Delay payment at Bank " + bankNumber);
						writer.println("Loan ID: " + lID);
						writer.println("Current Date: " + cDate);
						writer.println("New Date: " + nDate);
						writer.println();
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("The due date has been changed to " + nDate + ".");
				}
				else
					System.out.println("The loan ID doesn't exist.");	
				showMenu();
				break;
			case 2:
				System.out.println("Please choose the bank number (1-3)...");
				bankNumber = keyboard.nextInt();			
				System.out.println("The customer and loan information in bank " + bankNumber + ":");
				String info = aDLMS.printCustomerInfo(Integer.toString(bankNumber));
				System.out.println("Order for Customer:	Account	Number	First Name	Last Name	E-mail	Phone Number	Password	Maximum Credit");
				System.out.println("Order for Loan:	Loan ID	Account	Number	Amount	Due Date(MM/DD)");
				System.out.println(info);

				// write log file
				PrintWriter writer = null;
				try {
					writer = new PrintWriter(new FileWriter("Manager/Manager.txt",true));
					writer.println("Print the information at bank " + bankNumber);
					writer.println("Customer:	Account	Number	First Name	Last Name	E-mail		Phone Number	Password	Maximum Credit");
					writer.println("Loan:	Loan ID		Account	Number	Amount	Due Date(MM/DD)");
					writer.println(info);
					writer.println();
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
