package edu.georgetown.library.asExport;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ASAccession extends ASObject {
    
    public ASAccession(int repo, long objid, JSONObject json, ASConnection asConn) {
        super(repo, objid, json, asConn);
    }
    
    public String getType() {
        return "accession";
    }
    
    public List<String> getDescription() {
        ArrayList<String> ret = new ArrayList<>();
        JSONArray narr = getArray(json, "notes");
        for(int i=0; i< narr.size(); i++) {
            JSONObject nobj = (JSONObject)narr.get(i);
            if (!Boolean.TRUE.equals(nobj.get("publish"))) continue;
            if (!"scopecontent".equals(nobj.get("type"))) continue;
            JSONArray snarr = getArray(nobj, "subnotes");
            for(int j=0; j< snarr.size(); j++) {
                JSONObject snobj = (JSONObject)snarr.get(j);
                if (!Boolean.TRUE.equals(snobj.get("publish"))) continue;
                if (snobj.containsKey("content")) {
                    ret.add(snobj.get("content").toString().replaceAll("\\s+", " "));
                }
            }
        }
        return ret;
    }

    public JSONArray getSubjectArray() {
        return getArray(json, "subjects");
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
            Element unitdate  = addEADElement(did, "unitid", json.get("create_time").toString());
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
        return String.format("accession.%s.xml", objid);
    }
    
}
