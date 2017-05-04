package com.example.jarek.pogodaapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LauncherActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        Thread thread = new Thread() {
            public void run(){//tworzenie nowego wątku z metodą run
                try{
                    sleep(5000);//odczekanie 5s
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    Intent intent = new Intent(".MainActivity");
                    startActivity(intent);//wywołanie nowego okna
                }
            }
        };
        thread.start();//rozpoczęcie pracy wątku odliczania
    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();//zamknięcie activity po jego zniknięciu
    }
}
