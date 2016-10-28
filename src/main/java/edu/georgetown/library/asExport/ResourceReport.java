package edu.georgetown.library.asExport;

import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class ResourceReport {
      
  private String id        = "";
  private String title     = "";
  private String date      = "";
  private String subjects  = "";
  private ResourceStatus status;
  
  public ResourceReport(String id, boolean published) {
    this.id = id;
    this.status = published ? ResourceStatus.Unparsed : ResourceStatus.Unpublished;
  }
  
  public void setParsedValues(String csv) throws IOException {
    CSVRecord rec = CSVParser.parse(csv, CSVFormat.DEFAULT).getRecords().get(0);
    if (rec.size() > 1) title = rec.get(1);
    if (rec.size() > 2) date = rec.get(2);
    if (rec.size() > 3) subjects = rec.get(3);
    status = ResourceStatus.Parsed;
  }
  
  public void setStatus(ResourceStatus rs) {
      this.status = rs;
  }
  
  public String asCSV() throws IOException {
      StringBuilder sb = new StringBuilder();
      try(CSVPrinter cp = new CSVPrinter(sb, CSVFormat.DEFAULT)){
          cp.printRecord(id, status, title, date, subjects);          
      };
      return sb.toString();
  }
  public ResourceStatus getStatus() {
      return status;
  }
}
