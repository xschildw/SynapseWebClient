package profiler.org.sagebionetworks;
import static org.junit.Assert.*;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.sagebionetworks.StackConfiguration;
import org.mockito.Mockito;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;

import static org.mockito.Mockito.*;

//unit test for Consumer class
public class ConsumerTest {
	Consumer testConsumer;
	
	MetricDatum testMetricDatumOne;
	MetricDatum testMetricDatumTwo;
	MetricDatum testMetricDatumThree;
	
	WatcherImpl testWatcherOne;
	WatcherImpl testWatcherTwo;
	
	PutMetricDataRequest testpmdr;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		testConsumer = new Consumer();
		
		testMetricDatumOne = new MetricDatum();
		testMetricDatumOne.setMetricName("testMDone");
		testMetricDatumOne.setUnit("Milliseconds");
		testMetricDatumOne.setValue(0.7);
		DateTime timestamp1 = new DateTime();
		Date jdkDate1= timestamp1.toDate();
		testMetricDatumOne.setTimestamp(jdkDate1);
		
		testMetricDatumTwo = new MetricDatum();
		testMetricDatumTwo.setMetricName("testMDtwo");
		testMetricDatumTwo.setUnit("Milliseconds");
		testMetricDatumTwo.setValue(0.8);
		DateTime timestamp2 = new DateTime();
		Date jdkDate2 = timestamp2.toDate();
		testMetricDatumTwo.setTimestamp(jdkDate2);
		
		testMetricDatumThree = new MetricDatum();
		testMetricDatumThree.setMetricName("testMDthree");
		testMetricDatumThree.setUnit("Milliseconds");
		testMetricDatumThree.setValue(0.9);
		DateTime timestamp3 = new DateTime();
		Date jdkDate3 = timestamp3.toDate();
		testMetricDatumThree.setTimestamp(jdkDate3);	
		
		testWatcherOne = new WatcherImpl();
		testWatcherTwo = new WatcherImpl();
		
