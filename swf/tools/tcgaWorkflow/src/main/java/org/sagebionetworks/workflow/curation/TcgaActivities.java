package org.sagebionetworks.workflow.curation;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.utils.HttpClientHelperException;
import org.sagebionetworks.workflow.UnrecoverableException;

import com.amazonaws.services.simpleworkflow.flow.annotations.Activities;
import com.amazonaws.services.simpleworkflow.flow.annotations.Activity;
import com.amazonaws.services.simpleworkflow.flow.annotations.ActivityRegistrationOptions;
import com.amazonaws.services.simpleworkflow.flow.annotations.ExponentialRetry;

/**
 * @author deflaux
 * 
 */
@Activities(version=TcgaActivities.VERSION) 
@ActivityRegistrationOptions(defaultTaskScheduleToStartTimeoutSeconds = TcgaActivities.ONE_DAY_OF_SECONDS, 
		defaultTaskStartToCloseTimeoutSeconds = TcgaActivities.ONE_DAY_OF_SECONDS,
		defaultTaskList = TcgaActivities.ACTIVITIES_TASK_LIST)
public interface TcgaActivities {

	/**
	 * The one and only task list to use for activitites this workflow
	 */
	static final String ACTIVITIES_TASK_LIST = "TcgaActivities";
	/**
	 * This version should match our sprint version each time we bump it
	 */
	static final String VERSION = "0.11.3"; 
	/**
	 * A really long timeout to be used when you do not have a better idea of an appropriate timeout
	 */
	static final int ONE_DAY_OF_SECONDS = 86400;
	/**
	 * Retries for file downloads
	 */
	static final int NUM_RETRIES = 3;
	/**
	 * First amount of time to wait before retrying
	 */
	static final int INITIAL_RETRY_INTERVAL_SECONDS = 300;
	
	/**
	 * @param datasetId
	 * @param tcgaUrl
	 * @param doneIfExists
	 * @return the layerId of the newly created or updated layer
	 * @throws JSONException 
	 * @throws SynapseException 
	 * @throws HttpClientHelperException 
	 * @throws IOException 
	 * @throws UnrecoverableException 
	 * @throws NoSuchAlgorithmException 
	 * @throws ClientProtocolException 
	 */
	@Activity(version = VERSION)
	@ActivityRegistrationOptions(defaultTaskScheduleToStartTimeoutSeconds = ONE_DAY_OF_SECONDS, defaultTaskStartToCloseTimeoutSeconds = ONE_DAY_OF_SECONDS)
	@ExponentialRetry(initialRetryIntervalSeconds = INITIAL_RETRY_INTERVAL_SECONDS, maximumAttempts = NUM_RETRIES)
	String createMetadata(String datasetId, String tcgaUrl, Boolean doneIfExists) throws ClientProtocolException, NoSuchAlgorithmException, UnrecoverableException, IOException, HttpClientHelperException, SynapseException, JSONException;

	/**
	 * @param layerId
	 * @return the body of the notification message
	 * @throws UnrecoverableException 
	 * @throws JSONException 
	 * @throws SynapseException 
	 */
	@Activity(version = VERSION)
	@ActivityRegistrationOptions(defaultTaskScheduleToStartTimeoutSeconds = ONE_DAY_OF_SECONDS, defaultTaskStartToCloseTimeoutSeconds = ONE_DAY_OF_SECONDS)
	String formulateNotificationMessage(String layerId) throws SynapseException, JSONException, UnrecoverableException;

	/**
	 * @param recipient
	 * @param subject
	 * @param message
	 */
	@Activity(version = VERSION)
	@ActivityRegistrationOptions(defaultTaskScheduleToStartTimeoutSeconds = ONE_DAY_OF_SECONDS, defaultTaskStartToCloseTimeoutSeconds = ONE_DAY_OF_SECONDS)
	void notifyFollowers(String recipient, String subject, String message);

}
