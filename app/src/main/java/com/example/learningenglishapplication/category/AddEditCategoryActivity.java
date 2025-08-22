package com.example.learningenglishapplication.category;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.learningenglishapplication.Utils.BaseChildActivity;

import com.example.learningenglishapplication.Data.DataHelper.CategoryDataHelper;
import com.example.learningenglishapplication.Data.DatabaseHelper;
import com.example.learningenglishapplication.R;

public class AddEditCategoryActivity extends BaseChildActivity {

    private EditText etName, etDescription;
    private Button btnSave;
    private CategoryDataHelper categoryHelper;
    private long currentUserId;

    // Biến để xác định là đang sửa hay thêm mới
    private boolean isEditing = false;
    private long categoryIdToEdit = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_category);

        categoryHelper = new CategoryDataHelper(this);
        etName = findViewById(R.id.et_category_name);
        etDescription = findViewById(R.id.et_category_description);
        btnSave = findViewById(R.id.btn_save_category);

        // Lấy User ID
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentUserId = sharedPreferences.getLong("userId", -1);

        // KIỂM TRA XEM CÓ PHẢI LÀ CHẾ ĐỘ SỬA KHÔNG
        if (getIntent().hasExtra("CATEGORY_ID")) {
            isEditing = true;
            categoryIdToEdit = getIntent().getLongExtra("CATEGORY_ID", -1);
            setupToolbar("Sửa Thể Loại"); // Đổi tiêu đề Activity
            loadCategoryData();
        } else {
            setupToolbar("Thêm Thể Loại Mới");
        }

        btnSave.setOnClickListener(v -> saveCategory());
    }

    private void loadCategoryData() {
        Cursor cursor = categoryHelper.getCategory(categoryIdToEdit);

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_NAME));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_DESCRIPTION));

            etName.setText(name);
            etDescription.setText(description);

            cursor.close();
        } else {
            Toast.makeText(this, "Không tìm thấy dữ liệu để sửa", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveCategory() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Tên thể loại không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditing) {
            // Logic cho việc SỬA
            int rowsAffected = categoryHelper.updateCategory(categoryIdToEdit, name, description);
            if (rowsAffected > 0) {
                Toast.makeText(this, "Đã cập nhật thể loại!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Logic cho việc THÊM MỚI (giữ nguyên)
            boolean isAdded = categoryHelper.addCategory(name, description, currentUserId);
            if (isAdded) {
                Toast.makeText(this, "Đã lưu thể loại!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lưu thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
