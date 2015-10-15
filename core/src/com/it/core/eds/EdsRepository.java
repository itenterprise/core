package com.it.core.eds;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.iit.certificateAuthority.endUser.libraries.signJava.EndUser;
import com.iit.certificateAuthority.endUser.libraries.signJava.EndUserCMPSettings;
import com.iit.certificateAuthority.endUser.libraries.signJava.EndUserFileStoreSettings;
import com.iit.certificateAuthority.endUser.libraries.signJava.EndUserLDAPSettings;
import com.iit.certificateAuthority.endUser.libraries.signJava.EndUserOCSPSettings;
import com.iit.certificateAuthority.endUser.libraries.signJava.EndUserProxySettings;
import com.iit.certificateAuthority.endUser.libraries.signJava.EndUserResourceExtractor;
import com.iit.certificateAuthority.endUser.libraries.signJava.EndUserSignInfo;
import com.iit.certificateAuthority.endUser.libraries.signJava.EndUserTSPSettings;
import com.it.core.R;
import com.it.core.application.ApplicationBase;
import com.it.core.internalstorage.InternalStorageSerializer;
import com.it.core.notifications.Dialog;
import com.it.core.security.ObscuredSharedPreferences;
import com.it.core.serialization.SerializeHelper;
import com.it.core.tools.FileHelper;
import com.it.core.tools.PreferenceHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Класс для работы с ЕЦП
 */
public class EdsRepository {

	public static final String EDS_CREDENTIALS_LIST_KEY = "EdsCredentialsList";
	public static final String EDS_PRIVATE_KEY_PREFERENCE = "EdsPrivateKey";
	public static final String EDS_CERTIFICATE_PREFERENCE = "EdsCertificate";
	public static final String EDS_CENTER_KEY = "EdsCertificationCenter";
	private static final String EDS_KEY_DIRECTORY_KEY = "EDS_KEY";
	private static final String EDS_CERTIFICATES_DIRECTORY_KEY = "EDS_KEY";

	private static EdsRepository mInstance;
	private EndUser mEndUser;
	private EdsRepository() {}

	public static EdsRepository getInstance() {
		if (mInstance == null) {
			mInstance = new EdsRepository();
		}
		return mInstance;
	}

	/**
	 * Получить список сохраненных паролей с привязкой к пути файла ключа
	 */
	private ArrayList<EdsCredentials> getEdsCredentials() {
		SharedPreferences prefs = ObscuredSharedPreferences.getSharedPreferences(ApplicationBase.getInstance());
		String jsonList = prefs.getString(EDS_CREDENTIALS_LIST_KEY, "");
		return SerializeHelper.deserializeList(jsonList, EdsCredentials.class);
	}

	/**
	 * Очистить сохраненные пароли ЕЦП
	 */
	public void clearEdsCredentials() {
		ObscuredSharedPreferences.getSharedPreferences(ApplicationBase.getInstance())
				.edit()
				.putString(EDS_CREDENTIALS_LIST_KEY, null)
				.commit();
		//TODO: убрали удаление настройки последнего выбранного ключа
		//PreferenceHelper.putValue(EDS_PRIVATE_KEY_PREFERENCE, "");
		mEndUser = null;
	}

	/**
	 * Получить пароль ЕЦП
	 * @param keyPath Путь к файлу приватного ключа
	 * @return Пароль приватного ключа
	 */
	public String getSavedPassword(String keyPath) {
		ArrayList<EdsCredentials> edsCredentialsList = getEdsCredentials();
		if (edsCredentialsList == null || edsCredentialsList.isEmpty()) {
			return null;
		}
		String password = null;
		for (EdsCredentials credentials: edsCredentialsList) {
			if (credentials.getKeyPath().equals(keyPath)) {
				password = credentials.getPassword();
				break;
			}
		}
		return password;
	}

	/**
	 * Сохранить пароль ЕЦП
	 * @param keyPath Путь к файлу приватного ключа
	 * @param password Пароль приватного ключа
	 */
	public void saveKeyPassword(String keyPath, String password) {
		ArrayList<EdsCredentials> edsCredentialsList = getEdsCredentials();
		if (edsCredentialsList == null) {
			edsCredentialsList = new ArrayList<EdsCredentials>();
		}
		EdsCredentials newCredentials = new EdsCredentials(keyPath, password);
		edsCredentialsList.add(newCredentials);
		SharedPreferences.Editor prefsEditor = ObscuredSharedPreferences.getSharedPreferences(ApplicationBase.getInstance()).edit();
		prefsEditor.putString(EDS_CREDENTIALS_LIST_KEY, SerializeHelper.serialize(edsCredentialsList));
		prefsEditor.commit();
	}

