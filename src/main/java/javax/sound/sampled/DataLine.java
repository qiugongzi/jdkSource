

package javax.sound.sampled;

import java.util.Arrays;


public interface DataLine extends Line {



    public void drain();


    public void flush();


    public void start();


    public void stop();


    public boolean isRunning();


    public boolean isActive();


    public AudioFormat getFormat();


    public int getBufferSize();


    public int available();


    public int getFramePosition();



    public long getLongFramePosition();



    public long getMicrosecondPosition();


    public float getLevel();


    public static class Info extends Line.Info {

        private final AudioFormat[] formats;
        private final int minBufferSize;
        private final int maxBufferSize;


        public Info(Class<?> lineClass, AudioFormat[] formats, int minBufferSize, int maxBufferSize) {

            super(lineClass);

            if (formats == null) {
                this.formats = new AudioFormat[0];
            } else {
                this.formats = Arrays.copyOf(formats, formats.length);
            }

            this.minBufferSize = minBufferSize;
            this.maxBufferSize = maxBufferSize;
        }



        public Info(Class<?> lineClass, AudioFormat format, int bufferSize) {

            super(lineClass);

            if (format == null) {
                this.formats = new AudioFormat[0];
            } else {
                this.formats = new AudioFormat[]{format};
            }

            this.minBufferSize = bufferSize;
            this.maxBufferSize = bufferSize;
        }



        public Info(Class<?> lineClass, AudioFormat format) {
            this(lineClass, format, AudioSystem.NOT_SPECIFIED);
        }



        public AudioFormat[] getFormats() {
            return Arrays.copyOf(formats, formats.length);
        }


        public boolean isFormatSupported(AudioFormat format) {

            for (int i = 0; i < formats.length; i++) {
                if (format.matches(formats[i])) {
                    return true;
                }
            }

            return false;
        }


        public int getMinBufferSize() {
            return minBufferSize;
        }



        public int getMaxBufferSize() {
            return maxBufferSize;
        }



        public boolean matches(Line.Info info) {

            if (! (super.matches(info)) ) {
                return false;
            }

            Info dataLineInfo = (Info)info;

            if ((getMaxBufferSize() >= 0) && (dataLineInfo.getMaxBufferSize() >= 0)) {
                if (getMaxBufferSize() > dataLineInfo.getMaxBufferSize()) {
                    return false;
                }
            }

            if ((getMinBufferSize() >= 0) && (dataLineInfo.getMinBufferSize() >= 0)) {
                if (getMinBufferSize() < dataLineInfo.getMinBufferSize()) {
                    return false;
                }
            }

            AudioFormat[] localFormats = getFormats();

            if (localFormats != null) {

                for (int i = 0; i < localFormats.length; i++) {
                    if (! (localFormats[i] == null) ) {
                        if (! (dataLineInfo.isFormatSupported(localFormats[i])) ) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }


        public String toString() {

            StringBuffer buf = new StringBuffer();

            if ( (formats.length == 1) && (formats[0] != null) ) {
                buf.append(" supporting format " + formats[0]);
            } else if (getFormats().length > 1) {
                buf.append(" supporting " + getFormats().length + " audio formats");
            }

            if ( (minBufferSize != AudioSystem.NOT_SPECIFIED) && (maxBufferSize != AudioSystem.NOT_SPECIFIED) ) {
                buf.append(", and buffers of " + minBufferSize + " to " + maxBufferSize + " bytes");
            } else if ( (minBufferSize != AudioSystem.NOT_SPECIFIED) && (minBufferSize > 0) ) {
                buf.append(", and buffers of at least " + minBufferSize + " bytes");
            } else if (maxBufferSize != AudioSystem.NOT_SPECIFIED) {
                buf.append(", and buffers of up to " + minBufferSize + " bytes");
            }

            return new String(super.toString() + buf);
        }
    } }