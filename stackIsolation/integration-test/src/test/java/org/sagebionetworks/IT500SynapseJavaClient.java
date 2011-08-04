package org.sagebionetworks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sagebionetworks.client.Synapse;
import org.sagebionetworks.utils.HttpClientHelper;
import org.sagebionetworks.StackConfiguration;

/**
 * Run this integration test as a sanity check to ensure our Synapse Java Client
 * is working
 * 
 * TODO write more tests!
 * 
 * @author deflaux
 * 
 */
public class IT500SynapseJavaClient {
	private static Synapse synapse = null;

	/**
	 * @throws Exception
	 * 
	 */
	@BeforeClass
	public static void beforeClass() throws Exception {

		synapse = new Synapse();
		synapse.setAuthEndpoint(StackConfiguration.getAuthenticationServiceEndpoint());
		synapse.setRepositoryEndpoint(StackConfiguration.getRepositoryServiceEndpoint());
		synapse.login(StackConfiguration.getIntegrationTestUserOneName(), 
				StackConfiguration.getIntegrationTestUserOnePassword());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testJavaClientGetADataset() throws Exception {
		JSONObject results = synapse.query("select * from dataset limit 10");

		assertTrue(0 <= results.getInt("totalNumberOfResults"));
		
		JSONArray datasets = results.getJSONArray("results");

		if (0 < datasets.length()) {
			int datasetId = datasets.getJSONObject(0)
					.getInt("dataset.id");

			JSONObject dataset = synapse.getEntity("/dataset/" + datasetId);
			assertTrue(dataset.has("annotations"));

			JSONObject annotations = synapse.getEntity(dataset
					.getString("annotations"));
			assertTrue(annotations.has("stringAnnotations"));
			assertTrue(annotations.has("dateAnnotations"));
			assertTrue(annotations.has("longAnnotations"));
			assertTrue(annotations.has("doubleAnnotations"));
			assertTrue(annotations.has("blobAnnotations"));
		}
	}
	
	// TODO change this back to an agreement test
	
	// upload and then download, don't download sawyers dataset
	
	@Test
	public void testJavaClientDownloadLayerFromS3() throws Exception {
		
	    File tempFile = File.createTempFile("integrationTest", ".download");
	    // Delete temp file when program exits.
	    tempFile.deleteOnExit();
		
		JSONObject datasetQueryResults = synapse.query("select * from dataset where name == \"MSKCC Prostate Cancer\"");
		assertEquals(1, datasetQueryResults.getJSONArray("results").length());
		JSONObject datasetQueryResult = datasetQueryResults.getJSONArray("results").getJSONObject(0);
		
		JSONObject agreementQueryResults = synapse.query("select * from agreement where datasetId == " 
				+ datasetQueryResult.getString("dataset.id") + " and eulaId == \""
				+ datasetQueryResult.getString("dataset.eulaId") + "\" and userId == \""
				+ StackConfiguration.getIntegrationTestUserOneName() + "\"");

		// Agree to the eula, if needed
		if(0 == agreementQueryResults.getJSONArray("results").length()) {
			JSONObject agreement = new JSONObject();
			agreement.put("datasetId", datasetQueryResult.getString("dataset.id"));
			agreement.put("eulaId", datasetQueryResult.getString("dataset.eulaId"));
			synapse.createEntity("/agreement", agreement);
		}

		JSONObject layerQueryResults = synapse.query("select * from layer where name == \"QCed phenotypes\" and parentId == \""
				+ datasetQueryResult.getString("dataset.id") + "\"");
		assertEquals(1, layerQueryResults.getJSONArray("results").length());
		
		JSONObject locationsResult = synapse.getEntity("/" 
				+ layerQueryResults.getJSONArray("results").getJSONObject(0).getString("layer.id") 
				+ "/locations");
		JSONArray locations = locationsResult.getJSONArray("results");
		for (int j = 0; j < locations.length(); j++) {
			String locationUri = locations.getJSONObject(j).getString(
					"uri");
			String locationType = locations.getJSONObject(j).getString(
			"type");
			if (locationType.equals("awss3")) {
//				HttpClientHelper.downloadFile(locationUri, tempFile.getAbsolutePath());
			}
		}
		
//		assertTrue(tempFile.isFile());
//		assertTrue(tempFile.canRead());
//		assertTrue(0 < tempFile.length());
	}
}
