package com.example.jarek.pogodaapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    /* Program pogodowy. Stworzony dla własnych potrzeb, w celu
     * skrócenia czasu dostępu do tychże informacji (wykorzystywanie
      * przeglądarki trwa dłużej). Program pobiera obraz progrozy pogody
      * wprowadzonych wcześniej, na stałe, miejscowości. Po wybraniu
      * miasta, włączana jest druga aktywność i pobierany jest obraz.
      * Po kliknięciu obrazu program odświerza grafikę. Na głównej
      * aktywności zastosowałem zamiast przycisków grafiki, odpowiednio
      * uprzednio przygotowane (moim zdaniem wygląda lepiej).*/

    private static String sremURL = "";
    private static String poznanURL = "";
    private int idate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        int year, month, day;
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String smonth, sday;
        if (month < 10) {
            smonth = "0"+month;
        }else {
            smonth = ""+month;
        }
        if (hour < 7){
            day--;
        }
        if (day < 10) {
            sday = "0"+day;
        }else {
            sday = ""+day;
        }
        idate = year*10000+month*100+day;
        String date = Integer.toString(year) + smonth + sday;
        String prefix = "http://www.meteo.pl/um/metco/mgram_pict.php?ntype=0u&fdate=";
        String sufixsrem = "00&row=409&col=181&lang=pl";
        sremURL = prefix + date + sufixsrem;
        String sufixpoznan = "00&row=400&col=180&lang=pl";
        poznanURL = prefix + date + sufixpoznan;
    }

    public void buttonClick (View v){//kliknięcie na przycisk
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.scale_click);//załadowanie animacji
        //zmniejszenia obrazka po kliknięciu, daje użytkownikowi informacje zwrotną, że aplikacja
        //zarejestrowała naduszenie na ekran
        v.startAnimation(animation);//włączenie animacji
        int ID = v.getId();//pobranie ID klikniętego komponentu
        switch (ID){
            case R.id.image_Srem_button:{//wybrano Śrem
                Intent intent = new Intent(this, PogodaActivity.class);
                intent.putExtra("adres", sremURL);//dołączenie adresu grafiki
                intent.putExtra("filename", "Srem");
                intent.putExtra("date", idate);
                startActivity(intent);
                break;
            }
            case R.id.image_Poznan_button:{//wybrano Poznań
                Intent intent = new Intent(this, PogodaActivity.class);
                intent.putExtra("adres", poznanURL);//dołączenie adresu grafiki
                intent.putExtra("filename", "Poznan");
                intent.putExtra("date", idate);
                startActivity(intent);
                break;
            }
        }
    }

    public void endClick (View v){
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.scale_click);//opis w funkcji buttonClick
        v.startAnimation(animation);
        finish();
    }
}

//Jarosław Skrzypczak autor pomysłu, grafik i kodu. 8 sierpnia 2016