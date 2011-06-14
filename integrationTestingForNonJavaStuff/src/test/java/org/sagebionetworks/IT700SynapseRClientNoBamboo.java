package org.sagebionetworks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.sagebionetworks.Helpers.ExternalProcessResult;

/**
 * TODO 
 *  - get R CMD check to pass
 *  - fix uri prefix stuff in R client
 *  - fix R unit tests that are incorrectly stubbed and therefore running as integration tests
 * 
 * @author deflaux
 * 
 */
public class IT700SynapseRClientNoBamboo {

	/**
	 * @throws Exception
	 */
	@Test
	public void testBuildRClient() throws Exception {
		String cmd[] = { Helpers.getRPath(), "CMD", "build",
				"target/non-java-dependencies/synapseRClient" };
		ExternalProcessResult result = Helpers.runExternalProcess(cmd);
		assertEquals("", result.getStderr());
	}

	/**
	 * This fails due to dependencies upon pdflatex, etc.
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testCheckRClient() throws Exception {
		String cmd[] = { Helpers.getRPath(), "CMD", "check",
				"target/non-java-dependencies/synapseRClient" };
		ExternalProcessResult result = Helpers.runExternalProcess(cmd);
		assertEquals("", result.getStderr());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testInstallRClient() throws Exception {
		String cmd[] = { Helpers.getRPath(), "CMD", "install",
				"target/non-java-dependencies/synapseRClient" };
		ExternalProcessResult result = Helpers.runExternalProcess(cmd);
		assertTrue(0 <= result.getStderr().indexOf("DONE"));
	}

	/**
	 * TODO fix R client to remove /auth/v1 and /repo/v1 from its settings so
	 * that these can be our variables for servlet endpoints
	*
	 * @throws Exception
	 */
	@Test
	public void testRunRUnitTests() throws Exception {
		String cmd[] = {
				Helpers.getRPath(),
				"-e",
				"library(synapseClient)",
				"-e",
				"synapseAuthServiceHostName(host='http://localhost:8080/services-authentication-0.4-SNAPSHOT')",
				"-e",
				"sessionToken(session.token='"
						+ Helpers.getIntegrationTestUser() + "')",
				"-e",
				"synapseRepoServiceHostName(host='http://localhost:8080/services-repository-0.4-SNAPSHOT')",
				"-e",
				"synapseClient:::.test()" };
		ExternalProcessResult result = Helpers.runExternalProcess(cmd);
		assertTrue(0 <= result.getStdout().indexOf(" 0 errors, 0 failures"));
	}

	/**
	 * TODO fix R client to remove /auth/v1 and /repo/v1 from its settings so
	 * that these can be our variables for servlet endpoints
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRunRIntegrationTests() throws Exception {
		String cmd[] = {
				Helpers.getRPath(),
				"-e",
				"library(synapseClient)",
				"-e",
				"synapseAuthServiceHostName(host='http://localhost:8080/services-authentication-0.4-SNAPSHOT')",
				"-e",
				"sessionToken(session.token='"
						+ Helpers.getIntegrationTestUser() + "')",
				"-e",
				"synapseRepoServiceHostName(host='http://localhost:8080/services-repository-0.4-SNAPSHOT')", 
				"-e",
				"synapseClient:::.integrationTest()" };
		ExternalProcessResult result = Helpers.runExternalProcess(cmd);
		assertTrue(0 <= result.getStdout().indexOf(" 0 errors, 0 failures"));
	}
}
