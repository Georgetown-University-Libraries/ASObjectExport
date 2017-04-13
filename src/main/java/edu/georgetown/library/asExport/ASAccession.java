package edu.georgetown.library.asExport;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
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
        return String.format("accession.%s.xml", objid);
    }
    
}
