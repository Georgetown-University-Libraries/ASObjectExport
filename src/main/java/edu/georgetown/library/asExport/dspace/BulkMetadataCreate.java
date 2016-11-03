package edu.georgetown.library.asExport.dspace;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BulkMetadataCreate implements Closeable {
        
  private BufferedWriter bw;
  public BulkMetadataCreate(File f) throws IOException {
    bw = new BufferedWriter(new FileWriter(f));
    bw.write(BulkMetadataRecord.getHeaderCSV());
  }
  
  public void write(String s) throws IOException {
      bw.write(s);
      bw.flush();
  }

  
  public void writeRecord(BulkMetadataRecord rec) throws IOException {
      bw.write(rec.asCSV());
      bw.flush();
  }
  
  public void close() throws IOException {
      bw.close();
  }
}
