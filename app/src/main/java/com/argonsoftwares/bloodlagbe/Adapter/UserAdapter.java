package com.argonsoftwares.bloodlagbe.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.argonsoftwares.bloodlagbe.Email.JavaMailApi;
import com.argonsoftwares.bloodlagbe.Model.User;
import com.argonsoftwares.bloodlagbe.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = userList.get(position);


        holder.userName.setText(user.getFullName());
        holder.userEmail.setText(user.getEmail());
        holder.userPhone.setText(user.getPhoneNumber());
        holder.userBloodGroup.setText(user.getBloodGroup());

        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            Glide.with(context).load(user.getProfileImageUrl()).into(holder.profileImage);
        } else {
            holder.profileImage.setImageResource(R.drawable.profile_avatar);
        }

        final String receiverName = user.getFullName();
        final String receiverId = user.getId();

        // Sending Email
        holder.emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Send Email")
                        .setMessage("Do you want to send email to " + receiverName + " ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference reference =
                                        FirebaseDatabase.getInstance().getReference().child("users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                reference.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String email = snapshot.child("email").getValue().toString();
                                        String name = snapshot.child("fullName").getValue().toString();
                                        String phone = snapshot.child("phoneNumber").getValue().toString();
                                        String bloodGroup = snapshot.child("bloodGroup").getValue().toString();

                                        String mEmail = user.getEmail();
                                        String mSubject = "Blood Request";
                                        String mMessage = "Hello" + name + "\n" +
                                                "would like blood donation from you. Here's his/her " +
                                                "details:\n" +
                                                "Name: " + name + "\n" +
                                                "Email: " + email + "\n" +
                                                "Phone: " + phone + "\n" +
                                                "Blood Group: " + bloodGroup + "\n" +
                                                "Thank you for your support.";

                                        JavaMailApi javaMailApi = new JavaMailApi(context, mEmail, mSubject, mMessage);
                                        javaMailApi.execute();

                                        DatabaseReference reference1 =
                                                FirebaseDatabase.getInstance().getReference(
                                                        "emails").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        reference1.child(receiverId).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    DatabaseReference receiver =
                                                            FirebaseDatabase.getInstance().getReference("emails").child(receiverId);
                                                    receiver.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

                                                    // Sending notification to receiver
                                                    addNotifications(receiverId, FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView profileImage;
        public TextView userName, userEmail, userPhone, userBloodGroup;
        public Button emailButton, callButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.displayView_profileImage);

            userName = itemView.findViewById(R.id.displayView_userName);
            userEmail = itemView.findViewById(R.id.displayView_email);
            userPhone = itemView.findViewById(R.id.displayView_phoneNumber);
            userBloodGroup = itemView.findViewById(R.id.displayView_bloodGroup);

            emailButton = itemView.findViewById(R.id.displayView_btnEmail);
            callButton = itemView.findViewById(R.id.displayView_btnCall);
        }
    }

    private void addNotifications(String receiverId, String senderId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(
                "notifications").child(receiverId);
        String date = DateFormat.getDateTimeInstance().format(new Date());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("receiverId", receiverId);
        hashMap.put("senderId", senderId);
        hashMap.put("text", "You have received an email. Please check your email.");
        hashMap.put("date", date);

        reference.push().setValue(hashMap);
    }
}
