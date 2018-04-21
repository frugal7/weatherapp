package com.weeia.android.pogoda;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView output;
    EditText input;
    String link;


    //http://api.openweathermap.org/data/2.5/weather?q=Kutno&appid=682ecd3b5afca29ff4e66ba80c30dc16


    public class DownloadTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... params) {
            URL myUrl;

            HttpURLConnection myConnection;

            String result = "";

            try {
                myUrl = new URL(params[0]);

                myConnection = (HttpURLConnection)myUrl.openConnection();

                InputStream in = myConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1)
                {
                    char c = (char)data;

                    result += c;

                    data = reader.read();
                }
                return result;


            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            try
            {
                JSONObject myJSON = new JSONObject(s);      //tutaj mam całego stringa.

                /********** temperatura ***********/
                String temp = myJSON.getString("main");
                JSONObject tempJSON = new JSONObject(temp);
                String temperature = tempJSON.getString("temp");
                double t = Double.parseDouble(temperature);
                double c = t - 273.15;
                temp = Double.toString(c);

                /********** pogoda ***********/

                String weather = myJSON.getString("weather");
                JSONArray jsonArray = new JSONArray(weather);
                JSONObject weatherJSON = jsonArray.getJSONObject(0);
                String description = weatherJSON.getString("description");

                output.setText("Temperatura w tej miejscowości wynosi " + temp + "°C\nOpis pogody: " + description);

            }
            catch (Exception e)
            {
                e.printStackTrace();
                output.setText("Nie udało się pobrać pogody dla tego miejsca!");
            }
        }
    }



    public void click(View view)
    {
        String city = input.getText().toString();


        /************** ukryj klawiature ******************/
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(this.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);


        link = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=682ecd3b5afca29ff4e66ba80c30dc16";
        DownloadTask myTask = new DownloadTask();
        myTask.execute(link);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        output = (TextView)findViewById(R.id.output);
        input = (EditText)findViewById(R.id.input);
    }
}
