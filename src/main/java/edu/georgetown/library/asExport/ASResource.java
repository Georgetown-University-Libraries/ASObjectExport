package edu.georgetown.library.asExport;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ASResource extends ASObject {
    
    public ASResource(int repo, long objid, JSONObject json, ASConnection asConn) {
        super(repo, objid, json, asConn);
    }
    
    public String getType() {
        return "resource";
    }
    

    @Override
    public Document getXML() throws ClientProtocolException, URISyntaxException, IOException, SAXException, ParserConfigurationException, DataException {
        return asConn.getEADXML(repo, objid);
    }

    @Override
    public void saveFile(FORMAT fmt, File f)
        throws URISyntaxException, ClientProtocolException, IOException, DataException {
    }

    @Override
    public String getXmlFileName() {
        return String.format("ead.%s.xml", objid);
    }
    
}
