

package javax.xml.bind.util;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import java.util.ArrayList;
import java.util.List;


public class ValidationEventCollector implements ValidationEventHandler
{
    private final List<ValidationEvent> events = new ArrayList<ValidationEvent>();


    public ValidationEvent[] getEvents() {
        return events.toArray(new ValidationEvent[events.size()]);
    }


    public void reset() {
        events.clear();
    }


    public boolean hasEvents() {
        return !events.isEmpty();
    }

    public boolean handleEvent( ValidationEvent event ) {
        events.add(event);

        boolean retVal = true;
        switch( event.getSeverity() ) {
            case ValidationEvent.WARNING:
                retVal = true; break;
            case ValidationEvent.ERROR:
                retVal = true; break;
            case ValidationEvent.FATAL_ERROR:
                retVal = false; break;
            default:
                _assert( false,
                         Messages.format( Messages.UNRECOGNIZED_SEVERITY,
                                 event.getSeverity() ) );
                break;
        }

        return retVal;
    }

    private static void _assert( boolean b, String msg ) {
        if( !b ) {
            throw new InternalError( msg );
        }
    }
}
