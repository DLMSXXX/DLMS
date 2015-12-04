package DLMSApp.crown;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import DLMSApp.crown.bin.Customer;
import DLMSApp.crown.bin.Loan;

public class dlmsImpl extends dlmsPOA {
	private HashMap<Character, List<Customer>> accounts = new HashMap<Character, List<Customer>>();;
	private HashMap<Integer, Loan> loans = new HashMap<Integer, Loan>();
	public static final Character[] alphabet = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
			'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
	public int port;
	public int[] rest_port = new int[2];

	public dlmsImpl(int port) {
		this.port = port;
		Customer customer1 = new Customer("1231", "guan", "wang", "wg@outlook.com", "5145602937", "12345");
		Customer customer2 = new Customer("1232", "yucun", "li", "lyc@outlook.com", "5145602937", "23456");
		Customer customer3 = new Customer("1233", "ni", "ye", "yn@gmail.com", "5145602937", "34567");
		Customer customer4 = new Customer("1234", "jinyang", "du", "djy@hotmail.com", "5145602937", "45678");
		Loan loan1 = new Loan("1", "1231", "100", "2015-11-1");
		Loan loan2 = new Loan("2", "1232", "200", "2015-11-10");
		Loan loan3 = new Loan("3", "1233", "300", "2015-12-1");
		Loan loan4 = new Loan("4", "1234", "200", "2015-12-10");
		List<Customer> customerList1 = new LinkedList<Customer>();
		List<Customer> customerList2 = new LinkedList<Customer>();
		List<Customer> customerList3 = new LinkedList<Customer>();
		List<Customer> customerList4 = new LinkedList<Customer>();
		customerList1.add(customer1);
		customerList2.add(customer2);
		customerList3.add(customer3);
		customerList4.add(customer4);
		for (Character ch : alphabet) {
			accounts.put(ch, null);
		}
		accounts.put('g', customerList1);
		accounts.put('y', customerList2);
		accounts.put('n', customerList3);
		accounts.put('j', customerList4);
		loans.put(1, loan1);
		loans.put(2, loan2);
		loans.put(3, loan3);
		loans.put(4, loan4);
		// Spawn a new thread to listen to the upcoming UDP packet
		BankAsReceiver receiver = new BankAsReceiver();
		receiver.start();
	}

	/**
	 * Customer use to open an account in the bank
	 * 
	 * @param bank
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param phoneNumber
	 * @param password
	 * @return customer's account number
	 */
	@Override
	public String openAccount(String Bank, String fName, String lName, String email, String phoneNumber,
			String password) {
		String accountNumber;
		Customer customer = new Customer(null, fName, lName, email, phoneNumber, password);
		Character key = fName.charAt(0);
		int accountNum = (fName + lName).hashCode();
		// Lock the key
		synchronized (key) {
			List<Customer> customerList = accounts.get(key);
			if (customerList != null) {
				for (int i = 0; i < customerList.size(); i++) {
					if (customerList.get(i).getFirstName().equals(fName)) {
						return customerList.get(i).getAccountNumber();
					}
				}
			}
			// if cannot find means this is the new customer
			accountNumber = Integer.toString(accountNum);
			customer = new Customer(accountNumber, fName, lName, email, phoneNumber, password);
			customerList = new LinkedList<Customer>();
			customerList.add(customer);
			accounts.put(key, customerList);
			return accountNumber;
		}
	}

	/**
	 * Customer use to get loan from the bank
	 * 
	 * @param bank
	 * @param accountNumber
	 * @param password
	 * @param loanAmount
	 * @return loan information
	 */
	@Override
	public String getLoan(String Bank, String accountNumber, String password, String loanAmount) {
		int loanAmountOfThisBank = 0;
		for (Character key : accounts.keySet()) {
			if (accounts.get(key) != null) {
				List<Customer> listCustomer = accounts.get(key);
				for (int i = 0; i < listCustomer.size(); i++) {
					Customer cus = listCustomer.get(i);
					if (cus.getAccountNumber().equals(accountNumber) && cus.getPassword().equals(password)) {
						for (Integer dd : loans.keySet()) {
							if (loans.get(dd).getAccountNumber().equals(accountNumber)) {
								loanAmountOfThisBank += Integer.parseInt(loans.get(dd).getLoanAmt());
							}
						}
						loanAmountOfThisBank += Integer.parseInt(loanAmount);
						// Check if the customer has exceed the limit
						BankAsClient client0 = new BankAsClient(rest_port[0],
								"search" + ":" + cus.getFirstName() + "," + cus.getAccountNumber() + ":");
						BankAsClient client1 = new BankAsClient(rest_port[1],
								"search" + ":" + cus.getFirstName() + "," + cus.getAccountNumber() + ":");

						Thread th1 = client0.start();
						Thread th2 = client1.start();
						try {
							th1.join();
							th2.join();
						} catch (Exception ex) {
							return ex.getMessage();
						}

						if (loanAmountOfThisBank + Integer.parseInt(client0.getResult().trim())
								+ Integer.parseInt(client1.getResult().trim()) < 1000) {
							int newLoanID = 100000 + (int) (System.currentTimeMillis() % 10000);
							String loanId = Integer.toString(newLoanID);
							Loan newLoan = new Loan(loanId, accountNumber, loanAmount, "2016-1-1");
							if (!loans.containsKey(loanId))
								loans.put(Integer.parseInt(loanId), newLoan);
							String returnLoanInfo = newLoan.getLoanId();
							System.out.println(returnLoanInfo);
							return returnLoanInfo;
						}
					}
				}
			}
		}
		return "FAIL";
	}

