
    protected XMLSignatureInput enginePerformTransform(
        XMLSignatureInput input, OutputStream os, Transform transformObject
    ) throws TransformationException {

        Object exArgs[] = { implementedTransformURI };

        throw new TransformationException("signature.Transform.NotYetImplemented", exArgs);
    }
}
