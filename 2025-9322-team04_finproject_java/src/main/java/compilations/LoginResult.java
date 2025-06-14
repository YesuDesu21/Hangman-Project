package compilations;


/**
* compilations/LoginResult.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from LoginService.idl
* Saturday, May 24, 2025 4:10:23 PM PST
*/

public final class LoginResult implements org.omg.CORBA.portable.IDLEntity
{
  public boolean success = false;
  public boolean forcedLogout = false;
  public String sessionId = null;

  public LoginResult ()
  {
  } // ctor

  public LoginResult (boolean _success, boolean _forcedLogout, String _sessionId)
  {
    success = _success;
    forcedLogout = _forcedLogout;
    sessionId = _sessionId;
  } // ctor

} // class LoginResult
