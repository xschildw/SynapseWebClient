package org.sagebionetworks.web.client.security;

import static org.sagebionetworks.web.client.ServiceEntryPointUtils.fixServiceEntryPoint;

import java.util.Objects;

import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.auth.LoginRequest;
import org.sagebionetworks.repo.model.auth.LoginResponse;
import org.sagebionetworks.web.client.ClientProperties;
import org.sagebionetworks.web.client.DateTimeUtilsImpl;
import org.sagebionetworks.web.client.PortalGinInjector;
import org.sagebionetworks.web.client.SynapseJSNIUtils;
import org.sagebionetworks.web.client.UserAccountServiceAsync;
import org.sagebionetworks.web.client.cache.ClientCache;
import org.sagebionetworks.web.client.cookie.CookieKeys;
import org.sagebionetworks.web.client.cookie.CookieProvider;
import org.sagebionetworks.web.client.cookie.CookieUtils;
import org.sagebionetworks.web.client.place.Down;
import org.sagebionetworks.web.client.place.LoginPlace;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.shared.WebConstants;
import org.sagebionetworks.web.shared.exceptions.ReadOnlyModeException;
import org.sagebionetworks.web.shared.exceptions.SynapseDownException;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

/**
 * A util class for authentication
 * 
 * CODE SPLITTING NOTE: this class should be kept small
 * 
 * @author dburdick
 *
 */
public class AuthenticationControllerImpl implements AuthenticationController {
	public static final String USER_AUTHENTICATION_RECEIPT = "_authentication_receipt";
	private static final String AUTHENTICATION_MESSAGE = "Invalid usename or password.";
	private static String currentUserSessionToken;
	private static UserProfile currentUserProfile;
	private UserAccountServiceAsync userAccountService;	
	private ClientCache localStorage;
	private PortalGinInjector ginInjector;
	private SynapseJSNIUtils jsniUtils;
	private CookieProvider cookies;
	
	@Inject
	public AuthenticationControllerImpl(
			UserAccountServiceAsync userAccountService,
			ClientCache localStorage,
			CookieProvider cookies,
			PortalGinInjector ginInjector,
			SynapseJSNIUtils jsniUtils){
		this.userAccountService = userAccountService;
		fixServiceEntryPoint(userAccountService);
		this.localStorage = localStorage;
		this.cookies = cookies;
		this.ginInjector = ginInjector;
		this.jsniUtils = jsniUtils;
	}

