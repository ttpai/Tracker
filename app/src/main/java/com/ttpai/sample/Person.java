package com.ttpai.sample;

import android.content.Intent;
import android.view.View;

/**
 * FileName: Person
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-20
 * Description:
 */
public class Person {
    public int age;
    public String name;


    public void click(View v){
        v.getContext().startActivity(new Intent(v.getContext(), BActivity.class));

    }
    public Person(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
