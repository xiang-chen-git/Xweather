package com.spring.weather.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.spring.weather.R;
import com.spring.weather.model.City;

public class AutocompleteCustomArrayAdapter extends ArrayAdapter<City> {
    private ArrayList<City> items;
    private ArrayList<City> itemsAll;
    private ArrayList<City> suggestions;
    private int viewResourceId;

    public AutocompleteCustomArrayAdapter(Context context, int viewResourceId, ArrayList<City> items) {
        super(context, viewResourceId, items);
        this.items = items;
        this.itemsAll = items;
        this.suggestions = new ArrayList<City>();
        this.viewResourceId = viewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        City city = items.get(position);
        if (city != null) {
            TextView customerNameLabel = (TextView) v.findViewById(R.id.text);
            if (customerNameLabel != null) {
                customerNameLabel.setText(city.getName());
            }
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        public String convertResultToString(Object resultValue) {
            String str = ((City)(resultValue)).getName(); 
            return str;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null) {
                suggestions.clear();
                for (City city : itemsAll) {
                    if(city.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())){
                        suggestions.add(city);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            @SuppressWarnings("unchecked")
			ArrayList<City> filteredList = (ArrayList<City>) results.values;
            if(results != null && results.count > 0) {
                clear();
                for (City c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };


}