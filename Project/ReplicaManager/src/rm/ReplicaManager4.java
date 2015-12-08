package rm;

import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Account;
import model.Loan;
import servant.BankServant4;

public class ReplicaManager4 {

    public HashMap<String, Integer> port_map;

    public HashMap<String, BankServant4> BankServantMap;

    private int[] other_rm;

    // RMs status
    public String rm1Status;
    public String rm2Status;
    public String rm3Status;
    public String rm4Status;

    private int RM_port, FE_port;

    // wrong operations recording
    private int Wrong_Count = 0;

    public ReplicaManager4(int bankA_port, int bankB_port, int bankC_port, int rm_port, int[] other_rm_port, int fe_port) {

        //set each bank port
        port_map = new HashMap<String, Integer>();
        port_map.put("A", bankA_port);
        port_map.put("B", bankB_port);
        port_map.put("C", bankC_port);
        //set other rm array
        other_rm = other_rm_port;

        RM_port = rm_port;
        FE_port = fe_port;

        //3 bank servants
        BankServantMap = new HashMap<String, BankServant4>();
        //BankServant para: Bank port, RM_port, FE_port
        BankServantMap.put("A", new BankServant4("A", port_map, RM_port, FE_port));
        BankServantMap.put("B", new BankServant4("B", port_map, RM_port, FE_port));
        BankServantMap.put("C", new BankServant4("C", port_map, RM_port, FE_port));

        //receiver thread
        Thread receiver = new Thread(new RMReceiver(RM_port));
        receiver.start();

    }
    
    public void renewBankServant(){
        try {
            //close listening port
            RMSender bankA = new RMSender(port_map.get("A"), "shutdown");
            RMSender bankB = new RMSender(port_map.get("B"), "shutdown");
            RMSender bankC = new RMSender(port_map.get("C"), "shutdown");
            
            bankA.start();
            bankB.start();
            bankC.start();
            
            bankA.join();
            bankB.join();
            bankC.join();
            
            sleep(10000);
            
            BankServantMap.remove("A");
            BankServantMap.remove("B");
            BankServantMap.remove("C");
            
            BankServantMap.put("A", new BankServant4("A", port_map, RM_port, FE_port));
            BankServantMap.put("B", new BankServant4("B", port_map, RM_port, FE_port));
            BankServantMap.put("C", new BankServant4("C", port_map, RM_port, FE_port));
        } catch (InterruptedException ex) {
            
        }
    }

    //usage: send request msg to other rm
    private class RMSender extends Thread {

        private int otherBankPort;
        private String content;
        public String result;

