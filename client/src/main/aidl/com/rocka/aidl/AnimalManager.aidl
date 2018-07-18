// AnimalManager.aidl
package com.rocka.aidl;

// Declare any non-default types here with import statements

//
import com.rocka.aidl.Animal;

interface AnimalManager {
    /**
     * 如果需要传参数，需要加上定向tag
     */
    void addAnimal(in Animal animal);

    List<Animal> getAnimals();
}
