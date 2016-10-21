package edu.georgetown.library.asExport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ASProperties  {
    private Properties prop = new Properties();
    public static final String P_SERVICE  = "service";
    public static final String P_USER     = "user";
    public static final String P_PASSWORD = "password";
    public static final String P_REPOS    = "repositories";
    
    public Properties getProperties() {
        return prop;
    }
    
    public ASProperties(File propFile) throws FileNotFoundException, IOException {
        prop.load(new FileReader(propFile));
    }
    
    public String getService() throws DataException {
        String s = prop.getProperty(P_SERVICE, "");
        if (s.isEmpty()) throw new DataException("Service must be set in the property file");
        return s;
    }

    public String getUser() throws DataException {
        String s = prop.getProperty(P_USER, "");
        if (s.isEmpty()) throw new DataException("User must be set in the property file");
        return s;
    }

    public String getPassword() throws DataException {
        String s = prop.getProperty(P_PASSWORD, "");
        if (s.isEmpty()) throw new DataException("Password must be set in the property file");
        return s;
    }

    public int[] getRepositories() throws DataException {
        String s = prop.getProperty(P_REPOS,"");
        return getIntList("The repositories entry in the properties file", s);
    }

    public static int[] getIntList(String label, String s) throws DataException {
        if (s.isEmpty()) throw new DataException(String.format("%s [%s] must be a comma separate list of integers", label, s));
        String[] vals = s.split(",");
        int[] ivals = new int[vals.length];
        for(int i=0; i<vals.length; i++) {
            try {
                ivals[i] = Integer.parseInt(vals[i].trim());                
            } catch (NumberFormatException e) {
                throw new DataException(String.format("%s [%s] must be a comma separate list of integers", label, s));                
            }
        }
        return ivals;        
    }
    
}
