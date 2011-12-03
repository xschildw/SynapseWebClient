package org.sagebionetworks.repo.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sagebionetworks.repo.manager.TestUserDAO;
import org.sagebionetworks.repo.model.Code;
import org.sagebionetworks.repo.model.Dataset;
import org.sagebionetworks.repo.model.Layer;
import org.sagebionetworks.repo.model.LayerTypeNames;
import org.sagebionetworks.repo.model.NodeConstants;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.sagebionetworks.repo.model.Project;
import org.sagebionetworks.repo.model.S3Token;
import org.sagebionetworks.repo.web.ServiceConstants;
import org.sagebionetworks.repo.web.UrlHelpers;
import org.sagebionetworks.repo.web.controller.metadata.LocationMetadataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Unit tests for the Layer CRUD operations exposed by the LayerController with
 * JSON request and response encoding.
 * <p>
 * 
 * Note that test logic and assertions common to operations for all DAO-backed
 * entities can be found in the Helpers class. What follows are test cases that
 * make use of that generic test logic with some assertions specific to layers.
 * <p>
 * 
 * TODO refactor me, this file is too long
 * 
 * @author deflaux
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class S3TokenControllerTest {

	@Autowired
	private ServletTestHelper testHelper;

	private static final String TEST_USER1 = TestUserDAO.TEST_USER_NAME;
	private static final String TEST_USER2 = "testuser2@test.org";

	private Project project;
	private Dataset dataset;
	private Layer layer;
	private Code code;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		testHelper.setUp();

		testHelper.setTestUser(TEST_USER1);

		project = new Project();
		project = testHelper.createEntity(project, null);

		dataset = new Dataset();
		dataset.setParentId(project.getId());
		dataset = testHelper.createEntity(dataset, null);

		layer = new Layer();
		layer.setParentId(dataset.getId());
		layer.setType(LayerTypeNames.E);
		layer = testHelper.createEntity(layer, null);

		code = new Code();
		code.setParentId(project.getId());
		code = testHelper.createEntity(code, null);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		testHelper.tearDown();
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testCreateS3TokenForAFile() throws Exception {
		S3Token token = new S3Token();
		token.setPath("foo.jpg");
		token.setMd5("4053f00b39aae693a6969f37102e2764");

		token = testHelper.createObject(layer.getS3Token(), token);

		assertNotNull(token.getPath());
		assertNotNull(token.getMd5());
		assertNotNull(token.getContentType());
		assertNotNull(token.getSecretAccessKey());
		assertNotNull(token.getAccessKeyId());
		assertNotNull(token.getSessionToken());
		assertNotNull(token.getPresignedUrl());

	}

}
