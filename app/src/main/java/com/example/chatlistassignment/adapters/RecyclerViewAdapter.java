package com.example.chatlistassignment.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlistassignment.ItemClickListener;
import com.example.chatlistassignment.R;
import com.example.chatlistassignment.model.User;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {


    Context context;
    public ArrayList<User> userArrayList;
    ItemClickListener itemClickListener;
    public ArrayList<User> selected_usersList = new ArrayList<>();

    public RecyclerViewAdapter(Context context, ArrayList<User> userArrayList, ArrayList<User> multiselect_list, ItemClickListener itemClickListener) {
        this.context = context;
        this.userArrayList = userArrayList;
        this.selected_usersList = multiselect_list;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chat_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userArrayList.get(position);
        holder.textViewName.setText(user.getName());
        holder.textViewNumber.setText(user.getContactNumber());

        if (user.getProfilePic() == null || user.getProfilePic().equalsIgnoreCase(""))
            holder.imageViewProfilePic.setImageResource(R.drawable.ic_baseline_person_24);
        else
            holder.imageViewProfilePic.setImageURI(Uri.parse(user.getProfilePic()));

        if (selected_usersList.contains(userArrayList.get(position)))
            holder.clView.setBackgroundColor(ContextCompat.getColor(context, R.color.list_item_selected_state));
        else
            holder.clView.setBackgroundColor(ContextCompat.getColor(context, R.color.list_item_normal_state));

    }

    @Override
    public int getItemCount() {
        return userArrayList == null ? 0 : userArrayList.size();
    }

    public void updateData(ArrayList<User> userArrayList) {
        this.userArrayList = userArrayList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageViewProfilePic;
        TextView textViewName, textViewNumber;
        Button buttonEdit, buttonDelete;
        CardView clView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewProfilePic = itemView.findViewById(R.id.image_view_profile_pic);
            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewNumber = itemView.findViewById(R.id.text_view_number);
            clView = itemView.findViewById(R.id.clView);
            buttonEdit = itemView.findViewById(R.id.button_edit);
            buttonDelete = itemView.findViewById(R.id.button_delete);

            itemView.setOnClickListener(this);

            buttonEdit.setOnClickListener(this);
            buttonDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null)
                itemClickListener.onItemClicked(view, getAdapterPosition());
        }
    }
}
