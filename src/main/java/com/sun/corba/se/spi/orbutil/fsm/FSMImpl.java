

package com.sun.corba.se.spi.orbutil.fsm ;

import java.util.Set ;
import java.util.HashSet ;

import com.sun.corba.se.spi.orbutil.fsm.Input ;
import com.sun.corba.se.spi.orbutil.fsm.StateEngine ;
import com.sun.corba.se.impl.orbutil.fsm.StateEngineImpl ;
import com.sun.corba.se.impl.orbutil.ORBUtility ;
import com.sun.corba.se.spi.orbutil.fsm.FSM ;


public class FSMImpl implements FSM
{
    private boolean debug ;
    private State state ;
    private StateEngineImpl stateEngine ;


    public FSMImpl( StateEngine se, State startState )
    {
        this( se, startState, false ) ;
    }

    public FSMImpl( StateEngine se, State startState, boolean debug )
    {
        state = startState ;
        stateEngine = (StateEngineImpl)se ;
        this.debug = debug ;
    }


    public State getState()
    {
        return state ;
    }


    public void doIt( Input in )
    {
        stateEngine.doIt( this, in, debug ) ;
    }

    public void internalSetState( State nextState )
    {
        if (debug) {
            ORBUtility.dprint( this, "Calling internalSetState with nextState = " +
                nextState ) ;
        }

        state = nextState ;

        if (debug) {
            ORBUtility.dprint( this, "Exiting internalSetState with state = " +
                state ) ;
        }
    }
}

