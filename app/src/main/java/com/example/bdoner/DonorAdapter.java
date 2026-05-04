package com.example.bdoner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.ViewHolder> {

    private final Context context;
    private final List<SearchActivity.DonorWrapper> list;

    public DonorAdapter(Context context, List<SearchActivity.DonorWrapper> list) {
        this.context = context;
        this.list = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvBG, tvDetails, tvInitial, tvBadge;
        Button btnCall, btnWhatsApp, btnMap;

        public ViewHolder(View v) {
            super(v);

            tvName = v.findViewById(R.id.tvName);
            tvBG = v.findViewById(R.id.tvBG);
            tvDetails = v.findViewById(R.id.tvDetails);
            tvInitial = v.findViewById(R.id.tvInitial);
            tvBadge = v.findViewById(R.id.tvBadge);

            btnCall = v.findViewById(R.id.btnCall);
            btnWhatsApp = v.findViewById(R.id.btnWhatsApp);
            btnMap = v.findViewById(R.id.btnMap);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_donor, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {

        User u = list.get(position).user;
        double dist = list.get(position).distance;

        // 🧑 Name
        h.tvName.setText(u.name != null ? u.name : "Unknown");

        // 🧑 Initial
        if (u.name != null && u.name.length() > 0) {
            h.tvInitial.setText(u.name.substring(0, 1).toUpperCase());
        } else {
            h.tvInitial.setText("?");
        }

        // 🩸 Blood Group
        h.tvBG.setText(u.bloodGroup != null ? u.bloodGroup : "--");

        // 📍 Location
        String location = (u.district != null ? u.district : "Unknown")
                + ", " + (u.state != null ? u.state : "");

        // 🏠 Address
        String address = (u.address != null) ? u.address : "No address";

        // 📏 Distance Text
        String distanceText;

        if (dist == -1) {
            distanceText = "Location approx";
        } else if (dist < 2) {
            distanceText = "Very close (" + String.format("%.1f km", dist) + ")";
        } else if (dist <= 10) {
            distanceText = "Nearby (" + String.format("%.1f km", dist) + ")";
        } else {
            distanceText = "Far (" + String.format("%.1f km", dist) + ")";
        }

        h.tvDetails.setText(
                location + "\n📍 " + address + "\n" + distanceText
        );

        // 🔥 BEST MATCH BADGE (top item)
        if (position == 0) {
            h.tvBadge.setVisibility(View.VISIBLE);
        } else {
            h.tvBadge.setVisibility(View.GONE);
        }

        // 📞 CALL
        h.btnCall.setOnClickListener(v -> {
            if (u.phone != null && !u.phone.isEmpty()) {
                context.startActivity(new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + u.phone)));
            } else {
                Toast.makeText(context, "Phone not available", Toast.LENGTH_SHORT).show();
            }
        });

        // 💬 WHATSAPP
        h.btnWhatsApp.setOnClickListener(v -> {
            if (u.phone == null || u.phone.isEmpty()) {
                Toast.makeText(context, "Phone not available", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                String phone = u.phone.replace("+", "").replace(" ", "");

                String message = "Hello " +
                        (u.name != null ? u.name : "") +
                        ", I need blood (" + u.bloodGroup + "). Can you help?";

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(
                        "https://wa.me/" + phone + "?text=" + Uri.encode(message)
                ));

                context.startActivity(intent);

            } catch (Exception e) {
                Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
            }
        });

        // 🗺️ MAP
        h.btnMap.setOnClickListener(v -> {
            try {
                Uri uri;

                if (u.latitude != 0 && u.longitude != 0) {
                    uri = Uri.parse("geo:" + u.latitude + "," + u.longitude);
                } else {
                    uri = Uri.parse("geo:0,0?q=" +
                            u.address + "," + u.district + "," + u.state);
                }

                context.startActivity(new Intent(Intent.ACTION_VIEW, uri));

            } catch (Exception e) {
                Toast.makeText(context, "Map not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}