	/**
	 * Пароль ключа ЕЦП
	 * @param keyPath Путь к файлу приватного ключа
	 */
	public void removeKeyPassword(String keyPath) {
		ArrayList<EdsCredentials> edsCredentialsList = getEdsCredentials();
		if (edsCredentialsList == null) {
			edsCredentialsList = new ArrayList<EdsCredentials>();
		}
		EdsCredentials credentials = null;
		for (EdsCredentials cred: edsCredentialsList) {
			if (cred.getKeyPath().equals(keyPath)) {
				credentials = cred;
				break;
			}
		}
		if (credentials == null) {
			return;
		}
		edsCredentialsList.remove(credentials);
		SharedPreferences.Editor prefsEditor = ObscuredSharedPreferences.getSharedPreferences(ApplicationBase.getInstance()).edit();
		prefsEditor.putString(EDS_CREDENTIALS_LIST_KEY, SerializeHelper.serialize(edsCredentialsList));
		prefsEditor.commit();
	}

	/**
	 * Задать файл приватного ключа
	 * Осуществляет перенос ключа в InternalStorage
	 * @param context Контекст
	 * @param keyFile Файл приватного ключа
	 * @param messageId Сообщение о переносе ключа в InternalStorage
	 */
	public void putEdsPrivateKey(Context context, String keyName, File keyFile, @StringRes int messageId) {
		InternalStorageSerializer storageSerializer = new InternalStorageSerializer();
		try {
			storageSerializer.putFile(context, keyName, keyFile, EDS_KEY_DIRECTORY_KEY);
			keyFile.delete();
			Dialog.showPopup(context, messageId);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeEdsPrivateKey(Context context, String keyName) {
		InternalStorageSerializer storageSerializer = new InternalStorageSerializer();
		storageSerializer.removeFile(context, keyName, EDS_KEY_DIRECTORY_KEY);
	}

	/**
	 * Получить файл приватного ключа
	 * @param context Контекст
	 * @return Возвращает ранее сохраненный приватный ключ
	 */
	public File getEdsPrivateKey(Context context, String keyName) {
		InternalStorageSerializer storageSerializer = new InternalStorageSerializer();
		return storageSerializer.getFile(context,
				keyName,
				EDS_KEY_DIRECTORY_KEY);
	}

	/**
	 * Получить файл приватного ключа
	 * @param context Контекст
	 * @return Возвращает ранее сохраненный приватный ключ
	 */
	public File getAllEdsPrivateKeys(Context context) {
		InternalStorageSerializer storageSerializer = new InternalStorageSerializer();
		return storageSerializer.getFile(context,
				(String) PreferenceHelper.getValue(EdsRepository.EDS_PRIVATE_KEY_PREFERENCE, ""),
				EDS_KEY_DIRECTORY_KEY);
	}

	/**
	 * Создание ЕЦП файла
	 * @param context Контекст
	 * @param filePath Путь к файлу который надо подписать
	 * @param privateKeyPath Путь к приватному ключу
	 * @param privateKeyPassword Пароль приватного ключа
	 * @param listener Обработчик создания подписи
	 */
	public void createSign(final Context context, final String filePath, final String privateKeyPath,
							final String privateKeyPassword, final OnSignatureCreated listener) {
//		//TODO: на майбутнє можна забрати перевірки:
//		//TODO: - добавити діалог вибору ключа при першому підписанні
//		//TODO: - можливість натискати кнопку ОК при вводі пароля, тільки коли вже введені символи
//		if (!FileHelper.isFileExists(privateKeyPath)) {
//			Dialog.showPopup(context, R.string.path_to_private_key_not_specified_or_invalid);
//			listener.onSignatureCreated(null);
//			return;
//		}
//		if (privateKeyPassword == null || privateKeyPassword.isEmpty()) {
//			Dialog.showPopup(context, R.string.private_key_password_not_specified);
//			listener.onSignatureCreated(null);
//			return;
//		}
//		if (!FileHelper.isFileExists(filePath)) {
//			Dialog.showPopup(context, R.string.invalid_path_to_file_you_wont_sign);
//			listener.onSignatureCreated(null);
//			return;
//		}

		AsyncTask<Void, Void, SignResult> createSignAsync = new AsyncTask<Void, Void, SignResult>() {
			private ProgressDialog mDialog;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mDialog = Dialog.showProgressDialog(context, R.string.eds_creating);
			}

			@Override
			protected SignResult doInBackground(Void... params) {
				if (!FileHelper.isFileExists(filePath)) {
					return new SignResult(new Exception(context.getString(R.string.invalid_path_to_file_you_wont_sign)));
				}
				String signature;
				try {
					EndUser endUser = initEndUser(context);
//					endUser.ResetOperation();
//					endUser.ResetPrivateKey();
//					int count = endUser.GetEndUserCertificatesCount();

					endUser.ReadPrivateKeyFile(privateKeyPath, privateKeyPassword);
//					endUser.SignFile(filePath, signFilePath, true);
					byte [] data = FileHelper.readBytesFromFile(new File(filePath));
					signature = endUser.Sign(data);
					endUser.Verify(signature, data);
//					EndUserSignInfo stringFileInfo = endUser.VerifyFileWithExternalSign(filePath, signFilePath);
//					String signedString = endUser.Sign("test");
//					EndUserSignInfo stringSignedInfo = endUser.Verify(signedString, "test");
					if (!endUser.IsPrivateKeyReaded()) {
						return new SignResult(new Exception(context.getString(R.string.eds_creating_fail)));
					}
				}
				catch (Exception ex) {
					return new SignResult(ex);
				}
				return new SignResult(signature);
			}

			@Override
			protected void onPostExecute(SignResult result) {
				Dialog.hideProgress(mDialog);
				if (listener == null) {
					return;
				}
				listener.onSignatureCreated(result);
			}
		};
		createSignAsync.execute();
	}

	/**
	 * Верифицировать ЕЦП
	 * @param context Контекст
	 * @param signature Подпись (в base64)
	 * @param filePath Путь к файлу который надо верифицировать
	 *
	 * @param listener Обработчик верификации
	 */
	public void verifySign(final Context context, final String signature, final String filePath, final boolean showProgress, final OnSignatureVerified listener) {
		AsyncTask<Void, Void, VerifyResult> verifySignAsync = new AsyncTask<Void, Void, VerifyResult>() {
			private ProgressDialog mDialog;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if (showProgress) {
					mDialog = Dialog.showProgressDialog(context, R.string.eds_verifying);
				}
			}

			@Override
			protected VerifyResult doInBackground(Void... params) {
				if (!FileHelper.isFileExists(filePath)) {
					return new VerifyResult(new Exception(context.getString(R.string.invalid_path_to_file_you_wont_sign)));
				}
				EndUserSignInfo stringFileInfo;
				try {
					EndUser endUser = initEndUser(context);
					stringFileInfo = endUser.Verify(signature, FileHelper.readBytesFromFile(new File(filePath)));
				}
				catch (Exception ex) {
					return new VerifyResult(ex);
				}
				return new VerifyResult(stringFileInfo);
			}

			@Override
			protected void onPostExecute(VerifyResult result) {
				if (showProgress) {
					Dialog.hideProgress(mDialog);
				}
				if (listener == null) {
					return;
				}
				listener.onSignatureVerified(result);
			}
		};
		verifySignAsync.execute();
	}

