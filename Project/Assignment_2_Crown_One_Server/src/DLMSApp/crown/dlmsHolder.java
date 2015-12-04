package DLMSApp.crown;

/**
 * Holder class for : dlms
 * 
 * @author OpenORB Compiler
 */
final public class dlmsHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal dlms value
     */
    public DLMSApp.crown.dlms value;

    /**
     * Default constructor
     */
    public dlmsHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public dlmsHolder(DLMSApp.crown.dlms initial)
    {
        value = initial;
    }

    /**
     * Read dlms from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = dlmsHelper.read(istream);
    }

    /**
     * Write dlms into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        dlmsHelper.write(ostream,value);
    }

    /**
     * Return the dlms TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return dlmsHelper.type();
    }

}
