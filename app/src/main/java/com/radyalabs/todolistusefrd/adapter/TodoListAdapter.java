package com.radyalabs.todolistusefrd.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.radyalabs.todolistusefrd.R;
import com.radyalabs.todolistusefrd.activity.MainActivity;
import com.radyalabs.todolistusefrd.model.Todo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.MyViewHolder> {

    private ArrayList<Todo> data;
    private LayoutInflater inflater;
    private Context context;
    private AdapterView.OnItemClickListener onItemClickListener;
    private AdapterView.OnItemLongClickListener onItemLongClickListener;

    public TodoListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = new ArrayList<>();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public ArrayList<Todo> getData() {
        return data;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_todo, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Todo todo = data.get(position);
        holder.position = position;

        final long identity = System.currentTimeMillis();
        holder.identity = identity;

        if (holder.identity == identity){
            if (todo.is_done){
                holder.doneTodo.setImageResource(R.drawable.done);
            }else {
                holder.doneTodo.setImageResource(R.drawable.not_done);
            }

            holder.todoItem.setText(todo.item);

            holder.doneTodo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (todo.is_done){
                        ((MainActivity)context).setDone(todo.id, false);
                    }else {
                        ((MainActivity)context).setDone(todo.id, true);
                    }

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, AdapterView.OnLongClickListener{
        int position;
        long identity;

        @BindView(R.id.todoItem) TextView todoItem;
        @BindView(R.id.doneTodo) ImageView doneTodo;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            ButterKnife.bind(this, itemView);

        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null){
                onItemClickListener.onItemClick(null, v, position, 0);
            }
        }


        @Override
        public boolean onLongClick(View v) {
            if (onItemLongClickListener != null){
                onItemLongClickListener.onItemLongClick(null, v, position, 0);
            }
            return false;
        }
    }
}