	@Override
	public void loginUser(final String username, String password, final AsyncCallback<UserProfile> callback) {
		if(username == null || password == null) callback.onFailure(new AuthenticationException(AUTHENTICATION_MESSAGE));
		LoginRequest loginRequest = getLoginRequest(username, password);
		ginInjector.getSynapseJavascriptClient().login(loginRequest, new AsyncCallback<LoginResponse>() {		
			@Override
			public void onSuccess(LoginResponse session) {
				storeAuthenticationReceipt(username, session.getAuthenticationReceipt());
				revalidateSession(session.getSessionToken(), callback);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}
	
	public void storeAuthenticationReceipt(String username, String receipt) {
		localStorage.put(username + USER_AUTHENTICATION_RECEIPT, receipt, DateTimeUtilsImpl.getYearFromNow().getTime());
	}
	
	public LoginRequest getLoginRequest(String username, String password) {
		LoginRequest request = new LoginRequest();
		request.setUsername(username);
		request.setPassword(password);
		String authenticationReceipt = localStorage.get(username + USER_AUTHENTICATION_RECEIPT);
		request.setAuthenticationReceipt(authenticationReceipt);
		return request;
	}
	
	@Override
	public void revalidateSession(final String token, final AsyncCallback<UserProfile> callback) {
		currentUserSessionToken = null;
		if(token == null) {
			callback.onFailure(new AuthenticationException(AUTHENTICATION_MESSAGE));
			return;
		}
		//set the session cookie for same domain calls, and save the token locally for cross domain (backend) calls.
		updateSessionTokenCookie(token, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				logoutUser();
				if (caught instanceof SynapseDownException || caught instanceof ReadOnlyModeException) {
					ginInjector.getGlobalApplicationState().getPlaceChanger().goTo(new Down(ClientProperties.DEFAULT_PLACE_TOKEN));
				} else {
					callback.onFailure(caught);
				}
			}
			@Override
			public void onSuccess(String result) {
				cookies.setCookie(CookieKeys.USER_LOGGED_IN_RECENTLY, "true", DateTimeUtilsImpl.getWeekFromNow());
				currentUserSessionToken = token;
				userAccountService.getMyUserProfile(new AsyncCallback<UserProfile>() {
					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);	
					}
					@Override
					public void onSuccess(UserProfile newProfile) {
						currentUserProfile = newProfile;
						callback.onSuccess(newProfile);
						ginInjector.getSessionDetector().initializeSessionTokenState();
					}
				});
			}
		});
	}
	
	private void updateSessionTokenCookie(String token, AsyncCallback<String> callback) {
		String domain = CookieUtils.getDomain(Window.Location.getHostName());
		boolean isSecure = domain != null;
		ginInjector.getSynapseJavascriptClient().doGetString(jsniUtils.getSessionCookieUrl(token, isSecure), false, callback);
	}

	@Override
	public void logoutUser() {
		// terminate the session, remove the cookie
		ginInjector.getSynapseJavascriptClient().logout();
		jsniUtils.setAnalyticsUserId("");
		localStorage.clear();
		currentUserSessionToken = null;
		currentUserProfile = null;
		ginInjector.getHeader().refresh();
		ginInjector.getSessionDetector().initializeSessionTokenState();
		updateSessionTokenCookie(WebConstants.EXPIRE_SESSION_TOKEN, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
			}
			@Override
			public void onSuccess(String result) {
			}
		});
	}
	
	@Override
	public void updateCachedProfile(UserProfile updatedProfile){
		currentUserProfile = updatedProfile;
	}

	@Override
	public boolean isLoggedIn() {
		return currentUserSessionToken != null && !currentUserSessionToken.isEmpty() && currentUserProfile != null;
	}

	@Override
	public String getCurrentUserPrincipalId() {
		if(currentUserProfile != null) {
			return currentUserProfile.getOwnerId();						
		} 
		return null;
	}
	
	@Override
	public void reloadUserSessionData(Callback afterReload) {
		// attempt to get session token from cookies
		userAccountService.getCurrentSessionToken(new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				logoutUser();
				jsniUtils.consoleError(caught);
				afterReload.invoke();
			}
			@Override
			public void onSuccess(String token) {
				if (token != null) {
					revalidateSession(token, new AsyncCallback<UserProfile>() {
						@Override
						public void onFailure(Throwable caught) {
							jsniUtils.consoleError(caught);
							afterReload.invoke();
						}
						@Override
						public void onSuccess(UserProfile result) {
							afterReload.invoke();
						}
					});	
				} else {
					afterReload.invoke();
				}
			}
		});
	}

	@Override
	public UserProfile getCurrentUserProfile() {
		return currentUserProfile;
	}

	@Override
	public String getCurrentUserSessionToken() {
		return currentUserSessionToken;
	}
	
	@Override
	public void signTermsOfUse(boolean accepted, AsyncCallback<Void> callback) {
		userAccountService.signTermsOfUse(getCurrentUserSessionToken(), accepted, callback);
	}
	
	@Override
	public void checkForUserChange() {
		userAccountService.getCurrentSessionToken(new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				jsniUtils.consoleError(caught);
				logoutUser();
			}
			@Override
			public void onSuccess(String token) {
				String localSession = getCurrentUserSessionToken();
				// if the local session does not match the actual session, reload the app
				if (!Objects.equals(token, localSession)) {
					Window.Location.reload();
				} else {
					ginInjector.getHeader().refresh();
				}
			}
		});
	}
	
	@Override
	public void checkForSignedTermsOfUse() {
		if (isLoggedIn()) {
			// SWC-4278: do not log user out (that will clear all state, and the user may be in the middle of signing the pledge!)
			// Instead, redirect to the pledge.
			userAccountService.isTermsOfUseSigned(new AsyncCallback<Boolean>() {
				@Override
				public void onFailure(Throwable caught) {
					jsniUtils.consoleError(caught);
				}
				@Override
				public void onSuccess(Boolean isSigned) {
					if (!isSigned) {
						ginInjector.getGlobalApplicationState().getPlaceChanger().goTo(new LoginPlace(LoginPlace.SHOW_TOU));
					} // else, this is a no-op
				}
			});
			
		}
	}
}
