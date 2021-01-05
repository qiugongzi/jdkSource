

package javax.sound.sampled;


public interface Line extends AutoCloseable {


    public Line.Info getLineInfo();


    public void open() throws LineUnavailableException;



    public void close();




    public boolean isOpen();



    public Control[] getControls();


    public boolean isControlSupported(Control.Type control);



    public Control getControl(Control.Type control);



    public void addLineListener(LineListener listener);



    public void removeLineListener(LineListener listener);



    public static class Info {


        private final Class lineClass;



        public Info(Class<?> lineClass) {

            if (lineClass == null) {
                this.lineClass = Line.class;
            } else {
                this.lineClass = lineClass;
            }
        }




        public Class<?> getLineClass() {
            return lineClass;
        }



        public boolean matches(Info info) {

            if (! (this.getClass().isInstance(info)) ) {
                return false;
            }


            if (! (getLineClass().isAssignableFrom(info.getLineClass())) ) {
                return false;
            }

            return true;
        }



        public String toString() {

            String fullPackagePath = "javax.sound.sampled.";
            String initialString = new String(getLineClass().toString());
            String finalString;

            int index = initialString.indexOf(fullPackagePath);

            if (index != -1) {
                finalString = initialString.substring(0, index) + initialString.substring( (index + fullPackagePath.length()), initialString.length() );
            } else {
                finalString = initialString;
            }

            return finalString;
        }

    } }