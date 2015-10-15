package com.it.core.menu;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.it.core.R;
import com.it.core.activity.ChangePasswordActivity;
import com.it.core.activity.LoginActivity;
import com.it.core.contact.ContactRepository;
import com.it.core.contact.OnGetContact;
import com.it.core.contact.OnUserParamLoaded;
import com.it.core.contact.UserParam;
import com.it.core.contact.UserParamRepository;
import com.it.core.login.LoginService;
import com.it.core.model.UserInfo;
import com.it.core.tools.TextViewTools;
import com.it.core.tools.ViewTools;

import java.util.ArrayList;

/**
* Fragment used for managing interactions for and presentation of a navigation drawer.
* See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
* design guidelines</a> for a complete explanation of the behaviors implemented here.
*/
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	private static final String ARG_MENU_ITEMS = "MENU_ITEMS";
	private static final String ARG_DRAWER_LAYOUT = "ARG_DRAWER_LAYOUT";
	private static final String ARG_NAVIGATION_DRAWER = "NAVIGATION_DRAWER";
	private static final String ARG_GLOBAL_MENU_ID = "GLOBAL_MENU_ID";
	private static final String ARG_GLOBAL_TITLE_ID = "GLOBAL_TITLE_ID";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
	private View mDrawerView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mIsDrawerLocked = false;
	private int mGlobalMenuId;
	private int mGlobalTitleId;

	private ArrayList<SideMenuItem> mMenuItems;
	private int mDrawerLayoutId;
	private int mNavigationDrawerId;
	private ActionBar mActionBar;


	public static NavigationDrawerFragment newInstance(int drawerLayoutId, int navigationDrawerId, int globalMenuId, int globalTitleId, ArrayList<SideMenuItem> menuItems) {
		NavigationDrawerFragment fragment = new NavigationDrawerFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_DRAWER_LAYOUT, drawerLayoutId);
		args.putInt(ARG_NAVIGATION_DRAWER, navigationDrawerId);
		args.putInt(ARG_GLOBAL_MENU_ID, globalMenuId);
		args.putInt(ARG_GLOBAL_TITLE_ID, globalTitleId);
		args.putSerializable(ARG_MENU_ITEMS, menuItems);
		fragment.setArguments(args);
		return fragment;
	}

    public NavigationDrawerFragment() {
    }

