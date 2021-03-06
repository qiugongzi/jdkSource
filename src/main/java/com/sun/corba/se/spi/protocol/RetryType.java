

package com.sun.corba.se.spi.protocol ;










public enum RetryType {
    NONE( false ),
    BEFORE_RESPONSE( true ),
    AFTER_RESPONSE( true ) ;

    private final boolean isRetry ;

    RetryType( boolean isRetry ) {
        this.isRetry = isRetry ;
    }

    public boolean isRetry() {
        return this.isRetry ;
    }
} ;

