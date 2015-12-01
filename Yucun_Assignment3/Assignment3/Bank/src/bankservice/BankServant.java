package bankservice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;
import model.Account;
import model.Loan;

/**
 *
 * @author yucunli
 */
@WebService(endpointInterface = "bankservice.BankServantInterface")
public class BankServant implements BankServantInterface{
    
    public static final String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
                        "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    
    private static final int DEFAULT_CREDIT = 500;
    private static final int DEFAULT_DUEDATE = 100;
    
    HashMap<String, ArrayList<Account>> account_HashMap;
    HashMap<String, Loan> loan_HashMap;
    
    public int port;
    public int[] rest_port = new int[2];
    public int uniqueAccountID_Base;
    public int loanID_Base;
    
    public BankServant(){
        account_HashMap = new HashMap<String, ArrayList<Account>>();
        loan_HashMap = new HashMap<String, Loan>();
        
        for(String ch : alphabet){
            account_HashMap.put(ch, new ArrayList<Account>());
        }
        
        BankAsReceiver receiver = new BankAsReceiver();
        receiver.start();
    }

    @Override
    public String openAccount(String bank, String firstName, String lastName, String emailAddress, String phoneNumber, String password) {
        Account account = null;
        
        boolean foundAccount = false;
        ArrayList<Account> list = account_HashMap.get(lastName.toLowerCase().substring(0, 1));
        for(Account temp : list){
            if(temp.emailAddress.equals(emailAddress)){
                foundAccount = true;
            }
        }
        
        if(!foundAccount){
            
            account = new Account(""+uniqueAccountID_Base, firstName, lastName, emailAddress, phoneNumber, password, DEFAULT_CREDIT);
            list.add(account);
            
            log(firstName + lastName + " " + " create account : " + account.accountNumber);
            logCustomer(account.accountNumber, "account created");
            return account.accountNumber + " has been created for user " + firstName + " " + lastName;
        }else{
            return "Acount is already existed!";
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
            
            BankAsClient client0 = new BankAsClient(rest_port[0], "search"+":"+foundAccount.lastName+","+foundAccount.emailAddress+ ":");
            BankAsClient client1 = new BankAsClient(rest_port[1], "search"+":"+foundAccount.lastName+","+foundAccount.emailAddress+ ":");
            
            Thread th1 = client0.start();
            Thread th2 = client1.start();
            try{
                th1.join();
                th2.join();
            }catch(Exception ex){
                return ex.getMessage();
            }
            
            
            if(!(client0.getResult().equals("Exceed") || client1.getResult().equals("Exceed"))){
                loan = new Loan();
                loan.ID = "" + loanID_Base;
                loan.accountNumber = accountNumber;
                loan.amount = loanAmount;
                loan.dueDate = DEFAULT_DUEDATE;
                
                loan_HashMap.put(loan.ID, loan);
                
                synchronized(foundAccount){
                    foundAccount.creditLimit -= loanAmount;
                }
            }
        }
        
        if(loan == null){
            return "Not able to create your loan request";
        }else{
            logCustomer(accountNumber, "GetLoan performed \n" );
            log("Account " + accountNumber + " tried to get loan, and the result shows ");
            return loan.ID + " has been created";
        }
    }

    @Override
    public String transferLoan(String loanID, String currentBank, String otherBank) {
        if(loan_HashMap.get(loanID) == null){
            return "Not found loan";
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
                    return "Please try again";
                }

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
                        return "Please try again";
                    }
                }else{
                    foundAccount.creditLimit = foundAccount.creditLimit + loan.amount;
                    content = "transferDone"+":"+loan.ID+","+":";
                    client = new BankAsClient(Integer.valueOf(otherBank), content);
                    Thread th1 = client.start();
                    th1.join();
                }
        
            }catch(Exception ex){
                System.out.println(ex.toString());
            }
            
        }
        
        return "Successful transfered loan";
    }

    @Override
    public String delayPayment(String bank, String loanID, int currentDueDate, int newDueDate) {
        Loan loan = loan_HashMap.get(loanID);
        
        if(loan == null){
            return "Not found loan by ID: " + loanID;
        }
        
        synchronized(loan){
            loan.dueDate = newDueDate;
        }
        
        log("Loan " + loanID + " has been delayed from " + currentDueDate + " to " + newDueDate);
        logManager("Loan " + loanID + " has been delayed from " + currentDueDate + " to " + newDueDate);
        
        return "Successfully delay payment for loan : " + loanID;
    }

    @Override
    public String printCustomerInfo(String bank) {
        StringBuilder result = new StringBuilder();
        result.append("CustomerAccount Info:"+"\n");
        result.append("ID FIrstName LastName Email Phone Credit"+"\n");
        for(String ch : alphabet){
            ArrayList<Account> list = account_HashMap.get(ch);
            for(Account account : list){
                result.append(account.accountNumber + " "
                                + account.firstName + " " + account.lastName + " "
                                + account.emailAddress + " " + account.phoneNumber
                                + " " + account.creditLimit + "\n");
            }
            
        }
        
        result.append("Loan Info:"+"\n");
        result.append("ID CustomerID DueDate LoanAmount"+"\n");
        for(Loan loan : loan_HashMap.values()){
            result.append(loan.ID + " " + loan.accountNumber
                            + " " + loan.dueDate + " days left" + " " + loan.amount + "\n");
        }
        
        log("printCustomerInfo has been called");
        logManager("printCustomerInfo has been called");
        
        return result.toString();
    }

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
                    System.out.println("BankAsClient");
                    System.out.println("**********************");
                    System.out.println(e.toString());
                }
        }
    
    };
    
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
                            sendData = "NotExceed".getBytes();
                        }else{
                            synchronized(foundAccount){
                                // if its limit is not below 0
                                if(foundAccount.creditLimit >= 0){
                                    sendData = "NotExceed,".getBytes();
                                }else{
                                    sendData = "Exceed,".getBytes();
                                }
                            }
                        }
                        
                    }else if(request_array[0].equals("transfer")){
                        String[] content_array = request_array[1].split("#");
                        String[] loan_info = content_array[0].split(",");
                        Loan loan = new Loan();
                        loan.ID = loan_info[0];
                        loan.accountNumber = loan_info[1];
                        loan.dueDate = Integer.parseInt(loan_info[2]);
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
                System.out.println("BankAsReceiver");
                System.out.println("**********************");
                System.out.println(e.toString());
            }
        }
    }
    
    private void log(String content){
        
        File dir = new File(port+""); 
        dir.mkdir();
        
        String path = "./"+ port +"/log.txt";
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, true)))) {
            out.println(dateFormat.format(date) + "    " + content);
            
        }catch (IOException e) {
            
        }
    }
    
    private void logCustomer(String id, String content){
        File dir = new File(port+""); 
        dir.mkdir();
        
        String path = "./" + port + "/Customer"+ id +".txt";
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, true)))) {
            out.println(dateFormat.format(date) + "    " + content);
            
        }catch (IOException e) {
            
        }
    }
    
    private void logManager(String content){
        File dir = new File(port+""); 
        dir.mkdir();
        
        String path = "./"+port+"/Manager.txt";
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, true)))) {
            out.println(dateFormat.format(date) + "    " + content);
            
        }catch (IOException e) {
            
        }
    }
}
