package edu.uncc.hw08;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import edu.uncc.hw08.databinding.FragmentMyChatsBinding;


public class MyChatsFragment extends Fragment {


    FragmentMyChatsBinding binding;
    ArrayList<Message> messageArrayList = new ArrayList<>();
    MyChartsFragmentInterface mListener;
    MyChatsFragmentRecyclerAdapter adapter;


    public MyChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyChatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("My Chats");

        //Log out
        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.logout();


            }
        });



        //Create new chat
        binding.buttonNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.createChat();
            }
        });

        //Display message list
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyChatsFragmentRecyclerAdapter(messageArrayList);
        binding.recyclerView.setAdapter(adapter);
        getMessage();

    }

    public void getMessage(){
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



    class MyChatsFragmentRecyclerAdapter extends RecyclerView.Adapter<MyChatsFragmentRecyclerAdapter.MessageViewHolder>{

        ArrayList<Message> messages = new ArrayList<>();
        public MyChatsFragmentRecyclerAdapter(ArrayList<Message> data){
            this.messages = data;
        }

        @NonNull
        @Override
        public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_chats_list_item, parent, false);
            MyChatsFragmentRecyclerAdapter.MessageViewHolder messageViewHolder = new MessageViewHolder(view);

            return messageViewHolder;

        }

        @Override
        public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
            Message message = messages.get(position);
            holder.messageBy.setText(message.receiver);
            holder.messageText.setText(message.message);
            holder.messageOn.setText(message.date);
            holder.message = message;

        }

        @Override
        public int getItemCount() {
            return this.messages.size();
        }

        public class MessageViewHolder extends RecyclerView.ViewHolder{
            TextView messageBy, messageText, messageOn;
            Message message;


            public MessageViewHolder(@NonNull View itemView) {
                super(itemView);

                //Display message info
                messageBy = itemView.findViewById(R.id.textViewMsgBy);
                messageText = itemView.findViewById(R.id.textViewMsgText);
                messageOn = itemView.findViewById(R.id.textViewMsgOn);

                //Go to Chat Fragment
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.gotoChat(message);
                    }
                });
            }
        }


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (MyChartsFragmentInterface) context;
    }
    interface MyChartsFragmentInterface{
        void gotoLogin();
        void createChat();
        void logout();
        void gotoChat(Message message);
    }
}