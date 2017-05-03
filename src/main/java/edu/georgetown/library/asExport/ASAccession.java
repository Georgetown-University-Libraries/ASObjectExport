package edu.georgetown.library.asExport;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ASAccession extends ASObject {
    
    public ASAccession(int repo, long objid, JSONObject json, ASConnection asConn) {
        super(repo, objid, json, asConn);
    }
    
    @Override
    public String getID(String def) {
        return "accession." + super.getID(def);    
    }
    public String getType() {
        return "accession";
    }
    
    public static final String EAD = "urn:isbn:1-931666-22-9";

    @Override
    public Document getXML() throws ClientProtocolException, URISyntaxException, IOException, SAXException, ParserConfigurationException, DataException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document d = dbf.newDocumentBuilder().newDocument();
        
        Element root = d.createElementNS(EAD, "ead"); 
        d.appendChild(root);
        root.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:schemaLocation", "urn:isbn:1-931666-22-9 http://www.loc.gov/ead/ead.xsd");
        Element eadheader     = addEADElement(root, "eadheader", "");
        @SuppressWarnings("unused")
        Element eadid         = addEADElement(eadheader, "eadid", "");
        Element filedesc      = addEADElement(eadheader, "filedesc", "");
        Element titlestmt     = addEADElement(filedesc, "titlestmt", "");
        @SuppressWarnings("unused")
        Element titleproper   = addEADElement(titlestmt, "titleproper", getTitle());
        Element archdesc      = addEADElement(root, "archdesc", "");
        archdesc.setAttribute("level", "collection");
        Element did           = addEADElement(archdesc, "did", "");
        @SuppressWarnings("unused")
        Element unittitle     = addEADElement(did, "unittitle", getTitle());
        @SuppressWarnings("unused")
        Element unitid        = addEADElement(did, "unitid", getID("n/a"));
        if (json.containsKey("create_time")) {
            @SuppressWarnings("unused")
            Element unitdate  = addEADElement(did, "unitdate", json.get("create_time").toString());
        }
        Element scopecontent  = addEADElement(archdesc, "scopecontent", "");
        @SuppressWarnings("unused")
        Element p             = addEADElement(scopecontent, "p", getDescriptionStr());
        Element controlaccess = addEADElement(archdesc, "controlaccess", "");

        for(String s: getSubjects()) {
            @SuppressWarnings("unused")
	    Element subject   = addEADElement(controlaccess, "subject", s);
        }
        return d;
    }

    public static Element addEADElement(Element parent, String tag, String value) {
        Document d = parent.getOwnerDocument();    
        Element elem = d.createElementNS(EAD, tag);
        parent.appendChild(elem);
        elem.appendChild(d.createTextNode(value));
        return elem;
    }
    
    
    @Override
    public void saveFile(FORMAT fmt, File f)
        throws URISyntaxException, ClientProtocolException, IOException, DataException {
    }

    @Override
    public String getXmlFileName() {
        return String.format("ead.accession.%s.xml", objid);
    }
    
}