	/**
	 * Инициализация библиотеки ЕЦП
	 * @param context Контекст
	 * @return Клас библиотеки
	 * @throws Exception
	 */
	private EndUser initEndUser(Context context) throws Exception {
//		copyCertificates();
//		if (mEndUser != null) {
//			mEndUser.RefreshFileStore(true);
//			return mEndUser;
//		}
		EndUserResourceExtractor.SetPath(context.getFilesDir().getAbsolutePath());
		EndUserResourceExtractor.SetPreLoad(true);
		EndUser endUser = new EndUser();
		endUser.SetCharset("UTF-16LE");
		endUser.SetUIMode(false);
		endUser.Initialize();

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ApplicationBase.getInstance());
		String jsonCenter = preferences.getString(EDS_CENTER_KEY, "");
		EdsCenter center = SerializeHelper.deserialize(jsonCenter, EdsCenter.class);
		if (center == null) {
			center = new EdsCenter();
		}

		EndUserCMPSettings cmpSettings = endUser.CreateCMPSettings();
		cmpSettings.SetUseCMP(true);
		cmpSettings.SetAddress(TextUtils.isEmpty(center.getCmpAddress()) ? "acskidd.gov.ua" : center.getCmpAddress());
		cmpSettings.SetPort(TextUtils.isEmpty(center.getCmpPort()) ? "80" : center.getCmpPort());
		cmpSettings.SetCommonName(TextUtils.isEmpty(center.getCmpAddress()) ? "acskidd.gov.ua" : center.getCmpAddress());
		endUser.SetCMPSettings(cmpSettings);

//		EndUserLDAPSettings ldapSettings = endUser.CreateLDAPSettings();
//		ldapSettings.SetUseLDAP(true);
//		ldapSettings.SetAddress("ca.iit.com.ua");
//		ldapSettings.SetPort("389");
//		ldapSettings.SetAnonymous(true);
//		endUser.SetLDAPSettings(ldapSettings);

		EndUserLDAPSettings ldapSettings = endUser.CreateLDAPSettings();
		ldapSettings.SetUseLDAP(false);
		ldapSettings.SetAddress(null);
		ldapSettings.SetPort(null);
		ldapSettings.SetAnonymous(false);
		endUser.SetLDAPSettings(ldapSettings);

