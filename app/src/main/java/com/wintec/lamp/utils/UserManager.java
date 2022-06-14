package com.wintec.lamp.utils;

import com.tencent.mmkv.MMKV;

public class UserManager {

    private static final String PUBLIC_KEY = "public_key";

    private static final String VERSION_INFO = "version_info";

    private static MMKV mmkv;

    private UserManager() {
        mmkv = MMKV.defaultMMKV();
    }

    private static class UserManagerInstance {
        private static UserManager instance = new UserManager();
    }

    public static UserManager getInstance() {
        return UserManagerInstance.instance;
    }

    public void editPublicKey(String phone) {
        mmkv.putString(PUBLIC_KEY, phone);
    }

    public String getPublicKey() {
        return mmkv.getString(PUBLIC_KEY, "");
    }


    public void editVersionData(String versioninfo) {
        mmkv.putString(VERSION_INFO, versioninfo);
    }

    public String getVersionData() {
        return mmkv.getString(VERSION_INFO, "");
    }
//
//    public void editUserName(String name) {
//        mmkv.putString(USER_NAME, name);
//    }
//
//    public String getUserName() {
//        return mmkv.getString(USER_NAME, "");
//    }
//
//    public void editToken(String token) {
//        mmkv.putString(USER_TOKEN, token);
//    }
//
//    public String getUserToken() {
//        return mmkv.getString(USER_TOKEN, "test_android");
//    }
//
//    public void editUserHead(String token) {
//        mmkv.putString(USER_HEAD, token);
//    }
//
//    public String getUserHead() {
//        return mmkv.getString(USER_HEAD, "");
//    }
//
//    public void editCompany(String company) {
//        mmkv.putString(USER_COMPANY, company);
//    }
//
//    public String getCompany() {
//        return mmkv.getString(USER_COMPANY, "");
//    }
//
//    public void editLogin(boolean isLogin) {
//        mmkv.putBoolean(USER_LOGIN, isLogin);
//    }
//
//    public boolean getLogin() {
//        return mmkv.getBoolean(USER_LOGIN, false);
//    }
//
//    public void editDespose(int despouse) {
//        mmkv.putInt(USER_DESPOUSE, despouse);
//    }
//
//    public int getDespouse() {
//        return mmkv.getInt(USER_DESPOUSE, 0);
//    }
//
//    public void logout() {
//        mmkv.clearAll();
//    }
//
//    public void editIdentity(String identity) {
//        mmkv.putString(USER_IDENTITY, identity);
//    }
//
//    public String getUserIdentity() {
//        return mmkv.getString(USER_IDENTITY, "");
//    }
}
