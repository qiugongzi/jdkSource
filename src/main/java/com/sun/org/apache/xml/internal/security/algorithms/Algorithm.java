
    protected void setAlgorithmURI(String algorithmURI) {
        if (algorithmURI != null) {
            this.constructionElement.setAttributeNS(
                null, Constants._ATT_ALGORITHM, algorithmURI
            );
        }
    }
}
