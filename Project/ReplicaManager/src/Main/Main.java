package Main;

import frontend.DlmsFrontEnd;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import rm.ReplicaManager1;
import rm.ReplicaManager2;
import rm.ReplicaManager3;
import rm.ReplicaManager4;
import rm.ReplicaManagerX;
import sequencer.Sequencer;

public class Main {

    public static void main(String[] args) throws InvalidName, ServantAlreadyActive, ObjectNotActive, WrongPolicy, FileNotFoundException, AdapterInactive {
        
        // run sequencer
        Sequencer sequencer = new Sequencer(new int[]{6000, 6003, 6006, 6009},
                                            new int[]{6001, 6004, 6007, 6010},
                                            new int[]{6002, 6005, 6008, 6011},
                                            5000);
        
        // run replica manager
        ReplicaManager1 rm1 = new ReplicaManager1(6000, 6001, 6002, 7001, new int[]{7002, 7003, 7004}, 4000);
        ReplicaManager2 rm2 = new ReplicaManager2(6003, 6004, 6005, 7002, new int[]{7001, 7003, 7004}, 4000);
        ReplicaManager3 rm3 = new ReplicaManager3(6006, 6007, 6008, 7003, new int[]{7001, 7002, 7004}, 4000);
        ReplicaManager4 rm4 = new ReplicaManager4(6009, 6010, 6011, 7004, new int[]{7001, 7002, 7003}, 4000);
        
        // run front end
        HashMap<String, Integer> rm_port_map = new HashMap<String, Integer>();
        rm_port_map.put("RM1", 7001);
        rm_port_map.put("RM2", 7002);
        rm_port_map.put("RM3", 7003);
        rm_port_map.put("RM4", 7004);
        DlmsFrontEnd FE = new DlmsFrontEnd(5000, rm_port_map, 4000);
        ORB _orb = ORB.init(args, null);
        POA _rootPOA = POAHelper.narrow(_orb.resolve_initial_references("RootPOA"));
        
        byte[] _id = _rootPOA.activate_object(FE);
        org.omg.CORBA.Object _ref = _rootPOA.id_to_reference(_id);
        String _ior = _orb.object_to_string(_ref);
        String path = System.getProperty("user.dir");
            path = path.substring(0, path.lastIndexOf("/") + 1);
        PrintWriter _file = new PrintWriter(path +"FEOR.txt");
        _file.println(_ior);
        _file.close();

        _rootPOA.the_POAManager().activate();
        _orb.run();
        
    }
    
}
