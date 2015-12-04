package DLMSApp.crown;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public class dlmsServer {

	public static void main(String[] args) throws InvalidName, ServantAlreadyActive, WrongPolicy, ObjectNotActive,
			FileNotFoundException, AdapterInactive {

		dlmsImpl _bank1Obj = new dlmsImpl(2061);
        _bank1Obj.rest_port[0] = 2062;
        _bank1Obj.rest_port[1] = 2063;
		dlmsImpl _bank2Obj = new dlmsImpl(2062);
        _bank2Obj.rest_port[0] = 2061;
        _bank2Obj.rest_port[1] = 2063;
		dlmsImpl _bank3Obj = new dlmsImpl(2063);
        _bank3Obj.rest_port[0] = 2061;
        _bank3Obj.rest_port[1] = 2062;

		ORB _orb = ORB.init(args, null);
		POA _rootPOA = POAHelper.narrow(_orb.resolve_initial_references("RootPOA"));

		// Bank 1
		byte[] _id = _rootPOA.activate_object(_bank1Obj);
		org.omg.CORBA.Object _ref = _rootPOA.id_to_reference(_id);
		String _ior = _orb.object_to_string(_ref);
		// Print IOR in the file
		PrintWriter _file = new PrintWriter("bank1IOR.txt");
		_file.println(_ior);
		_file.close();

		// Bank 2
		_id = _rootPOA.activate_object(_bank2Obj);
		_ref = _rootPOA.id_to_reference(_id);
		_ior = _orb.object_to_string(_ref);
		// Print IOR in the file
		_file = new PrintWriter("bank2IOR.txt");
		_file.println(_ior);
		_file.close();

		// Bank 3
		_id = _rootPOA.activate_object(_bank3Obj);
		_ref = _rootPOA.id_to_reference(_id);
		_ior = _orb.object_to_string(_ref);
		// Print IOR in the file
		_file = new PrintWriter("bank3IOR.txt");
		_file.println(_ior);
		_file.close();

		// For Client
		_rootPOA.the_POAManager().activate();
		_orb.run();

	}

}
