package org.sagebionetworks.workflow.curation;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.utils.HttpClientHelperException;
import org.sagebionetworks.workflow.UnrecoverableException;

import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.core.Task;
import com.amazonaws.services.simpleworkflow.flow.core.TryFinally;
import com.amazonaws.services.simpleworkflow.flow.examples.helloworld.HelloWorldActivities;
import com.amazonaws.services.simpleworkflow.flow.examples.helloworld.HelloWorldWorkflow;
import com.amazonaws.services.simpleworkflow.flow.examples.helloworld.HelloWorldWorkflowClient;
import com.amazonaws.services.simpleworkflow.flow.examples.helloworld.HelloWorldWorkflowImpl;
import com.amazonaws.services.simpleworkflow.flow.junit.FlowBlockJUnit4ClassRunner;
import com.amazonaws.services.simpleworkflow.flow.junit.WorkflowTest;

/**
 * @author deflaux
 * 
 */
@RunWith(FlowBlockJUnit4ClassRunner.class)
public class TcgaWorkflowTest {

	private static final String EXPECTED_RESULT = "workflow"
			+ ":createMetadata" + ":formulateNotificationMessage"
			+ ":notifyFollowers";

	private final class TestTcgaActivities implements TcgaActivities {

		String result = "workflow";

		/**
		 * @return the result
		 */
		public String getResult() {
			return result;
		}

		@Override
		public String createMetadata(String datasetId, String tcgaUrl,
				Boolean doneIfExists) throws ClientProtocolException,
				NoSuchAlgorithmException, UnrecoverableException, IOException,
				HttpClientHelperException, SynapseException, JSONException {
			try {
				Thread.sleep(10000);
				// Delay is for the purpose of illustration
			} catch (InterruptedException e) {
			}
			result += ":createMetadata";
			return result;
		}

		@Override
		public String formulateNotificationMessage(String layerId)
				throws SynapseException, JSONException, UnrecoverableException {
			try {
				Thread.sleep(1000);
				// Delay is for the purpose of illustration
			} catch (InterruptedException e) {
			}
			result += ":formulateNotificationMessage";
			return result;
		}

		@Override
		public void notifyFollowers(String recipient, String subject,
				String message) {
			try {
				Thread.sleep(1000);
				// Delay is for the purpose of illustration
			} catch (InterruptedException e) {
			}
			result += ":notifyFollowers";
		}
	}

	/**
     * 
     */
	@Rule
	public WorkflowTest workflowTest = new WorkflowTest();

	private final TcgaWorkflowClientFactory workflowFactory = new TcgaWorkflowClientFactoryImpl();

	private TestTcgaActivities activitiesImplementation;

	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		workflowTest.addWorkflowImplementationType(TcgaWorkflowImpl.class);
		activitiesImplementation = new TestTcgaActivities();
		workflowTest.addActivitiesImplementation(activitiesImplementation);
	}

	/**
	 * Test through generated workflow client. As workflow unit tests run in
	 * dummy workflow context the same client that is used for creation of child
	 * workflows is used.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testThroughClient() throws Exception {
		TcgaWorkflowClient workflow = workflowFactory.getClient();
		Promise<Void> done = workflow.addRawTcgaLayer("datasetId", "tcgaUrl",
				true);
		assertResult(done);
	}

	@Asynchronous
	private void assertResult(Promise<Void> done) {
		Assert.assertEquals(EXPECTED_RESULT, activitiesImplementation
				.getResult());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testThroughClientAssertWithTask() throws Exception {
		TcgaWorkflowClient workflow = workflowFactory.getClient();
		Promise<Void> done = workflow.addRawTcgaLayer("datasetId", "tcgaUrl",
				true);
		new Task(done) {

			@Override
			protected void doExecute() throws Throwable {
				Assert.assertEquals(EXPECTED_RESULT, activitiesImplementation
						.getResult());
			}
		};
	}

	/**
	 * Instantiate workflow implementation object directly. Note that any object
	 * that is part of workflow can be unit tested through direct instantiation.
	 */
	@Test
	public void directTest() {
		final TcgaWorkflow workflow = new TcgaWorkflowImpl();
		new TryFinally() {

			@Override
			protected void doTry() throws Throwable {
				// addRawTcgaLayer returns void so we use TryFinally
				// to wait for its completion
				workflow.addRawTcgaLayer("datasetId", "tcgaUrl", true);
			}

			@Override
			protected void doFinally() throws Throwable {
				Assert.assertEquals(EXPECTED_RESULT, activitiesImplementation
						.getResult());
			}
		};
	}
}
