package sequencer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;

public class Sequencer {
    
    private int[] bankA;
    private int[] bankB;
    private int[] bankC;
    
    private int Sequencer_port;

    Integer seqCount = new Integer(0);
    Queue<String> seqQueue = new LinkedList<String>();

    public Sequencer(int[] bankA, int[] bankB, int[] bankC, int sequencer_port) {
        this.bankA = bankA;
        this.bankB = bankB;
        this.bankC = bankC;
        
        this.Sequencer_port = sequencer_port;
    }

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
                    
                    seqQueue.add(Integer.toString(++seqCount) + "|" + request);
                    synchronized (seqQueue) {
                        String sendMessage = seqQueue.peek();
                        //!!!!!! need add checking process to determine which bank
                        SeqSender sender1 = new SeqSender(bankA[0], sendMessage);
                        SeqSender sender2 = new SeqSender(bankA[1], sendMessage);
                        SeqSender sender3 = new SeqSender(bankA[2], sendMessage);
                        SeqSender sender4 = new SeqSender(bankA[3], sendMessage);
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
                    
                    //send sequence id back to front end
                    DatagramPacket out = new DatagramPacket((seqCount+"").getBytes(), 1000, inetAddress, port);
                    receiveSocket.send(out);
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
