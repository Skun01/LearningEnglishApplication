package com.example.learningenglishapplication;

import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class VocabularyListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VocabularyAdapter adapter;
    private DatabaseHelper databaseHelper;
    private FloatingActionButton fabAddVocabulary;

    private long categoryId;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_list);

        databaseHelper = new DatabaseHelper(this);

        // Lấy dữ liệu ID và Tên của thể loại được truyền từ màn hình trước
        Intent intent = getIntent();
        categoryId = intent.getLongExtra("CATEGORY_ID", -1);
        categoryName = intent.getStringExtra("CATEGORY_NAME");

        // Kiểm tra an toàn
        if (categoryId == -1) {
            Toast.makeText(this, "Lỗi: ID thể loại không hợp lệ.", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity nếu không có ID
            return;
        }

        // --- Thiết lập Toolbar ---
        Toolbar toolbar = findViewById(R.id.toolbar_vocabulary_list); // Đảm bảo ID này tồn tại trong XML
        toolbar.setTitle(categoryName); // Đặt tên thể loại làm tiêu đề
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // --- Thiết lập RecyclerView ---
        recyclerView = findViewById(R.id.rv_vocabularies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lấy dữ liệu ban đầu và khởi tạo Adapter
        Cursor cursor = databaseHelper.getVocabulariesForCategory(categoryId);
        adapter = new VocabularyAdapter(this, cursor);
        recyclerView.setAdapter(adapter);

        // --- Thiết lập Nút Floating Action Button ---
        fabAddVocabulary = findViewById(R.id.fab_add_vocabulary);
        fabAddVocabulary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở màn hình thêm từ mới
                Intent addIntent = new Intent(VocabularyListActivity.this, AddEditVocabularyActivity.class);
                // Gửi ID của thể loại hiện tại sang màn hình thêm mới
                addIntent.putExtra("CATEGORY_ID", categoryId);
                startActivity(addIntent);
            }
        });
    }

    // Tải lại dữ liệu mỗi khi quay lại màn hình này
    private void loadVocabularies() {
        Cursor newCursor = databaseHelper.getVocabulariesForCategory(categoryId);
        adapter.swapCursor(newCursor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVocabularies(); // Rất quan trọng: Cập nhật danh sách khi quay lại
    }

    // Xử lý sự kiện nhấn nút quay lại trên Toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Đóng activity và quay về
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
