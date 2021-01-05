
    public Map parse( Properties props )
    {
        Map map = new HashMap() ;
        Iterator iter = actions.iterator() ;
        while (iter.hasNext()) {
            ParserAction act = (ParserAction)(iter.next()) ;

            Object result = act.apply( props ) ;



            if (result != null)
                map.put( act.getFieldName(), result ) ;
        }

        return map ;
    }

    public Iterator iterator()
    {
        return actions.iterator() ;
    }
}
