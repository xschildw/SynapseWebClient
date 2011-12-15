package org.sagebionetworks.client;

import java.io.File;

import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.S3Token;

/**
 * Interface for data upload functionality for synapse
 * 
 * @author deflaux
 */
public interface DataUploader {
	/**
	 * @param s3Token
	 * @param dataFile
	 * @throws SynapseException
	 */
	public void uploadData(S3Token s3Token, File dataFile) throws SynapseException;
}
