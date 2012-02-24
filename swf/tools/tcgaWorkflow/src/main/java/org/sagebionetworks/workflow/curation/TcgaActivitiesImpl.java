package org.sagebionetworks.workflow.curation;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.utils.HttpClientHelperException;
import org.sagebionetworks.workflow.Notification;
import org.sagebionetworks.workflow.UnrecoverableException;

import com.amazonaws.services.simpleworkflow.flow.ActivityFailureException;

/**
 * 
 * @author deflaux
 * 
 */
public class TcgaActivitiesImpl implements TcgaActivities {
	
	@Override
	public String createMetadata(String datasetId, String tcgaUrl,
			Boolean doneIfExists) throws ClientProtocolException, NoSuchAlgorithmException, UnrecoverableException, IOException, HttpClientHelperException, SynapseException, JSONException  {
		String rawLayerId = null;
		try {
			rawLayerId = TcgaCuration
					.doCreateSynapseMetadataForTcgaSourceLayer(doneIfExists,
							datasetId, tcgaUrl);
		} catch (SocketTimeoutException e) {
			throw new ActivityFailureException("Communication timeout, try this again");
		}
		return rawLayerId;
	}

	@Override
	public String formulateNotificationMessage(String layerId) throws SynapseException, JSONException, UnrecoverableException {
		return TcgaCuration.formulateLayerCreationMessage(layerId);
	}

	@Override
	public void notifyFollowers(String recipient, String subject, String message) {
		Notification.doSnsNotifyFollowers(ConfigHelper.getSNSClient(), recipient, subject, message);
		
	}
}
