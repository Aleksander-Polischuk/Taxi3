package com.example.taxi3;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DeleteData extends AppCompatActivity {

    private LinearLayout carsLayout;
    private List<Car> cars = new ArrayList<>();
    private String currentSortOrder = "car_id";  // Default sort order

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_data);

        carsLayout = findViewById(R.id.carsLayout);
        Spinner sortSpinner = findViewById(R.id.sortSpinner);

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
                new LoadCarsTask().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Load initial data
        new LoadCarsTask().execute();

        // Виклик методу для видалення вибраних автомобілів
        findViewById(R.id.DeleteDataButton).setOnClickListener(this::onClickDeleteSelected);

    }

    private void populateCarsLayout() {
        carsLayout.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Car car : cars) {
            View carItemView = inflater.inflate(R.layout.car_item, carsLayout, false);

            TextView carInfo = carItemView.findViewById(R.id.carInfo);
            carInfo.setText(car.toString());

            CheckBox checkBox = carItemView.findViewById(R.id.checkBox);
            checkBox.setTag(car.getId());

            carsLayout.addView(carItemView);
        }
    }


    public void onClickDeleteSelected(View view) {
        List<Integer> selectedCarIds = new ArrayList<>();
        for (int i = 0; i < carsLayout.getChildCount(); i++) {
            LinearLayout carItemLayout = (LinearLayout) carsLayout.getChildAt(i);
            CheckBox checkBox = (CheckBox) carItemLayout.getChildAt(1);
            if (checkBox.isChecked()) {
                selectedCarIds.add((Integer) checkBox.getTag());
            }
        }

        new DeleteCarsTask().execute(selectedCarIds);
    }

    private class LoadCarsTask extends AsyncTask<Void, Void, List<Car>> {
        @Override
        protected List<Car> doInBackground(Void... voids) {
            return getCarsFromDatabase();
        }

        @Override
        protected void onPostExecute(List<Car> carsResult) {
            cars = carsResult;
            sortCars();
            populateCarsLayout();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DeleteCarsTask extends AsyncTask<List<Integer>, Void, Void> {

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Integer>... params) {
            deleteSelectedCarsFromDatabase(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Оновлення списку автомобілів після видалення
            new LoadCarsTask().execute();
        }
    }

    private void sortCars() {
        switch (currentSortOrder) {
            case "car_id":
                cars.sort(Comparator.comparingInt(Car::getId));
                break;
            case "brand":
                cars.sort(Comparator.comparing(Car::getBrand));
                break;
            case "price":
                cars.sort(Comparator.comparingDouble(Car::getPrice));
                break;
            case "speed":
                cars.sort(Comparator.comparingInt(Car::getSpeed));
                break;
            case "year":
                cars.sort(Comparator.comparingInt(Car::getYear));
                break;
        }
    }

    private List<Car> getCarsFromDatabase() {
        List<Car> cars = new ArrayList<>();
        String url = "jdbc:mysql://192.168.1.104:3306/Taxi";
        String user = "test";
        String password = "";

        String query = "SELECT car_id, brand, model, year, speed, price FROM cars ORDER BY " + currentSortOrder;

        try {
            // Завантаження драйвера
            Class.forName("com.mysql.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(url, user, password);
                 PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    int id = resultSet.getInt("car_id");
                    String brand = resultSet.getString("brand");
                    String model = resultSet.getString("model");
                    int year = resultSet.getInt("year");
                    int speed = resultSet.getInt("speed");
                    double price = resultSet.getDouble("price");
                    cars.add(new Car(id, brand, model, year, speed, price));
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return cars;
    }

    private void deleteSelectedCarsFromDatabase(List<Integer> selectedCarIds) {
        String url = "jdbc:mysql://192.168.1.104:3306/Taxi";
        String user = "test";
        String password = "";

        String query = "DELETE FROM cars WHERE car_id = ?";

        try {
            // Завантаження драйвера
            Class.forName("com.mysql.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(url, user, password);
                 PreparedStatement statement = connection.prepareStatement(query)) {

                connection.setAutoCommit(false);

                    for (Integer id : selectedCarIds) {
                        statement.setInt(1, id);
                        statement.addBatch();
                    }

                    statement.executeBatch();
                    connection.commit();
                } catch(SQLException e){
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }