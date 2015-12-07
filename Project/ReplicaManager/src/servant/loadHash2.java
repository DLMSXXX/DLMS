/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servant;

import java.util.ArrayList;
import java.util.List;
import model.Account;
import model.Loan;
import rm.ReplicaManager1;

/**
 *
 * @author ni_ye
 */
public class loadHash2 {
    
    
     public String hashToString2(ReplicaManager1 rm)
    {
        String result = null;
        
        for (ArrayList<Account> account_list : rm.BankServantMap.get("A").account_HashMap.values())
        {
            for (Account account : account_list) {
                result += account.accountNumber + "," + account.firstName + "," + account.lastName + "," + account.emailAddress + "," + account.phoneNumber
                        + "," + account.password + "|";
            }         
        }
        result += "@";
        for (Loan temp : rm.BankServantMap.get("A").loan_HashMap.values()) {
            result += temp.ID + "," + temp.accountNumber + "," + temp.amount + "," + temp.dueDate + "|";
        }
        
        result += "!";
        
        for (ArrayList<Account> account_list : rm.BankServantMap.get("B").account_HashMap.values())
        {
            for (Account account : account_list) {
                result += account.accountNumber + "," + account.firstName + "," + account.lastName + "," + account.emailAddress + "," + account.phoneNumber
                        + "," + account.password + "|";
            }         
        }
        result += "@";
        for (Loan temp : rm.BankServantMap.get("B").loan_HashMap.values()) {
            result += temp.ID + "," + temp.accountNumber + "," + temp.amount + "," + temp.dueDate + "|";
        }
        
        result += "!";
        
        for (ArrayList<Account> account_list : rm.BankServantMap.get("C").account_HashMap.values())
        {
            for (Account account : account_list) {
                result += account.accountNumber + "," + account.firstName + "," + account.lastName + "," + account.emailAddress + "," + account.phoneNumber
                        + "," + account.password + "|";
            }         
        }
        result += "@";
        for (Loan temp : rm.BankServantMap.get("C").loan_HashMap.values()) {
            result += temp.ID + "," + temp.accountNumber + "," + temp.amount + "," + temp.dueDate + "|";
        }
        
        //System.out.println(result); 
        return result;
    }
     
    public void stringToHash2(String s, ReplicaManager1 rm)
    {
        String banks[] = s.split("!");
        String elements[] = banks[0].split("@");
        String customers[] = elements[0].split("|");
        String loans[] = elements[1].split("|");
            
        for (int i = 0; i<customers.length; i++)
        {
            String token[] = customers[i].split(",");
            Account customer = new Account(token[1].trim(), token[2].trim(), token[3].trim(), token[4].trim(), token[5].trim(),1000);    
            String key = Character.toString(token[1].trim().charAt(0));
            ArrayList<Account> customerList = rm.BankServantMap.get("A").account_HashMap.get(key);
            customerList.add(customer);
            rm.BankServantMap.get("A").account_HashMap.put(key, customerList);
        }     
        for (int i = 0; i<loans.length; i++)
        {
            String token[] = loans[i].split(",");
            Loan loan = new Loan(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim());
            String key = token[0].trim();
            rm.BankServantMap.get("A").loan_HashMap.put(key, loan);
        }
        
        String elements2[] = banks[0].split("@");
        String customers2[] = elements[0].split("|");
        String loans2[] = elements[1].split("|");
            
        for (int i = 0; i<customers2.length; i++)
        {
            String token[] = customers2[i].split(",");
            Account customer = new Account(token[1].trim(), token[2].trim(), token[3].trim(), token[4].trim(), token[5].trim(),1000);    
            String key = Character.toString(token[1].trim().charAt(0));
            ArrayList<Account> customerList = rm.BankServantMap.get("B").account_HashMap.get(key);
            customerList.add(customer);
            rm.BankServantMap.get("B").account_HashMap.put(key, customerList);
        }     
        for (int i = 0; i<loans2.length; i++)
        {
            String token[] = loans2[i].split(",");
            Loan loan = new Loan(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim());
            String key = token[0].trim();
            rm.BankServantMap.get("B").loan_HashMap.put(key, loan);
        }
        
        String elements3[] = banks[0].split("@");
        String customers3[] = elements[0].split("|");
        String loans3[] = elements[1].split("|");
            
        for (int i = 0; i<customers3.length; i++)
        {
            String token[] = customers3[i].split(",");
            Account customer = new Account(token[1].trim(), token[2].trim(), token[3].trim(), token[4].trim(), token[5].trim(),1000);    
            String key = Character.toString(token[1].trim().charAt(0));
            ArrayList<Account> customerList = rm.BankServantMap.get("C").account_HashMap.get(key);
            customerList.add(customer);
            rm.BankServantMap.get("C").account_HashMap.put(key, customerList);
        }     
        for (int i = 0; i<loans3.length; i++)
        {
            String token[] = loans3[i].split(",");
            Loan loan = new Loan(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim());
            String key = token[0].trim();
            rm.BankServantMap.get("C").loan_HashMap.put(key, loan);
        }
    }
}
