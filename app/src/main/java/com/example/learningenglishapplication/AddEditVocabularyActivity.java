package com.example.learningenglishapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditVocabularyActivity extends AppCompatActivity {

    private EditText etWord, etMeaning;
    private Button btnSave;
    private DatabaseHelper databaseHelper;

    private long categoryId;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_vocabulary);

        databaseHelper = new DatabaseHelper(this);

        // Lấy ID của thể loại từ Intent
        categoryId = getIntent().getLongExtra("CATEGORY_ID", -1);

        // Lấy ID của người dùng từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getLong("userId", -1);

        // Kiểm tra an toàn
        if (categoryId == -1 || userId == -1) {
            Toast.makeText(this, "Lỗi: Không thể xác định thể loại hoặc người dùng.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        etWord = findViewById(R.id.et_word);
        etMeaning = findViewById(R.id.et_meaning);
        btnSave = findViewById(R.id.btn_save_vocabulary);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveVocabulary();
            }
        });
    }

    private void saveVocabulary() {
        String word = etWord.getText().toString().trim();
        String meaning = etMeaning.getText().toString().trim();

        if (word.isEmpty() || meaning.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ từ và nghĩa", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi phương thức trong DatabaseHelper để thêm từ mới
        boolean isAdded = databaseHelper.addVocabulary(userId, categoryId, word, meaning);

        if (isAdded) {
            Toast.makeText(this, "Đã thêm từ mới!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng màn hình và quay lại danh sách
        } else {
            Toast.makeText(this, "Thêm từ mới thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}