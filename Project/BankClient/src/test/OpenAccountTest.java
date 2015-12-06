package test;

import client.dlmsClient;

/**
 *
 * @author yucunli
 */
public class OpenAccountTest {
    
    String[] ap = new String[4];
    
    public void runTest(String[] args){
        dlmsClient client = new dlmsClient(args);
        
        ap[0] = client.sendRequest("A", "openAccount", new String[]{"li", "yu", "email", "phone", "pass"});
        ap[1] = client.sendRequest("B", "openAccount", new String[]{"ye", "ni", "email", "phone", "pass"});
        ap[2] = client.sendRequest("C", "openAccount", new String[]{"wang", "guan", "email", "phone", "pass"});
        ap[3] = client.sendRequest("D", "openAccount", new String[]{"du", "jinyang", "email", "phone", "pass"});
    }
}
