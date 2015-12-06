/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servant;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import model2.Loan;
import model2.Customer;

/**
 *
 * @author ni_ye
 */
public class loadHash {
    
    /*public String hashToString(dlmsImpl bank)
    {
        String result = null;
        for (String key : bank.accounts.keySet()) {
            if (bank.accounts.get(key) != null) {
                List<Customer> listCustomer = bank.accounts.get(key);
                for (int i = 0; i < listCustomer.size(); i++) {
                    result += listCustomer.get(i).getAccountNumber() + "," + listCustomer.get(i).getFirstName() + "," + listCustomer.get(i).getLastName() + ","
                            + listCustomer.get(i).getEmail() + "," + listCustomer.get(i).getPhoneNumber() + "," + listCustomer.get(i).getPassword() + "|";
                }
            }
        }
        result += "@";
        for (Integer key : bank.loans.keySet()) {
            if (bank.loans.get(key) != null) {              
                result += bank.loans.get(key).getLoanId() + "," + bank.loans.get(key).getAccountNumber() + "," + bank.loans.get(key).getLoanAmt() 
                        + bank.loans.get(key).getDueDate() + "|";  
            }
        }   
        //System.out.println(result);
        return result;    
    }
    
   
    
    public void stringToHash(String s, dlmsImpl bank)
    {
        String elements[] = s.split("@");
        String customers[] = elements[0].split("|");
        String loans[] = elements[1].split("|");
            
        for (int i = 0; i<customers.length; i++)
        {
            String token[] = customers[i].split(",");
            Customer customer = new Customer(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim(), token[4].trim(), token[5].trim());    
            Character key = token[1].trim().charAt(0);
            List<Customer> customerList = bank.accounts.get(key);
            customerList.add(customer);
            bank.accounts.put(key, customerList);
        }
        
        for (int i = 0; i<loans.length; i++)
        {
            String token[] = loans[i].split(",");
            Loan loan = new Loan(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim());
            Integer key = Integer.parseInt(token[0].trim());
            bank.loans.put(key, loan);
        }
    }*/
}

