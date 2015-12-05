package Main;

import rm.ReplicaManager;
import sequencer.Sequencer;

public class Main {

    public static void main(String[] args) {
        
        // run front end
        
        // run sequencer
        Sequencer sequencer = new Sequencer(7001, 7002, 7003, 7004, 5000);
        
        // run replica manager
        ReplicaManager rm1 = new ReplicaManager(6000, 6001, 6002, 7001, new int[]{7002, 7003, 7004});
        ReplicaManager rm2 = new ReplicaManager(6003, 6004, 6005, 7002, new int[]{7001, 7003, 7004});
        ReplicaManager rm3 = new ReplicaManager(6006, 6007, 6008, 7003, new int[]{7001, 7002, 7004});
        ReplicaManager rm4 = new ReplicaManager(6009, 6010, 6011, 7004, new int[]{7001, 7002, 7003});
    }
    
}
