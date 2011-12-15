package org.sagebionetworks.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.UnsupportedEncodingException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.repo.model.S3Token;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ProgressEvent;
import com.amazonaws.services.s3.model.ProgressListener;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

/**
 * Multipart upload implementation of synapse data
 * 
 * @author deflaux
 * 
 */
public class DataUploaderMultipartImpl implements DataUploader {
	private static final String TITLE = "Synapse File Upload to Amazon S3";
	private static final int MIN_HEIGHT_PIXELS = 100;
	private static final int MIN_WIDTH_PIXELS = 300;

	private class SynapseUploadProgressListener implements ProgressListener {
		private JProgressBar pb;
		private JFrame frame;
		private Upload upload;

		public SynapseUploadProgressListener() {
			frame = new JFrame(TITLE);
			pb = new JProgressBar(0, 100);
			pb.setStringPainted(true);

			frame.setContentPane(createContentPane());
			frame.pack();
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setMinimumSize(new Dimension(MIN_WIDTH_PIXELS,
					MIN_HEIGHT_PIXELS));
		}

		public void setUpload(Upload upload) {
			this.upload = upload;
		}

		private JPanel createContentPane() {
			JPanel panel = new JPanel();
			panel.add(pb);

			JPanel borderPanel = new JPanel();
			borderPanel.setLayout(new BorderLayout());
			borderPanel.add(panel, BorderLayout.NORTH);
			borderPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20,
					20));
			return borderPanel;
		}

		@Override
		public void progressChanged(ProgressEvent progressEvent) {
			if (upload == null)
				return;

			pb.setValue((int) upload.getProgress().getPercentTransfered());

			switch (progressEvent.getEventCode()) {
			case ProgressEvent.COMPLETED_EVENT_CODE:
				pb.setValue(100);
				break;
			case ProgressEvent.FAILED_EVENT_CODE:
				try {
					AmazonClientException e = upload.waitForException();
					JOptionPane.showMessageDialog(frame,
							"Unable to upload file to Amazon S3: "
									+ e.getMessage(), "Error Uploading File",
							JOptionPane.ERROR_MESSAGE);
				} catch (InterruptedException e) {
				}
				break;
			}
		}
	}

	@Override
	public void uploadData(S3Token s3Token, File dataFile)
			throws SynapseException {

		// Formulate the request, note that S3 does not verify that the entire
		// upload matches this md5, unlike the single part upload
		String base64Md5;
		try {
			byte[] encoded = Base64.encodeBase64(Hex.decodeHex(s3Token.getMd5()
					.toCharArray()));
			base64Md5 = new String(encoded, "ASCII");
		} catch (DecoderException ex) {
			throw new SynapseException(ex);
		} catch (UnsupportedEncodingException ex) {
			throw new SynapseException(ex);
		}

		ObjectMetadata s3Metadata = new ObjectMetadata();
		s3Metadata.setContentType(s3Token.getContentType());
		s3Metadata.setContentMD5(base64Md5);

		SynapseUploadProgressListener progressListener = new SynapseUploadProgressListener();
		PutObjectRequest request = new PutObjectRequest(s3Token.getBucket(),
				s3Token.getPath().substring(1), dataFile).withMetadata(
				s3Metadata).withProgressListener(progressListener);
		request.setCannedAcl(CannedAccessControlList.BucketOwnerFullControl);

		// Initiate the multipart uploas
		AWSCredentials credentials = new BasicSessionCredentials(s3Token
				.getAccessKeyId(), s3Token.getSecretAccessKey(), s3Token
				.getSessionToken());
		TransferManager tx = new TransferManager(credentials);
		Upload upload = tx.upload(request);
		progressListener.setUpload(upload);

		// Wait for the upload to complete before returning (making this
		// synchronous, can change it later if we want asynchronous behavior)
		try {
			upload.waitForUploadResult();
		} catch (Exception e) {
			throw new SynapseException("AWS S3 multipart upload of " + dataFile
					+ " failed", e);
		}
	}
}
