package profiler.org.sagebionetworks;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.aspectj.lang.ProceedingJoinPoint;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

//class to test functionality of ControllerProfiler and ControllerProfilerConsumer
public class ControllerProfilerTest {
	//instance variables
	
	//consumer thread, will need a list of MetricDatam objects
	String countMetricName = "MinuteCount";
	String latencyMetricName = "MinuteLatency";
	String metricNamespace = "Count And Latency";
	List<MetricDatum> testList = new ArrayList<MetricDatum>();
	MetricDatum nextMD;
	DateTime timestamp;
	Date jdkDate;
	double startTime = System.nanoTime();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		//now create and add to list, three latency MetricDatum Objects
		nextMD = new MetricDatum();
		nextMD.setMetricName(latencyMetricName);
		nextMD.setValue(System.nanoTime() - startTime);
		nextMD.setUnit("Milliseconds");
		timestamp = new DateTime();
		jdkDate = timestamp.toDate();
		nextMD.setTimestamp(jdkDate);
		testList.add(nextMD);
		
		nextMD = new MetricDatum();
		nextMD.setMetricName(latencyMetricName);
		nextMD.setValue(System.nanoTime() - startTime);
		nextMD.setUnit("Milliseconds");
		timestamp = new DateTime();
		jdkDate = timestamp.toDate();
		nextMD.setTimestamp(jdkDate);
		testList.add(nextMD);
		
		nextMD = new MetricDatum();
		nextMD.setMetricName(latencyMetricName);
		nextMD.setValue(System.nanoTime() - startTime);
		nextMD.setUnit("Milliseconds");
		timestamp = new DateTime();
		jdkDate = timestamp.toDate();
		nextMD.setTimestamp(jdkDate);
		testList.add(nextMD);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	//this will test functionality of ControllerProfiler and Consumer
	//HOWEVER THEY CURRENTLY HAVE NO RELATIONSHIP, AND CONTROLLERpROFILER'S LIST
	//NEVER LEAVES THE CLASS AS A REFERENCE OR ANYTHING ELSE????????
	
	//test the business logic in Consumer's sendMetrics method
	@Test
	public void testSendMetricsForConsumer() throws Exception {
		
		/*
		//create a mock CloudWatch object
		AmazonCloudWatchClient mockCloudWatch = mock(AmazonCloudWatchClient.class);
	
		//create a Consumer with a valid list and mocked CloudWatch client
		Consumer testConsumer = new Consumer();
		
		PutMetricDataRequest testPMDR = new PutMetricDataRequest();
		testPMDR.setNamespace("LatencyNamespace");
		testPMDR.setMetricData(testList);
		
		testConsumer.sendMetrics(testPMDR, mockCloudWatch);	
		testConsumer.cancel();
		verify(mockCloudWatch, times(1)).putMetricData(testPMDR);
		*/
		
	}
	
	/*
	//test the business logic in Consumer's run method
	@Test
	public void testRunForConsumer() throws Exception{
		//create a mock CloudWatch object
		//AmazonCloudWatchClient mockCloudWatch = mock(AmazonCloudWatchClient.class);
		//create a consumer with a valid list, and mock CloudWatch
		//Consumer testConsumer = new Consumer();		
		//testConsumer.init();
		//testConsumer.cancel();
		//verify(mockCloudWatch, atLeastOnce()).putMetricData((PutMetricDataRequest) anyObject());
	}
	
	//test ControllerProfiler's makeMD method
	/*
	@Test
	public void testMakeMDForControllerProfiler() throws Exception{
		String metricNameParameter = "methodNameIsMetricName";
		long latencyInMSParameter = 20000;
		ControllerProfiler testControllerProfiler = new ControllerProfiler();
		MetricDatum seeTheResults = testControllerProfiler.makeMD
			(metricNameParameter, latencyInMSParameter);
		
		assertNotNull(seeTheResults);
		assertEquals(metricNameParameter, seeTheResults.getMetricName());
		assertEquals("Milliseconds", seeTheResults.getUnit());
		assertNotNull(seeTheResults.getTimestamp());
		//assertEquals(seeTheResults.getValue(),latencyInMSParameter);
	}
	
	
	//test ControllerProfiler's doBasicProfiling method
	//@Test
	//public void testDoBasicProfiling() throws Exception{
		//ControllerProfiler testControllerProfiler = new ControllerProfiler();
		//ProceedingJoinPoint mockPJP = mock(ProceedingJoinPoint.class);
		//try {
			//testControllerProfiler.doBasicProfiling(mockPJP);
		//} catch (Throwable e) {
			//throw new RuntimeException(e);
		//}
		//verify(mockPJP, times(1)).getSignature();
		//try {
			//verify(mockPJP, times(1)).proceed();
		//} catch (Throwable e) {
			//throw new RuntimeException(e);
		//}
		//fails line 141 NullPointerException
	//}
	 * 
	 */
}
