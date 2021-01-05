

package javax.print;



public interface CancelablePrintJob extends DocPrintJob {


    public void cancel() throws PrintException;

}
