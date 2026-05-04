package com.example.bdoner;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;

public class AIActivity extends BaseActivity {

    private static final String TAG = "AIActivity";

    private EditText etMsg;
    private ImageButton btnSend;
    private TextView tvChat;
    private ScrollView scrollChat;

    private String chatHistory = "";
    private String userId; // ✅ ADDED

    private static final String WELCOME_MSG =
            "🤖 BDoner AI\n\nHello! I'm your health assistant. Ask me anything.";

    private final String API_KEY = "PUT YOUR API KEY HEAR";
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupDrawer(R.layout.activity_ai);

        // ✅ Get userId
        userId = getIntent().getStringExtra("userId");
        if (userId == null) userId = "guest";

        etMsg = findViewById(R.id.etMsg);
        btnSend = findViewById(R.id.btnSend);
        tvChat = findViewById(R.id.tvChat);
        scrollChat = findViewById(R.id.scrollChat);

        loadChatHistory();

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void loadChatHistory() {
        try {
            String saved = ChatStorage.loadChat(this, userId); // ✅ FIXED

            if (saved == null || saved.isEmpty()) {
                chatHistory = WELCOME_MSG;
                ChatStorage.saveChat(this, userId, chatHistory); // ✅ FIXED
            } else {
                chatHistory = saved;
            }

            tvChat.setText(chatHistory);
            scrollToBottom();

        } catch (Exception e) {
            Log.e(TAG, "Load error", e);
        }
    }

    private void sendMessage() {
        String msg = etMsg.getText().toString().trim();
        if (TextUtils.isEmpty(msg)) return;

        chatHistory += "\n\nYou: " + msg;
        tvChat.setText(chatHistory);
        etMsg.setText("");
        scrollToBottom();

        ChatStorage.saveChat(this, userId, chatHistory); // ✅ FIXED

        callGeminiAPI(msg);
    }

    private void callGeminiAPI(String userMessage) {
        try {
            JSONObject json = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject part = new JSONObject();

            part.put("text", userMessage);
            parts.put(part);
            content.put("parts", parts);
            contents.put(content);
            json.put("contents", contents);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent?key=" + API_KEY)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() ->
                            addAssistantMessage("⚠️ Network error")
                    );
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    String res = response.body() != null ? response.body().string() : "";

                    try {
                        JSONObject obj = new JSONObject(res);
                        JSONArray candidates = obj.optJSONArray("candidates");

                        if (candidates != null && candidates.length() > 0) {
                            String reply = candidates.getJSONObject(0)
                                    .getJSONObject("content")
                                    .getJSONArray("parts")
                                    .getJSONObject(0)
                                    .getString("text");

                            runOnUiThread(() -> addAssistantMessage(reply));
                        }

                    } catch (Exception e) {
                        runOnUiThread(() ->
                                addAssistantMessage("⚠️ Error reading response")
                        );
                    }
                }
            });

        } catch (Exception e) {
            addAssistantMessage("⚠️ Failed to send");
        }
    }

    private void addAssistantMessage(String message) {
        chatHistory += "\n\nAssistant: " + message;
        tvChat.setText(chatHistory);
        scrollToBottom();

        ChatStorage.saveChat(this, userId, chatHistory); // ✅ FIXED
    }

    private void scrollToBottom() {
        scrollChat.post(() -> scrollChat.fullScroll(ScrollView.FOCUS_DOWN));
    }
}