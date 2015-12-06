package test;

import test.OpenAccountTest;
import test.GetLoanTest;
import test.TransferLoanTest;

public class Main {

    public static void main(String[] args) {
       
        // test open account
        OpenAccountTest openAccountTest = new OpenAccountTest();
        openAccountTest.runTest(args);
        
        // test get loan
        GetLoanTest getLoanTest = new GetLoanTest();
        getLoanTest.runTest(args);
        
        // test transfer loan
        TransferLoanTest transferLoanText = new TransferLoanTest();
        transferLoanText.runTest(args);
    }
    
}
