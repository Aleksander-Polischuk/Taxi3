package com.example.taxi3;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddData extends AppCompatActivity {

    private Spinner brandSpinner;
    private Spinner modelSpinner;
    private EditText yearEditText;
    private EditText engineCapacityEditText;
    private EditText fuelConsumptionEditText;
    private EditText maxSpeedEditText;
    private EditText priceEditText;
    private Button addButton;

    private String Brand;
    private String Model;
    private int Year;
    private double EngineCapacity;
    private double FuelConsumption;
    private int MaxSpeed;
    private int Price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        brandSpinner = findViewById(R.id.brandSpinner);
        modelSpinner = findViewById(R.id.ModelSpinner);
        yearEditText = findViewById(R.id.InputYear);
        engineCapacityEditText = findViewById(R.id.InputEngineCapacity);
        fuelConsumptionEditText = findViewById(R.id.InputFuelConsumption);
        maxSpeedEditText = findViewById(R.id.InputMaxSpeed);
        priceEditText = findViewById(R.id.InputPrice);
        addButton = findViewById(R.id.AddCarButton);


        // Initialize the brand spinner
        ArrayAdapter<CharSequence> brandAdapter = ArrayAdapter.createFromResource(this,
                R.array.brands_array_withoutFirst, android.R.layout.simple_spinner_item);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandSpinner.setAdapter(brandAdapter);

        brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Brand = position == 0 ? "" : (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Initialize the Model spinner
        ArrayAdapter<CharSequence> ModelAdapter = ArrayAdapter.createFromResource(this,
                R.array.brands_array_withoutFirst, android.R.layout.simple_spinner_item);
        ModelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelSpinner.setAdapter(ModelAdapter);

        modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Model = position == 0 ? "" : (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });




        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String brand = brandSpinner.getSelectedItem().toString();
                String model = modelSpinner.getSelectedItem().toString();
                int year = Integer.parseInt(yearEditText.getText().toString());
                double engineCapacity = Double.parseDouble(engineCapacityEditText.getText().toString());
                double fuelConsumption = Double.parseDouble(fuelConsumptionEditText.getText().toString());
                int maxSpeed = Integer.parseInt(maxSpeedEditText.getText().toString());
                double price = Double.parseDouble(priceEditText.getText().toString());

                new AddCarTask().execute(brand, model, year, engineCapacity, fuelConsumption, maxSpeed, price);
            }
        });
    }
    private class AddCarTask extends AsyncTask<Object, Void, String> {

        private static final String DB_URL = "jdbc:mysql://192.168.1.104:3306/Taxi";
        private static final String USER = "test";
        private static final String PASSWORD = "";

        @Override
        protected String doInBackground(Object... params) {
            String brand = (String) params[0];
            String model = (String) params[1];
            int year = (int) params[2];
            double engineCapacity = (double) params[3];
            double fuelConsumption = (double) params[4];
            int maxSpeed = (int) params[5];
            double price = (double) params[6];

            Connection conn = null;
            PreparedStatement stmt = null;

            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                String sql = "INSERT INTO cars (brand, model, year, engine_capacity, fuel_consumption, speed, price) VALUES (?, ?, ?, ?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, brand);
                stmt.setString(2, model);
                stmt.setInt(3, year);
                stmt.setDouble(4, engineCapacity);
                stmt.setDouble(5, fuelConsumption);
                stmt.setInt(6, maxSpeed);
                stmt.setDouble(7, price);
                stmt.executeUpdate();
                return "Car added successfully";
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                return "Error adding car: " + e.getMessage();
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(AddData.this, result, Toast.LENGTH_LONG).show();
        }
    }
}
