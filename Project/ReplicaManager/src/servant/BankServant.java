package servant;

import rm.ReplicaManager;

/**
 *
 * @author yucunli
 */
public class BankServant {
    public int port;
    public int[] other_port = new int[2];
    public int AccountID_UniqueBase;
    public int LoanID_UniqueBase;
    
    public BankServant(){
    }
    
    public BankServant(int _port, int _AccountID_UniqueBase, int _LoanID_UniqueBase){
        this.port = _port;
        this.AccountID_UniqueBase = _AccountID_UniqueBase;
        this.LoanID_UniqueBase = _LoanID_UniqueBase;
        
        switch(_port){
            case ReplicaManager.BANK_A_PORT:
                other_port[0] = ReplicaManager.BANK_B_PORT;
                other_port[1] = ReplicaManager.BANK_C_PORT;
                break;
            case ReplicaManager.BANK_B_PORT:
                other_port[0] = ReplicaManager.BANK_A_PORT;
                other_port[1] = ReplicaManager.BANK_C_PORT;
                break;
            case ReplicaManager.BANK_C_PORT:
                other_port[0] = ReplicaManager.BANK_A_PORT;
                other_port[1] = ReplicaManager.BANK_B_PORT;
                break;
        }
    }
    
    public BankServant(int _port, int _AccountID_UniqueBase, int _LoanID_UniqueBase, int _target_port){
        this.port = _port;
        this.AccountID_UniqueBase = _AccountID_UniqueBase;
        this.LoanID_UniqueBase = _LoanID_UniqueBase;
        
        switch(_port){
            case ReplicaManager.BANK_A_PORT:
                other_port[0] = ReplicaManager.BANK_B_PORT;
                other_port[1] = ReplicaManager.BANK_C_PORT;
                break;
            case ReplicaManager.BANK_B_PORT:
                other_port[0] = ReplicaManager.BANK_A_PORT;
                other_port[1] = ReplicaManager.BANK_C_PORT;
                break;
            case ReplicaManager.BANK_C_PORT:
                other_port[0] = ReplicaManager.BANK_A_PORT;
                other_port[1] = ReplicaManager.BANK_B_PORT;
                break;
        }
        
        // recovering data from _target_port bank servant
    }
    
}
