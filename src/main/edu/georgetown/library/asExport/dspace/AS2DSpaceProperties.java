package edu.georgetown.library.asExport.dspace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.georgetown.library.asExport.ASProperties;
import edu.georgetown.library.asExport.DataException;

public class AS2DSpaceProperties extends ASProperties {

    public AS2DSpaceProperties(File propFile) throws FileNotFoundException, IOException {
        super(propFile);
    }

    public String getRepoHandle(int repo) throws DataException {
        String s = getProperties().getProperty(String.format("handle_%d", repo), "");
        if (s.isEmpty()) throw new DataException(String.format("A handle [handle_%d] must be defined in the properties file", repo));
        return s;
    }

}
