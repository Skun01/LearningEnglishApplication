package com.example.learningenglishapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.Menu;
import androidx.appcompat.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CategoryManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private DatabaseHelper databaseHelper;
    private FloatingActionButton fabAddCategory;
    private long currentUserId;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);
        // --- Thiết lập Toolbar ---
        toolbar = findViewById(R.id.toolbar_category);
        setSupportActionBar(toolbar);
        // Hiển thị nút quay lại (mũi tên)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        databaseHelper = new DatabaseHelper(this);

        // Lấy User ID đã được lưu khi đăng nhập
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentUserId = sharedPreferences.getLong("userId", -1);

        if (currentUserId == -1) {
            // Xử lý lỗi, người dùng chưa đăng nhập nhưng vào được màn hình này
            Toast.makeText(this, "Lỗi: Không tìm thấy người dùng.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.rv_categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lấy dữ liệu và gán cho adapter
        Cursor cursor = databaseHelper.getAllCategories(currentUserId);
        adapter = new CategoryAdapter(this, cursor);
        recyclerView.setAdapter(adapter);

        fabAddCategory = findViewById(R.id.fab_add_category);
        fabAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình thêm thể loại
                startActivity(new Intent(CategoryManagementActivity.this, AddEditCategoryActivity.class));
            }
        });
    }

    // Cập nhật lại danh sách mỗi khi quay lại màn hình này
    @Override
    protected void onResume() {
        super.onResume();
        Cursor newCursor = databaseHelper.getAllCategories(currentUserId);
        adapter.swapCursor(newCursor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Khi người dùng nhấn enter
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Khi người dùng gõ từng chữ
                Cursor cursor = databaseHelper.searchCategories(currentUserId, newText);
                adapter.swapCursor(cursor); // Cập nhật lại RecyclerView
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}