package DLMSApp.crown;

/**
 * Interface definition: dlms.
 * 
 * @author OpenORB Compiler
 */
public interface dlmsOperations
{
    /**
     * Operation openAccount
     */
    public String openAccount(String Bank, String fName, String lName, String email, String phoneNumber, String password);

    /**
     * Operation getLoan
     */
    public String getLoan(String Bank, String accountNumber, String password, String loanAmount);

    /**
     * Operation delayPayment
     */
    public String delayPayment(String Bank, String loanID, String currentD, String newD);

    /**
     * Operation printCustomerInfo
     */
    public String printCustomerInfo(String Bank);

    /**
     * Operation transferLoan
     */
    public String transferLoan(String loanID, String currentBank, String otherBank);

}
