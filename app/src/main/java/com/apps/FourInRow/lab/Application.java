package com.apps.FourInRow.lab;

import android.content.Context;

/**
 * Главный класс приложения. Создан для хранения контекста
 * и получению доступа к нему в любой точке программы.
 */
public class Application extends android.app.Application
{
    public static Context INSTANCE; //Сам контекст

    @Override
    public void onCreate()
    {
        super.onCreate();
        INSTANCE = this;
    }
}