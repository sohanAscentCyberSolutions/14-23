package com.itwings.dataVerification.Adepters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.itwings.dataVerification.FamilyMemberModel;
import com.itwings.dataVerification.R;

import java.util.ArrayList;

public class FamilyMembersAdepter extends RecyclerView.Adapter<FamilyMembersAdepter.MyViewHolder> {

    private ArrayList<FamilyMemberModel> dataList;
    Activity ctx;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvMemberId, tvRelationWithHead,tvGender,tvAge;

        public MyViewHolder(View view) {
            super(view);
            tvName =  view.findViewById(R.id.tvName);
            tvMemberId =  view.findViewById(R.id.tvMemberId);
            tvRelationWithHead = (TextView) view.findViewById(R.id.tvRelationWithHead);
            tvGender = (TextView) view.findViewById(R.id.tvGender);
            tvAge = (TextView) view.findViewById(R.id.tvAge);
        }
    }


    public FamilyMembersAdepter(Activity ctx , ArrayList<FamilyMemberModel> dataList) {
        this.dataList = dataList;
        this.ctx = ctx;
    }

    @Override
    public FamilyMembersAdepter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_member_details, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final FamilyMemberModel model = dataList.get(position);
        holder.tvName.setText(""+model.getName());
        holder.tvMemberId.setText(""+model.getNewMemberID());
        holder.tvRelationWithHead.setText(""+model.getRelationWithHead());
        holder.tvGender.setText(""+model.getGender());
        holder.tvAge.setText(""+model.getAge());


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
