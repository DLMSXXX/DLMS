package initial;

import frontend.DlmsFrontEnd;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import rm.ReplicaManager1;
import sequencer.Sequencer;

/**
 *
 * @author yucunli
 */
public class RunServer {

    //********************
    //Start to run server
    //********************
    // run sequencer
    public void start(String[] args){
        try {
            // run sequencer
            Sequencer sequencer = new Sequencer(new int[]{6000, 6003, 6006, 6009},
                    new int[]{6001, 6004, 6007, 6010},
                    new int[]{6002, 6005, 6008, 6011},
                    5000);
            
            // run replica manager
            ReplicaManager1 rm1 = new ReplicaManager1(6000, 6001, 6002, 7001, new int[]{7002, 7003, 7004}, 4000);
            ReplicaManager1 rm2 = new ReplicaManager1(6003, 6004, 6005, 7002, new int[]{7001, 7003, 7004}, 4000);
            ReplicaManager1 rm3 = new ReplicaManager1(6006, 6007, 6008, 7003, new int[]{7001, 7002, 7004}, 4000);
            ReplicaManager1 rm4 = new ReplicaManager1(6009, 6010, 6011, 7004, new int[]{7001, 7002, 7003}, 4000);
            
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
        } catch (InvalidName ex) {
            Logger.getLogger(RunServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServantAlreadyActive ex) {
            Logger.getLogger(RunServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WrongPolicy ex) {
            Logger.getLogger(RunServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ObjectNotActive ex) {
            Logger.getLogger(RunServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RunServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AdapterInactive ex) {
            Logger.getLogger(RunServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

}
