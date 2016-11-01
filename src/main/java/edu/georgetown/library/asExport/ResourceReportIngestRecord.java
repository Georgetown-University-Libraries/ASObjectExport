package edu.georgetown.library.asExport;

import java.io.BufferedWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class ResourceReportIngestRecord implements ResourceReportRecord {
      
  private String id        = "";
  private String title     = "";
  private String date      = "";
  private String subjects  = "";
  private ResourceStatus status;
  private String note = "";
  
  public ResourceReportIngestRecord(String id, boolean published) {
    this.id = id;
    if (published) {
        setStatus(ResourceStatus.Unparsed, "Not yet parsed");
    } else {
        setStatus(ResourceStatus.Unpublished, "Finding Aid is not published in ArchivesSpace");
    }
  }
  
  public void setParsedValues(String csv) throws IOException {
    CSVRecord rec = CSVParser.parse(csv, CSVFormat.DEFAULT).getRecords().get(0);
    if (rec.size() > 1) title = rec.get(1);
    if (rec.size() > 2) date = rec.get(2);
    if (rec.size() > 3) subjects = rec.get(3);
    setStatus(ResourceStatus.Parsed, "");
  }
  
  public void setStatus(ResourceStatus rs, String note) {
      this.status = rs;
      this.note =  note;
  }
  
  public static final String[] HEADER = {"Finding Aid ID", "Report Status", "Title", "Finding Aid Date", "Finding Aid Subjects"}; 
  public static String getReportHeader() {
      return String.join(",", ResourceReportIngestRecord.HEADER);
  }
  
  public String asCSV() throws IOException {
      StringBuilder sb = new StringBuilder();
      //CSVFormat.EXCEL is needed to force the output to be wrapped in quotes
      try(CSVPrinter cp = new CSVPrinter(sb, CSVFormat.EXCEL)){
          cp.printRecord(id, getStatusText(), title, date, subjects);          
      };
      return sb.toString();
  }
  public ResourceStatus getStatus() {
      return status;
  }
  
  public String getStatusText() {
      if (note.isEmpty()) {
          return status.name();
      }
      return String.format("%s (%s)", status.name(), note);
  }
  
  public String getNote() {
      return note;
  }
  
  public void writeHeader(BufferedWriter bw) throws IOException {
      bw.write(getReportHeader());
      bw.flush();
  }
  public void writeRecord(BufferedWriter bw) throws IOException {
     bw.write(asCSV());
     bw.flush();
  }
}
