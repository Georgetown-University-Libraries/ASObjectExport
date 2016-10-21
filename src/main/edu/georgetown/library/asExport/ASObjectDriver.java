package edu.georgetown.library.asExport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class ASObjectDriver extends ASDriver {

    public ASObjectDriver(ASParsedCommandLine cmdLine)
            throws DataException, FileNotFoundException, IOException, URISyntaxException, ParseException {
        super(cmdLine);
    }

    public static void main(String[] args) {
        try {
            ASCommandLineSpec asCmdLine = new ASCommandLineSpec(ASDriver.class.getName());
            asCmdLine.addRepoTypeObject();
            ASParsedCommandLine cmdLine = asCmdLine.parse(args);
            ASObjectDriver driver = new ASObjectDriver(cmdLine);
            driver.processRequest();
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

    public void processRequest() throws DataException, ClientProtocolException, URISyntaxException, IOException {
        int repo   = cmdLine.getRepositoryId();
        long objid = cmdLine.getObjectId();
        TYPE type  = cmdLine.getType();
        
        asConn.getResourceMetadata(repo, type, objid);
        asConn.saveResourceFile(repo, type, objid, FORMAT.pdf, new File("test.pdf"));
        asConn.saveResourceFile(repo, type, objid, FORMAT.pdf, new File("test.xml"));
    }
}
