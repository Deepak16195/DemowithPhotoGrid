package com.PhotoGridMaker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.PhotoGridMaker.Data.SharePrefarence;
import com.PhotoGridMaker.Softsimplicity.MainActivity;
import com.PhotoGridMaker.Softsimplicity.R;
import com.PhotoGridMaker.utils.ConnectivityReceiver;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 21;
    private SignInButton btnSignIn;
    private static final String TAG = "LoginActivity";
    protected FirebaseUser mfirebaseuser;
    protected GoogleApiClient mGoogleApiClient;
    protected FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;
    private GoogleSignInOptions mGso;
    private ConnectivityReceiver connectivityReceiver;
    Boolean isInternetPresent = false;
    String personName = "deepak", email = "deepak", ProfileUrl = "deepak";
    int Userid;
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        Userid =SharePrefarence.getmInstance(this).getUserId();
        if (!(Userid==0)) {
            i = new Intent(this, MainActivity.class);
            startActivity(i);
        }

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
//            startActivity(new Intent(this, RegisterActivity.class));
            mfirebaseuser = mAuth.getCurrentUser();
        }
        mGso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.GoogleId))
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, LoginActivity.this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGso)
                .build();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callLoginWithGoogle();
            }
        });

    }


    public void callLoginWithGoogle() {
        connectivityReceiver = new ConnectivityReceiver(getApplicationContext());
        // Initialize SDK before setContentView(Layout ID)
        isInternetPresent = connectivityReceiver.isConnectingToInternet();
        if (isInternetPresent) {
            signIn();
        } else {

            Toast.makeText(LoginActivity.this, "No Internet Connection, Please connect to Internet.", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }

    private void initView() {
        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
    }


    private void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Log.e(TAG, "signIn: sign is creating");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: is being created");

        if (resultCode == Activity.RESULT_OK) {

            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {

                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    GoogleSignInAccount account = result.getSignInAccount();
                    handleSignInResult(account);

                }

            }
        }
    }

    private void handleSignInResult(GoogleSignInAccount account) {
        showProgressDialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Signed in successfully, show authenticated UI.
                    Log.e(TAG, "display name: ");
                    personName = mAuth.getCurrentUser().getDisplayName();
                    email = mAuth.getCurrentUser().getEmail();
                    ProfileUrl = String.valueOf(mAuth.getCurrentUser().getPhotoUrl());
                    SharePrefarence.getmInstance(getApplicationContext()).SaveData(1, personName, email, ProfileUrl);
                    dismissProgressDialog();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            }
        });
    }

    void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(false);
        }
        mProgressDialog.show();
    }

    protected void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();

        }
    }


}
