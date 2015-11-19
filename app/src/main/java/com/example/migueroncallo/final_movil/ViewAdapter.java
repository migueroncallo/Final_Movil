package com.example.migueroncallo.final_movil;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by migueroncallo on 11/19/15.
 */
public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private List<Information> data = Collections.emptyList();
    private RecyclerClickListner mRecyclerClickListener;

    public ViewAdapter(Context context, List<Information> data){
        inflater = LayoutInflater.from(context);
        this.data=data;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.customrow, viewGroup,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        Information information =data.get(i);
        myViewHolder.ct.setText(information.informacion);

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setRecyclerClickListner(RecyclerClickListner recyclerClickListner){
        mRecyclerClickListener = recyclerClickListner;
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView ct,cp;

        public MyViewHolder(View itemView) {
            super(itemView);
            ct= (TextView)itemView.findViewById(R.id.cursoTitle);
        }

        @Override
        public void onClick(View v) {
            if(mRecyclerClickListener != null){
                mRecyclerClickListener.itemClick(v, getPosition());
            }
        }
    }

    public interface RecyclerClickListner
    {
        public void itemClick(View view, int position);
    }
}
