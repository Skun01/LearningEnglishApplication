package com.example.learningenglishapplication.Vocabulary;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.google.android.material.appbar.MaterialToolbar;

import com.example.learningenglishapplication.Data.DatabaseHelper;
import com.example.learningenglishapplication.Data.DataHelper.VocabularyDataHelper; // SỬA: Sử dụng VocabularyDataHelper
import com.example.learningenglishapplication.R;
import com.example.learningenglishapplication.Data.model.Vocabulary;
import com.example.learningenglishapplication.Utils.BaseChildActivity;

public class VocabularyDetailActivity extends BaseChildActivity {

    private TextView tvWord, tvPronunciation, tvMeaning;
    private DatabaseHelper databaseHelper;
    private VocabularyDataHelper vocabularyDataHelper; // SỬA: Đổi tên thành DataHelper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_detail);

        // Sử dụng phương thức setupToolbar từ BaseChildActivity
        setupToolbar("Chi tiết từ vựng");

        databaseHelper = new DatabaseHelper(this);
        // SỬA LỖI: Khởi tạo VocabularyDataHelper bằng cách truyền DatabaseHelper vào
        vocabularyDataHelper = new VocabularyDataHelper(databaseHelper);

        tvWord = findViewById(R.id.tv_detail_word);
        tvPronunciation = findViewById(R.id.tv_detail_pronunciation);
        tvMeaning = findViewById(R.id.tv_detail_meaning);

        long vocabId = getIntent().getLongExtra("VOCAB_ID", -1);

        if (vocabId != -1) {
            // SỬA ĐỔI: Gọi phương thức từ VocabularyDataHelper
            Vocabulary vocabulary = vocabularyDataHelper.getVocabularyById(vocabId);
            if (vocabulary != null) {
                tvWord.setText(vocabulary.getWord());
                tvPronunciation.setText(vocabulary.getPronunciation());
                tvMeaning.setText(vocabulary.getMeaning());
            } else {
                Toast.makeText(this, "Không tìm thấy từ vựng.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Lỗi: ID từ vựng không hợp lệ.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}