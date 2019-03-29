package org.sagebionetworks.web.client.widget.team;

import org.gwtbootstrap3.client.ui.Button;
import org.sagebionetworks.web.client.view.bootstrap.table.TableData;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class OpenTeamInvitationWidget implements IsWidget{
	public interface Binder extends UiBinder<Widget, OpenTeamInvitationWidget> {}
	@UiField 
	TableData badgeTableData;
	@UiField 
	TableData messageTableData;
	@UiField 
	TableData createdOnTableData;
	@UiField
	TableData joinButtonContainer;
	@UiField
	Button cancelButton;
	
	private Widget widget;
	
	@Inject
	public OpenTeamInvitationWidget(Binder binder) {
		widget = binder.createAndBindUi(this);
	}
	
	@Override
	public Widget asWidget() {
		return widget;
	}
}
