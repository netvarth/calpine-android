package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.model.ProviderUserModel;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {
    UsersAdapter.OnItemClickListener onItemClickListener;
    static Context mcontext;
    ArrayList<ProviderUserModel> usersList = new ArrayList<ProviderUserModel>();

    String businessName;
    int department;

    public UsersAdapter(UsersAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setFields(ArrayList<ProviderUserModel> usersList, String businessName) {
        this.usersList = usersList;
        this.businessName = businessName;
    }

    public interface OnItemClickListener {

        void usersClick(ArrayList<ProviderUserModel> usersList, String businessName);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView deptName;
        public MyViewHolder(View view) {
            super(view);
            deptName = (TextView) view.findViewById(R.id.deptName);
        }
    }

    @NonNull
    @Override
    public UsersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.departmentlist, parent, false);
        return new UsersAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final UsersAdapter.MyViewHolder holder, final int position) {
        final ProviderUserModel providerUserModel = usersList.get(position);

        holder.deptName.setText(usersList.get(position).getFirstName() +" "+ usersList.get(position).getLastName() );

        holder.deptName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    onItemClickListener.usersClick(usersList,businessName);
            }
        });
    }
    @Override
    public int getItemCount() {
        return usersList.size();
    }
}