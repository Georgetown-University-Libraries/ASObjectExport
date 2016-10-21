package edu.georgetown.library.asExport;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ASCommandLineSpec {
    public static final String OPT_HELP     = "h";
    public static final String OPT_PROP     = "prop";
    public static final String OPT_REPOS    = "repos";
    public static final String OPT_REPO     = "repo";
    public static final String OPT_MODDATE  = "moddate";
    
    private Options options = new Options();
    private String commandClass;
    
    public ASCommandLineSpec(String commandClass) {
        this.commandClass = commandClass;
        options.addOption(OPT_HELP, true, "Help");
        options.addOption(OPT_PROP, true, "Property File");
        options.getOption(OPT_PROP).setRequired(true);
    }
    
    public ASCommandLineSpec addRepos() {
        options.addOption(OPT_REPOS, true, "Repository List (comma separated) or blank for all");
        return this;
    }

    public ASCommandLineSpec addRepo() {
        options.addOption(OPT_REPO, true, "Repository id");
        options.getOption(OPT_REPO).setRequired(true);
        return this;
    }
    
    public ASParsedCommandLine parse(String[] args) {
        DefaultParser clParse = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try {
            CommandLine cmdLine = clParse.parse(options, args);
            if (cmdLine.hasOption(OPT_HELP)) {
                formatter.printHelp(commandClass, options);
                System.exit(0);
            }
            return new ASParsedCommandLine(cmdLine);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp(commandClass, options);
            System.exit(1);
        }
        return null;
    }
}
