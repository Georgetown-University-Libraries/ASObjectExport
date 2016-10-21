package edu.georgetown.library.asExport;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ASConnection {
  //https://archivesspace.github.io/archivesspace/doc/file.API.html

  private String root = "";
  private String pass = "";
  private String user = "";
  private CloseableHttpClient client;
  private String sessionId = null;
  private JSONParser parser = new JSONParser();
  private ASProperties prop;
  
  public ASConnection(ASProperties prop) throws ClientProtocolException, URISyntaxException, IOException, ParseException, DataException {
      this.prop = prop;
	  this.root = prop.getService();
      this.user = prop.getUser();
	  this.pass = prop.getPassword();
      client = HttpClients.createDefault();
	  login();
  }
  
  public boolean login() throws URISyntaxException, ClientProtocolException, IOException, ParseException {
    JSONParser parser = new JSONParser();
    HttpPost loginmethod = new HttpPost();
    String loginurl = String.format("%susers/%s/login", root, user);
    URIBuilder loginuri = new URIBuilder(loginurl);
    loginuri.addParameter("password", pass);
    
    loginmethod.setURI(loginuri.build());
    CloseableHttpResponse loginresp = client.execute(loginmethod);
    
    String json = EntityUtils.toString(loginresp.getEntity(), "UTF-8");
    JSONObject loginObject = (JSONObject)parser.parse(json);
    sessionId = loginObject.get("session").toString();
    return true;
  }
  
  public HttpGet makeGetRequest(URIBuilder uri) throws URISyntaxException {
      HttpGet method = new HttpGet();
      method.setURI(uri.build());
      method.addHeader("X-ArchivesSpace-Session", sessionId);
      return method;      
  }
  
  public List<Long> getObjects(int repo, TYPE type) throws URISyntaxException, ClientProtocolException, IOException {
	  ArrayList<Long> objects = new ArrayList<>();
	  String url = String.format("%srepositories/%d/%s", root, repo, type.toString());
      URIBuilder uri = new URIBuilder(url);
      uri.addParameter("all_ids", "true");
        
      HttpGet method = makeGetRequest(uri);
      CloseableHttpResponse resp = client.execute(method);
      
      String json = EntityUtils.toString(resp.getEntity(), "UTF-8");

      try {
          Object resultObject = parser.parse(json);

          if (resultObject instanceof JSONArray) {
              JSONArray array=(JSONArray)resultObject;
              for (Object object : array) {
                  objects.add((Long)object);
              }
          }
      } catch(Exception e) {
    	  e.printStackTrace();
      }  
      method.releaseConnection();
      return objects;
  }

  public JSONObject getObject(int repo, TYPE type, long objid) throws URISyntaxException, ClientProtocolException, IOException {
      String url = String.format("%srepositories/%d/%s/%d", root, repo, type.toString(), objid);
      URIBuilder uri = new URIBuilder(url);
        
      HttpGet method = makeGetRequest(uri);
      CloseableHttpResponse resp = client.execute(method);
      
      String json = EntityUtils.toString(resp.getEntity(), "UTF-8");

      JSONObject resultObject = null;
      try {
         resultObject = (JSONObject)parser.parse(json);
      } catch(Exception e) {
          e.printStackTrace();
      } finally {
          method.releaseConnection();          
      }
      return resultObject;
  }

  public JSONObject getPublishedObject(int repo, TYPE type, long objid) throws URISyntaxException, ClientProtocolException, IOException {
      JSONObject resultObject = getObject(repo, type, objid);
      if (Boolean.TRUE.equals(resultObject.get("publish"))) {
          return resultObject;
      }
      return null;
  }
  
  public int[] getReposToProcess(String paramRepo) throws DataException {
      if (paramRepo == null) return prop.getRepositories();
      if (paramRepo.isEmpty()) return prop.getRepositories();
      return ASProperties.getIntList("The repos parameter", paramRepo);
  }
  
  
  public void processRepos() throws ClientProtocolException, URISyntaxException, IOException, NumberFormatException, DataException {
      processRepos(prop.getRepositories());
  }
  public void processRepos(int[] repos) throws ClientProtocolException, URISyntaxException, IOException, NumberFormatException, DataException {
      for(int irepo: repos){
          //String handle = prop.getRepoHandle(irepo);
          //System.out.println(String.format("REPO %d -- %s", irepo, handle));
          List<Long> list = getObjects(irepo, TYPE.resources);
          for(long objid : list) {
              JSONObject obj = getPublishedObject(irepo, TYPE.resources, objid);
              if (obj == null) continue;
              ASResource res = new ASResource(obj);
              System.out.println("Title         : "+res.getTitle());
              System.out.println("Date          : "+res.getDate());
              System.out.println("Mod Date      : "+res.getModDate());
              System.out.println("Description   : "+res.getDescription());
              System.out.println("");
          }
      }      
  }
  
}
