package org.sagebionetworks.web.client.widget.entity.browse;

import java.util.List;

import org.sagebionetworks.repo.model.Entity;
import org.sagebionetworks.repo.model.EntityHeader;
import org.sagebionetworks.repo.model.Reference;
import org.sagebionetworks.repo.model.VersionInfo;
import org.sagebionetworks.repo.model.request.ReferenceList;
import org.sagebionetworks.web.client.SynapseView;
import org.sagebionetworks.web.shared.PaginatedResults;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface EntityFinderView extends SynapseView {

	/**
	 * Set the presenter.
	 * @param presenter
	 */
	public void setPresenter(Presenter presenter);

	public void setVersions(List<VersionInfo> versions);
	
	void initFinderComponents(EntityFilter filter);
	void setBrowseAreaVisible();
	void setSynapseIdAreaVisible();
	void setSearchAreaVisible();
	void setSynapseMultiIdAreaVisible();
	boolean isShowing();
	void show();
	void hide();
	EntityFinderArea getCurrentArea();
	void setSynAlert(Widget w);
	/**
	 * Presenter interface
	 */
	public interface Presenter {

		void setSelectedEntity(Reference selected);

		void lookupEntity(String entityId, AsyncCallback<List<EntityHeader>> asyncCallback);
		void lookupEntity(ReferenceList rl, AsyncCallback<List<EntityHeader>> callback);

		void loadVersions(String entityId);
		
		boolean showVersions();
		
		void okClicked();
		
		void show();
		void hide();

		Widget asWidget();

		void setSelectedEntities(List<Reference> selected);

		void clearSelectedEntities();

	}
	Widget asWidget();

}
