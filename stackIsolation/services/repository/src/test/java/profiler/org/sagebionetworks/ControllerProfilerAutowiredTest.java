package profiler.org.sagebionetworks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sagebionetworks.repo.manager.TestUserDAO;
import org.sagebionetworks.repo.manager.UserManager;
import org.sagebionetworks.repo.model.AccessControlList;
import org.sagebionetworks.repo.model.Agreement;
import org.sagebionetworks.repo.model.Annotations;
import org.sagebionetworks.repo.model.Dataset;
import org.sagebionetworks.repo.model.Dataset;
import org.sagebionetworks.repo.model.Dataset;
import org.sagebionetworks.repo.model.DatastoreException;
import org.sagebionetworks.repo.model.EntityHeader;
import org.sagebionetworks.repo.model.InputDataLayer;
import org.sagebionetworks.repo.model.Eula;
import org.sagebionetworks.repo.model.InvalidModelException;
import org.sagebionetworks.repo.model.Nodeable;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.PaginatedResults;
import org.sagebionetworks.repo.model.Project;
import org.sagebionetworks.repo.model.UnauthorizedException;
import org.sagebionetworks.repo.model.UserInfo;
import org.sagebionetworks.repo.model.Versionable;
import org.sagebionetworks.repo.web.GenericEntityController;
import org.sagebionetworks.repo.web.NotFoundException;
import org.sagebionetworks.repo.web.UrlHelpers;
import org.sagebionetworks.repo.web.controller.ObjectTypeFactory;
import org.sagebionetworks.repo.web.controller.ServletTestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.DispatcherServlet;

import com.amazonaws.services.cloudwatch.model.MetricDatum;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class ControllerProfilerAutowiredTest {
	
/*
	 
	
	// Used for cleanup
	@Autowired
	GenericEntityController entityController;
	
	@Autowired
	public UserManager userManager;

	@Autowired
	ControllerProfiler controllerProfiler;
	//static private Log log = LogFactory
			//.getLog(ControllerProfilerCloudWatchTest.class);
	
	private static HttpServlet dispatchServlet;
	private String userName = TestUserDAO.ADMIN_USER_NAME;
	private UserInfo testUser;
	private List<String> toDelete;

	@Before
	public void before() throws DatastoreException, NotFoundException {
		assertNotNull(entityController);
		toDelete = new ArrayList<String>();
		// Map test objects to their urls
		// Make sure we have a valid user.
		testUser = userManager.getUserInfo(userName);
		UserInfo.validateUserInfo(testUser);
	}

	//dynamically deletes everything that got created in the test
	//so nothing sticks around to create database issues
	@After
	public void after() throws UnauthorizedException {
		if (entityController != null && toDelete != null) {
			for (String idToDelete : toDelete) {
				try {
					entityController.deleteEntity(userName, idToDelete);
				} catch (NotFoundException e) {
					// nothing to do here
				} catch (DatastoreException e) {
					// nothing to do here.
				}
			}
		}
	}

	@BeforeClass
	public static void beforeClass() throws ServletException {
		// Setup the servlet once
		// Create a Spring MVC DispatcherServlet so that we can test our URL
		// mapping, request format, response format, and response status
		// code.
		MockServletConfig servletConfig = new MockServletConfig("repository");
		servletConfig.addInitParameter("contextConfigLocation",	"classpath:test-context.xml");
		dispatchServlet = new DispatcherServlet();
		dispatchServlet.init(servletConfig);

	}
	*/
	
	/**
	 * This is a test helper method that will create at least on of each type of entity.
	 * @return
	 * @throws IOException 
	 * @throws ServletException 
	 * @throws InvalidModelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws UnauthorizedException 
	 * @throws NotFoundException 
	 * @throws DatastoreException 
	 */
	
	
	@Test
	public void testcreateMetricDatumsForDefaultControler() throws Exception{
		/*
		// For now put each object in a project so their parent id is not null;
		// Create a project
		Project project = new Project();	
		project.setName("createAtLeastOneOfEachType");
		//should produce one createEntity() call
		project = ServletTestHelper.createEntity(dispatchServlet, project, userName);
		assertNotNull(project);
		//should delete project and cascade delete everything under project
		toDelete.add(project.getId());	 
		
		// Create a dataset
		//should produce one createEntity() call
		Dataset datasetParent = (Dataset) ObjectTypeFactory.createObjectForTest("datasetParent", ObjectType.dataset, project.getId());
		datasetParent = ServletTestHelper.createEntity(dispatchServlet, datasetParent, userName);
		
		//Create a second dataset
		Dataset secondDataset = (Dataset) ObjectTypeFactory.createObjectForTest("secondDataset", ObjectType.dataset, project.getId());
		secondDataset = ServletTestHelper.createEntity(dispatchServlet, secondDataset, userName);
		
		//Create a third dataset
		Dataset thirdDataset = (Dataset) ObjectTypeFactory.createObjectForTest("thirdDataset", ObjectType.dataset, project.getId());
		thirdDataset = ServletTestHelper.createEntity(dispatchServlet, thirdDataset, userName);
		
		//Create a fourth dataset
		Dataset fourthDataset = (Dataset) ObjectTypeFactory.createObjectForTest("fourthDataset", ObjectType.dataset, project.getId());
		fourthDataset = ServletTestHelper.createEntity(dispatchServlet, fourthDataset, userName);
		
		//Create a fifth dataset
		Dataset fifthDataset = (Dataset) ObjectTypeFactory.createObjectForTest("fifthDataset", ObjectType.dataset, project.getId());
		fifthDataset = ServletTestHelper.createEntity(dispatchServlet, fifthDataset, userName);
		
		//Create a sixth dataset
		Dataset sixthDataset = (Dataset) ObjectTypeFactory.createObjectForTest("sixthDataset", ObjectType.dataset, project.getId());
		sixthDataset = ServletTestHelper.createEntity(dispatchServlet, sixthDataset, userName);
		
		//Create a seventh dataset
		Dataset seventhDataset = (Dataset) ObjectTypeFactory.createObjectForTest("seventhDataset", ObjectType.dataset, project.getId());
		seventhDataset = ServletTestHelper.createEntity(dispatchServlet, seventhDataset, userName);
		
		//Create a eighth dataset
		Dataset eighthDataset = (Dataset) ObjectTypeFactory.createObjectForTest("eighthDataset", ObjectType.dataset, project.getId());
		eighthDataset = ServletTestHelper.createEntity(dispatchServlet, eighthDataset, userName);
		
		//Create a ninth dataset
		Dataset ninthDataset = (Dataset) ObjectTypeFactory.createObjectForTest("ninthDataset", ObjectType.dataset, project.getId());
		ninthDataset = ServletTestHelper.createEntity(dispatchServlet, ninthDataset, userName);
		
		//Create a tenth dataset
		Dataset tenthDataset = (Dataset) ObjectTypeFactory.createObjectForTest("tenthDataset", ObjectType.dataset, project.getId());
		tenthDataset = ServletTestHelper.createEntity(dispatchServlet, tenthDataset, userName);
		
		//List <MetricDatum> toSeeMetricDatumsCreated = ControllerProfiler.consumer.getMetricDatumList();
		//System.out.println("here's our MD's");
		//System.out.println(toSeeMetricDatumsCreated.toString());
		//assertEquals(11, toSeeMetricDatumsCreated.size());
		 *
		 */
	}

}
