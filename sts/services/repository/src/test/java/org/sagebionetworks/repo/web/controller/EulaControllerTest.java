package org.sagebionetworks.repo.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sagebionetworks.repo.manager.TestUserDAO;
import org.sagebionetworks.repo.model.Agreement;
import org.sagebionetworks.repo.model.Dataset;
import org.sagebionetworks.repo.model.Eula;
import org.sagebionetworks.repo.model.Project;
import org.sagebionetworks.repo.model.QueryResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit tests for the Location CRUD operations on Eula entities exposed by the GenericController
 * with JSON request and response encoding.
 * <p>
 * 
 * Note that test logic and assertions common to operations for all DAO-backed
 * entities can be found in the Helpers class. What follows are test cases that
 * make use of that generic test logic with some assertions specific to
 * eulas.
 * 
 * @author deflaux
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class EulaControllerTest {

	private static final String TEST_USER1 = TestUserDAO.TEST_USER_NAME;
	private static final String TEST_USER2 = "testuser2@test.org";
	
	@Autowired
	private ServletTestHelper testHelper;

	private Project project;
	private Eula eula;
	private Dataset dataset;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		testHelper.setUp();
		testHelper.setTestUser(TEST_USER1);

		project = new Project();
		project = testHelper.createEntity(project, null);

		eula = new Eula();
		eula.setName("TCGA Redistribution Use Agreement");
		eula.setAgreement("The recipient acknowledges that the data herein is provided by TCGA and not SageBionetworks and must abide by ...");
		eula = testHelper.createEntity(eula, null);
		
		dataset = new Dataset();
		dataset.setParentId(project.getId());
		dataset.setEulaId(eula.getId());
		dataset = testHelper.createEntity(dataset, null);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		testHelper.tearDown();
	}

	/*************************************************************************************************************************
	 * Happy case tests
	 */

	/**
	 * @throws Exception
	 */
	@Test
	public void testEulaWithLongVerbiage() throws Exception {
		String longAgreement = new String(new char[20])
				.replace(
						"\0",
						"Lorem ipsum vis alia possit dolores an, id quo apeirian consequat. Te usu nihil facilis forensibus, graece populo deserunt vel an. Populo semper eu quo, ne ignota deleniti salutatus mea. Ullum petentium et duo, adhuc detracto vel ei. Disputando delicatissimi et eos, eam no labore mollis,");
		Eula longEula = new Eula();
		longEula.setAgreement(longAgreement);
		longEula = testHelper.createEntity(longEula, null);
		assertEquals(longAgreement, longEula.getAgreement());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testCreateAgreement() throws Exception {

		Agreement agreement = new Agreement();
		agreement.setEulaId(eula.getId());
		agreement.setDatasetId(dataset.getId());

		agreement = testHelper.createEntity(agreement, null);
		
		String query = "select * from agreement where agreement.datasetId == \""
				+ dataset.getId()
				+ "\" and agreement.eulaId == \""
				+ eula.getId()
				+ "\" and agreement.createdBy == \"" + TEST_USER1 + "\"";
		
		QueryResults queryResult = testHelper.query(query);
		assertEquals(1, queryResult.getTotalNumberOfResults());
		assertEquals(1, queryResult.getResults().size());
		Map<String, Object> result = queryResult.getResults().get(0);
		assertEquals(agreement.getId(), result
				.get("agreement.id"));
	}

	/*************************************************************************************************************************
	 * Enforcement tests
	 */

	/**
	 * @throws Exception
	 */
	@Test
	public void testCreateAgreementInvalidUserId() throws Exception {
		testHelper.setTestUser(TEST_USER2);

		// Ensure that users cannot create agreements for other users, they can only create them for themselves
		Agreement agreement = new Agreement();
		agreement.setEulaId(eula.getId());
		agreement.setDatasetId(dataset.getId());
		agreement.setCreatedBy(TEST_USER1);  // this is not the user that is creating the agreement

		try {
			testHelper.createEntity(agreement, null);
			fail("expected exception not thrown");
		}
		catch(ServletTestHelperException ex) {
			assertTrue(ex.getMessage().startsWith("createdBy must be"));
			assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
		}
	}

	/**
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testEnforceUseAgreement() throws Exception {
		testHelper.setTestUser(TEST_USER2);
		try {
			testHelper.getEntity(dataset, null);
			fail("expected exception not thrown");
		}
		catch(ServletTestHelperException ex) {
			assertTrue(ex.getMessage().startsWith("update access is required to obtain an S3Token for entity"));
		}
		
		/*
		// Make a use agreement
		JSONObject eula = helper.testCreateJsonEntity(helper.getServletPrefix()
				+ "/eula", SAMPLE_EULA);

		// Add the use agreement restriction to the dataset
		dataset = helper.testGetJsonEntity(dataset.getString("uri"));
		dataset.put("eulaId", eula.getString("id"));
		JSONObject updatedDataset = helper.testUpdateJsonEntity(dataset);
		assertEquals(eula.getString("id"), updatedDataset.getString("eulaId"));

		// Make another dataset in addition to the one made by setUp and add the eula to it too
		JSONObject dataset2 = helper.testCreateJsonEntity(helper.getServletPrefix()
				+ "/dataset", "{\"eulaId\":\"" + eula.getString("id") + "\", \"parentId\":\"" + project.getString("id") + "\"}");
		JSONObject datasetLocation2 = new JSONObject(LocationControllerTest.SAMPLE_LOCATION)
				.put(NodeConstants.COL_PARENT_ID, dataset2.getString("id"));
		datasetLocation2 = helper.testCreateJsonEntity(helper.getServletPrefix()
				+ UrlHelpers.LOCATION, datasetLocation2.toString());
		helper.addPublicReadOnlyAclToEntity(dataset2);
		
		// Make an agreement for the current user
		helper.testCreateJsonEntity(helper.getServletPrefix() + "/agreement",
				"{ \"datasetId\":\""
						+ dataset.getString("id") + "\", \"eulaId\":\""
						+ eula.getString("id") + "\"}");

		// Change the user from the creator of the dataset to someone else
		helper.useTestUser();
		// The ACL on the dataset has public read so this works
		helper.testGetJsonEntity(dataset.getString("uri"));
		// The ACL on the eula has public read so this works
		helper.testGetJsonEntity(eula.getString("uri"));
		// But the user has not signed the agreement so these does not work
		helper.testGetJsonEntityShouldFail(dataset.getString("uri")+UrlHelpers.LOCATION,
				HttpStatus.FORBIDDEN);
		helper.testGetJsonEntityShouldFail(datasetLocation.getString("uri"),
				HttpStatus.FORBIDDEN);
		helper.testGetJsonEntityShouldFail(layer.getString("uri")+UrlHelpers.LOCATION,
				HttpStatus.FORBIDDEN);
		helper.testGetJsonEntityShouldFail(layerLocation.getString("uri"),
				HttpStatus.FORBIDDEN);
		helper.testGetJsonEntityShouldFail(dataset2.getString("uri")+UrlHelpers.LOCATION,
				HttpStatus.FORBIDDEN);
		helper.testGetJsonEntityShouldFail(datasetLocation2.getString("uri"),
				HttpStatus.FORBIDDEN);
		helper.testQueryShouldFail(
				"select * from location where parentId == \""
						+ dataset.getString("id") + "\"", HttpStatus.FORBIDDEN);
		helper.testQueryShouldFail(
				"select * from location where parentId == \""
						+ layer.getString("id") + "\"", HttpStatus.FORBIDDEN);

		// Make agreement for the first dataset, but not the second
		JSONObject agreement = helper.testCreateJsonEntity(helper
				.getServletPrefix()
				+ "/agreement", "{ \"datasetId\":\""
				+ dataset.getString("id") + "\", \"eulaId\":\""
				+ eula.getString("id") + "\"}");
		assertExpectedAgreementProperties(agreement);

		// Now that the user has signed the agreement, these do work
		helper.testGetJsonEntities(dataset.getString("uri")+UrlHelpers.LOCATION);
		helper.testGetJsonEntity(datasetLocation.getString("uri"));
		helper.testGetJsonEntities(layer.getString("uri")+UrlHelpers.LOCATION);
		helper.testGetJsonEntity(layerLocation.getString("uri"));
		JSONObject datasetLocationQueryResult = helper
				.testQuery("select * from location where location.parentId == \""
						+ dataset.getString("id") + "\"");
		assertEquals(1, datasetLocationQueryResult
				.getInt("totalNumberOfResults"));
		JSONObject layerLocationQueryResult = helper
				.testQuery("select * from location where location.parentId == \""
						+ layer.getString("id") + "\"");
		assertEquals(1, layerLocationQueryResult.getInt("totalNumberOfResults"));

		// These still do not work
		helper.testGetJsonEntityShouldFail(dataset2.getString("uri")+UrlHelpers.LOCATION,
				HttpStatus.FORBIDDEN);
		helper.testGetJsonEntityShouldFail(datasetLocation2.getString("uri"),
				HttpStatus.FORBIDDEN);
		
		// Ensure that this non-admin user can see their agreement for this
		// dataset plus others
		JSONObject queryResult = helper
				.testQuery("select * from agreement where eulaId == \""
						+ eula.getString("id") + "\"");
		assertEquals(2, queryResult.getInt("totalNumberOfResults"));
*/
	}


}
