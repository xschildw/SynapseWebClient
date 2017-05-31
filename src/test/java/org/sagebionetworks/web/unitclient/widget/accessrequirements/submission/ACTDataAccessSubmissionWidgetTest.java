package org.sagebionetworks.web.unitclient.widget.accessrequirements.submission;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.repo.model.dataaccess.AccessApprovalResult;
import org.sagebionetworks.repo.model.dataaccess.BatchAccessApprovalRequest;
import org.sagebionetworks.repo.model.dataaccess.BatchAccessApprovalResult;
import org.sagebionetworks.repo.model.dataaccess.ResearchProject;
import org.sagebionetworks.repo.model.dataaccess.Submission;
import org.sagebionetworks.repo.model.dataaccess.SubmissionState;
import org.sagebionetworks.repo.model.file.FileHandleAssociateType;
import org.sagebionetworks.repo.model.file.FileHandleAssociation;
import org.sagebionetworks.web.client.DataAccessClientAsync;
import org.sagebionetworks.web.client.PortalGinInjector;
import org.sagebionetworks.web.client.SynapseJSNIUtils;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.client.widget.FileHandleWidget;
import org.sagebionetworks.web.client.widget.accessrequirements.submission.ACTDataAccessSubmissionWidget;
import org.sagebionetworks.web.client.widget.accessrequirements.submission.ACTDataAccessSubmissionWidgetView;
import org.sagebionetworks.web.client.widget.entity.BigPromptModalView;
import org.sagebionetworks.web.client.widget.entity.act.UserBadgeItem;
import org.sagebionetworks.web.client.widget.entity.controller.SynapseAlert;
import org.sagebionetworks.web.client.widget.upload.FileHandleList;
import org.sagebionetworks.web.client.widget.user.UserBadge;
import org.sagebionetworks.web.test.helper.AsyncMockStubber;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

public class ACTDataAccessSubmissionWidgetTest {
	
	ACTDataAccessSubmissionWidget widget;
	@Mock
	ACTDataAccessSubmissionWidgetView mockView;
	@Mock
	SynapseAlert mockSynapseAlert;
	@Mock
	DataAccessClientAsync mockClient;
	@Mock
	BigPromptModalView mockPromptModalView;
	@Mock
	FileHandleWidget mockDucFileRenderer;
	@Mock
	FileHandleWidget mockIrbFileRenderer;
	@Mock
	FileHandleList mockFileHandleList;
	@Mock
	SynapseJSNIUtils mockJSNIUtils;
	@Mock
	PortalGinInjector mockGinInjector;
	@Mock
	Submission mockDataAccessSubmission;
	@Mock
	ResearchProject mockResearchProjectSnapshot;
	@Captor
	ArgumentCaptor<Callback> promptModalPresenterCaptor;
	Callback confirmRejectionCallback;
	@Captor
	ArgumentCaptor<FileHandleAssociation> fhaCaptor;
	@Mock
	UserBadgeItem mockUserBadge;
	@Mock
	BatchAccessApprovalResult mockBatchAccessApprovalResult;
	@Mock
	UserBadge mockModifiedByBadge;
	@Mock
	AccessApprovalResult mockAccessApprovalResult1;
	@Mock
	AccessApprovalResult mockAccessApprovalResult2;
	List<AccessApprovalResult> accessApprovalResults;
	
	public static final String SUBMISSION_ID = "9876545678987";
	public static final String INSTITUTION = "Univerisity of Washington";
	public static final String INTENDED_DATA_USE = "lorem ipsum";
	public static final String PROJECT_LEAD = "Mr. Rogers";
	public static final String SMALL_DATE_STRING = "1/2/33";
	
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		when(mockDataAccessSubmission.getResearchProjectSnapshot()).thenReturn(mockResearchProjectSnapshot);
		when(mockFileHandleList.configure()).thenReturn(mockFileHandleList);
		when(mockFileHandleList.setCanDelete(anyBoolean())).thenReturn(mockFileHandleList);
		when(mockFileHandleList.setCanUpload(anyBoolean())).thenReturn(mockFileHandleList);
		when(mockDataAccessSubmission.getState()).thenReturn(SubmissionState.APPROVED);
		when(mockGinInjector.getUserBadgeWidget()).thenReturn(mockModifiedByBadge);
		when(mockDataAccessSubmission.getId()).thenReturn(SUBMISSION_ID);
		when(mockResearchProjectSnapshot.getInstitution()).thenReturn(INSTITUTION);
		when(mockResearchProjectSnapshot.getIntendedDataUseStatement()).thenReturn(INTENDED_DATA_USE);
		when(mockResearchProjectSnapshot.getProjectLead()).thenReturn(PROJECT_LEAD);
		when(mockJSNIUtils.convertDateToSmallString(any(Date.class))).thenReturn(SMALL_DATE_STRING);
		
