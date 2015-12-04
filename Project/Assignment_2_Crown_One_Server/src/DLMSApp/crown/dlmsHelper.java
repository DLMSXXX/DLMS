package DLMSApp.crown;

/** 
 * Helper class for : dlms
 *  
 * @author OpenORB Compiler
 */ 
public class dlmsHelper
{
    /**
     * Insert dlms into an any
     * @param a an any
     * @param t dlms value
     */
    public static void insert(org.omg.CORBA.Any a, DLMSApp.crown.dlms t)
    {
        a.insert_Object(t , type());
    }

    /**
     * Extract dlms from an any
     *
     * @param a an any
     * @return the extracted dlms value
     */
    public static DLMSApp.crown.dlms extract( org.omg.CORBA.Any a )
    {
        if ( !a.type().equivalent( type() ) )
        {
            throw new org.omg.CORBA.MARSHAL();
        }
        try
        {
            return DLMSApp.crown.dlmsHelper.narrow( a.extract_Object() );
        }
        catch ( final org.omg.CORBA.BAD_PARAM e )
        {
            throw new org.omg.CORBA.MARSHAL(e.getMessage());
        }
    }

    //
    // Internal TypeCode value
    //
    private static org.omg.CORBA.TypeCode _tc = null;

    /**
     * Return the dlms TypeCode
     * @return a TypeCode
     */
    public static org.omg.CORBA.TypeCode type()
    {
        if (_tc == null) {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            _tc = orb.create_interface_tc( id(), "dlms" );
        }
        return _tc;
    }

    /**
     * Return the dlms IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:DLMSApp/crown/dlms:1.0";

    /**
     * Read dlms from a marshalled stream
     * @param istream the input stream
     * @return the readed dlms value
     */
    public static DLMSApp.crown.dlms read(org.omg.CORBA.portable.InputStream istream)
    {
        return(DLMSApp.crown.dlms)istream.read_Object(DLMSApp.crown._dlmsStub.class);
    }

    /**
     * Write dlms into a marshalled stream
     * @param ostream the output stream
     * @param value dlms value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, DLMSApp.crown.dlms value)
    {
        ostream.write_Object((org.omg.CORBA.portable.ObjectImpl)value);
    }

    /**
     * Narrow CORBA::Object to dlms
     * @param obj the CORBA Object
     * @return dlms Object
     */
    public static dlms narrow(org.omg.CORBA.Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof dlms)
            return (dlms)obj;

        if (obj._is_a(id()))
        {
            _dlmsStub stub = new _dlmsStub();
            stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
            return stub;
        }

        throw new org.omg.CORBA.BAD_PARAM();
    }

    /**
     * Unchecked Narrow CORBA::Object to dlms
     * @param obj the CORBA Object
     * @return dlms Object
     */
    public static dlms unchecked_narrow(org.omg.CORBA.Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof dlms)
            return (dlms)obj;

        _dlmsStub stub = new _dlmsStub();
        stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
        return stub;

    }

}
