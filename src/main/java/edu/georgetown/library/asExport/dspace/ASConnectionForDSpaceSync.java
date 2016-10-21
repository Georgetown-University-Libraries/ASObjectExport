package edu.georgetown.library.asExport.dspace;

import org.apache.http.client.ClientProtocolException;

import java.io.IOException;
import java.net.URISyntaxException;
import org.json.simple.parser.ParseException;

import edu.georgetown.library.asExport.ASConnection;
import edu.georgetown.library.asExport.ASProperties;
import edu.georgetown.library.asExport.DataException;

public class ASConnectionForDSpaceSync extends ASConnection {

  public ASConnectionForDSpaceSync(ASProperties prop) throws ClientProtocolException, URISyntaxException, IOException, ParseException, DataException {
      super(prop);
  }

}
