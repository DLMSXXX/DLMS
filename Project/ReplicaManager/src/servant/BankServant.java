package servant;

import rm.ReplicaManager;

/**
 *
 * @author yucunli
 */
public class BankServant {
    public int port;
    public int[] other_port;
    public int AccountID_UniqueBase;
    public int LoanID_UniqueBase;
    
    public BankServant(){
    }
    
    public BankServant(int _port, int[] _other_port, int _AccountID_UniqueBase, int _LoanID_UniqueBase){
        this.port = _port;
        this.AccountID_UniqueBase = _AccountID_UniqueBase;
        this.LoanID_UniqueBase = _LoanID_UniqueBase;
        
        this.other_port = _other_port;
    }
    
    public BankServant(int _port, int[] _other_port, int _AccountID_UniqueBase, int _LoanID_UniqueBase, int _target_port){
        this.port = _port;
        this.AccountID_UniqueBase = _AccountID_UniqueBase;
        this.LoanID_UniqueBase = _LoanID_UniqueBase;
        
        this.other_port = _other_port;
        
        // recovering data from _target_port bank servant
    }
    
}
