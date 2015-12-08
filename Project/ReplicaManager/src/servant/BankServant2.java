package servant;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.Account;
import model.Loan;
import rm.ReplicaManager1;

/**
 *
 * @author yucunli
 */
public class BankServant2 implements BankServantInterface {

    public int rm_port;
    public String bankName;
    public HashMap<String, Integer> port_map;
    public int fe_port;

    public BankServant2() {
    }

    //Initialize
    public BankServant2(String _bankName, HashMap<String, Integer> _port_map, int rm_port, int fe_port) {
        account_HashMap = new HashMap<String, ArrayList<Account>>();
        loan_HashMap = new HashMap<String, Loan>();
        
        for(String ch : alphabet){
            account_HashMap.put(ch, new ArrayList<Account>());
        }
        
        this.bankName = _bankName;

        this.port_map = _port_map;
        
        this.rm_port = rm_port;
        this.fe_port = fe_port;
        
        BankAsReceiver bankAsReceiver = new BankAsReceiver();
        bankAsReceiver.start();
    }

    //Initialize from other server
    public BankServant2(String _bankName, HashMap<String, Integer> _port_map, int _target_port, int rm_port, int fe_port) {
        account_HashMap = new HashMap<String, ArrayList<Account>>();
        loan_HashMap = new HashMap<String, Loan>();
        
        for(String ch : alphabet){
            account_HashMap.put(ch, new ArrayList<Account>());
        }
        
        this.bankName = _bankName;

        this.port_map = _port_map;
        
        this.rm_port = rm_port;
        this.fe_port = fe_port;

        // recovering data from _target_port bank servant
    }
    
    public void refreshHashMap(){
        account_HashMap.clear();
        loan_HashMap.clear();
        
        for(String ch : alphabet){
            account_HashMap.put(ch, new ArrayList<Account>());
        }
    }

    //*****************************
    //*****************************
    //Code From Previous Assignment
    //*****************************
    //*****************************
    public static final String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
        "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

    private static final int DEFAULT_CREDIT = 1000;
    private static final String DEFAULT_DUEDATE = "2016-1-1";

    public HashMap<String, ArrayList<Account>> account_HashMap;
    public HashMap<String, Loan> loan_HashMap;

    @Override
    public String openAccount(String bank, String firstName, String lastName, String emailAddress, String phoneNumber, String password) {
        Account account = null;

        Account foundAccount = null;
        ArrayList<Account> list = account_HashMap.get(lastName.toLowerCase().substring(0, 1));
        for (Account temp : list) {
            if (temp.firstName.equals(firstName) && temp.lastName.equals(lastName)) {
                foundAccount = temp;
            }
        }

        if (foundAccount == null) {

            account = new Account(firstName, lastName, emailAddress, phoneNumber, password, DEFAULT_CREDIT);
            list.add(account);

            //log(firstName + lastName + " " + " create account : " + account.accountNumber);
            //logCustomer(account.accountNumber, "account created");
            return account.accountNumber;
        } else {
            return foundAccount.accountNumber;
        }
    }

    @Override
    public String getLoan(String bank, String accountNumber, String password, String loanAmount, String sequenceId) {
        Account foundAccount = null;
        Loan loan = null;

        for (ArrayList<Account> account_list : account_HashMap.values()) {
            for (Account account : account_list) {
                if (account.accountNumber.equals(accountNumber)) {
                    foundAccount = account;
                    break;
                }
            }
        }

        if (foundAccount != null && foundAccount.password.equals(password)) {
            
            int[] rest_port = new int[2];
            int count = 0;
            for(String s : port_map.keySet()){
                if(!s.equals(bankName)){
                    rest_port[count] = port_map.get(s);
                    count++;
                }
            }

            BankAsClient client0 = new BankAsClient(rest_port[0], "search" + ":" + foundAccount.firstName + "," + foundAccount.lastName + ":");
            BankAsClient client1 = new BankAsClient(rest_port[1], "search" + ":" + foundAccount.firstName + "," + foundAccount.lastName + ":");

            client0.start();
            client1.start();
            try {
                client0.join();
                client1.join();
            } catch (Exception ex) {
                return ex.getMessage();
            }

            //get foundAccount debt
            int debt = 0;
            for (Loan temp : loan_HashMap.values()) {
                if (temp.accountNumber.equals(foundAccount.accountNumber)) {
                    debt += Integer.parseInt(temp.amount);
                }
            }
            debt += Integer.parseInt(client0.getResult()) + Integer.parseInt(client1.getResult());

            if (foundAccount.creditLimit - debt >= 0) {
                loan = new Loan(sequenceId, accountNumber, loanAmount, DEFAULT_DUEDATE);
                loan_HashMap.put(loan.ID, loan);
            }
        }

        if (loan == null) {
            return "FAIL";
        } else {
            //logCustomer(accountNumber, "GetLoan performed \n" );
            //log("Account " + accountNumber + " tried to get loan, and the result shows ");
            return loan.ID;
        }
    }

