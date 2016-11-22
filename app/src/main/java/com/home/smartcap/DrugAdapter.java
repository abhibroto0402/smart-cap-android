package com.home.smartcap;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by amukherjee on 11/16/16.
 */

public class DrugAdapter extends ArrayAdapter <DrugSchedule> {

    Context mcontext;
    int mLayoutResourceId;
    DrugSchedule mdata[] = null;

    public DrugAdapter(Context context, int resource, DrugSchedule[] data) {
        super(context, resource, data);
        this.mcontext = context;
        this.mLayoutResourceId = resource;
        this.mdata = data;
    }

    @Nullable
    @Override
    public DrugSchedule getItem(int position) {
        return super.getItem(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        LayoutInflater inflater = LayoutInflater.from(mcontext);
        row = inflater.inflate(mLayoutResourceId,parent, false);

        //get a reference to the different view elements we wish to update
        TextView drug_nameView = (TextView) row.findViewById(R.id.drugname);
        TextView dailyTime = (TextView) row.findViewById(R.id.daily_time);
        TextView expdate = (TextView) row.findViewById(R.id.expdate);
        TextView next_due = (TextView) row.findViewById(R.id.next_due);

        DrugSchedule ds = mdata[position];

        drug_nameView.setText( ds.getListData("drug_name"));
        dailyTime.setText(ds.getListData("dosage"));
        expdate.setText(ds.getListData("expdate"));
        next_due.setText(ds.getListData("nextIntake"));

        return row;
    }
}
