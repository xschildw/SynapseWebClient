package org.sagebionetworks.client;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sagebionetworks.Dataset;

/**
 * Unit test for Synapse.
 * @author jmhill
 *
 */
public class SynapseTest {
	
	HttpClientProvider mockProvider = null;
	Synapse synapse;
	
	@Before
	public void before(){
		// The mock provider
		mockProvider = Mockito.mock(HttpClientProvider.class);
		synapse = new Synapse(mockProvider);
	}
	
	@Test
	public void testCreateDataset(){
		Dataset ds = EntityFactory.createNewDataset();

	}

}