	/**
	 * Manager use this function to delay payment due for the customer
	 * 
	 * @param bank
	 * @param loanID
	 * @param currentDueDate
	 * @param newDueDate
	 * @return if successfully delay the loan
	 * 
	 */
	@Override
	public String delayPayment(String Bank, String loanID, String currentD, String newD) {
		for (Integer key : loans.keySet()) {
			if (loans.get(key).getLoanId().equals(loanID)) {
				loans.get(key).setDueDate(newD);
				System.out.println("Current Loan [loanId=" + loans.get(key).getLoanId() + ", accountNumber="
						+ loans.get(key).getAccountNumber() + ", loanAmt=" + loans.get(key).getLoanAmt() + ", dueDate="
						+ loans.get(key).getDueDate() + "]");
				return loans.get(key).getDueDate();
			}
		}
		return "FAIL";
	}

	/**
	 * Manager use to print all the customer information in the bank
	 * 
	 * @param bank
	 * @return all the customers
	 */
	@Override
	public String printCustomerInfo(String Bank) {
		String info = "Information:\n";
		for (Character key : accounts.keySet()) {
			if (accounts.get(key) != null) {
				List<Customer> printList = accounts.get(key);
				for (int i = 0; i < printList.size(); i++) {
					Customer cus = printList.get(i);
					info += ("Customer [accountNumber=" + cus.getAccountNumber() + ", firstName=" + cus.getFirstName()
							+ ", lastName=" + cus.getLastName() + ", email=" + cus.getEmail() + ", phoneNumber="
							+ cus.getPhoneNumber() + ", creditLimit=" + cus.getCreditLimit() + "]\n");
				}
			}
		}
		return info;
	}

