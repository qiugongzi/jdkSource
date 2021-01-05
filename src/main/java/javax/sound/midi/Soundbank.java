

package javax.sound.midi;

import java.net.URL;




public interface Soundbank {



    public String getName();


    public String getVersion();


    public String getVendor();


    public String getDescription();



    public SoundbankResource[] getResources();



    public Instrument[] getInstruments();


    public Instrument getInstrument(Patch patch);


}
