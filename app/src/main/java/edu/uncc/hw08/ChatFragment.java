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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import edu.uncc.hw08.databinding.FragmentChatBinding;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ChatFragment extends Fragment {

    ArrayList<Message> messageArrayList = new ArrayList<>();
    ChatFragmentRecyclerViewAdapter adapter;
    Message message, mMessage;
    LocalDateTime date;
    String name;
    String pattern = "MM/dd/yyyy HH:mma";
    DateTimeFormatter dateTime = DateTimeFormatter.ofPattern(pattern);



    private static final String ARG_PARAM_MESSAGE = "param_message";

    public ChatFragment() {
        // Required empty public constructor
    }


    public static ChatFragment newInstance(Message data) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_MESSAGE, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMessage = (Message) getArguments().getSerializable(ARG_PARAM_MESSAGE);
        }
    }

    FragmentChatBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("Send", "onViewCreated: " + mMessage.receiver);


        //Close button
        binding.buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Close", "onClick: " + "Close");
                mListener.goBackMyChats();
            }
        });

        //Delete the whole chat
        binding.buttonDeleteChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                db.collection("Users")
                        .document(user.getUid())
                        .collection("Message List")
                        .document()
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("Message", "onSuccess: Message conversation successfully deleted");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Message", "onFailure: Error deleting message conversation" + e);
                            }
                        });

            }
        });


        //Submit message
        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.editTextMessage.getText().toString();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                DocumentReference docRef = db.collection("Users").document(firebaseUser.getUid()).
                        collection("Message List").document();

                message = new Message();
                message.setMessageID(docRef.getId());
                message.setCreatorID(FirebaseAuth.getInstance().getCurrentUser().getUid());
                message.setMessage(binding.editTextMessage.getText().toString());
                date = LocalDateTime.now();
                message.setDate(dateTime.format(date));
                message.setCreator( firebaseUser.getDisplayName());
                //message.setReceiver(name);
                docRef.set(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            adapter.notifyDataSetChanged();
                        } else {
                        }
                    }
                });
            }
        });


        //Display the message
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatFragmentRecyclerViewAdapter(messageArrayList);
        binding.recyclerView.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //.orderBy("createdAt", descending: true).limit(1)

        db.collection("Users").document(user.getUid())
                .collection("Message List")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        messageArrayList.clear();
                        for(QueryDocumentSnapshot messageDoc : value) {
                            Message message = messageDoc.toObject(Message.class);
                            if (message.creatorID.equals(user.getUid()) || message.receiverID.equals(user.getUid()))
                            {
                                messageArrayList.add(message);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

    }


    class ChatFragmentRecyclerViewAdapter extends RecyclerView.Adapter<ChatFragmentRecyclerViewAdapter.ChatFragmentViewHolder>{

        ArrayList<Message> messageArrayList = new ArrayList<>();
        public ChatFragmentRecyclerViewAdapter(ArrayList<Message> messageArrayList) {
            this.messageArrayList = messageArrayList;
        }

        @NonNull
        @Override
        public ChatFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);
            ChatFragmentRecyclerViewAdapter.ChatFragmentViewHolder chatFragmentViewHolder = new ChatFragmentViewHolder(view);
            return chatFragmentViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ChatFragmentViewHolder holder, int position) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            Message message = messageArrayList.get(position);

            holder.textViewMsgText.setText(message.message);
            holder.textViewMsgOn.setText(message.date);


            if(user != null && message.creator.equals(user.getDisplayName())) {
                holder.textViewMsgBy.setText("ME");
                holder.trash.setVisibility(View.VISIBLE);
            } else {
                holder.textViewMsgBy.setText(mMessage.creator);
                holder.trash.setVisibility(View.INVISIBLE);
            }

            holder.message = message;



        }

        @Override
        public int getItemCount() {
            return messageArrayList.size();
        }

        public class ChatFragmentViewHolder extends RecyclerView.ViewHolder{
            Message message;
            TextView textViewMsgBy, textViewMsgText, textViewMsgOn;
            ImageView trash;

            public ChatFragmentViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewMsgBy = itemView.findViewById(R.id.textViewMsgBy);
                textViewMsgText = itemView.findViewById(R.id.textViewMsgText);
                textViewMsgOn = itemView.findViewById(R.id.textViewMsgOn);
                trash = itemView.findViewById(R.id.imageViewDelete);

                trash.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        db.collection("Users")
                                .document(user.getUid())
                                .collection("Message List")
                                .document(message.messageID)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("Message", "onSuccess: Message successfully deleted");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Message", "onFailure: Error deleting message" + e);
                                    }
                                });

                    }
                });


            }
        }


    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ChatFragmentListener) context;
    }
    ChatFragmentListener mListener;
    interface ChatFragmentListener{
        void goBackMyChats();
    }
}