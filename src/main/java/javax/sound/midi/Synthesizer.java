

package javax.sound.midi;

import javax.sound.sampled.Control;



public interface Synthesizer extends MidiDevice {


    public int getMaxPolyphony();



    public long getLatency();



    public MidiChannel[] getChannels();



    public VoiceStatus[] getVoiceStatus();



    public boolean isSoundbankSupported(Soundbank soundbank);



    public boolean loadInstrument(Instrument instrument);



    public void unloadInstrument(Instrument instrument);



    public boolean remapInstrument(Instrument from, Instrument to);



    public Soundbank getDefaultSoundbank();



    public Instrument[] getAvailableInstruments();



    public Instrument[] getLoadedInstruments();



    public boolean loadAllInstruments(Soundbank soundbank);




    public void unloadAllInstruments(Soundbank soundbank);



    public boolean loadInstruments(Soundbank soundbank, Patch[] patchList);


    public void unloadInstruments(Soundbank soundbank, Patch[] patchList);


    }
