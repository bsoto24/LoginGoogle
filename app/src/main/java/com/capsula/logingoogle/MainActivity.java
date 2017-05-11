package com.capsula.logingoogle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private LinearLayout lyProfile;
    private Button btnLogout;
    private TextView tvName, tvEmail;
    private ImageView imgPhoto;
    private SignInButton btnLogin;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lyProfile = (LinearLayout) findViewById(R.id.ly_profile);
        btnLogout = (Button) findViewById(R.id.btn_logout);
        tvEmail = (TextView) findViewById(R.id.tv_email);
        tvName = (TextView) findViewById(R.id.tv_name);
        imgPhoto = (ImageView) findViewById(R.id.img_photo);
        btnLogin = (SignInButton) findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(this);
        btnLogout.setOnClickListener(this);

        lyProfile.setVisibility(View.GONE);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                logIn();
                break;
            case R.id.btn_logout:
                logOut();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void logIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);

    }

    private void logOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });
    }

    private void handleResult(GoogleSignInResult result) {
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            tvName.setText(account.getDisplayName());
            tvEmail.setText(account.getEmail());
            if(account.getPhotoUrl()!=null)Picasso.with(this).load(account.getPhotoUrl().toString()).into(imgPhoto);
            updateUI(true);
        }else{
            updateUI(false);
        }
    }

    private void updateUI(boolean isLogin) {
        if(isLogin){
            lyProfile.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
        }else{
            lyProfile.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_CODE){

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }
}
