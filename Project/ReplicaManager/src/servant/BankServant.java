package servant;

import java.util.ArrayList;
import java.util.HashMap;
import model.Account;
import model.Loan;
import rm.ReplicaManager;

/**
 *
 * @author yucunli
 */
public class BankServant implements BankServantInterface{
    public int port;
    public int[] other_port;
    
    public BankServant(){
    }
    
    //Initialize
    public BankServant(int _port, int[] _other_port){
        this.port = _port;
        
        this.other_port = _other_port;
    }
    
    //Initialize from other server
    public BankServant(int _port, int[] _other_port, int _target_port){
        this.port = _port;
        this.other_port = _other_port;
        
        this.other_port = _other_port;
        
        // recovering data from _target_port bank servant
    }
    
    //*****************************
    //*****************************
    //Customized Receiver and Sender
    //*****************************
    //*****************************
    
    
    //*****************************
    //*****************************
    //Code From Previous Assignment
    //*****************************
    //*****************************
    public static final String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
                        "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    
    private static final int DEFAULT_CREDIT = 500;
    private static final int DEFAULT_DUEDATE = 100;
    
    HashMap<String, ArrayList<Account>> account_HashMap;
    HashMap<String, Loan> loan_HashMap;

    @Override
    public String openAccount(String bank, String firstName, String lastName, String emailAddress, String phoneNumber, String password) {
        Account account = null;
        
        boolean foundAccount = false;
        ArrayList<Account> list = account_HashMap.get(lastName.toLowerCase().substring(0, 1));
        for(Account temp : list){
            if(temp.emailAddress.equals(emailAddress)){
                foundAccount = true;
            }
        }
        
        if(!foundAccount){
            
            account = new Account(firstName, lastName, emailAddress, phoneNumber, password, DEFAULT_CREDIT);
            list.add(account);
            
            //log(firstName + lastName + " " + " create account : " + account.accountNumber);
            //logCustomer(account.accountNumber, "account created");
            return account.accountNumber + " has been created for user " + firstName + " " + lastName;
        }else{
            return "Acount is already existed!";
        }
    }

    @Override
    public String getLoan(String bank, String accountNumber, String password, int loanAmount) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String transferLoan(String loanID, String currentBank, String otherBank) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String delayPayment(String bank, String loanID, int currentDueDate, int newDueDate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String printCustomerInfo(String bank) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
