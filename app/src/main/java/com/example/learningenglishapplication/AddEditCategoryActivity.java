package com.example.learningenglishapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditCategoryActivity extends AppCompatActivity {

    private EditText etName, etDescription;
    private Button btnSave;
    private DatabaseHelper databaseHelper;
    private long currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_category);

        databaseHelper = new DatabaseHelper(this);
        etName = findViewById(R.id.et_category_name);
        etDescription = findViewById(R.id.et_category_description);
        btnSave = findViewById(R.id.btn_save_category);

        // Lấy User ID
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentUserId = sharedPreferences.getLong("userId", -1);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCategory();
            }
        });
    }

    private void saveCategory() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Tên thể loại không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUserId == -1) {
            Toast.makeText(this, "Lỗi: Không thể lưu.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isAdded = databaseHelper.addCategory(name, description, currentUserId);
        if (isAdded) {
            Toast.makeText(this, "Đã lưu thể loại!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng màn hình và quay lại danh sách
        } else {
            Toast.makeText(this, "Lưu thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}
