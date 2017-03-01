package org.sagebionetworks.web.client.view;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.sagebionetworks.web.client.SynapseJSNIUtils;
import org.sagebionetworks.web.client.place.TeamSearch;
import org.sagebionetworks.web.client.widget.footer.Footer;
import org.sagebionetworks.web.client.widget.header.Header;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TeamSearchViewImpl extends Composite implements TeamSearchView {
	public interface TeamSearchViewImplUiBinder extends UiBinder<Widget, TeamSearchViewImpl> {}
	
	@UiField
	SimplePanel header;
	@UiField
	SimplePanel footer;
	@UiField
	FlowPanel mainContainer;
	@UiField
	SimplePanel paginationPanel;
	@UiField
	TextBox searchField;
	@UiField
	Button searchButton;
	@UiField
	SimplePanel synAlertPanel;
	
	private Header headerWidget;
	private Footer footerWidget;
	private Presenter presenter;
	private SynapseJSNIUtils synapseJsniUtils;
	
	@Inject
	public TeamSearchViewImpl(TeamSearchViewImplUiBinder binder,
			Header headerWidget, 
			Footer footerWidget, 
			SynapseJSNIUtils synapseJsniUtils) {
		initWidget(binder.createAndBindUi(this));
		this.headerWidget = headerWidget;
		this.footerWidget = footerWidget;
		this.synapseJsniUtils = synapseJsniUtils;
		headerWidget.configure(false);
		header.add(headerWidget.asWidget());
		footer.add(footerWidget.asWidget());
		configureSearchBox();
	}
	
	@Override
	public void setLoadMoreContainer(Widget w) {
		mainContainer.clear();
		mainContainer.add(w);
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		header.clear();
		headerWidget.configure(false);
		header.add(headerWidget.asWidget());
		footer.clear();
		footer.add(footerWidget.asWidget());
		headerWidget.refresh();	
		Window.scrollTo(0, 0); // scroll user to top of page
	}
	
	@Override
	public void setSearchTerm(String searchTerm) {
		searchField.setValue(searchTerm);
		String pageTitleSearchTerm = searchTerm != null && searchTerm.length() > 0 ? " '"+searchTerm + "' " : "";
		synapseJsniUtils.setPageTitle("Team Search" + pageTitleSearchTerm);
	}
	
	private void configureSearchBox() {
		searchButton.addClickHandler(new ClickHandler() {				
			@Override
			public void onClick(ClickEvent event) {					
				presenter.goTo(new TeamSearch(searchField.getValue()));
			}
		});
		
		searchField.addKeyDownHandler(new KeyDownHandler() {				
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
	                searchButton.fireEvent(new ClickEvent() {});
	            }					
			}
		});				
	}

	@Override
	public void setSynAlertWidget(Widget synAlert) {
		synAlertPanel.setWidget(synAlert);
	}	

}
