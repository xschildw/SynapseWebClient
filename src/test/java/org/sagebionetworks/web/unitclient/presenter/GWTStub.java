package org.sagebionetworks.web.unitclient.presenter;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;

import org.sagebionetworks.web.client.GWTWrapper;
import org.sagebionetworks.web.client.utils.Callback;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.google.gwt.regexp.shared.RegExp;

public class GWTStub implements GWTWrapper {
	private final static RegExp PATTERN_WHITE_SPACE = RegExp.compile("^\\s+$");

	public GWTStub() {
	}

	@Override
	public String getHostPageBaseURL() {
		return "http://hostpage/url";
	}

	@Override
	public String getModuleBaseURL() {
		return "http://baseurl/";
	}

	@Override
	public void assignThisWindowWith(String url) {
	}

	@Override
	public String encodeQueryString(String queryString) {
		return URLEncoder.encode(queryString);
	}

	@Override
	public String decodeQueryString(String queryString) {
		return URLDecoder.decode(queryString);
	}

	@Override
	public XMLHttpRequest createXMLHttpRequest() {
		return null;
	}

	@Override
	public NumberFormat getNumberFormat(String pattern) {
		return null;
	}

	@Override
	public String getHostPrefix() {
		return null;
	}

	@Override
	public String getCurrentURL() {
		return null;
	}

	@Override
	public DateTimeFormat getDateTimeFormat(PredefinedFormat format) {
		return null;
	}
	
	@Override
	public void scheduleExecution(Callback callback, int delay) {
	}

	@Override
	public String getUserAgent() {
		return null;
	}

	@Override
	public String getAppVersion() {
		return null;
	}
	@Override
	public int nextRandomInt() {
		return 0;
	}
	@Override
	public void scheduleDeferred(Callback callback) {
	}

	@Override
	public String getFormattedDateString(Date date) {
		return null;
	}
	@Override
	public void addDaysToDate(Date date, int days) {
	}

	@Override
	public boolean isWhitespace(String text) {
		return PATTERN_WHITE_SPACE.test(text);
	}
}

