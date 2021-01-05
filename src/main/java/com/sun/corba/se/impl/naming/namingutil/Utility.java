
    static void validateGIOPVersion( IIOPEndpointInfo endpointInfo ) {
        if ((endpointInfo.getMajor() > NamingConstants.MAJORNUMBER_SUPPORTED) ||
            (endpointInfo.getMinor() > NamingConstants.MINORNUMBERMAX ) )
        {
            throw wrapper.insBadAddress() ;
        }
    }
}
