package compilations;


import compilations.WordServicePackage.GameNotFoundException;
import compilations.WordServicePackage.GameNotFoundExceptionHelper;

/**
* compilations/_WordServiceStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from WordService.idl
* Saturday, May 24, 2025 4:10:34 PM PST
*/

public class _WordServiceStub extends org.omg.CORBA.portable.ObjectImpl implements WordService
{

  public String getNewWord (String gameId)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getNewWord", true);
                $out.write_string (gameId);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getNewWord (gameId        );
            } finally {
                _releaseReply ($in);
            }
  } // getNewWord

  public void markWordAsUsed (String word, String gameId)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("markWordAsUsed", true);
                $out.write_string (word);
                $out.write_string (gameId);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                markWordAsUsed (word, gameId        );
            } finally {
                _releaseReply ($in);
            }
  } // markWordAsUsed

  public String getCurrentWord (String gameId)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getCurrentWord", true);
                $out.write_string (gameId);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getCurrentWord (gameId        );
            } finally {
                _releaseReply ($in);
            }
  } // getCurrentWord

  public void clearCurrentWord (String gameId)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("clearCurrentWord", true);
                $out.write_string (gameId);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                clearCurrentWord (gameId        );
            } finally {
                _releaseReply ($in);
            }
  } // clearCurrentWord


  //NEW METHOD BELOW-JAS
  public String getRoundWord (String gameid, String roundid) throws GameNotFoundException
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getRoundWord", true);
                $out.write_string (gameid);
                $out.write_string (roundid);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:compilations/WordService/GameNotFoundException:1.0"))
                    throw GameNotFoundExceptionHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getRoundWord (gameid, roundid        );
            } finally {
                _releaseReply ($in);
            }
  } // getRoundWord

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:compilations/WordService:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     com.sun.corba.se.impl.orbutil.IORCheckImpl.check(str, "compilations._WordServiceStub");
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     org.omg.CORBA.Object obj = orb.string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
   } finally {
     orb.destroy() ;
   }
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     String str = orb.object_to_string (this);
     s.writeUTF (str);
   } finally {
     orb.destroy() ;
   }
  }
} // class _WordServiceStub
