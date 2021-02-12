package com.example.panglimi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//menu_item을 recyclerView에 넣기 위한 adapter 정의
public class CautionAdapter extends RecyclerView.Adapter<CautionAdapter.ViewHolder> implements OnMenuItemClickListener{
    public static ArrayList<Caution> items = new ArrayList<Caution>();

    public interface OnItemClickLisener{
        void onItemClick(View v, int posision);
    }

    private OnMenuItemClickListener mListener = null;

    public void setOnItemClickListener(OnMenuItemClickListener listener){
        this.mListener = listener;
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if(mListener != null)
            mListener.onItemClick(holder, view, position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.caution_item, viewGroup, false);

        return new CautionAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Caution item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Caution item){
        items.add(item);
    }

    public void setItems(ArrayList<Caution> items){
        this.items = items;
    }

    public Caution getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, Caution item){
        items.set(position, item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, address, date;

        public ViewHolder(View itemView) {
            super(itemView);

            address = itemView.findViewById(R.id.caution_address);
            date = itemView.findViewById(R.id.caution_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        if(mListener != null){
                            mListener.onItemClick(ViewHolder.this, view, position);
                        }
                    }
                }
            });
        }

        public void setItem(Caution item) {
            address.setText(item.getAddress());
            date.setText(item.getDate());
        }
    }
}

