

package javax.management.loading;

import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import java.util.Set;
import java.util.Enumeration;

import javax.management.*;




public interface MLetMBean   {



    public Set<Object> getMBeansFromURL(String url)
            throws ServiceNotFoundException;


    public Set<Object> getMBeansFromURL(URL url)
            throws ServiceNotFoundException;


    public void addURL(URL url) ;


    public void addURL(String url) throws ServiceNotFoundException;


    public URL[] getURLs();


    public URL getResource(String name);


    public InputStream getResourceAsStream(String name);


    public Enumeration<URL> getResources(String name) throws IOException;


    public String getLibraryDirectory();


    public void setLibraryDirectory(String libdir);

 }
