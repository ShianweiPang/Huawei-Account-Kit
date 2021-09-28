package com.example.myapplogin234;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnAuthModeSignIn, btnIdTokenModeSignIn, btnSilentlySignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAuthModeSignIn = findViewById(R.id.bttnAuthMode);
        btnIdTokenModeSignIn = findViewById(R.id.bttnidToken);
        btnSilentlySignIn = findViewById(R.id.bttnSilently);

        btnAuthModeSignIn.setOnClickListener(this);
        btnSilentlySignIn.setOnClickListener(this);
        btnIdTokenModeSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bttnAuthMode:
                AccountAuthParams authParams1 = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                        .setAuthorizationCode().createParams();
                AccountAuthService service1 = AccountAuthManager.getService(MainActivity.this, authParams1);
                startActivityForResult(service1.getSignInIntent(), 8888);
                break;
            case R.id.bttnidToken:
                AccountAuthParams authParams2 = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                        .setIdToken().createParams();
                AccountAuthService service2 = AccountAuthManager.getService(MainActivity.this, authParams2);
                startActivityForResult(service2.getSignInIntent(), 2222);
                break;
            case R.id.bttnSilently:
                AccountAuthParams authParams3 = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                        .createParams();
                AccountAuthService service = AccountAuthManager.getService(MainActivity.this, authParams3);
                Task<AuthAccount> task = service.silentSignIn();

                task.addOnSuccessListener(new OnSuccessListener<AuthAccount>() {
                    @Override
                    public void onSuccess(AuthAccount authAccount) {
                        // Obtain the user's ID information.
                        Log.i("MainActivity", "displayName:" + authAccount.getDisplayName());
                        // Obtain the ID type (0: HUAWEI ID; 1: AppTouch ID).
                        Log.i("MainActivity", "accountFlag:" + authAccount.getAccountFlag());
                    }
                });
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // The sign-in failed. Try to sign in explicitly using getSignInIntent().
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            Log.i("MainActivity", "sign failed status:" + apiException.getStatusCode());
                        }
                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 8888) {
            Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
            if (authAccountTask.isSuccessful()) {
                // The sign-in is successful, and the user's ID information and authorization code are obtained.
                AuthAccount authAccount = authAccountTask.getResult();
                Log.i("MainActivity", "serverAuthCode:" + authAccount.getAuthorizationCode());
            } else {
                // The sign-in failed.
                Log.e("MainActivity", "sign in failed:" + ((ApiException) authAccountTask.getException()).getStatusCode());
            }
        }else if (requestCode == 2222) {
            Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
            if (authAccountTask.isSuccessful()) {
                // The sign-in is successful, and the user's ID information and ID token are obtained.
                AuthAccount authAccount = authAccountTask.getResult();
                Log.i("MainActivity", "idToken:" + authAccount.getIdToken());
                // Obtain the ID type (0: HUAWEI ID; 1: AppTouch ID).
                Log.i("MainActivity", "accountFlag:" + authAccount.getAccountFlag());
            } else {
                // The sign-in failed. No processing is required. Logs are recorded for fault locating.
                Log.e("MainActivity", "sign in failed : " +((ApiException) authAccountTask.getException()).getStatusCode());
            }
        }
    }
}