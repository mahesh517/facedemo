package com.app.detection;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import org.json.JSONObject;

import java.util.Set;


public class LoginPrefManager {

    private final Context _context;

    private static android.app.AlertDialog.Builder alertDialogBuilder = null;
    private static android.app.AlertDialog networkAlertDialog = null;

    Set<String> fav_list;
    private SharedPreferences pref;
    private Editor editor;

    private static final String PREF_NAME = "AndroidCustomerPracleoPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String Check_login = "0";
    private static JSONObject jsonObject;

    private String pickupaddress_latitude;

    public LoginPrefManager(Context context) {
        this._context = context;
        int PRIVATE_MODE = 0;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.apply();
    }

    public void setLoginPrefData(String text, String data) {
        editor.putString(text, data).commit();
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(Check_login, "1").commit();
        editor.commit();
    }

    public void setPrefData(String text, String data) {
        editor.putString(text, data).commit();
        editor.commit();
    }

    public String getPrefData(String text) {
        return pref.getString(text, "");
    }

    public Editor getEditor() {
        return editor;
    }

    public SharedPreferences getShaPref() {
        return pref;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }

    public void setMyPref(SharedPreferences pref) {
        this.pref = pref;
    }

    public void setIntValue(String keyName, int value) {
        pref.edit().putInt(keyName, value).apply();
    }

    public int getIntValue(String keyName) {
        return pref.getInt(keyName, 0);
    }

    public void setStringValue(String keyName, String value) {
        pref.edit().putString(keyName, value).apply();

    }

    public String getStringValue(String keyName) {

        return pref.getString(keyName, "");

    }


    public void setBooleanValue(String keyName, boolean value) {
        pref.edit().putBoolean(keyName, value).apply();
    }


    public Boolean getBooleanValue(String keyName) {
        return pref.getBoolean(keyName, false);
    }

    public void remove(String key) {
        pref.edit().remove(key).apply();
    }

    public boolean clear() {
        return pref.edit().clear().commit();
    }

    public void LogOutClearDataMethod() {

        pref.edit().putString("customer_id", "").apply();
        pref.edit().putString("notification_count", "0").apply();


    }

    public void chagepasswordcleraMethod() {

        pref.edit().putString("username", "").apply();
        pref.edit().putString("password1", "").apply();

    }


    // Get login State
    private boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public static void setJsonObject(JSONObject jsonObject) {
        LoginPrefManager.jsonObject = jsonObject;
    }

    public static JSONObject getJsonObject() {
        return jsonObject;
    }


    //Pic a City details
    public void setCityIDandName(String City_Id, String City_Name) {
        pref.edit().putString("Pic_City_Id", City_Id).apply();
        pref.edit().putString("Pic_City_Name", City_Name).apply();
    }

    public String getCityID() {
        return pref.getString("Pic_City_Id", "");
    }

    public String getCityName() {
        return pref.getString("Pic_City_Name", "");
    }

    // Pic a Location Details
    public void setLocIDandName(String Loc_Id, String Loc_Name) {
        pref.edit().putString("Pic_loc_id", Loc_Id).apply();
        pref.edit().putString("Pic_loc_name", Loc_Name).apply();
    }

    public String getLocID() {
        return pref.getString("Pic_loc_id", "");
    }

    public String getLocName() {
        return pref.getString("Pic_loc_name", "");
    }

    public String getCurrencySide() {
        return pref.getString("currency_side", "");
    }

    public String getCurrencySymbole() {
        return pref.getString("currency_symbol", "");
    }

    public String getCurrencyName() {
        return pref.getString("currency_name", "");
    }


    public void setDriverID(String driver_id) {
        pref.edit().putString("driver_id", driver_id).apply();
    }


    public void setDriverStatus(String driverStatus) {
        pref.edit().putString("driver_status", driverStatus).apply();
    }

    public String getDriverStatus() {
        return pref.getString("driver_status", "0");
    }


    public void checkFirst(boolean status) {
        pref.edit().putBoolean("first_time", status).apply();
    }

    public boolean getLoginFirst() {
        return pref.getBoolean("first_time", true);
    }

    public void setDeviceToken(String deviceToken) {
        pref.edit().putString("device_token", deviceToken).apply();
    }

    public void setDefaultLang(String keyName) {
        pref.edit().putString("def_lang", keyName).apply();
    }

    public void setUserToken(String userToken) {
        pref.edit().putString("user_token", userToken).apply();
    }

    public String getUserToken() {
        return pref.getString("user_token", "");
    }


    public void setRole(String role) {
        pref.edit().putString("role", role).apply();
    }

    public String getRole() {
        return pref.getString("role", "");
    }


    public void setDeviceId(String deviceId) {
        pref.edit().putString("device_id", deviceId).apply();
    }

    public String getDeviceId() {
        return pref.getString("device_id", "");
    }

    public String getDeviceToken() {
        return pref.getString("device_token", "");
    }


    public String getDefaultLang() {
        return pref.getString("def_lang", "");
    }


    public String getDriverID() {
        return pref.getString("driver_id", "");
    }


    public void setGender(String gender) {

        pref.edit().putString("user_gender", gender).apply();
    }

    public String getGender() {
        return pref.getString("user_gender", "");
    }


    public String getFormatCurrencyValue(String Currency) {
        if (getCurrencySide().equals("1")) {
            return String.format("%s %s", "" + getCurrencySymbole(), "" + Currency);
        } else {
            return String.format("%s %s", "" + Currency, "" + getCurrencySymbole());
        }
    }


    public void setAccessToken(String accessToken) {
        pref.edit().putString("access_token", accessToken).apply();
    }

    public String getAccessToken() {
        return pref.getString("access_token", "");
    }

    public void setCogintoToken(String accessToken) {
        pref.edit().putString("cognito_token", accessToken).apply();
    }

    public String getCogintoToken() {
        return pref.getString("cognito_token", "");
    }

    public void setRefreshTokenToken(String accessToken) {
        pref.edit().putString("refresh_token", accessToken).apply();
    }

    public String getRefreshToken() {
        return pref.getString("refresh_token", "");
    }


    public void setAdminList(Set stringList) {
        pref.edit().putStringSet("manager_list", stringList).apply();
    }

    public Set getAdminList() {
        return pref.getStringSet("manager_list", null);
    }


}
