package com.example.learningenglishapplication;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditCategoryActivity extends AppCompatActivity {

    private EditText etName, etDescription;
    private Button btnSave;
    private DatabaseHelper databaseHelper;
    private long currentUserId;

    // Biến để xác định là đang sửa hay thêm mới
    private boolean isEditing = false;
    private long categoryIdToEdit = -1;

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

        // KIỂM TRA XEM CÓ PHẢI LÀ CHẾ ĐỘ SỬA KHÔNG
        if (getIntent().hasExtra("CATEGORY_ID")) {
            isEditing = true;
            categoryIdToEdit = getIntent().getLongExtra("CATEGORY_ID", -1);
            setTitle("Sửa Thể Loại"); // Đổi tiêu đề Activity
            loadCategoryData();
        } else {
            setTitle("Thêm Thể Loại Mới");
        }

        btnSave.setOnClickListener(v -> saveCategory());
    }

    private void loadCategoryData() {
        // Lấy thông tin thể loại cũ từ DB và điền vào EditText
        // (Bạn cần thêm một phương thức trong DatabaseHelper để lấy một thể loại duy nhất)
        // Tạm thời chúng ta sẽ bỏ qua phần này để đơn giản, bạn có thể tự thêm sau.
        // Ví dụ: Category category = databaseHelper.getCategory(categoryIdToEdit);
        // etName.setText(category.getName());
        // etDescription.setText(category.getDescription());
        Toast.makeText(this, "Chế độ sửa (chức năng tải dữ liệu cũ sẽ được thêm sau)", Toast.LENGTH_SHORT).show();
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
            int rowsAffected = databaseHelper.updateCategory(categoryIdToEdit, name, description);
            if (rowsAffected > 0) {
                Toast.makeText(this, "Đã cập nhật thể loại!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Logic cho việc THÊM MỚI (giữ nguyên)
            boolean isAdded = databaseHelper.addCategory(name, description, currentUserId);
            if (isAdded) {
                Toast.makeText(this, "Đã lưu thể loại!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lưu thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
