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

/**
 *
 * @author ni_ye
 */
public class loadHash2 {
    
    
     public String hashToString2(BankServant bank)
    {
        String result = null;
        for (ArrayList<Account> account_list : bank.account_HashMap.values())
        {
            for (Account account : account_list) {
                result += account.accountNumber + "," + account.firstName + "," + account.lastName + "," + account.emailAddress + "," + account.phoneNumber
                        + "," + account.password + "|";
            }         
        }
        result += "@";
        for (Loan temp : bank.loan_HashMap.values()) {
            result += temp.ID + "," + temp.accountNumber + "," + temp.amount + "," + temp.dueDate + "|";
        }
        //System.out.println(result);
        return result;
    }
     
    public void stringToHash2(String s, BankServant bank)
    {
        String elements[] = s.split("@");
        String customers[] = elements[0].split("|");
        String loans[] = elements[1].split("|");
            
        for (int i = 0; i<customers.length; i++)
        {
            String token[] = customers[i].split(",");
            Account customer = new Account(token[1].trim(), token[2].trim(), token[3].trim(), token[4].trim(), token[5].trim(),1000);    
            String key = Character.toString(token[1].trim().charAt(0));
            ArrayList<Account> customerList = bank.account_HashMap.get(key);
            customerList.add(customer);
            bank.account_HashMap.put(key, customerList);
        }
        
        for (int i = 0; i<loans.length; i++)
        {
            String token[] = loans[i].split(",");
            Loan loan = new Loan(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim());
            String key = token[0].trim();
            bank.loan_HashMap.put(key, loan);
        }
    }
}
