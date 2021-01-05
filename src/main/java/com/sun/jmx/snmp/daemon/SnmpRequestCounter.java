
        public synchronized int getNewId() {
                if (++reqid  < 0)
                        reqid = 1 ;
                return reqid ;
        }
}
