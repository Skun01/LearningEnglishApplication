package com.example.learningenglishapplication.Vocabulary;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

import com.example.learningenglishapplication.Data.DataHelper.StatisticsDataHelper;
import com.example.learningenglishapplication.Data.DataHelper.VocabularyDataHelper;
import com.example.learningenglishapplication.Data.DatabaseHelper;
import com.example.learningenglishapplication.Data.model.Vocabulary;
import com.example.learningenglishapplication.R;

public class AddEditVocabularyActivity extends AppCompatActivity {

    private EditText etWord, etPronunciation, etMeaning;
    private Button btnSave;
    private VocabularyDataHelper vocabularyDataHelper;
    private StatisticsDataHelper statisticsDataHelper;

    private long categoryId;
    private long userId;

    private boolean isEditing = false;
    private long vocabIdToEdit = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_vocabulary);

        // Khởi tạo các helper
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        vocabularyDataHelper = new VocabularyDataHelper(databaseHelper);
        statisticsDataHelper = new StatisticsDataHelper(this);

        // Ánh xạ các View
        etWord = findViewById(R.id.et_word);
        etPronunciation = findViewById(R.id.et_pronunciation);
        etMeaning = findViewById(R.id.et_meaning);
        btnSave = findViewById(R.id.btn_save_vocabulary);

        // Cài đặt Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar_add_edit_vocabulary);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Lấy dữ liệu từ Intent và Shared Preferences
        categoryId = getIntent().getLongExtra("CATEGORY_ID", -1);
        userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getLong("userId", -1);
        vocabIdToEdit = getIntent().getLongExtra("VOCAB_ID", -1);

        if (vocabIdToEdit != -1) {
            isEditing = true;
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Sửa Từ Vựng");
            }
            loadVocabularyData();
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Thêm Từ Mới");
            }
        }

        // Xử lý sự kiện click
        btnSave.setOnClickListener(v -> saveVocabulary());
    }

    /**
     * Ghi đè phương thức này để xử lý sự kiện khi nhấn nút "up" (quay lại) trên Toolbar.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadVocabularyData() {
        if (vocabIdToEdit != -1) {
            Vocabulary vocabulary = vocabularyDataHelper.getVocabularyById(vocabIdToEdit);
            if (vocabulary != null) {
                etWord.setText(vocabulary.getWord());
                etPronunciation.setText(vocabulary.getPronunciation());
                etMeaning.setText(vocabulary.getMeaning());
            } else {
                Toast.makeText(this, "Không tìm thấy từ vựng.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void saveVocabulary() {
        String word = etWord.getText().toString().trim();
        String pronunciation = etPronunciation.getText().toString().trim();
        String meaning = etMeaning.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (word.isEmpty() || meaning.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ từ và nghĩa.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (categoryId == -1 || userId == -1) {
            Toast.makeText(this, "Cập nhật thất bại !", Toast.LENGTH_SHORT).show();
            return;
        }

        long returnedVocabId = -1;

        if (isEditing) {
            // Chế độ Sửa
            int result = vocabularyDataHelper.updateVocabularyWithCheck(vocabIdToEdit, categoryId, word, pronunciation, meaning);

            if (result == 1) { // Thành công
                returnedVocabId = vocabIdToEdit;
                Toast.makeText(this, "Cập nhật từ vựng thành công!", Toast.LENGTH_SHORT).show();
            } else if (result == -1) { // Lỗi trùng lặp
                Toast.makeText(this, "Từ vựng cập nhật đã tồn tại trong thể loại!", Toast.LENGTH_SHORT).show();
                return;
            } else { // Cập nhật thất bại
                Toast.makeText(this, "Cập nhật thất bại.", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            // Chế độ Thêm mới
            if (vocabularyDataHelper.isVocabularyExists(categoryId, word, meaning)) {
                Toast.makeText(this, "Từ vựng này đã tồn tại trong thể loại!", Toast.LENGTH_SHORT).show();
                return;
            }

            returnedVocabId = vocabularyDataHelper.addVocabulary(userId, categoryId, word, pronunciation, meaning);

            if (returnedVocabId != -1) {
                statisticsDataHelper.logWordLearned(userId);
                Toast.makeText(this, "Đã thêm từ mới!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Thêm từ mới thất bại.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Trả về kết quả và ID của từ vựng đã được thêm/cập nhật
        Intent resultIntent = new Intent();
        resultIntent.putExtra("HIGHLIGHT_VOCAB_ID", returnedVocabId);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}