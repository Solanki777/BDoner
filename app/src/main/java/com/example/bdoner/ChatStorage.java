package com.example.bdoner;

import android.content.Context;
import android.content.SharedPreferences;

public class ChatStorage {

    private static final String PREF = "chat_data";

    // ✅ Save chat per user
    public static void saveChat(Context ctx, String userId, String data) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putString("messages_" + userId, data).apply();
    }

    // ✅ Load chat per user
    public static String loadChat(Context ctx, String userId) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sp.getString("messages_" + userId, "");
    }

    // ✅ Clear only that user's chat
    public static void clearChat(Context ctx, String userId) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().remove("messages_" + userId).apply();
    }
}