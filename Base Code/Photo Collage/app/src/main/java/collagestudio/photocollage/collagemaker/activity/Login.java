package collagestudio.photocollage.collagemaker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import collagestudio.photocollage.collagemaker.R;
import com.facebook.CallbackManager;

public class Login extends AppCompatActivity {
    private CallbackManager callbackManager;
    TextView skipBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        skipBtn = findViewById(R.id.tv_skip);
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,MainActivity.class));
            }
        });
//        LoginButton loginButton = findViewById(R.id.button2);
//        loginButton.setReadPermissions("email", "public_profile");
//        loginButton.registerCallback(callbackManager,
//                new FacebookCallback<LoginResult>() {
//
//                    @Override
//                    public void onSuccess(LoginResult loginResult) {
//                        Log.d("4747", "onSuccess: " + loginResult);
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        Log.d("4747", "onCancel: User cancelled sign-in");
//                    }
//
//                    @Override
//                    public void onError(FacebookException error) {
//                        Log.d("4747", "onError: " + error);
//                    }
//                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}