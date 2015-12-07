package servant;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model2.Customer;
import model2.Loan;

public class dlmsImpl {

    private HashMap<String, List<Customer>> accounts = new HashMap<String, List<Customer>>();
    ;
	private HashMap<Integer, Loan> loans = new HashMap<Integer, Loan>();
    public static final String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
        "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    public String bankName;
    public HashMap<String, Integer> portMap;
    private int lastSeq = 0;
    public int rmPort;
    public int fePort;

    public dlmsImpl(String bankName, HashMap<String, Integer> portMap, int rmPort, int fePort) {
        this.bankName = bankName;
        this.portMap = portMap;
        this.fePort = fePort;
        this.rmPort = rmPort;
        for (String ss : alphabet) {
            accounts.put(ss, new LinkedList<Customer>());
        }

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
    public String openAccount(String Bank, String fName, String lName, String email, String phoneNumber,
            String password) {
        String accountNumber;
        Customer customer = new Customer(null, fName, lName, email, phoneNumber, password);
        String key = lName.substring(0, 1);
        int accountNum = (fName + lName).hashCode();
        // Lock the key
        synchronized (key) {
            List<Customer> customerList = accounts.get(key);
            if (customerList != null) {
                for (int i = 0; i < customerList.size(); i++) {
                    if (customerList.get(i).getLastName().equals(lName)) {
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
    public String getLoan(String Bank, String accountNumber, String password, String loanAmount) {
        int loanAmountOfThisBank = 0;
        for (String key : accounts.keySet()) {
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
                        int[] otherPort = new int[2];
                        int a = 0;
                        for (String s : portMap.keySet()) {
                            if (!s.equals(Bank)) {
                                otherPort[a++] = portMap.get(s);
                            }
                        }
                        // Check if the customer has exceed the limit
                        BankAsClient client0 = new BankAsClient(otherPort[0],
                                "search" + ":" + cus.getLastName()+ "," + cus.getAccountNumber() + ":");
                        BankAsClient client1 = new BankAsClient(otherPort[1],
                                "search" + ":" + cus.getLastName()+ "," + cus.getAccountNumber() + ":");

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
                            int newLoanID = lastSeq+1;
                            String loanId = Integer.toString(newLoanID);
                            Loan newLoan = new Loan(loanId, accountNumber, loanAmount, "2016-1-1");
                            if (!loans.containsKey(loanId)) {
                                loans.put(Integer.parseInt(loanId), newLoan);
                            }
                            return Integer.toString(newLoanID);
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
    public String printCustomerInfo(String Bank) {
        String info = "Information:\n";
        for (String key : accounts.keySet()) {
            if (accounts.get(key) != null) {
                List<Customer> printList = accounts.get(key);
                for (int i = 0; i < printList.size(); i++) {
                    Customer cus = printList.get(i);
                    info += ("Customer [accountNumber=" + cus.getAccountNumber() + ", firstName=" + cus.getFirstName()
                            + ", lastName=" + cus.getLastName() + ", email=" + cus.getEmail() + ", phoneNumber="
                            + cus.getPhoneNumber() + ", password=" + cus.getPassword() + ", creditLimit="
                            + cus.getCreditLimit() + "]\n");
                }
            }
        }
        return info;
    }

    public String transferLoan(String loanID, String currentBank, String otherBank) {
        int portNumber = 0;
        for (String s : portMap.keySet()) {
            if (s.equals(otherBank)) {
                portNumber = portMap.get(s);
            }
        }
//      
        if (loans.get(Integer.parseInt(loanID)) == null) {
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
                for (String ss : accounts.keySet()) {
                    if (accounts.get(ss) != null) {
                        List<Customer> customerList = accounts.get(ss);
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
                    if (client.getResult().equals("No")) {
                        return "FAIL";
                    }
                    /* The transfer has been done, try to delete this account */
                    if (loans.remove(key, loans.get(key))) {
                        return "Done";
                    } else {
                        message = "rollback:" + transferCustomer.getLastName()+ "," + accountNumber + "," + id + ":";
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

    class BankAsReceiver extends Thread {

        @Override
        public void run() {
            try {
                DatagramSocket serverSocket = new DatagramSocket(portMap.get(bankName));

                while (true) {
                    byte[] receiveData = new byte[1024];
                    byte[] sendData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);
                    String sentence = new String(receivePacket.getData());

                    if (sentence.contains("%")) {
                        String elements[] = sentence.split("%");
                        String func[] = elements[elements.length - 1].split("#");
                        String funcPara[] = func[1].split(",");
                        String first = func[0];
                        String send = null;
                        if (Integer.parseInt(elements[0]) == (lastSeq + 1)) {
                            if (first.equals("openAccount")) {
                                send = openAccount(funcPara[0], funcPara[1], funcPara[2], funcPara[3], funcPara[4], funcPara[5]);
                            } else if (first.equals("getLoan")) {
                                send = getLoan(funcPara[0], funcPara[1], funcPara[2], funcPara[3]);
                            } else if (first.equals("transferLoan")) {
                                send = transferLoan(funcPara[0], funcPara[1], funcPara[2]);
                            } else if (first.equals("delayPayment")) {
                                send = delayPayment(funcPara[0], funcPara[1], funcPara[2], funcPara[3]);
                            } else if (first.equals("printCustomerInfo")) {
                                send = printCustomerInfo(funcPara[0]);
                            }
                            lastSeq++;
                        } else {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            continue;
                        }
                        String message = Integer.toString(lastSeq) + "%" + Integer.toString(rmPort) + "#" + send + "#";
                        BankAsSend bankAsSend = new BankAsSend(fePort, message);
                        bankAsSend.start();
                    } else {
                        
                        InetAddress IPAddress = receivePacket.getAddress();
                        int port = receivePacket.getPort();
                        String[] request_array = sentence.split(":");
                        
                        if (request_array[0].equals("search")) {
                            String[] content_array = request_array[1].split(",");
                            Customer foundAccount = null;
                            for (String key : accounts.keySet()) {
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
                            Loan newLoan = new Loan(loan_info[1], loan_info[0], loan_info[2], loan_info[3]);
                            String[] account_info = content_array[1].split(",");
                            Customer newCustomer = new Customer(account_info[0], account_info[1], account_info[2],
                                    account_info[3], account_info[4], account_info[5]);
                            String keyForSearchCustomer = account_info[2].substring(0,1);
                            loans.put(Integer.parseInt(newLoan.getLoanId()), newLoan);
                            List<Customer> list = new LinkedList<Customer>(
                                    accounts.get(keyForSearchCustomer));
                            list.add(newCustomer);
                            accounts.put(newCustomer.getLastName().substring(0, 1), list);
                            sendData = "Yes".getBytes();
                        } else if (request_array[0].equals("rollback")) {
                            String[] content_array = request_array[1].split("#");
                            List<Customer> list = accounts.get(content_array[0].substring(0, 1));
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

                }

            } catch (IOException e) {
                e.printStackTrace();
            } 
        }

    }

    class BankAsSend extends Thread {

        private int otherBankPort;
        private String content;
        private String result;

        public BankAsSend(int otherBankPort, String content) {
            this.otherBankPort = otherBankPort;
            this.content = content;
        }

        public void run() {
            try {
                DatagramSocket clientSocket = new DatagramSocket();
                InetAddress IPAddress = InetAddress.getByName("localhost");
                byte[] sendData = new byte[1024];
                
                sendData = content.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, otherBankPort);
                clientSocket.send(sendPacket);
                clientSocket.close();
                
            } catch (Exception e) {
                System.out.println(e);
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
