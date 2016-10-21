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
    
    public int getRepository() throws DataException {
        String rep = cmdLine.getOptionValue(ASCommandLineSpec.OPT_REPO, "");
        if (rep.isEmpty()) throw new DataException("Repository cannot be blank");
        try {
            int val = Integer.parseInt(rep);
            return val;            
        } catch (NumberFormatException e) {
            throw new DataException(String.format("Repository [%s] must be an integer", rep));
        }
    }
    
    public String getRepositoryList() {
        return cmdLine.getOptionValue(ASCommandLineSpec.OPT_REPOS, "");
    }
}
