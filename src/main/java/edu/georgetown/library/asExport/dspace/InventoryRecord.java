package edu.georgetown.library.asExport.dspace;

import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVRecord;

import edu.georgetown.library.asExport.DataException;

public class InventoryRecord {
    private String collHandle = "";
    private String itemHandle = "";
    private String title      = "";
    private String url        = "";
    private String filename   = "";
    private String dateStr    = "";
    private Date date;
    private int repo = 0;
    private long objid = 0;
    
    InventoryRecord(CSVRecord rec) throws DataException, ParseException {
        if (rec.size() != 6) throw new DataException("Inventory records must contain 6 fields");
        collHandle = rec.get(0);
        itemHandle = rec.get(1);
        title      = rec.get(2);
        url        = rec.get(3);
        filename   = rec.get(4);
        dateStr    = rec.get(5);
        
        Pattern p = Pattern.compile("^.*/repositories/(\\d+)/resources/(\\d+)$");
        Matcher m = p.matcher(url);
        if (!m.matches()) {
            System.err.println(String.format("Warning: unparseable finding aid URL: %s", url));
        } else {
            repo = Integer.parseInt(m.group(1));
            objid = Long.parseLong(m.group(2));
        }
        if (!dateStr.isEmpty()) {
            date = CreateIngestFolders.exportDateFormat.parse(dateStr);
        }
    }
    
    public String getCollHandle() {return collHandle;}
    public String getItemHandle() {return itemHandle;}
    public String getTitle() {return title;}
    public String getFindingAidUrl() {return url;}
    public String getFindingAidFilename() {return filename;}
    public Date getFindingAidExportDate() {return date;}
    public int getFindingAidRepo() {return repo;}
    public long getFindingAidResourceId() {return objid;}
}