package edu.uncc.hw08;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.uncc.hw08.databinding.FragmentMyChatsBinding;


public class MyChatsFragment extends Fragment {


    FragmentMyChatsBinding binding;
    ArrayList<Roomchat> roomchatArrayList = new ArrayList<>();
    ArrayList<Message> lastMessageArrayList = new ArrayList<>();
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
        adapter = new MyChatsFragmentRecyclerAdapter(roomchatArrayList);
        binding.recyclerView.setAdapter(adapter);
        getMessage();
        Log.d("sorted list", "onViewCreated: "+roomchatArrayList.size());
        //Log.d("sorted list", "onViewCreated: "+roomchatArrayList.get(roomchatArrayList.size()-1).message.createdAt);




    }

    public void getMessage(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("RoomChat").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                lastMessageArrayList.clear();
                roomchatArrayList.clear();
                for (QueryDocumentSnapshot roomChatDoc: value){
                    if(roomChatDoc.getId().contains(user.getUid())) {
                        Roomchat roomchat = roomChatDoc.toObject(Roomchat.class);
                        roomchatArrayList.add(roomchat);
                        lastMessageArrayList.add(roomchat.message);
                        adapter.notifyDataSetChanged();
                    }

                }

                if(roomchatArrayList.size()>=2) {
                    Collections.sort(roomchatArrayList, new Comparator<Roomchat>() {
                        @Override
                        public int compare(Roomchat r1, Roomchat r2) {
                            return (-1)*r1.time.compareTo(r2.time);
                        }
                    });
                    adapter.notifyDataSetChanged();
                }
                adapter.notifyDataSetChanged();


            }
        });
    }


    class MyChatsFragmentRecyclerAdapter extends RecyclerView.Adapter<MyChatsFragmentRecyclerAdapter.MessageViewHolder>{

        ArrayList<Roomchat> roomchats = new ArrayList<>();
        public MyChatsFragmentRecyclerAdapter(ArrayList<Roomchat> data){
            this.roomchats = data;
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
            Roomchat roomchat = roomchats.get(position);
            Log.d("test", "onBindViewHolder: "+ roomchat.message);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user.getDisplayName().equals(roomchat.userNames.get(0))) {
                holder.messageBy.setText(roomchat.userNames.get(1));
            }
            else { holder.messageBy.setText(roomchat.userNames.get(0)); }

            if(roomchat.message !=null) {
            holder.messageText.setText(roomchat.message.message);
            holder.messageOn.setText(roomchat.message.date);
            holder.roomchat = roomchat;
            }

        }

        @Override
        public int getItemCount() {
            return this.roomchats.size();
        }

        public class MessageViewHolder extends RecyclerView.ViewHolder{
            TextView messageBy, messageText, messageOn;
            Roomchat roomchat;


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
                        mListener.gotoChat(roomchat);
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
        void gotoChat(Roomchat roomchat);
    }
}