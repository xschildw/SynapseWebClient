package org.sagebionetworks.workflow.curation;

import org.sagebionetworks.workflow.Constants;

import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
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

	private TcgaActivitiesClient client;

	/**
	 * Default constructor
	 */
	public TcgaWorkflowImpl() {
		client = new TcgaActivitiesClientImpl();
	}

	/**
	 * Constructor for unit testing or if we are using Spring to wire this up
	 * 
	 * @param client
	 */
	public TcgaWorkflowImpl(TcgaActivitiesClient client) {
		this.client = client;
	}

	@Override
	public void addRawTcgaLayer(final String datasetId, final String tcgaUrl,
			final Boolean doneIfExists) {

		new TryCatchFinally() {

			@Override
			protected void doTry() throws Throwable {
				Promise<String> layerId = client.createMetadata(datasetId,
						tcgaUrl,

						doneIfExists);
				notifyFollowersIfApplicable(layerId);
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

	@Asynchronous
	private void notifyFollowersIfApplicable(Promise<String> layerId) {
		if (!Constants.WORKFLOW_DONE.equals(layerId.get())) {
			Promise<String> message = client
					.formulateNotificationMessage(layerId);
			client.notifyFollowers(NOTIFICATION_SNS_TOPIC,
					NOTIFICATION_SUBJECT, message.get(), message);
		}
	}
}
