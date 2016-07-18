package com.ua.ricardomartins.qualar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ua.ricardomartins.qualar.models.Pollutant;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by ricardo on 02/03/16.
 */
public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.MyViewHolder> {

    private MyListItemClickListener myListener;
    private Context mContext;
    private ArrayList<String[]> mAlerts;
    private String[] mKeys;

    public AlertAdapter(ArrayList<String[]> alerts, Context context){
        mContext = context;
        mAlerts = alerts;
        //mKeys = mPollutants.keySet().toArray(new String[pollutants.size()]);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_card,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String[] currentAlert= mAlerts.get(position);
        holder.mTextViewAlertMessage.setText(currentAlert[0]);
        holder.mTextViewAlertCampaign.setText(Html.fromHtml("<font color=#536DFE>Campanha: </font> <font color=#80000000>" + currentAlert[1] + "</font>"));
        holder.mTextViewAlertCode.setText(Html.fromHtml("<font color=#536DFE>Código de erro: </font> <font color=#80000000>" + currentAlert[3] + "</font>"));
        holder.mTextViewAlertDate.setText(Html.fromHtml("<font color=#536DFE>Data: </font> <font color=#80000000>" + currentAlert[2] + "</font>"));
    }

    @Override
    public int getItemCount() {
    return mAlerts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView mTextViewAlertMessage;
        public TextView mTextViewAlertCampaign;
        public TextView mTextViewAlertCode;
        public TextView mTextViewAlertDate;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTextViewAlertMessage = (TextView) itemView.findViewById(R.id.alert_message);
            mTextViewAlertCampaign = (TextView) itemView.findViewById(R.id.alert_campaign);
            mTextViewAlertCode = (TextView) itemView.findViewById(R.id.alert_code);
            mTextViewAlertDate = (TextView) itemView.findViewById(R.id.alert_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public class MyListItemClickListener {
    }
}
