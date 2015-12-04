package model;

/**
 *
 * @author yucunli
 */
public class Loan {
    
    public String ID = null;
    public String accountNumber = null;
    public int amount = 0;
    public String dueDate = "";
    
    public Loan (){}
    
    //Initialize with ID
    public Loan (String _ID, String _accountNumber, int _amount, String _dueDate)
    {
        ID = _ID;
        accountNumber = _accountNumber;
        amount = _amount;
        dueDate = _dueDate;
    }
    
    //Initialize
    public Loan(String _accountNumber, int _amount, String _dueDate){
        ID = System.currentTimeMillis()+"";
        accountNumber = _accountNumber;
        amount = _amount;
        dueDate = _dueDate;
    }
}
