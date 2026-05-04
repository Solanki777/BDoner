package com.example.bdoner;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔥 Load this screen inside drawer
        setupDrawer(R.layout.activity_home);

        // 🔥 Load scrollable health updates
        loadHealthUpdates();
    }

    // 🩺 Dummy health updates (scrollable)
    private void loadHealthUpdates() {

        LinearLayout container = findViewById(R.id.healthContainer);

        if (container == null) return;

        String[] tips = {
                "🩸 Donate blood regularly to save lives",
                "🥗 Eat iron-rich foods like spinach, dates, and beetroot",
                "💧 Drink plenty of water before donation",
                "😴 Get proper sleep before donating blood",
                "🚫 Avoid alcohol at least 24 hours before donation",
                "🏃 Exercise regularly to stay fit and eligible",
                "❤️ Maintain a healthy lifestyle to help others",
                "🧪 Check your hemoglobin levels regularly",
                "🧍‍♂️ Stay relaxed and calm during donation",
                "📅 Maintain a gap of 90 days between donations"
        };

        for (String tip : tips) {

            TextView tv = new TextView(this);
            tv.setText(tip);
            tv.setTextSize(16);
            tv.setTextColor(Color.BLACK); // 🔥 FIX: Set text color to black for visibility
            tv.setPadding(0, 20, 0, 20);

            container.addView(tv);
        }
    }
}
