package Main;

import frontend.DlmsFrontEnd;
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
import rm.ReplicaManager;
import sequencer.Sequencer;

public class Main {

    public static void main(String[] args) throws InvalidName, ServantAlreadyActive, ObjectNotActive, WrongPolicy, FileNotFoundException, AdapterInactive {
        
        // run front end
        DlmsFrontEnd FE = new DlmsFrontEnd(5000, new int[]{7001, 7002, 7003, 7004}, 4000);
        ORB _orb = ORB.init(args, null);
        POA _rootPOA = POAHelper.narrow(_orb.resolve_initial_references("RootPOA"));
        
        byte[] _id = _rootPOA.activate_object(FE);
        org.omg.CORBA.Object _ref = _rootPOA.id_to_reference(_id);
        String _ior = _orb.object_to_string(_ref);
        
        PrintWriter _file = new PrintWriter("FEOR.txt");
        _file.println(_ior);
        _file.close();

        _rootPOA.the_POAManager().activate();
        _orb.run();
        
        // run sequencer
        /*Sequencer sequencer = new Sequencer(new int[]{6000, 6003, 6006, 6009}, 
                                            new int[]{6001, 6004, 6007, 6010},
                                            new int[]{6002, 6005, 6008, 6011}, 
                                            5000);
        
        // run replica manager
        ReplicaManager rm1 = new ReplicaManager(6000, 6001, 6002, 7001, new int[]{7002, 7003, 7004});
        ReplicaManager rm2 = new ReplicaManager(6003, 6004, 6005, 7002, new int[]{7001, 7003, 7004});
        ReplicaManager rm3 = new ReplicaManager(6006, 6007, 6008, 7003, new int[]{7001, 7002, 7004});
        ReplicaManager rm4 = new ReplicaManager(6009, 6010, 6011, 7004, new int[]{7001, 7002, 7003});
        */
    }
    
}