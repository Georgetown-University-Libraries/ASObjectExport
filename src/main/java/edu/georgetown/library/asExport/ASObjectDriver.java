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
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
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
        
        JSONObject obj = asConn.getPublishedObject(repo, type, objid);
        if (obj != null) {
            //System.out.println(String.format("[%d] %s", objid, obj.toString()));
            ASResource res = new ASResource(obj, asConn);
            System.out.println("Title         : "+res.getTitle());
            System.out.println("Date          : "+res.getDate());
            System.out.println("Mod Date      : "+res.getModDate());
            //System.out.println("Description   : "+res.getDescription());
            System.out.println("");                    
        } else {
            System.out.println(" *** Unpublished ***\n\n");
        }
        
        Document d;
        try {
            long start = getStart();      
            System.out.println(String.format("Download EAD as XML: %d/%d", repo, objid));
            System.out.flush();
            d = asConn.getEADXML(repo, objid);
            
            File f = new File("test.xml");
            saveEAD(d, f);
            System.out.println(String.format("File written: %s; %s", f.getAbsolutePath(), getDurationString(start)));
            System.out.flush();
            f = new File("test.fmt.xml");
            convertEAD(d, f, repo, objid);
            System.out.println(String.format("File written: %s; %s", f.getAbsolutePath(), getDurationString(start)));
            System.out.flush();
            bw.write(dumpEAD(d));
            System.out.println(String.format("Download PDF EAD: %d/%d", repo, objid));
            File eadFile = new File(String.format("ead.%d.pdf", objid));
            asConn.saveResourceFile(repo, objid, FORMAT.pdf, eadFile);
            System.out.println(String.format("File written: %s; %s", eadFile.getAbsolutePath(), getDurationString(start)));
            System.out.flush();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
