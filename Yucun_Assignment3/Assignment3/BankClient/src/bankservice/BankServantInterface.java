package bankservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface BankServantInterface {

    @WebMethod
    String openAccount(@WebParam(name="bank") String bank, 
            @WebParam(name="firstName") String firstName, 
            @WebParam(name="lastName") String lastName, 
            @WebParam(name="email") String emailAddress, 
            @WebParam(name="phone") String phoneNumber, 
            @WebParam(name="password") String password);
    
    @WebMethod
    String getLoan(@WebParam(name="bank") String bank, 
            @WebParam(name="accountNumber") String accountNumber, 
            @WebParam(name="password") String password, 
            @WebParam(name="loanAmount") int loanAmount);
    
    @WebMethod
    String transferLoan(@WebParam(name="loan ID") String loanID, 
            @WebParam(name="currentBank") String currentBank, 
            @WebParam(name="otherBank") String otherBank);
    
    @WebMethod
    String delayPayment(@WebParam(name="bank") String bank, 
            @WebParam(name="loanID") String loanID, 
            @WebParam(name="currentDueDate") int currentDueDate, 
            @WebParam(name="newDueDate") int newDueDate);
    
    @WebMethod
    String printCustomerInfo(@WebParam(name="bank") String bank);
}
