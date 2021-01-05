

package javax.sound.midi.spi;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.Sequence;
import javax.sound.midi.InvalidMidiDataException;


public abstract class MidiFileReader {


    public abstract MidiFileFormat getMidiFileFormat(InputStream stream)
            throws InvalidMidiDataException, IOException;


    public abstract MidiFileFormat getMidiFileFormat(URL url)
            throws InvalidMidiDataException, IOException;


    public abstract MidiFileFormat getMidiFileFormat(File file)
            throws InvalidMidiDataException, IOException;


    public abstract Sequence getSequence(InputStream stream)
            throws InvalidMidiDataException, IOException;


    public abstract Sequence getSequence(URL url)
            throws InvalidMidiDataException, IOException;


    public abstract Sequence getSequence(File file)
            throws InvalidMidiDataException, IOException;
}