	@Override
	public String transferLoan(String loanID, String currentBank, String otherBank) {
		int portNumber;
		if (currentBank == "1") {
			if (otherBank == "2") {
				portNumber = rest_port[0];
			} else {
				portNumber = rest_port[1];
			}
		} else if (currentBank == "2") {
			if (otherBank == "1") {
				portNumber = rest_port[0];
			} else {
				portNumber = rest_port[1];
			}
		} else {
			if (otherBank == "1") {
				portNumber = rest_port[0];
			} else {
				portNumber = rest_port[1];
			}
		}
		if (loans.get(loanID) == null) {
			return "NotFoundLoan";
		}
		for (Integer key : loans.keySet()) {
			if (loans.get(key).getLoanId().equals(loanID)) {
				String accountNumber = loans.get(key).getAccountNumber();
				String id = loanID;
				String loanAmount = loans.get(key).getLoanAmt();
				String dueDate = loans.get(key).getDueDate();
				Customer transferCustomer = null;
				// Get corresponding customer information
				for (Character ch : accounts.keySet()) {
					if (accounts.get(ch) != null) {
						List<Customer> customerList = accounts.get(ch);
						for (int i = 0; i < customerList.size(); i++) {
							Customer cus = customerList.get(i);
							if (cus.getAccountNumber().equals(accountNumber)) {
								transferCustomer = cus;
							}
						}
					}
				}
				String message = "transfer" + ":" + accountNumber + "," + id + "," + loanAmount + "," + dueDate + "#"
						+ transferCustomer.getAccountNumber() + "," + transferCustomer.getFirstName() + ","
						+ transferCustomer.getLastName() + "," + transferCustomer.getEmail() + ","
						+ transferCustomer.getPhoneNumber() + "," + transferCustomer.getPassword() + ","
						+ transferCustomer.getCreditLimit() + ":";
				synchronized (transferCustomer) {
					BankAsClient client = new BankAsClient(portNumber, message);
					Thread th = client.start();
					try {
						th.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					/* The transfer has not been done */
					if (client.getResult().equals("No"))
						return "FAIL";
					/* The transfer has been done, try to delete this account */
					if (loans.remove(key, loans.get(key)))
						return "Done";
					else {
						message = "rollback:" + transferCustomer.getFirstName() + "," + accountNumber + "," + id + ":";
						;
						client = new BankAsClient(Integer.valueOf(otherBank), message);
						Thread th1 = client.start();
						try {
							th1.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return "FAIL";
	}

	class BankAsReceiver implements Runnable {
		public void start() {
			new Thread(this).start();
		}

		@SuppressWarnings("resource")
		@Override
		public void run() {
			try {
				DatagramSocket serverSocket = new DatagramSocket(port);
				byte[] receiveData = new byte[1024];
				byte[] sendData = new byte[1024];
				while (true) {
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					serverSocket.receive(receivePacket);
					String sentence = new String(receivePacket.getData());
					InetAddress IPAddress = receivePacket.getAddress();
					int port = receivePacket.getPort();
					String[] request_array = sentence.split(":");
					System.out.println(request_array[0]);
					if (request_array[0].equals("search")) {
						String[] content_array = request_array[1].split(",");
						Customer foundAccount = null;
						for (Character key : accounts.keySet()) {
							if (accounts.get(key) != null) {
								List<Customer> listCustomer = accounts.get(key);
								for (int i = 0; i < listCustomer.size(); i++) {
									Customer cus = listCustomer.get(i);
									if (cus.getAccountNumber().equals(content_array[1])) {
										foundAccount = cus;
										break;
									}
								}
							}
						}

						if (foundAccount == null) {
							sendData = Integer.toString(0).getBytes();
						} else {
							int total = 0;
							for (Integer key : loans.keySet()) {
								if (loans.get(key).getAccountNumber().equals(foundAccount.getAccountNumber())) {
									total += Integer.parseInt(loans.get(key).getLoanAmt());
								}
							}
							sendData = Integer.toString(total).getBytes();
						}
					} else if (request_array[0].equals("transfer")) {
						String[] content_array = request_array[1].split("#");
						String[] loan_info = content_array[0].split(",");
						Loan newLoan = new Loan(loan_info[0], loan_info[1], loan_info[2], loan_info[3]);
						String[] account_info = content_array[1].split(",");
						Customer newCustomer = new Customer(account_info[0], account_info[1], account_info[2],
								account_info[3], account_info[4], account_info[5]);
						loans.put(Integer.parseInt(newLoan.getLoanId()), newLoan);
						List<Customer> list = new LinkedList<Customer>(
								accounts.get(newCustomer.getFirstName().charAt(0)));
						list.add(newCustomer);
						accounts.put(newCustomer.getFirstName().charAt(0), list);
						sendData = "Yes".getBytes();
					} else if (request_array[0].equals("rollback")) {
						String[] content_array = request_array[1].split("#");
						List<Customer> list = accounts.get(content_array[0].charAt(0));
						Customer foundCustomer = null;
						for (Customer cus : list) {
							if (cus.getAccountNumber().equals(content_array[1])) {
								foundCustomer = cus;
								break;
							}
						}
						if (foundCustomer != null) {
							list.remove(foundCustomer);
						}
						if (loans.get(content_array[2]) != null) {
							loans.remove(content_array[2]);
						}
					}
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
					serverSocket.send(sendPacket);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	class BankAsClient implements Runnable {

		private int otherBankPort;
		private String content;
		private String result;

		public BankAsClient(int otherBankPort, String content) {
			this.otherBankPort = otherBankPort;
			this.content = content;
		}

		public Thread start() {
			Thread thread = new Thread(this);
			thread.start();
			return thread;
		}

		public String getResult() {
			return result;
		}

		@Override
		public void run() {
			try {
				DatagramSocket clientSocket = new DatagramSocket();
				InetAddress IPAddress = InetAddress.getByName("localhost");
				byte[] sendData = new byte[1024];
				byte[] receiveData = new byte[1024];

				sendData = content.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, otherBankPort);
				clientSocket.send(sendPacket);
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				clientSocket.receive(receivePacket);
				String reply = new String(receivePacket.getData());
				clientSocket.close();
				String[] reply_array = reply.split(":");
				result = reply_array[0];
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}

	}

}
