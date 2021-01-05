

package com.sun.corba.se.spi.orbutil.fsm;


public interface StateEngine
{

        public StateEngine add( State oldState, Input input, Guard guard,
            Action action, State newState ) throws IllegalStateException ;


        public StateEngine add( State oldState, Input input,
            Action action, State newState ) throws IllegalStateException ;


        public StateEngine setDefault( State oldState, Action action, State newState )
                throws IllegalStateException ;


        public StateEngine setDefault( State oldState, State newState )
                throws IllegalStateException ;


        public StateEngine setDefault( State oldState )
                throws IllegalStateException ;


        public void setDefaultAction( Action act ) throws IllegalStateException ;


        public void done() throws IllegalStateException ;


        public FSM makeFSM( State startState ) throws IllegalStateException ;
}

