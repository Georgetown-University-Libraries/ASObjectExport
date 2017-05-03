package edu.georgetown.library.asExport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

public class ASObjectDriver extends ASDriver {

    OutputStream os;
    BufferedWriter bw;
    public ASObjectDriver(ASParsedCommandLine cmdLine)
            throws DataException, FileNotFoundException, IOException, URISyntaxException, ParseException {
        super(cmdLine);
        File outDir = prop.resetOutputDir();
        File rptDir = new File(outDir, "reports");
        rptDir.mkdirs();
        File f = new File(rptDir, "AS.report.csv");
        os = new FileOutputStream(f);
        bw = new BufferedWriter(new OutputStreamWriter(os));
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
        
        ASObject obj = asConn.getObject(repo, type, objid);
        if (obj != null) {
            System.out.println(String.format(obj.toString()));
            try {
                File f = obj.saveXML();
                System.out.println(String.format("File %s downloaded\n\n", f.getAbsolutePath()));
	    } catch (SAXException e) {
                e.printStackTrace();
            } catch (TransformerException | TransformerFactoryConfigurationError | ParserConfigurationException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(String.format(" *** Unable to retrieve [%d/%d]***\n\n", repo, objid));
        }
        
    }


}
