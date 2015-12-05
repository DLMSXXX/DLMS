package frontend;

import DLMS.dlmsPOA;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public class dlmsFrontEnd extends dlmsPOA{	
	
	private int SequencerPortNumber = 2222;
	private int RMPort = 3333;
	private int RMNumber = 4;

	static class BankReceiver extends Thread{
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
	}
	
	static class RMSender extends Thread{
		private String sendMsg;
		private int RMPortNumber;
		public RMSender(String s, int port)
		{
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
	}
	
	@Override
	public String openAccount(String Bank, String fName, String lName, String email, String phoneNumber,
			String password) {
		// TODO Auto-generated method stub
		String result = null;
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			InetAddress aHost = InetAddress.getByName("localhost");
			String toServer = "openAccount," + Bank + "," + fName + "," + lName + "," + email + ","
					+ phoneNumber + "," + password;
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
		return result;
	}

	@Override
	public String getLoan(String Bank, String accountNumber, String password, String loanAmount) {
		// TODO Auto-generated method stub
		String result = null;
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
		return result;
	}

	@Override
	public String delayPayment(String Bank, String loanID, String currentD, String newD) {
		// TODO Auto-generated method stub
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
		return result;
	}

	@Override
	public String printCustomerInfo(String Bank) {
		// TODO Auto-generated method stub
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
		return result;
	}

	@Override
	public String transferLoan(String loanID, String currentBank, String otherBank) {
		// TODO Auto-generated method stub
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
		return result;
	}
	
	//method to receive from banks, compare and send to RMs
	private String RCS()
	{
		String result = null;
		// receive from banks
		BankReceiver[] br = new BankReceiver[RMNumber];
		for (int i = 0; i<RMNumber; i++)
		{
			br[i] = new BankReceiver();
			br[i].start();
		}
		for (int i = 0; i<RMNumber; i++)
		{
			try {
				br[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// compare
		int downRM = 0;
		for (int i = 0; i<RMNumber; i++)
		{
			if (br[i].down == true)
			{
				downRM = i+1;
				break;
			}
		}
		String toRM = null;
		outerloop:
		for (int i = 0; i<RMNumber; i++)
		{
			for (int j = 0; j<RMNumber; j++)
			{
				if(br[i].result.equals(br[j].result))
				{
					toRM = Integer.toString(downRM) + "," + br[i].result;
					result = br[i].result;
					break outerloop;
				}
			}
		}
		
		// send to RMs
		RMSender[] rs = new RMSender[RMNumber];
		for (int i = 0; i<RMNumber; i++)
		{
			rs[i] = new RMSender(toRM, (RMPort + i));
			rs[i].start();
		}
		for (int i = 0; i<RMNumber; i++)
		{
			try {
				rs[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
	}

	public static void main(String[] args) throws InvalidName, ServantAlreadyActive, WrongPolicy, ObjectNotActive, FileNotFoundException, AdapterInactive {
		// TODO Auto-generated method stub
		dlmsFrontEnd FE = new dlmsFrontEnd();
		ORB _orb = ORB.init(args, null);
		POA _rootPOA = POAHelper.narrow(_orb.resolve_initial_references("RootPOA"));
		
		byte[] _id = _rootPOA.activate_object(FE);
		org.omg.CORBA.Object _ref = _rootPOA.id_to_reference(_id);
		String _ior = _orb.object_to_string(_ref);
		// Print IOR in the file
		PrintWriter _file = new PrintWriter("FEOR.txt");
		_file.println(_ior);
		_file.close();
		
		_rootPOA.the_POAManager().activate();
		_orb.run();
	}
}
