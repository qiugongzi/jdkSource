

package javax.sound.midi;




public interface MidiChannel {


    public void noteOn(int noteNumber, int velocity);


    public void noteOff(int noteNumber, int velocity);


    public void noteOff(int noteNumber);


    public void setPolyPressure(int noteNumber, int pressure);


    public int getPolyPressure(int noteNumber);


    public void setChannelPressure(int pressure);


    public int getChannelPressure();


    public void controlChange(int controller, int value);


    public int getController(int controller);


    public void programChange(int program);


    public void programChange(int bank, int program);


    public int getProgram();


    public void setPitchBend(int bend);


    public int getPitchBend();


    public void resetAllControllers();


    public void allNotesOff();


    public void allSoundOff();


    public boolean localControl(boolean on);


    public void setMono(boolean on);


    public boolean getMono();


    public void setOmni(boolean on);


    public boolean getOmni();


    public void setMute(boolean mute);


    public boolean getMute();


    public void setSolo(boolean soloState);


    public boolean getSolo();
}