    @Override
    public String transferLoan(String loanID, String currentBank, String otherBank) {
        if (loan_HashMap.get(loanID) == null) {
            return "NotFoundLoan";
        }

        Loan loan = loan_HashMap.get(loanID);
        Account foundAccount = null;

        for (ArrayList<Account> account_list : account_HashMap.values()) {
            for (Account account : account_list) {
                if (account.accountNumber.equals(loan.accountNumber)) {
                    foundAccount = account;
                    break;
                }
            }
        }

        synchronized (foundAccount) {

            try {
                String content = "transfer" + ":" + loan.ID + "," + loan.accountNumber + "," + loan.dueDate + "," + loan.amount
                        + "#" + foundAccount.accountNumber + "," + foundAccount.firstName + "," + foundAccount.lastName + "," + foundAccount.emailAddress + "," + foundAccount.phoneNumber + "," + foundAccount.password + "," + foundAccount.creditLimit
                        + ":";
                BankAsClient client = new BankAsClient(Integer.valueOf(port_map.get(otherBank)), content);
                client.run();

                // return result Yes/True
                // operate on local database
                if (client.getResult().equals("No")) {
                    //do nothing, just return
                    return "FAIL";
                }

                if (client.getResult().equals("Yes")) {
                    //if operation done well
                    //if not well -> roll back
                    loan_HashMap.remove(loan.ID);
                    if (loan_HashMap.get(loan.ID) != null) {
                        content = "rollback" + ":" + foundAccount.lastName + "," + foundAccount.accountNumber + "," + loan.ID + ":";
                        client = new BankAsClient(Integer.valueOf(port_map.get(otherBank)), content);
                        client.run();

                        if (client.getResult().equals("No")) {
                            //do nothing, just return
                            return "FAIL";
                        }
                    } else {
                        
                        content = "transferDone" + ":" + loan.ID + "," + ":";
                        client = new BankAsClient(Integer.valueOf(port_map.get(otherBank)), content);
                        client.start();
                        client.join();
                    }
                }

            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
        }

        return "DONE";
    }

    @Override
    public String delayPayment(String bank, String loanID, String currentDueDate, String newDueDate) {
        Loan loan = loan_HashMap.get(loanID);

        if (loan == null) {
            return "FAIL";
        }

        synchronized (loan) {
            loan.dueDate = newDueDate;
        }

        //log("Loan " + loanID + " has been delayed from " + currentDueDate + " to " + newDueDate);
        //logManager("Loan " + loanID + " has been delayed from " + currentDueDate + " to " + newDueDate);
        return newDueDate;
    }

    @Override
    public String printCustomerInfo(String bank) {
        StringBuilder result = new StringBuilder();
        for (String ch : alphabet) {
            ArrayList<Account> list = account_HashMap.get(ch);
            for (Account account : list) {
                result.append(account.toString());
            }
        }

        //log("printCustomerInfo has been called");
        //logManager("printCustomerInfo has been called");
        return result.toString();
    }

    //*****************************
    //*****************************
    //Customized Receiver and Sender
    //*****************************
    //*****************************
    // getLoan: need search other bank
    // transferLoan: need send loan info, send back successful info
    // recover process: send whole database to other bank server cluster
    // send to front end
    class BankAsClient extends Thread {

        private int otherBankPort;
        private String content;
        private String result;

        public BankAsClient(int otherBankPort, String content) {
            this.otherBankPort = otherBankPort;
            this.content = content;
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
                
                if(content.contains(":")){
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    clientSocket.receive(receivePacket);
                    String reply = new String(receivePacket.getData());
                    
                    result = reply.trim();
                }
               
                clientSocket.close();

            } catch (Exception e) {
                System.out.println("**********************");
                System.out.println("BankAsClient Problem Happened");
                System.out.println(e.toString());
                System.out.println("**********************");
            }
        }
    }

