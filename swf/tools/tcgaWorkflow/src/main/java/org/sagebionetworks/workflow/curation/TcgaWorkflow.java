package org.sagebionetworks.workflow.curation;

import com.amazonaws.services.simpleworkflow.flow.annotations.Execute;
import com.amazonaws.services.simpleworkflow.flow.annotations.Workflow;
import com.amazonaws.services.simpleworkflow.flow.annotations.WorkflowRegistrationOptions;

/**
 * @author deflaux
 *
 */
@Workflow
@WorkflowRegistrationOptions(defaultExecutionStartToCloseTimeoutSeconds = TcgaActivities.ONE_DAY_OF_SECONDS) 
public interface TcgaWorkflow {
	
	/**
	 * The one and only task list to use for decisions in this workflow
	 */
	static final String DECISIONS_TASK_LIST = "TcgaDecisions";
	
    /**
     * @param datasetId
     * @param tcgaUrl
     * @param doneIfExists 
     */
    @Execute(version = TcgaActivities.VERSION) 
    void addRawTcgaLayer(String datasetId, String tcgaUrl, Boolean doneIfExists);

}