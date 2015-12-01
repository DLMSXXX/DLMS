package rm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import servant.BankServant;

public class ReplicaManager {
    
    public static final int BANK_A_PORT = 1;
    public static final int BANK_B_PORT = 2;
    public static final int BANK_C_PORT = 3;
    public static final int BANK_A_ACCOUNT_UNIQUE_BASE = 1;
    public static final int BANK_B_ACCOUNT_UNIQUE_BASE = 1;
    public static final int BANK_C_ACCOUNT_UNIQUE_BASE = 1;
    public static final int BANK_A_LOAN_UNIQUE_BASE = 1;
    public static final int BANK_B_LOAN_UNIQUE_BASE = 1;
    public static final int BANK_C_LOAN_UNIQUE_BASE = 1;
    
    HashMap<String, BankServant> BankServantMap;
    
    private int[] other_rm;
    
    // wrong operations recording

    public ReplicaManager(){
        
        //3 bank servants
        BankServantMap = new HashMap<String, BankServant>();
        //BankServant para: Bank port,AccountID_UniqueBase,LoanID_UniqueBase
        BankServantMap.put("A", new BankServant(BANK_A_PORT, BANK_A_ACCOUNT_UNIQUE_BASE, BANK_A_LOAN_UNIQUE_BASE));
        BankServantMap.put("B", new BankServant(BANK_B_PORT, BANK_B_ACCOUNT_UNIQUE_BASE, BANK_B_LOAN_UNIQUE_BASE));
        BankServantMap.put("C", new BankServant(BANK_C_PORT, BANK_C_ACCOUNT_UNIQUE_BASE, BANK_C_LOAN_UNIQUE_BASE));
        
        //receiver thread
        RMReceiver rmReceiver = new RMReceiver(9999);
        rmReceiver.run();
        
        //set other rm array
    }
    
    //recovery method
    private void recoverPrepare(){
        //delete bank servant instance
        BankServantMap.clear();
        
        //obtain correct bank servant port
        //!!!!!!!need to decide what kind of message to be used here
        RMSender rmSender0 = new RMSender(other_rm[0], "");
        RMSender rmSender1 = new RMSender(other_rm[1], "");
        RMSender rmSender2 = new RMSender(other_rm[2], "");
        
        rmSender0.start();
        rmSender1.start();
        rmSender2.start();
        
        try {
            //!!!!need to do some timeout situation
            rmSender0.join();
            rmSender1.join();
            rmSender2.join();
        } catch (InterruptedException ex) {
            System.out.println(ex.toString());
        }
        
        //choose the target port
        //!!!!need to define message format
        if(rmSender0.result.equals("")){
            recoverStart(1,2,3);
        }else if(rmSender1.result.equals("")){
            recoverStart(1,2,3);
        }else if(rmSender2.result.equals("")){
            recoverStart(1,2,3);
        }else{
            System.out.println("Error: There is no other working Replica Manager!!!");
        }
        
    }
    
    private void recoverStart(int target_port_A, int target_port_B, int target_port_C){
        //recovery
        //BankServant para: corresponding right bank servant
        BankServantMap.put("A", new BankServant(BANK_A_PORT, BANK_A_ACCOUNT_UNIQUE_BASE, BANK_A_LOAN_UNIQUE_BASE, target_port_A));
        BankServantMap.put("B", new BankServant(BANK_B_PORT, BANK_B_ACCOUNT_UNIQUE_BASE, BANK_B_LOAN_UNIQUE_BASE, target_port_B));
        BankServantMap.put("C", new BankServant(BANK_C_PORT, BANK_C_ACCOUNT_UNIQUE_BASE, BANK_C_LOAN_UNIQUE_BASE, target_port_C));
    
    }
    
    //usage: send request msg to other rm
    private class RMSender extends Thread{
        
        private int otherBankPort;
        private String content;
        public String result;
        
        public RMSender(int otherBankPort, String content){
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
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                String reply = new String(receivePacket.getData());
                clientSocket.close();

                //*************split symbol!!!!!! need discuss to use which one
                String[] reply_array = reply.split(":");
                result = reply_array[0];
                
            } catch (SocketException ex) {
                System.out.println(ex.toString());
            } catch (UnknownHostException ex) {
                System.out.println(ex.toString());
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }
            
        }
        
    }
    
    private class RMReceiver implements Runnable{
        
        int RMport;
        
        public RMReceiver(int RMport){
            this.RMport = RMport;
        }

        @Override
        public void run() {
            
            try {
                DatagramSocket serverSocket = new DatagramSocket(RMport);
                
                byte[] receiveData = new byte[1024];
                byte[] sendData = new byte[1024];
                
                while(true){
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);
                    String sentence = new String(receivePacket.getData());
                    InetAddress IPAddress = receivePacket.getAddress();
                    int port = receivePacket.getPort();
                    
                    //***************************
                    //UDP message processing...
                    //***************************
                }
            
            } catch (SocketException ex) {
                System.out.println(ex.toString());
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }
        }
        
    }
}
