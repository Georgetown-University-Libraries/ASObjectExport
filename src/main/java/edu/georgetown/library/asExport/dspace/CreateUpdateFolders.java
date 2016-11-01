package edu.georgetown.library.asExport.dspace;

import org.apache.http.client.ClientProtocolException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;

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
import edu.georgetown.library.asExport.ResourceReportIngestRecord;
import edu.georgetown.library.asExport.ResourceStatus;
import edu.georgetown.library.asExport.TYPE;

public class CreateUpdateFolders extends ASDriver {   
    ResourceReport frpt;
    private int[] irepos;
    int maxitem = 0;
    private File outDir;
    private File rptDir;
    private String dateStr;
    private DSpaceInventoryFile dspaceInventory;
    Date moddate;
    
    public CreateUpdateFolders(ASParsedCommandLine cmdLine) throws ClientProtocolException, URISyntaxException, IOException, ParseException, DataException {
        super(cmdLine);
        String repList = cmdLine.getRepositoryList();
        irepos = repList.isEmpty() ? prop.getRepositories() : ASProperties.getIntList("The repos parameter", repList);
        maxitem = cmdLine.getMaxItemPerRepo();
        outDir = prop.resetOutputDir();
        rptDir = prop.getReportDir();
        frpt = new ResourceReport(new File(rptDir, "AS.report.csv"));
        dateStr = ASDriver.exportDateFormat.format(new Date());
        dspaceInventory = cmdLine.getInventoryFile();
        moddate = cmdLine.getModdate();
    }

    public void processRepos() throws ClientProtocolException, URISyntaxException, IOException, NumberFormatException, DataException {
        frpt.write(ResourceReportIngestRecord.getReportHeader());
        processRepos(irepos);
        frpt.close();
    }
    public void processRepos(int[] repos) throws ClientProtocolException, URISyntaxException, IOException, NumberFormatException, DataException {
        File updateDir = new File(outDir, "update");
        updateDir.mkdirs();
        int count = 0;
        for(int irepo: repos){
            processRepo(updateDir, irepo, String.format("Repo %d of %d", ++count, repos.length));
        }
    }
    
    public void processRepo(File updateDir, int irepo, String header) throws DataException, ClientProtocolException, URISyntaxException, IOException {
        String repoName = prop.getRepoHandle(irepo).replaceAll("[/\\s]", "_");
        File repoDir = new File(updateDir, repoName);
        repoDir.mkdirs();
        
        HashMap<Long, InventoryRecord> inventory = dspaceInventory.getRepoInventory(irepo);
        int count = 0;
        for(long objid: inventory.keySet()) {
            count++;
            if (maxitem > 0 && count > maxitem) break;
            InventoryRecord invRec = inventory.get(objid);
            Date faDate = invRec.getFindingAidExportDate();
            String datestr = faDate == null ? "No Date" : ASDriver.exportDateFormat.format(faDate);
            try {
                String rheader = String.format("%s; Resource %d of %d [%s]", header, count, inventory.size(), datestr);
                processResource(repoDir, irepo, objid, rheader, faDate);
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
    
    public void processResource(File repoDir, int irepo, long objid, String rheader, Date faDate) throws ClientProtocolException, URISyntaxException, IOException, ParserConfigurationException, DataException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
        JSONObject obj = asConn.getObject(irepo, TYPE.resources, objid);
        if (obj == null) {
            System.out.println(String.format(" *** Object not found - skipping"));
            return;
        }
        ASResource asRes = new ASResource(obj);
        String id = asRes.getID(String.format("res_%d", objid));
        ResourceReportIngestRecord rrpt = new ResourceReportIngestRecord(id, asRes.isPublished());
        String label = String.format("%s: %s", rheader, id);
        System.out.println(label);
        try {
            if (faDate != null && moddate.compareTo(faDate) < 0) {
                rrpt.setStatus(ResourceStatus.Skipped, String.format("Item was last updated on [%s]", ASDriver.exportDateFormat.format(faDate)));
            } else if (asRes.isPublished()) {
                //Attempt to parse and report on document before creating folder.  Only create folder if parse-able.
                Document d = asConn.getEADXML(irepo, objid);
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
        } catch (DataException | SAXException e) {
            rrpt.setStatus(ResourceStatus.Unparsed, e.getMessage());                
        } finally {
            if (rrpt.getStatus() != ResourceStatus.Published) {
                System.out.println(String.format(" *** %s - SKIPPING", rrpt.getStatusText()));                    
            }
            frpt.writeRecord(rrpt);
        }
    }

    /*
     * Args
     *   prop filename
     *   dspace finding aid inventory filename
     *   list of repos to process (optional)
     */
    public static void main(String[] args) {
        ASCommandLineSpec asCmdLine = new ASCommandLineSpec(CreateUpdateFolders.class.getName());
        asCmdLine.addRepos().addInventory().addModdate();
        try {
            ASParsedCommandLine cmdLine = asCmdLine.parse(args);
            CreateUpdateFolders createUpdateFolders = new CreateUpdateFolders(cmdLine);
            System.out.println("Process Repos");
            createUpdateFolders.processRepos();        
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
