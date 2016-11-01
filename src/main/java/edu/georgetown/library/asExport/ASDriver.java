package edu.georgetown.library.asExport;

import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;

import edu.georgetown.library.asExport.dspace.AS2DSpaceProperties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class ASDriver {
  protected ASParsedCommandLine cmdLine;
  protected ASConnection asConn;
  protected AS2DSpaceProperties prop;
  public static final DateFormat exportDateFormat = new SimpleDateFormat("YYYYMMdd");

  public ASDriver(ASParsedCommandLine cmdLine) throws DataException, FileNotFoundException, IOException, URISyntaxException, ParseException {
      this.cmdLine = cmdLine;
      this.prop = cmdLine.getPropertyFile();
      asConn = new ASConnection(prop);    
  }
  
  public String getObjectUri(int repo, TYPE type, long objid) throws DataException {
      return String.format("%s/repositories/%d/%s/%d", prop.getPubService(), repo, type.name(), objid);
  }

  public void convertEAD(Document d, File f, int repo, long objid) throws TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError, FileNotFoundException, IOException, DataException{
      InputStream is = this.getClass().getClassLoader().getResourceAsStream("edu/georgetown/library/asExport/eadMetadata.xsl");
      try(FileOutputStream fos = new FileOutputStream(f)) {
          Transformer t = TransformerFactory.newInstance().newTransformer(new StreamSource(is));
          t.setParameter("creator", prop.getProperty("creator", repo));
          t.setParameter("rights", prop.getProperty("rights", repo));
          t.setParameter("author", prop.getProperty("author", repo));
          t.setParameter("uri", getObjectUri(repo, TYPE.resources, objid));
          t.transform(new DOMSource(d), new StreamResult(fos));          
      }
  }

  public void saveEAD(Document d, File f) throws TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError, FileNotFoundException, IOException{
      try(FileOutputStream fos = new FileOutputStream(f)) {
          TransformerFactory.newInstance().newTransformer().transform(new DOMSource(d), new StreamResult(fos));          
      }
  }

  public String dumpEAD(Document d) throws TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError, FileNotFoundException, IOException{
      InputStream is = this.getClass().getClassLoader().getResourceAsStream("edu/georgetown/library/asExport/eadReport.xsl");
      StringWriter sw = new StringWriter();
      TransformerFactory.newInstance().newTransformer(new StreamSource(is)).transform(new DOMSource(d), new StreamResult(sw));
      return sw.toString();
  }

}
