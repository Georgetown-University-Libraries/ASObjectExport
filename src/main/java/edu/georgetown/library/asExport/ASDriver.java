package edu.georgetown.library.asExport;

import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;

import edu.georgetown.library.asExport.dspace.AS2DSpaceProperties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ASDriver {
  protected ASParsedCommandLine cmdLine;
  protected ASConnection asConn;
  protected AS2DSpaceProperties prop;
  public static final DateFormat exportDateFormat = new SimpleDateFormat("yyyyMMdd");

  public ASDriver(ASParsedCommandLine cmdLine) throws DataException, FileNotFoundException, IOException, URISyntaxException, ParseException {
      this.cmdLine = cmdLine;
      this.prop = cmdLine.getPropertyFile();
      asConn = new ASConnection(prop);    
  }
  
  public String getObjectUri(int repo, TYPE type, long objid) throws DataException {
      return String.format("%srepositories/%d/%s/%d", prop.getPubService(), repo, type.name(), objid);
  }

  public static void saveXml(Document d, File f) throws TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError, FileNotFoundException, IOException{
      try(FileOutputStream fos = new FileOutputStream(f)) {
          TransformerFactory.newInstance().newTransformer().transform(new DOMSource(d), new StreamResult(fos));          
      }
  }

  public long getStart() {
      return new Date().getTime();
  }
  
  public long getDuration(long start) {
      return getStart() - start;
  }

  public String formatDurationString(long ms) {
      ms = ms/1000;
      return String.format("%d:%02d:%02d", ms / 3600, (ms % 3600) / 60, ms % 60);
  }
  
  public String getDurationString(long start) {
      return formatDurationString(getDuration(start));
  }
}
