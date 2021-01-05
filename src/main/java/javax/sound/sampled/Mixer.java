

package javax.sound.sampled;



public interface Mixer extends Line {


    public Info getMixerInfo();



    public Line.Info[] getSourceLineInfo();


    public Line.Info[] getTargetLineInfo();



    public Line.Info[] getSourceLineInfo(Line.Info info);



    public Line.Info[] getTargetLineInfo(Line.Info info);



    public boolean isLineSupported(Line.Info info);


    public Line getLine(Line.Info info) throws LineUnavailableException;

    public int getMaxLines(Line.Info info);



    public Line[] getSourceLines();


    public Line[] getTargetLines();


    public void synchronize(Line[] lines, boolean maintainSync);


    public void unsynchronize(Line[] lines);



    public boolean isSynchronizationSupported(Line[] lines, boolean maintainSync);



    public static class Info {


        private final String name;


        private final String vendor;


        private final String description;


        private final String version;


        protected Info(String name, String vendor, String description, String version) {

            this.name = name;
            this.vendor = vendor;
            this.description = description;
            this.version = version;
        }



        public final boolean equals(Object obj) {
            return super.equals(obj);
        }


        public final int hashCode() {
            return super.hashCode();
        }


        public final String getName() {
            return name;
        }


        public final String getVendor() {
            return vendor;
        }


        public final String getDescription() {
            return description;
        }


        public final String getVersion() {
            return version;
        }


        public final String toString() {
            return (name + ", version " + version);
        }
    } }
