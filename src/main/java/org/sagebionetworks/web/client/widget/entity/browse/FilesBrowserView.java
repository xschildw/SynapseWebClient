package org.sagebionetworks.web.client.widget.entity.browse;

import org.sagebionetworks.web.client.SynapseView;

import com.google.gwt.user.client.ui.IsWidget;

public interface FilesBrowserView extends IsWidget, SynapseView {

	/**
	 * Set the presenter.
	 * @param presenter
	 */
	public void setPresenter(Presenter presenter);
	
	/**
	 * Configure the view with the parent id
	 * @param entityId
	 */
	public void configure(String entityId, boolean canEdit);

	/**
	 * Configure the view with the parent id and title
	 * @param entityId
	 * @param title
	 */
	public void configure(String entityId, boolean canEdit, String title);

	public void refreshTreeView(String entityId);
	
	public void showFolderEditDialog(String folderEntityId);
	
	/**
	 * Presenter interface
	 */
	public interface Presenter {

		void createFolder();
		void updateFolderName(String newFolderName, String folderEntityId);
		void deleteFolder(String folderEntityId, boolean skipTrashCan);
		void fireEntityUpdatedEvent();

	}

}
