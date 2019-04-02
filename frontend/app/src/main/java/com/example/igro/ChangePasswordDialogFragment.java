package com.example.igro;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.igro.Controller.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.android.volley.VolleyLog.TAG;


public class ChangePasswordDialogFragment extends AppCompatDialogFragment {

    EditText oldPasswordEditText;
    EditText newPasswordEditText;
    EditText confirmNewPasswordEditText;
    String oldPassword;
    String newPassword;
    String confirmNewPassword;
    Button changePasswordButton;
    private Helper helper=new Helper(getActivity(), FirebaseAuth.getInstance());

    private FirebaseAuth mAuth; //authentication instance
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_change_password, null);

        builder.setView(view);
        //initialization
        oldPasswordEditText = view.findViewById(R.id.oldPasswordEditText);
        newPasswordEditText = view.findViewById(R.id.newPasswordEditText);
        confirmNewPasswordEditText = view.findViewById(R.id.confirmNewPasswordEditText);
        changePasswordButton=view.findViewById(R.id.changePasswordButton);

        builder.setTitle("Change Password");
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPassword = oldPasswordEditText.getText().toString();
                newPassword = newPasswordEditText.getText().toString();
                confirmNewPassword = newPasswordEditText.getText().toString();
                if (!TextUtils.isEmpty(oldPassword) && !TextUtils.isEmpty(newPassword)
                        && !TextUtils.isEmpty(confirmNewPassword)) {
                    validateCurrentPassword(oldPassword, newPassword, confirmNewPassword);


                   /* helper.signout();
                    helper.goToActivity(LoginActivity.class);*/

                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "Please fill in all the fields!!!"
                            , Toast.LENGTH_LONG).show();
                }
            }
        });
        /*builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                oldPassword = oldPasswordEditText.getText().toString();
                newPassword = newPasswordEditText.getText().toString();
                confirmNewPassword = confirmNewPasswordEditText.getText().toString();
                validateCurrentPassword(oldPassword, newPassword, confirmNewPassword);
            }
        });*/
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });


        // Create the AlertDialog object and return it
        return builder.create();

    }

    //check if current password is valid
    private void validateCurrentPassword(String oldPassword,  String newPassword, final String confirmNewPassword) {

            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
            user.reauthenticate(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        //authentication successful
                        Log.d(TAG, "Authentication validated to change password");

                            user.updatePassword(confirmNewPassword);
                        Toast.makeText(getActivity().getApplicationContext(), "Password Changed Successfully"
                                , Toast.LENGTH_LONG).show();
                        }
                     else {
                        //authentication failed
                        Log.w(TAG, "Authentication failed in order to change password", task.getException());
                        Toast.makeText(getActivity(), "The OLD PASSWORD IS WRONG!!!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });



    }


}



