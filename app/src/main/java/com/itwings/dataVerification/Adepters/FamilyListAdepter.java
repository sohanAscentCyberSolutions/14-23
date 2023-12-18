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

import com.itwings.dataVerification.FamilyModel;
import com.itwings.dataVerification.R;
import com.itwings.wastemanagement.Utills.Comman;

import java.util.ArrayList;

public class FamilyListAdepter extends RecyclerView.Adapter<FamilyListAdepter.MyViewHolder> {

    private ArrayList<FamilyModel> dataList;
    public ClickAdepterListener onClickListener;
    Activity ctx;
    Comman cmn;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvWardVillage, tvHOFName, tvMobile,tvMemberCount,tvAddress,tvBenifisory;
        public TextView btMembernotTracable,btEnterInfo;
        ImageButton menuBt;

        public MyViewHolder(View view) {
            super(view);
            btMembernotTracable =  view.findViewById(R.id.btMembernotTracable);
            btEnterInfo =  view.findViewById(R.id.btEnterInfo);
            tvWardVillage = (TextView) view.findViewById(R.id.tvWardVillage);
            tvHOFName = (TextView) view.findViewById(R.id.tvHOFName);
            tvMobile = (TextView) view.findViewById(R.id.tvMobile);
            tvMemberCount = (TextView) view.findViewById(R.id.tvMemberCount);
            tvAddress = (TextView) view.findViewById(R.id.tvAddress);
            tvBenifisory = (TextView) view.findViewById(R.id.tvBenifisory);
            menuBt = view.findViewById(R.id.menuBt);
        }
    }


    public FamilyListAdepter(Activity ctx , ArrayList<FamilyModel> dataList , ClickAdepterListener onClickListener) {
        this.dataList = dataList;
        this.onClickListener = onClickListener;
        this.ctx = ctx;
        cmn = new Comman(ctx);
    }

    @Override
    public FamilyListAdepter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_family_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final FamilyModel model = dataList.get(position);
        holder.tvWardVillage.setText(""+model.getWardVillage());
        holder.tvHOFName.setText(""+model.getHoF_Name());
        holder.tvAddress.setText(""+model.getAddress());
        holder.tvMobile.setText(""+model.getMobileno());
        holder.tvMemberCount.setText(""+model.getMemberCount());
        holder.tvBenifisory.setText(""+model.getName());
       // holder.tvMemberCount.setVisibility(View.GONE);
       /* if (!cmn.getUser().getDesignation().equals(Comman.Companion.getUserTeamLead())){
            holder.btMembernotTracable.setText("Members");
        }*/

      /*  if (cmn.getUser().getRole().equals("Zonal")){
            holder.btMembernotTracable.setText("Members");
            holder.menuBt.setVisibility(View.GONE);
        }*/
        holder.btMembernotTracable.setVisibility(View.GONE);
        holder.btEnterInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.enterInfoClicked( v , position);
            }
        });

        holder.btMembernotTracable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (cmn.getUser().getRole().equals("Zonal")){
                    onClickListener.membersClicked( v , position);
                }else if (cmn.getUser().getDesignation().equals(Comman.Companion.getUserTeamLead())){
                    onClickListener.membernotTracableClicked( v , position);
                }else{
                    onClickListener.membersClicked( v , position);
                }*/
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
        void enterInfoClicked(View v, int position);
        void updateFamilyMemberCountClicked(View v, int position);
        void membersClicked(View v, int position);
    }

    void setUpPopup(int forPosition, View v){
        PopupMenu popup = new PopupMenu(ctx, v);
        popup.getMenu().add("Enter Info");
       // popup.getMenu().add("Members");
       // popup.getMenu().add("Member Not Traceable");
       // popup.getMenu().add("Update Family Member Count");
       /* if (cmn.getUser().getDesignation().equalsIgnoreCase(Comman.Companion.getUserTeamLead())) {
            popup.getMenu().add("Enter Info");
            popup.getMenu().add("Members");
            popup.getMenu().add("Member Not Traceable");
            popup.getMenu().add("Update Family Member Count");
        }else{
            popup.getMenu().add("Members");
            popup.getMenu().add("Enter Info");
            // popup.getMenu().add("Edit")
        }*/
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString()){
                    case "Enter Info":
                        onClickListener.enterInfoClicked( v , forPosition);
                        break;
                    case "Member Not Traceable":
                        onClickListener.membernotTracableClicked( v , forPosition);
                        break;
                    case "Members":
                        onClickListener.membersClicked( v , forPosition);
                        break;
                    case "Update Family Member Count":
                        onClickListener.updateFamilyMemberCountClicked( v , forPosition);
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
