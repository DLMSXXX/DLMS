package rm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import model2.Customer;
import model2.Loan;
import servant.dlmsImpl;

public class ReplicaManager2 {

    public HashMap<String, Integer> port_map;

    public HashMap<String, dlmsImpl> BankServantMap;

    private int[] other_rm;

    // RMs status
    public String rm1Status;
    public String rm2Status;
    public String rm3Status;
    public String rm4Status;

    private int RM_port, FE_port;

    // wrong operations recording
    private int Wrong_Count = 0;

    public ReplicaManager2(int bankA_port, int bankB_port, int bankC_port, int rm_port, int[] other_rm_port, int fe_port) {
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

    public void stringToHash(String s, ReplicaManager2 replicaManager2)
    {
        String banks[] = s.split("!");
        String elements[] = banks[0].split("@");
        String customers[] = elements[0].split("|");
        String loans[] = elements[1].split("|");
            
        for (int i = 0; i<customers.length; i++)
        {
            String token[] = customers[i].split(",");
            Customer customer = new Customer(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim(), token[4].trim(), token[5].trim());    
            String key = token[2].trim().substring(0, 1);
            List<Customer> customerList = replicaManager2.BankServantMap.get("A").accounts.get(key);
            customerList.add(customer);
            replicaManager2.BankServantMap.get("A").accounts.put(key, customerList);
        }    
        for (int i = 0; i<loans.length; i++)
        {
            String token[] = loans[i].split(",");
            Loan loan = new Loan(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim());
            Integer key = Integer.parseInt(token[0].trim());
            replicaManager2.BankServantMap.get("A").loans.put(key, loan);
        }

        String elements2[] = banks[1].split("@");
        String customers2[] = elements2[0].split("|");
        String loans2[] = elements2[1].split("|");
        
        for (int i = 0; i<customers2.length; i++)
        {
            String token[] = customers2[i].split(",");
            Customer customer = new Customer(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim(), token[4].trim(), token[5].trim());    
            String key = token[2].trim().substring(0, 1);
            List<Customer> customerList = replicaManager2.BankServantMap.get("B").accounts.get(key);
            customerList.add(customer);
            replicaManager2.BankServantMap.get("B").accounts.put(key, customerList);
        }    
        for (int i = 0; i<loans2.length; i++)
        {
            String token[] = loans2[i].split(",");
            Loan loan = new Loan(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim());
            Integer key = Integer.parseInt(token[0].trim());
            replicaManager2.BankServantMap.get("B").loans.put(key, loan);
        }
        
        String elements3[] = banks[1].split("@");
        String customers3[] = elements3[0].split("|");
        String loans3[] = elements3[1].split("|");
        
        for (int i = 0; i<customers3.length; i++)
        {
            String token[] = customers3[i].split(",");
            Customer customer = new Customer(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim(), token[4].trim(), token[5].trim());    
            String key = token[2].trim().substring(0, 1);
            List<Customer> customerList = replicaManager2.BankServantMap.get("C").accounts.get(key);
            customerList.add(customer);
            replicaManager2.BankServantMap.get("C").accounts.put(key, customerList);
        }    
        for (int i = 0; i<loans3.length; i++)
        {
            String token[] = loans3[i].split(",");
            Loan loan = new Loan(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim());
            Integer key = Integer.parseInt(token[0].trim());
            replicaManager2.BankServantMap.get("C").loans.put(key, loan);
        }
    }
    
    public String hashToString(ReplicaManager2 rm) {
        String result = null;

        for (String key : rm.BankServantMap.get("A").accounts.keySet()) {
            if (rm.BankServantMap.get("A").accounts.get(key) != null) {
                List<Customer> listCustomer = rm.BankServantMap.get("A").accounts.get(key);
                for (int i = 0; i < listCustomer.size(); i++) {
                    result += listCustomer.get(i).getAccountNumber() + "," + listCustomer.get(i).getFirstName() + "," + listCustomer.get(i).getLastName() + ","
                            + listCustomer.get(i).getEmail() + "," + listCustomer.get(i).getPhoneNumber() + "," + listCustomer.get(i).getPassword() + "|";
                }
            }
        }
        result += "@";
        for (Integer key : rm.BankServantMap.get("A").loans.keySet()) {
            if (rm.BankServantMap.get("A").loans.get(key) != null) {
                result += rm.BankServantMap.get("A").loans.get(key).getLoanId() + "," + rm.BankServantMap.get("A").loans.get(key).getAccountNumber() + "," + rm.BankServantMap.get("A").loans.get(key).getLoanAmt()
                        + rm.BankServantMap.get("A").loans.get(key).getDueDate() + "|";
            }
        }
        
        result += "!";
        
        for (String key : rm.BankServantMap.get("B").accounts.keySet()) {
            if (rm.BankServantMap.get("B").accounts.get(key) != null) {
                List<Customer> listCustomer = rm.BankServantMap.get("B").accounts.get(key);
                for (int i = 0; i < listCustomer.size(); i++) {
                    result += listCustomer.get(i).getAccountNumber() + "," + listCustomer.get(i).getFirstName() + "," + listCustomer.get(i).getLastName() + ","
                            + listCustomer.get(i).getEmail() + "," + listCustomer.get(i).getPhoneNumber() + "," + listCustomer.get(i).getPassword() + "|";
                }
            }
        }
        result += "@";
        for (Integer key : rm.BankServantMap.get("B").loans.keySet()) {
            if (rm.BankServantMap.get("B").loans.get(key) != null) {
                result += rm.BankServantMap.get("B").loans.get(key).getLoanId() + "," + rm.BankServantMap.get("B").loans.get(key).getAccountNumber() + "," + rm.BankServantMap.get("B").loans.get(key).getLoanAmt()
                        + rm.BankServantMap.get("B").loans.get(key).getDueDate() + "|";
            }
        }
        
        result += "!";
        
        for (String key : rm.BankServantMap.get("C").accounts.keySet()) {
            if (rm.BankServantMap.get("C").accounts.get(key) != null) {
                List<Customer> listCustomer = rm.BankServantMap.get("C").accounts.get(key);
                for (int i = 0; i < listCustomer.size(); i++) {
                    result += listCustomer.get(i).getAccountNumber() + "," + listCustomer.get(i).getFirstName() + "," + listCustomer.get(i).getLastName() + ","
                            + listCustomer.get(i).getEmail() + "," + listCustomer.get(i).getPhoneNumber() + "," + listCustomer.get(i).getPassword() + "|";
                }
            }
        }
        result += "@";
        for (Integer key : rm.BankServantMap.get("C").loans.keySet()) {
            if (rm.BankServantMap.get("C").loans.get(key) != null) {
                result += rm.BankServantMap.get("C").loans.get(key).getLoanId() + "," + rm.BankServantMap.get("C").loans.get(key).getAccountNumber() + "," + rm.BankServantMap.get("C").loans.get(key).getLoanAmt()
                        + rm.BankServantMap.get("C").loans.get(key).getDueDate() + "|";
            }
        }
        
        //System.out.println(result);
        return result;
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
                    if (sentence.equals("ASK")) {
                        RMSender rmSender = new RMSender(receivePacket.getPort(), hashToString(ReplicaManager2.this));
                    } else {
                        //***************************
                        //UDP message processing...
                        //***************************
                        String rmAndItStatus[] = sentence.split("#");
                        rm1Status = rmAndItStatus[1].split("%")[1];
                        rm2Status = rmAndItStatus[2].split("%")[1];
                        rm3Status = rmAndItStatus[3].split("%")[1];
                        rm4Status = rmAndItStatus[4].split("%")[1];
                        System.out.println(RMport);
                        int _port = 0;
                        //check if corresponding bankserver correct, if not recovery
                        for (int i = 1; i <= 4; i++) {
                            if (rmAndItStatus[i].split("%")[0].equals(Integer.toString(RMport))) {
                                if (rmAndItStatus[i].split("%")[1].equals("Running")) {
                                    break;
                                }
                                Wrong_Count++;
                                if (rm1Status.equals("Running")) {
                                    _port = Integer.parseInt(rmAndItStatus[1].split("%")[0]);
                                    //ask for data
                                } else if (rm2Status.equals("Running")) {
                                    _port = Integer.parseInt(rmAndItStatus[2].split("%")[0]);
                                    //ask for data
                                } else if (rm3Status.equals("Running")) {
                                    _port = Integer.parseInt(rmAndItStatus[3].split("%")[0]);
                                    //ask for data
                                } else if (rm4Status.equals("Running")) {
                                    _port = Integer.parseInt(rmAndItStatus[4].split("%")[0]);
                                    //ask for data
                                }
                                String ask = "ASK";
                                DatagramPacket datagramPacket = new DatagramPacket(ask.getBytes(), _port);
                                serverSocket.send(datagramPacket);
                                receivePacket = new DatagramPacket(receiveData, _port);
                                String data = new String(receivePacket.getData());
                                stringToHash(data, ReplicaManager2.this);
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
