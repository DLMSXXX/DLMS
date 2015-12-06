package rm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import servant.dlmsImpl;

public class ReplicaManager2 {
  
    public HashMap<String, Integer> port_map;
    
    HashMap<String, dlmsImpl> BankServantMap;

    private int[] other_rm;
    
    // RMs status
    public String rm1Status;
    public String rm2Status;
    public String rm3Status;
    public String rm4Status;
    
    private int RM_port, FE_port;
    
    // wrong operations recording
    private int Wrong_Count = 0;
    
    public ReplicaManager2(int bankA_port, int bankB_port, int bankC_port, int rm_port, int[] other_rm_port, int fe_port){
        //set each bank port
        port_map = new HashMap<String, Integer>();
        port_map.put("A", bankA_port);
        port_map.put("B", bankB_port);
        port_map.put("C", bankC_port);
        //set other rm array
        other_rm = other_rm_port;
        
        RM_port = rm_port;
        FE_port = fe_port;
    
        BankServantMap = new HashMap<String, dlmsImpl>();
        BankServantMap.put("A", new dlmsImpl("A", port_map, RM_port, FE_port));
        BankServantMap.put("B", new dlmsImpl("B", port_map, RM_port, FE_port));
        BankServantMap.put("C", new dlmsImpl("C", port_map, RM_port, FE_port));
        
        //receiver thread
        Thread receiver = new Thread(new RMReceiver(RM_port));
        receiver.start();
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

                sendData = content.getBytes();
                
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, otherBankPort);
                clientSocket.send(sendPacket);
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
                    String rmAndItStatus[] = sentence.split("#");
                    rm1Status = rmAndItStatus[1].split("%")[1];
                    rm2Status = rmAndItStatus[2].split("%")[1];
                    rm3Status = rmAndItStatus[3].split("%")[1];
                    rm4Status = rmAndItStatus[4].split("%")[1];
                    System.out.println(RMport);
                    //check if corresponding bankserver correct, if not recovery
                    for(int i = 1; i <= 4; i++){
                        if(rmAndItStatus[i].split("%")[0].equals(Integer.toString(RMport))){
                            if(rmAndItStatus[i].split("%")[1].equals("Running")) break;
                            Wrong_Count++;
                            if(rm1Status.equals("Running")){
                                //ask for data
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
        
    }
}
