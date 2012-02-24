package org.sagebionetworks.workflow.curation;

import org.sagebionetworks.workflow.Constants;

import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.core.TryCatchFinally;

/**
 * @author deflaux
 * 
 */
public class TcgaWorkflowImpl implements TcgaWorkflow {

	private static final String NOTIFICATION_SUBJECT = "TCGA Workflow Notification ";
	private static final String NOTIFICATION_SNS_TOPIC = ConfigHelper
	.getWorkflowSnsTopic();

	TcgaActivitiesClient client = new TcgaActivitiesClientImpl();

	@Override
	public void addRawTcgaLayer(final String datasetId, final String tcgaUrl, final Boolean doneIfExists) {

		new TryCatchFinally() {

			@Override
			protected void doTry() throws Throwable {

				Promise<String> rawLayerId = client.createMetadata(datasetId, tcgaUrl, doneIfExists);

				if(Constants.WORKFLOW_DONE != rawLayerId.get()) {
					Promise<String> message = client.formulateNotificationMessage(rawLayerId);
					client.notifyFollowers(NOTIFICATION_SNS_TOPIC, NOTIFICATION_SUBJECT, message.get(), message);
				}
			}

			@Override
			protected void doCatch(Throwable e) throws Throwable {
				throw e;
			}

			@Override
			protected void doFinally() throws Throwable {
				// do nothing
			}
		};
	}
}
