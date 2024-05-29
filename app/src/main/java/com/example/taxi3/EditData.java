package com.example.taxi3;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class EditData extends AppCompatActivity {
    private Spinner carSpinner;
    private EditText inputYear, inputPrice, inputMaxSpeed, inputFuelConsumption, inputEngineCapacity;
    private Button saveButton;
    private HashMap<String, Car> carMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);

        carSpinner = findViewById(R.id.CarSpinner);
        inputYear = findViewById(R.id.InputYear);
        inputPrice = findViewById(R.id.InputPrice);
        inputMaxSpeed = findViewById(R.id.InputMaxSpeed);
        inputFuelConsumption = findViewById(R.id.InputFuelConsumption);
        inputEngineCapacity = findViewById(R.id.InputEngineCapacity);
        saveButton = findViewById(R.id.EditDataButton);

        new LoadCarsTask().execute();

        carSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCar = (String) parent.getItemAtPosition(position);
                Car car = carMap.get(selectedCar);
                if (car != null) {
                    inputYear.setText(String.valueOf(car.getYear()));
                    inputPrice.setText(String.valueOf(car.getPrice()));
                    inputMaxSpeed.setText(String.valueOf(car.getMaxSpeed()));
                    inputFuelConsumption.setText(String.valueOf(car.getFuelConsumption()));
                    inputEngineCapacity.setText(String.valueOf(car.getEngineCapacity()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        saveButton.setOnClickListener(v -> new SaveCarTask().execute());
    }

    private class LoadCarsTask extends AsyncTask<Void, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            return getCars();
        }

        @Override
        protected void onPostExecute(ArrayList<String> cars) {
            super.onPostExecute(cars);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(EditData.this, android.R.layout.simple_spinner_item, cars);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            carSpinner.setAdapter(adapter);
        }
    }

    private ArrayList<String> getCars() {
        ArrayList<String> cars = new ArrayList<>();
        try {
            Connection connection = getConnection();
            if (connection != null) {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM Cars");

                while (resultSet.next()) {
                    String carInfo = resultSet.getString("brand") + " " +
                            resultSet.getString("model") + " (" +
                            resultSet.getString("year") + ")";
                    cars.add(carInfo);

                    Car car = new Car(
                            resultSet.getInt("car_id"),
                            resultSet.getString("brand"),
                            resultSet.getString("model"),
                            resultSet.getInt("year"),
                            resultSet.getInt("speed"),
                            resultSet.getDouble("price"),
                            resultSet.getDouble("fuel_consumption"),
                            resultSet.getDouble("engine_capacity")

                    );
                    carMap.put(carInfo, car);
                }

                resultSet.close();
                statement.close();
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cars;
    }

    private class SaveCarTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            saveCarData();
            return null;
        }
    }

    private void saveCarData() {
        String selectedCar = (String) carSpinner.getSelectedItem();
        Car car = carMap.get(selectedCar);
        if (car != null) {
            int carId = car.getCarId();
            int year = Integer.parseInt(inputYear.getText().toString());
            double price = Double.parseDouble(inputPrice.getText().toString());
            int maxSpeed = Integer.parseInt(inputMaxSpeed.getText().toString());
            double fuelConsumption = Double.parseDouble(inputFuelConsumption.getText().toString());
            double engineCapacity = Double.parseDouble(inputEngineCapacity.getText().toString());

            try {
                Connection connection = getConnection();
                if (connection != null) {
                    String query = "UPDATE Cars SET year = ?, price = ?, speed = ?, fuel_consumption = ?, engine_capacity = ? WHERE car_id = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, year);
                    preparedStatement.setDouble(2, price);
                    preparedStatement.setInt(3, maxSpeed);
                    preparedStatement.setDouble(4, fuelConsumption);
                    preparedStatement.setDouble(5, engineCapacity);
                    preparedStatement.setInt(6, carId);
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static final String URL = "jdbc:mysql://192.168.1.104:3306/Taxi";
    private static final String USER = "test";
    private static final String PASSWORD = "";

    private Connection getConnection() {
        Connection connection = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    private static class Car {
        private int carId;
        private String brand;
        private String model;
        private int year;
        private int maxSpeed;
        private double price;
        private double fuelConsumption;
        private double engineCapacity;

        public Car(int carId, String brand, String model, int year, int maxSpeed, double price, double fuelConsumption, double engineCapacity) {
            this.carId = carId;
            this.brand = brand;
            this.model = model;
            this.year = year;
            this.maxSpeed = maxSpeed;
            this.price = price;
            this.fuelConsumption = fuelConsumption;
            this.engineCapacity = engineCapacity;
        }

        public int getCarId() {
            return carId;
        }

        public int getYear() {
            return year;
        }

        public double getPrice() {
            return price;
        }

        public int getMaxSpeed() {
            return maxSpeed;
        }

        public double getFuelConsumption() {
            return fuelConsumption;
        }

        public double getEngineCapacity() {
            return engineCapacity;
        }
    }
}
