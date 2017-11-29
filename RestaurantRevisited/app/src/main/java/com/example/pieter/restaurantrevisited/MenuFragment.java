package com.example.pieter.restaurantrevisited;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.view.menu.MenuAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends ListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url = "https://resto.mprog.nl/menu";
        RequestQueue queue = Volley.newRequestQueue(getContext());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new responseListener(),
                new errorListener()
        );

        queue.add(stringRequest);
    }

    private class responseListener implements Response.Listener<String> {
        @Override
        public void onResponse(String response) {
            tuple dishnames = parseJson(response);
            setAdapter(dishnames);
        }
    }

    private class errorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            tuple errorstring = new tuple();
            setAdapter(errorstring);
        }
    }

    private tuple parseJson(String response) {
        String category = this.getArguments().getString("category");
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> prices = new ArrayList<>();
        ArrayList<String> numbers = new ArrayList<>();

        try {
            JSONObject responseObject = new JSONObject(response);
            JSONArray jArray = responseObject.getJSONArray("items");
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject2 = jArray.getJSONObject(i);
                if (jObject2.getString("category").equals(category)) {
                    names.add(jObject2.getString("name"));
                    prices.add("€" + Integer.toString(jObject2.getInt("price")));
                    numbers.add("");
                }
            }
        } catch (Exception e) {
            names.add("Error");
            prices.add("Error");
            numbers.add("Error");
        }
        tuple result = new tuple(names, prices, numbers);
        return result;
    }

    private void setAdapter(tuple courses) {
        menuAdapter adapter = new menuAdapter(getContext(), courses);
        MenuFragment.this.setListAdapter(adapter);
    }

    private class menuAdapter extends BaseAdapter {
        Context context;
        ArrayList<String> names;
        ArrayList<String> prices;
        ArrayList<String> numbers;

        public menuAdapter(@NonNull Context context, tuple namesprices) {
            this.context = context;
            this.names = namesprices.names;
            this.prices = namesprices.prices;
            this.numbers = namesprices.numbers;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = convertView;
            if (rowView == null) {
                rowView = inflater.inflate(R.layout.row_resto, parent, false);
            }

            TextView nametv = rowView.findViewById(R.id.nametv);
            TextView pricetv = rowView.findViewById(R.id.pricetv);
            TextView numberstv = rowView.findViewById(R.id.numbertv);

            nametv.setText(names.get(position));
            pricetv.setText(prices.get(position));
            numberstv.setText(numbers.get(position));

            return rowView;
        }

        @Override
        public int getCount() {
            return names.size();
        }

        @Override
        public Object getItem(int i) {
            return new tuple(names, prices, numbers);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
    }

    private class tuple {
        public final ArrayList<String> names;
        public final ArrayList<String> prices;
        public final ArrayList<String> numbers;

        public tuple() {
            names = new ArrayList<>();
            prices = new ArrayList<>();
            numbers = new ArrayList<>();
        }

        public tuple(ArrayList<String> names, ArrayList<String> prices, ArrayList<String> numbers) {
            this.names = names;
            this.prices = prices;
            this.numbers = numbers;
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        RestoDatabase db = RestoDatabase.getInstance(getContext().getApplicationContext());

        TextView nametv = v.findViewById(R.id.nametv);
        TextView pricetv = v.findViewById(R.id.pricetv);

        int number = 1;

        String name = nametv.getText().toString();
        String price = pricetv.getText().toString();
        db.addItem(name, new Integer(price.substring(1)), number);
    }
}
