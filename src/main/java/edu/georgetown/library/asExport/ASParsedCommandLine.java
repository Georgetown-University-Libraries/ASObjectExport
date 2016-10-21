package edu.georgetown.library.asExport;

import java.io.File;

import org.apache.commons.cli.CommandLine;

public class ASParsedCommandLine {
    private CommandLine cmdLine;
    public ASParsedCommandLine(CommandLine cmdLine) {
        this.cmdLine = cmdLine;
    }
    public File getPropertyFile() throws DataException {
        String fname = cmdLine.getOptionValue(ASCommandLineSpec.OPT_PROP, "");
        if (fname.isEmpty()) throw new DataException("Prop file cannot be blank");
        File propFile = new File(fname);
        if (!propFile.exists()) throw new DataException(String.format("Prop file [%s] does not exist", propFile));
        return propFile;
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
}
