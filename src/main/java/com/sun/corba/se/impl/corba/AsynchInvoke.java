

    public void run()
    {

        _req.doInvocation();



        synchronized (_req)
            {

                _req.gotResponse = true;


                _req.notify();
            }

        if (_notifyORB == true) {
            _orb.notifyORB() ;
        }
    }

};


