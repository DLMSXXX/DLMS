package bankservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

/**
 *
 * @author yucunli
 */
public class BankClient {

    public static void main(String[] args) throws IOException {
        
        BankClient main = new BankClient();
        
        System.out.println("Welcome to our bank system customer client!");
        boolean login = true;
        while(login){
            main.console();
            System.out.println("Do you have any other operation to do?\n1. Yes\n2. No");
            
            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	    String choice = bufferRead.readLine();
            
            if(choice.equals("2")){
                login = false;
                System.out.println("Have a nice day!");
            }
            System.out.println("---------------------------");
        }
    }
    
    public void sendRequest(String bankName, String request, String[] infoArray){
        try{
            URL url = new URL("http://localhost:8888/ws/" + bankName + "?wsdl");
            QName qname = new QName("http://bankservice/", "BankServantService");
            Service service = Service.create(url, qname);
            BankServantInterface bankinterface = service.getPort(BankServantInterface.class);
            
            if(request.equals("openAccount")){
                System.out.println(bankinterface.openAccount("", infoArray[0], infoArray[1], infoArray[2], infoArray[3], infoArray[4]));
            }else if(request.equals("getLoan")){
                System.out.println(bankinterface.getLoan("", infoArray[0], infoArray[1], Integer.valueOf(infoArray[2])));
            }else if(request.equals("transferLoan")){
                System.out.println(bankinterface.transferLoan(infoArray[0], "", infoArray[1]));
            }
            
        }catch(MalformedURLException ex){
            System.out.println(ex.toString());
        }
        
    }
    
    public void console(){
        System.out.println("Here are two available operation you can do on our system:");
        System.out.println("1. Open Account");
        System.out.println("2. Get Loan");
        System.out.println("3. Transfer Loan");
        
        try{
	    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	    String choice = bufferRead.readLine();
            
            if(!(choice.equals("1") || choice.equals("2") || choice.equals("3"))){
                System.out.println("Illegal Input, please choice 1 or 2 or 3");
                return;
            }
            
            System.out.println("Please choose a bank:");
            System.out.println("1. " + "BankA" + "\n2. "+"BankB"
            +"\n3. "+"BankC");

            bufferRead = new BufferedReader(new InputStreamReader(System.in));
            String bank = bufferRead.readLine();
            String bankName = null;

            if(bank.equals("1")){
                bankName = "bankA";
            }else if(bank.equals("2")){
                bankName = "bankB";
            }else if(bank.equals("3")){
                bankName = "bankC";
            }else{
                System.out.println("Illegal Input, please choice 1 or 2 or 3");
                return;
            }
	      
            if(choice.equals("1")){
                
                System.out.println("Please enter your information(FirstName, LastName, Email, Phone, Password)");
                bufferRead = new BufferedReader(new InputStreamReader(System.in));
                String info = bufferRead.readLine();
                String[] infoArray = info.split(",");
                
                sendRequest(bankName, "openAccount", infoArray);
                
            }else if(choice.equals("2")){
                
                System.out.println("Please enter your information(AccountNumber, Password, LoanAmount)");
                bufferRead = new BufferedReader(new InputStreamReader(System.in));
                String info = bufferRead.readLine();
                String[] infoArray = info.split(",");
                
                sendRequest(bankName, "getLoan", infoArray);
                
            }else if(choice.equals("3")){
                System.out.println("Please enter your information(loan ID, target bank)");
                System.out.println("BankA: 2298; BankB: 5298; BankC: 7298");
                bufferRead = new BufferedReader(new InputStreamReader(System.in));
                String info = bufferRead.readLine();
                String[] infoArray = info.split(",");
                
                sendRequest(bankName, "transferLoan", infoArray);
                
            }else{
                System.out.println("Illegal Input, please choice 1 or 2 or 3");
            }
	}
	catch(IOException e)
	{
            System.out.println(e.toString());
	}
        catch(Exception e){
            System.out.println(e.toString());
        }
    }
}
