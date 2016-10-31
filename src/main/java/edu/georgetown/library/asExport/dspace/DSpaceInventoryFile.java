package edu.georgetown.library.asExport.dspace;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import edu.georgetown.library.asExport.DataException;

public class DSpaceInventoryFile {

    
    private HashMap<Integer,HashMap<Long,InventoryRecord>> inventoryMap = new HashMap<>();
    
    public DSpaceInventoryFile(File f) throws IOException {
        CSVParser cp = CSVParser.parse(f, Charset.forName("UTF-8"), CSVFormat.DEFAULT);
        //Map<String,Integer> headers = cp.getHeaderMap();
        List<CSVRecord> recs = cp.getRecords();
        for(CSVRecord rec: recs) {
            if (rec.getRecordNumber() == 1) continue;
            try {
                InventoryRecord invRec = new InventoryRecord(rec);
                HashMap<Long,InventoryRecord> repoRecs = getRepoInventory(invRec.getFindingAidRepo());
                repoRecs.put(invRec.getFindingAidResourceId(), invRec);
            } catch (DataException | ParseException e) {
                System.err.println(String.format("Error parsing inventory: %s", e.getMessage()));
            }
        }        
    }
    
    public HashMap<Long,InventoryRecord> getRepoInventory(int repo) {
        HashMap<Long, InventoryRecord> repoRecs = inventoryMap.get(repo);
        if (repoRecs == null){
            repoRecs = new HashMap<Long, InventoryRecord>();
            inventoryMap.put(repo,  repoRecs);
        }
        return repoRecs;
    }
    
    
    public InventoryRecord get(int repoid, long objid) {
        return getRepoInventory(repoid).get(objid);
    }

    public int count() {
        int count = 0;
        for(int repo: inventoryMap.keySet()) {
            count += inventoryMap.get(repo).size();
        }
        return count;
    }
    
    public static void main(String[] args) {
        String s = args.length == 0 ? "reports/asSyncInventory.csv" : args[0];
        try {
            DSpaceInventoryFile dspaceInv = new DSpaceInventoryFile(new File(s));
            System.out.println(String.format("Inventory has [%d] Finding Aids", dspaceInv.count()));
        } catch (IOException e) {
           e.printStackTrace();
        }
    }
}
