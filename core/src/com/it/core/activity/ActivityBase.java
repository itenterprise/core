package com.it.core.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.it.core.R;
import com.it.core.application.ApplicationBase;
import com.it.core.fragments.Section;
import com.it.core.fragments.SlidingMenuFragment;
import com.it.core.fragments.SlidingMenuHelper;
import com.it.core.login.LoginService;
import com.it.core.login.OnLoginCompleted;
import com.it.core.menu.NavigationDrawerFragment;
import com.it.core.menu.SideMenuItem;
import com.it.core.model.InitResult;
import com.it.core.model.UserInfo;
import com.it.core.model.UserInfo.Credentials;
import com.it.core.service.IService;
import com.it.core.service.OnTaskCompleted;
import com.it.core.service.ServiceFactory;
import com.it.core.tools.KeyboardHelper;
import com.it.core.tools.LocaleHelper;
import com.it.core.tools.PreferenceHelper;
import com.it.core.tools.UpdateHelper;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;

public class ActivityBase extends FragmentActivity implements SlidingMenuHelper, NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1002;
	public static final int REQUEST_CODE_EXCEPTION_RETRY = 1003;
	public static final int REQUEST_CODE_URL_CHANGED = 1004;
	public static final int REQUEST_CODE_FIRST_URL_SET = 1005;
	public static final int REQUEST_CODE_DELEGATE_USER_CHANGED = 1006;

	private static final int GENERAL_MENU_SECTION = 997;
	private static final int SETTINGS_MENU_ITEM = 998;
	private static final int LOGOUT_MENU_ITEM = 999;

	private static final String GENERAL_MENU_SECTION_TAG = "GENERAL";
	private static final String SETTINGS_MENU_ITEM_TAG = "SETTINGS";
	private static final String IS_RECREATING_KEY = "IS RECREATING";
	private static final String CURRENT_MENU_ITEM_KEY = "CURRENT_MENU_ITEM";


	private boolean mIsNavigationDrawerSet;
	private static String mIsAuthenticated;

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	/**
	 * Идентификатор выбранного пункта меню
	 */
	private SideMenuItem mSelectedMenuItem;

	/**
	 * Идентификатор ориентации екрана
	 */
	private int mOrientation;

	private int mLayoutId;
	private int mContentId;
	private int mDrawerLayoutId;
	private int mNavigationDrawerId;
	private int mGlobalMenuId;
	private int mGlobalTitleId;
	private int mMenuId;
	private int mMenuStartIndex;
	private ArrayList<SideMenuItem> mMenuItems;

	/**
		Выезжающее меню
	 */
	private SlidingMenu slidingMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LocaleHelper.refreshLocale();
		PreferenceHelper.setApplicationVersion();
		String value = PreferenceHelper.getWebServiceUrl();
		if (value.isEmpty()) {
			onUrlNotSetUp();
			return;
		}
		if (!isNetworkConnected()) {
			checkAuthentication();
		} else {
			checkItVersion();
		}
	}

	protected void onUrlNotSetUp() {
		Intent i = new Intent(this, SettingsActivityBase.class);
		startActivityForResult(i, REQUEST_CODE_FIRST_URL_SET);
	}

	private void checkItVersion() {
		if (!ApplicationBase.getInstance().isInited() && needsCheckVersion()) {
			ServiceFactory.ServiceParams params = new ServiceFactory.ServiceParams(this);
			params.setProgressMessage(getString(R.string.version_check));
			params.setSkipErrors(true);
			IService service = ServiceFactory.createService(params);
			service.setOnExecuteCompletedHandler(onVersionReceived);
			String ver = "";
			try {
				ver = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			} catch (PackageManager.NameNotFoundException e) {
			}
			final String verFinal = ver;
			service.ExecObject("INIT", new Object(){
				public final String module = ApplicationBase.getInstance().getSystemModule();
				public final String project = ApplicationBase.getInstance().getCurrentObject();
				public final String version = verFinal;
				public final String appId = ApplicationBase.getInstance().getApplicationID();
				public final String os = "Android";
			}, InitResult.class, this);
		}
		else {
			checkAuthentication();
		}
	}

	/**
	 * Обработчик событий завершения вызова веб-метода проверки версии
	 */
	final OnTaskCompleted onVersionReceived = new OnTaskCompleted() {
		@Override
		public void onTaskCompleted(Object result) {
			InitResult initResult = (InitResult)result;

			// Если все успешно - перейти к проверке учетных данных
			if (initResult == null || initResult.getStatus() == InitResult.SUCCESS_STATUS) {
				ApplicationBase.getInstance().setInited(true);
				checkAuthentication();
			}
			// Иначе показать диалог и выполнить соответствующие действия
			else {
				AlertDialog.Builder builder = new AlertDialog.Builder(ActivityBase.this);
				String message = "";
				switch (initResult.getStatus()){
					case InitResult.MODULE_ERROR:
						message = String.format(getString(R.string.module_error), ApplicationBase.getInstance().getSystemModule());
						break;
					case InitResult.PROJECT_ERROR:
						message = String.format(getString(R.string.object_error), ApplicationBase.getInstance().getCurrentObject());
						break;
					case InitResult.NEED_NEW_VERSION:
						String url = initResult.getConfigUrl();
						Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						try {
							message = getString(R.string.use_new_version);
							startActivity(i);
						}
						catch (ActivityNotFoundException e){
							message = String.format(getString(R.string.install_from_store));
							Intent pmIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(initResult.getStoreUrl()));
							try {
								startActivity(pmIntent);
							}
							catch (ActivityNotFoundException e1){

							}
						}
						break;
					case InitResult.NEED_UPDATE_APP:
						UpdateHelper.updateApplication(ActivityBase.this, initResult.getUpdateFileName());
						return;
					default:
						return;
				}
				builder.setMessage(message);
				builder.setCancelable(false);
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ActivityBase.this.finish();
					}
				});
				builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						ActivityBase.this.finish();
					}
				});
				builder.show();
			}
		}
	};

	/**
	 * Проверить необходимость входа в систему
	 */
	private void checkAuthentication() {
		if (needsAuthentication()) {
			Credentials cred = ApplicationBase.getInstance().getCredentials();
			if (cred == null){
				cred = UserInfo.getCredentials();
			}
			if (cred.getGoogleLogin().isEmpty() && (cred.getLogin().isEmpty() || cred.getPassword().isEmpty()))	{
				navigateToLogin();
				return;
			}
			if (!UserInfo.isAuthenticated()) {
				LoginService loginService = new LoginService(ActivityBase.this);
				loginService.setOnLoginSuccessHandler(onLoginFromStoredCredentialsCompleted);
				loginService.loginFromStoredCredentials();
				return;
			}
		}
		onAfterCreate();
	}

	/**
	 * Обработчик результата попытки входа в систему
	 */
	private final OnLoginCompleted onLoginFromStoredCredentialsCompleted = new OnLoginCompleted() {
		@Override
		public void onSuccess(boolean needUpdateSession) {
			onAfterCreate();
		}

		@Override
		public void onFail(String failReason) {
			navigateToLogin();
		}

		@Override
		public void onError() {
			navigateToLogin();
		}
	};

	/**
	 * Выполнить переход на форму входа
	 */
	private void navigateToLogin() {
		Intent loginIntent = new Intent(ActivityBase.this, LoginActivity.class);
		loginIntent.putExtras(ActivityBase.this.getIntent());
		ActivityBase.this.startActivity(loginIntent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		ApplicationBase.getInstance().setActiveActivity(this);
	}

	protected void onAfterCreate() {
		if (mIsNavigationDrawerSet) {
			initUI();
			onNavigationDrawerItemSelected(mMenuItems.get(mMenuStartIndex));
		}
	}

	/**
	 * Создать выезжающее меню
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	protected void createSlidingMenu() {
		ActionBar ab = getActionBar();
		if (ab != null){
			ab.setHomeButtonEnabled(true);
			ab.setDisplayHomeAsUpEnabled(true);
		}
		if (slidingMenu != null){
			return;
		}
		slidingMenu = new SlidingMenu(this);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		slidingMenu.setMenu(R.layout.slidingmenu);
	}

	protected void updateMenu(){
		FragmentManager manager = getFragmentManager();
		SlidingMenuFragment fr = (SlidingMenuFragment)manager.findFragmentById(R.id.slidingmenu);
		if (fr != null){
			fr.updateMenu();
		}
	}


	@Override
	public void onBackPressed() {
		homeAsUpAction(true);
	}

	/**
	 * Действие для кнопки Home ActionBar
	 * @param checkMenuShowing проверить открыто ли меню
	 */
	private void homeAsUpAction(boolean checkMenuShowing) {
		if (slidingMenu != null) {
			if (!checkMenuShowing || slidingMenu.isMenuShowing()){
				slidingMenu.toggle();
			} else {
				this.moveTaskToBack(true);
			}
		}
		else{
			super.onBackPressed();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (slidingMenu != null && keyCode == KeyEvent.KEYCODE_MENU) {
			this.slidingMenu.toggle();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (slidingMenu != null && slidingMenu.isMenuShowing()) {
			slidingMenu.showContent();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if(mIsNavigationDrawerSet){
				return mNavigationDrawerFragment.onOptionsItemSelected(item);
			} else {
				homeAsUpAction(false);
				return true;
			}
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Создать пункты выезжающего меню. Для добавления новых необходимо
	 * переопределить метод в Activity и добавить свои секции в список sections
	 * @param sections
	 */
	@Override
	public void createMenu(ArrayList<Section> sections) {
		Section s = new Section(getString(R.string.general));
		s.addSectionItem(SETTINGS_MENU_ITEM, getString(R.string.settings), null);
		if (ApplicationBase.getInstance().isRequireAuth()) {
			s.addSectionItem(LOGOUT_MENU_ITEM, getString(R.string.exit), null);
		}
		sections.add(s);
	}

    /**
     * Спрятать/показать выезжающее меню
     */
    public void toggleMenu(){
        slidingMenu.toggle();
    }

    /**
     * Отметить пункт выезжающего меню
     */
    public void checkMenuItem(int id) {

        slidingMenu.getChildCount();

        View view = slidingMenu.getChildAt(id);
        ImageView icon = (ImageView)view.findViewById(R.id.slidingmenu_sectionitem_icon);
        icon.setImageResource(R.drawable.ic_action_accept_white);
    }

	/**
	 * Обработчик клика на пункте меню
	 * @param id ID пункта меню
	 */
	@Override
	public void onMenuItemClick(long id) {
		Intent i;
		switch ((int) id) {
		case LOGOUT_MENU_ITEM:
			LoginService login = new LoginService(this);
			login.logout();
			i = new Intent(this, LoginActivity.class);
			startActivity(i);
			break;
		case SETTINGS_MENU_ITEM:
			i = new Intent(this, SettingsActivityBase.class);
			startActivityForResult(i, REQUEST_CODE_URL_CHANGED);
			break;
		}
	}

	@Override
	public void onMenuItemLongClick(long id) { }

	/**
	 * Получили результат
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Из activity исключительной ситуации
		if (requestCode == REQUEST_CODE_EXCEPTION_RETRY) {
			checkItVersion();
			return;
		}
		// Из настроек. При первом запуске (когда не задан адрес сервера)
		if (requestCode == REQUEST_CODE_FIRST_URL_SET) {
			navigateToLogin();
		}
		// Из настроек. Изменилась ссылка
		if (requestCode == REQUEST_CODE_URL_CHANGED && resultCode == RESULT_OK) {
			boolean urlChanged = data.getBooleanExtra(SettingsActivityBase.WEB_SERVICE_URL_CHANGED_KEY, false);
			boolean localeChanged = data.getBooleanExtra(SettingsActivityBase.LOCALE_CHANGED_KEY, false);
			if (urlChanged) {
				onUrlChanged();
			} else if (localeChanged) {
				this.recreate();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void onUrlChanged() {
		LoginService login = new LoginService(this);
		login.logout();
		Intent i = new Intent(this, LoginActivity.class);
		startActivity(i);
	}

	public static boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) ApplicationBase.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else{
			return true;
		}
	}

	/**
	 * Для деятельности необходим авторизированный пользователь
	 * @return
	 */
	protected boolean needsAuthentication() {
		return false;
	}

	protected boolean needsCheckVersion() {return true;}

	/**
	 * Задать выезжающее меню (NavigationDrawer)
	 * @param layoutId Идентификатор макета активности в ресурсах
	 * @param contentId Идентификатор елемента в котором отобрается контент
	 * @param drawerLayoutId Идентификатор макета DrawerLayout
	 * @param navigationDrawerId Идентификатор NavigationDrawer-а
	 * @param actionBarMenuId Идентификатор меню
	 * @param menuItems Пункты меню
	 * @param menuStartIndex Позиция пункта меню по умолчанию
	 */
	public void setNavigationDrawer(int layoutId, int contentId, int drawerLayoutId, int navigationDrawerId,
	                                int actionBarMenuId, int actionBarGlobalMenuId, int actionBarGlobalTitleId, ArrayList<SideMenuItem> menuItems, int menuStartIndex){
		mLayoutId = layoutId;
		mContentId = contentId;
		mDrawerLayoutId = drawerLayoutId;
		mNavigationDrawerId = navigationDrawerId;
		mMenuId = actionBarMenuId;
		mGlobalMenuId = actionBarGlobalMenuId;
		mGlobalTitleId = actionBarGlobalTitleId;
		mMenuStartIndex = menuStartIndex;
		mMenuItems = menuItems;
		mMenuItems.add(new SideMenuItem(GENERAL_MENU_SECTION, getString(R.string.general), null, true));
		mMenuItems.add(new SideMenuItem(SETTINGS_MENU_ITEM, getString(R.string.settings), R.drawable.ic_settings, false));
		mIsNavigationDrawerSet = true;
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setTitle(mTitle);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (mNavigationDrawerFragment != null && !mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(mMenuId, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		return KeyboardHelper.toggleKeyboard(this, event, super.dispatchTouchEvent(event));
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE ||
				(getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) &&
				newConfig.orientation != mOrientation &&
				mLayoutId > 0) {
			mOrientation = newConfig.orientation;
			setContentView(mLayoutId);
			initUI();
			onNavigationDrawerItemSelected(mSelectedMenuItem);
		}
	}

	@Override
	public void onNavigationDrawerItemSelected(SideMenuItem item) {
		switch (item.Id) {
			case SETTINGS_MENU_ITEM:
				Intent i = new Intent(this, SettingsActivityBase.class);
				startActivityForResult(i, REQUEST_CODE_URL_CHANGED);
				return;
		}
		mSelectedMenuItem = item;
		mTitle = item.ActionBarTitle;
	}

	private void initUI()
	{
		mNavigationDrawerFragment = NavigationDrawerFragment.newInstance(mDrawerLayoutId, mNavigationDrawerId, mGlobalMenuId, mGlobalTitleId, mMenuItems);
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(mNavigationDrawerId, mNavigationDrawerFragment)
				.commitAllowingStateLoss();
	}
}