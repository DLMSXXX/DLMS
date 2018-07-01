package DLMS;


/**
* DLMS/dlmsOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DLMS.idl
* Saturday, December 12, 2015 7:33:14 PM EST
*/

public interface dlmsOperations 
{
  String openAccount (String Bank, String fName, String lName, String email, String phoneNumber, String password);
  String getLoan (String Bank, String accountNumber, String password, String loanAmount);
  String delayPayment (String Bank, String loanID, String currentD, String newD);
  String printCustomerInfo (String Bank);
  String transferLoan (String loanID, String currentBank, String otherBank);
} // interface dlmsOperations