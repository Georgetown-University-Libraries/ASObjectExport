package edu.georgetown.library.asExport.dspace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import edu.georgetown.library.asExport.ResourceReportRecord;

public class BulkMetadataRecord implements ResourceReportRecord {
    
    private HashMap<MetadataRecordHeader,String> values = new HashMap<>();
    public BulkMetadataRecord(String coll) {
        addValue(MetadataRecordHeader.TYPE, "Finding Aid");
        addValue(MetadataRecordHeader.ID, "+");
        addValue(MetadataRecordHeader.COLL, coll);
    }
    
    public void addValue(MetadataRecordHeader rh, String value) {
        String val = values.containsKey(rh) ? values.get(rh) + "||" + value : value;
        values.put(rh, val);
    }

    public void addValue(MetadataRecordHeader rh, List<String>values) {
        for(String v: values) {
            addValue(rh, v);
        }
    }

    public static String getHeaderCSV() throws IOException {
        StringBuilder sb = new StringBuilder();
        //CSVFormat.EXCEL is needed to force the output to be wrapped in quotes
        
        ArrayList<String> list = new ArrayList<>();
        for(MetadataRecordHeader rh: MetadataRecordHeader.values()) {
            list.add(rh.getHeader());
        }
        try(CSVPrinter cp = new CSVPrinter(sb, CSVFormat.EXCEL)){
            cp.printRecord(list);          
        };
        return sb.toString();
    }
    public String asCSV() throws IOException {
        StringBuilder sb = new StringBuilder();
        //CSVFormat.EXCEL is needed to force the output to be wrapped in quotes
        
        ArrayList<String> list = new ArrayList<>();
        for(MetadataRecordHeader rh: MetadataRecordHeader.values()) {
            String v = values.containsKey(rh) ? values.get(rh) : "";
            list.add(v);
        }
        try(CSVPrinter cp = new CSVPrinter(sb, CSVFormat.EXCEL)){
            cp.printRecord(list);          
        };
        return sb.toString();
    }

}
