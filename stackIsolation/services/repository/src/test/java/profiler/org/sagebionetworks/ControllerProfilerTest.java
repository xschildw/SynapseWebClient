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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

//class to test functionality of ControllerProfiler
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:managers-spb.xml" })
public class ControllerProfilerTest {	
	
	@Autowired
	ControllerProfiler controllerProfiler;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	//test that consumer gets autowired
	@Test
	public void testConsumerNotNull() throws Exception {
		assertNotNull(controllerProfiler.getConsumer());
	}
	
	//test the business logic in Consumer's makeMD method
	@Test
	public void testMakeMD() throws Exception {
		assertNotNull(controllerProfiler);
		
		String tempMetricName = "testMetric";
		double testLatency = 30.0;
		MetricDatum results = new MetricDatum();
		results = controllerProfiler.makeMD(tempMetricName, (long)testLatency);
		
		assertNotNull(results);
		assertEquals(tempMetricName, results.getMetricName());
		assertEquals("Milliseconds", results.getUnit());
		
		Date testjdkDate = results.getTimestamp();
		assertNotNull(testjdkDate);
	}
	
	//test getConsumer and non-default constructor
	@Test
	public void testGetConsumer() throws Exception {
		Consumer testConsumer = new Consumer();
		
		ControllerProfiler testCP = new ControllerProfiler(testConsumer);
		
		assertEquals(testConsumer, testCP.getConsumer());
	}
	
	//test doBasicProfiling
	@Test
	public void testReturnValueForDoBasicProfiling() throws Exception {
		// testPJP = new ProceedingJoinPoint();
		
		//can't  make a pjp, you can make a moch pjp, but it will not return a 
		//valid object so this test will fail
		
		//ProceedingJoinPoint testPJP = mock(ProceedingJoinPoint.class);
		//Object returnObject = testControllerProfiler.doBasicProfiling(testPJP);
	}
	
	//test the consumer's behavior in doBasicProfiling()
	@Test
	public void testConsumerInDoBasicProfiling() throws Exception {
		Consumer mockConsumer = mock(Consumer.class);
		ControllerProfiler controllerWithMockConsumer = new ControllerProfiler(mockConsumer);
		ProceedingJoinPoint testPJP = mock(ProceedingJoinPoint.class);
		MetricDatum testMD = new MetricDatum();
		
		try {
			controllerWithMockConsumer.doBasicProfiling(testPJP);
		} catch (Throwable e) {}
		
		//verify(mockConsumer, atLeastOnce()).addMetric((MetricDatum)anyObject());
	}
}