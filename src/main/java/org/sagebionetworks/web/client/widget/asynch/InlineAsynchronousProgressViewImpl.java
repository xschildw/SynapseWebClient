package org.sagebionetworks.web.client.widget.asynch;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.ProgressBar;
import org.gwtbootstrap3.client.ui.html.Div;
import org.gwtbootstrap3.client.ui.html.Span;
import org.gwtbootstrap3.client.ui.html.Text;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * The view portion of the AsynchronousProgressWidget. There is zero business
 * logic in this class.
 * 
 * @author John
 * 
 */
public class InlineAsynchronousProgressViewImpl implements AsynchronousProgressView {

	public interface Binder extends
			UiBinder<Div, InlineAsynchronousProgressViewImpl> {
	}

	@UiField
	Div progressColumn;
	@UiField
	Div spinnerColumn;
	@UiField
	Div title;
	@UiField
	ProgressBar progressBar;
	@UiField
	Anchor cancelLink;
	@UiField
	Div message;
	Presenter presenter;
	Div container;

	@Inject
	public InlineAsynchronousProgressViewImpl(final Binder uiBinder) {
		container = uiBinder.createAndBindUi(this);
		this.cancelLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.onCancel();
			}
		});
	}

	@Override
	public Widget asWidget() {
		return container;
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setTitle(String title) {
		this.title.clear();
		this.title.add(new Text(title));
	}

	@Override
	public void setIsDetermiante(boolean isDeterminate) {
		spinnerColumn.setVisible(!isDeterminate);
		progressColumn.setVisible(isDeterminate);
	}

	@Override
	public void setDeterminateProgress(double percent, String text,
			String message) {
		progressBar.setPercent(percent);
		progressBar.setText(text);
		this.message.clear();
		if (message != null) {
			this.message.add(new Text(message));	
		}
	}

	@Override
	public void setIndetermianteProgress(String message) {
		this.message.clear();
		if (message != null) {
			this.message.add(new Text(message));	
		}
	}

	@Override
	public boolean isAttached() {
		return progressBar.isAttached();
	}
}
