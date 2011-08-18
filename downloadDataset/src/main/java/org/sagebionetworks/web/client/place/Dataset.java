package org.sagebionetworks.web.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class Dataset extends Place {
	
	private String token;	
	private Boolean download;

	public Dataset(String token, Boolean download) {
		this.token = token;
		this.download = download;
	}

	public String toToken() {
		String token = this.token;		
		// add optional tokens if they are defined
		if(download != null) token += ";Download:" + download.toString();

		return token;
	}
	
	public Boolean getDownload() {
		return download;
	}
	
	public static class Tokenizer implements PlaceTokenizer<Dataset> {
        @Override
        public String getToken(Dataset place) {
            return place.toToken();
        }

        @Override
        public Dataset getPlace(String token) {
        	String datasetId = token.replaceAll(";.*", "");
        	Boolean download = null;
        	
        	// parse other tokens
	        	String[] subtokens = token.split(";");
	        	// skip first token as it is the datasetId
	        	for(int i=1; i<subtokens.length; i++) {
	        		if(subtokens[i] != null) {
		        		String[] keyValue = subtokens[i].split(":");
		        		// only parse valid subtokens
		        		if(keyValue != null && keyValue.length==2) {	        			
		        			if("Download".equals(keyValue[0]) && "true".equals(keyValue[1]))
	        					download = true;	        			
		        		}
	        		}
	        	}
	        	
	        // TODO: check that this works, this code looks wrong to me but it matches what I see in Layer
        	        	
            return new Dataset(token, download);

        }    }

}
