package dbs.corba;


/**
* dbs/corba/clientHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from dbs.idl
* Friday, December 1, 2017 10:26:46 PM EST
*/

abstract public class clientHelper
{
  private static String  _id = "IDL:dbs/corba/client:1.0";

  public static void insert (org.omg.CORBA.Any a, dbs.corba.client that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static dbs.corba.client extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  private static boolean __active = false;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      synchronized (org.omg.CORBA.TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active)
          {
            return org.omg.CORBA.ORB.init().create_recursive_tc ( _id );
          }
          __active = true;
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [5];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "firstName",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[1] = new org.omg.CORBA.StructMember (
            "lastName",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[2] = new org.omg.CORBA.StructMember (
            "address",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[3] = new org.omg.CORBA.StructMember (
            "phoneNum",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[4] = new org.omg.CORBA.StructMember (
            "clientID",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (dbs.corba.clientHelper.id (), "client", _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static dbs.corba.client read (org.omg.CORBA.portable.InputStream istream)
  {
    dbs.corba.client value = new dbs.corba.client ();
    value.firstName = istream.read_string ();
    value.lastName = istream.read_string ();
    value.address = istream.read_string ();
    value.phoneNum = istream.read_string ();
    value.clientID = istream.read_string ();
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, dbs.corba.client value)
  {
    ostream.write_string (value.firstName);
    ostream.write_string (value.lastName);
    ostream.write_string (value.address);
    ostream.write_string (value.phoneNum);
    ostream.write_string (value.clientID);
  }

}
