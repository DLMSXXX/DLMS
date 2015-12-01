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
    
    private static int counter = 0;
    
    public Loan (){}
    
    public Loan (String _ID, String _accountNumber, int _amount, int _dueDate)
    {
      ID = _ID + counter++;
      accountNumber = _accountNumber;
      amount = _amount;
      dueDate = _dueDate;
    }
}
