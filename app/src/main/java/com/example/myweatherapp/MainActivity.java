package com.example.myweatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText editTextCity;
    private Button buttonGetWeather;
    private TextView textViewCity, textViewDate, textViewTemp, textViewHumidity, textViewWind;
    private TextView textViewSunrise, textViewSunset;
    private ImageView imageViewWeather, imageViewSunriseIcon, imageViewSunsetIcon;
    private CardView weatherCard;
    private ConstraintLayout rootLayout;
    private TextView textViewWeatherCondition;

    private final String API_KEY = "YOUR API KEY";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextCity = findViewById(R.id.editTextCity);
        buttonGetWeather = findViewById(R.id.buttonGetWeather);
        imageViewWeather = findViewById(R.id.imageViewWeather);

        textViewCity = findViewById(R.id.textViewCity);
        textViewDate = findViewById(R.id.textViewDate);
        textViewTemp = findViewById(R.id.textViewTemp);
        textViewHumidity = findViewById(R.id.textViewHumidity);
        textViewWind = findViewById(R.id.textViewWind);
        textViewSunrise = findViewById(R.id.textViewSunrise);
        textViewSunset = findViewById(R.id.textViewSunset);

        imageViewSunriseIcon = findViewById(R.id.imageViewSunriseIcon);
        imageViewSunsetIcon = findViewById(R.id.imageViewSunsetIcon);
        textViewWeatherCondition = findViewById(R.id.textViewWeatherCondition);

        imageViewSunriseIcon.setImageResource(R.drawable.ic_sun);
        imageViewSunsetIcon.setImageResource(R.drawable.ic_sunset);

        weatherCard = findViewById(R.id.weatherCard);
        rootLayout = findViewById(R.id.rootLayout);

        weatherCard.setAlpha(0f);

        buttonGetWeather.setOnClickListener(v -> {
            String city = editTextCity.getText().toString().trim();

            if (city.isEmpty()) {
                Toast.makeText(MainActivity.this, "Lütfen şehir giriniz", Toast.LENGTH_SHORT).show();
                return;
            }

            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(editTextCity.getWindowToken(), 0);
            }

            new Thread(() -> fetchWeather(city)).start();
        });
    }

    private void fetchWeather(String city) {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" +
                city + "&appid=" + API_KEY + "&units=metric&lang=tr";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(7000);
            connection.setReadTimeout(7000);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                parseAndShowWeather(response.toString());
            } else {
                showError("Sunucudan veri alınamadı: " + responseCode);
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Hata: " + e.getMessage());
        }
    }

    private void parseAndShowWeather(String json) {
        try {
            JSONObject root = new JSONObject(json);

            String cityName = root.getString("name");

            JSONObject main = root.getJSONObject("main");
            double temp = main.getDouble("temp");
            int humidity = main.getInt("humidity");

            JSONObject wind = root.getJSONObject("wind");
            double windSpeed = wind.getDouble("speed");

            JSONObject sys = root.getJSONObject("sys");
            long sunriseTimestamp = sys.getLong("sunrise");
            long sunsetTimestamp = sys.getLong("sunset");

            String sunrise = new SimpleDateFormat("HH:mm").format(new Date(sunriseTimestamp * 1000));
            String sunset = new SimpleDateFormat("HH:mm").format(new Date(sunsetTimestamp * 1000));

            String dateToday = new SimpleDateFormat("d MMMM yyyy", new Locale("tr")).format(new Date());

            JSONObject weatherObj = root.getJSONArray("weather").getJSONObject(0);
            String weatherMain = weatherObj.getString("main");
            String weatherDescription = weatherObj.getString("description");

            DecimalFormat df = new DecimalFormat("##");
            String formattedTemp = df.format(temp);

            runOnUiThread(() -> {
                textViewCity.setText(cityName);
                textViewTemp.setText(formattedTemp + "°C");
                textViewHumidity.setText("Nem: %" + humidity);
                textViewWind.setText("Rüzgar: " + windSpeed + " m/s");
                textViewSunrise.setText("Gündoğumu: " + sunrise);
                textViewSunset.setText("Günbatımı: " + sunset);
                textViewDate.setText("Bugün, " + dateToday);

                String displayDescription;
                switch (weatherMain.toLowerCase(Locale.ROOT)) {
                    case "clear":
                        displayDescription = "Güneşli";
                        break;
                    case "clouds":
                        displayDescription = "Bulutlu";
                        break;
                    case "rain":
                        displayDescription = "Yağmurlu";
                        break;
                    case "snow":
                        displayDescription = "Karlı";
                        break;
                    case "thunderstorm":
                        displayDescription = "Fırtınalı";
                        break;
                    default:
                        displayDescription = weatherDescription.substring(0,1).toUpperCase() + weatherDescription.substring(1);
                        break;
                }
                textViewWeatherCondition.setText(displayDescription);

                switch (weatherMain.toLowerCase(Locale.ROOT)) {
                    case "rain":
                        weatherCard.setCardBackgroundColor(getResources().getColor(R.color.blue));
                        break;
                    case "snow":
                        weatherCard.setCardBackgroundColor(getResources().getColor(R.color.light_blue));
                        break;
                    case "clouds":
                        weatherCard.setCardBackgroundColor(getResources().getColor(R.color.dark_blue));
                        break;
                    case "clear":
                        weatherCard.setCardBackgroundColor(getResources().getColor(R.color.blue));
                        break;
                    case "thunderstorm":
                        weatherCard.setCardBackgroundColor(getResources().getColor(R.color.dark_gray));
                        break;
                    default:
                        weatherCard.setCardBackgroundColor(getResources().getColor(R.color.default_bg));
                        break;
                }

                switch (weatherMain.toLowerCase(Locale.ROOT)) {
                    case "rain":
                        imageViewWeather.setImageResource(R.drawable.rainy);
                        break;
                    case "snow":
                        imageViewWeather.setImageResource(R.drawable.snowy);
                        break;
                    case "clouds":
                        imageViewWeather.setImageResource(R.drawable.clouds);
                        break;
                    case "clear":
                        imageViewWeather.setImageResource(R.drawable.sun);
                        break;
                    case "thunderstorm":
                        imageViewWeather.setImageResource(R.drawable.thun);
                        break;
                    default:
                        imageViewWeather.setImageResource(R.drawable.sun);
                        break;
                }

                imageViewSunriseIcon.setImageResource(R.drawable.ic_sun);
                imageViewSunsetIcon.setImageResource(R.drawable.ic_sunset);

                weatherCard.setAlpha(0f);
                weatherCard.animate()
                        .alpha(1f)
                        .setDuration(500)
                        .setInterpolator(new DecelerateInterpolator())
                        .start();
            });

        } catch (JSONException e) {
            e.printStackTrace();
            showError("Veri ayrıştırma hatası");
        }
    }

    private void showError(final String message) {
        runOnUiThread(() -> {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            weatherCard.setAlpha(0f);
        });
    }
}
