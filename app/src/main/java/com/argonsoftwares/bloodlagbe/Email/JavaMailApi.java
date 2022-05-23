package com.argonsoftwares.bloodlagbe.Email;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.argonsoftwares.bloodlagbe.R;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class JavaMailApi extends AsyncTask<Void, Void, Void> {

    private Context context;
    private Session session;
    private String email, subject, message;

    public JavaMailApi(Context context, String email, String subject, String message) {
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    ProgressDialog progressDialog;

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Sending email");
        progressDialog.setTitle("Please wait");
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Util.EMAIL, Util.PASSWORD);
            }
        });

        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom(new javax.mail.internet.InternetAddress(Util.EMAIL));
            mimeMessage.addRecipient(javax.mail.Message.RecipientType.TO, new javax.mail.internet.InternetAddress(email));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);
            javax.mail.Transport.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        progressDialog.dismiss();
        startAlertDialog();
        super.onPostExecute(unused);
    }

    private void startAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.output_layout, null);
        dialog.setView(view);

        final AlertDialog alertDialog = dialog.create();
        dialog.setCancelable(false);

        Button closeButton = view.findViewById(R.id.btn_emailClose);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}
