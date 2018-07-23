// AnimalManager.aidl
package com.rocka.aidl;

// Declare any non-default types here with import statements

//
import com.rocka.aidl.Animal;

interface AnimalManager {
    /**
      * 除了基本数据类型，其他类型的参数都需要标记方向类型，in(输入)，out(输出)，inout(输入输出)
      */
    void addAnimal(in Animal animal);

    List<Animal> getAnimals();
}
