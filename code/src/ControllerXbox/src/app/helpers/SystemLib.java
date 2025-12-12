package app.helpers;

/**
 * Classe de méthodes statiques pour gérer
 * des informations "système".
 *
 * @author Jean-Claude Stritt
 */
public class SystemLib {

  public static String getCurrentMethod() {
    StackTraceElement e[] = Thread.currentThread().getStackTrace();
    StackTraceElement trace = e[2];
    return trace.getMethodName();
  }

  public static String getTestCurrentMethod() {
    return "*** " + getCurrentMethod() + "...";
  }
  
   public static String getFullMethodName(){
    StackTraceElement e[] = Thread.currentThread().getStackTrace();
    StackTraceElement trace = e[2];
    return trace.getClassName()+"."+trace.getMethodName()+"\n";
  }


}
