package org.sagebionetworks.web.client.widget.modal;

import java.util.Iterator;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalSize;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * Lightweight Bootstrap modal dialog, uses gwtbootstrap3.
 * There is also zero business logic in this class therefore it is 100% "view" with no 
 * presenter.
 * 
 * @author jhodgson
 * 
 */
public class Dialog extends UIObject implements IsWidget, HasWidgets {
	
	public interface DialogUiBinder extends UiBinder<Widget, Dialog> {}
	
	private DialogUiBinder uiBinder;
	
	private Callback callback;

	@UiField
	FlowPanel mainContent;
	@UiField
	Button primaryButton;
	@UiField
	Button defaultButton;
	@UiField
	Modal modal;
	
	boolean autoHide;
	Widget widget;

	/**
	 * Create a new Modal dialog.
	 */
	public Dialog() {
		uiBinder = GWT.create(DialogUiBinder.class);
		widget = uiBinder.createAndBindUi(this);
		primaryButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (callback != null)
					callback.onPrimary();
				if (autoHide)
					hide();
			}
		});
		ClickHandler defaultButtonClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (callback != null)
					callback.onDefault();
				if (autoHide)
					hide();
			}
		};
		defaultButton.addClickHandler(defaultButtonClickHandler);
		modal.addCloseHandler(defaultButtonClickHandler);
	}
	
	public void setSize(ModalSize modalSize) {
		modal.setSize(modalSize);
	}
	
	/**
	 * @param title The text shown in the title bar.
	 * @param body This will be the main body of the dialog.  It can be any GWT widget.
	 * @param primaryButtonText The text for the primary button (i.e "Save").  The primary button is highlighted.
	 * @param defaultButtonText The text for the default button (i.e "Cancel").  The default button will not be highlighted.  If null, will hide default button.
	 * @param callback
	 * @param autoHide if true, will hide the dialog on primary or default button click.
	 */
	public void configure(String title, Widget body, String primaryButtonText, String defaultButtonText, Callback callback, boolean autoHide) {
		configure(title, primaryButtonText, defaultButtonText, callback, autoHide);
		mainContent.clear();
		mainContent.add(body);
	}
	
	/**
	 * @param title The text shown in the title bar.
	 * @param primaryButtonText The text for the primary button (i.e "Save").  The primary button is highlighted.
	 * @param defaultButtonText The text for the default button (i.e "Cancel").  The default button will not be highlighted.  If null, will hide default button.
	 * @param callback
	 * @param autoHide if true, will hide the dialog on primary or default button click.
	 */
	public void configure(String title, String primaryButtonText, String defaultButtonText, Callback callback, boolean autoHide) {
		this.autoHide = autoHide;
		this.callback = callback;
		boolean isPrimaryButtonVisible = primaryButtonText != null;
		primaryButton.setVisible(isPrimaryButtonVisible);
		if (isPrimaryButtonVisible)
			primaryButton.setText(primaryButtonText);
		boolean isDefaultButtonVisible = defaultButtonText != null;
		defaultButton.setVisible(isDefaultButtonVisible);
		if (isDefaultButtonVisible)
			defaultButton.setText(defaultButtonText);
		modal.setTitle(title);
		modal.setHideOtherModals(false);
	}
	
	/**
	 * The Callback handles events generated by this modal dialog.
	 * 
	 */
	public interface Callback {
		/**
		 * Called when the primary button is pressed.
		 */
		public void onPrimary();

		/**
		 * Called when the default button is pressed.
		 */
		public void onDefault();
	}
	
	public Button getPrimaryButton() {
		return primaryButton;
	}
	public Button getDefaultButton() {
		return defaultButton;
	}
	
	public void show() {
		modal.show();
	}
	public void hide() {
		modal.hide();
	}
	
	public void addStyleName(String style) {
		modal.addStyleName(style);
	}
	
	public void setClosable(boolean closable) {
		modal.setClosable(closable);		
	}
	
	public Widget asWidget() {
		return widget;
	}

	public boolean isVisible() {
		return widget.isVisible();
	}

	@Override
	public void add(Widget w) {
		mainContent.add(w);
	}

	@Override
	public void clear() {
		mainContent.clear();
	}

	@Override
	public Iterator<Widget> iterator() {
		return mainContent.iterator();
	}

	@Override
	public boolean remove(Widget w) {
		return mainContent.remove(w);
	}
	
	
}
