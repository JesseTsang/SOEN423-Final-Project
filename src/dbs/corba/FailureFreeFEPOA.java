package dbs.corba;


/**
* dbs/corba/FailureFreeFEPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from dbs.idl
* Friday, December 1, 2017 10:26:46 PM EST
*/

public abstract class FailureFreeFEPOA extends org.omg.PortableServer.Servant
 implements dbs.corba.FailureFreeFEOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("sayHello", new java.lang.Integer (0));
    _methods.put ("deposit", new java.lang.Integer (1));
    _methods.put ("withdraw", new java.lang.Integer (2));
    _methods.put ("balance", new java.lang.Integer (3));
    _methods.put ("transfer", new java.lang.Integer (4));
    _methods.put ("requestResponse", new java.lang.Integer (5));
    _methods.put ("shutdown", new java.lang.Integer (6));
    _methods.put ("createAccount", new java.lang.Integer (7));
    _methods.put ("setByzantineFlag", new java.lang.Integer (8));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // dbs/corba/FailureFreeFE/sayHello
       {
         String $result = null;
         $result = this.sayHello ();
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 1:  // dbs/corba/FailureFreeFE/deposit
       {
         long accountNum = in.read_longlong ();
         float amount = in.read_float ();
         double $result = (double)0;
         $result = this.deposit (accountNum, amount);
         out = $rh.createReply();
         out.write_double ($result);
         break;
       }

       case 2:  // dbs/corba/FailureFreeFE/withdraw
       {
         long accountNum = in.read_longlong ();
         float amount = in.read_float ();
         double $result = (double)0;
         $result = this.withdraw (accountNum, amount);
         out = $rh.createReply();
         out.write_double ($result);
         break;
       }

       case 3:  // dbs/corba/FailureFreeFE/balance
       {
         long accountNum = in.read_longlong ();
         double $result = (double)0;
         $result = this.balance (accountNum);
         out = $rh.createReply();
         out.write_double ($result);
         break;
       }

       case 4:  // dbs/corba/FailureFreeFE/transfer
       {
         long src_accountNum = in.read_longlong ();
         long dest_accountNum = in.read_longlong ();
         float amount = in.read_float ();
         double $result = (double)0;
         $result = this.transfer (src_accountNum, dest_accountNum, amount);
         out = $rh.createReply();
         out.write_double ($result);
         break;
       }

       case 5:  // dbs/corba/FailureFreeFE/requestResponse
       {
         dbs.corba.CallBack cb = dbs.corba.CallBackHelper.read (in);
         String $result = null;
         $result = this.requestResponse (cb);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 6:  // dbs/corba/FailureFreeFE/shutdown
       {
         this.shutdown ();
         out = $rh.createReply();
         break;
       }

       case 7:  // dbs/corba/FailureFreeFE/createAccount
       {
         String firstName = in.read_string ();
         String lastName = in.read_string ();
         String address = in.read_string ();
         String phone = in.read_string ();
         String customerID = in.read_string ();
         String branchID = in.read_string ();
         boolean $result = false;
         $result = this.createAccount (firstName, lastName, address, phone, customerID, branchID);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }


  //True if we want to set a replic to artificially produce a wrong result.
       case 8:  // dbs/corba/FailureFreeFE/setByzantineFlag
       {
         boolean flag = in.read_boolean ();
         this.setByzantineFlag (flag);
         out = $rh.createReply();
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:dbs/corba/FailureFreeFE:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public FailureFreeFE _this() 
  {
    return FailureFreeFEHelper.narrow(
    super._this_object());
  }

  public FailureFreeFE _this(org.omg.CORBA.ORB orb) 
  {
    return FailureFreeFEHelper.narrow(
    super._this_object(orb));
  }


} // class FailureFreeFEPOA
