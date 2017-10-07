package com.example.dailynote;

/**
 * Created by David on 2017/10/5.
 */
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import android.util.Log;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
    private LayoutInflater inflater;
    private Context mContext;

    public MyAdapter(Context context, List<Note> data) {
        inflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.my_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyAdapter.MyViewHolder holder, int position) {
        Note current = Notes.data.get(position);
        holder.date.setText(current.date);
        holder.title.setText(current.title);

    }

    public void delete(int position){
        Notes.data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return Notes.data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView date;
        TextView title;
        ImageView delete;

        public MyViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.note_date);
            title = (TextView) itemView.findViewById(R.id.note_title);
            delete = (ImageView) itemView.findViewById(R.id.delete_button);
            delete.setOnClickListener(this);
            title.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            switch (v.getId()) {
                case R.id.delete_button:
                    showDialog((Activity) mContext, "Delete Note Entry", "Are you sure you want to delete it?", getAdapterPosition());
                    break;
                case R.id.note_title:
                    /**
                     * to be finished
                     */
                    Intent i = new Intent(mContext, EditNote.class);
                    i.putExtra("id", getAdapterPosition());
                    i.putExtra("note", Notes.data.get(getAdapterPosition()));
                    ((Activity) mContext).startActivityForResult(i, Notes.REQUEST_EDIT_NOTE);
                    break;
            }

        }
    }

    public void showDialog(Activity activity, String title, CharSequence message, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Delete button
                delete(position);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Cancel button
                dialog.dismiss();
            }
        });
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }




}
