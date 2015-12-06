package rm;

import java.util.HashMap;
import servant.dlmsImpl;

public class ReplicaManager2 {
    /*public static void main(String[] args) {
        dlmsImpl _bank1Obj = new dlmsImpl(6003, 7002, 4000);
        _bank1Obj.rest_port[0] = 6004;
        _bank1Obj.rest_port[1] = 6005;
        dlmsImpl _bank2Obj = new dlmsImpl(6004, 7002, 4000);
        _bank2Obj.rest_port[0] = 6003;
        _bank2Obj.rest_port[1] = 6005;
        dlmsImpl _bank3Obj = new dlmsImpl(6005, 7002, 4000);
        _bank3Obj.rest_port[0] = 6003;
        _bank3Obj.rest_port[1] = 6004;
    }*/
    
    public int Bank_A_Port;
    public int Bank_B_Port;
    public int Bank_C_Port;
    
    HashMap<String, dlmsImpl> BankServantMap;

    private int[] other_rm;
    
    private int RM_port, FE_port;
    
    // wrong operations recording
    private int Wrong_Count = 0;
    
    public ReplicaManager2(int bankA_port, int bankB_port, int bankC_port, int rm_port, int[] other_rm_port, int fe_port){
        //set each bank port
        Bank_A_Port = bankA_port;
        Bank_B_Port = bankB_port;
        Bank_C_Port = bankC_port;
        //set other rm array
        other_rm = other_rm_port;
        
        RM_port = rm_port;
        FE_port = fe_port;
    
        BankServantMap = new HashMap<String, dlmsImpl>();
        BankServantMap.put("A", new dlmsImpl());
        BankServantMap.put("B", new dlmsImpl());
        BankServantMap.put("C", new dlmsImpl());
    }
}
