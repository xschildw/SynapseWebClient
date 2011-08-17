package profiler.org.sagebionetworks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.StackConfiguration;
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
	private static final long NANOSECOND_PER_SLEEP = 5000;
	private static AmazonCloudWatchClient cw;	//cloudWatch client
	//volatile boolean to control how long each thread runs
	private volatile boolean cancelled = false;
	//string to hold namespace for the metric
	String namespace = "Latency Times For ControllerProfiler";
	Thread myThread;	//our consumer thread
	
	private List<MetricDatum> listMDS = Collections.synchronizedList(new ArrayList<MetricDatum>());
	
	//constructor
	//should it take a list and cw client?
	public Consumer(){
		//let's create our cloud watch client, and set up the credentials
		//BasicAWSCredentials object takes two aparameters
		//first is accessKey, second is secretKey
		//StackConfiguration's getIAMUserID returns AccessKey
		//StackConfiguration's getIAMUserKey returns SecretKey
		BasicAWSCredentials credentials = new BasicAWSCredentials
		(StackConfiguration.getIAMUserId(), StackConfiguration.getIAMUserKey());
		cw = new AmazonCloudWatchClient(credentials);
	}
		 
	//method to send list of MetricDatum objects to AWS CloudWatch
	public void sendMetrics(PutMetricDataRequest listForCW, AmazonCloudWatchClient cw){
		try {
			System.out.println("hereiswhatthePut " + listForCW.toString());
			//below is the line that sends to CloudWatch
			//cw.putMetricData(listForCW);
		}catch (Exception e1){
			throw new RuntimeException(e1);
		}        
	}
	
	//method to kill thread
	public void cancel(){
		cancelled = true;
	}
	
	//method that allows you to set the namespace
	public void setNamespace(String namespace){
		this.namespace = namespace;
	}
	
	//method to start the consumer
	public void init(){
		myThread = new Thread(this);
		myThread.start(); 
	}
	
	public void run(){	
		while(!cancelled){
			try {
				myThread.sleep(NANOSECOND_PER_SLEEP);
			} catch (InterruptedException e1){
				throw new RuntimeException(e1);
			}
			
			//colect the MetricDatum obects from the synchronized list
			List<MetricDatum> nextBunchMD = new ArrayList<MetricDatum>();
			synchronized(listMDS){
				nextBunchMD.addAll(listMDS);
				listMDS.clear();
			}
			
			//will throw exception if nothing was in the synchronized list
			if (nextBunchMD.size() <= 0){
				//make a dummy MetricDatum and add to list so no exception is thrown
				MetricDatum dummy = new MetricDatum();
				dummy.setMetricName("defaultMetric");
				dummy.setUnit("Milliseconds");
				dummy.setValue(0.0);
				nextBunchMD.add(dummy);
			}
			
			//Put object for Amazon CloudWatch
			PutMetricDataRequest nextPut = new PutMetricDataRequest();
			nextPut.setNamespace(namespace);
			
			//Put will send an error if it holds a list of >20 objects
			while (nextBunchMD.size() > 0){
				if (nextBunchMD.size() > 20){
					List<MetricDatum> next20MetricDatums = new ArrayList<MetricDatum>();
					for (int i = 0; i < 20; i++){
						next20MetricDatums.add(nextBunchMD.remove(nextBunchMD.size() - 1));
					}
					//next20MetricDatums.addAll(nextBunchMD.size() - 20, nextBunchMD);
					//nextBunchMD.subList(nextBunchMD.size() - 20, nextBunchMD.size() -1).clear();
					nextPut.setMetricData(next20MetricDatums);
					sendMetrics(nextPut, cw);
				}
				else{
					nextPut.setMetricData(nextBunchMD);
					sendMetrics(nextPut, cw);
					nextBunchMD.clear();
				}
			}
		}
	}

	public void addMetric(MetricDatum addToListMDS) {
		listMDS.add(addToListMDS);		
	}
}


