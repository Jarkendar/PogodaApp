package com.example.jarek.pogodaapp;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class PogodaActivity extends AppCompatActivity {

    private String adres;//adres grafiki
    private ProgressBar progressBar;
    private ImageView image;
    private TextView percent;
    private TextView dayBannertextView;
    private String filename;
    private String date;
    private String nameDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pogoda);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        //powiązanie zmiennych z odpowiednimi komponentami na aktywności
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        image = (ImageView)findViewById(R.id.image_forecast);
        percent = (TextView)findViewById(R.id.text_Percentage);
        dayBannertextView = (TextView)findViewById(R.id.textView2);

        //stworzenie obiektu Bundle i pobranie adresu wysłanej przy tworzeniu aktywności
        Bundle b = getIntent().getExtras();
        adres = b.getString("adres");
        filename = b.getString("filename");
        int idate = b.getInt("date");
        date = " " + idate%100 + ":";
        if ((idate/100)%100 < 10){
            date = date.concat("0" + (idate/100)%100 + ":");
        }else {
            date = date.concat((idate/100)%100 + ":");
        }
        date = date.concat(idate/10000 + "");
        if (adres.contains("00&row=409&col=181&lang=pl")){
            nameDate = "srem";
        }else if (adres.contains("00&row=400&col=180&lang=pl")){
            nameDate = "poznan";
        }
        DownloadForecast downloadForecast = new DownloadForecast();//stworzenie nowego wątku
        downloadForecast.execute(adres);//rozpoczącie wykonywania stworzonego wątku
    }

    public void endClick(View view) {
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.scale_click);
        //opis w aktywności głównej funckja buttonClick
        view.startAnimation(animation);
        finish();
    }

    public void imageClick(View view) {//odświerzenie grafiki
        //opis w funckjach onCreate (tej aktywności) i w funckji buttonClick aktywności głównej
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.scale_click);
        view.startAnimation(animation);
        DownloadForecast downloadForecast = new DownloadForecast();
        downloadForecast.execute(adres);
    }

    private class DownloadForecast extends AsyncTask <String, Void, Boolean>{//klasa nowego wątku
        //String = parametry wejsciowe, Void = parametry wyjściowe, Boolean = parametr przekazywany przez
        //funkcję doInBackground do onPostExecute
        private Bitmap bitmap;
        @Override
        protected void onPreExecute() {//rozpoczącie
            progressBar.setVisibility(View.VISIBLE);//włączenie widoczności komponentowi ProgressBar
            percent.setText(R.string.download_process);//ustawienie stanu, informacja dla użytkownika
            super.onPreExecute();//konieczne wywołanie funkcji przodka
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {//zakończenie
            String sharedName = "dateLastWeather";
            SharedPreferences sharedPreferences = getSharedPreferences(sharedName, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String weather = getString(R.string.weather_on);
            if (aBoolean) {//jeśli nie wystąpił błąd
                Canvas canvas = new Canvas(bitmap);//twórz obiekt Canvas - rysujący
                Paint paint = new Paint();//twórz obiekt Paint - ze stylami i kolorami
                int width = bitmap.getWidth(), height = bitmap.getHeight();//wyciągnięcie rozmiaru grafiki
                paint.setColor(Color.WHITE);//ustawienie koloru na biały
                paint.setStyle(Paint.Style.STROKE);//ustawienie stylu na kreskę
                canvas.drawRect(0, 0, width - 1, height - 1, paint);//narysowanie obwódki grafiki
                image.setImageBitmap(bitmap);//ustawienie grafiki
                percent.setText(R.string.download_end);//ustawienie stanu, informacja dla użytkownika
                dayBannertextView.setText(weather.concat(date));
                editor.putString(nameDate,date);
            } else {//jeśli wystąpił błąd
                percent.setText(R.string.download_error);//ustawienie stanu, informacja dla użytkownika
                String sdate = sharedPreferences.getString(nameDate," brak prognozy pogody");
                dayBannertextView.setText(weather.concat(sdate));
                try {
                    FileInputStream inputStream = getApplicationContext().openFileInput(filename);//strumień wejściowy pliku
                    bitmap = BitmapFactory.decodeStream(inputStream);//dekodowanie bitmapy
                    image.setImageBitmap(bitmap);//ustawienie bitmapy
                    inputStream.close();//zamknięcie strumienia
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            editor.apply();
            progressBar.setVisibility(View.INVISIBLE);//ustawnie widoczności komponentu ProgressBar na niewidzialny
            super.onPostExecute(aBoolean);//konieczne wywołanie funkcji przodka
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            URL url;
            InputStream inputStream;
            boolean ret = true;//status powodzenia
            try {
            //obraz pogody pokazuje się dopiero po 6 rano
                url = new URL(strings[0]);//ustawienie parametru URL, przez pobranie parametru z wywołania
                inputStream = url.openStream();//ustawienie strumienia wejściowego
                Bitmap temp = BitmapFactory.decodeStream(inputStream);//ściągnięcie i zdekodowanie strumienia
                bitmap = temp.copy(Bitmap.Config.ARGB_8888, true);//przekopiowanie grafiki do obiektu docelowego z odpowiednim kodowaniem
                FileOutputStream outputStream = openFileOutput(filename, MODE_PRIVATE);//strumien zapisu
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);//kompresja bitmapy do pliku png
                outputStream.close();//zamknięcie strumienia
            }catch (Exception e){//w razie wystąpienia błędu
                ret = false;
            }
            return ret;//zwrócenie stanu powodzenia
        }
    }
}
