
    public static Object getValue(Object jaxbElement) {
        if (jaxbElement instanceof JAXBElement) {
            return ((JAXBElement)jaxbElement).getValue();
        } else {


            return jaxbElement;
        }
    }
}
