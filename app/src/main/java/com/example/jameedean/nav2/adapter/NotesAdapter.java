package com.example.jameedean.nav2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jameedean.nav2.R;
import com.example.jameedean.nav2.model.NoteModel;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private Context mContext;
    private ArrayList<NoteModel> mData;

    private OnItemClick mListener;

    public NotesAdapter(Context context, OnItemClick listener) {
        mContext = context;
        mData = new ArrayList<>();

        mListener = listener;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_holder_notes, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {

        NoteModel model = mData.get(position);

        holder.title.setText(model.getTitle());
        holder.agency.setText(model.getAgency());
        holder.description.setText(model.getDescription());
        // set description as log
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addData(NoteModel model) {
        mData.add(model);

        notifyDataSetChanged();
    }

    public void clear() {
        mData.clear();

        notifyDataSetChanged();
    }

    public NoteModel getItem(int position) {
        return mData.get(position);
    }

    public interface OnItemClick {
        void onClick(int pos);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title, agency;
        private TextView description;
        private ImageView imageView,drawView;

        NoteViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tv_title);
            agency = itemView.findViewById(R.id.tv_agency);
            imageView = itemView.findViewById(R.id.cameraImg);
            description = itemView.findViewById(R.id.tv_description);
            drawView =itemView.findViewById(R.id.iv_draw);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(getAdapterPosition());
        }
    }
}
