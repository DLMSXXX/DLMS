package Main;

import function.test.OpenAccountTest;
import initial.RunServer;

public class Main {

    public static void main(String[] args) {
        // run server
        RunServer runserver = new RunServer();
        runserver.start(args);
        
        // test open account
        OpenAccountTest openAccountTest = new OpenAccountTest();
        openAccountTest.runTest(args);
    }
    
}
