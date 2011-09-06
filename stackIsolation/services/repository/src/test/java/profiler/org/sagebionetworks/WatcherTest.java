package profiler.org.sagebionetworks;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Queue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sagebionetworks.repo.model.Nodeable;
import org.sagebionetworks.repo.model.ObjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//unit test, to test functionality of profiler.org.sagebionetworks.Watcher class
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:managers-spb.xml" })
public class WatcherTest {
	@Autowired
	WatcherImpl watcher;
	
	String frontOfQueue = "string one";
	String middleOfQueue = "string two";
	String backOfQueue = "string three";

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
	
	//test if our watcher's consumer is null
	@Test
	public void watchersConsumerNotNull() throws Exception {
		assertNotNull(watcher.getConsumer());
	}
	
	//test add method and verify it adds a String to the queue
	@Test
	public void watcherAddMethodForEmptyQueue() throws Exception {
		assertTrue(watcher.getUpdates().size() == 0);
		watcher.addToQueue(frontOfQueue);
		assertTrue(watcher.getUpdates().size() == 1);
		assertEquals(frontOfQueue, watcher.getUpdates().poll());
	}
	
	//test add method and verify it behaves correctly when queue is not 
	//empty but does have items in it
	@Test
	public void watcherAddMethodForQueueWithTwoItemsInIt() throws Exception {
		assertTrue(watcher.getUpdates().size() == 0);
		watcher.addToQueue(frontOfQueue);
		watcher.addToQueue(middleOfQueue);
		assertTrue(watcher.getUpdates().size() == 2);
		watcher.addToQueue(backOfQueue);
		assertTrue(watcher.getUpdates().size() == 3);
		
		watcher.removeQueueHead();
		watcher.removeQueueHead();
		watcher.removeQueueHead();
	}
	
	//test add method and verify does not add null string
	@Test
	public void watcherAddInvalidStringToQueue() throws Exception {
		assertTrue(watcher.getUpdates().size() == 0);
		String badString = null;
		watcher.addToQueue(badString);
		assertTrue(watcher.getUpdates().size() == 0);
	}
	
	//test add method and verify it behaves correctly when queue is at max desired
	//size
	@Test
	public void watcherAddStringWhenQueueIsFull() throws Exception {
		for (int i = 0; i < 20; i++){
			watcher.addToQueue("" + i);
		}
		assertTrue(watcher.getUpdates().size() == 
			watcher.getMaxAllowedSizeOfQueue());
		watcher.addToQueue("we're over allowed size");
		assertTrue(watcher.getUpdates().size() == 
			watcher.getMaxAllowedSizeOfQueue());
		assertEquals("" + 1, watcher.getUpdates().peek());
		
		//clean up 
		for (int i = 0; i < 20; i++)
		{
			watcher.removeQueueHead();
		}
	}
	
	//test getter for queue method
	@Test
	public void watcherGetUpdates() throws Exception {
		//make a watcher
		//add 3 strings
		//get the list and verify the size of list and each individual string
		assertTrue(watcher.getUpdates().size() == 0);
		watcher.addToQueue(frontOfQueue);
		watcher.addToQueue(middleOfQueue);
		watcher.addToQueue(backOfQueue);
		Queue<String> testResultsForGetUpdates = watcher.getUpdates(); 
		
		assertNotNull(testResultsForGetUpdates);
		assertEquals(3, testResultsForGetUpdates.size());
		assertEquals(frontOfQueue, testResultsForGetUpdates.poll());
		assertEquals(middleOfQueue, testResultsForGetUpdates.poll());
		assertEquals(backOfQueue, testResultsForGetUpdates.poll());
	}
	
	//test update method 
	@Test
	public void watcherUpdateMethod() throws Exception {
		assertTrue(watcher.getUpdates().size() == 0);
		watcher.update(frontOfQueue);
		assertTrue(watcher.getUpdates().size() == 1);
		
		//clean up
		watcher.removeQueueHead();
	}
	
	
	//test removeQueueHead method
	@Test
	public void testRemoveQueueHead() throws Exception {
		assertTrue(watcher.getUpdates().size() == 0);
		watcher.addToQueue(frontOfQueue);
		watcher.addToQueue(middleOfQueue);
		watcher.addToQueue(backOfQueue);
		String next = watcher.removeQueueHead();
		assertEquals(frontOfQueue, next);
		next = watcher.removeQueueHead();
		assertEquals(middleOfQueue, next);
		next = watcher.removeQueueHead();
		assertEquals(backOfQueue, next);		
	}
	
	//test removeQueueHead method when queue is empty
	@Test
	public void testRemoveQueueHeadFromEmptyQueue() throws Exception {
		assertEquals(0, watcher.getUpdates().size());
		String resultsOfRemove = watcher.removeQueueHead();
		assertEquals("queue was empty", resultsOfRemove);
	}
}
