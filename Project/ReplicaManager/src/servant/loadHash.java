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
import rm.ReplicaManager2;

/**
 *
 * @author ni_ye
 */
public class loadHash {

    public String hashToString(ReplicaManager2 rm) {
        String result = null;

        for (String key : rm.BankServantMap.get("A").accounts.keySet()) {
            if (rm.BankServantMap.get("A").accounts.get(key) != null) {
                List<Customer> listCustomer = rm.BankServantMap.get("A").accounts.get(key);
                for (int i = 0; i < listCustomer.size(); i++) {
                    result += listCustomer.get(i).getAccountNumber() + "," + listCustomer.get(i).getFirstName() + "," + listCustomer.get(i).getLastName() + ","
                            + listCustomer.get(i).getEmail() + "," + listCustomer.get(i).getPhoneNumber() + "," + listCustomer.get(i).getPassword() + "|";
                }
            }
        }
        result += "@";
        for (Integer key : rm.BankServantMap.get("A").loans.keySet()) {
            if (rm.BankServantMap.get("A").loans.get(key) != null) {
                result += rm.BankServantMap.get("A").loans.get(key).getLoanId() + "," + rm.BankServantMap.get("A").loans.get(key).getAccountNumber() + "," + rm.BankServantMap.get("A").loans.get(key).getLoanAmt()
                        + rm.BankServantMap.get("A").loans.get(key).getDueDate() + "|";
            }
        }
        
        result += "!";
        
        for (String key : rm.BankServantMap.get("B").accounts.keySet()) {
            if (rm.BankServantMap.get("B").accounts.get(key) != null) {
                List<Customer> listCustomer = rm.BankServantMap.get("B").accounts.get(key);
                for (int i = 0; i < listCustomer.size(); i++) {
                    result += listCustomer.get(i).getAccountNumber() + "," + listCustomer.get(i).getFirstName() + "," + listCustomer.get(i).getLastName() + ","
                            + listCustomer.get(i).getEmail() + "," + listCustomer.get(i).getPhoneNumber() + "," + listCustomer.get(i).getPassword() + "|";
                }
            }
        }
        result += "@";
        for (Integer key : rm.BankServantMap.get("B").loans.keySet()) {
            if (rm.BankServantMap.get("B").loans.get(key) != null) {
                result += rm.BankServantMap.get("B").loans.get(key).getLoanId() + "," + rm.BankServantMap.get("B").loans.get(key).getAccountNumber() + "," + rm.BankServantMap.get("B").loans.get(key).getLoanAmt()
                        + rm.BankServantMap.get("B").loans.get(key).getDueDate() + "|";
            }
        }
        
        result += "!";
        
        for (String key : rm.BankServantMap.get("C").accounts.keySet()) {
            if (rm.BankServantMap.get("C").accounts.get(key) != null) {
                List<Customer> listCustomer = rm.BankServantMap.get("C").accounts.get(key);
                for (int i = 0; i < listCustomer.size(); i++) {
                    result += listCustomer.get(i).getAccountNumber() + "," + listCustomer.get(i).getFirstName() + "," + listCustomer.get(i).getLastName() + ","
                            + listCustomer.get(i).getEmail() + "," + listCustomer.get(i).getPhoneNumber() + "," + listCustomer.get(i).getPassword() + "|";
                }
            }
        }
        result += "@";
        for (Integer key : rm.BankServantMap.get("C").loans.keySet()) {
            if (rm.BankServantMap.get("C").loans.get(key) != null) {
                result += rm.BankServantMap.get("C").loans.get(key).getLoanId() + "," + rm.BankServantMap.get("C").loans.get(key).getAccountNumber() + "," + rm.BankServantMap.get("C").loans.get(key).getLoanAmt()
                        + rm.BankServantMap.get("C").loans.get(key).getDueDate() + "|";
            }
        }
        
        //System.out.println(result);
        return result;
    }
    
   
    
    public void stringToHash(String s, dlmsImpl bank)
    {
        String banks[] = s.split("!");
        String elements[] = banks[0].split("@");
        String customers[] = elements[0].split("|");
        String loans[] = elements[1].split("|");
            
        for (int i = 0; i<customers.length; i++)
        {
            String token[] = customers[i].split(",");
            Customer customer = new Customer(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim(), token[4].trim(), token[5].trim());    
            String key = token[2].trim().substring(0, 1);
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

        String elements2[] = banks[1].split("@");
        String customers2[] = elements2[0].split("|");
        String loans2[] = elements2[1].split("|");
        
        for (int i = 0; i<customers2.length; i++)
        {
            String token[] = customers2[i].split(",");
            Customer customer = new Customer(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim(), token[4].trim(), token[5].trim());    
            String key = token[2].trim().substring(0, 1);
            List<Customer> customerList = bank.accounts.get(key);
            customerList.add(customer);
            bank.accounts.put(key, customerList);
        }    
        for (int i = 0; i<loans2.length; i++)
        {
            String token[] = loans2[i].split(",");
            Loan loan = new Loan(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim());
            Integer key = Integer.parseInt(token[0].trim());
            bank.loans.put(key, loan);
        }
        
        String elements3[] = banks[1].split("@");
        String customers3[] = elements3[0].split("|");
        String loans3[] = elements3[1].split("|");
        
        for (int i = 0; i<customers3.length; i++)
        {
            String token[] = customers3[i].split(",");
            Customer customer = new Customer(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim(), token[4].trim(), token[5].trim());    
            String key = token[2].trim().substring(0, 1);
            List<Customer> customerList = bank.accounts.get(key);
            customerList.add(customer);
            bank.accounts.put(key, customerList);
        }    
        for (int i = 0; i<loans3.length; i++)
        {
            String token[] = loans3[i].split(",");
            Loan loan = new Loan(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim());
            Integer key = Integer.parseInt(token[0].trim());
            bank.loans.put(key, loan);
        }
    }
}

