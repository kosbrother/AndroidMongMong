package com.kosbrother.mongmongwoo.search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.R;

import java.util.List;

public class QuerySuggestionAdapter extends RecyclerView.Adapter<QuerySuggestionAdapter.ViewHolder> {

    private final List<String> suggestionList;
    private final OnQuerySuggestionTextViewListener listener;

    QuerySuggestionAdapter(List<String> suggestionList, OnQuerySuggestionTextViewListener listener) {
        this.suggestionList = suggestionList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_query_suggestion, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(suggestionList.get(position), listener);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return suggestionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView queryTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            queryTextView = (TextView) itemView;
        }

        public void bind(String query, final OnQuerySuggestionTextViewListener listener) {
            queryTextView.setText(query);
            queryTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String query = ((TextView) v).getText().toString();
                    listener.onQueryTextViewClick(query);
                }
            });
        }
    }

    public interface OnQuerySuggestionTextViewListener {

        void onQueryTextViewClick(String query);

    }
}
