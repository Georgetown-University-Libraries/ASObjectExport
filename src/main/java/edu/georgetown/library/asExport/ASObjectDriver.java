package edu.georgetown.library.asExport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ASObjectDriver extends ASDriver {

    OutputStream os;
    public ASObjectDriver(ASParsedCommandLine cmdLine)
            throws DataException, FileNotFoundException, IOException, URISyntaxException, ParseException {
        super(cmdLine);
        File outDir = new File("output");
        if (outDir.exists()) {
            FileUtils.deleteDirectory(outDir);
        }
        outDir.mkdirs();
        File rptDir = new File(outDir, "reports");
        rptDir.mkdirs();
        File f = new File(rptDir, "AS.report.csv");
        os = new FileOutputStream(f);
    }

    public static void main(String[] args) {
        try {
            ASCommandLineSpec asCmdLine = new ASCommandLineSpec(ASDriver.class.getName());
            asCmdLine.addRepoTypeObject();
            ASParsedCommandLine cmdLine = asCmdLine.parse(args);
            ASObjectDriver driver = new ASObjectDriver(cmdLine);
            //driver.processRequest();
            driver.processRepos();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (DataException e) {
            e.printStackTrace();
        }
    }

    public void convertEAD(Document d, File f, String creator, String rights, String author, String uri) throws TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError, FileNotFoundException, IOException{
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("edu/georgetown/library/asExport/eadMetadata.xsl");
        try(FileOutputStream fos = new FileOutputStream(f)) {
            Transformer t = TransformerFactory.newInstance().newTransformer(new StreamSource(is));
            t.setParameter("creator", creator);
            t.setParameter("rights", rights);
            t.setParameter("author", author);
            t.setParameter("uri", uri);
            t.transform(new DOMSource(d), new StreamResult(fos));          
        }
    }

    public void saveEAD(Document d, File f) throws TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError, FileNotFoundException, IOException{
        try(FileOutputStream fos = new FileOutputStream(f)) {
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(d), new StreamResult(fos));          
        }
    }

    public void dumpEAD(Document d) throws TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError, FileNotFoundException, IOException{
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("edu/georgetown/library/asExport/eadReport.xsl");
        TransformerFactory.newInstance().newTransformer(new StreamSource(is)).transform(new DOMSource(d), new StreamResult(os));
        System.out.flush();
    }

    public void processRequest() throws DataException, ClientProtocolException, URISyntaxException, IOException {
        int repo   = cmdLine.getRepositoryId();
        long objid = cmdLine.getObjectId();
        TYPE type  = cmdLine.getType();
        
        JSONObject obj = asConn.getPublishedObject(repo, type, objid);
        if (obj != null) {
            System.out.println(String.format("[%d] %s", objid, obj.toString()));
            ASResource res = new ASResource(obj);
            System.out.println("Title         : "+res.getTitle());
            System.out.println("Date          : "+res.getDate());
            System.out.println("Mod Date      : "+res.getModDate());
            System.out.println("Description   : "+res.getDescription());
            System.out.println("");                    
        } else {
            System.out.println(" *** Unpublished ***\n\n");
        }
        
        Document d;
        try {
            d = asConn.getEADXML(repo,  objid);
            saveEAD(d, new File("test.xml"));
            //convertEAD(d, new File("test.fmt.xml"));
            dumpEAD(d);
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

    public void processRepos() throws ClientProtocolException, URISyntaxException, IOException, NumberFormatException, DataException {
        processRepos(prop.getRepositories());
    }
    public void processRepos(int[] repos) throws ClientProtocolException, URISyntaxException, IOException, NumberFormatException, DataException {
        File ingestDir = new File("output/ingest");
        ingestDir.mkdirs();
        DateFormat df = new SimpleDateFormat("YYYYMMdd");
        String dateStr = df.format(new Date());
        for(int irepo: repos){
            String repoName = prop.getRepoHandle(irepo).replaceAll("[/\\s]", "_");
            File repoDir = new File(ingestDir, repoName);
            repoDir.mkdirs();
            
            List<Long> list = asConn.getObjects(irepo, TYPE.resources);
            for(long objid : list) {
                JSONObject obj = asConn.getPublishedObject(irepo, TYPE.resources, objid);
                if (obj == null) continue;
                ASResource asRes = new ASResource(obj);
                String id = asRes.getID(String.format("res_%d", objid));
                System.out.println(id);
                
                File objDir = new File(repoDir, id);
                objDir.mkdirs();
                
                Document d;
                try {
                    d = asConn.getEADXML(irepo,  objid);
                    dumpEAD(d);
                    String reluri  = this.getObjectUri(irepo, TYPE.resources, objid);
                    String creator = prop.getProperty("creator", irepo);
                    String rights  = prop.getProperty("rights", irepo);
                    String author  = prop.getProperty("author", irepo);

                    convertEAD(d, new File(objDir, "dublin_core.xml"), creator, rights, author, reluri);
                    File eadFile = new File(objDir, String.format("ead.%s.%s.pdf", id, dateStr));
                    asConn.saveResourceFile(irepo, objid, FORMAT.pdf, eadFile);
                    
                    File contentsFile = new File(objDir, "contents");
                    try(BufferedWriter bw = new BufferedWriter(new FileWriter(contentsFile))) {
                        bw.write(String.format("%s\tbundle:ORIGINAL\tdescription:HTML Finding Aid", eadFile.getName()));
                    }
                    
                } catch (SAXException e) {
                    System.out.println(" *** " + e.getMessage());
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
    }

}
