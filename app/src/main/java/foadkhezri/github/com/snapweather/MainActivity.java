package foadkhezri.github.com.snapweather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    ImageView background;
    Button searchWeather;
    TextView result;
    String name;
    Toast toast;
    StringBuilder weatherInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = findViewById(R.id.cityName);
        searchWeather = findViewById(R.id.searchWeather);
        result = findViewById(R.id.result);
        background = findViewById(R.id.background);
        Glide
                .with(MainActivity.this)
                .load("https://images.unsplash.com/photo-1533293622232-d5074edb980f?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=5f9646aec313cc903a3be048f426d663&auto=format&fit=crop&w=1868&q=80")
                .into(background);
    }

    @SuppressLint("StaticFieldLeak")
    public class DownloadWeather extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result.append(current);
                    data = reader.read();
                }
            }catch (Exception e) {
                toast = Toast.makeText(MainActivity.this, "connection error", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                e.printStackTrace();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                JSONArray arr = new JSONArray(weatherInfo);
                for (int i = 0; i < arr.length(); i ++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    result.setText(String.format("%s", jsonPart.getString("description")));
                }
            } catch (JSONException e) {
                toast = Toast.makeText(MainActivity.this, "connection error", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void getWeather(View view) {

        try {
            if (!cityName.getText().toString().equals("")) {
                name = String.valueOf(cityName.getText());
                DownloadWeather task = new DownloadWeather();
                task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + cityName.getText().toString() + "&appid=218c95cc487f6c2f298a50ab75e4cef1");
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert mgr != null;
                mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);
            }
            else {
                toast = Toast.makeText(MainActivity.this, "please enter the city name", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

        }catch (Exception e) {
            toast = Toast.makeText(MainActivity.this, "connection error", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            e.printStackTrace();
        }
    }
}