		EndUserTSPSettings tspSettings = endUser.CreateTSPSettings();
		tspSettings.SetAddress(TextUtils.isEmpty(center.getTspAddress()) ? "acskidd.gov.ua" : center.getTspAddress());
		tspSettings.SetPort(TextUtils.isEmpty(center.getTspPort()) ? "80" : center.getTspPort());
		tspSettings.SetGetStamps(true);
		endUser.SetTSPSettings(tspSettings);

		EndUserOCSPSettings ocspSettings = endUser.CreateOCSPSettings();
		ocspSettings.SetAddress(TextUtils.isEmpty(center.getOcspAddress()) ? "acskidd.gov.ua" : center.getOcspAddress());
		ocspSettings.SetPort(TextUtils.isEmpty(center.getOcspPort()) ? "80" : center.getOcspPort());
		ocspSettings.SetUseOCSP(true);
		ocspSettings.SetBeforeStore(true);
		endUser.SetOCSPSettings(ocspSettings);

		EndUserFileStoreSettings fileSettings = endUser.CreateFileStoreSettings();
		fileSettings.SetAutoRefresh(true);
		fileSettings.SetSaveLoadedCerts(true);
		fileSettings.SetCheckCRLs(true);
		fileSettings.SetFullAndDeltaCRLs(true);
		fileSettings.SetOwnCRLsOnly(true);
		fileSettings.SetAutoDownloadCRLs(true);
		fileSettings.SetPath(FileHelper.getFilesDir().getPath());
		fileSettings.SetExpireTime(3600);
		endUser.SetFileStoreSettings(fileSettings);

		EndUserProxySettings proxy = endUser.CreateProxySettings();
		proxy.SetUseProxy(false);
		endUser.SetProxySettings(proxy);

//		EndUserModeSettings modeSettings = endUser.CreateModeSettings();
//		modeSettings.SetOfflineMode(true);
//		endUser.SetModeSettings(modeSettings);

		endUser.RefreshFileStore(true);
		mEndUser = endUser;
		return endUser;
	}

	/**
	 * Скопировать корневые сертификаты в файловое хранилище
	 * @throws IOException
	 */
	private void copyCertificates() throws IOException {
		String path = (String) PreferenceHelper.getValue(EDS_CERTIFICATE_PREFERENCE, "");
		if (TextUtils.isEmpty(path)) {
			// TODO: error message - file path is empty
			return;
		}
		File directory = new File(path);
		if (directory.exists() && directory.isDirectory()) {
			String[] children = directory.list();
			for (String aChildren : children) {
				if (aChildren.lastIndexOf(".") == -1) {
					continue;
				}
				String extension = aChildren.substring(aChildren.lastIndexOf(".") + 1);
				if (extension.equalsIgnoreCase("cer")) {
					File orig = new File(directory, aChildren);
					File copy = new File(FileHelper.getFilesDir(), aChildren);
					FileHelper.copy(orig, copy);
				}
			}
		}
	}

	public class SignResult {
		private boolean mSuccess;
		private String mSignature;
		private Exception mException;

		public SignResult(Exception ex) {
			mSuccess = false;
			mSignature = null;
			mException = ex;
		}

		public SignResult(String signature) {
			mSuccess = true;
			mSignature = signature;
			mException = null;
		}

		public boolean isSuccess(){
			return mSuccess;
		}
		public String getSignature(){
			return mSignature;
		}
		public Exception getException(){
			return mException;
		}
	}

	public class VerifyResult {
		private boolean mSuccess;
		private EndUserSignInfo mVerifyInfo;
		private Exception mException;

		public VerifyResult(Exception ex) {
			mSuccess = false;
			mVerifyInfo = null;
			mException = ex;
		}

		public VerifyResult(EndUserSignInfo verifyInfo) {
			mSuccess = true;
			mVerifyInfo = verifyInfo;
			mException = null;
		}

		public boolean isSuccess(){
			return mSuccess;
		}
		public EndUserSignInfo getVerifyInfo(){
			return mVerifyInfo;
		}
		public Exception getException(){
			return mException;
		}
	}

//	private class SignResult { }

//	private class FailedSignResult extends SignResult {
//		private Exception mException;
//
//		public FailedSignResult(Exception ex){
//			mException = ex;
//		}
//
//		public Exception getException(){
//			return mException;
//		}
//	}
//
//	private class SuccessSignResult extends SignResult {
//		private String mSignPath;
//
//		public SuccessSignResult(String signPath){
//			mSignPath = signPath;
//		}
//
//		public String getSignPath(){
//			return mSignPath;
//		}
//	}

	/**
	 * Обработчик создания ЕЦП
	 */
	public interface OnSignatureCreated {
		public void onSignatureCreated(SignResult signResult);
	}

	/**
	 * Обработчик верификации ЕЦП
	 */
	public interface OnSignatureVerified {
		public void onSignatureVerified(VerifyResult verifyResult);
	}
}