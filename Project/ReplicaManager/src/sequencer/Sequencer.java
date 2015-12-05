package sequencer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;

public class Sequencer {

    private int RM1_PORT;
    private int RM2_PORT;
    private int RM3_PORT;
    private int RM4_PORT;
    
    private int Sequencer_port;

    Integer seqCount = new Integer(0);
    Queue<String> seqQueue = new LinkedList<String>();

    public Sequencer(int rm1, int rm2, int rm3, int rm4, int sequencer_port) {
        this.RM1_PORT = rm1;
        this.RM2_PORT = rm2;
        this.RM3_PORT = rm3;
        this.RM4_PORT = rm4;
        
        this.Sequencer_port = sequencer_port;
    }

    /*public static void main(String[] args) {
     SeqReceiver seqReceiver = new SeqReceiver(5000);
     seqReceiver.run();
     }*/
    class SeqReceiver implements Runnable {

        @SuppressWarnings("resource")
        @Override
        public void run() {
            try {
                DatagramSocket receiveSocket = new DatagramSocket(Sequencer_port);

                byte[] receiveData = new byte[1024];

                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    receiveSocket.receive(receivePacket);
                    String request = new String(receivePacket.getData());
                    InetAddress inetAddress = receivePacket.getAddress();
                    int port = receivePacket.getPort();
                    seqQueue.add(Integer.toString(++seqCount) + "|" + inetAddress + "|" + port + "|" + request);
                    synchronized (seqQueue) {
                        String sendMessage = seqQueue.peek();
                        SeqSender sender1 = new SeqSender(RM1_PORT, sendMessage);
                        SeqSender sender2 = new SeqSender(RM2_PORT, sendMessage);
                        SeqSender sender3 = new SeqSender(RM3_PORT, sendMessage);
                        SeqSender sender4 = new SeqSender(RM4_PORT, sendMessage);
                        sender1.start();
                        sender2.start();
                        sender3.start();
                        sender4.start();
                        try {
                            sender1.join();
                            sender2.join();
                            sender3.join();
                            sender4.join();
                        } catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }
                        seqQueue.poll();
                    }
                }
            } catch (SocketException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    class SeqSender extends Thread {

        private int rmPort;
        private String requestMessage;

        public SeqSender(int rmPort, String requestMessage) {
            super();
            this.rmPort = rmPort;
            this.requestMessage = requestMessage;
        }

        @Override
        public void run() {
            try {
                @SuppressWarnings("resource")
                DatagramSocket sendSocket = new DatagramSocket();
                InetAddress inetAddress = InetAddress.getByName("localhost");

                byte[] sendData = new byte[1024];
                sendData = requestMessage.getBytes();

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, inetAddress, rmPort);
                sendSocket.send(sendPacket);
            } catch (SocketException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

    }

}
