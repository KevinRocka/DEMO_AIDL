package com.rocka.server;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rocka.aidl.Animal;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * @author Rocka
 * @description 跨进程通信，进程可以是任意一种形式，在Server端添加Lion和在Client添加tiger都是共同维护一个List<Animal></>
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;

    private boolean connected = false;

    private TextView server;

    private Button connectBtn;

    private Button addBtn;

    private TextView output;

    private Intent intent;

    private int age = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        EventBus.getDefault().register(this);

        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        server = findViewById(R.id.server_txt);
        output = findViewById(R.id.output);
        connectBtn = findViewById(R.id.server_connect);
        addBtn = findViewById(R.id.add);
        connectBtn.setOnClickListener(this);
        addBtn.setOnClickListener(this);
    }

    private void initData() {
        server.setText("Server _Side");
        connectBtn.setText("连接");
        intent = new Intent(MainActivity.this, AIDLServer.class);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add:
                addClick();
                break;
            case R.id.server_connect:
                connectClick();
                break;
            default:
                break;
        }
    }

    private void addClick() {
        age = age + 1;
        Animal animal = new Animal();
        animal.setAge(age);
        animal.setName("Lion" + age);
        EventBus.getDefault().post(animal);
    }


    private void refreshOutput(List<Animal> animals) {
        StringBuilder sb = new StringBuilder();
        for (Animal animal : animals) {
            sb.append(animal.toString());
            sb.append("\n");
        }
        output.setText(sb);
    }

    private void connectClick() {
        if (!connected) {
            connected = true;
            connectBtn.setText("已连接");
            Toast.makeText(context, "已经启动服务", Toast.LENGTH_LONG).show();
            startService(intent);
        } else {
            connected = false;
            connectBtn.setText("连接");
            Toast.makeText(context, "已经关闭服务", Toast.LENGTH_LONG).show();
            stopService(intent);
        }
    }

    @Subscribe
    public void onEventMainThread(List<Animal> animals) {
        if (animals != null && animals.size() > 0) {
            refreshOutput(animals);
        }
    }

}
