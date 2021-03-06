package com.JinxMarket;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment {

    public static final CharSequence PAGE_TITLE = "Users";

    private RecyclerView usersListRecyclerView;
    private UsersListAdapter usersListAdapter;
    private List<UserModel> mUserModels;

    private DatabaseReference mDatabaseUserRef;
    private NavController navController;

    private EditText usersListSearch;

    public UserListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_list, container, false);

        usersListSearch = v.findViewById(R.id.usersListSearch);

        usersListRecyclerView = v.findViewById(R.id.usersListRecyclerView);
        usersListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mDatabaseUserRef = FirebaseDatabase.getInstance().getReference("Users");

        mUserModels = new ArrayList<>();

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        mDatabaseUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot currentUsersListSnapshot) {
                for(DataSnapshot currentUserSnapshot: currentUsersListSnapshot.getChildren()){

                    UserModel currentUser = currentUserSnapshot.getValue(UserModel.class);
                    mUserModels.add(currentUser);
                }

                usersListAdapter = new UsersListAdapter(getContext(), mUserModels, navController);

                usersListRecyclerView.setAdapter(usersListAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        usersListSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filterList(s.toString());
            }
        });

    }

    private void filterList(String text) {
        ArrayList<UserModel> filteredUserModelList = new ArrayList<>();

        for(UserModel userModel: mUserModels){
            if(userModel.getUsername().toLowerCase().contains(text.toLowerCase())){
                filteredUserModelList.add(userModel);
            }
        }

        usersListAdapter.filterList(filteredUserModelList);
    }
}
