package com.rocka.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * @author Rocka
 * @description 这是AIDL的一个客户端代码
 */
public class MainActivity extends AppCompatActivity {

    private AnimalManager animalManager;

    private boolean mConnection = false;

    private List<Animal> animals;

    private TextView output;

    private int age = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView client = findViewById(R.id.client_txt);
        client.setText("Client _Side");

        Button add = findViewById(R.id.client_add_btn);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });

        output = findViewById(R.id.output);
        output.setText("output : " + " none");
    }

    private void refreshOutput() {
        try {
            if (animalManager != null) {
                StringBuilder sb = new StringBuilder();
                for (Animal animal : animalManager.getAnimals()){
                    sb.append(animal.toString());
                    sb.append("\n");
                }
                output.setText(sb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void add() {
        if (!mConnection) {
            bindService();
            Toast.makeText(this, "当前与服务端未连接状态，尝试重连，请稍后再试", Toast.LENGTH_SHORT).show();
            return;
        }

        if (animalManager == null) {
            return;
        }

        try {
            age +=  1;
            Animal animal = new Animal();
            animal.setAge(age);
            animal.setName("Panda" + age);
            animalManager.addAnimal(animal);

            refreshOutput();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindService() {
        Intent intent = new Intent();
        intent.setAction("com.rocka.aidl");
        intent.setPackage("com.rocka.server");
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mConnection) {
            bindService();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mConnection) {
            unbindService(mServiceConnection);
            mConnection = false;
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("TAG", "Client serviceConnected  ...");
            mConnection = true;
            animalManager = AnimalManager.Stub.asInterface(iBinder);

            if (animalManager != null) {
                try {
                    animals = animalManager.getAnimals();
                    Log.d("TAG", "Client :" + animals.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("TAG", "Client serviceDisConnected  ...");
            mConnection = false;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        refreshOutput();
    }
}
