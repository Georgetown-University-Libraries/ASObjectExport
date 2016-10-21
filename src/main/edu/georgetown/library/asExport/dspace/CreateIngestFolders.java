package edu.georgetown.library.asExport.dspace;

import org.apache.http.client.ClientProtocolException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import edu.georgetown.library.asExport.ASCommandLineSpec;
import edu.georgetown.library.asExport.ASDriver;
import edu.georgetown.library.asExport.ASParsedCommandLine;
import edu.georgetown.library.asExport.ASProperties;
import edu.georgetown.library.asExport.ASResource;
import edu.georgetown.library.asExport.DataException;
import edu.georgetown.library.asExport.TYPE;

public class CreateIngestFolders extends ASDriver {

    /*
     * Args
     *   prop filename
     *   dspace finding aid inventory filename
     *   list of repos to process (optional)
     */
    public static void main(String[] args) {
        try {
            ASCommandLineSpec asCmdLine = new ASCommandLineSpec(ASDriver.class.getName());
            asCmdLine.addRepos();
            ASParsedCommandLine cmdLine = asCmdLine.parse(args);
            CreateIngestFolders createIngestFolders = new CreateIngestFolders(cmdLine);
            createIngestFolders.processRepos();        
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
    
    private int[] irepos;
    
    public CreateIngestFolders(ASParsedCommandLine cmdLine) throws ClientProtocolException, URISyntaxException, IOException, ParseException, DataException {
        super(cmdLine);
        String repList = cmdLine.getRepositoryList();
        irepos = repList.isEmpty() ? prop.getRepositories() : ASProperties.getIntList("The repos parameter", repList);
    }

    public void processRepos() throws ClientProtocolException, URISyntaxException, IOException, NumberFormatException, DataException {
        for(int irepo: irepos){
            String handle = prop.getRepoHandle(irepo);
            System.out.println(String.format("REPO %d -- %s", irepo, handle));
            List<Long> list = asConn.getObjects(irepo, TYPE.resources);
            for(long objid : list) {
                JSONObject obj = asConn.getPublishedObject(irepo, TYPE.resources, objid);
                if (obj == null) continue;
                System.out.println(obj);
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
