package com.JinxMarket;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class MarketItemFragment extends Fragment {

    private ImageView mImageView;

    private CircleImageView mUserPicImageView;
    private CircleImageView mUserStatusImage;
    private TextView mUserNameTextView;

    private TextView mTitleTextView;
    private TextView mBrandTextView;
    private TextView mSizeTextView;
    private TextView mConditionTextView;
    private TextView mPriceTextView;
    private TextView mDescriptionTextView;

    private TextView mFavoritesTextView;

    private ImageView mFavoritesImageView;

    private CardView mFavoritesButton;
    private CardView mEditButton;
    private CardView mMessageButton;

    private DatabaseReference mDataBaseRef;
    private DatabaseReference mFavoritesDataBaseRef;
    private String itemUserID;
    private String currentUserID;

    private RelativeLayout mUserForm;

    private AddFragmentModel addFragmentModel;
    private Bundle messageBundle = new Bundle();

    public MarketItemFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_market_item, container, false);

        mImageView = v.findViewById(R.id.marketFragmentImage);

        mUserPicImageView = v.findViewById(R.id.marketFragmentUserPic);
        mUserNameTextView = v.findViewById(R.id.marketFragmentUsername_tv);
        mUserStatusImage = v.findViewById(R.id.marketFragmentUserStatus);

        mTitleTextView = v.findViewById(R.id.marketFragmentTitle_tv);
        mBrandTextView = v.findViewById(R.id.marketFragmentBrand_tv);
        mSizeTextView = v.findViewById(R.id.marketFragmentSize_tv);
        mConditionTextView = v.findViewById(R.id.marketFragmentCondition_tv);
        mPriceTextView = v.findViewById(R.id.marketFragmentPrice_tv);
        mDescriptionTextView = v.findViewById(R.id.marketFragmentDescrDetailed_tv);
        mFavoritesTextView = v.findViewById(R.id.favorites_tv);
        mFavoritesImageView = v.findViewById(R.id.favorites_image);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mFavoritesButton = v.findViewById(R.id.addToFavoritesBtn);

        mEditButton = v.findViewById(R.id.editBTN);
        mEditButton.setEnabled(false);

        mMessageButton = v.findViewById(R.id.messageBTN);
        mMessageButton.setEnabled(false);

        mUserForm = v.findViewById(R.id.userForm);

        mDataBaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mFavoritesDataBaseRef = FirebaseDatabase.getInstance().getReference().child("Users/" + currentUserID + "/UserFavorites");


        if(getArguments() != null){
            addFragmentModel = getArguments().getParcelable("item");
            Picasso.get()
                    .load(addFragmentModel.getImageURL())
                    .into(mImageView);

            itemUserID = addFragmentModel.getUserIdModel();

            if(itemUserID.equals(currentUserID)){
                mEditButton.setEnabled(true);
                mEditButton.setVisibility(View.VISIBLE);
            } else {
                mDataBaseRef.child(currentUserID).child("UserChats").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() != null){
                            for (DataSnapshot currentChatSnapshot : dataSnapshot.getChildren()) {
                                if (currentChatSnapshot.child("receiver").getValue().toString().equals(addFragmentModel.getUserIdModel()) ||
                                        currentChatSnapshot.child("sender").getValue().toString().equals(addFragmentModel.getUserIdModel())
                                ) {
                                    messageBundle.putString("userReceiverID", addFragmentModel.getUserIdModel());
                                    messageBundle.putString("chatID", currentChatSnapshot.child("chatID").getValue().toString());
                                } else {
                                    messageBundle.putString("userReceiverID", addFragmentModel.getUserIdModel());
                                }
                            }
                    }else{
                            messageBundle.putString("userReceiverID", addFragmentModel.getUserIdModel());
                        }

                        mMessageButton.setEnabled(true);
                        mMessageButton.setVisibility(View.VISIBLE);

                        mMessageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {
                                NavController navController = Navigation.findNavController(v);
                                navController.navigate(R.id.action_marketItemFragment_to_chatFragment, messageBundle);

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            mDataBaseRef.child(addFragmentModel.getUserIdModel()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Picasso.get()
                            .load(dataSnapshot.child("imageURL").getValue().toString())
                            .into(mUserPicImageView);

                    if(dataSnapshot.child("status").getValue().toString().equals("online")){
                        mUserStatusImage.setVisibility(View.VISIBLE);
                    }
                    else{
                        mUserStatusImage.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mUserNameTextView.setText(addFragmentModel.getUsernameModel());
            mTitleTextView.setText(addFragmentModel.getTitleModel());
            mBrandTextView.setText(addFragmentModel.getBrandModel());
            mSizeTextView.setText(addFragmentModel.getSizeModel());
            mConditionTextView.setText(addFragmentModel.getConditionModel());
            mPriceTextView.setText(addFragmentModel.getPriceModel());
            mDescriptionTextView.setText(addFragmentModel.getDescriptionModel());

            mFavoritesDataBaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Boolean isInFavorites = false;
                    for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                        if(postSnapshot.child("uploadID").getValue().equals(addFragmentModel.getUploadIDModel())){
                            isInFavorites = true;
                        }
                    }
                    if(isInFavorites){setFavoritesButtonRemoval();}
                    else{
                        setFavoritesButtonAdding();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFavoritesDataBaseRef.child(addFragmentModel.getUploadIDModel()).child("uploadID").setValue(addFragmentModel.getUploadIDModel()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                        mFavoritesButton.setEnabled(false);
                        mFavoritesTextView.setText(R.string.remove_from_favorites);
                        mFavoritesButton.setCardBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.PlainTextColor2, null));
                    }
                });
            }
        });

        mUserForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("userID", itemUserID);
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_marketItemFragment_to_userPagerAdapterFragment, bundle);
            }
        });

        if(mEditButton.getVisibility() == View.VISIBLE){
            mEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavController navController = Navigation.findNavController(v);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("item", addFragmentModel);
                    navController.navigate(R.id.action_marketItemFragment_to_marketItemEditFragment, bundle);
                }
            });

        }

        if(mMessageButton.getVisibility() == View.VISIBLE){

        }
    }

    private void setFavoritesButtonRemoval(){
        mFavoritesImageView.setVisibility(View.GONE);
        mFavoritesButton.setCardBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.PlainTextColor2, null));
        mFavoritesTextView.setText(R.string.remove_from_favorites);

        mFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFavoritesDataBaseRef.child(addFragmentModel.getUploadIDModel()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                        setFavoritesButtonAdding();
                    }
                });
            }
        });
    }

    private void setFavoritesButtonAdding(){
        mFavoritesImageView.setVisibility(View.VISIBLE);
        mFavoritesButton.setCardBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
        mFavoritesTextView.setText(R.string.add_to_favorites);

        mFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFavoritesDataBaseRef.child(addFragmentModel.getUploadIDModel()).child("uploadID").setValue(addFragmentModel.getUploadIDModel()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                        setFavoritesButtonRemoval();
                    }
                });
            }
        });
    }
}
