package frontend;

import DLMS.dlmsPOA;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DlmsFrontEnd extends dlmsPOA {

    private int SequencerPortNumber;
    private HashMap<String, Integer> RMPort_map;
    private int FEport;
    
    public FrontEndReceiver FEReceiver;
    public Hashtable<String, Hashtable<String, String>> ResultMap = new Hashtable<String, Hashtable<String, String>>();
    
    public DlmsFrontEnd(int sequencer_port, HashMap<String, Integer> RMPort_map, int fe_port){
        this.SequencerPortNumber = sequencer_port;
        this.RMPort_map = RMPort_map;
        this.FEport = fe_port;
        
        FEReceiver = new FrontEndReceiver(FEport);
        FEReceiver.start();
    }
    
    class FrontEndReceiver extends Thread {
        int FEport;
        
        public FrontEndReceiver(int FEport){
            this.FEport = FEport;
        }
        
        @Override
        public void run(){
            try {
                DatagramSocket serverSocket = new DatagramSocket(FEport);
                
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
                    
                    String[] responseArr = sentence.split("%");
                    String sequenceId = responseArr[0];
                    String[] rmArr = responseArr[1].split("#");
                    String rm_port = rmArr[0];
                    String result = rmArr[1];
                    
                    if(ResultMap.get(sequenceId) == null){
                        Hashtable<String, String> rm_result_map = new Hashtable<String, String>();
                        rm_result_map.put(rm_port, result);
                        ResultMap.put(sequenceId, rm_result_map);
                    }else{
                        ResultMap.get(sequenceId).put(rm_port, result);
                    }
                    
                    System.out.println("FrontEndReceiver get information: " + sentence);
                    
                }
            
            } catch (SocketException ex) {
                System.out.println(ex.toString());
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }
        }
    }
    
    class FrontEndSender extends Thread{
        private int target_port;
        private String content;
        private String result;
        
        public FrontEndSender(int target_port, String content){
            this.target_port = target_port;
            this.content = content;
        }
        
        @Override
        public void run(){
            try {
                DatagramSocket clientSocket = new DatagramSocket();
                InetAddress IPAddress = InetAddress.getByName("localhost");
                byte[] sendData = new byte[1024];
                byte[] receiveData = new byte[1024];

                sendData = content.getBytes();
                
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, target_port);
                clientSocket.send(sendPacket);
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                String reply = new String(receivePacket.getData());
                clientSocket.close();
                
                 //!!!!!!receive sequence id from sequencer
                result = reply.trim();
                
            } catch (SocketException ex) {
                System.out.println(ex.toString());
            } catch (UnknownHostException ex) {
                System.out.println(ex.toString());
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }
        }
    }

    @Override
    public String openAccount(String Bank, String fName, String lName, String email, String phoneNumber,
            String password) {

        // send request to sequencer
        FrontEndSender sender = new FrontEndSender(SequencerPortNumber, "openAccount#" + Bank + "," + fName + "," + lName + "," + email + ","
                + phoneNumber + "," + password + "#");
        sender.run();

        String request_id = sender.result;

        long startTime = System.currentTimeMillis();
        
        //There is no result from bank server
        while(ResultMap.get(request_id) == null && (System.currentTimeMillis() - startTime) < 60000){
            try {
                sleep(5000);
                
            } catch (InterruptedException ex) {
                
            }
        }
        
        while(ResultMap.get(request_id).size() < 4 && (System.currentTimeMillis() - startTime) < 60000){
        }
        
        // Time out problem
        if((System.currentTimeMillis() - startTime) > 60000){
        
        }else{
            // Everything is ok, now we check all result
            Hashtable<String, String> re_map = ResultMap.get(request_id);
            if(re_map.get(RMPort_map.get("RM1")).equals(re_map.get(RMPort_map.get("RM2"))) 
                    && re_map.get(RMPort_map.get("RM2")).equals(re_map.get(RMPort_map.get("RM3")))
                    && re_map.get(RMPort_map.get("RM3")).equals(re_map.get(RMPort_map.get("RM4")))){
                // all equals
                return re_map.get(RMPort_map.get("RM1"));
                
            }else if(!re_map.get(RMPort_map.get("RM1")).equals(re_map.get(RMPort_map.get("RM2"))) 
                    && re_map.get(RMPort_map.get("RM2")).equals(re_map.get(RMPort_map.get("RM3")))
                    && re_map.get(RMPort_map.get("RM3")).equals(re_map.get(RMPort_map.get("RM4")))){
                // RM1 have problem
                
            }else if(re_map.get(RMPort_map.get("RM1")).equals(re_map.get(RMPort_map.get("RM3"))) 
                    && !re_map.get(RMPort_map.get("RM2")).equals(re_map.get(RMPort_map.get("RM3")))
                    && re_map.get(RMPort_map.get("RM3")).equals(re_map.get(RMPort_map.get("RM4")))){
                // RM2 have problem
                
            }else if(re_map.get(RMPort_map.get("RM1")).equals(re_map.get(RMPort_map.get("RM2"))) 
                    && re_map.get(RMPort_map.get("RM2")).equals(re_map.get(RMPort_map.get("RM4")))
                    && !re_map.get(RMPort_map.get("RM3")).equals(re_map.get(RMPort_map.get("RM4")))){
                // RM3 have problem
                
            }else if(re_map.get(RMPort_map.get("RM1")).equals(re_map.get(RMPort_map.get("RM2"))) 
                    && re_map.get(RMPort_map.get("RM2")).equals(re_map.get(RMPort_map.get("RM3")))
                    && !re_map.get(RMPort_map.get("RM3")).equals(re_map.get(RMPort_map.get("RM4")))){
                // RM4 have problem
                
            }
        }
        
        return "Got problem on Front End";
    }

    @Override
    public String getLoan(String Bank, String accountNumber, String password, String loanAmount) {
        FrontEndSender sender = new FrontEndSender(SequencerPortNumber, "getLoan#" + Bank + "," + accountNumber + "," + password + "," + loanAmount + "#");
        sender.run();

        String request_id = sender.result;
        // TODO Auto-generated method stub
        /*String result = null;
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket();
            InetAddress aHost = InetAddress.getByName("localhost");
            String toServer = "getLoan," + Bank + "," + accountNumber + "," + password + "," + loanAmount;
            byte[] m = toServer.getBytes();
            DatagramPacket request = new DatagramPacket(m, m.length, aHost, SequencerPortNumber);
            aSocket.send(request);	// send to Sequencer
            aSocket.close();
            result = RCS();

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
        return result;*/
        return request_id.trim();
    }

    @Override
    public String delayPayment(String Bank, String loanID, String currentD, String newD) {
        FrontEndSender sender = new FrontEndSender(SequencerPortNumber, "delayPayment#" + Bank + "," + loanID + "," + currentD + "," + newD + "#");
        sender.run();

        String request_id = sender.result;
        
        /*
        String result = null;
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket();
            InetAddress aHost = InetAddress.getByName("localhost");
            String toServer = "delayPayment," + Bank + "," + loanID + "," + currentD + "," + newD;
            byte[] m = toServer.getBytes();
            DatagramPacket request = new DatagramPacket(m, m.length, aHost, SequencerPortNumber);
            aSocket.send(request);	// send to Sequencer
            aSocket.close();
            result = RCS();

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
        return result;*/
        return request_id.trim();
    }

    @Override
    public String printCustomerInfo(String Bank) {
        
        FrontEndSender sender = new FrontEndSender(SequencerPortNumber, "printCustomerInfo#" + Bank + "#");
        sender.run();

        String request_id = sender.result;
        
        /*
        String result = null;
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket();
            InetAddress aHost = InetAddress.getByName("localhost");
            String toServer = "printCustomerInfo," + Bank;
            byte[] m = toServer.getBytes();
            DatagramPacket request = new DatagramPacket(m, m.length, aHost, SequencerPortNumber);
            aSocket.send(request);	// send to Sequencer
            aSocket.close();
            result = RCS();

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
        return result;*/
        return request_id.trim();
    }

    @Override
    public String transferLoan(String loanID, String currentBank, String otherBank) {
        
        FrontEndSender sender = new FrontEndSender(SequencerPortNumber, "transferLoan#" + loanID + "," +  currentBank + "," + otherBank + "#");
        sender.run();

        String request_id = sender.result;
        /*
        String result = null;
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket();
            InetAddress aHost = InetAddress.getByName("localhost");
            String toServer = "transferLoan," + loanID + "," + currentBank + "," + otherBank;
            byte[] m = toServer.getBytes();
            DatagramPacket request = new DatagramPacket(m, m.length, aHost, SequencerPortNumber);
            aSocket.send(request);	// send to Sequencer
            aSocket.close();
            result = RCS();

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
        return result;*/
        return request_id.trim();
    }

    //method to receive from banks, compare and send to RMs
    /*private String RCS() {
        String result = null;
        // receive from banks
        BankReceiver[] br = new BankReceiver[RMNumber];
        for (int i = 0; i < RMNumber; i++) {
            br[i] = new BankReceiver();
            br[i].start();
        }
        for (int i = 0; i < RMNumber; i++) {
            try {
                br[i].join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // compare
        int downRM = 0;
        for (int i = 0; i < RMNumber; i++) {
            if (br[i].down == true) {
                downRM = i + 1;
                break;
            }
        }
        String toRM = null;
        outerloop:
        for (int i = 0; i < RMNumber; i++) {
            for (int j = 0; j < RMNumber; j++) {
                if (br[i].result.equals(br[j].result)) {
                    toRM = Integer.toString(downRM) + "," + br[i].result;
                    result = br[i].result;
                    break outerloop;
                }
            }
        }

        // send to RMs
        RMSender[] rs = new RMSender[RMNumber];
        for (int i = 0; i < RMNumber; i++) {
            rs[i] = new RMSender(toRM, (RMPort + i));
            rs[i].start();
        }
        for (int i = 0; i < RMNumber; i++) {
            try {
                rs[i].join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return result;
    }*/

}
