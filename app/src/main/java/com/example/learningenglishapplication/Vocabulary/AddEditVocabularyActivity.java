package com.example.learningenglishapplication.Vocabulary;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningenglishapplication.Data.DatabaseHelper;
import com.example.learningenglishapplication.R;

public class AddEditVocabularyActivity extends AppCompatActivity {

    private EditText etWord, etMeaning;
    private Button btnSave;
    private DatabaseHelper databaseHelper;

    private long categoryId;
    private long userId;

    // Biến để xác định chế độ Sửa/Thêm
    private boolean isEditing = false;
    private long vocabIdToEdit = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_vocabulary);

        databaseHelper = new DatabaseHelper(this);

        categoryId = getIntent().getLongExtra("CATEGORY_ID", -1);
        userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getLong("userId", -1);

        etWord = findViewById(R.id.et_word);
        etMeaning = findViewById(R.id.et_meaning);
        btnSave = findViewById(R.id.btn_save_vocabulary);

        // KIỂM TRA XEM CÓ PHẢI LÀ CHẾ ĐỘ SỬA KHÔNG
        if (getIntent().hasExtra("VOCAB_ID")) {
            isEditing = true;
            vocabIdToEdit = getIntent().getLongExtra("VOCAB_ID", -1);
            setTitle("Sửa Từ Vựng");
            loadVocabularyData(); // Tải dữ liệu cũ lên
        } else {
            setTitle("Thêm Từ Mới");
        }

        btnSave.setOnClickListener(v -> saveVocabulary());
    }

    private void loadVocabularyData() {
        if (vocabIdToEdit != -1) {
            Cursor cursor = databaseHelper.getVocabulary(vocabIdToEdit);
            if (cursor.moveToFirst()) {
                String word = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_WORD));
                String meaning = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_MEANING));
                etWord.setText(word);
                etMeaning.setText(meaning);
            }
            cursor.close();
        }
    }

    private void saveVocabulary() {
        String word = etWord.getText().toString().trim();
        String meaning = etMeaning.getText().toString().trim();

        if (word.isEmpty() || meaning.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ từ và nghĩa", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditing) {
            // Logic SỬA
            int rowsAffected = databaseHelper.updateVocabulary(vocabIdToEdit, word, meaning);
            if (rowsAffected > 0) {
                Toast.makeText(this, "Đã cập nhật!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Logic THÊM MỚI (đã có)
            if (categoryId == -1 || userId == -1) {
                Toast.makeText(this, "Lỗi: Không thể xác định thể loại hoặc người dùng.", Toast.LENGTH_LONG).show();
                return;
            }
            boolean isAdded = databaseHelper.addVocabulary(userId, categoryId, word, meaning);
            if (isAdded) {
                databaseHelper.logWordLearned(userId);
                Toast.makeText(this, "Đã thêm từ mới!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Thêm từ mới thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }
}