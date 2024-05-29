package com.example.taxi3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WorkWithDataBase extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_work_with_data_base);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void GoAdd(View view) {
        Intent intent;
        intent = new Intent(WorkWithDataBase.this, AddData.class);
        startActivity(intent);
    }
    public void GoDelete(View view) {
        Intent intent;
        intent = new Intent(WorkWithDataBase.this, DeleteData.class);
        startActivity(intent);
    }
    public void GoEdit(View view) {
        Intent intent;
        intent = new Intent(WorkWithDataBase.this, EditData.class);
        startActivity(intent);
    }

}