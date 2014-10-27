package org.sagebionetworks.web.client.widget.sharing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.ListItem;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.Tooltip;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.sagebionetworks.repo.model.ACCESS_TYPE;
import org.sagebionetworks.web.client.DisplayConstants;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.IconsImageBundle;
import org.sagebionetworks.web.client.SageImageBundle;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.SynapseJSNIUtils;
import org.sagebionetworks.web.client.UrlCache;
import org.sagebionetworks.web.client.cookie.CookieProvider;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.client.utils.CallbackP;
import org.sagebionetworks.web.client.widget.modal.Dialog;
import org.sagebionetworks.web.client.widget.search.UserGroupSuggestBox;
import org.sagebionetworks.web.shared.PublicPrincipalIds;
import org.sagebionetworks.web.shared.users.AclEntry;
import org.sagebionetworks.web.shared.users.AclUtils;
import org.sagebionetworks.web.shared.users.PermissionLevel;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class AccessControlListEditorViewImpl extends FlowPanel implements AccessControlListEditorView {
 
	private static final String STYLE_VERTICAL_ALIGN_MIDDLE = "vertical-align:middle !important;";
	private static final String PRINCIPAL_COLUMN_ID = "principalData";
	private static final String ACCESS_COLUMN_ID = "accessData";
	private static final String REMOVE_COLUMN_ID = "removeData";
	
	private static final String CANNOT_MODIFY_ACL_TEXT = "You do not have sufficient privileges to modify the ACL.";	// TODO: Check if this text is ok.
	
	private Presenter presenter;
	private IconsImageBundle iconsImageBundle;
	private UrlCache urlCache;
	private Map<PermissionLevel, String> permissionDisplay;
	private SageImageBundle sageImageBundle;
	private SynapseJSNIUtils synapseJSNIUtils;
	private CookieProvider cookies;
	private SynapseClientAsync synapseClient;
	private PublicPrincipalIds publicPrincipalIds;
	private Boolean isPubliclyVisible;
	private boolean showEditColumns;
	
	private SharingPermissionsGrid permissionsGrid;
	
	private Select permissionLevelSelectBox;
	
	private AddPeopleToAclPanel addPeoplePanel;
	
	private Dialog dialog;	// For access to the save button.
	
	private PermissionLevel[] permList = {PermissionLevel.CAN_VIEW, PermissionLevel.CAN_EDIT, PermissionLevel.CAN_EDIT_DELETE, PermissionLevel.CAN_ADMINISTER};	// To enforce order.
	
	@Inject
	public AccessControlListEditorViewImpl(IconsImageBundle iconsImageBundle, 
			SageImageBundle sageImageBundle, UrlCache urlCache, SynapseJSNIUtils synapseJSNIUtils,
			CookieProvider cookies, SynapseClientAsync synapseClient,
			SharingPermissionsGrid permissionsGrid, AddPeopleToAclPanel addPeoplePanel) {
		this.iconsImageBundle = iconsImageBundle;		
		this.sageImageBundle = sageImageBundle;
		this.urlCache = urlCache;
		this.synapseJSNIUtils = synapseJSNIUtils;
		this.cookies = cookies;
		this.synapseClient = synapseClient;
		this.permissionsGrid = permissionsGrid;
		this.addPeoplePanel = addPeoplePanel;
		permissionDisplay = new HashMap<PermissionLevel, String>();
		permissionDisplay.put(PermissionLevel.CAN_VIEW, DisplayConstants.MENU_PERMISSION_LEVEL_CAN_VIEW);
		permissionDisplay.put(PermissionLevel.CAN_EDIT, DisplayConstants.MENU_PERMISSION_LEVEL_CAN_EDIT);
		permissionDisplay.put(PermissionLevel.CAN_EDIT_DELETE, DisplayConstants.MENU_PERMISSION_LEVEL_CAN_EDIT_DELETE);
		permissionDisplay.put(PermissionLevel.CAN_ADMINISTER, DisplayConstants.MENU_PERMISSION_LEVEL_CAN_ADMINISTER);		
		permissionDisplay.put(PermissionLevel.OWNER, DisplayConstants.MENU_PERMISSION_LEVEL_CAN_ADMINISTER);
		
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}
	
	@Override
	public void addAclEntry(AclEntry aclEntry) {
		if (permissionsGrid == null)
			throw new IllegalStateException("Permissions window has not been built yet");
		ListBox permissionsListBox = createEditAccessListBox(aclEntry);
		if (!aclEntry.isIndividual()) {
			permissionsGrid.insert(aclEntry, 0, permissionsListBox); // insert groups first // TODO: PUBLIC is just a group? No team?
		} else if (aclEntry.isOwner()) {
			//owner should be the first (after groups, if present)
			int insertIndex = 0;
			for (; insertIndex < permissionsGrid.getCount(); insertIndex++) {
				if (permissionsGrid.getAt(insertIndex).isIndividual())
					break;
			}
			permissionsGrid.insert(aclEntry, insertIndex, permissionsListBox); // insert owner
		}
		else
			permissionsGrid.add(aclEntry, permissionsListBox);
	}
	
	@Override
	public void setPublicPrincipalIds(PublicPrincipalIds publicPrincipalIds) {
		this.publicPrincipalIds = publicPrincipalIds;
	}
	
	@Override
	public void setIsPubliclyVisible(Boolean isPubliclyVisible) {
		this.isPubliclyVisible = isPubliclyVisible;
		if (isPubliclyVisible != null) {
			addPeoplePanel.setMakePublicButtonDisplay(isPubliclyVisible);
		}
	}
	
	@Override
	public void clear() {
		super.clear();
		permissionsGrid.clear();
	}
	
	@Override
	public void buildWindow(boolean isInherited, boolean canEnableInheritance, boolean unsavedChanges, boolean canChangePermission) {		
		clear();
		
		// Display Permissions grid.
		showEditColumns = canChangePermission && !isInherited;
		CallbackP<Long> removeUserCallback = null;
		if (showEditColumns) {
			removeUserCallback = new CallbackP<Long>() {
					@Override
					public void invoke(Long principalId) {
						if (dialog != null)
							dialog.getPrimaryButton().setEnabled(true);
						presenter.removeAccess(principalId);
					}
				};
		}
		permissionsGrid.configure(removeUserCallback);
		add(permissionsGrid.asWidget());
		
		if (!canChangePermission) {
			// Inform user of restricted privileges.
			Label canNotModify = new Label();
			canNotModify.setText(CANNOT_MODIFY_ACL_TEXT);
			add(canNotModify);
		} else {
			if(isInherited) {
				// Notify user of inherited sharing settings.
				Label readOnly = new Label(DisplayConstants.PERMISSIONS_INHERITED_TEXT);		
				add(readOnly);
				
				// 'Create ACL' button
				org.gwtbootstrap3.client.ui.Button createAclButton = new org.gwtbootstrap3.client.ui.Button(DisplayConstants.BUTTON_PERMISSIONS_CREATE_NEW_ACL, IconType.PLUS, new ClickHandler() {
	
					@Override
					public void onClick(ClickEvent event) {
						presenter.createAcl();
					}
					
				});
				createAclButton.setType(ButtonType.SUCCESS);
				createAclButton.addStyleName("margin-top-10");
				
				Tooltip toolTipAndCreateAclButton = new Tooltip();
				toolTipAndCreateAclButton.setWidget(createAclButton);
				toolTipAndCreateAclButton.setText(DisplayConstants.PERMISSIONS_CREATE_NEW_ACL_TEXT);
				toolTipAndCreateAclButton.setPlacement(Placement.BOTTOM);
				add(toolTipAndCreateAclButton);
			} else {
				// Configure AddPeopleToAclPanel.
				CallbackP<Void> selectPermissionCallback = new CallbackP<Void>() {
					@Override
					public void invoke(Void param) {
						presenter.setUnsavedViewChanges(true);
					}
				};
				
				CallbackP<Void> addPersonCallback = new CallbackP<Void>() {
					@Override
					public void invoke(Void param) {
						addPersonToAcl();
					}
				};
				
				CallbackP<Void> makePublicCallback = new CallbackP<Void>() {
					@Override
					public void invoke(Void param) {
						if (dialog != null)
							dialog.getPrimaryButton().setEnabled(true);
						// Add the ability for PUBLIC to see this entity.
						if (isPubliclyVisible) {
							presenter.makePrivate();
						} else {
							if (publicPrincipalIds.getPublicAclPrincipalId() != null) {
								presenter.setAccess(publicPrincipalIds.getPublicAclPrincipalId(), PermissionLevel.CAN_VIEW);
							}
						}
					}
				};
				
				addPeoplePanel.configure(permList, permissionDisplay, selectPermissionCallback, addPersonCallback, makePublicCallback, isPubliclyVisible);
				add(addPeoplePanel.asWidget());
				
				// 'Delete ACL' button
				org.gwtbootstrap3.client.ui.Button deleteAclButton = new org.gwtbootstrap3.client.ui.Button(DisplayConstants.BUTTON_PERMISSIONS_DELETE_ACL);
				deleteAclButton.setType(ButtonType.DANGER);
				deleteAclButton.setSize(ButtonSize.EXTRA_SMALL);
				deleteAclButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent ce) {
						presenter.deleteAcl();					
						}
				});
					
				Tooltip toolTipAndDeleteAclButton = new Tooltip();
				toolTipAndDeleteAclButton.setWidget(deleteAclButton);
				toolTipAndDeleteAclButton.setText(DisplayConstants.PERMISSIONS_DELETE_ACL_TEXT);
				toolTipAndDeleteAclButton.setPlacement(Placement.BOTTOM);
				deleteAclButton.setEnabled(canEnableInheritance);
				add(toolTipAndDeleteAclButton);
			}
		}
	}
	
	@Override
	public Boolean isNotifyPeople(){
		return addPeoplePanel.getNotifyPeopleCheckBox().getValue();
	}
	
	@Override
	public void setIsNotifyPeople(Boolean value) {
		if (value != null)
			addPeoplePanel.getNotifyPeopleCheckBox().setValue(value);
	}
	
	@Override
	public void setDialog(Dialog dialog) {
		this.dialog = dialog;
		if (dialog != null)
			dialog.getPrimaryButton().setEnabled(false);
	}
	
	@Override
	public void showLoading() {
		this.clear();
		this.add(new HTML(SafeHtmlUtils.fromSafeConstant(DisplayUtils.getIconHtml(sageImageBundle.loading16()) + " Loading...")));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void showErrorMessage(String message) {
		DisplayUtils.showErrorMessage(message);
	}

	@Override
	public void showInfo(String title, String message) {
		DisplayUtils.showInfo(title, message);
	}
	
	@Override
	public void showInfoSuccess(String title, String message) {
		DisplayUtils.showInfo(title, message);
	}
	
	@Override
	public void showInfoError(String title, String message) {
		DisplayUtils.showErrorMessage(message);
	}
	
	/*
	 * Private Methods
	 */	
	private void showAddMessage(String message) {
		// TODO : put this on the form somewhere
		showErrorMessage(message);
	}

	public static Grid<PermissionsTableEntry> createPermissionsGrid(
			ListStore<PermissionsTableEntry> permissionsStore,
			GridCellRenderer<PermissionsTableEntry> peopleRenderer,
			GridCellRenderer<PermissionsTableEntry> buttonRenderer,
			GridCellRenderer<PermissionsTableEntry> removeRenderer,
			boolean isEditable) {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();
		column.setId(PRINCIPAL_COLUMN_ID);
		column.setHeader("People");
		column.setWidth(200);
		column.setRenderer(peopleRenderer);
		configs.add(column);

		column = new ColumnConfig();
		column.setId(ACCESS_COLUMN_ID);
		column.setHeader("Access");
		column.setWidth(110);
		column.setRenderer(buttonRenderer);
		column.setStyle(STYLE_VERTICAL_ALIGN_MIDDLE);
		configs.add(column);

		column = new ColumnConfig();
		column.setId(REMOVE_COLUMN_ID);
		column.setHeader("");
		column.setWidth(25);
		column.setRenderer(removeRenderer);
		column.setStyle(STYLE_VERTICAL_ALIGN_MIDDLE);
		column.setHidden(!isEditable);
		configs.add(column);

		Grid<PermissionsTableEntry> permissionsGrid = new Grid<PermissionsTableEntry>(
				permissionsStore, new ColumnModel(configs));
		permissionsGrid.setAutoExpandColumn(PRINCIPAL_COLUMN_ID);
		permissionsGrid.setBorders(true);
		permissionsGrid.setWidth(520);
		permissionsGrid.setHeight(180);
		return permissionsGrid;
	}
	
	private ListBox createEditAccessListBox(final AclEntry aclEntry) {
		final Long principalId = Long.parseLong(aclEntry.getOwnerId());
		
		final ListBox listBox = new ListBox();
		
		if (aclEntry.isOwner()) {
			listBox.addItem("Owner");
			listBox.setEnabled(false);
			return listBox;
		}
		
		PermissionLevel permLevel = AclUtils.getPermissionLevel(new HashSet<ACCESS_TYPE>(aclEntry.getAccessTypes()));
		for (int i = 0; i < permList.length; i++) {
			listBox.addItem(permissionDisplay.get(permList[i]));
			if (permList[i].equals(permLevel))
				listBox.setSelectedIndex(i);
		}
		
		listBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				presenter.setAccess(principalId, permList[listBox.getSelectedIndex()]);
			}
		});
		
		return listBox;
	}

	public static GridCellRenderer<PermissionsTableEntry> createPeopleRenderer(
			final PublicPrincipalIds publicPrincipalIds, 
			final SynapseJSNIUtils synapseJSNIUtils,
			final IconsImageBundle iconsImageBundle) {
		GridCellRenderer<PermissionsTableEntry> personRenderer = new GridCellRenderer<PermissionsTableEntry>() {
			@Override
			public Object render(PermissionsTableEntry model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<PermissionsTableEntry> store,
					Grid<PermissionsTableEntry> grid) {
				PermissionsTableEntry entry = store.getAt(rowIndex);
				AclEntry aclEntry = entry.getAclEntry();
				String principalHtml = "";
				Long publicPrincipalId = publicPrincipalIds.getPublicAclPrincipalId();
				Long authenticatedPrincipalId = publicPrincipalIds.getAuthenticatedAclPrincipalId();
				Long anonymousUserPrincipalId = publicPrincipalIds.getAnonymousUserPrincipalId();
				
				if (aclEntry != null & aclEntry.getOwnerId() != null) {
					if (publicPrincipalId != null && aclEntry.getOwnerId().equals(publicPrincipalId.toString())) {
						//is public group
						principalHtml = DisplayUtils.getUserNameDescriptionHtml(DisplayConstants.PUBLIC_ACL_TITLE, DisplayConstants.PUBLIC_ACL_DESCRIPTION);
					} else if (authenticatedPrincipalId != null && aclEntry.getOwnerId().equals(authenticatedPrincipalId.toString())) {
						//is authenticated group
						principalHtml = DisplayUtils.getUserNameDescriptionHtml(DisplayConstants.AUTHENTICATED_USERS_ACL_TITLE, DisplayConstants.AUTHENTICATED_USERS_ACL_DESCRIPTION);	
					} else if (anonymousUserPrincipalId != null && aclEntry.getOwnerId().equals(anonymousUserPrincipalId.toString())) {
						//is anonymous user
						principalHtml = DisplayUtils.getUserNameDescriptionHtml(DisplayConstants.PUBLIC_USER_ACL_TITLE, DisplayConstants.PUBLIC_USER_ACL_DESCRIPTION);
					} else {
						principalHtml = DisplayUtils.getUserNameDescriptionHtml(aclEntry.getTitle(), aclEntry.getSubtitle());
					}
				}
				
				String iconHtml = "";
				if (publicPrincipalId != null && aclEntry.getOwnerId().equals(publicPrincipalId.toString())){
					ImageResource icon = iconsImageBundle.globe32();
					iconHtml = DisplayUtils.getIconThumbnailHtml(icon);	
				} else if (!aclEntry.isIndividual()) {
					//if a group, then try to fill in the icon from the team
					String url = DisplayUtils.createTeamIconUrl(
							synapseJSNIUtils.getBaseFileHandleUrl(), 
							aclEntry.getOwnerId()
					);
					iconHtml = DisplayUtils.getThumbnailPicHtml(url);
				} else {
					// try to get the userprofile picture
					String url = DisplayUtils.createUserProfilePicUrl(
							synapseJSNIUtils.getBaseProfileAttachmentUrl(), 
							aclEntry.getOwnerId() 
					);
					iconHtml = DisplayUtils.getThumbnailPicHtml(url);
				}
				return iconHtml + "&nbsp;&nbsp;" + principalHtml;
			}
			
		};
		return personRenderer;
	}

	public static GridCellRenderer<PermissionsTableEntry> createRemoveRenderer(final IconsImageBundle iconsImageBundle, final CallbackP<Long> callback) {
		GridCellRenderer<PermissionsTableEntry> removeButton = new GridCellRenderer<PermissionsTableEntry>() {  			   
			@Override  
			public Object render(final PermissionsTableEntry model, String property, ColumnData config, int rowIndex,  
				  final int colIndex, ListStore<PermissionsTableEntry> store, Grid<PermissionsTableEntry> grid) {				 
				  final PermissionsTableEntry entry = store.getAt(rowIndex);
					Anchor removeAnchor = new Anchor();
					removeAnchor.setHTML(DisplayUtils.getIconHtml(iconsImageBundle.deleteButton16()));
					removeAnchor.addClickHandler(new ClickHandler() {			
						@Override
						public void onClick(ClickEvent event) {
							Long principalId = (Long.parseLong(entry.getAclEntry().getOwnerId()));
							callback.invoke(principalId);
						}
					});
					return removeAnchor;
			  }
			};  
		return removeButton;
	}
	
	@Override
	public void alertUnsavedViewChanges(final Callback saveCallback) {
		DisplayUtils.showConfirmDialog(DisplayConstants.UNSAVED_CHANGES, DisplayConstants.ADD_ACL_UNSAVED_CHANGES, 
				new Callback() {
					@Override
					public void invoke() {
						addPersonToAcl();
						saveCallback.invoke();
					}
				});
	}

	private void addPersonToAcl() {
		UserGroupSuggestBox peopleSuggestBox = addPeoplePanel.getSuggestBox();
		if(peopleSuggestBox.getSelectedSuggestion() != null) {
			String principalIdStr = peopleSuggestBox.getSelectedSuggestion().getHeader().getOwnerId();
			Long principalId = (Long.parseLong(principalIdStr));
			
			if (addPeoplePanel.getSelectedPermissionLevel() != null) {
				PermissionLevel level = addPeoplePanel.getSelectedPermissionLevel();
				presenter.setAccess(principalId, level);
				
				// clear selections
				peopleSuggestBox.clear();
				presenter.setUnsavedViewChanges(false);
			} else {
				showAddMessage("Please select a permission level to grant.");
			}
		} else {
			showAddMessage("Please select a user or team to grant permission to.");
		}
	}

}



