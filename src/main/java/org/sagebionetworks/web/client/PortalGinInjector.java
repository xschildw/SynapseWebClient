package org.sagebionetworks.web.client;

import org.sagebionetworks.web.client.presenter.BCCOverviewPresenter;
import org.sagebionetworks.web.client.presenter.ComingSoonPresenter;
import org.sagebionetworks.web.client.presenter.EntityPresenter;
import org.sagebionetworks.web.client.presenter.GovernancePresenter;
import org.sagebionetworks.web.client.presenter.HomePresenter;
import org.sagebionetworks.web.client.presenter.LoginPresenter;
import org.sagebionetworks.web.client.presenter.LookupPresenter;
import org.sagebionetworks.web.client.presenter.ProfilePresenter;
import org.sagebionetworks.web.client.presenter.ProjectsHomePresenter;
import org.sagebionetworks.web.client.presenter.SearchPresenter;
import org.sagebionetworks.web.client.presenter.SettingsPresenter;
import org.sagebionetworks.web.client.presenter.users.PasswordResetPresenter;
import org.sagebionetworks.web.client.presenter.users.RegisterAccountPresenter;
import org.sagebionetworks.web.client.security.AuthenticationController;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

/**
 * The root portal dependency injection root.
 * 
 * @author jmhill
 *
 */
@GinModules(PortalGinModule.class)
public interface PortalGinInjector extends Ginjector {

	public GlobalApplicationState getGlobalApplicationState();
	
	public HomePresenter getHomePresenter();

	public EntityPresenter getEntityPresenter();
	
	public ProjectsHomePresenter getProjectsHomePresenter();
	
	public LoginPresenter getLoginPresenter();
	
	public AuthenticationController getAuthenticationController();
	
	public PasswordResetPresenter getPasswordResetPresenter();
	
	public RegisterAccountPresenter getRegisterAccountPresenter();

	public ProfilePresenter getProfilePresenter();

	public SettingsPresenter getSettingsPresenter();
	
	public ComingSoonPresenter getComingSoonPresenter();
	
	public BCCOverviewPresenter getBCCOverviewPresenter();
	
	public GovernancePresenter getGovernancePresenter();
	
	public LookupPresenter getLookupPresenter();
	
	public SearchPresenter getSearchPresenter();
	
	public BCCSignup getBCCSignup();
	
	public EventBus getEventBus();
}
