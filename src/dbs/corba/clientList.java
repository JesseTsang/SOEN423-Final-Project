package dbs.corba;


/**
* dbs/corba/clientList.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from dbs.idl
* Sunday, December 3, 2017 12:34:16 PM EST
*/

public final class clientList implements org.omg.CORBA.portable.IDLEntity
{
  public String branchID = null;
  public dbs.corba.client allClients[] = null;

  public clientList ()
  {
  } // ctor

  public clientList (String _branchID, dbs.corba.client[] _allClients)
  {
    branchID = _branchID;
    allClients = _allClients;
  } // ctor

} // class clientList
