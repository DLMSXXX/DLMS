package sequencer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Sequencer {

    private int[] bankA;
    private int[] bankB;
    private int[] bankC;

    private int Sequencer_port;

    Integer seqCount = new Integer(0);
    BlockingQueue<String> seqQueue = new LinkedBlockingQueue<String>();

    public Sequencer(int[] bankA, int[] bankB, int[] bankC, int sequencer_port) {
        this.bankA = bankA;
        this.bankB = bankB;
        this.bankC = bankC;

        this.Sequencer_port = sequencer_port;

        SeqReceiver seqReceiver = new SeqReceiver();
        seqReceiver.start();

        SequenceQueueConsumer sequenceQueueConsumer = new SequenceQueueConsumer();
        sequenceQueueConsumer.start();
    }

    class SequenceQueueConsumer extends Thread {

        @Override
        public void run() {

            while (true) {
                if (seqQueue.size() != 0) {

                    String sendMessage = seqQueue.peek();
                    
                    System.out.println("SequenceQueueConsumer **** "+sendMessage);

                    String[] request = sendMessage.split("%")[1].split("#");
                    String requestType = request[0];
                    
                     String requestBank;
                     
                    if(requestType.equals("transferLoan")){
                        requestBank = request[1].split(",")[1];
                    }else{
                        requestBank = request[1].split(",")[0];
                    }

                    switch (requestBank) {
                        case "A":
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

                            break;
                        case "B":
                            sender1 = new SeqSender(bankB[0], sendMessage);
                            sender2 = new SeqSender(bankB[1], sendMessage);
                            sender3 = new SeqSender(bankB[2], sendMessage);
                            sender4 = new SeqSender(bankB[3], sendMessage);
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
                            break;
                        case "C":
                            sender1 = new SeqSender(bankC[0], sendMessage);
                            sender2 = new SeqSender(bankC[1], sendMessage);
                            sender3 = new SeqSender(bankC[2], sendMessage);
                            sender4 = new SeqSender(bankC[3], sendMessage);
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
                            break;
                        default:
                            break;
                    }

                    seqQueue.poll();

                }
            }

        }
    }

    class SeqReceiver extends Thread {

        @Override
        public void run() {
            try {
                DatagramSocket receiveSocket = new DatagramSocket(Sequencer_port);

                while (true) {
                    byte[] receiveData = new byte[1024];
                    byte[] sendData = new byte[1024];
                    
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    receiveSocket.receive(receivePacket);
                    String request = new String(receivePacket.getData());
                    InetAddress inetAddress = receivePacket.getAddress();
                    int port = receivePacket.getPort();

                    seqQueue.add(Integer.toString(++seqCount) + "%" + request);

                    sendData = (seqCount + "").getBytes();

                    //send sequence id back to front end
                    DatagramPacket out = new DatagramPacket(sendData, sendData.length, inetAddress, port);
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

        private int serverPort;
        private String requestMessage;

        public SeqSender(int serverPort, String requestMessage) {
            super();
            this.serverPort = serverPort;
            this.requestMessage = requestMessage;
        }

        @Override
        public void run() {
            try {

                DatagramSocket sendSocket = new DatagramSocket();
                InetAddress inetAddress = InetAddress.getByName("localhost");

                byte[] sendData = new byte[1024];
                sendData = requestMessage.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, inetAddress, serverPort);
                sendSocket.send(sendPacket);
                

            } catch (SocketException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

    }

}
