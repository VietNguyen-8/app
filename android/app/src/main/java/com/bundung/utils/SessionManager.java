package com.bundung.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.bundung.models.User;

public class SessionManager {
    private static final String PREF_NAME = "BunDungSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_ROLE = "role";
    private static final String KEY_TOKEN = "token";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Lưu thông tin đăng nhập
     */
    public void saveLoginSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, user.getUserId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_FULL_NAME, user.getFullName());
        editor.putString(KEY_ROLE, user.getRole());
        editor.putString(KEY_TOKEN, user.getToken());
        editor.commit();
    }

    /**
     * Lấy thông tin người dùng hiện tại
     */
    public User getCurrentUser() {
        if (!isLoggedIn()) {
            return null;
        }

        User user = new User();
        user.setUserId(pref.getInt(KEY_USER_ID, 0));
        user.setUsername(pref.getString(KEY_USERNAME, ""));
        user.setFullName(pref.getString(KEY_FULL_NAME, ""));
        user.setRole(pref.getString(KEY_ROLE, ""));
        user.setToken(pref.getString(KEY_TOKEN, ""));
        return user;
    }

    /**
     * Kiểm tra đã đăng nhập chưa
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Lấy user ID
     */
    public int getUserId() {
        return pref.getInt(KEY_USER_ID, 0);
    }

    /**
     * Lấy role
     */
    public String getRole() {
        return pref.getString(KEY_ROLE, "");
    }

    /**
     * Lấy token
     */
    public String getToken() {
        return pref.getString(KEY_TOKEN, "");
    }

    /**
     * Đăng xuất
     */
    public void logout() {
        editor.clear();
        editor.commit();
    }
}