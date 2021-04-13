package com.example.erbroker.Activites;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import com.example.erbroker.R;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }
    public void goLogin(View view)
    {
        startFadingAnimation(this, view);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    // registering new user
    // register start with choosing broker name
    public void goRegisterBrokerActivity(View view)
    {
        startFadingAnimation(this, view);
        Intent intent = new Intent(this, RegisterBroker.class);
        startActivity(intent);
    }

    // start streaming video information about application
    public void goAbout(View view)
    {
        startFadingAnimation(this, view);
        Toast.makeText(MainActivity.this, "Loading Video Tutorial be patient", Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override public void run()
            {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        }, 1000);
    }

    // doing fade out animation on button in context
    public static void startFadingAnimation(Context context, View view)
    {
        Animation animation;
        animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        view.startAnimation(animation);
    }
}