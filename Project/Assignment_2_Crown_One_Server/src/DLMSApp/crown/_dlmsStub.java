package DLMSApp.crown;

/**
 * Interface definition: dlms.
 * 
 * @author OpenORB Compiler
 */
public class _dlmsStub extends org.omg.CORBA.portable.ObjectImpl
        implements dlms
{
    static final String[] _ids_list =
    {
        "IDL:DLMSApp/crown/dlms:1.0"
    };

    public String[] _ids()
    {
     return _ids_list;
    }

    private final static Class _opsClass = DLMSApp.crown.dlmsOperations.class;

    /**
     * Operation openAccount
     */
    public String openAccount(String Bank, String fName, String lName, String email, String phoneNumber, String password)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("openAccount",true);
                    _output.write_string(Bank);
                    _output.write_string(fName);
                    _output.write_string(lName);
                    _output.write_string(email);
                    _output.write_string(phoneNumber);
                    _output.write_string(password);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("openAccount",_opsClass);
                if (_so == null)
                   continue;
                DLMSApp.crown.dlmsOperations _self = (DLMSApp.crown.dlmsOperations) _so.servant;
                try
                {
                    return _self.openAccount( Bank,  fName,  lName,  email,  phoneNumber,  password);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation getLoan
     */
    public String getLoan(String Bank, String accountNumber, String password, String loanAmount)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("getLoan",true);
                    _output.write_string(Bank);
                    _output.write_string(accountNumber);
                    _output.write_string(password);
                    _output.write_string(loanAmount);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("getLoan",_opsClass);
                if (_so == null)
                   continue;
                DLMSApp.crown.dlmsOperations _self = (DLMSApp.crown.dlmsOperations) _so.servant;
                try
                {
                    return _self.getLoan( Bank,  accountNumber,  password,  loanAmount);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation delayPayment
     */
    public String delayPayment(String Bank, String loanID, String currentD, String newD)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("delayPayment",true);
                    _output.write_string(Bank);
                    _output.write_string(loanID);
                    _output.write_string(currentD);
                    _output.write_string(newD);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("delayPayment",_opsClass);
                if (_so == null)
                   continue;
                DLMSApp.crown.dlmsOperations _self = (DLMSApp.crown.dlmsOperations) _so.servant;
                try
                {
                    return _self.delayPayment( Bank,  loanID,  currentD,  newD);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation printCustomerInfo
     */
    public String printCustomerInfo(String Bank)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("printCustomerInfo",true);
                    _output.write_string(Bank);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("printCustomerInfo",_opsClass);
                if (_so == null)
                   continue;
                DLMSApp.crown.dlmsOperations _self = (DLMSApp.crown.dlmsOperations) _so.servant;
                try
                {
                    return _self.printCustomerInfo( Bank);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation transferLoan
     */
    public String transferLoan(String loanID, String currentBank, String otherBank)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("transferLoan",true);
                    _output.write_string(loanID);
                    _output.write_string(currentBank);
                    _output.write_string(otherBank);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("transferLoan",_opsClass);
                if (_so == null)
                   continue;
                DLMSApp.crown.dlmsOperations _self = (DLMSApp.crown.dlmsOperations) _so.servant;
                try
                {
                    return _self.transferLoan( loanID,  currentBank,  otherBank);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

}
