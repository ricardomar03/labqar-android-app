package com.ua.ricardomartins.qualar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ua.ricardomartins.qualar.models.Pollutant;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by ricardo on 02/03/16.
 */
public class PollutantAdapter extends RecyclerView.Adapter<PollutantAdapter.MyViewHolder> {

    private MyListItemClickListener myListener;
    private Context mContext;
    private LinkedHashMap<String, Pollutant> mPollutants;
    private String[] mKeys;

    public PollutantAdapter(LinkedHashMap<String, Pollutant> pollutants, Context context, MyListItemClickListener listener){
        myListener = listener;
        mContext = context;
        mPollutants = pollutants;
        mKeys = mPollutants.keySet().toArray(new String[pollutants.size()]);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pollutant_card,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Pollutant currentPollutant= mPollutants.get((mKeys[position]));

        holder.mTextViewPollutantName.setText(currentPollutant.getName());
        holder.mTextViewPollutantScale.setText("Medido em " + currentPollutant.getUnit());
        holder.mTextViewPollutantValue.setText(currentPollutant.getValue());
        if(Integer.parseInt(currentPollutant.getStat()) > 0 && ApiManager.getLoggedUser().isSuperuser()) {
            holder.mTextViewPollutantAlert.setVisibility(View.VISIBLE);
            holder.mTextViewPollutantValue.setText(currentPollutant.getValue());
        }
        else{
            holder.mTextViewPollutantAlert.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mPollutants.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView mTextViewPollutantName;
        public TextView mTextViewPollutantScale;
        public TextView mTextViewPollutantValue;
        public TextView mTextViewPollutantAlert;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTextViewPollutantName = (TextView) itemView.findViewById(R.id.pollutant_name);
            mTextViewPollutantScale = (TextView) itemView.findViewById(R.id.pollutant_scale);
            mTextViewPollutantValue = (TextView) itemView.findViewById(R.id.pollutant_value);
            mTextViewPollutantAlert = (TextView) itemView.findViewById(R.id.pollutant_alert);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myListener.onItemClick(mKeys[getPosition()]);
        }
    }

    public static interface MyListItemClickListener {

        public void onItemClick(String pollutantName);

    }
}
