package com.example.pieter.restaurantrevisited;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

/**
 * Created by pieter on 28-11-17.
 */

public class RestoAdapter extends ResourceCursorAdapter {
    public RestoAdapter(Context context, Cursor c) {
        super(context, R.layout.row_resto, c);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nametv = view.findViewById(R.id.nametv);
        TextView pricetv = view.findViewById(R.id.pricetv);
        TextView numbertv = view.findViewById(R.id.numbertv);

        nametv.setText(cursor.getString(cursor.getColumnIndex("name")));
        pricetv.setText("€" + Integer.toString(cursor.getInt(cursor.getColumnIndex("price"))));
        numbertv.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex("number"))) + "x");
    }
}
