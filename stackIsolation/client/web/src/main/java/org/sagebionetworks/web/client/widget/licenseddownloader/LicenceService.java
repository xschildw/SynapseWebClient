package org.sagebionetworks.web.client.widget.licenseddownloader;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("license")
public interface LicenceService extends RemoteService {
	
	/**
	 * Gets any lincense agreements that need to be accepted before download access can be given
	 * @param username
	 * @param type
	 * @param objectId
	 * @return a LicenseAgreement if the user needs to accept one for this download 
	 */
	public boolean hasAccepted(String username, String objectUri); 
	
	/**
	 * Tells the system that the use has accepted the license agreement for this type/id
	 * @param username
	 * @param type
	 * @param objectId
	 * @return true if acceptance succeeds
	 */
	public boolean acceptLicenseAgreement(String username, String objectUri);
	
	/**
	 * Logs the uer's download 
	 * @param username
	 * @param objectUri 
	 */
	public void logUserDownload(String username, String objectUri, String fileUri);
	
}
