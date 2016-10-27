package edu.georgetown.library.asExport.dspace;

import org.apache.http.client.ClientProtocolException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import edu.georgetown.library.asExport.ASCommandLineSpec;
import edu.georgetown.library.asExport.ASDriver;
import edu.georgetown.library.asExport.ASParsedCommandLine;
import edu.georgetown.library.asExport.ASProperties;
import edu.georgetown.library.asExport.ASResource;
import edu.georgetown.library.asExport.DataException;
import edu.georgetown.library.asExport.FORMAT;
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
    
    private OutputStream os;
    private int[] irepos;
    private File outDir;
    private String dateStr;
    
    public CreateIngestFolders(ASParsedCommandLine cmdLine) throws ClientProtocolException, URISyntaxException, IOException, ParseException, DataException {
        super(cmdLine);
        String repList = cmdLine.getRepositoryList();
        irepos = repList.isEmpty() ? prop.getRepositories() : ASProperties.getIntList("The repos parameter", repList);
        outDir = prop.resetOutputDir();
        File rptDir = new File(outDir, "reports");
        rptDir.mkdirs();
        File f = new File(rptDir, "AS.report.csv");
        os = new FileOutputStream(f);        
        DateFormat df = new SimpleDateFormat("YYYYMMdd");
        dateStr = df.format(new Date());

    }

    public void processRepos() throws ClientProtocolException, URISyntaxException, IOException, NumberFormatException, DataException {
        processRepos(irepos);
    }
    public void processRepos(int[] repos) throws ClientProtocolException, URISyntaxException, IOException, NumberFormatException, DataException {
        File ingestDir = new File(outDir, "ingest");
        ingestDir.mkdirs();
        int count = 0;
        for(int irepo: repos){
            processRepo(ingestDir, irepo, String.format("Repo %d of %d", ++count, repos.length));
        }      
    }
    
    public void processRepo(File ingestDir, int irepo, String header) throws DataException, ClientProtocolException, URISyntaxException, IOException {
        String repoName = prop.getRepoHandle(irepo).replaceAll("[/\\s]", "_");
        File repoDir = new File(ingestDir, repoName);
        repoDir.mkdirs();
        
        List<Long> list = asConn.getObjects(irepo, TYPE.resources);
        int count = 0;
        for(long objid : list) {
            try {
                String rheader = String.format("%s; Resource %d of %d", header, ++count, list.size());
                processResource(repoDir, irepo, objid, rheader);
            } catch (SAXException e) {
                System.out.println(" *** " + e.getMessage());
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
    
    public void processResource(File repoDir, int irepo, long objid, String rheader) throws ClientProtocolException, URISyntaxException, IOException, SAXException, ParserConfigurationException, DataException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
        JSONObject obj = asConn.getPublishedObject(irepo, TYPE.resources, objid);
        if (obj == null) {
            return;
        }
        ASResource asRes = new ASResource(obj);
        String id = asRes.getID(String.format("res_%d", objid));
        System.out.println(String.format("%s: %s", rheader, id));
        
        File objDir = new File(repoDir, id);
        objDir.mkdirs();
        
        Document d = asConn.getEADXML(irepo,  objid);
        dumpEAD(d, os);

        convertEAD(d, new File(objDir, "dublin_core.xml"), irepo, objid);
        File eadFile = new File(objDir, String.format("ead.%s.%s.pdf", id, dateStr));
        asConn.saveResourceFile(irepo, objid, FORMAT.pdf, eadFile);
            
        File contentsFile = new File(objDir, "contents");
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(contentsFile))) {
            bw.write(String.format("%s\tbundle:ORIGINAL\tdescription:%s", eadFile.getName(), prop.getBitstreamDesc("Finding Aid")));
        }        
    }

}
