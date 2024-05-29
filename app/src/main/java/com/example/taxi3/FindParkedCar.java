package com.example.taxi3;

import static android.icu.lang.UCharacter.toLowerCase;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class FindParkedCar extends AppCompatActivity {

    private LinearLayout carsLayout;
    private DrawerLayout drawerLayout;
    private Spinner sortSpinner;
    private Spinner brandSpinner;
    private EditText idMin, idMax, speedMin, speedMax, priceMin, priceMax;

    private String selectedBrand = "";
    private int minId = -1;
    private int maxId = -1;
    private int minSpeed = -1;
    private int maxSpeed = -1;
    private double minPrice = -1;
    private double maxPrice = -1;
    private String currentSortOrder = "car_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_parked_car);

        carsLayout = findViewById(R.id.carsLayout);
        drawerLayout = findViewById(R.id.drawerLayout);
        sortSpinner = findViewById(R.id.sortSpinner);
        brandSpinner = findViewById(R.id.brandSpinner);
        idMin = findViewById(R.id.idMin);
        idMax = findViewById(R.id.idMax);
        speedMin = findViewById(R.id.speedMin);
        speedMax = findViewById(R.id.speedMax);
        priceMin = findViewById(R.id.priceMin);
        priceMax = findViewById(R.id.priceMax);

        // Initialize the brand spinner
        ArrayAdapter<CharSequence> brandAdapter = ArrayAdapter.createFromResource(this,
                R.array.brands_array, android.R.layout.simple_spinner_item);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandSpinner.setAdapter(brandAdapter);

        brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBrand = position == 0 ? "" : (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Initialize the sort spinner
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        currentSortOrder = "car_id";
                        break;
                    case 1:
                        currentSortOrder = "brand";
                        break;
                    case 2:
                        currentSortOrder = "price";
                        break;
                    case 3:
                        currentSortOrder = "speed";
                        break;
                    case 4:
                        currentSortOrder = "year";
                        break;
                }
                // Fetch data again with new sort order
                new ConnectDBTask().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Set on click listener for filter button
        Button filterButton = findViewById(R.id.filterButton);
        filterButton.setOnClickListener(v -> drawerLayout.openDrawer(findViewById(R.id.filterDrawer)));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.containerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void OnClick(View view) {
        new ConnectDBTask().execute();
    }

    public void applyFilters(View view) {
        minId = parseInteger(idMin.getText().toString(), -1);
        maxId = parseInteger(idMax.getText().toString(), -1);
        minSpeed = parseInteger(speedMin.getText().toString(), -1);
        maxSpeed = parseInteger(speedMax.getText().toString(), -1);
        minPrice = parseDouble(priceMin.getText().toString(), -1);
        maxPrice = parseDouble(priceMax.getText().toString(), -1);

        drawerLayout.closeDrawer(findViewById(R.id.filterDrawer));

        new ConnectDBTask().execute();
    }

    private int parseInteger(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private double parseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private class ConnectDBTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder result = new StringBuilder();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(
                        "jdbc:mysql://192.168.1.104:3306/Taxi",
                        "test",
                        ""
                );

                StringBuilder query = new StringBuilder("SELECT * FROM cars WHERE 1=1");

                // Apply filters
                if (!selectedBrand.isEmpty()) {
                    query.append(" AND brand = '").append(selectedBrand).append("'");
                }
                if (minId != -1) {
                    query.append(" AND car_id >= ").append(minId);
                }
                if (maxId != -1) {
                    query.append(" AND car_id <= ").append(maxId);
                }
                if (minSpeed != -1) {
                    query.append(" AND speed >= ").append(minSpeed);
                }
                if (maxSpeed != -1) {
                    query.append(" AND speed <= ").append(maxSpeed);
                }
                if (minPrice != -1) {
                    query.append(" AND price >= ").append(minPrice);
                }
                if (maxPrice != -1) {
                    query.append(" AND price <= ").append(maxPrice);
                }

                // Add sorting
                query.append(" ORDER BY ").append(currentSortOrder);

                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query.toString());

                while (resultSet.next()) {
                    int id = resultSet.getInt("car_id");
                    String brand = resultSet.getString("brand");
                    String model = resultSet.getString("model");
                    String logoUrl = resultSet.getString("brand");
                    logoUrl = toLowerCase(logoUrl);
                    int year = resultSet.getInt("year");
                    int speed = resultSet.getInt("speed");
                    double price = resultSet.getDouble("price");

                    int logoResourceId = getResources().getIdentifier(logoUrl, "drawable", getPackageName());

                    result.append("ID: ").append(id).append("\n");
                    result.append("Brand: ").append(brand).append("\n");
                    result.append("Model: ").append(model).append("\n");
                    result.append("Year: ").append(year).append("\n");
                    result.append("Speed: ").append(speed).append("\n");
                    result.append("Price: ").append(price).append("\n\n");

                    if (logoResourceId != 0) {
                        result.append("Logo: ").append(logoResourceId).append("\n\n");
                    }

                    result.append("----------------------------------------\n\n");
                }

                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
                result.append(e.getMessage());
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            carsLayout.removeAllViews(); // Clear the layout before adding new data

            String[] carEntries = result.split("----------------------------------------\n\n");
            for (String entry : carEntries) {
                LinearLayout carLayout = new LinearLayout(FindParkedCar.this);
                carLayout.setOrientation(LinearLayout.VERTICAL);

                String[] lines = entry.split("\n");
                for (String line : lines) {
                    if (line.startsWith("Logo: ")) {
                        int logoResourceId = Integer.parseInt(line.substring(6));
                        ImageView imageView = new ImageView(FindParkedCar.this);
                        imageView.setImageResource(logoResourceId);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dpToPx(75), dpToPx(75));
                        imageView.setLayoutParams(layoutParams);
                        carLayout.addView(imageView);
                    } else {
                        TextView textView = new TextView(FindParkedCar.this);
                        textView.setText(line);
                        textView.setTextSize(16); // Встановити розмір шрифту 16sp
                        carLayout.addView(textView);
                    }
                }

                View separator = new View(FindParkedCar.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                );
                params.setMargins(0, 16, 0, 16);
                separator.setLayoutParams(params);
                separator.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

                carsLayout.addView(carLayout);
                carsLayout.addView(separator);
            }
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
