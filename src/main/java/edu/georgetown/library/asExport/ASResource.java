package edu.georgetown.library.asExport;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ASResource {
    private JSONObject json;
    private ArrayList<String> subjects;
    private ASConnection asConn;
    
    public ASResource(JSONObject json, ASConnection asConn) {
        this.json = json;
        this.asConn = asConn;
    }
    
    public static JSONArray getArray(JSONObject obj, String key) {
        JSONArray retVal = new JSONArray();
        if (obj.containsKey(key)) {
            if (obj.get(key) instanceof JSONArray) {
                retVal = (JSONArray)obj.get(key); 
            }
        }
        return retVal;
    }
    
    public boolean isPublished() {
        if (!json.containsKey("publish")) return false;
        return Boolean.TRUE.equals(json.get("publish"));
    }
    public String getTitle() {
        return json.containsKey("title") ? json.get("title").toString() : "";
    }

    public String getID(String def) {
        if (!json.containsKey("id_0")) return def;
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<10; i++) {
            String s = String.format("id_%d", i);
            if (json.containsKey(s)) {
                if (i > 0) sb.append(".");
                sb.append(json.get(s).toString().replaceAll("/", "_"));
            } else {
                break;
            }
        }
        return sb.toString();
    }

    public Date getModDate() {
        if (json.containsKey("system_mtime")) {
            String s = json.get("system_mtime").toString().substring(0,10);
            try {
                return new SimpleDateFormat("yyyy-MM-dd").parse(s);
            } catch (ParseException e) {
            }
        }
        return new Date();
    }
    public String getDate() {
        JSONArray darr = getArray(json, "dates");
        for(int i=0; i< darr.size(); i++) {
            JSONObject nobj = (JSONObject)darr.get(i);
            if (!"creation".equals(nobj.get("label"))) continue;
            if (nobj.containsKey("expression")) return nobj.get("expression").toString();
            if (nobj.containsKey("begin") && nobj.containsKey("end")) {
                return nobj.get("begin").toString() + "-" + nobj.get("end").toString(); 
            }
        }
        return "";
        //return json.containsKey("finding_aid_date") ? json.get("finding_aid_date").toString() : "";
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
    public List<String> getSubjects() throws ClientProtocolException, URISyntaxException, IOException {
        if (subjects == null) {
            subjects = new ArrayList<String>();
            JSONArray narr = getArray(json, "subjects");
            for(int i=0; i< narr.size(); i++) {
                JSONObject obj = (JSONObject)narr.get(i);
                String ref = obj.get("ref").toString();
                JSONObject jsub = asConn.getSubject(ref);
                if (!Boolean.TRUE.equals(jsub.get("publish"))) continue;
                subjects.add(jsub.get("title").toString());            
            }
        }
        return subjects;        
    }
}
