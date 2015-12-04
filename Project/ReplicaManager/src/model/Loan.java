package model;

/**
 *
 * @author yucunli
 */
public class Loan {
    
    public String ID = null;
    public String accountNumber = null;
    public int amount = (int)0;
    public int dueDate = (int)0;
    
    public Loan (){}
    
    //Initialize with ID
    public Loan (String _ID, String _accountNumber, int _amount, int _dueDate)
    {
        ID = _ID;
        accountNumber = _accountNumber;
        amount = _amount;
        dueDate = _dueDate;
    }
    
    //Initialize
    public Loan(String _accountNumber, int _amount, int _dueDate){
        ID = System.currentTimeMillis()+"";
        accountNumber = _accountNumber;
        amount = _amount;
        dueDate = _dueDate;
    }
}
