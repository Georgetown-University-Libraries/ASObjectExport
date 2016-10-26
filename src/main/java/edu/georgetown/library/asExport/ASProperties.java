package edu.georgetown.library.asExport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

public class ASProperties  {
    private Properties prop = new Properties();
    public static final String P_SERVICE     = "service";
    public static final String P_PUBSERVICE  = "pubservice";
    public static final String P_USER        = "user";
    public static final String P_PASSWORD    = "password";
    public static final String P_REPOS       = "repositories";
    public static final String P_BITDESC     = "bitstream-description";
    public static final String P_OUTDIR      = "output-dir";
    public static final String P_CLEANOUTDIR = "clean-output-dir";
    
    public Properties getProperties() {
        return prop;
    }
    
    public ASProperties(File propFile) throws FileNotFoundException, IOException {
        prop.load(new FileReader(propFile));
    }
    
    public String getService() throws DataException {
        String s = prop.getProperty(P_SERVICE, "");
        if (s.isEmpty()) throw new DataException("service must be set in the property file");
        return s;
    }
    
    public String getPubService() throws DataException {
        String s = prop.getProperty(P_PUBSERVICE, "");
        if (s.isEmpty()) throw new DataException("pubservice must be set in the property file");
        return s;
    }
    

    public String getUser() throws DataException {
        String s = prop.getProperty(P_USER, "");
        if (s.isEmpty()) throw new DataException("user must be set in the property file");
        return s;
    }

    public String getPassword() throws DataException {
        String s = prop.getProperty(P_PASSWORD, "");
        if (s.isEmpty()) throw new DataException("password must be set in the property file");
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
    
    public String getProperty(String prefix, int repo) {
        String key = String.format("%s_%d", prefix, repo);
        return prop.getProperty(key, prop.getProperty(key, ""));
    }
    
    public String getBitstreamDesc(String def) {
        return prop.getProperty(P_BITDESC, def);
    }
    
    public File getOutputDir() throws DataException {
        String s = prop.getProperty(P_OUTDIR, "");
        if (s.isEmpty()) throw new DataException("output-dir must be set in the property file");
        File f = new File(s);
        return f;
    }
    public boolean getCleanOutputDir() throws DataException {
        String s = prop.getProperty(P_CLEANOUTDIR, "");
        if (s.equals("N")) return false;
        if (s.equals("Y")) return true;
        throw new DataException("clean-output-dir must be set to Y or N in the property file");
    }
    
    public File resetOutputDir() throws DataException, IOException {
        File f = getOutputDir();
        if (getCleanOutputDir()) {
            FileUtils.deleteDirectory(f);
        }
        if (f.exists()) {
            throw new DataException(String.format("The output-dir [%s] specified in the property file already exists", f.getName()));
        }
        f.mkdirs();
        return f;
    }
}
