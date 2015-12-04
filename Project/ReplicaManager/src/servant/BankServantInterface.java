package servant;

/**
 *
 * @author yucunli
 */
public interface BankServantInterface {
    public String openAccount(String bank, String firstName, String lastName, String emailAddress, String phoneNumber, String password);
    public String getLoan(String bank, String accountNumber, String password, int loanAmount);
    public String transferLoan(String loanID, String currentBank, String otherBank);
    public String delayPayment(String bank, String loanID, int currentDueDate, int newDueDate);
    public String printCustomerInfo(String bank);
}