		testpmdr = new PutMetricDataRequest();
		testpmdr.setNamespace("testNamespace");
	}

	@After
	public void tearDown() throws Exception {
	}
	
	//test to verify default consumer creates a valid CloudWatch object
	@Test
	public void testDefaultConstructorCreatesCW() throws Exception {
		Consumer tempConsumer = new Consumer();
		assertNotNull(tempConsumer.getCW());
	}
	
	//test constructor to make sure it correctly initializes namespace
	//and Amazon CloudWatch object
	@Test
	public void testConstructorInitializations() throws Exception {
		String namespace = "fancyAWSNamespace";
		AmazonCloudWatchClient testcw;		
		BasicAWSCredentials credentials = new BasicAWSCredentials
		(StackConfiguration.getIAMUserId(), StackConfiguration.getIAMUserKey());
		testcw = new AmazonCloudWatchClient(credentials);
		
		Consumer consumerWithParameters = new Consumer(namespace, testcw);
		assertNotNull(consumerWithParameters.getCW());
		assertEquals(namespace, consumerWithParameters.getNamespace());
		assertEquals(testcw, consumerWithParameters.getCW());
	}
	
	//test getListMD method and addMetricMethod
	//also tests addMetric method
	@Test
	public void testAddMetricAndGetListMdMethods() throws Exception {
		List<MetricDatum> listOfResults = testConsumer.getListMD();
		assertNotNull(listOfResults);
		assertEquals(0, listOfResults.size());
		testConsumer.addMetric(testMetricDatumOne);
		testConsumer.addMetric(testMetricDatumTwo);
		testConsumer.addMetric(testMetricDatumThree);
		listOfResults = testConsumer.getListMD();
		assertEquals(3, listOfResults.size());
	}
	
	//test when bad MetricDatum is added to list
	//no error should be thrown, but it should not be added to list
	@Test
	public void testBadMetricDatumWithAdd() throws Exception {
		List<MetricDatum> listOfResults = testConsumer.getListMD();
		MetricDatum badMetricDatum = null;
		int sizeOfListBeforeAdd = listOfResults.size();
		testConsumer.addMetric(badMetricDatum);
		listOfResults = testConsumer.getListMD();
		assertEquals(sizeOfListBeforeAdd,listOfResults.size());
	}
	
	//test registerWatcher method
	@Test
	public void testRegisterWatcherMethod() throws Exception {
		List<Watcher> consumersListOfActiveWatchers  = 
			testConsumer.getWatcherList(); 
		int numWatchersToStart = consumersListOfActiveWatchers.size();
		testConsumer.registerWatcher(testWatcherOne);
		consumersListOfActiveWatchers  = testConsumer.getWatcherList(); 
		assertEquals(numWatchersToStart + 1, consumersListOfActiveWatchers.size());		
	}
	
	//test registerWatcher method for a null WatcherImpl
	@Test
	public void testRegisterBadWatcher() throws Exception {
		List<Watcher> consumersListOfActiveWatchers  = 
			testConsumer.getWatcherList(); 
		int numWatchersToStart = consumersListOfActiveWatchers.size();
		WatcherImpl badWatcher = null;
		testConsumer.registerWatcher(badWatcher);
		consumersListOfActiveWatchers = testConsumer.getWatcherList();
		assertEquals(numWatchersToStart, consumersListOfActiveWatchers.size());
	}
	
	//test removeWatchers method
	@Test
	public void testRemoveWatchersMethod() throws Exception {
		testConsumer.registerWatcher(testWatcherOne);
		testConsumer.registerWatcher(testWatcherTwo);
		List<Watcher> consumersListOfActiveWatchers  = 
			testConsumer.getWatcherList(); 
		int numWatchersToStart = consumersListOfActiveWatchers.size();
		testConsumer.removeWatcher(testWatcherOne);
		consumersListOfActiveWatchers = testConsumer.getWatcherList();
		assertEquals(numWatchersToStart -1, consumersListOfActiveWatchers.size());
	}
	
	//test removing an invalid watcher
	//list should remain unchanged
	@Test
	public void testRemoveInvalidWatcher() throws Exception {
		testConsumer.registerWatcher(testWatcherOne);
		testConsumer.registerWatcher(testWatcherTwo);
		List<Watcher> consumersListOfActiveWatchers  = 
			testConsumer.getWatcherList(); 
		int numWatchersToStart = consumersListOfActiveWatchers.size();
		WatcherImpl badWatcher = new WatcherImpl();
		WatcherImpl nullWatcher = null;
		testConsumer.removeWatcher(badWatcher);
		consumersListOfActiveWatchers = testConsumer.getWatcherList();
		assertEquals(numWatchersToStart, consumersListOfActiveWatchers.size());
		testConsumer.removeWatcher(nullWatcher);
		consumersListOfActiveWatchers = testConsumer.getWatcherList();
		assertEquals(numWatchersToStart, consumersListOfActiveWatchers.size());
	}
	
	//test notifyWatchers method and verify a string is added to each watcher
	@Test
	public void testNotifyWatchersOutcome() throws Exception {
		testConsumer.registerWatcher(testWatcherOne);
		testConsumer.registerWatcher(testWatcherTwo);
		String testMessageForWatchers = "yo did I get in each watcher";
		testConsumer.notifyWatchers(testMessageForWatchers);
		List<Watcher> consumersListOfActiveWatchers  = 
			testConsumer.getWatcherList(); 
		for (Watcher w:consumersListOfActiveWatchers){
			WatcherImpl nextW = (WatcherImpl)w;
			String next = nextW.removeQueueHead();
			assertEquals(testMessageForWatchers, next);
		}
	}
	
	//test reportSuccess method
	@Test
	public void testReportSuccessMethod() throws Exception{
		testConsumer.registerWatcher(testWatcherOne);
		testConsumer.registerWatcher(testWatcherTwo);
		DateTime testTimestamp = new DateTime();
		Date testJdkDate= testTimestamp.toDate();
		String results = testConsumer.reportSuccess(testJdkDate);
		assertEquals("SUCCESS PutMetricDataRequest was successfully sent at time " 
				+ testJdkDate, results);
	}
	
	//test reportFailure method
	@Test
	public void testReportFailureMethod() throws Exception {
		testConsumer.registerWatcher(testWatcherOne);
		testConsumer.registerWatcher(testWatcherTwo);
		DateTime testTimestamp = new DateTime();
		Date testJdkDate= testTimestamp.toDate();
		String fakeErrorMessage = "this is a something broke error message";
		String results = testConsumer.reportFailure(fakeErrorMessage, testJdkDate);
		assertEquals("FAILURE " + fakeErrorMessage + " at time " + testJdkDate, results);
	}
	
	//test sendMetrics for a valid PutMetricDataRequest parameter
	@Test
	public void testSendMetricsForValidPut() throws Exception {
		List<MetricDatum> testListMetricDatum = new ArrayList<MetricDatum>();
		testListMetricDatum.add(testMetricDatumOne);
		testListMetricDatum.add(testMetricDatumTwo);
		testListMetricDatum.add(testMetricDatumThree);
		testpmdr.setMetricData(testListMetricDatum);
		
		//do not want the call to actually go to Amazon Web Services 
		//so will use a mock cloud watch client
		AmazonCloudWatchClient mockCloudWatch = mock(AmazonCloudWatchClient.class);
		String results = testConsumer.sendMetrics(testpmdr, mockCloudWatch);
		verify(mockCloudWatch).putMetricData(testpmdr);
		assertEquals('S', results.charAt(0));
	}
	
	//test sendMetrics with a invalid PutMetricDataRequest parameter
	@Test
	public void testSendMetricsForInvalidParameter() throws Exception {
		PutMetricDataRequest nullPmdr = null;
		AmazonCloudWatchClient mockCloudWatch = mock(AmazonCloudWatchClient.class);
		String results = testConsumer.sendMetrics(nullPmdr, mockCloudWatch);
		assertEquals('F', results.charAt(0));
	}
	
	//test sendEm method's return string
	@Test
	public void testSendEmReturnString() throws Exception {
		//need to make a consumer that has a mocked cloud watch client
		String testNamespace = "testNamespace";
		AmazonCloudWatchClient mockCloudWatch = mock(AmazonCloudWatchClient.class);
		Consumer sendEmConsumer = new Consumer(testNamespace, mockCloudWatch);
		
		sendEmConsumer.addMetric(testMetricDatumOne);
		//synchronized list now has one legitimate metricDatum 
		String results = sendEmConsumer.sendEm();
		verify(mockCloudWatch).putMetricData((PutMetricDataRequest) anyObject());
		assertEquals('S', results.charAt(0));
	}
	
	//test to verify you still receive a correct return string when
	//synchronized list is empty
	@Test
	public void testReturnStringForSendEmWithEmptyList() throws Exception {
		//need to make a consumer that has a mocked cloud watch client
		String testNamespace = "testNamespace";
		AmazonCloudWatchClient mockCloudWatch = mock(AmazonCloudWatchClient.class);
		Consumer sendEmConsumer = new Consumer(testNamespace, mockCloudWatch);
		
		//verify list is currently empty
		List<MetricDatum> testList = sendEmConsumer.getListMD();
		assertEquals(0, testList.size());
		String results = sendEmConsumer.sendEm();
		verify(mockCloudWatch).putMetricData((PutMetricDataRequest) anyObject());
		assertEquals('S', results.charAt(0));
	}
	
	//test to verify you recieve a failure string when "Put" to CloudWatch fails
	@Test
	public void testInvalidSendEmShouldFail() throws Exception {
		//need to make a consumer with an invalid cloud watch client
		String testNamespace = "testNamespace";
		AmazonCloudWatchClient nullCloudWatch = null;
		Consumer sendEmConsumer = new Consumer(testNamespace, nullCloudWatch);	
		String results = sendEmConsumer.sendEm();
		assertEquals('F', results.charAt(0));
	}
}
