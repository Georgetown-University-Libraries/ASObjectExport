package edu.georgetown.library.asExport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.cli.CommandLine;

import edu.georgetown.library.asExport.dspace.AS2DSpaceProperties;
import edu.georgetown.library.asExport.dspace.DSpaceInventoryFile;

public class ASParsedCommandLine {
    private CommandLine cmdLine;
    public ASParsedCommandLine(CommandLine cmdLine) {
        this.cmdLine = cmdLine;
    }
    public AS2DSpaceProperties getPropertyFile() throws DataException, FileNotFoundException, IOException {
        String fname = cmdLine.getOptionValue(ASCommandLineSpec.OPT_PROP, "");
        if (fname.isEmpty()) throw new DataException("Prop file cannot be blank");
        File propFile = new File(fname);
        if (!propFile.exists()) throw new DataException(String.format("Prop file [%s] does not exist", propFile));
        return new AS2DSpaceProperties(propFile);
    }
    
    public DSpaceInventoryFile getInventoryFile() throws DataException, IOException {
        String fname = cmdLine.getOptionValue(ASCommandLineSpec.OPT_INVENTORY, "");
        if (fname.isEmpty()) throw new DataException("Inventory file cannot be blank");
        File invFile = new File(fname);
        if (!invFile.exists()) throw new DataException(String.format("Inventory file [%s] does not exist", invFile));
        return new DSpaceInventoryFile(invFile);
    }

    public int getRepositoryId() throws DataException {
        String rep = cmdLine.getOptionValue(ASCommandLineSpec.OPT_REPO, "");
        if (rep.isEmpty()) throw new DataException("Repository id cannot be blank");
        try {
            return Integer.parseInt(rep);
        } catch (NumberFormatException e) {
            throw new DataException(String.format("Repository id [%s] must be an integer", rep));
        }
    }

    public int getMaxItemPerRepo() throws DataException {
        String maxitem = cmdLine.getOptionValue(ASCommandLineSpec.OPT_MAXITEM, "0");
        try {
            return Integer.parseInt(maxitem);
        } catch (NumberFormatException e) {
            throw new DataException(String.format("Max item per repo [%s] must be an integer", maxitem));
        }
    }

    
    public long getObjectId() throws DataException {
        String s = cmdLine.getOptionValue(ASCommandLineSpec.OPT_OBJ, "");
        if (s.isEmpty()) throw new DataException("Object id cannot be blank");
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new DataException(String.format("Object id [%s] must be a long integer", s));
        }
    }

    public TYPE getType() throws DataException {
        for(TYPE t: TYPE.values()) {
            if (cmdLine.hasOption(t.name())) {
                return t;
            }
        }
        throw new DataException("Object type parameter not found");
    }

    public String getRepositoryList() {
        return cmdLine.getOptionValue(ASCommandLineSpec.OPT_REPOS, "");
    }
    
    public Date getModdate() throws DataException {
        String moddate = cmdLine.getOptionValue(ASCommandLineSpec.OPT_MODDATE, "");
        if (moddate.isEmpty()) {
            throw new DataException("moddate cannot be blank");
        }
        try {
            return ASDriver.exportDateFormat.parse(moddate);
        } catch (ParseException e) {
            throw new DataException(String.format("moddate [%s] must be in YYYYMMDD format", moddate));
        }
    }
}
