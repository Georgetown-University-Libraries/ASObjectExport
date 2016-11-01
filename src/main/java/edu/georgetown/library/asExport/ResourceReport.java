package edu.georgetown.library.asExport;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ResourceReport implements Closeable {
        
  private BufferedWriter bw;
  public ResourceReport(File f) throws IOException {
    this.bw = new BufferedWriter(new FileWriter(f));
  }
  
  public void write(String s) throws IOException {
      bw.write(s);
      bw.flush();
  }

  public void writeRecord(ResourceReportRecord rec) throws IOException {
      bw.write(rec.asCSV());
      bw.flush();
  }
  
  public void close() throws IOException {
      bw.close();
  }
}
