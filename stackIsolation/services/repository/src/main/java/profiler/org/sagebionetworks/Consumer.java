package profiler.org.sagebionetworks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import org.joda.time.DateTime;
import org.sagebionetworks.StackConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;

//Consumer class that tracks list of Watchers (Observer pattern)
//and sends periodic lists of MetricDatum objects to Amazon's Web Services
public class Consumer implements MetricDatumSubject {
	//AmazonWebServices CloudWatch client
	private static AmazonCloudWatchClient cw;
	
	//string to hold namespace for the metric
	String namespace = "Latency Times For ControllerProfiler";
	
	//PutMetricDataRequest object that holds list of MetricDatums
	PutMetricDataRequest pmdr = new PutMetricDataRequest();
	
	//synchronized list to hold MetricDatums objects	
	private List<MetricDatum> listMDS = Collections.synchronizedList(new ArrayList<MetricDatum>());
	
	//list of watcherss who will be notified with success/failure of all "puts"	
	List<Watcher> listOfWatchers = Collections.synchronizedList(new ArrayList<Watcher>());
	
	//no argument constructor that initializes CloudWatch client
	public Consumer(){
		//let's create our cloud watch client, and set up the credentials
		//BasicAWSCredentials object takes two parameters
		//first is accessKey, second is secretKey
		//StackConfiguration's getIAMUserID returns AccessKey
		//StackConfiguration's getIAMUserKey returns SecretKey
		BasicAWSCredentials credentials = new BasicAWSCredentials
		(StackConfiguration.getIAMUserId(), StackConfiguration.getIAMUserKey());
		cw = new AmazonCloudWatchClient(credentials);
	}
	
	//constructor that takes namespace and preinitialized CloudWatch
	public Consumer(String namespace, AmazonCloudWatchClient cw){
		this.cw = cw;
		this.namespace = namespace;
	}
	
	//method that adds an MetricDatum object to the list
	//method will not add null MetricDatum items
	public void addMetric(MetricDatum addToListMDS) {
		//add method will allow null items
		if(addToListMDS == null){
			return;
		}
		listMDS.add(addToListMDS);
	}
	
	//sendEm method that removes all MetricDatum objects from list
	//adds them to PutMetricDatum Object
	//sends them to CloudWatch, and collects a response from the "put"
	//then notifies any/all Watchers of "put" success/failure
	//returns string sent to Watcher
	public String sendEm(){
		String toReturn = "";
		//collect the MetricDatum objects from the synchronized list
		List<MetricDatum> nextBunchMD = new ArrayList<MetricDatum>();
		synchronized(listMDS){
			nextBunchMD.addAll(listMDS);
			listMDS.clear();
		}
		
		//CloudWatch will throw exception if nothing was in the synchronized list
		if (nextBunchMD.size() == 0){
			//make a dummy MetricDatum and add to list so no exception is thrown
			MetricDatum dummy = new MetricDatum();
			dummy.setMetricName("defaultMetric");
			dummy.setUnit("Milliseconds");
			dummy.setValue(0.0);
			DateTime timestamp = new DateTime();
			Date jdkDate= timestamp.toDate();
			dummy.setTimestamp(jdkDate);
			nextBunchMD.add(dummy);
		}
		
		pmdr.setNamespace(namespace);
		
		//Put will send an error if it holds a list of > 20 objects
		while (nextBunchMD.size() > 0){
			if (nextBunchMD.size() > 20){
				List<MetricDatum> next20MetricDatums = new ArrayList<MetricDatum>();
				for (int i = 0; i < 20; i++){
					next20MetricDatums.add(nextBunchMD.remove(nextBunchMD.size() - 1));
				}
				//next20MetricDatums.addAll(nextBunchMD.size() - 20, nextBunchMD);
				//nextBunchMD.subList(nextBunchMD.size() - 20, nextBunchMD.size() -1).clear();
				pmdr.setMetricData(next20MetricDatums);
				toReturn = sendMetrics(pmdr, cw);
			}
			else{
				pmdr.setMetricData(nextBunchMD);
				toReturn = sendMetrics(pmdr, cw);
				nextBunchMD.clear();
			}
		}
		return toReturn;
	}
	
	//helper method that sends PutMetricDatum Object to CloudWatch
	//method to send list of MetricDatum objects to AWS CloudWatch
	public String sendMetrics(PutMetricDataRequest listForCW, AmazonCloudWatchClient cw){
		String resultsFromCWPut = "";
		try {
			System.out.println("hereiswhatthePut " + listForCW.toString());
			//below is the line that sends to CloudWatch
			cw.putMetricData(listForCW);
			//here we were successful
			DateTime timestamp = new DateTime();
			Date jdkDate= timestamp.toDate();
			resultsFromCWPut = reportSuccess(jdkDate);
		}catch (Exception e1){
			DateTime timestamp = new DateTime();
			Date jdkDate= timestamp.toDate();
			resultsFromCWPut = reportFailure(e1.toString(), jdkDate);
			throw new RuntimeException(e1);
		} finally {
			return resultsFromCWPut;
		}
	}
	
	//reportSuccess returns a string takes timestamp parameter
	public String reportSuccess(Date jdkDate){
		String toReturn = 
			"SUCCESS PutMetricDataRequest was successfully sent at time " 
			+ jdkDate; 
		notifyWatchers(toReturn);
		return toReturn;
	}
	
	//reportFailure returns a string, takes an error message and timestamp
	public String reportFailure(String errorMessage, Date jdkDate){
		String toReturn = "FAILURE " + errorMessage + " at time " + jdkDate;
		notifyWatchers(toReturn);
		return toReturn;
	}
	
	//method that adds a new Watcher to list
	//add method will add a null item to list which we don't want
	//so nothing will happen if a null item is added
	public void registerWatcher(Watcher w){
		if (w == null){
			return;
		}
		listOfWatchers.add(w);
	}
	
	//method that removes a Watcher from the list
	public void removeWatcher(Watcher w){
		listOfWatchers.remove(w);
	}
	
	//helper method informing all Watcher that a "put" was either
	public void notifyWatchers(String message){
		for(Watcher w: listOfWatchers){
			if(!(w instanceof WatcherImpl))
			{
				continue;
			}
			WatcherImpl next = (WatcherImpl)w;
			next.addToQueue(message);
		}
	}
	
	//getter for synchronized list of MetricDatums
	public List<MetricDatum> getListMD(){
		List<MetricDatum> toReturn = new ArrayList<MetricDatum>(listMDS);
		return toReturn;
	}
	
	//getter for List of Watchers
	public List<Watcher> getWatcherList(){
		List<Watcher> toReturn = new ArrayList<Watcher>(listOfWatchers);
		return toReturn;
	}
	
	//getter for CloudWatch client
	public AmazonCloudWatchClient getCW(){
		return cw;
	}
	
	//getter for namespace
	public String getNamespace(){
		return namespace;
	}
	
	//setter for namespace
	public void setNamespace(String namespace){
		this.namespace = namespace;
	}
	
	//setter for cloud watch
	public void setCloudWatch(AmazonCloudWatchClient cw){
		this.cw = cw;
	}
}
