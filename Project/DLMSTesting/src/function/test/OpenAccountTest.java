package function.test;

import client.dlmsClient;

/**
 *
 * @author yucunli
 */
public class OpenAccountTest {
    
    public void runTest(String[] args){
        dlmsClient client = new dlmsClient(args);
        
        String accountID = client.sendRequest("A", "openAccount", new String[]{"li", "yu", "email", "phone", "pass"});
    }
    
}
