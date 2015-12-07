package rm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import model.Account;
import model.Loan;
import servant.BankServant;
import servant.BankServant3;

/**
 *
 * @author yucunli
 */
public class ReplicaManager3 {
    public HashMap<String, Integer> port_map;
    
    public HashMap<String, BankServant3> BankServantMap;
    
    private int[] other_rm;
    
    // RMs status
    public String rm1Status;
    public String rm2Status;
    public String rm3Status;
    public String rm4Status;
    
    private int RM_port, FE_port;
    
    // wrong operations recording
    private int Wrong_Count = 0;

    public ReplicaManager3(int bankA_port, int bankB_port, int bankC_port, int rm_port, int[] other_rm_port, int fe_port){
        
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
        BankServantMap = new HashMap<String, BankServant3>();
        //BankServant para: Bank port, RM_port, FE_port
        BankServantMap.put("A", new BankServant3("A", port_map, RM_port, FE_port));
        BankServantMap.put("B", new BankServant3("B", port_map, RM_port, FE_port));
        BankServantMap.put("C", new BankServant3("C", port_map, RM_port, FE_port));
        
        //receiver thread
        Thread receiver = new Thread(new RMReceiver(RM_port));
        receiver.start();
        
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
        BankServantMap.put("A", new BankServant3("A", port_map, target_port_A, RM_port, FE_port));
        BankServantMap.put("B", new BankServant3("B", port_map, target_port_B, RM_port, FE_port));
        BankServantMap.put("C", new BankServant3("C", port_map, target_port_C, RM_port, FE_port));
    
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
                
                while(true){
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
                    String rmAndItStatus[] = sentence.split("#");
                    rm1Status = rmAndItStatus[1].split("%")[1];
                    rm2Status = rmAndItStatus[2].split("%")[1];
                    rm3Status = rmAndItStatus[3].split("%")[1];
                    rm4Status = rmAndItStatus[4].split("%")[1];
                    System.out.println(RMport);
                    //check if corresponding bankserver correct, if not recovery
                    for(int i = 1; i <= 4; i++){
                        if(rmAndItStatus[i].split("%")[0].equals(Integer.toString(RMport))){
                            if(rmAndItStatus[i].split("%")[1].equals("Running"))break;
                            Wrong_Count++;
                            if(rm1Status.equals("Running")){
                                //ask for data
                                RMSender sender = new RMSender(Integer.parseInt(rmAndItStatus[1].split("%")[0]), "");
                                sender.run();
                                
                                String data = sender.result;
                                
                                
                            }else if(rm2Status.equals("Running")){
                                //ask for data
                                
                            }else if(rm3Status.equals("Running")){
                                //ask for data
                                
                            }else if(rm4Status.equals("Running")){
                                //ask for data
                                
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
        
        
        
        public void stringToHash2(String s, BankServant3 bank)
    {
        String elements[] = s.split("@");
        String customers[] = elements[0].split("|");
        String loans[] = elements[1].split("|");
            
        for (int i = 0; i<customers.length; i++)
        {
            String token[] = customers[i].split(",");
            Account customer = new Account(token[1].trim(), token[2].trim(), token[3].trim(), token[4].trim(), token[5].trim(),1000);    
            String key = Character.toString(token[1].trim().charAt(0));
            ArrayList<Account> customerList = bank.account_HashMap.get(key);
            customerList.add(customer);
            bank.account_HashMap.put(key, customerList);
        }
        
        for (int i = 0; i<loans.length; i++)
        {
            String token[] = loans[i].split(",");
            Loan loan = new Loan(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim());
            String key = token[0].trim();
            bank.loan_HashMap.put(key, loan);
        }
    }
    }
}
