package org.sagebionetworks.web.client.widget.table;

import org.gwtbootstrap3.client.ui.html.Div;
import org.gwtbootstrap3.client.ui.html.Span;
import org.sagebionetworks.repo.model.EntityHeader;
import org.sagebionetworks.repo.model.entity.Direction;
import org.sagebionetworks.repo.model.entity.SortBy;
import org.sagebionetworks.repo.model.table.SortDirection;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.PortalGinInjector;
import org.sagebionetworks.web.client.widget.LoadingSpinner;
import org.sagebionetworks.web.client.widget.table.v2.results.SortableTableHeaderImpl;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * View of a widget that lists table entities.  
 */
public class TableListWidgetViewImpl implements TableListWidgetView {
	
	public interface Binder extends UiBinder<HTMLPanel, TableListWidgetViewImpl> {}
	
	@UiField
	Div tablesList;
	@UiField
	Div loadMoreWidgetContainer;
	@UiField
	Div synAlertContainer;
	@UiField
	Span emptyUI;
	@UiField
	SortableTableHeaderImpl nameColumnHeader;
	HTMLPanel panel;
	Presenter presenter;
	PortalGinInjector ginInjector;
	@UiField
	LoadingSpinner loadingUI;
	@Inject
	public TableListWidgetViewImpl(Binder binder, 
			PortalGinInjector ginInjector) {
		this.panel = binder.createAndBindUi(this);
		this.ginInjector = ginInjector;
		nameColumnHeader.setSortingListener(header -> {
			presenter.toggleSort(SortBy.NAME);
		});
	}

	@Override
	public void setSortUI(SortBy sortBy, Direction dir) {
		if (SortBy.NAME.equals(sortBy)) {
			SortDirection direction = Direction.ASC.equals(dir) ? SortDirection.ASC : SortDirection.DESC;
			nameColumnHeader.setSortDirection(direction);
		}
	}
	@Override
	public void addTableListItem(final EntityHeader header) {
		emptyUI.setVisible(false);
		TableEntityListGroupItem item = ginInjector.getTableEntityListGroupItem();
		item.configure(header, event -> {
			presenter.onTableClicked(header);
		});
		tablesList.add(item);
	}
	
	@Override
	public void clearTableWidgets() {
		tablesList.clear();
		emptyUI.setVisible(true);
	}
	
	@Override
	public void setLoadMoreWidget(IsWidget w) {
		loadMoreWidgetContainer.clear();
		loadMoreWidgetContainer.add(w);
	}
	
	@Override
	public void setPresenter(final Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public void showLoading() {
		loadingUI.setVisible(true);
	}
	@Override
	public void hideLoading() {
		loadingUI.setVisible(false);	
	}

	@Override
	public void showInfo(String message) {
		DisplayUtils.showInfo(message);
	}

	@Override
	public void showErrorMessage(String message) {
		DisplayUtils.showErrorMessage(message);
	}
	
	@Override
	public Widget asWidget() {
		return panel;
	}

	@Override
	public void clear() {
		tablesList.clear();
	}
	
	@Override
	public void setSynAlert(IsWidget w) {
		synAlertContainer.clear();
		synAlertContainer.add(w);
	}
}
