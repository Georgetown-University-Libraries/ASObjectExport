package edu.georgetown.library.asExport;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class ASDriver {
  public static void main(String[] args) {
	try {
	    ASCommandLineSpec asCmdLine = new ASCommandLineSpec(ASDriver.class.getName());
	    ASParsedCommandLine cmdLine = asCmdLine.parse(args);
	    File propFile = cmdLine.getPropertyFile();
	    ASProperties prop = new ASProperties(propFile);
	    ASConnection asData = new ASConnection(prop);
	    asData.processRepos();
	} catch (ClientProtocolException e) {
		e.printStackTrace();
	} catch (URISyntaxException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} catch (ParseException e) {
		e.printStackTrace();
	} catch (DataException e) {
        e.printStackTrace();
    }
  }
}
