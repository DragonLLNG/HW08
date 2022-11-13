package edu.uncc.hw08;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import edu.uncc.hw08.databinding.FragmentCreateChatBinding;

@RequiresApi(api = Build.VERSION_CODES.O)
public class CreateChatFragment extends Fragment {

    FragmentCreateChatBinding binding;
    CreateChatListener mListener;
    ArrayList<Roomchat> roomchatArrayList = new ArrayList<>();
    ArrayList<Message> messageList = new ArrayList<>();
    Roomchat roomchat = new Roomchat();
    ArrayList<User> userList = new ArrayList<>();
    User currentUser;

    CreateChatFragmentRecyclerAdapter adapter;
    Message message = new Message();
    LocalDateTime date;
    String name;
    String id;
    String pattern = "MM/dd/yyyy HH:mma";
    DateTimeFormatter dateTime = DateTimeFormatter.ofPattern(pattern);


    private static final String ARG_PARAM_USER = "param_user";

    public CreateChatFragment() {
        // Required empty public constructor
    }

    public static CreateChatFragment newInstance(User data) {
        CreateChatFragment fragment = new CreateChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_USER, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable(ARG_PARAM_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateChatBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("New Chat");


        //Cancel
        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goBackMyChats();

            }
        });



        //Send chat
        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.editTextMessage.getText().toString().isEmpty() || name == null) {
                    Toast.makeText(getActivity(), "All fields are required!!!!", Toast.LENGTH_SHORT).show();
                }

                else{

                //Create room chat
                String combineId = FirebaseAuth.getInstance().getUid() + id;
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("RoomChat").document(combineId);


                roomchat.setRoomId(FirebaseAuth.getInstance().getUid() + id);
                roomchat.userIds.add(FirebaseAuth.getInstance().getUid());
                roomchat.userIds.add(id);
                roomchat.userNames.add(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                roomchat.userNames.add(name);
                roomchat.setTime(new Date());

                docRef.set(roomchat).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            roomchatArrayList.add(roomchat);

                        } else {
                        }
                    }
                });


                DocumentReference docRefMess = docRef
                        .collection("Message").document();
                message.setMessageID(docRefMess.getId());
                message.setCreatorID(FirebaseAuth.getInstance().getCurrentUser().getUid());
                message.setMessage(binding.editTextMessage.getText().toString());
                date = LocalDateTime.now();
                message.setDate(dateTime.format(date));
                message.setCreator(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                message.setReceiver(name);
                message.setReceiverID(id);
                message.setCreatedAt(new Date());


                docRefMess.set(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("check", "onComplete: " + messageList);
                            messageList.add(message);
                            mListener.goBackMyChats();

                        } else {

                        }
                    }
                });

                FirebaseFirestore.getInstance().collection("RoomChat").document(roomchat.roomId)
                        .update("message", message).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

            }
        }
        });




        //Get the list of users
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CreateChatFragmentRecyclerAdapter(userList);
        binding.recyclerView.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        userList.clear();
                        for(QueryDocumentSnapshot userDoc : value) {
                            User user = userDoc.toObject(User.class);
                            if (!user.userName.equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())){
                                userList.add(user);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }


    class CreateChatFragmentRecyclerAdapter extends RecyclerView.Adapter<CreateChatFragmentRecyclerAdapter.CreateChatViewHolder>{
        ArrayList<User> users = new ArrayList<>();

        public CreateChatFragmentRecyclerAdapter (ArrayList<User> data){
            this.users = data;
        }

        @NonNull
        @Override
        public CreateChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_row_item, parent, false);
            CreateChatFragmentRecyclerAdapter.CreateChatViewHolder createChatViewHolder = new CreateChatViewHolder(view);
            return createChatViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull CreateChatViewHolder holder, int position) {
            User user = users.get(position);
            holder.userTextView.setText(user.userName);
            holder.user = user;


            //Set online
            if (user.status==true){
                holder.onlineImageView.setVisibility(View.VISIBLE);
            }
            else{
                holder.onlineImageView.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public int getItemCount() {
            return this.users.size();
        }


        public class CreateChatViewHolder extends RecyclerView.ViewHolder{
            TextView userTextView;
            ImageView onlineImageView;
            User user;

            public CreateChatViewHolder(@NonNull View itemView) {
                super(itemView);
                userTextView = itemView.findViewById(R.id.textViewName);
                onlineImageView = itemView.findViewById(R.id.imageViewOnline);

                //Select user to send chat
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Click", "onClick: " + user.userName);
                        binding.textViewSelectedUser.setText(user.userName);
                        //Setting the receiver
                        name = user.userName;
                        id = user.userID;

                    }
                });
            }
        }
    }




    interface CreateChatListener{
        void goBackMyChats();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (CreateChatListener) context;

    }
}