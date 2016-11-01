package edu.georgetown.library.asExport.dspace;

import org.apache.http.client.ClientProtocolException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import edu.georgetown.library.asExport.ResourceReport;
import edu.georgetown.library.asExport.ResourceStatus;
import edu.georgetown.library.asExport.TYPE;

public class CreateIngestFolders extends ASDriver {   
    File frpt;
    private int[] irepos;
    int maxitem = 0;
    private File outDir;
    private File rptDir;
    private String dateStr;
    public static final DateFormat exportDateFormat = new SimpleDateFormat("YYYYMMdd");
    private DSpaceInventoryFile dspaceInventory;
    
    public CreateIngestFolders(ASParsedCommandLine cmdLine) throws ClientProtocolException, URISyntaxException, IOException, ParseException, DataException {
        super(cmdLine);
        String repList = cmdLine.getRepositoryList();
        irepos = repList.isEmpty() ? prop.getRepositories() : ASProperties.getIntList("The repos parameter", repList);
        maxitem = cmdLine.getMaxItemPerRepo();
        outDir = prop.resetOutputDir();
        rptDir = prop.getReportDir();
        frpt = new File(rptDir, "AS.report.csv");
        dateStr = exportDateFormat.format(new Date());
        dspaceInventory = cmdLine.getInventoryFile();
    }

    public void processRepos() throws ClientProtocolException, URISyntaxException, IOException, NumberFormatException, DataException {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(frpt))){
            processRepos(bw, irepos);
        }
    }
    public void processRepos(BufferedWriter bw, int[] repos) throws ClientProtocolException, URISyntaxException, IOException, NumberFormatException, DataException {
        File ingestDir = new File(outDir, "ingest");
        ingestDir.mkdirs();
        int count = 0;
        for(int irepo: repos){
            processRepo(bw, ingestDir, irepo, String.format("Repo %d of %d", ++count, repos.length));
        }
    }
    
    public void processRepo(BufferedWriter bw, File ingestDir, int irepo, String header) throws DataException, ClientProtocolException, URISyntaxException, IOException {
        String repoName = prop.getRepoHandle(irepo).replaceAll("[/\\s]", "_");
        File repoDir = new File(ingestDir, repoName);
        repoDir.mkdirs();
        
        List<Long> list = asConn.getObjects(irepo, TYPE.resources);
        int count = 0;
        for(long objid : list) {
            count++;
            if (maxitem > 0 && count > maxitem) break;
            try {
                String rheader = String.format("%s; Resource %d of %d", header, count, list.size());
                processResource(bw, repoDir, irepo, objid, rheader);
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
    
    public void processResource(BufferedWriter bw, File repoDir, int irepo, long objid, String rheader) throws ClientProtocolException, URISyntaxException, IOException, ParserConfigurationException, DataException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
        JSONObject obj = asConn.getObject(irepo, TYPE.resources, objid);
        if (obj == null) {
            System.out.println(String.format(" *** Object not found - skipping"));
            return;
        }
        ASResource asRes = new ASResource(obj);
        String id = asRes.getID(String.format("res_%d", objid));
        ResourceReport rrpt = new ResourceReport(id, asRes.isPublished());
        String label = String.format("%s: %s", rheader, id);
        System.out.println(label);
        try {
            if (dspaceInventory.isInInventory(irepo, objid)) {
                InventoryRecord irec = dspaceInventory.get(irepo, objid);
                rrpt.setStatus(ResourceStatus.Skipped, String.format("Item already in DSpace with handle [%s]", irec.getItemHandle()));
            } else if (asRes.isPublished()) {
                //Attempt to parse and report on document before creating folder.  Only create folder if parse-able.
                Document d = asConn.getEADXML(irepo,  objid);
                rrpt.setParsedValues(dumpEAD(d));

                File objDir = new File(repoDir, id);
                objDir.mkdirs();
                
                convertEAD(d, new File(objDir, "dublin_core.xml"), irepo, objid);
                File eadFile = new File(objDir, String.format("ead.%s.%s.pdf", id, dateStr));
                try {
                    asConn.saveResourceFile(irepo, objid, FORMAT.pdf, eadFile);
                } catch(Exception e) {
                    rrpt.setStatus(ResourceStatus.ExportFailure, e.getMessage());
                }
                    
                File contentsFile = new File(objDir, "contents");
                try(BufferedWriter contentsbw = new BufferedWriter(new FileWriter(contentsFile))) {
                    contentsbw.write(String.format("%s\tbundle:ORIGINAL\tdescription:%s", eadFile.getName(), prop.getBitstreamDesc("Finding Aid")));
                }
                rrpt.setStatus(ResourceStatus.Published, "");
            }        
        } catch (SAXException e) {
            rrpt.setStatus(ResourceStatus.Unparsed, e.getMessage());                
        } finally {
            if (rrpt.getStatus() != ResourceStatus.Published) {
                System.out.println(String.format(" *** %s - SKIPPING", rrpt.getStatusText()));                    
            }
            bw.write(rrpt.asCSV());
            bw.flush();
        }
    }

    /*
     * Args
     *   prop filename
     *   dspace finding aid inventory filename
     *   list of repos to process (optional)
     */
    public static void main(String[] args) {
        ASCommandLineSpec asCmdLine = new ASCommandLineSpec(CreateIngestFolders.class.getName());
        asCmdLine.addRepos().addInventory();
        try {
            ASParsedCommandLine cmdLine = asCmdLine.parse(args);
            CreateIngestFolders createIngestFolders = new CreateIngestFolders(cmdLine);
            System.out.println("Process Repos");
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
            System.err.println(e.getMessage());
            System.err.println();
            asCmdLine.usage();
        }
    }

}
