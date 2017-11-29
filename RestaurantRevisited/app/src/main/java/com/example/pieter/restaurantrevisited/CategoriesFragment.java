package com.example.pieter.restaurantrevisited;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends ListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url = "https://resto.mprog.nl/categories";
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
            String[] courses = parseJson(response);
            setAdapter(courses);
        }
    }

    private class errorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            setAdapter(new String[]{"Error"});
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        MenuFragment menuFragment = new MenuFragment();

        Bundle args = new Bundle();
        args.putString("category", l.getItemAtPosition(position).toString());
        menuFragment.setArguments(args);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, menuFragment)
                .addToBackStack(null)
                .commit();
    }

    private String[] parseJson(String response) {
        String[] result;
        try {
            JSONObject responseObject = new JSONObject(response);
            JSONArray jArray = responseObject.getJSONArray("categories");
            result = new String[jArray.length()];
            for (int i = 0; i < jArray.length(); i++) {
                result[i] = jArray.getString(i);
            }
        } catch (Exception e) {
            result = new String[1];
            result[0] = "Error";
        }
        return result;
    }

    private void setAdapter(String[] courses) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, courses);
        CategoriesFragment.this.setListAdapter(adapter);
    }
}
