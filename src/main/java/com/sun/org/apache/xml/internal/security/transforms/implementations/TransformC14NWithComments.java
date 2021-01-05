
    protected XMLSignatureInput enginePerformTransform(
        XMLSignatureInput input, OutputStream os, Transform transformObject
    ) throws CanonicalizationException {

        Canonicalizer20010315WithComments c14n = new Canonicalizer20010315WithComments();
        if (os != null) {
            c14n.setWriter(os);
        }

        byte[] result = null;
        result = c14n.engineCanonicalize(input);
        XMLSignatureInput output = new XMLSignatureInput(result);
        if (os != null) {
            output.setOutputStream(os);
        }
        return output;
    }
}
