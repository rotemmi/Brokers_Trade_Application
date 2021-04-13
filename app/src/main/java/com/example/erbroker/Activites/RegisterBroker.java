package com.example.erbroker.Activites;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.erbroker.R;

// manage the process with brokers
// it can be expanded later with real time connection
public class RegisterBroker extends AppCompatActivity
{
    private final String BROKER_KEY="broker_name";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_broker);
    }
    public void goRegisterActivity(View v)
    {
        Intent intent = new Intent(this, RegisterActivity.class);
        String brokerName = ((Button)v).getText().toString();
        intent.putExtra(BROKER_KEY,brokerName);
        startActivity(intent);

    }
}