package DLMS;

/**
* DLMS/dlmsHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DLMS.idl
* Friday, December 11, 2015 7:31:21 PM EST
*/

public final class dlmsHolder implements org.omg.CORBA.portable.Streamable
{
  public DLMS.dlms value = null;

  public dlmsHolder ()
  {
  }

  public dlmsHolder (DLMS.dlms initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = DLMS.dlmsHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    DLMS.dlmsHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return DLMS.dlmsHelper.type ();
  }

}