		widget = new ACTDataAccessSubmissionWidget(mockView, mockSynapseAlert, mockClient, mockPromptModalView, mockDucFileRenderer, mockIrbFileRenderer, mockFileHandleList, mockJSNIUtils, mockGinInjector);
		AsyncMockStubber.callSuccessWith(mockDataAccessSubmission).when(mockClient).updateDataAccessSubmissionState(anyString(), any(SubmissionState.class), anyString(), any(AsyncCallback.class));
		verify(mockPromptModalView).configure(anyString(),  anyString(), anyString(),  promptModalPresenterCaptor.capture());
		confirmRejectionCallback = promptModalPresenterCaptor.getValue();
		accessApprovalResults = new ArrayList<>();
		when(mockBatchAccessApprovalResult.getResults()).thenReturn(accessApprovalResults);
		AsyncMockStubber.callSuccessWith(mockBatchAccessApprovalResult).when(mockClient).getAccessApprovalInfo(any(BatchAccessApprovalRequest.class), any(AsyncCallback.class));
	}

	@Test
	public void testConstruction() {
		verify(mockFileHandleList).configure();
		verify(mockFileHandleList).setCanDelete(false);
		verify(mockFileHandleList).setCanUpload(false);
		verify(mockView).setPresenter(widget);
	}
	
	@Test
	public void testConfigure() {
		when(mockGinInjector.getUserBadgeItem()).thenReturn(mockUserBadge);
		// set up accessors
		boolean user1HasApproval = true;
		boolean user2HasApproval = false;
		String userId1 = "12";
		String userId2 = "34";
		List<String> userIds = new ArrayList<String>();
		userIds.add(userId1);
		userIds.add(userId2);
		when(mockDataAccessSubmission.getAccessors()).thenReturn(userIds);
		// set up other documents
		String fileHandleId1 = "873";
		String fileHandleId2 = "5432";
		List<String> fileHandleIds = new ArrayList<String>();
		fileHandleIds.add(fileHandleId1);
		fileHandleIds.add(fileHandleId2);
		when(mockDataAccessSubmission.getAttachments()).thenReturn(fileHandleIds);
		when(mockAccessApprovalResult1.getUserId()).thenReturn(userId1);
		when(mockAccessApprovalResult1.getHasApproval()).thenReturn(user1HasApproval);
		accessApprovalResults.add(mockAccessApprovalResult1);
		when(mockAccessApprovalResult2.getUserId()).thenReturn(userId2);
		when(mockAccessApprovalResult2.getHasApproval()).thenReturn(user2HasApproval);
		accessApprovalResults.add(mockAccessApprovalResult2);
		
		when(mockDataAccessSubmission.getIsRenewalSubmission()).thenReturn(false);
		String fileHandleId3 = "565499";
		when(mockDataAccessSubmission.getDucFileHandleId()).thenReturn(fileHandleId3);
		String fileHandleId4 = "1111112";
		when(mockDataAccessSubmission.getIrbFileHandleId()).thenReturn(fileHandleId4);
		
		widget.configure(mockDataAccessSubmission);
		
		verify(mockView).hideActions();
		// verify accessors
		verify(mockView).clearAccessors();
		verify(mockGinInjector, times(2)).getUserBadgeItem();
		
		verify(mockUserBadge).configure(userId1);
		verify(mockUserBadge).setMetRequirementIconVisible(user1HasApproval);
		
		verify(mockUserBadge).configure(userId2);
		verify(mockUserBadge).setMetRequirementIconVisible(user2HasApproval);
		
		verify(mockView, times(2)).addAccessors(any(IsWidget.class));
		// verify other documents
		verify(mockFileHandleList).clear();
		verify(mockFileHandleList, times(2)).addFileLink(fhaCaptor.capture());
		List<FileHandleAssociation> fhas = fhaCaptor.getAllValues();
		FileHandleAssociation fha = fhas.get(0);
		assertEquals(SUBMISSION_ID, fha.getAssociateObjectId());
		assertEquals(FileHandleAssociateType.DataAccessSubmissionAttachment, fha.getAssociateObjectType());
		assertTrue(fileHandleId1.equals(fha.getFileHandleId()) || fileHandleId2.equals(fha.getFileHandleId()));
		// verify duc
		verify(mockDucFileRenderer).setVisible(true);
		verify(mockDucFileRenderer).configure(fhaCaptor.capture());
		fha = fhaCaptor.getValue();
		assertEquals(SUBMISSION_ID, fha.getAssociateObjectId());
		assertEquals(FileHandleAssociateType.DataAccessSubmissionAttachment, fha.getAssociateObjectType());
		assertEquals(fileHandleId3, fha.getFileHandleId());
		// verify irb
		verify(mockIrbFileRenderer).setVisible(true);
		verify(mockIrbFileRenderer).configure(fhaCaptor.capture());
		fha = fhaCaptor.getValue();
		assertEquals(SUBMISSION_ID, fha.getAssociateObjectId());
		assertEquals(FileHandleAssociateType.DataAccessSubmissionAttachment, fha.getAssociateObjectType());
		assertEquals(fileHandleId4, fha.getFileHandleId());
		// verify view
		verify(mockView).setInstitution(INSTITUTION);
		verify(mockView).setIntendedDataUse(INTENDED_DATA_USE);
		verify(mockView).setIsRenewal(false);
		verify(mockView).setProjectLead(PROJECT_LEAD);
		verify(mockView).setSubmittedOn(SMALL_DATE_STRING);
	}
	
	@Test
	public void testConfigureSubmitted() {
		when(mockDataAccessSubmission.getState()).thenReturn(SubmissionState.SUBMITTED);
		widget.configure(mockDataAccessSubmission);
		verify(mockView).hideActions();
		verify(mockView).showApproveButton();
		verify(mockView).showRejectButton();
		

		// no duc or irb, verify they are hidden
		verify(mockIrbFileRenderer).setVisible(false);
		verify(mockDucFileRenderer).setVisible(false);
	}
	
	@Test
	public void testConfigureOtherStates() {
		when(mockDataAccessSubmission.getState()).thenReturn(SubmissionState.CANCELLED);
		widget.configure(mockDataAccessSubmission);
		
		when(mockDataAccessSubmission.getState()).thenReturn(SubmissionState.REJECTED);
		widget.configure(mockDataAccessSubmission);
		
		when(mockDataAccessSubmission.getState()).thenReturn(SubmissionState.APPROVED);
		widget.configure(mockDataAccessSubmission);
		
		verify(mockView, times(3)).hideActions();
		verify(mockView, never()).showApproveButton();
		verify(mockView, never()).showRejectButton();
	}
	
	@Test
	public void testUpdateDataAccessSubmissionState() {
		widget.configure(mockDataAccessSubmission);
		String rejectionReason = "missing info";
		when(mockPromptModalView.getValue()).thenReturn(rejectionReason);
		when(mockDataAccessSubmission.getState()).thenReturn(SubmissionState.REJECTED);
		
		confirmRejectionCallback.invoke();
		
		verify(mockPromptModalView).hide();
		verify(mockClient).updateDataAccessSubmissionState(eq(SUBMISSION_ID), eq(SubmissionState.REJECTED), eq(rejectionReason), any(AsyncCallback.class));
		verify(mockView).setState(SubmissionState.REJECTED.name());
	}
	
	@Test
	public void testUpdateDataAccessSubmissionStateFailure() {
		widget.configure(mockDataAccessSubmission);
		Exception ex = new Exception();
		AsyncMockStubber.callFailureWith(ex).when(mockClient).updateDataAccessSubmissionState(anyString(), any(SubmissionState.class), anyString(), any(AsyncCallback.class));
		widget.updateDataAccessSubmissionState(SubmissionState.APPROVED, "");
		verify(mockClient).updateDataAccessSubmissionState(eq(SUBMISSION_ID), eq(SubmissionState.APPROVED), anyString(), any(AsyncCallback.class));
		verify(mockSynapseAlert).handleException(ex);
	}
}