package DLMS;
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

			byte[] buffer = new byte[100];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);	// receive from bank
			result = new String(reply.getData());
			result = result.trim();
					
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

			byte[] buffer = new byte[100];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);	// receive from bank
			result = new String(reply.getData());
			result = result.trim();
					
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

			byte[] buffer = new byte[100];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);	// receive from bank
			result = new String(reply.getData());
			result = result.trim();
					
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

			byte[] buffer = new byte[100];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);	// receive from bank
			result = new String(reply.getData());
			result = result.trim();
					
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

			byte[] buffer = new byte[100];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);	// receive from bank
			result = new String(reply.getData());
			result = result.trim();				
			
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
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