    class BankAsReceiver extends Thread {
        @Override
        public void run() {
            try {

                DatagramSocket serverSocket = new DatagramSocket(port_map.get(bankName));

                while (true) {
                    byte[] receiveData = new byte[1024];
                    byte[] sendData = new byte[1024];
                
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);
                    String sentence = new String(receivePacket.getData());
                    InetAddress IPAddress = receivePacket.getAddress();
                    int port = receivePacket.getPort();

                    System.out.println(sentence.trim());
                    
                    if(sentence.startsWith("shutdown")){
                        break;
                        
                    } else if (sentence.contains("%")) {
                        // message from sequencer
                        String[] message = sentence.split("%");
                        String sequenceId = message[0];
                        String[] request = message[1].split("#");
                        String requestType = request[0];
                        String[] requestPara = request[1].split(",");
                        
                        String result = "";
                        
                        switch(requestType){
                            case "openAccount":
                                result = sequenceId + "%" + rm_port + "#" + openAccount(requestPara[0], requestPara[1], requestPara[2], requestPara[3], requestPara[4], requestPara[5]) + "#";
                                
                                break;
                            case "getLoan":
                                result = sequenceId + "%" + rm_port + "#" + getLoan(requestPara[0], requestPara[1], requestPara[2], requestPara[3], sequenceId) + "#";
                                
                                break;
                            case "transferLoan":
                                result = sequenceId + "%" + rm_port + "#" + transferLoan(requestPara[0], requestPara[1], requestPara[2]) + "#";
                                
                                break;
                            case "delayLoan":
                                result = sequenceId + "%" + rm_port + "#" + delayPayment(requestPara[0], requestPara[1], requestPara[2], requestPara[3]) + "#";
                                
                                break;
                            case "printCustomerInfo":
                                result = sequenceId + "%" + rm_port + "#" + printCustomerInfo(requestPara[0]) + "#";
                                
                                break;
                        }
                        
                        // send result back to front end
                        BankAsClient client = new BankAsClient(fe_port, result);
                        client.start();

                    } else {
                        // message from internal bank cluster
                        String[] request_array = sentence.split(":");
                        if (request_array[0].equals("search")) {
                            String[] content_array = request_array[1].split(",");
                            ArrayList<Account> list = account_HashMap.get(content_array[0].toLowerCase().substring(0, 1));
                            Account foundAccount = null;
                            for (Account account : list) {
                                if (account.emailAddress.equals(content_array[1])) {
                                    foundAccount = account;
                                    break;
                                }
                            }

                            if (foundAccount == null) {
                                sendData = "0".getBytes();
                            } else {
                                int debt = 0;
                                for (Loan loan : loan_HashMap.values()) {
                                    if (loan.accountNumber == foundAccount.accountNumber) {
                                        debt += Integer.parseInt(loan.amount);
                                    }
                                }

                                sendData = (debt + "").getBytes();
                            }

                        } else if (request_array[0].equals("transfer")) {
                            String[] content_array = request_array[1].split("#");
                            String[] loan_info = content_array[0].split(",");
                            Loan loan = new Loan();
                            loan.ID = loan_info[0];
                            loan.accountNumber = loan_info[1];
                            loan.dueDate = loan_info[2];
                            loan.amount = loan_info[3];

                            String[] account_info = content_array[1].split(",");
                            Account account = new Account();
                            account.accountNumber = account_info[0];
                            account.firstName = account_info[1];
                            account.lastName = account_info[2];
                            account.emailAddress = account_info[3];
                            account.phoneNumber = account_info[4];
                            account.password = account_info[5];
                            account.creditLimit = Integer.valueOf(account_info[6]);

                            loan_HashMap.put(loan.ID, loan);
                            List<Account> list = account_HashMap.get(account.lastName.toLowerCase().substring(0, 1));
                            list.add(account);

                            if (loan_HashMap.get(loan.ID) != null && list.contains(account)) {
                                sendData = "Yes".getBytes();
                            } else {
                                if (loan_HashMap.get(loan.ID) != null) {
                                    loan_HashMap.remove(loan.ID);
                                }
                                if (list.contains(account)) {
                                    list.remove(account);
                                }

                                sendData = "No".getBytes();
                            }

                            // thread used to lock loan object
                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                    synchronized (loan) {
                                        try {
                                            loan.wait();
                                        } catch (InterruptedException ex) {
                                            System.out.println(ex.toString());
                                        }
                                    }

                                }
                            };
                            thread.start();

                        } else if (request_array[0].equals("rollback")) {
                            String[] content_array = request_array[1].split(",");
                            Account foundAccount = null;
                            List<Account> account_list = account_HashMap.get(content_array[0].toLowerCase().substring(0, 1));
                            for (Account account : account_list) {
                                if (account.accountNumber.equals(content_array[1])) {
                                    foundAccount = account;
                                    break;
                                }
                            }
                            if (foundAccount != null) {
                                account_list.remove(foundAccount);
                            }
                            if (loan_HashMap.get(content_array[2]) != null) {
                                Loan loan = loan_HashMap.get(content_array[2]);

                                //unlock loan object
                                synchronized (loan) {
                                    loan_HashMap.remove(content_array[2]);
                                    loan.notify();
                                }

                            }
                        } else if (request_array[0].equals("transferDone")) {
                            String[] content_array = request_array[1].split(",");
                            Loan loan = loan_HashMap.get(content_array[0]);
                            //unlock loan object
                            synchronized (loan) {
                                loan_HashMap.remove(content_array[0]);
                                loan.notify();
                            }
                        } 
                        
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                        serverSocket.send(sendPacket);
                    }
                }
                
                serverSocket.close();
                System.out.println("BankServant.class *"+serverSocket.getPort() +"* closed");

            } catch (Exception e) {
                System.out.println("**********************");
                System.out.println("BankAsReceiver Happened Problem");
                System.out.println(e.toString());
                System.out.println("**********************");
            } 
        }
    }
}
