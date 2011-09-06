package profiler.org.sagebionetworks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.aspectj.lang.Signature;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.joda.time.DateTime;
import org.sagebionetworks.repo.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.MetricDatum;

//class responsible for adding MetricDatum objects to a synchronized list
//MD objects represent latency times for DefaultController methods 
@Aspect
public class ControllerProfiler {
	//constant for nanosecond conversion to ms
	private static final long NANOSECOND_PER_MILLISECOND = 1000000L;
	
	//this is the log returned by LogFactory for ControllerProfiler
	//will print the timestamp, level, message, latency in ms, etc
	static private Log log = LogFactory.getLog(ControllerProfiler.class);
	
	@Autowired
	Consumer consumer;
	
	public ControllerProfiler(){
	}

	//constructor
	public ControllerProfiler(Consumer consumer){
		this.consumer = consumer;
	}	
	
	//this is the Pointcut which handles all joinPoints for our Aspect
	//@Around line represents what's included for our joinPoints (class/package)
	@Around("execution(* org.sagebionetworks.repo.web.controller.DefaultController.*(..))")
	public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
		//parameter is a ProceedingJoinPoint, Spring object used
		//to expose the proceed() method to run wrapped methods	
		//the proceed() method call will return an Object
		
		//get signature of current thread that is at the ProceedingJoinPoint
		//signature will give us the methodName which will become the 
		//CloudWatch metric name
		Signature signature = pjp.getSignature();
		String methodNameForMetric = signature.getName();
		
		long start = System.nanoTime();	//collect method start time
		Object results = pjp.proceed(); // runs the method
		long end = System.nanoTime();	//collect method end time
		//converting from nanoseconds to milliseconds
		long timeMS = (end - start) /NANOSECOND_PER_MILLISECOND;
		
		//use our latency time to make a MetricDatum, and
		//add to synchronized list
		MetricDatum addToListMDS = makeMD(methodNameForMetric, timeMS);
		consumer.addMetric(addToListMDS);
		
		//in configuration file log is set to DEBUG
		log.debug("let's see our MD  " + addToListMDS.toString());
		
		//must return whatever method returned
		return results;
	}
	
	//method that takes a metric name and latency and conversts
	//to MetricDatum object
	public MetricDatum makeMD(String metricName, long latency){
		MetricDatum nextMD = new MetricDatum();
		nextMD.setMetricName(metricName);
		nextMD.setUnit("Milliseconds");
		DateTime timestamp = new DateTime();
		Date jdkDate= timestamp.toDate();
		nextMD.setTimestamp(jdkDate);
		nextMD.setValue((double) latency);
		return nextMD;
	}
	
	//setter for consumer
	public void setConsumer(Consumer consumer){
		this.consumer = consumer;
	}
	
	//getter for consumer
	public Consumer getConsumer(){
		return this.consumer;
	}
}
