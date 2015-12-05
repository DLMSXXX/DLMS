package frontend;

import DLMS.dlmsPOA;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;

public class DlmsFrontEnd extends dlmsPOA {

    private int SequencerPortNumber;
    private int[] RMPort;
    private int FEport;
    
    public FrontEndReceiver FEReceiver;
    public HashMap<String, String> ResultMap = new HashMap<String, String>();
    
    public DlmsFrontEnd(int sequencer_port, int[] rm_port, int fe_port){
        this.SequencerPortNumber = sequencer_port;
        this.RMPort = rm_port;
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
                    
                    //assume sequence request id is 1!!!
                    System.out.println("FrontEndReceiver get information: " + sentence);
                    ResultMap.put("1", sentence);
                    
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
                result = reply;
                
            } catch (SocketException ex) {
                System.out.println(ex.toString());
            } catch (UnknownHostException ex) {
                System.out.println(ex.toString());
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }
        }
    }

    /*static class BankReceiver extends Thread {

        public String result = null;
        public boolean down = false;

        public void run() {
            DatagramSocket aSocket = null;
            try {
                byte[] buffer = new byte[100];
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                aSocket.setSoTimeout(1000);
                try {
                    aSocket.receive(reply);
                } catch (SocketTimeoutException e) {
                    // timeout exception.
                    System.out.println("Timeout reached!!! " + e);
                    down = true;
                    aSocket.close();
                }
                result = new String(reply.getData());
                result = result.trim();
            } catch (IOException e) {
                e.printStackTrace();
            }
            aSocket.close();
        }
    }*/

    /*static class RMSender extends Thread {

        private String sendMsg;
        private int RMPortNumber;

        public RMSender(String s, int port) {
            sendMsg = s;
            RMPortNumber = port;
        }

        public void run() {
            DatagramSocket aSocket = null;
            try {
                aSocket = new DatagramSocket();
                InetAddress aHost = InetAddress.getByName("localhost");
                byte[] m = sendMsg.getBytes();
                DatagramPacket request = new DatagramPacket(m, m.length, aHost, RMPortNumber);
                aSocket.send(request);
            } catch (SocketException e) {
                System.out.println("Socket: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("IO: " + e.getMessage());
            }
            aSocket.close();
        }
    }*/

    @Override
    public String openAccount(String Bank, String fName, String lName, String email, String phoneNumber,
            String password) {
        /*try {
            
            // send request to sequencer
            FrontEndSender sender = new FrontEndSender(SequencerPortNumber, "openAccount," + Bank + "," + fName + "," + lName + "," + email + ","
                        + phoneNumber + "," + password);
            sender.start();
            sender.join();
            String request_id = sender.result;
            
            // !!!!!!need change
            long startTime = System.currentTimeMillis();
            
            while(ResultMap.get(request_id) == null && (System.currentTimeMillis() -  startTime) < 60000){
                // keep waiting for response from server
            }
            
            return ResultMap.get(request_id) == null ? "out of time" : ResultMap.get(request_id);
            
        } catch (InterruptedException ex) {
            System.out.println(ex.toString());
        }*/
        
        // !!!!!!need change
        return "Problem";
    }

    @Override
    public String getLoan(String Bank, String accountNumber, String password, String loanAmount) {
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
        return "";
    }

    @Override
    public String delayPayment(String Bank, String loanID, String currentD, String newD) {
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
        return "";
    }

    @Override
    public String printCustomerInfo(String Bank) {
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
        return "";
    }

    @Override
    public String transferLoan(String loanID, String currentBank, String otherBank) {
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
        return "";
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
