package compilations;

/**
* compilations/WordMaskInfoHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from GameService.idl
* Saturday, May 24, 2025 4:10:05 PM PST
*/

public final class WordMaskInfoHolder implements org.omg.CORBA.portable.Streamable
{
  public WordMaskInfo value = null;

  public WordMaskInfoHolder ()
  {
  }

  public WordMaskInfoHolder (WordMaskInfo initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = WordMaskInfoHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    WordMaskInfoHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return WordMaskInfoHelper.type ();
  }

}
