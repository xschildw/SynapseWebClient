package org.sagebionetworks.web.client.widget.table.modal.fileview;

import java.util.ArrayList;
import java.util.List;

import org.sagebionetworks.repo.model.Entity;
import org.sagebionetworks.repo.model.EntityHeader;
import org.sagebionetworks.repo.model.Reference;
import org.sagebionetworks.web.client.DisplayUtils.SelectedHandler;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.widget.entity.browse.EntityFilter;
import org.sagebionetworks.web.client.widget.entity.browse.EntityFinder;
import org.sagebionetworks.web.client.widget.entity.controller.SynapseAlert;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class EntityContainerListWidget implements EntityContainerListWidgetView.Presenter, IsWidget {
	EntityFinder finder;
	EntityContainerListWidgetView view;
	SynapseClientAsync synapseClient;
	List<String> entityIds;
	SynapseAlert synAlert;
	boolean canEdit = true;
	@Inject
	public EntityContainerListWidget(EntityContainerListWidgetView view, EntityFinder finder, SynapseClientAsync synapseClient, SynapseAlert synAlert) {
		this.view = view;
		this.finder = finder;
		this.synapseClient = synapseClient;
		this.synAlert = synAlert;
		view.setPresenter(this);
		boolean showVersions = false;
		entityIds = new ArrayList<String>();
		finder.configure(EntityFilter.CONTAINER, showVersions, new SelectedHandler<Reference>() {
			@Override
			public void onSelected(Reference selected) {
				onAddProject(selected.getTargetId());
			}
		});
	}
	
	public void configure(List<String> entityContainerIds, boolean canEdit) {
		view.clear();
		this.canEdit = canEdit;
		view.setAddButtonVisible(canEdit);
		view.setNoContainers(entityContainerIds.isEmpty());
		synAlert.clear();
		if (!entityContainerIds.isEmpty()) {
			synapseClient.getEntityHeaderBatch(entityContainerIds, new AsyncCallback<ArrayList<EntityHeader>>() {
				@Override
				public void onFailure(Throwable caught) {
					synAlert.handleException(caught);
				}
			
				@Override
				public void onSuccess(ArrayList<EntityHeader> entityHeaders) {
					for (EntityHeader header : entityHeaders) {
						entityIds.add(header.getId());
						view.addEntity(header.getId(), header.getName(), EntityContainerListWidget.this.canEdit);
					}
				}
			});
		}
	}
	
	@Override
	public void onAddProject() {
		finder.show();
	}
	
	/**
	 * Called when a container entity is selected in the entity finder.
	 * @param id
	 */
	public void onAddProject(String id) {
		synapseClient.getEntity(id, new AsyncCallback<Entity>() {
			@Override
			public void onSuccess(Entity entity) {
				entityIds.add(entity.getId());
				view.setNoContainers(false);
				view.addEntity(entity.getId(), entity.getName(), canEdit);
				finder.hide();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				finder.showError(caught.getMessage());
			}
		});
	}
	
	@Override
	public void onRemoveProject(String id) {
		entityIds.remove(id);
	}
	
	public List<String> getEntityIds() {
		return entityIds;
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}
}