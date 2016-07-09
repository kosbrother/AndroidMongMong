package com.kosbrother.mongmongwoo.search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.R;

import java.util.List;

public class QueryHistoryAdapter extends RecyclerView.Adapter<QueryHistoryAdapter.ViewHolder> {

    private final List<String> queryList;
    private final OnQueryHistoryTextViewListener listener;

    QueryHistoryAdapter(List<String> queryList, OnQueryHistoryTextViewListener listener) {
        this.queryList = queryList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_query_history, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(queryList.get(position), listener);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return queryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView queryTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            queryTextView = (TextView) itemView;
        }

        public void bind(String query, final OnQueryHistoryTextViewListener listener) {
            queryTextView.setText(query);
            queryTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String query = ((TextView) v).getText().toString();
                    listener.onQueryTextViewClick(query);
                }
            });
            queryTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String query = ((TextView) v).getText().toString();
                    listener.onQueryTextViewLongClick(query);
                    return true;
                }
            });
        }
    }

    public interface OnQueryHistoryTextViewListener {

        void onQueryTextViewClick(String query);

        void onQueryTextViewLongClick(String query);

    }
}