/******************************************************************************************
 * 
 * Old Class
 * 
 *****************************************************************************************/

//package org.sagebionetworks.web.client.widget.sharing;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//
//import org.sagebionetworks.repo.model.ACCESS_TYPE;
//import org.sagebionetworks.web.client.DisplayConstants;
//import org.sagebionetworks.web.client.DisplayUtils;
//import org.sagebionetworks.web.client.IconsImageBundle;
//import org.sagebionetworks.web.client.SageImageBundle;
//import org.sagebionetworks.web.client.SynapseClientAsync;
//import org.sagebionetworks.web.client.SynapseJSNIUtils;
//import org.sagebionetworks.web.client.UrlCache;
//import org.sagebionetworks.web.client.cookie.CookieProvider;
//import org.sagebionetworks.web.client.utils.Callback;
//import org.sagebionetworks.web.client.utils.CallbackP;
//import org.sagebionetworks.web.client.widget.search.UserGroupSuggestBox;
//import org.sagebionetworks.web.shared.PublicPrincipalIds;
//import org.sagebionetworks.web.shared.users.AclEntry;
//import org.sagebionetworks.web.shared.users.AclUtils;
//import org.sagebionetworks.web.shared.users.PermissionLevel;
//
//import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
//import com.extjs.gxt.ui.client.data.ModelData;
//import com.extjs.gxt.ui.client.event.ButtonEvent;
//import com.extjs.gxt.ui.client.event.Events;
//import com.extjs.gxt.ui.client.event.GridEvent;
//import com.extjs.gxt.ui.client.event.Listener;
//import com.extjs.gxt.ui.client.event.MenuEvent;
//import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
//import com.extjs.gxt.ui.client.event.SelectionChangedListener;
//import com.extjs.gxt.ui.client.event.SelectionListener;
//import com.extjs.gxt.ui.client.store.ListStore;
//import com.extjs.gxt.ui.client.widget.BoxComponent;
//import com.extjs.gxt.ui.client.widget.HorizontalPanel;
//import com.extjs.gxt.ui.client.widget.Label;
//import com.extjs.gxt.ui.client.widget.LayoutContainer;
//import com.extjs.gxt.ui.client.widget.button.Button;
//import com.extjs.gxt.ui.client.widget.form.ComboBox;
//import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
//import com.extjs.gxt.ui.client.widget.form.FieldSet;
//import com.extjs.gxt.ui.client.widget.form.FormPanel;
//import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
//import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
//import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
//import com.extjs.gxt.ui.client.widget.grid.ColumnData;
//import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
//import com.extjs.gxt.ui.client.widget.grid.Grid;
//import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
//import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
//import com.extjs.gxt.ui.client.widget.layout.FormLayout;
//import com.extjs.gxt.ui.client.widget.layout.MarginData;
//import com.extjs.gxt.ui.client.widget.layout.TableData;
//import com.extjs.gxt.ui.client.widget.menu.Menu;
//import com.extjs.gxt.ui.client.widget.menu.MenuItem;
//import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.event.dom.client.ClickHandler;
//import com.google.gwt.resources.client.ImageResource;
//import com.google.gwt.safehtml.shared.SafeHtmlUtils;
//import com.google.gwt.user.client.Element;
//import com.google.gwt.user.client.ui.AbstractImagePrototype;
//import com.google.gwt.user.client.ui.Anchor;
//import com.google.gwt.user.client.ui.CheckBox;
//import com.google.gwt.user.client.ui.FlowPanel;
//import com.google.gwt.user.client.ui.HTML;
//import com.google.gwt.user.client.ui.SuggestBox;
//import com.google.gwt.user.client.ui.Widget;
//import com.google.inject.Inject;
//
//public class AccessControlListEditorViewImpl extends LayoutContainer implements AccessControlListEditorView {
// 
//	private static final int FIELD_WIDTH = 500;
//	private static final String STYLE_VERTICAL_ALIGN_MIDDLE = "vertical-align:middle !important;";
//	private static final String PRINCIPAL_COLUMN_ID = "principalData";
//	private static final String ACCESS_COLUMN_ID = "accessData";
//	private static final String REMOVE_COLUMN_ID = "removeData";
//	private static final int DEFAULT_WIDTH = 380;
//	private static final int BUTTON_PADDING = 3;
//	
//	private Presenter presenter;
//	private IconsImageBundle iconsImageBundle;
//	private UrlCache urlCache;
//	private Grid<PermissionsTableEntry> permissionsGrid;
//	private Map<PermissionLevel, String> permissionDisplay;
//	private SageImageBundle sageImageBundle;
//	private SynapseJSNIUtils synapseJSNIUtils;
//	private CookieProvider cookies;
//	private SynapseClientAsync synapseClient;
//	private ListStore<PermissionsTableEntry> permissionsStore;
//	private ColumnModel columnModel;
//	private PublicPrincipalIds publicPrincipalIds;
//	private Boolean isPubliclyVisible;
//	private com.google.gwt.user.client.ui.Button publicButton;
//	private SimpleComboBox<PermissionLevelSelect> permissionLevelCombo;
//	private UserGroupSuggestBox peopleSuggestBox;
//	private CheckBox notifyPeopleCheckbox;
//	private boolean showEditColumns;
//	
//	@Inject
//	public AccessControlListEditorViewImpl(IconsImageBundle iconsImageBundle, 
//			SageImageBundle sageImageBundle, UrlCache urlCache, SynapseJSNIUtils synapseJSNIUtils,
//			CookieProvider cookies, SynapseClientAsync synapseClient, UserGroupSuggestBox peopleCombo) {
//		this.iconsImageBundle = iconsImageBundle;		
//		this.sageImageBundle = sageImageBundle;
//		this.urlCache = urlCache;
//		this.synapseJSNIUtils = synapseJSNIUtils;
//		this.cookies = cookies;
//		this.synapseClient = synapseClient;
//		this.peopleSuggestBox = peopleCombo;
//		permissionDisplay = new HashMap<PermissionLevel, String>();
//		permissionDisplay.put(PermissionLevel.CAN_VIEW, DisplayConstants.MENU_PERMISSION_LEVEL_CAN_VIEW);
//		permissionDisplay.put(PermissionLevel.CAN_EDIT, DisplayConstants.MENU_PERMISSION_LEVEL_CAN_EDIT);
//		permissionDisplay.put(PermissionLevel.CAN_EDIT_DELETE, DisplayConstants.MENU_PERMISSION_LEVEL_CAN_EDIT_DELETE);
//		permissionDisplay.put(PermissionLevel.CAN_ADMINISTER, DisplayConstants.MENU_PERMISSION_LEVEL_CAN_ADMINISTER);		
//		permissionDisplay.put(PermissionLevel.OWNER, DisplayConstants.MENU_PERMISSION_LEVEL_CAN_ADMINISTER);
//	}
//	
//	@Override
//	protected void onRender(Element parent, int pos) {
//		super.onRender(parent, pos);
//	}
//		
//	@Override
//	public Widget asWidget() {
//		return this;
//	}
//	
//	@Override
//	public void addAclEntry(AclEntry aclEntry) {
//		if (permissionsStore == null || columnModel == null || permissionsGrid == null)
//			throw new IllegalStateException("Permissions window has not been built yet");
//		if (!aclEntry.isIndividual())
//			permissionsStore.insert(new PermissionsTableEntry(permissionDisplay, aclEntry), 0); // insert groups first
//		else if (aclEntry.isOwner()) {
//			//owner should be the first (after groups, if present)
//			int insertIndex = 0;
//			for (; insertIndex < permissionsStore.getCount(); insertIndex++) {
//				if (permissionsStore.getAt(insertIndex).getAclEntry().isIndividual())
//					break;
//			}
//			permissionsStore.insert(new PermissionsTableEntry(permissionDisplay, aclEntry), insertIndex); // insert owner
//		}
//		else
//			permissionsStore.add(new PermissionsTableEntry(permissionDisplay, aclEntry));
//		permissionsGrid.reconfigure(permissionsStore, columnModel);
//	}
//	
//	@Override
//	public void setPublicPrincipalIds(PublicPrincipalIds publicPrincipalIds) {
//		this.publicPrincipalIds = publicPrincipalIds;
//	}
//	
//	@Override
//	public void setIsPubliclyVisible(Boolean isPubliclyVisible) {
//		this.isPubliclyVisible = isPubliclyVisible;
//		if (publicButton != null) {
//			if(isPubliclyVisible) {
//				DisplayUtils.relabelIconButton(publicButton, DisplayConstants.BUTTON_REVOKE_PUBLIC_ACL, "glyphicon-lock");
////				DisplayUtils.addTooltip(publicButton, DisplayConstants.BUTTON_REVOKE_PUBLIC_ACL_TOOLTIP);
//			} else {
//				DisplayUtils.relabelIconButton(publicButton, DisplayConstants.BUTTON_MAKE_PUBLIC_ACL, "glyphicon-globe");
////				DisplayUtils.addTooltip(publicButton, DisplayConstants.BUTTON_MAKE_PUBLIC_ACL_TOOLTIP);
//			}
//		}
//	}
//	
//	@Override
//	public void buildWindow(boolean isInherited, boolean canEnableInheritance, boolean unsavedChanges, boolean canChangePermission) {		
//		this.removeAll(true);
//		this.setLayout(new FlowLayout(10));
//		
//		// show existing permissions
//		permissionsStore = new ListStore<PermissionsTableEntry>();
//		showEditColumns = canChangePermission && !isInherited;
//		permissionsGrid = AccessControlListEditorViewImpl.createPermissionsGrid(
//				permissionsStore, 
//				AccessControlListEditorViewImpl.createPeopleRenderer(publicPrincipalIds, synapseJSNIUtils, iconsImageBundle), 
//				createButtonRenderer(), 
//				AccessControlListEditorViewImpl.createRemoveRenderer(iconsImageBundle, new CallbackP<Long>() {
//					@Override
//					public void invoke(Long principalId) {
//						presenter.removeAccess(principalId);
//					}
//				}),
//				showEditColumns);
//
//		add(permissionsGrid, new MarginData(5, 0, 0, 0));
//		columnModel = permissionsGrid.getColumnModel();
//		
//		// create panel to hold ACL management buttons
//		HorizontalPanel hPanel = new HorizontalPanel();
//		hPanel.setWidth(FIELD_WIDTH);
//		TableData tdLeft = new TableData("1%", "100%");
//		tdLeft.setPadding(BUTTON_PADDING);
//		TableData tdRight = new TableData();
//		tdRight.setPadding(BUTTON_PADDING);
//		if (canChangePermission) {
//			if(isInherited) { 
//				Label readOnly = new Label(DisplayConstants.PERMISSIONS_INHERITED_TEXT);		
//				readOnly.setWidth(450);
//				add(readOnly, new MarginData(15, 0, 0, 0));			
//				
//				// 'Create ACL' button
//				Button createAclButton = new Button(DisplayConstants.BUTTON_PERMISSIONS_CREATE_NEW_ACL, AbstractImagePrototype.create(iconsImageBundle.addSquare16()));
//				createAclButton.setToolTip(new ToolTipConfig("Warning", DisplayConstants.PERMISSIONS_CREATE_NEW_ACL_TEXT));
//				createAclButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
//					@Override
//					public void componentSelected(ButtonEvent ce) {
//						presenter.createAcl();
//					}
//				});
//				hPanel.setHorizontalAlign(HorizontalAlignment.LEFT);
//				hPanel.add(createAclButton, tdLeft);
//			} else {
//				// show add people view
//				FormPanel form2 = new FormPanel();  
//				form2.setFrame(false);  
//				form2.setHeaderVisible(false);  
//				form2.setAutoWidth(true);			
//				form2.setLayout(new FlowLayout());
//				
//				FormLayout layout = new FormLayout();  
//				layout.setLabelWidth(75);
//				layout.setDefaultWidth(DEFAULT_WIDTH);
//				  
//				FieldSet fieldSet = new FieldSet();  
//				fieldSet.setHeading(DisplayConstants.LABEL_PERMISSION_TEXT_ADD_PEOPLE);  
//				fieldSet.setCheckboxToggle(false);
//				fieldSet.setCollapsible(false);			
//				fieldSet.setLayout(layout);
//				fieldSet.setWidth(FIELD_WIDTH);
//				
//				
//				// user/group Suggest Box
//				peopleSuggestBox.configureURLs(synapseJSNIUtils.getBaseFileHandleUrl(), synapseJSNIUtils.getBaseProfileAttachmentUrl());
//				peopleSuggestBox.setPlaceholderText("Enter name...");
//				peopleSuggestBox.setWidth(DEFAULT_WIDTH + "px");
//				HorizontalPanel userGroupPanel = new HorizontalPanel();
//				userGroupPanel.addStyleName("x-form-item");	// TODO: Remove when moving away from gxt components.
//				
//				Label nameLbl = new Label("Name:");
//				nameLbl.addStyleName("width-80");
//				userGroupPanel.add(nameLbl);
//				userGroupPanel.add(peopleSuggestBox.asWidget());
//				fieldSet.add(userGroupPanel);
//				
//				// permission level combobox
//				permissionLevelCombo = new SimpleComboBox<PermissionLevelSelect>();
//				permissionLevelCombo.add(new PermissionLevelSelect(permissionDisplay.get(PermissionLevel.CAN_VIEW), PermissionLevel.CAN_VIEW));
//				permissionLevelCombo.add(new PermissionLevelSelect(permissionDisplay.get(PermissionLevel.CAN_EDIT), PermissionLevel.CAN_EDIT));
//				permissionLevelCombo.add(new PermissionLevelSelect(permissionDisplay.get(PermissionLevel.CAN_EDIT_DELETE), PermissionLevel.CAN_EDIT_DELETE));
//				permissionLevelCombo.add(new PermissionLevelSelect(permissionDisplay.get(PermissionLevel.CAN_ADMINISTER), PermissionLevel.CAN_ADMINISTER));			
//				permissionLevelCombo.setEmptyText("Select access level...");
//				permissionLevelCombo.setFieldLabel("Access Level");
//				permissionLevelCombo.setTypeAhead(false);
//				permissionLevelCombo.setEditable(false);
//				permissionLevelCombo.setForceSelection(true);
//				permissionLevelCombo.setTriggerAction(TriggerAction.ALL);
//				permissionLevelCombo.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<PermissionLevelSelect>>() {				
//					@Override
//					public void selectionChanged(SelectionChangedEvent<SimpleComboValue<PermissionLevelSelect>> se) {
//						presenter.setUnsavedViewChanges(true);
//					}
//				});
//				fieldSet.add(permissionLevelCombo);
//				
//				// share button and listener
//				Button shareButton = new Button("Add");
//				shareButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
//					@Override
//					public void componentSelected(ButtonEvent ce) {
//						addPersonToAcl();
//					}
//				});
//	
//				fieldSet.add(shareButton);
//				
//				form2.add(fieldSet);
//				
//				//Make Public button
//				if (publicButton == null) {
//					publicButton = DisplayUtils.createButton("Test");
//					publicButton.addStyleName("btn-xs");
//					setIsPubliclyVisible(false);
//					
//					publicButton.addClickHandler(new ClickHandler() {
//						@Override
//						public void onClick(ClickEvent event) {
//							//add the ability for PUBLIC to see this entity
//							if (isPubliclyVisible) {
//								presenter.makePrivate();
//							}
//							else {
//								if (publicPrincipalIds.getPublicAclPrincipalId() != null) {
//									presenter.setAccess(publicPrincipalIds.getPublicAclPrincipalId(), PermissionLevel.CAN_VIEW);
//								}
//							}
//						}
//					});
//				}
//				
//				if (notifyPeopleCheckbox == null) {
//					notifyPeopleCheckbox = new CheckBox("Notify people via email");
//					setIsNotifyPeople(true);
//					DisplayUtils.addTooltip(notifyPeopleCheckbox, DisplayConstants.NOTIFY_PEOPLE_TOOLTIP);
//				}
//				
//				FlowPanel cbPanel = new FlowPanel();
//				cbPanel.addStyleName("margin-top-0 margin-right-10 checkbox right");
//				cbPanel.add(notifyPeopleCheckbox);
//				
//				FlowPanel publicButtonPanel = new FlowPanel();
//				publicButtonPanel.addStyleName("margin-top-0 margin-left-10 left");
//				publicButtonPanel.add(publicButton);
//				
//				FlowPanel publicButtonAndCheckbox = new FlowPanel();
//				publicButtonAndCheckbox.add(publicButtonPanel);
//				publicButtonAndCheckbox.add(cbPanel);
//				form2.add(publicButtonAndCheckbox);
//				add(form2);
//				
//				// 'Delete ACL' button
//				Button deleteAclButton = new Button(DisplayConstants.BUTTON_PERMISSIONS_DELETE_ACL, AbstractImagePrototype.create(iconsImageBundle.deleteButton16()));
//				deleteAclButton.setToolTip(new ToolTipConfig("Warning", DisplayConstants.PERMISSIONS_DELETE_ACL_TEXT));
//				deleteAclButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
//					@Override
//					public void componentSelected(ButtonEvent ce) {
//						presenter.deleteAcl();					
//					}
//				});
//				
//				hPanel.setHorizontalAlign(HorizontalAlignment.LEFT);
//				//delete button takes up the rest of the line space
//				hPanel.add(deleteAclButton, tdLeft);
//				deleteAclButton.setEnabled(canEnableInheritance);
//			}
//		}
//		
//		// Unsaved changes label
////		Label blank = new Label("");
////		Label unsavedChangesLabel = new Label(DisplayUtils.getIconHtml(iconsImageBundle.warning16()) + " " + DisplayConstants.PERMISSIONS_UNSAVED_CHANGES);
////	   	hPanel.setHorizontalAlign(HorizontalAlignment.RIGHT);
////		hPanel.add(unsavedChanges ? unsavedChangesLabel : blank, tdRight);
//		
//		this.add(hPanel, new MarginData(10, 0, 0, 0));
//		this.layout(true);
//	}
//	
//	@Override
//	public Boolean isNotifyPeople(){
//		return notifyPeopleCheckbox.getValue();
//	}
//	
//	@Override
//	public void setIsNotifyPeople(Boolean value) {
//		if (notifyPeopleCheckbox != null && value != null)
//			notifyPeopleCheckbox.setValue(value);
//	}
//	
//	@Override
//	public void showLoading() {
//		this.removeAll(true);
//		this.add(new HTML(SafeHtmlUtils.fromSafeConstant(DisplayUtils.getIconHtml(sageImageBundle.loading16()) + " Loading...")));
//		this.layout(true);
//	}
//
//	@Override
//	public void setPresenter(Presenter presenter) {
//		this.presenter = presenter;
//	}
//
//	@Override
//	public void showErrorMessage(String message) {
//		DisplayUtils.showErrorMessage(message);
//	}
//
//	@Override
//	public void clear() {
//		this.removeAll();
//	}
//
//	@Override
//	public void showInfo(String title, String message) {
//		DisplayUtils.showInfo(title, message);
//	}
//	
//	@Override
//	public void showInfoSuccess(String title, String message) {
//		DisplayUtils.showInfo(title, message);
//	}
//	
//	@Override
//	public void showInfoError(String title, String message) {
//		DisplayUtils.showErrorMessage(message);
//	}
//	
//	/*
//	 * Private Methods
//	 */	
//	private void showAddMessage(String message) {
//		// TODO : put this on the form somewhere
//		showErrorMessage(message);
//	}
//
//	public static Grid<PermissionsTableEntry> createPermissionsGrid(
//			ListStore<PermissionsTableEntry> permissionsStore,
//			GridCellRenderer<PermissionsTableEntry> peopleRenderer,
//			GridCellRenderer<PermissionsTableEntry> buttonRenderer,
//			GridCellRenderer<PermissionsTableEntry> removeRenderer,
//			boolean isEditable) {
//		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
//
//		ColumnConfig column = new ColumnConfig();
//		column.setId(PRINCIPAL_COLUMN_ID);
//		column.setHeader("People");
//		column.setWidth(200);
//		column.setRenderer(peopleRenderer);
//		configs.add(column);
//
//		column = new ColumnConfig();
//		column.setId(ACCESS_COLUMN_ID);
//		column.setHeader("Access");
//		column.setWidth(110);
//		column.setRenderer(buttonRenderer);
//		column.setStyle(STYLE_VERTICAL_ALIGN_MIDDLE);
//		configs.add(column);
//
//		column = new ColumnConfig();
//		column.setId(REMOVE_COLUMN_ID);
//		column.setHeader("");
//		column.setWidth(25);
//		column.setRenderer(removeRenderer);
//		column.setStyle(STYLE_VERTICAL_ALIGN_MIDDLE);
//		column.setHidden(!isEditable);
//		configs.add(column);
//
//		Grid<PermissionsTableEntry> permissionsGrid = new Grid<PermissionsTableEntry>(
//				permissionsStore, new ColumnModel(configs));
//		permissionsGrid.setAutoExpandColumn(PRINCIPAL_COLUMN_ID);
//		permissionsGrid.setBorders(true);
//		permissionsGrid.setWidth(520);
//		permissionsGrid.setHeight(180);
//		return permissionsGrid;
//	}
//	
//	private Menu createEditAccessMenu(final AclEntry aclEntry) {
//		final Long principalId = Long.parseLong(aclEntry.getOwnerId());
//		Menu menu = new Menu();
//		menu.setEnableScrolling(false);
//		MenuItem item;
//		
//		item = new MenuItem(permissionDisplay.get(PermissionLevel.CAN_VIEW));			
//		item.addSelectionListener(new SelectionListener<MenuEvent>() {
//			public void componentSelected(MenuEvent menuEvent) {
//				presenter.setAccess(principalId, PermissionLevel.CAN_VIEW);
//			}
//		});
//		menu.add(item);
//
//		item = new MenuItem(permissionDisplay.get(PermissionLevel.CAN_EDIT));			
//		item.addSelectionListener(new SelectionListener<MenuEvent>() {
//			public void componentSelected(MenuEvent menuEvent) {
//				presenter.setAccess(principalId, PermissionLevel.CAN_EDIT);
//			}
//		});
//		menu.add(item);
//
//		item = new MenuItem(permissionDisplay.get(PermissionLevel.CAN_EDIT_DELETE));			
//		item.addSelectionListener(new SelectionListener<MenuEvent>() {
//			public void componentSelected(MenuEvent menuEvent) {
//				presenter.setAccess(principalId, PermissionLevel.CAN_EDIT_DELETE);
//			}
//		});
//		menu.add(item);
//
//		item = new MenuItem(permissionDisplay.get(PermissionLevel.CAN_ADMINISTER));			
//		item.addSelectionListener(new SelectionListener<MenuEvent>() {
//			public void componentSelected(MenuEvent menuEvent) {
//				presenter.setAccess(principalId, PermissionLevel.CAN_ADMINISTER);
//			}
//		});
//		menu.add(item);
//		
//		return menu;
//	}
//
//	public static GridCellRenderer<PermissionsTableEntry> createPeopleRenderer(
//			final PublicPrincipalIds publicPrincipalIds, 
//			final SynapseJSNIUtils synapseJSNIUtils,
//			final IconsImageBundle iconsImageBundle) {
//		GridCellRenderer<PermissionsTableEntry> personRenderer = new GridCellRenderer<PermissionsTableEntry>() {
//			@Override
//			public Object render(PermissionsTableEntry model, String property,
//					ColumnData config, int rowIndex, int colIndex,
//					ListStore<PermissionsTableEntry> store,
//					Grid<PermissionsTableEntry> grid) {
//				PermissionsTableEntry entry = store.getAt(rowIndex);
//				AclEntry aclEntry = entry.getAclEntry();
//				String principalHtml = "";
//				Long publicPrincipalId = publicPrincipalIds.getPublicAclPrincipalId();
//				Long authenticatedPrincipalId = publicPrincipalIds.getAuthenticatedAclPrincipalId();
//				Long anonymousUserPrincipalId = publicPrincipalIds.getAnonymousUserPrincipalId();
//				
//				if (aclEntry != null & aclEntry.getOwnerId() != null) {
//					if (publicPrincipalId != null && aclEntry.getOwnerId().equals(publicPrincipalId.toString())) {
//						//is public group
//						principalHtml = DisplayUtils.getUserNameDescriptionHtml(DisplayConstants.PUBLIC_ACL_TITLE, DisplayConstants.PUBLIC_ACL_DESCRIPTION);
//					} else if (authenticatedPrincipalId != null && aclEntry.getOwnerId().equals(authenticatedPrincipalId.toString())) {
//						//is authenticated group
//						principalHtml = DisplayUtils.getUserNameDescriptionHtml(DisplayConstants.AUTHENTICATED_USERS_ACL_TITLE, DisplayConstants.AUTHENTICATED_USERS_ACL_DESCRIPTION);	
//					} else if (anonymousUserPrincipalId != null && aclEntry.getOwnerId().equals(anonymousUserPrincipalId.toString())) {
//						//is anonymous user
//						principalHtml = DisplayUtils.getUserNameDescriptionHtml(DisplayConstants.PUBLIC_USER_ACL_TITLE, DisplayConstants.PUBLIC_USER_ACL_DESCRIPTION);
//					} else {
//						principalHtml = DisplayUtils.getUserNameDescriptionHtml(aclEntry.getTitle(), aclEntry.getSubtitle());
//					}
//				}
//				
//				String iconHtml = "";
//				if (publicPrincipalId != null && aclEntry.getOwnerId().equals(publicPrincipalId.toString())){
//					ImageResource icon = iconsImageBundle.globe32();
//					iconHtml = DisplayUtils.getIconThumbnailHtml(icon);	
//				} else if (!aclEntry.isIndividual()) {
//					//if a group, then try to fill in the icon from the team
//					String url = DisplayUtils.createTeamIconUrl(
//							synapseJSNIUtils.getBaseFileHandleUrl(), 
//							aclEntry.getOwnerId()
//					);
//					iconHtml = DisplayUtils.getThumbnailPicHtml(url);
//				} else {
//					// try to get the userprofile picture
//					String url = DisplayUtils.createUserProfilePicUrl(
//							synapseJSNIUtils.getBaseProfileAttachmentUrl(), 
//							aclEntry.getOwnerId() 
//					);
//					iconHtml = DisplayUtils.getThumbnailPicHtml(url);
//				}
//				return iconHtml + "&nbsp;&nbsp;" + principalHtml;
//			}
//			
//		};
//		return personRenderer;
//	}
//
//	private GridCellRenderer<PermissionsTableEntry> createButtonRenderer() {
//		GridCellRenderer<PermissionsTableEntry> buttonRenderer = new GridCellRenderer<PermissionsTableEntry>() {  
//			   
//			  private boolean init;  
//			  @Override	   
//			  public Object render(final PermissionsTableEntry model, String property, ColumnData config, final int rowIndex,  
//			      final int colIndex, ListStore<PermissionsTableEntry> store, Grid<PermissionsTableEntry> grid) {
//				  PermissionsTableEntry entry = store.getAt(rowIndex);
//			    if (!init) {  
//			      init = true;  
//			      grid.addListener(Events.ColumnResize, new Listener<GridEvent<PermissionsTableEntry>>() {  
//					   
//			        public void handleEvent(GridEvent<PermissionsTableEntry> be) {  
//			          for (int i = 0; i < be.getGrid().getStore().getCount(); i++) {  
//			            if (be.getGrid().getView().getWidget(i, be.getColIndex()) != null  
//			                && be.getGrid().getView().getWidget(i, be.getColIndex()) instanceof BoxComponent) {  
//			              ((BoxComponent) be.getGrid().getView().getWidget(i, be.getColIndex())).setWidth(be.getWidth() - 10);  
//			            }  
//			          }  
//			        }  
//			      });  
//			    }
//			    if(entry.getAclEntry().isOwner()) {
//				    Button b = new Button(DisplayConstants.MENU_PERMISSION_LEVEL_IS_OWNER);
//				    b.setWidth(grid.getColumnModel().getColumnWidth(colIndex) - 15);
//				    b.disable();
//					return b;		    	
//			    } else {
//				    Button b = new Button((String) model.get(property));  
//				    b.setWidth(grid.getColumnModel().getColumnWidth(colIndex) - 25);  
//				    if (showEditColumns) {
//				    	b.setToolTip("Click to change");
//				    } else {
//				    	b.disable();
//				    }
//				    				  
//				    b.setMenu(createEditAccessMenu(entry.getAclEntry()));
//				    return b;
//			    }
//			  }
//			};  
//			
//			return buttonRenderer;
//	}
//
//	public static GridCellRenderer<PermissionsTableEntry> createRemoveRenderer(final IconsImageBundle iconsImageBundle, final CallbackP<Long> callback) {
//		GridCellRenderer<PermissionsTableEntry> removeButton = new GridCellRenderer<PermissionsTableEntry>() {  			   
//			@Override  
//			public Object render(final PermissionsTableEntry model, String property, ColumnData config, int rowIndex,  
//				  final int colIndex, ListStore<PermissionsTableEntry> store, Grid<PermissionsTableEntry> grid) {				 
//				  final PermissionsTableEntry entry = store.getAt(rowIndex);
//					Anchor removeAnchor = new Anchor();
//					removeAnchor.setHTML(DisplayUtils.getIconHtml(iconsImageBundle.deleteButton16()));
//					removeAnchor.addClickHandler(new ClickHandler() {			
//						@Override
//						public void onClick(ClickEvent event) {
//							Long principalId = (Long.parseLong(entry.getAclEntry().getOwnerId()));
//							callback.invoke(principalId);
//						}
//					});
//					return removeAnchor;
//			  }
//			};  
//		return removeButton;
//	}
//	
//	@Override
//	public void alertUnsavedViewChanges(final Callback saveCallback) {
//		DisplayUtils.showConfirmDialog(DisplayConstants.UNSAVED_CHANGES, DisplayConstants.ADD_ACL_UNSAVED_CHANGES, 
//				new Callback() {
//					@Override
//					public void invoke() {
//						addPersonToAcl();
//						presenter.setUnsavedViewChanges(false);
//						saveCallback.invoke();
//					}
//				});
//	}
//
//	private void addPersonToAcl() {
//		if(peopleSuggestBox.getSelectedSuggestion() != null) {
//			String principalIdStr = peopleSuggestBox.getSelectedSuggestion().getHeader().getOwnerId();
//			Long principalId = (Long.parseLong(principalIdStr));
//			
//			if(permissionLevelCombo.getValue() != null) {
//				PermissionLevel level = permissionLevelCombo.getValue().getValue().getLevel();
//				presenter.setAccess(principalId, level);
//				
//				// clear selections
//				peopleSuggestBox.clear();
//				permissionLevelCombo.clearSelections();
//				presenter.setUnsavedViewChanges(false);
//			} else {
//				showAddMessage("Please select a permission level to grant.");
//			}
//		} else {
//			showAddMessage("Please select a user or team to grant permission to.");
//		}
//	}
//}
