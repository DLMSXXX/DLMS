package DLMS;


/**
* DLMS/dlmsHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DLMS.idl
* Saturday, December 12, 2015 7:13:17 PM EST
*/

abstract public class dlmsHelper
{
  private static String  _id = "IDL:DLMS/dlms:1.0";

  public static void insert (org.omg.CORBA.Any a, DLMS.dlms that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static DLMS.dlms extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (DLMS.dlmsHelper.id (), "dlms");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static DLMS.dlms read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_dlmsStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, DLMS.dlms value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static DLMS.dlms narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof DLMS.dlms)
      return (DLMS.dlms)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      DLMS._dlmsStub stub = new DLMS._dlmsStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static DLMS.dlms unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof DLMS.dlms)
      return (DLMS.dlms)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      DLMS._dlmsStub stub = new DLMS._dlmsStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}