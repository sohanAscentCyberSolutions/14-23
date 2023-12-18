package com.itwings.dataVerification.Adepters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.itwings.dataVerification.FamilyMemberModel;
import com.itwings.dataVerification.R;
import com.itwings.wastemanagement.Utills.Comman;

import java.util.ArrayList;

public class FamilyListDOBAdepter extends RecyclerView.Adapter<FamilyListDOBAdepter.MyViewHolder> {

    private ArrayList<FamilyMemberModel> dataList;
    public ClickAdepterListener onClickListener;
    Activity ctx;
    Comman cmn;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvFamilyId, tvHOFName, tvMobile,tvGender,tvAddress;
        public TextView btMembernotTracable,btDOBVerification;
        ImageButton menuBt;

        public MyViewHolder(View view) {
            super(view);
            btMembernotTracable =  view.findViewById(R.id.btMembernotTracable);
            btDOBVerification =  view.findViewById(R.id.btDOBVerification);
            tvFamilyId = (TextView) view.findViewById(R.id.tvFamilyId);
            tvHOFName = (TextView) view.findViewById(R.id.tvHOFName);
            tvMobile = (TextView) view.findViewById(R.id.tvMobile);
            tvGender = (TextView) view.findViewById(R.id.tvGender);
            tvAddress = (TextView) view.findViewById(R.id.tvAddress);
            menuBt = view.findViewById(R.id.menuBt);
        }
    }


    public FamilyListDOBAdepter(Activity ctx , ArrayList<FamilyMemberModel> dataList , ClickAdepterListener onClickListener) {
        this.dataList = dataList;
        this.onClickListener = onClickListener;
        this.ctx = ctx;
        cmn = new Comman(ctx);
    }

    @Override
    public FamilyListDOBAdepter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_family_list_dob, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final FamilyMemberModel model = dataList.get(position);
        holder.tvFamilyId.setText(""+model.getNewFamilyID());
        holder.tvHOFName.setText(""+model.getName());
        holder.tvAddress.setText(""+model.getAddress());
        holder.tvMobile.setText(""+model.getMobileno());
        holder.tvGender.setText(""+model.getGender());


        if (!cmn.getUser().getDesignation().equals(Comman.Companion.getUserTeamLead())){
            holder.btMembernotTracable.setVisibility(View.GONE);
        }


        holder.btDOBVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.btDOBVerificationClicked( v , position);
            }
        });



        holder.btMembernotTracable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.membernotTracableClicked( v , position);
            }
        });

        holder.menuBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpPopup(position,v);
            }
        });

    }


    public interface ClickAdepterListener {
        void membernotTracableClicked(View v, int position);
        void btDOBVerificationClicked(View v, int position);
        void markAsDeadClicked(View v, int position);
    }

    void setUpPopup(int forPosition, View v){
        PopupMenu popup = new PopupMenu(ctx, v);
        if (cmn.getUser().getDesignation().equalsIgnoreCase(Comman.Companion.getUserTeamLead())) {
            popup.getMenu().add("DOB Verification");
            popup.getMenu().add("Member Not Traceable");
            popup.getMenu().add("Mark As Dead");
        }else{
            popup.getMenu().add("DOB Verification");
            // popup.getMenu().add("Edit")
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString()){
                    case "DOB Verification":
                        onClickListener.btDOBVerificationClicked( v , forPosition);
                        break;
                    case "Member Not Traceable":
                        onClickListener.membernotTracableClicked( v , forPosition);
                        break;
                    case "Mark As Dead":
                        onClickListener.markAsDeadClicked( v , forPosition);
                        break;
                }
                //Toast.makeText(context, "Some Text" + item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        popup.show();
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
