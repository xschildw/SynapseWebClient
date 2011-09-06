package profiler.org.sagebionetworks;

import java.util.LinkedList;
import java.util.Observer;
import java.util.Observable;
import java.util.Queue;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//class that implements Observer portion of the the Observable pattern
//it's job is to receive and track strings in a bounded queue
//queue holds most recent updates, not oldest
public class WatcherImpl implements Watcher{
	
	//max size of success/failure strings maintained at any one time
	public static final int MAX_NUM_MESSAGES = 20;
	
	//need the consumer, which gets created in bean factory
	@Autowired
	Consumer consumer;
	
	//bounded queue holding strings
	//there will only ever be one consumer, so this does not need any synchronization
	Queue<String> updateStringsList = new LinkedList<String>();
	
	//default constructor, adds Watcher to Consumer's list of watchers
	public WatcherImpl(){
		//consumer.registerWatcher(this);
	}
	
	//constructor that takes Queue of strings
	public WatcherImpl(Queue<String> q){
		this.updateStringsList = q;
		consumer.registerWatcher(this);
	}
	
	//method to add ourselves/this object to the consumer's list of watchers
	public void addWatcherToConsumerList(){
		this.consumer.registerWatcher(this);
	}
	
	//update method that gets the new success/failure data
	//and stores them in queue
	public void update(String message) {
		addToQueue(message);
	}
	
	//method to add success/failure items to queue, and 
	//handle any size requirements
	//also handles invalid strings (null strings)
	public void addToQueue(String message){
		//check for null string
		if (message == null){
			return;
		}
		//if queue is full, must remove head and then add
		if (updateStringsList.size() >= MAX_NUM_MESSAGES){
				updateStringsList.poll();
				updateStringsList.offer(message);
		}
		else{
			updateStringsList.offer(message);
		}
	}
	
	//method to return the head message of a queue
	public String removeQueueHead(){
		if (updateStringsList.size() == 0){
			return "queue was empty";
		}
		String next = updateStringsList.poll();
		return next;
	}
	
	//getter for queue
	public Queue<String> getUpdates(){
		return updateStringsList;
	}
	
	//getter for size of bounded queue
	public int getMaxAllowedSizeOfQueue(){
		return MAX_NUM_MESSAGES;
	}
	
	//setter for consumer for bean
	public void setConsumer(Consumer c){
		this.consumer = c;
	}
	
	//getter for consumer
	public Consumer getConsumer(){
		return this.consumer;
	}
}

