package edu.georgetown.library.asExport;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ASResource {
    private JSONObject json;
    
    public ASResource(JSONObject json) {
        this.json = json;
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

    
    public String getModDate() {
        return json.containsKey("system_mtime") ? json.get("system_mtime").toString() : "";
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

    public String getDescription() {
        StringBuilder sb = new StringBuilder();
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
                    sb.append(snobj.get("content"));
                }
            }
        }
        return sb.toString();
    }
}
