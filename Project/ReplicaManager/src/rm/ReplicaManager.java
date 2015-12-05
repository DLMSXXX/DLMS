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
    
    public int Bank_A_Port;
    public int Bank_B_Port;
    public int Bank_C_Port;
    
    HashMap<String, BankServant> BankServantMap;
    
    private int[] other_rm;
    
    // wrong operations recording
    private int Wrong_Count = 0;

    public ReplicaManager(int bankA_port, int bankB_port, int bankC_port, int RM_port, int[] other_rm_port){
        
        //set each bank port
        Bank_A_Port = bankA_port;
        Bank_B_Port = bankB_port;
        Bank_C_Port = bankC_port;
        //set other rm array
        other_rm = other_rm_port;
        
        //3 bank servants
        BankServantMap = new HashMap<String, BankServant>();
        //BankServant para: Bank port,AccountID_UniqueBase,LoanID_UniqueBase
        BankServantMap.put("A", new BankServant(Bank_A_Port, new int[]{Bank_B_Port, Bank_C_Port}));
        BankServantMap.put("B", new BankServant(Bank_B_Port, new int[]{Bank_A_Port, Bank_C_Port}));
        BankServantMap.put("C", new BankServant(Bank_C_Port, new int[]{Bank_A_Port, Bank_B_Port}));
        
        //receiver thread
        RMReceiver rmReceiver = new RMReceiver(RM_port);
        rmReceiver.run();
        
    }
    
    //recovery method
    private void recoverPrepare(){
        //delete bank servant instance
        BankServantMap.clear();
        
        //obtain correct bank servant port
        RMSender rmSender0 = new RMSender(other_rm[0], "CheckRM#");
        RMSender rmSender1 = new RMSender(other_rm[1], "CheckRM#");
        RMSender rmSender2 = new RMSender(other_rm[2], "CheckRM#");
        
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
        if(rmSender0.result.startsWith("Running")){
            String[] ports_string = rmSender0.result.split("#")[1].split(",");
            int[] ports_integer = {Integer.parseInt(ports_string[0]), Integer.parseInt(ports_string[1]), Integer.parseInt(ports_string[2])};
            recoverStart(ports_integer[0],ports_integer[1],ports_integer[2]);
        }else if(rmSender1.result.startsWith("Running")){
            String[] ports_string = rmSender1.result.split("#")[1].split(",");
            int[] ports_integer = {Integer.parseInt(ports_string[0]), Integer.parseInt(ports_string[1]), Integer.parseInt(ports_string[2])};
            recoverStart(ports_integer[0],ports_integer[1],ports_integer[2]);
        }else if(rmSender2.result.startsWith("Running")){
            String[] ports_string = rmSender2.result.split("#")[1].split(",");
            int[] ports_integer = {Integer.parseInt(ports_string[0]), Integer.parseInt(ports_string[1]), Integer.parseInt(ports_string[2])};
            recoverStart(ports_integer[0],ports_integer[1],ports_integer[2]);
        }else{
            System.out.println("Error: There is no other working Replica Manager!!!");
        }
        
    }
    
    private void recoverStart(int target_port_A, int target_port_B, int target_port_C){
        //recovery
        //BankServant para: corresponding right bank servant
        BankServantMap.put("A", new BankServant(Bank_A_Port, new int[]{Bank_B_Port, Bank_C_Port}, target_port_A));
        BankServantMap.put("B", new BankServant(Bank_B_Port, new int[]{Bank_A_Port, Bank_C_Port}, target_port_B));
        BankServantMap.put("C", new BankServant(Bank_C_Port, new int[]{Bank_A_Port, Bank_B_Port}, target_port_C));
    
        // reset wrong operations recording
        Wrong_Count = 0;
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
