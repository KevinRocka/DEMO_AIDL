package com.rocka.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.rocka.aidl.Animal;
import com.rocka.aidl.AnimalManager;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Rocka
 * @version: 1.0
 * @time:2018/7/17
 */
public class AIDLServer extends Service {

    private List<Animal> animals = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return manager;
    }

    public final AnimalManager.Stub manager = new AnimalManager.Stub() {
        @Override
        public void addAnimal(Animal animal) {
            synchronized (this) {
                if (animals == null) {
                    animals = new ArrayList<>();
                }

                if (animal == null) {
                    animal = new Animal();
                }

                if (!animals.contains(animal)) {
                    animals.add(animal);
                }
                Log.d("TAG", "AnimalManager.Stub - addAnimal" + animals.toString());
            }
        }

        @Override
        public List<Animal> getAnimals() {
            synchronized (this) {
                if (animals != null) {
                    return animals;
                }
                return new ArrayList<>();
            }
        }
    };

    @Subscribe
    public void onEventMainThread(Animal animal) {
        try {
            if (animal != null) {
                manager.addAnimal(animal);
            }
            EventBus.getDefault().post(manager.getAnimals());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
