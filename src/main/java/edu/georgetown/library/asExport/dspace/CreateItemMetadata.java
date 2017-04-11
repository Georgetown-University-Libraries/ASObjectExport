package edu.georgetown.library.asExport.dspace;

import org.apache.http.client.ClientProtocolException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import edu.georgetown.library.asExport.ASCommandLineSpec;
import edu.georgetown.library.asExport.ASDriver;
import edu.georgetown.library.asExport.ASParsedCommandLine;
import edu.georgetown.library.asExport.ASProperties;
import edu.georgetown.library.asExport.ASResource;
import edu.georgetown.library.asExport.DataException;
import edu.georgetown.library.asExport.ResourceReport;
import edu.georgetown.library.asExport.ResourceReportIngestRecord;
import edu.georgetown.library.asExport.ResourceStatus;
import edu.georgetown.library.asExport.TYPE;

public class CreateItemMetadata extends ASDriver {   
    ResourceReport frpt;
    BulkMetadataCreate bulkMeta;
    private int[] irepos;
    int maxitem = 0;
    private File rptDir;
    private DSpaceInventoryFile dspaceInventory;
    
    public CreateItemMetadata(ASParsedCommandLine cmdLine) throws ClientProtocolException, URISyntaxException, IOException, ParseException, DataException {
        super(cmdLine);
        String repList = cmdLine.getRepositoryList();
        irepos = repList.isEmpty() ? prop.getRepositories() : ASProperties.getIntList("The repos parameter", repList);
        maxitem = cmdLine.getMaxItemPerRepo();
        rptDir = prop.getReportDir();
        frpt = new ResourceReport(new File(rptDir, "AS.report.csv"));
        bulkMeta = new BulkMetadataCreate(new File(rptDir, "AS.metadata.csv"));
        dspaceInventory = cmdLine.getInventoryFile();
    }

    public void processRepos() throws ClientProtocolException, URISyntaxException, IOException, NumberFormatException, DataException {
        frpt.write(ResourceReportIngestRecord.getReportHeader());
        processRepos(irepos);
        frpt.close();
        bulkMeta.close();
    }
    public void processRepos(int[] repos) throws ClientProtocolException, URISyntaxException, IOException, NumberFormatException, DataException {
        int count = 0;
        for(int irepo: repos){
            processRepo(irepo, String.format("Repo %d of %d", ++count, repos.length));
        }
    }
    
    public void processRepo(int irepo, String header) throws DataException, ClientProtocolException, URISyntaxException, IOException {
        List<Long> list = asConn.getObjects(irepo, TYPE.resources);
        int count = 0;
        for(long objid : list) {
            count++;
            if (maxitem > 0 && count > maxitem) break;
            try {
                String rheader = String.format("%s; Resource %d of %d", header, count, list.size());
                processResource(irepo, objid, rheader);
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
    
    public void processResource(int irepo, long objid, String rheader) throws ClientProtocolException, URISyntaxException, IOException, ParserConfigurationException, DataException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
        JSONObject obj = asConn.getObject(irepo, TYPE.resources, objid);
        if (obj == null) {
            System.out.println(String.format(" *** Object not found - skipping"));
            return;
        }
        //System.out.println(obj.toString());
        ASResource asRes = new ASResource(obj, asConn);
        String id = asRes.getID(String.format("res_%d", objid));
        ResourceReportIngestRecord rrpt = new ResourceReportIngestRecord(id, asRes.isPublished());
        String label = String.format("%s: %s", rheader, id);
        System.out.println(label);
        
        BulkMetadataRecord bmr = new BulkMetadataRecord(prop.getRepoHandle(irepo));
        rrpt.setMetadata(objid, asRes);
        
        try {
            if (dspaceInventory.isInInventory(irepo, objid)) {
                InventoryRecord irec = dspaceInventory.get(irepo, objid);
                rrpt.setStatus(ResourceStatus.Skipped, String.format("Item already in DSpace with handle [%s]", irec.getItemHandle()));
            } else if (asRes.isPublished()) {
                bmr.addValue(MetadataRecordHeader.TITLE, asRes.getTitle());
                bmr.addValue(MetadataRecordHeader.AUTHOR, prop.getProperty("author", irepo));
                bmr.addValue(MetadataRecordHeader.CREATOR, prop.getProperty("creator", irepo));
                bmr.addValue(MetadataRecordHeader.RIGHTS, prop.getProperty("rights", irepo));
                bmr.addValue(MetadataRecordHeader.RELURI, getObjectUri(irepo, TYPE.resources, objid));
                bmr.addValue(MetadataRecordHeader.DATE, asRes.getDate());
                bmr.addValue(MetadataRecordHeader.DESC, asRes.getDescription());
                bmr.addValue(MetadataRecordHeader.IDOTHER, asRes.getID(""+objid));
                bmr.addValue(MetadataRecordHeader.SUBJ, asRes.getSubjects());
                bulkMeta.writeRecord(bmr);
                rrpt.setStatus(ResourceStatus.MetadataCreated, "");
            }        
        } finally {
            if (rrpt.getStatus() != ResourceStatus.MetadataCreated) {
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
        ASCommandLineSpec asCmdLine = new ASCommandLineSpec(CreateItemMetadata.class.getName());
        asCmdLine.addRepos().addInventory();
        try {
            ASParsedCommandLine cmdLine = asCmdLine.parse(args);
            CreateItemMetadata createIngestMetadata = new CreateItemMetadata(cmdLine);
            System.out.println("Process Repos");
            createIngestMetadata.processRepos();        
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
