package edu.georgetown.library.as2dspace;

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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ASResourceExtract {
  //https://archivesspace.github.io/archivesspace/doc/file.API.html

  class DataException extends Exception {
      private static final long serialVersionUID = 1L;
      public DataException(String s) {
          super(s);
      }
  }
    
  private String root = "";
  private String pass = "";
  private String user = "";
  private CloseableHttpClient client;
  private String sessionId = null;
  private JSONParser parser = new JSONParser();
  private Properties prop;
  
  public ASResourceExtract(Properties prop) throws ClientProtocolException, URISyntaxException, IOException, ParseException, DataException {
      this.prop = prop;
	  this.root = getProperty("service");
      this.user = getProperty("user");
	  this.pass = getProperty("password");
      client = HttpClients.createDefault();
	  login();
  }
  
  public String getProperty(String name) throws DataException {
      String s = prop.getProperty(name);
      if (s == null) throw new DataException(String.format("Property %s not found", name));
      return s;
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
  
  enum TYPE{resources,accessions,digital_objects;}
  
  public List<Long> getObjects(int repo, TYPE type) throws URISyntaxException, ClientProtocolException, IOException {
	  ArrayList<Long> objects = new ArrayList<>();
	  String url = String.format("%srepositories/%d/%s", root, repo, type.toString());
      URIBuilder uri = new URIBuilder(url);
      uri.addParameter("all_ids", "true");
        
      HttpGet method = new HttpGet();
      method.setURI(uri.build());
      method.addHeader("X-ArchivesSpace-Session", sessionId);
      // Execute the method.
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
        
      HttpGet method = new HttpGet();
      method.setURI(uri.build());
      method.addHeader("X-ArchivesSpace-Session", sessionId);
      // Execute the method.
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
      
      System.out.println("==================================");
      System.out.println(url);
      System.out.println("----------------------------------");
      if (Boolean.TRUE.equals(resultObject.get("publish"))){
          System.out.println(json);
      } else {
          System.out.println(" -- unpublished ");
      }
      System.out.println("==================================");
      return resultObject;
  }

  
  public void processRepos() throws ClientProtocolException, URISyntaxException, IOException, NumberFormatException, DataException {
      for(String repo: getProperty("repositories").split(",")){
          int irepo = Integer.parseInt(repo.trim());
          String handle = getProperty(String.format("handle_%d",irepo));
          System.out.println(String.format("REPO %d -- %s", irepo, handle));
          List<Long> list = getObjects(irepo, TYPE.resources);
          for(long objid : list) {
              getObject(irepo, TYPE.resources, objid);
          }
      }      
  }

  
  public static void main(String[] args) {
    if (args.length < 1) {
        System.err.println("A property file is required");
        System.exit(1);
    }
    String propFile = args[0];
    
	try {
	    Properties prop = new Properties();
	    prop.load(new FileReader(new File(propFile)));
	    ASResourceExtract asData = new ASResourceExtract(prop);
	    asData.processRepos();
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
}
