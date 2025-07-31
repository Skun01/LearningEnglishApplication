package com.example.learningenglishapplication.Searching;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningenglishapplication.Data.DatabaseHelper;
import com.example.learningenglishapplication.R;
import com.example.learningenglishapplication.Vocabulary.VocabularyAdapter;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearchKeyword;
    private Button btnSearch;
    private RecyclerView rvSearchResults;
    private VocabularyAdapter adapter; // Tái sử dụng Adapter tuyệt vời này!
    private DatabaseHelper databaseHelper;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_export); // Đảm bảo bạn dùng đúng layout

        // --- Thiết lập Toolbar ---
        // Bạn cần thêm một Toolbar vào layout activity_search_export.xml với id: toolbar_search
        Toolbar toolbar = findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tìm kiếm Từ vựng");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // --- Ánh xạ Views và khởi tạo ---
        databaseHelper = new DatabaseHelper(this);
        userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getLong("userId", -1);

        etSearchKeyword = findViewById(R.id.et_search_keyword);
        btnSearch = findViewById(R.id.btn_search);
        rvSearchResults = findViewById(R.id.rv_search_results);

        // --- Thiết lập RecyclerView ---
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VocabularyAdapter(this, null); // Khởi tạo với cursor rỗng
        rvSearchResults.setAdapter(adapter);

        // --- Xử lý sự kiện ---
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });
    }

    private void performSearch() {
        String keyword = etSearchKeyword.getText().toString().trim();
        if (!keyword.isEmpty()) {
            Cursor cursor = databaseHelper.searchVocabularies(userId, keyword);
            adapter.swapCursor(cursor); // Cập nhật kết quả lên RecyclerView
        }
    }

    // Xử lý nút quay lại trên Toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}