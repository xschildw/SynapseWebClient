package org.sagebionetworks.web.client.view;

import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.IconsImageBundle;
import org.sagebionetworks.web.client.SageImageBundle;
import org.sagebionetworks.web.client.widget.footer.Footer;
import org.sagebionetworks.web.client.widget.header.Header;
import org.sagebionetworks.web.shared.BCCSignupProfile;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class BCCOverviewViewImpl extends Composite implements BCCOverviewView {

	public interface BCCOverviewViewImplUiBinder extends UiBinder<Widget, BCCOverviewViewImpl> {}

	@UiField
	SimplePanel header;
	@UiField
	SimplePanel footer;
	@UiField
	SimplePanel applyForChallenge;
	@UiField
	Anchor supportLink;
//	@UiField
//	SimplePanel challengeFeedPanel;
	
	private Presenter presenter;
	private IconsImageBundle icons;
	private Header headerWidget;
	private Footer footerWidget;
	
	@Inject
	public BCCOverviewViewImpl(BCCOverviewViewImplUiBinder binder,
			Header headerWidget, Footer footerWidget, IconsImageBundle icons,
			SageImageBundle imageBundle) {		
		initWidget(binder.createAndBindUi(this));

		this.icons = icons;
		this.headerWidget = headerWidget;
		this.footerWidget = footerWidget;
		header.add(headerWidget.asWidget());
		footer.add(footerWidget.asWidget());
	}



	@Override
	public void setPresenter(final Presenter presenter) {
		this.presenter = presenter;
		header.clear();
		header.add(headerWidget.asWidget());
		footer.clear();
		footer.add(footerWidget.asWidget());
		headerWidget.refresh();
		Window.scrollTo(0, 0); // scroll user to top of page

	}

	@Override
	public void showOverView() {
		LayoutContainer megaButton = new LayoutContainer();
		megaButton.setStyleName("mega-button");
		megaButton.setStyleAttribute("margin-top", "10px;");
		megaButton.setStyleAttribute("float", "left;");
		Anchor applyForChallengeLink = new Anchor();
		applyForChallengeLink.setText("Join the Challenge");
		applyForChallengeLink.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				BCCSignupProfile profile = presenter.getBCCSignupProfile();
				final BCCCallback callback = presenter.getBCCSignupCallback();
				BCCSignupHelper.showDialog(profile, callback);				
			}
		});
		megaButton.add(applyForChallengeLink);
		applyForChallenge.clear();
		applyForChallenge.add(megaButton);
		
		headerWidget.getSupportHRef(new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				supportLink.setHref(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				//should never enter this code.  if the fastpass request fails, it should still return the standard support site url
			}
		});
	}

	@Override
	public void showErrorMessage(String message) {
		DisplayUtils.showErrorMessage(message);
	}

	@Override
	public void showLoading() {
	}

	@Override
	public void showInfo(String title, String message) {
		DisplayUtils.showInfo(title, message);
	}

	@Override
	public void clear() {		
	}

	@Override
	public void showChallengeInfo(String html){
//		HTMLPanel panel = new HTMLPanel(html);
//		DisplayUtils.sendAllLinksToNewWindow(panel);
//		challengeFeedPanel.clear();
//		challengeFeedPanel.add(panel);
	}

	@Override
	public void showSubmissionAcknowledgement() {
		Window.alert("Your submission has been received.");
	}



	@Override
	public void showSubmissionError() {
		Window.alert("There was an error during submission.  Please try again or contact Sage Bionetworks.");
	}

}