        public RMSender(int otherBankPort, String content) {
            this.otherBankPort = otherBankPort;
            this.content = content;
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

                if(!content.equals("shutdown")){
                    
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    clientSocket.receive(receivePacket);
                    String reply = new String(receivePacket.getData());
                    
                    result = reply.trim();
                }
                
                clientSocket.close();

            } catch (SocketException ex) {
                System.out.println(ex.toString());
            } catch (UnknownHostException ex) {
                System.out.println(ex.toString());
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }

        }

    }

    private class RMReceiver implements Runnable {

        int RMport;

        public RMReceiver(int RMport) {
            this.RMport = RMport;
        }

        @Override
        public void run() {
            try {
                DatagramSocket serverSocket = new DatagramSocket(RMport);

                while (true) {
                    byte[] receiveData = new byte[1024];
                    byte[] sendData = new byte[1024];

                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);
                    String sentence = new String(receivePacket.getData());
                    InetAddress IPAddress = receivePacket.getAddress();
                    int port = receivePacket.getPort();

                    //***************************
                    //UDP message processing...
                    //***************************
                    if (sentence.contains("ASK")) {

                        String hash_data = hashToString2(ReplicaManager4.this);
                        sendData = hash_data.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                        serverSocket.send(sendPacket);

                    } else {
                        String rmAndItStatus[] = sentence.split("#");
                        rm1Status = rmAndItStatus[1].split("%")[1];
                        rm2Status = rmAndItStatus[2].split("%")[1];
                        rm3Status = rmAndItStatus[3].split("%")[1];
                        rm4Status = rmAndItStatus[4].split("%")[1];

                        //check if corresponding bankserver correct, if not recovery
                        for (int i = 1; i <= 4; i++) {
                            if (rmAndItStatus[i].split("%")[0].equals(Integer.toString(RMport))) {
                                if (rmAndItStatus[i].split("%")[1].equals("Running")) {
                                    break;
                                }
                                
                                Wrong_Count++;
                                
                                if (rmAndItStatus[i].split("%")[1].equals("Timeout")) {
                                    System.out.println("RM4: Timeout is more than 3, we renew our bank servant");
                                    
                                    renewBankServant();
                                    Wrong_Count = 0;
                                }
                                
                                // if wrong count bigger than 3, we have to renew whole sever
                                if(Wrong_Count > 2){
                                    System.out.println("RM4: Wrong_Count is more than 3, we renew our bank servant");
                                    
                                    renewBankServant();
                                    Wrong_Count = 0;
                                }
                                
                                if (rm1Status.equals("Running")) {
                                    //ask for data
                                    RMSender sender = new RMSender(Integer.parseInt(rmAndItStatus[1].split("%")[0]), "ASK");
                                    sender.run();

                                    stringToHash2(sender.result, ReplicaManager4.this);

                                } else if (rm2Status.equals("Running")) {
                                    //ask for data
                                    RMSender sender = new RMSender(Integer.parseInt(rmAndItStatus[2].split("%")[0]), "ASK");
                                    sender.run();

                                    stringToHash2(sender.result, ReplicaManager4.this);
                                    
                                } else if (rm3Status.equals("Running")) {
                                    //ask for data
                                    RMSender sender = new RMSender(Integer.parseInt(rmAndItStatus[3].split("%")[0]), "ASK");
                                    sender.run();

                                    stringToHash2(sender.result, ReplicaManager4.this);
                                    
                                } else if (rm4Status.equals("Running")) {
                                    //ask for data
                                    RMSender sender = new RMSender(Integer.parseInt(rmAndItStatus[3].split("%")[0]), "ASK");
                                    sender.run();

                                    stringToHash2(sender.result, ReplicaManager4.this);
                                }
                            }
                        }
                    }

                }

            } catch (SocketException ex) {
                System.out.println(ex.toString());
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }
        }

    }

    public void stringToHash2(String s, ReplicaManager4 rm) {
        String banks[] = s.split("!");

        if (!banks[0].equals("@")) {
            String elements[] = banks[0].split("@");
            String customers[] = elements[0].split(";");
            String loans[] = {};
            if (elements.length > 1) {
                loans = elements[1].split(";");
            }
            
            rm.BankServantMap.get("A").refreshHashMap();

            for (int i = 0; i < customers.length; i++) {
                String token[] = customers[i].split(",");
                Account customer = new Account(token[1].trim(), token[2].trim(), token[3].trim(), token[4].trim(), token[5].trim(), 1000);
                String key = Character.toString(token[1].trim().charAt(0));
                ArrayList<Account> customerList = rm.BankServantMap.get("A").account_HashMap.get(key);
                customerList.add(customer);
                rm.BankServantMap.get("A").account_HashMap.put(key, customerList);
            }
            for (int i = 0; i < loans.length; i++) {
                String token[] = loans[i].split(",");
                Loan loan = new Loan(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim());
                String key = token[0].trim();
                rm.BankServantMap.get("A").loan_HashMap.put(key, loan);
            }
        }

        if (!banks[1].equals("@")) {
            String elements2[] = banks[1].split("@");
            String customers2[] = elements2[0].split(";");
            String loans2[] = {};
            if (elements2.length > 1){
                loans2 = elements2[1].split(";");
            }
            
            rm.BankServantMap.get("B").refreshHashMap();
            
            for (int i = 0; i < customers2.length; i++) {
                String token[] = customers2[i].split(",");
                Account customer = new Account(token[1].trim(), token[2].trim(), token[3].trim(), token[4].trim(), token[5].trim(), 1000);
                String key = Character.toString(token[1].trim().charAt(0));
                ArrayList<Account> customerList = rm.BankServantMap.get("B").account_HashMap.get(key);
                customerList.add(customer);
                rm.BankServantMap.get("B").account_HashMap.put(key, customerList);
            }
            for (int i = 0; i < loans2.length; i++) {
                String token[] = loans2[i].split(",");
                Loan loan = new Loan(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim());
                String key = token[0].trim();
                rm.BankServantMap.get("B").loan_HashMap.put(key, loan);
            }
        }
        
        if(!banks[2].equals("@")){
            String elements3[] = banks[0].split("@");
            String customers3[] = elements3[0].split(";");
            String loans3[] = {};
            if (customers3.length > 1){
                loans3 = elements3[1].split(";");
            }
            
            rm.BankServantMap.get("C").refreshHashMap();

            for (int i = 0; i < customers3.length; i++) {
                String token[] = customers3[i].split(",");
                Account customer = new Account(token[1].trim(), token[2].trim(), token[3].trim(), token[4].trim(), token[5].trim(), 1000);
                String key = Character.toString(token[1].trim().charAt(0));
                ArrayList<Account> customerList = rm.BankServantMap.get("C").account_HashMap.get(key);
                customerList.add(customer);
                rm.BankServantMap.get("C").account_HashMap.put(key, customerList);
            }
            for (int i = 0; i < loans3.length; i++) {
                String token[] = loans3[i].split(",");
                Loan loan = new Loan(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim());
                String key = token[0].trim();
                rm.BankServantMap.get("C").loan_HashMap.put(key, loan);
            }
        }
        
    }

    public String hashToString2(ReplicaManager4 rm) {
        String result = "";

        for (ArrayList<Account> account_list : rm.BankServantMap.get("A").account_HashMap.values()) {
            for (Account account : account_list) {
                result += account.accountNumber + "," + account.firstName + "," + account.lastName + "," + account.emailAddress + "," + account.phoneNumber
                        + "," + account.password + ";";
            }
        }
        result += "@";
        for (Loan temp : rm.BankServantMap.get("A").loan_HashMap.values()) {
            result += temp.ID + "," + temp.accountNumber + "," + temp.amount + "," + temp.dueDate + ";";
        }

        result += "!";

        for (ArrayList<Account> account_list : rm.BankServantMap.get("B").account_HashMap.values()) {
            for (Account account : account_list) {
                result += account.accountNumber + "," + account.firstName + "," + account.lastName + "," + account.emailAddress + "," + account.phoneNumber
                        + "," + account.password + ";";
            }
        }
        result += "@";
        for (Loan temp : rm.BankServantMap.get("B").loan_HashMap.values()) {
            result += temp.ID + "," + temp.accountNumber + "," + temp.amount + "," + temp.dueDate + ";";
        }

        result += "!";

        for (ArrayList<Account> account_list : rm.BankServantMap.get("C").account_HashMap.values()) {
            for (Account account : account_list) {
                result += account.accountNumber + "," + account.firstName + "," + account.lastName + "," + account.emailAddress + "," + account.phoneNumber
                        + "," + account.password + ";";
            }
        }
        result += "@";
        for (Loan temp : rm.BankServantMap.get("C").loan_HashMap.values()) {
            result += temp.ID + "," + temp.accountNumber + "," + temp.amount + "," + temp.dueDate + ";";
        }

        //System.out.println(result); 
        return result;
    }
}
