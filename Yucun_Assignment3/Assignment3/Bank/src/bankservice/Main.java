package bankservice;

import javax.xml.ws.Endpoint;

/**
 *
 * @author yucunli
 */
public class Main {

    public static void main(String[] args) {
        BankServant Bank_A = new BankServant();
        Bank_A.port = 2298;
        Bank_A.uniqueAccountID_Base = 1;
        Bank_A.loanID_Base = 1;
        Bank_A.rest_port[0] = 5298;
        Bank_A.rest_port[1] = 7298;

        BankServant Bank_B = new BankServant();
        Bank_B.port = 5298;
        Bank_B.uniqueAccountID_Base = 1000;
        Bank_B.loanID_Base = 1000;
        Bank_B.rest_port[0] = 2298;
        Bank_B.rest_port[1] = 7298;

        BankServant Bank_C = new BankServant();
        Bank_C.port = 7298;
        Bank_C.uniqueAccountID_Base = 2000;
        Bank_C.loanID_Base = 2000;
        Bank_C.rest_port[0] = 2298;
        Bank_C.rest_port[1] = 5298;
        
        Endpoint.publish("http://localhost:8888/ws/bankA", Bank_A);
        Endpoint.publish("http://localhost:8888/ws/bankB", Bank_B);
        Endpoint.publish("http://localhost:8888/ws/bankC", Bank_C);
    }
    
}
