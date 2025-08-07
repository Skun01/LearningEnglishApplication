package com.example.learningenglishapplication.Category;

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
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningenglishapplication.Data.DAO.CategoryDAO;
import com.example.learningenglishapplication.Data.DatabaseHelper;
import com.example.learningenglishapplication.R;
import com.example.learningenglishapplication.Vocabulary.VocabularyListActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CategoryManagementActivity extends AppCompatActivity implements CategoryAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private CategoryDAO categoryDAO;
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

        categoryDAO = new CategoryDAO(this);

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
        Cursor cursor = categoryDAO.getAllCategories(currentUserId);
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

        adapter = new CategoryAdapter(this, cursor);
        adapter.setOnItemClickListener(this); // Đăng ký listener
        recyclerView.setAdapter(adapter);
    }

    // Phương thức được gọi khi một thể loại được nhấn
    @Override
    public void onItemClick(long categoryId, String categoryName) {
        Intent intent = new Intent(this, VocabularyListActivity.class);
        intent.putExtra("CATEGORY_ID", categoryId);
        intent.putExtra("CATEGORY_NAME", categoryName);
        startActivity(intent);
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
                Cursor cursor = categoryDAO.searchCategories(currentUserId, newText);
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

    // THÊM PHƯƠNG THỨC MỚI ĐỂ XỬ LÝ LONG CLICK
    @Override
    public void onItemLongClick(long categoryId, String categoryName) {
        final CharSequence[] options = {"Sửa", "Xóa", "Hủy"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lựa chọn cho: " + categoryName);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Sửa")) {
                    // Chuyển sang màn hình AddEditCategory với dữ liệu cũ
                    Intent intent = new Intent(CategoryManagementActivity.this, AddEditCategoryActivity.class);
                    intent.putExtra("CATEGORY_ID", categoryId); // Gửi ID để biết là đang sửa
                    startActivity(intent);
                } else if (options[item].equals("Xóa")) {
                    // Hiển thị hộp thoại xác nhận xóa
                    showDeleteConfirmationDialog(categoryId, categoryName);
                } else if (options[item].equals("Hủy")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void showDeleteConfirmationDialog(long categoryId, String categoryName) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa thể loại '" + categoryName + "' không? Tất cả từ vựng bên trong cũng sẽ bị xóa vĩnh viễn.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    categoryDAO.deleteCategory(categoryId);
                    Toast.makeText(this, "Đã xóa thể loại: " + categoryName, Toast.LENGTH_SHORT).show();
                    loadCategories(); // Tải lại danh sách
                })
                .setNegativeButton("Hủy", null) // Không làm gì khi nhấn Hủy
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // Tạo một phương thức riêng để tải lại danh sách cho gọn
    private void loadCategories() {
        Cursor newCursor = categoryDAO.getAllCategories(currentUserId);
        adapter.swapCursor(newCursor);
    }

    // Cập nhật lại danh sách mỗi khi quay lại màn hình này
    @Override
    protected void onResume() {
        super.onResume();
        loadCategories(); // Sử dụng phương thức mới
    }

}