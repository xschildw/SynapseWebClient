package org.sagebionetworks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.Helpers.ExternalProcessResult;

/**
 * TODO invoke our python unit tests here
 * 
 * TODO consider switching the existing handful of tests to PyUnit
 * http://pyunit.sourceforge.net/
 * 
 * @author deflaux
 * 
 */
public class IT600SynapsePythonClientNoBamboo {

	@Test
	public void testPythonClient() throws Exception {
	String cmd[] = {
			Helpers.getPython27Path(),
			"target/non-java-dependencies/synapse/client.py"};
	ExternalProcessResult result = Helpers.runExternalProcess(cmd);
	
	String results[] = result.getStderr().split("\n");	
	assertTrue(results[results.length -1].endsWith("OK"));
	}
}