//	public NavigationDrawerFragment(int mGlobalTitleId,int globalMenuId, int customMenuId) {
//		mGlobalMenuId = globalMenuId;
//		mCustomMenuId = customMenuId;
//	}

	public ArrayList<SideMenuItem> getMenuItems(){
		return mMenuItems;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	    if (getArguments() != null) {
		    mDrawerLayoutId = getArguments().getInt(ARG_DRAWER_LAYOUT);
		    mNavigationDrawerId = getArguments().getInt(ARG_NAVIGATION_DRAWER);
		    mGlobalMenuId = getArguments().getInt(ARG_GLOBAL_MENU_ID);
		    mGlobalTitleId = getArguments().getInt(ARG_GLOBAL_TITLE_ID);
		    mMenuItems = (ArrayList<SideMenuItem>)getArguments().getSerializable(ARG_MENU_ITEMS);
	    }

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
//
//        Select either the default item (0) or the last selected item.
//        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
	    mDrawerView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView = (ListView) mDrawerView.findViewById(R.id.drawer_list_view);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		        selectItem(position);
	        }
        });
        mDrawerListView.setAdapter(new DrawerMenuAdapter(getActivity(), mMenuItems));
	    mActionBar = getActivity().getActionBar();
	    initAccountSwitcher();
	    UserParamRepository.getInstance().loadUserParam(getActivity(), new OnUserParamLoaded() {
		    @Override
		    public void onUserParamLoaded() {
			    initAccountSwitcher();
		    }
	    });
	    ContactRepository.getInstance().refreshAuthenticatedUser(getActivity(), new OnGetContact() {
		    @Override
		    public void onGetContact() {
			    initAccountSwitcher();
		    }
	    });
        setUp();
        return mDrawerView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView) && !mIsDrawerLocked;
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     */
    public void setUp(){//int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(mNavigationDrawerId);
        mDrawerLayout = (DrawerLayout) getActivity().findViewById(mDrawerLayoutId);

	    Configuration config = getResources().getConfiguration();
	    if (((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE ||
			    (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) &&
			    config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
		    mIsDrawerLocked = true;
	    } else {
		    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		    mIsDrawerLocked = false;
	    }

//        if(orientation != null && orientation.equals("large-landscape")){
//            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
//            mIsDrawerLocked = true;
//        } else {
//            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//            mIsDrawerLocked = false;
//	        mDrawerLayout.closeDrawers();
//        }
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }
                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
//        ActionBar actionBar = getActionBar();
	    if (mActionBar == null) {
		    return;
	    }
        if (!mIsDrawerLocked) {
            mDrawerLayout.setDrawerListener(mDrawerToggle);
	        mActionBar.setDisplayHomeAsUpEnabled(true);
	        mActionBar.setHomeButtonEnabled(true);
        } else {
	        mActionBar.setDisplayHomeAsUpEnabled(false);
	        mActionBar.setHomeButtonEnabled(false);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        if(mDrawerToggle!=null){
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen() && !mIsDrawerLocked) {
            inflater.inflate(mGlobalMenuId, menu);
            showGlobalContextActionBar();
        }
        if (mIsDrawerLocked){
            //getActivity().invalidateOptionsMenu();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if(item.getItemId() == android.R.id.home){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	/**
	 * Callbacks interfaces that all activities using this fragment must implement.
	 */
	public static interface NavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected(SideMenuItem item);
	}

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
//        ActionBar actionBar = getActionBar();
	    if (mActionBar != null) {
		    mActionBar.setDisplayShowTitleEnabled(true);
		    mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		    mActionBar.setTitle(mGlobalTitleId);
	    }
    }

//    @Nullable
//    private ActionBar getActionBar() {
//        return getActivity().getActionBar();
//    }

	private void selectItem(int position) {
		mCurrentSelectedPosition = position;
		if (mDrawerListView != null) {
			mDrawerListView.setItemChecked(position, true);
		}
		if (mDrawerLayout != null && !mIsDrawerLocked) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(mMenuItems.get(position));
		}
	}

	private void initAccountSwitcher() {
		mDrawerView.findViewById(R.id.account_switcher).setOnClickListener(null);
		ImageView userPhoto = (ImageView)mDrawerView.findViewById(R.id.account_switcher_user_photo);
		TextView userName = (TextView)mDrawerView.findViewById(R.id.account_switcher_user_name);
		TextView userDetails = (TextView)mDrawerView.findViewById(R.id.account_switcher_user_details);
		ImageButton userOptions = (ImageButton)mDrawerView.findViewById(R.id.account_switcher_user_options);

		UserParam userParam = UserParamRepository.getInstance().getUserParam();
		TextViewTools.setText(userName, UserInfo.getUserName());
//		ViewTools.setText(userDetails, userParam.getNkdk().trim(), "(", ")");
		ViewTools.setPhoto(getActivity(), userPhoto, userParam.getUserLogin());
		userOptions.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				PopupMenu popup = new PopupMenu(getActivity(), v);
				popup.getMenuInflater().inflate(R.menu.user_options, popup.getMenu());

				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						if (item.getItemId() == R.id.action_user_exit) {
							LoginService login = new LoginService(getActivity());
							login.logout();
							Intent intent = new Intent(getActivity(), LoginActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						}
						if (item.getItemId() == R.id.action_change_password) {
							Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
							startActivity(intent);
						}
						return true;
					}
				});
				popup.show();
			}
		});
	}
}