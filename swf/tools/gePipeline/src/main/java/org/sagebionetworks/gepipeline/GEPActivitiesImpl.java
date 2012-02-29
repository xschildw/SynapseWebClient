package org.sagebionetworks.gepipeline;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import org.sagebionetworks.workflow.Notification;

import com.amazonaws.services.simpleworkflow.flow.core.Settable;

/**
 * 
 */
public class GEPActivitiesImpl implements GEPActivities {

	// Even though we are sending stdout and stderr from the R script to a log
	// file, include a portion of that output in the workflow history for
	// convenience. We only want to dig through the logs if we need to.
	private static final int MAX_SCRIPT_OUTPUT = 10240;

	/**
	 * @param s
	 * @return string url encoded but with + as %20
	 */
	public static String formatAsScriptParam(String s) {
		// so let's try URLEncoding the param.  This means we must URLDecode on the R side
		try {
			// R's URLdecode expects %20 for space, not +
			return URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String processData(String script, String activityInput,
			Settable<String> stdout, Settable<String> stderr) {
		ScriptResult result = ScriptProcessor.doProcess(script, Arrays
				.asList(new String[] { GEPWorkflow.INPUT_DATA_PARAMETER_KEY,
						formatAsScriptParam(activityInput), }));
		stdout.set((MAX_SCRIPT_OUTPUT > result.getStdout().length()) ? result
				.getStdout() : result.getStdout().substring(0,
				MAX_SCRIPT_OUTPUT));
		stderr.set((MAX_SCRIPT_OUTPUT > result.getStderr().length()) ? result
				.getStderr() : result.getStderr().substring(0,
				MAX_SCRIPT_OUTPUT));
		return result.getStringResult(ScriptResult.OUTPUT_JSON_KEY);
	}

	@Override
	public void notifyFollower(String recipient, String subject, String message) {
		Notification.doSnsNotifyFollowers(GEPWorkflowConfigHelper.getSNSClient(),
				recipient, subject, message);
	}
	
}
