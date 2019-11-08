package com.example.mrrobot.concurrent.models;

public interface IDao<T> {

     void create(T t);
     T update(T t);
     T getById(String id);

}
