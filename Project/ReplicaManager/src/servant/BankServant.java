package servant;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.Account;
import model.Loan;
import rm.ReplicaManager;

/**
 *
 * @author yucunli
 */
public class BankServant implements BankServantInterface{
    public int port;
    public int[] other_port;
    
    public BankServant(){
    }
    
    //Initialize
    public BankServant(int _port, int[] _other_port){
        this.port = _port;
        
        this.other_port = _other_port;
    }
    
    //Initialize from other server
    public BankServant(int _port, int[] _other_port, int _target_port){
        this.port = _port;
        this.other_port = _other_port;
        
        this.other_port = _other_port;
        
        // recovering data from _target_port bank servant
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
    
    HashMap<String, ArrayList<Account>> account_HashMap;
    HashMap<String, Loan> loan_HashMap;

    @Override
    public String openAccount(String bank, String firstName, String lastName, String emailAddress, String phoneNumber, String password) {
        Account account = null;
        
        Account foundAccount = null;
        ArrayList<Account> list = account_HashMap.get(lastName.toLowerCase().substring(0, 1));
        for(Account temp : list){
            if(temp.firstName.equals(firstName) && temp.lastName.equals(lastName)){
                foundAccount = temp;
            }
        }
        
        if(foundAccount == null){
            
            account = new Account(firstName, lastName, emailAddress, phoneNumber, password, DEFAULT_CREDIT);
            list.add(account);
            
            //log(firstName + lastName + " " + " create account : " + account.accountNumber);
            //logCustomer(account.accountNumber, "account created");
            return account.accountNumber;
        }else{
            return foundAccount.accountNumber;
        }
    }

    @Override
    public String getLoan(String bank, String accountNumber, String password, int loanAmount) {
        Account foundAccount = null;
        Loan loan = null;
        
        for(ArrayList<Account> account_list : account_HashMap.values()){
            for(Account account : account_list){
                if(account.accountNumber.equals(accountNumber)){
                    foundAccount = account;
                    break;
                }
            }
        }
        
        if(foundAccount != null && foundAccount.password.equals(password)){
            
            BankAsClient client0 = new BankAsClient(other_port[0], "search"+":"+foundAccount.firstName+","+foundAccount.lastName+":");
            BankAsClient client1 = new BankAsClient(other_port[1], "search"+":"+foundAccount.firstName+","+foundAccount.lastName+":");
            
            Thread th1 = client0.start();
            Thread th2 = client1.start();
            try{
                th1.join();
                th2.join();
            }catch(Exception ex){
                return ex.getMessage();
            }
            
            //get foundAccount debt
            int debt = 0;
            for(Loan temp : loan_HashMap.values()){
                if(temp.accountNumber.equals(foundAccount.accountNumber)){
                    debt += temp.amount;
                }
            }
            debt += Integer.parseInt(client0.getResult()) + Integer.parseInt(client1.getResult());
            
            if(foundAccount.creditLimit - debt >= 0){
                loan = new Loan(accountNumber, loanAmount, DEFAULT_DUEDATE);
                loan_HashMap.put(loan.ID, loan);
            }
        }
        
        if(loan == null){
            return "FAIL";
        }else{
            //logCustomer(accountNumber, "GetLoan performed \n" );
            //log("Account " + accountNumber + " tried to get loan, and the result shows ");
            return loan.ID;
        }
    }

    @Override
    public String transferLoan(String loanID, String currentBank, String otherBank) {
        if(loan_HashMap.get(loanID) == null){
            return "NotFoundLoan";
        }
        
        Loan loan = loan_HashMap.get(loanID);
        Account foundAccount = null;
        
        for(ArrayList<Account> account_list : account_HashMap.values()){
            for(Account account : account_list){
                if(account.accountNumber.equals(loan.accountNumber)){
                    foundAccount = account;
                    break;
                }
            }
        }
        
        synchronized(foundAccount){
            
            try{
                String content = "transfer" + ":" + loan.ID + "," + loan.accountNumber + "," + loan.dueDate + "," + loan.amount 
                            + "#" + foundAccount.accountNumber + "," + foundAccount.firstName + "," + foundAccount.lastName + "," + foundAccount.emailAddress + "," + foundAccount.phoneNumber + "," + foundAccount.password + "," + foundAccount.creditLimit
                            + ":";
                BankAsClient client = new BankAsClient(Integer.valueOf(otherBank), content);
                Thread th = client.start();
                th.join();

                // return result Yes/True
                // operate on local database
                if(client.getResult().equals("No")){
                    //do nothing, just return
                    return "FAIL";
                }
                
                if(client.getResult().equals("Yes")){
                    //if operation done well
                    //if not well -> roll back
                    loan_HashMap.remove(loan);
                    if(loan_HashMap.get(loan.ID) != null){
                        content = "rollback"+":"+foundAccount.lastName+","+foundAccount.accountNumber+","+loan.ID+":";
                        client = new BankAsClient(Integer.valueOf(otherBank), content);
                        Thread th1 = client.start();
                        th1.join();

                        if(client.getResult().equals("No")){
                            //do nothing, just return
                            return "FAIL";
                        }
                    }else{
                        foundAccount.creditLimit = foundAccount.creditLimit + loan.amount;
                        content = "transferDone"+":"+loan.ID+","+":";
                        client = new BankAsClient(Integer.valueOf(otherBank), content);
                        Thread th1 = client.start();
                        th1.join();
                    }
                }

            }catch(Exception ex){
                System.out.println(ex.toString());
            }
        }
        
        return "DONE";
    }

    @Override
    public String delayPayment(String bank, String loanID, String currentDueDate, String newDueDate) {
        Loan loan = loan_HashMap.get(loanID);
        
        if(loan == null){
            return "FAIL";
        }
        
        synchronized(loan){
            loan.dueDate = newDueDate;
        }
        
        //log("Loan " + loanID + " has been delayed from " + currentDueDate + " to " + newDueDate);
        //logManager("Loan " + loanID + " has been delayed from " + currentDueDate + " to " + newDueDate);
        
        return newDueDate;
    }

    @Override
    public String printCustomerInfo(String bank) {
        StringBuilder result = new StringBuilder();
        for(String ch : alphabet){
            ArrayList<Account> list = account_HashMap.get(ch);
            for(Account account : list){
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
    class BankAsClient implements Runnable{

        private int otherBankPort;
        private String content;
        private String result;

        public BankAsClient(int otherBankPort, String content){
            this.otherBankPort = otherBankPort;
            this.content = content;
        }

        public Thread start(){
            Thread thread = new Thread(this);
            thread.start();
            return thread;
        }

        public String getResult(){
            return result;
        }

        @Override
        public void run() {
            try{
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

                }catch(Exception e){
                    System.out.println("**********************");
                    System.out.println("BankAsClient Problem Happened");
                    System.out.println("**********************");
                    System.out.println(e.toString());
                }
        }
    }
    
    class BankAsReceiver implements Runnable{
        
        public void start(){
            new Thread(this).start();
        }

        @Override
        public void run() {
            try{
            
                DatagramSocket serverSocket = new DatagramSocket(port);
                
                byte[] receiveData = new byte[1024];
                byte[] sendData = new byte[1024];

                while(true){
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);
                    String sentence = new String(receivePacket.getData());
                    InetAddress IPAddress = receivePacket.getAddress();
                    int port = receivePacket.getPort();

                    String[] request_array = sentence.split(":");
                    if(request_array[0].equals("search")){
                        String[] content_array = request_array[1].split(",");
                        ArrayList<Account> list = account_HashMap.get(content_array[0].toLowerCase().substring(0, 1));
                        Account foundAccount = null;
                        for(Account account : list){
                            if(account.emailAddress.equals(content_array[1])){
                                foundAccount = account;
                                break;
                            }
                        }
                        
                        if(foundAccount == null){
                            sendData = "0".getBytes();
                        }else{
                            int debt = 0;
                            for(Loan loan : loan_HashMap.values()){
                                if(loan.accountNumber == foundAccount.accountNumber){
                                    debt += loan.amount;
                                }
                            }
                            
                            sendData = (debt+"").getBytes();
                        }
                        
                    }else if(request_array[0].equals("transfer")){
                        String[] content_array = request_array[1].split("#");
                        String[] loan_info = content_array[0].split(",");
                        Loan loan = new Loan();
                        loan.ID = loan_info[0];
                        loan.accountNumber = loan_info[1];
                        loan.dueDate = loan_info[2];
                        loan.amount = Integer.parseInt(loan_info[3]);
                        
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
                        
                        if(loan_HashMap.get(loan.ID)!=null && list.contains(account)){
                            sendData = "Yes".getBytes();
                        }else{
                            if(loan_HashMap.get(loan.ID)!=null){
                                loan_HashMap.remove(loan.ID);
                            }
                            if(list.contains(account)){
                                list.remove(account);
                            }
                            
                            sendData = "No".getBytes();
                        }
                        
                        // thread used to lock loan object
                        Thread thread = new Thread(){
                            @Override
                            public void run(){
                                synchronized(loan){
                                    try {
                                        loan.wait();
                                    } catch (InterruptedException ex) {
                                        System.out.println(ex.toString());
                                    }
                                }
                                
                            }
                        };
                        thread.start();
                        
                    }else if(request_array[0].equals("rollback")){
                        String[] content_array = request_array[1].split(",");
                        Account foundAccount = null;
                        List<Account> account_list = account_HashMap.get(content_array[0].toLowerCase().substring(0, 1));
                        for(Account account : account_list){
                            if(account.accountNumber.equals(content_array[1])){
                                foundAccount = account;
                                break;
                            }
                        }
                        if(foundAccount != null){
                            account_list.remove(foundAccount);
                        }
                        if(loan_HashMap.get(content_array[2])!=null){
                            Loan loan = loan_HashMap.get(content_array[2]);
                            
                            //unlock loan object
                            synchronized(loan){
                                loan_HashMap.remove(content_array[2]);
                                loan.notify();
                            }
                            
                        }
                    }else if(request_array[0].equals("transferDone")){
                        String[] content_array = request_array[1].split(",");
                        Loan loan = loan_HashMap.get(content_array[0]);
                        //unlock loan object
                        synchronized(loan){
                            loan_HashMap.remove(content_array[2]);
                            loan.notify();
                        }
                    }

                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                    serverSocket.send(sendPacket);
                }
            
            }catch(Exception e){
                System.out.println("**********************");
                System.out.println("BankAsReceiver Happened Problem");
                System.out.println("**********************");
                System.out.println(e.toString());
            }
        }
    }
}
