package org.sagebionetworks.repo.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sagebionetworks.repo.manager.TestUserDAO;
import org.sagebionetworks.repo.model.AccessControlList;
import org.sagebionetworks.repo.model.Agreement;
import org.sagebionetworks.repo.model.AuthorizationConstants;
import org.sagebionetworks.repo.model.Dataset;
import org.sagebionetworks.repo.model.Eula;
import org.sagebionetworks.repo.model.Layer;
import org.sagebionetworks.repo.model.LayerTypeNames;
import org.sagebionetworks.repo.model.LocationData;
import org.sagebionetworks.repo.model.LocationStatusNames;
import org.sagebionetworks.repo.model.LocationTypeNames;
import org.sagebionetworks.repo.model.Project;
import org.sagebionetworks.repo.model.QueryResults;
import org.sagebionetworks.repo.model.ResourceAccess;
import org.sagebionetworks.repo.model.AuthorizationConstants.ACCESS_TYPE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit tests for the Location CRUD operations on Eula entities exposed by the
 * GenericController with JSON request and response encoding.
 * <p>
 * 
 * Note that test logic and assertions common to operations for all DAO-backed
 * entities can be found in the Helpers class. What follows are test cases that
 * make use of that generic test logic with some assertions specific to eulas.
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
	private Layer layer;

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
		eula
				.setAgreement("The recipient acknowledges that the data herein is provided by TCGA and not SageBionetworks and must abide by ...");
		eula = testHelper.createEntity(eula, null);

		LocationData externalLocation = new LocationData();
		externalLocation.setType(LocationTypeNames.external);
		externalLocation
				.setPath("http://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/coad/cgcc/unc.edu/agilentg4502a_07_3/transcriptome/unc.edu_COAD.AgilentG4502A_07_3.Level_2.2.0.0.tar.gz");
		List<LocationData> locations = new LinkedList<LocationData>();
		locations.add(externalLocation);

		dataset = new Dataset();
		dataset.setParentId(project.getId());
		dataset.setEulaId(eula.getId());
		dataset.setMd5("33183779e53ce0cfc35f59cc2a762cbd");
		dataset.setLocations(locations);
		dataset = testHelper.createEntity(dataset, null);

		layer = new Layer();
		layer.setParentId(dataset.getId());
		layer.setType(LayerTypeNames.C);
		layer.setMd5("33183779e53ce0cfc35f59cc2a762cbd");
		layer.setLocations(locations);
		layer = testHelper.createEntity(layer, null);
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
	public void testEulasArePubliclyReadable() throws Exception {
		// user 1 created the eula, but user 2 can read it no problem
		testHelper.setTestUser(TEST_USER2);
		Eula eulaFetchedByUser2 = testHelper.getEntity(eula, null);
		assertEquals(eula, eulaFetchedByUser2);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testQueryAgreements() throws Exception {

		Agreement agreement = new Agreement();
		agreement.setEulaId(eula.getId());
		agreement.setDatasetId(dataset.getId());

		testHelper.setTestUser(TEST_USER1);
		Agreement user1Agreement = testHelper.createEntity(agreement, null);

		String query = "select * from agreement where agreement.datasetId == \""
				+ dataset.getId()
				+ "\" and agreement.eulaId == \""
				+ eula.getId()
				+ "\" and agreement.createdBy == \""
				+ TEST_USER1 + "\"";

		QueryResults queryResult = testHelper.query(query);
		assertEquals(1, queryResult.getTotalNumberOfResults());
		assertEquals(1, queryResult.getResults().size());
		Map<String, Object> result = queryResult.getResults().get(0);
		assertEquals(user1Agreement.getId(), result.get("agreement.id"));

		// Note that authorization to make an agreement has nothing to do with
		// authorization to access the dataset, this is okay because
		// authorization to access a dataset takes precedence over whether the
		// eula has been agreed to
		testHelper.setTestUser(TEST_USER2);
		Agreement user2Agreement = testHelper.createEntity(agreement, null);
		assertNotNull(user2Agreement);

		// Ensure that this user can see their agreement for this dataset plus
		// other one, all agreements are public read
		queryResult = testHelper.query("select * from agreement");
		assertEquals(2, queryResult.getTotalNumberOfResults());
		assertEquals(TEST_USER1, queryResult.getResults().get(0).get(
				"agreement.createdBy"));
		assertEquals(TEST_USER2, queryResult.getResults().get(1).get(
				"agreement.createdBy"));
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

		// Ensure that users cannot create agreements for other users, they can
		// only create them for themselves
		Agreement agreement = new Agreement();
		agreement.setEulaId(eula.getId());
		agreement.setDatasetId(dataset.getId());
		agreement.setCreatedBy(TEST_USER1); // this is not the user that is
		// creating the agreement

		try {
			testHelper.createEntity(agreement, null);
			fail("expected exception not thrown");
		} catch (ServletTestHelperException ex) {
			assertTrue(ex.getMessage().startsWith("createdBy must be"));
			assertEquals(HttpStatus.BAD_REQUEST.value(), ex.getHttpStatus());
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testEnforceUseAgreement() throws Exception {
		// Switch to another user and confirm that user cannot read the dataset
		// at all
		testHelper.setTestUser(TEST_USER2);
		try {
			testHelper.getEntity(dataset, null);
			fail("expected exception not thrown");
		} catch (ServletTestHelperException ex) {
			assertTrue(ex.getMessage().startsWith(
					TEST_USER2 + " lacks read access to the requested object."));
		}

		// Add a public read ACL to the project object
		testHelper.setTestUser(TEST_USER1);
		AccessControlList projectAcl = testHelper.getEntityACL(project);
		ResourceAccess ac = new ResourceAccess();
		ac
				.setGroupName(AuthorizationConstants.DEFAULT_GROUPS.AUTHENTICATED_USERS
						.name());
		ac.setAccessType(new HashSet<ACCESS_TYPE>());
		ac.getAccessType().add(ACCESS_TYPE.READ);
		projectAcl.getResourceAccess().add(ac);
		projectAcl = testHelper.updateEntityAcl(project, projectAcl);

		// Now user2 can see the metadata for the dataset, but not its
		// locations
		testHelper.setTestUser(TEST_USER2);
		dataset = testHelper.getEntity(dataset, null);
		assertNull(dataset.getLocations());
		assertEquals(LocationStatusNames.pendingEula, dataset
				.getLocationStatus());

		// Same for the layer in this dataset, no locations
		layer = testHelper.getEntity(layer, null);
		assertNull(layer.getLocations());
		assertEquals(LocationStatusNames.pendingEula, layer.getLocationStatus());

		// TODO test queries

		// Make an agreement for user2
		Agreement agreement = new Agreement();
		agreement.setEulaId(eula.getId());
		agreement.setDatasetId(dataset.getId());
		agreement = testHelper.createEntity(agreement, null);

		// Now user2 can see the locations for the dataset
		dataset = testHelper.getEntity(dataset, null);
		assertEquals(1, dataset.getLocations().size());
		assertEquals(LocationStatusNames.available, dataset.getLocationStatus());

		// And the locations for the layer
		layer = testHelper.getEntity(layer, null);
		assertEquals(1, layer.getLocations().size());
		assertEquals(LocationStatusNames.available, layer.getLocationStatus());
	}

}
