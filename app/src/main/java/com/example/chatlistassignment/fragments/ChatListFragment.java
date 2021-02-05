package com.example.chatlistassignment.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatlistassignment.ItemClickListener;
import com.example.chatlistassignment.R;
import com.example.chatlistassignment.activities.DetailedUserInfoActivity;
import com.example.chatlistassignment.activities.EditUserInfoActivity;
import com.example.chatlistassignment.adapters.RecyclerViewAdapter;
import com.example.chatlistassignment.model.User;
import com.example.chatlistassignment.utils.AlertDialogHelper;
import com.example.chatlistassignment.utils.RecyclerItemClickListener;
import com.example.chatlistassignment.viewmodel.FragmentViewModel;

import java.util.ArrayList;
import java.util.List;


public class ChatListFragment extends Fragment implements ItemClickListener, AlertDialogHelper.AlertDialogListener {
    ActionMode mActionMode;
    ///recycler view
    RecyclerView recyclerViewChatList;
    LinearLayoutManager layoutManager;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<User> userArrayList;
    ArrayList<User> multiselect_list = new ArrayList<>();
    boolean isMultiSelect = false;
    ArrayList<User> queryArrayList;
    Menu context_menu;
    FragmentViewModel fragmentViewModel;
    boolean isScrolling = false;
    int currentItem, totalItem, scrollOutItem;
    int showListItem = 8;
    AlertDialogHelper alertDialogHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentViewModel = new ViewModelProvider(this).get(FragmentViewModel.class);
        userArrayList = new ArrayList<>();
        queryArrayList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        init(view);

        observeForDbChanges();
        observeQueryString();

        recyclerViewChatList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItem = layoutManager.getChildCount();
                totalItem = layoutManager.getItemCount();
                if (userArrayList.size() > showListItem) {
                    dataFetch();
                }
                scrollOutItem = layoutManager.findFirstVisibleItemPosition();
                if (isScrolling && (currentItem + scrollOutItem == totalItem)) {
                    isScrolling = false;
                    dataFetch();
                }
            }
        });

        recyclerViewChatList.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerViewChatList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect)
                    multi_select(position);
                else
                    Toast.makeText(getActivity(), "Details Page", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<User>();
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        mActionMode = getActivity().startActionMode(mActionModeCallback);
                    }
                }

                multi_select(position);

            }
        }));
        return view;
    }

    private void dataFetch() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 1000);
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    alertDialogHelper.showAlertDialog("", "Delete Contact", "DELETE", "CANCEL", 1, false);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<User>();
            refreshAdapter();
        }
    };


    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(userArrayList.get(position)))
                multiselect_list.remove(userArrayList.get(position));
            else
                multiselect_list.add(userArrayList.get(position));

            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            refreshAdapter();

        }
    }


    private void observeQueryString() {
        fragmentViewModel.getQueryString().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String query) {
                Log.d("TAG", "Inside ChatListFragment: " + query);
                queryChatList(query);
            }
        });
    }

    private void queryChatList(String query) {
        query = "%" + query + "%";

        fragmentViewModel.queryAllUser(getContext(), query).observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                queryArrayList.clear();
                queryArrayList = (ArrayList<User>) users;
                recyclerViewAdapter.updateData(queryArrayList);
            }
        });
    }

    private void observeForDbChanges() {
        fragmentViewModel.getAllUser(getContext()).observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                userArrayList.clear();
                userArrayList = (ArrayList<User>) users;
                recyclerViewAdapter.updateData(userArrayList);
            }
        });
    }

    private void init(View view) {
        alertDialogHelper = new AlertDialogHelper(getContext(), ChatListFragment.this);
        recyclerViewChatList = view.findViewById(R.id.recyclerview_chat_list);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), userArrayList, multiselect_list, this);

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerViewChatList);
        recyclerViewChatList.setLayoutManager(layoutManager);
        recyclerViewChatList.setAdapter(recyclerViewAdapter);

    }

    public void refreshAdapter() {
        recyclerViewAdapter.selected_usersList = multiselect_list;
        recyclerViewAdapter.userArrayList = userArrayList;
        recyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClicked(View view, int position) {
        switch (view.getId()) {
            case R.id.button_delete:
                fragmentViewModel.deleteUser(userArrayList.get(position), getContext());
                break;
            case R.id.button_edit:
                Intent intentEditUserInfoActivity = new Intent(getContext(), EditUserInfoActivity.class);
                intentEditUserInfoActivity.putExtra("User", userArrayList.get(position));
                startActivity(intentEditUserInfoActivity);
                break;
            default:
                Intent intentDetailedUserInfoActivity = new Intent(getContext(), DetailedUserInfoActivity.class);
                intentDetailedUserInfoActivity.putExtra("User", userArrayList.get(position));
                startActivity(intentDetailedUserInfoActivity);
                break;
        }
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            fragmentViewModel.deleteUser(userArrayList.get(viewHolder.getAdapterPosition()), getContext());
        }
    };

    @Override
    public void onPositiveClick(int from) {
        if (from == 1) {
            if (multiselect_list.size() > 0) {
                userArrayList = multiselect_list;
                for (int i = 0; i < multiselect_list.size(); i++)
                    //  userArrayList.remove(multiselect_list.get(i));

                    fragmentViewModel.deleteUser(userArrayList.get(i), getContext());
                recyclerViewAdapter.notifyDataSetChanged();

                if (mActionMode != null) {
                    mActionMode.finish();
                }
                Toast.makeText(getActivity(), "Delete Click", Toast.LENGTH_SHORT).show();
            }
        } else if (from == 2) {
            if (mActionMode != null) {
                mActionMode.finish();
            }

            //SampleModel mSample = new SampleModel("Name"+user_list.size(),"Designation"+user_list.size());
            //userArrayList.add(mSample);
            //multiSelectAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }
}