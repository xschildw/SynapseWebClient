package profiler.org.sagebionetworks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;

//class designed to collect MetricDatum objects and send them
//to cloudWatch each minute
public class Consumer implements Runnable{

	//instance variables
	private static AmazonCloudWatchClient cw;	//cloudWatch client
	//volatile boolean to control how long each thread runs
	private volatile boolean cancelled = false;
	//string to hold namespace for the metric
	String namespace = "Latency Times For Controller Profiler";
	
	@Autowired
	static List<MetricDatum> LISTMDS = Collections.synchronizedList(new ArrayList<MetricDatum>());
	
	//constructor
	public Consumer(){
		//let's create our cloud watch client, and set up the credentials
		BasicAWSCredentials credentials = new BasicAWSCredentials
			("Dn1Jz8vnlRtygfXriILNWaqfRCCk9Ek++xX87cbf", "AKIAJZJIQ72YK2RSSXLA");
		cw = new AmazonCloudWatchClient(credentials);
	}
		 
	//method to send list of MetricDatum objects to AWS CloudWatch
	public void sendMetrics(PutMetricDataRequest listForCW, AmazonCloudWatchClient cw)
	{
		try 
		{
			//below is the line that sends to CloudWatch
			cw.putMetricData(listForCW);
			//System.out.println(listForCW.toString());
		} 
		catch (Exception e1) 
		{
			throw new RuntimeException(e1);
		}        
	}
	
	//method to kill thread
	public void cancel(){
		cancelled = true;
	}
	
	//method to give access to synchronized list
	public List<MetricDatum> getMetricDatumList(){
		return LISTMDS;
	}
	
	//method that allows you to set the namespace
	public void setNamespace(String namespace){
		this.namespace = namespace;
	}
	
	//method to start the consumer
	public void init(){
		this.run();
	}
	
	//method to ensure consumer is stopped
	public void cleanup(){
		cancel();
	}
	
	public void run()
	{	
		while(!cancelled)
		{
			try 
			{
				Thread.sleep(60000);
			} 
			catch (InterruptedException e1) 
			{
				throw new RuntimeException(e1);
			}
			
			//make and send put object for AmazonCloudWatch
			List<MetricDatum> nextBunchMD = new ArrayList<MetricDatum>();
			synchronized(LISTMDS)
			{
				nextBunchMD.addAll(LISTMDS);
				LISTMDS.clear();
			}
			PutMetricDataRequest nextPut = new PutMetricDataRequest();
			nextPut.setNamespace(namespace);
			nextPut.setMetricData(nextBunchMD);
			sendMetrics(nextPut, cw);
		}
	}
}


