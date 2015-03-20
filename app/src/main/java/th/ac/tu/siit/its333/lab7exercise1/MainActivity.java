package th.ac.tu.siit.its333.lab7exercise1;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends ActionBarActivity {
    int lastBtn;
    long time_start = 0;
    long time_end = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        WeatherTask w = new WeatherTask();
        w.execute("http://ict.siit.tu.ac.th/~cholwich/bangkok.json", "Bangkok Weather");
        lastBtn = R.id.btBangkok;
        time_start = System.currentTimeMillis();
    }

    public void buttonClicked(View v) {
        int id = v.getId();
        WeatherTask w = new WeatherTask();
        /*switch (id) {
            case R.id.btBangkok:
                w.execute("http://ict.siit.tu.ac.th/~cholwich/bangkok.json", "Bangkok Weather");
                break;
            case R.id.btNon:
                w.execute("http://ict.siit.tu.ac.th/~cholwich/nontaburi.json", "Nonthaburi Weather");
                break;
            case R.id.btPathum:
                w.execute("http://ict.siit.tu.ac.th/~cholwich/pathumthani.json", "Pathum Weather");
                break;
        }*/
        if(id == R.id.btBangkok){
            if(lastBtn!=R.id.btBangkok && time_end-time_start <= 60000){
                w.execute("http://ict.siit.tu.ac.th/~cholwich/bangkok.json", "Bangkok Weather");
                lastBtn = R.id.btBangkok;
                time_start = System.currentTimeMillis();
            }
            else{
                Toast t = Toast.makeText(this.getApplicationContext(),
                        "Wait 1 Minute",
                        Toast.LENGTH_SHORT);
                t.show();
            }
        }
        else if(id == R.id.btNon){
            if(lastBtn!=R.id.btNon && time_end-time_start <= 60000){
                w.execute("http://ict.siit.tu.ac.th/~cholwich/nonthaburi.json", "Nonthaburi Weather");
                lastBtn = R.id.btNon;
                time_start = System.currentTimeMillis();
            }
            else{
                Toast t = Toast.makeText(this.getApplicationContext(),
                        "Wait 1 Minute",
                        Toast.LENGTH_SHORT);
                t.show();
            }
        }
        else if(id == R.id.btPathum){
            if(lastBtn!=R.id.btPathum && time_end-time_start <= 60000){
                w.execute("http://ict.siit.tu.ac.th/~cholwich/pathumthani.json", "Pathumthani Weather");
                lastBtn = R.id.btPathum;
                time_start = System.currentTimeMillis();
            }
            else{
                Toast t = Toast.makeText(this.getApplicationContext(),
                        "Wait 1 Minute",
                        Toast.LENGTH_SHORT);
                t.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class WeatherTask extends AsyncTask<String, Void, Boolean> {
        String errorMsg = "";
        ProgressDialog pDialog;
        String title;

        double windSpeed;
        double temp;
        double tempmin;
        double tempmax;
        int humidity;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading weather data ...");
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            BufferedReader reader;
            StringBuilder buffer = new StringBuilder();
            String line;
            try {
                title = params[1];
                URL u = new URL(params[0]);
                HttpURLConnection h = (HttpURLConnection)u.openConnection();
                h.setRequestMethod("GET");
                h.setDoInput(true);
                h.connect();

                int response = h.getResponseCode();
                if (response == 200) {
                    reader = new BufferedReader(new InputStreamReader(h.getInputStream()));
                    while((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    //Start parsing JSON
                    JSONObject jWeather = new JSONObject(buffer.toString());
                    JSONObject jWind = jWeather.getJSONObject("wind");
                    windSpeed = jWind.getDouble("speed");
                    JSONObject jTemp = jWeather.getJSONObject("main");
                    temp = jTemp.getDouble("temp")-273.15;
                    tempmin = jTemp.getDouble("tempmin")-273.15;
                    tempmax = jTemp.getDouble("tempmax")-273.15;
                    humidity = jTemp.getInt("humidity");
                    errorMsg = "";
                    return true;
                }
                else {
                    errorMsg = "HTTP Error";
                }
            } catch (MalformedURLException e) {
                Log.e("WeatherTask", "URL Error");
                errorMsg = "URL Error";
            } catch (IOException e) {
                Log.e("WeatherTask", "I/O Error");
                errorMsg = "I/O Error";
            } catch (JSONException e) {
                Log.e("WeatherTask", "JSON Error");
                errorMsg = "JSON Error";
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            TextView tvTitle, tvWeather, tvWind, tvTemp, tvHumid;
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            tvTitle = (TextView)findViewById(R.id.tvTitle);
            tvWeather = (TextView)findViewById(R.id.tvWeather);
            tvWind = (TextView)findViewById(R.id.tvWind);
            tvTemp = (TextView)findViewById(R.id.tvTemp);
            tvHumid = (TextView)findViewById(R.id.tvHumid);

            if (result) {
                tvTitle.setText(title);
                tvWind.setText(String.format("%.1f", windSpeed));
                tvTemp.setText(String.format("%.1f", temp)+ "(max = " +String.format("%.1f", tempmax)+ ", min = "+ String.format("%.1f", tempmin)+")");
                tvHumid.setText(Integer.toString(humidity)+"%");
            }
            else {
                tvTitle.setText(errorMsg);
                tvWeather.setText("");
                tvWind.setText("");
            }
        }
    }
}
