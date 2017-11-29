package com.example.pieter.restaurantrevisited;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends DialogFragment implements View.OnClickListener {
    RestoDatabase db;
    RestoAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        db = RestoDatabase.getInstance(getContext().getApplicationContext());
        Cursor all_data = db.selectAll();
        adapter = new RestoAdapter(getContext(), all_data);

        ListView orderlist = getView().findViewById(R.id.orderlistview);
        orderlist.setAdapter(adapter);

        Button cancelbutton = getView().findViewById(R.id.cancel_button);
        cancelbutton.setOnClickListener(this);

        Button orderbutton = getView().findViewById(R.id.place_order_button);
        orderbutton.setOnClickListener(this);

        TextView totalpricetv = getView().findViewById(R.id.totalpricetv);
        int total_price = 0;
        if (all_data.moveToFirst()) {
            do {
                total_price += all_data.getInt(all_data.getColumnIndex("price")) * all_data.getInt(all_data.getColumnIndex("number"));
            } while (all_data.moveToNext());
        } else {
            orderbutton.setVisibility(View.INVISIBLE);
        }
        totalpricetv.setText("Total price: €" + total_price);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_button:
                db.clear();
                this.dismiss();
                break;
            case R.id.place_order_button:
                sendorder();
                break;
        }
    }

    public void sendorder() {
        String url = "https://resto.mprog.nl/order";
        RequestQueue queue = Volley.newRequestQueue(getContext());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new responseListener(),
                new errorListener()
        );
        queue.add(stringRequest);
    }

    private class responseListener implements Response.Listener<String> {
        @Override
        public void onResponse(String response) {
            int preptime = 0;
            try {
                JSONObject obj = new JSONObject(response);
                preptime = obj.getInt("preparation_time");
            } catch (Exception e) {

            }
            db.clear();
            ArrayAdapter<String> newadapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, new String[] {"Your order has been placed! Preparations will take approximately " + preptime + " minutes."});
            ListView lv = getView().findViewById(R.id.orderlistview);
            lv.setAdapter(newadapter);
        }
    }

    private class errorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    }
}
