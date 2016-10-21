package edu.georgetown.library.asExport;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import edu.georgetown.library.asExport.dspace.AS2DSpaceProperties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class ASDriver {
  public static void main(String[] args) {
	try {
	    ASCommandLineSpec asCmdLine = new ASCommandLineSpec(ASDriver.class.getName());
	    ASParsedCommandLine cmdLine = asCmdLine.parse(args);
	    ASDriver driver = new ASDriver(cmdLine);
	    driver.processRepos();
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
  
  protected ASParsedCommandLine cmdLine;
  protected ASConnection asConn;
  protected AS2DSpaceProperties prop;

  public ASDriver(ASParsedCommandLine cmdLine) throws DataException, FileNotFoundException, IOException, URISyntaxException, ParseException {
      this.cmdLine = cmdLine;
      File propFile = cmdLine.getPropertyFile();
      this.prop = new AS2DSpaceProperties(propFile);
      asConn = new ASConnection(prop);    
  }
  
  public void processRepos() throws ClientProtocolException, URISyntaxException, IOException, NumberFormatException, DataException {
      processRepos(prop.getRepositories());
  }
  public void processRepos(int[] repos) throws ClientProtocolException, URISyntaxException, IOException, NumberFormatException, DataException {
      for(int irepo: repos){
          //String handle = prop.getRepoHandle(irepo);
          //System.out.println(String.format("REPO %d -- %s", irepo, handle));
          List<Long> list = asConn.getObjects(irepo, TYPE.resources);
          for(long objid : list) {
              JSONObject obj = asConn.getPublishedObject(irepo, TYPE.resources, objid);
              if (obj == null) continue;
              
              System.out.println(String.format("[%d] %s", objid, obj.toString()));
              ASResource res = new ASResource(obj);
              System.out.println("Title         : "+res.getTitle());
              System.out.println("Date          : "+res.getDate());
              System.out.println("Mod Date      : "+res.getModDate());
              System.out.println("Description   : "+res.getDescription());
              System.out.println("");
          }
      }      
  }

}
