package com.example.learningenglishapplication.Vocabulary;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.Serializable;
import java.util.List;

import com.example.learningenglishapplication.Data.DatabaseHelper;
import com.example.learningenglishapplication.R;
import com.example.learningenglishapplication.Data.model.Vocabulary;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class VocabularyListActivity extends AppCompatActivity implements VocabularyAdapter.OnItemInteractionListener {

    private RecyclerView recyclerView;
    private VocabularyAdapter adapter;
    private DatabaseHelper databaseHelper;
    private FloatingActionButton fabAddVocabulary;
    private Button btnStartFlashcard;

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
        adapter = new VocabularyAdapter(this, null); // Khởi tạo với cursor rỗng
        adapter.setOnItemInteractionListener(this); // Đăng ký listener
        recyclerView.setAdapter(adapter);

        loadVocabularies();

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

        btnStartFlashcard = findViewById(R.id.btn_start_flashcard);
        btnStartFlashcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy danh sách từ vựng từ database
                List<Vocabulary> vocabList = databaseHelper.getVocabulariesAsList(categoryId);

                if (vocabList.isEmpty()) {
                    Toast.makeText(VocabularyListActivity.this, "Chưa có từ nào để ôn tập!", Toast.LENGTH_SHORT).show();
                } else {
                    // Nếu có từ vựng, bắt đầu FlashcardActivity
                    Intent flashcardIntent = new Intent(VocabularyListActivity.this, FlashcardActivity.class);
                    // Gửi toàn bộ danh sách sang
                    flashcardIntent.putExtra("VOCAB_LIST", (Serializable) vocabList);
                    startActivity(flashcardIntent);
                }
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

    @Override
    public void onItemClick(long vocabId) {
        // Hiện tại chưa làm gì, có thể dùng sau này để xem chi tiết từ
    }

    // PHƯƠNG THỨC MỚI: Xử lý khi nhấn giữ vào một từ
    @Override
    public void onItemLongClick(long vocabId, String word) {
        final CharSequence[] options = {"Sửa", "Xóa", "Hủy"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lựa chọn cho: " + word);
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Sửa")) {
                Intent intent = new Intent(VocabularyListActivity.this, AddEditVocabularyActivity.class);
                intent.putExtra("VOCAB_ID", vocabId); // Gửi ID của từ vựng để sửa
                intent.putExtra("CATEGORY_ID", categoryId); // Vẫn cần ID thể loại
                startActivity(intent);
            } else if (options[item].equals("Xóa")) {
                showDeleteConfirmationDialog(vocabId, word);
            } else if (options[item].equals("Hủy")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showDeleteConfirmationDialog(long vocabId, String word) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa từ '" + word + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    databaseHelper.deleteVocabulary(vocabId);
                    Toast.makeText(this, "Đã xóa từ: " + word, Toast.LENGTH_SHORT).show();
                    loadVocabularies(); // Tải lại danh sách
                })
                .setNegativeButton("Hủy", null)
                .show();
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
