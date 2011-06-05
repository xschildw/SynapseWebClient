package org.sagebionetworks;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sagebionetworks.Helpers.ExternalProcessResult;

/**
 * Run this integration test to load in some data
 * 
 * @author deflaux
 *
 */
public class IT015DatasetMetadataLoaderNoBamboo {

	/**
	 * @throws Exception
	 */
	@Test
	public void testDatasetMetadataLoader() throws Exception {
		String cmd[] = {
				Helpers.getPython27Path(),
				"target/non-java-dependencies/datasetCsvLoader.py",
				"--fakeLocalData",
				"--serviceEndpoint",
				Helpers.getRepositoryServiceBaseUrl(),
				"--datasetsCsv",
				"target/non-java-dependencies/AllDatasets.csv",
				"--layersCsv",
				"target/non-java-dependencies/AllDatasetLayerLocations.csv" };
		ExternalProcessResult result = Helpers.runExternalProcess(cmd);
		assertEquals("", result.getStderr());
	}

}
