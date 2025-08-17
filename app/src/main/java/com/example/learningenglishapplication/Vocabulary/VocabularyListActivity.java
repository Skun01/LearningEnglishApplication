package com.example.learningenglishapplication.Vocabulary;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import com.google.android.material.appbar.MaterialToolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

import com.example.learningenglishapplication.Data.DatabaseHelper;
import com.example.learningenglishapplication.Data.DataHelper.VocabularyDataHelper;
import com.example.learningenglishapplication.R;
import com.example.learningenglishapplication.Data.model.Vocabulary;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class VocabularyListActivity extends AppCompatActivity implements VocabularyAdapter.OnItemInteractionListener, VocabularyAdapter.OnFavoriteClickListener {

    private RecyclerView recyclerView;
    private VocabularyAdapter adapter;
    private DatabaseHelper databaseHelper;
    private VocabularyDataHelper vocabularyHelper;
    private FloatingActionButton fabAddVocabulary;
    private Button btnStartFlashcard;
    private TextView tvEmptyMessage;
    private ImageView btnFilter;
    private TextView tvVocabularyCount;

    private String currentFilter = "ALL";
    private static final int ADD_EDIT_VOCABULARY_REQUEST = 1;

    private long categoryId;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_list);

        databaseHelper = new DatabaseHelper(this);
        vocabularyHelper = new VocabularyDataHelper(databaseHelper);

        Intent intent = getIntent();
        categoryId = intent.getLongExtra("CATEGORY_ID", -1);
        categoryName = intent.getStringExtra("CATEGORY_NAME");

        if (categoryId == -1) {
            Toast.makeText(this, "Lỗi: ID thể loại không hợp lệ.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar_vocabulary_list);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(categoryName);
        }

        recyclerView = findViewById(R.id.rv_vocabularies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new VocabularyAdapter(this, null);
        adapter.setOnItemInteractionListener(this);
        adapter.setOnFavoriteClickListener(this);
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

        fabAddVocabulary = findViewById(R.id.fab_add_vocabulary);
        fabAddVocabulary.setOnClickListener(v -> {
            Intent addIntent = new Intent(VocabularyListActivity.this, AddEditVocabularyActivity.class);
            addIntent.putExtra("CATEGORY_ID", categoryId);
            startActivityForResult(addIntent, ADD_EDIT_VOCABULARY_REQUEST);
        });

        btnStartFlashcard = findViewById(R.id.btn_start_flashcard);
        btnStartFlashcard.setOnClickListener(v -> {
            List<Vocabulary> vocabList = vocabularyHelper.getVocabulariesAsList(categoryId);
            if (vocabList.isEmpty()) {
                Toast.makeText(VocabularyListActivity.this, "Chưa có từ nào để ôn tập!", Toast.LENGTH_SHORT).show();
            } else {
                Intent flashcardIntent = new Intent(VocabularyListActivity.this, FlashcardActivity.class);
                flashcardIntent.putExtra("VOCAB_LIST", (Serializable) vocabList);
                startActivity(flashcardIntent);
            }
        });

        tvEmptyMessage = findViewById(R.id.tv_empty_message);

        btnFilter = findViewById(R.id.btn_filter);
        btnFilter.setOnClickListener(this::showFilterMenu);

        tvVocabularyCount = findViewById(R.id.tv_vocabulary_count);

        loadVocabularies();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_EDIT_VOCABULARY_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                // Nhận ID của từ vựng được làm nổi bật từ Intent
                long highlightedVocabId = data.getLongExtra("HIGHLIGHT_VOCAB_ID", -1);

                // Chuyển về chế độ lọc "Tất cả" và làm mới danh sách
                setFilter("ALL");

                // Đặt ID cần làm nổi bật cho adapter
                adapter.setHighlightedVocabId(highlightedVocabId);
            }
        }
    }

    private void showFilterMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.filter_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.filter_all) {
                setFilter("ALL");
            } else if (id == R.id.filter_favorite) {
                setFilter("FAVORITE");
            } else if (id == R.id.filter_unlearned) {
                setFilter("UNLEARNED");
            } else if (id == R.id.filter_learned) {
                setFilter("LEARNED");
            }
            return true;
        });
        popup.show();
    }

    private void setFilter(String filterType) {
        currentFilter = filterType;
        loadVocabularies();
    }

    private void loadVocabularies() {
        Cursor newCursor = null;

        switch (currentFilter) {
            case "ALL":
                newCursor = vocabularyHelper.getVocabulariesForCategory(categoryId);
                break;
            case "FAVORITE":
                newCursor = vocabularyHelper.getFavoriteVocabularies(categoryId);
                break;
            case "UNLEARNED":
                newCursor = vocabularyHelper.getUnlearnedVocabularies(categoryId);
                break;
            case "LEARNED":
                newCursor = vocabularyHelper.getLearnedVocabularies(categoryId);
                break;
        }

        int count = newCursor.getCount();
        tvVocabularyCount.setText("Tổng số: " + count + " từ");

        adapter.swapCursor(newCursor);

        if (newCursor != null && newCursor.getCount() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyMessage.setVisibility(View.GONE);
            tvVocabularyCount.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
            tvEmptyMessage.setVisibility(View.VISIBLE);
            tvVocabularyCount.setVisibility(View.GONE);
            switch (currentFilter) {
                case "FAVORITE":
                    tvEmptyMessage.setText("Chưa có từ vựng yêu thích nào.");
                    break;
                case "UNLEARNED":
                    tvEmptyMessage.setText("Bạn đã học hết các từ vựng.");
                    break;
                case "LEARNED":
                    tvEmptyMessage.setText("Bạn chưa học từ vựng nào.");
                    break;
                default:
                    tvEmptyMessage.setText("Hãy thêm từ mới.");
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVocabularies();
    }

    @Override
    public void onFavoriteClick(long vocabId, int isFavorite) {
        vocabularyHelper.updateFavoriteStatus(vocabId, isFavorite);
        loadVocabularies();
        recyclerView.scrollToPosition(0);
        if (isFavorite == 1) {
            Toast.makeText(this, "Đã thêm vào mục yêu thích", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Đã xóa khỏi mục yêu thích", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(long vocabId) {
        Intent intent = new Intent(VocabularyListActivity.this, VocabularyDetailActivity.class);
        intent.putExtra("VOCAB_ID", vocabId);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(long vocabId, String word) {
        final CharSequence[] options = {"Sửa", "Xóa", "Đánh dấu đã học", "Hủy"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lựa chọn cho: " + word);
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Sửa")) {
                Intent intent = new Intent(VocabularyListActivity.this, AddEditVocabularyActivity.class);
                intent.putExtra("VOCAB_ID", vocabId);
                intent.putExtra("CATEGORY_ID", categoryId);
                startActivity(intent);
            } else if (options[item].equals("Xóa")) {
                showDeleteConfirmationDialog(vocabId, word);
            } else if (options[item].equals("Đánh dấu đã học")) {
                vocabularyHelper.markVocabularyAsLearned(vocabId);
                loadVocabularies();
                Toast.makeText(this, "Đã đánh dấu '" + word + "' là đã học", Toast.LENGTH_SHORT).show();
            } else {
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
                    vocabularyHelper.deleteVocabulary(vocabId);
                    Toast.makeText(this, "Đã xóa từ: " + word, Toast.LENGTH_SHORT).show();
                    loadVocabularies();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            long vocabId = adapter.getVocabIdAtPosition(position);
            String vocabWord = adapter.getWordAtPosition(position);

            if (direction == ItemTouchHelper.LEFT) {
                showDeleteConfirmationDialog(vocabId, vocabWord);
            } else if (direction == ItemTouchHelper.RIGHT) {
                Intent intent = new Intent(VocabularyListActivity.this, AddEditVocabularyActivity.class);
                intent.putExtra("VOCAB_ID", vocabId);
                intent.putExtra("CATEGORY_ID", categoryId);
                startActivity(intent);
            }
            adapter.notifyItemChanged(position);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                View itemView = viewHolder.itemView;
                float height = (float) itemView.getBottom() - (float) itemView.getTop();
                float width = height / 3;

                Paint p = new Paint();

                if (dX > 0) {
                    p.setColor(ContextCompat.getColor(VocabularyListActivity.this, R.color.green));
                    RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                    c.drawRect(background, p);

                    p.setColor(Color.WHITE);
                    p.setTextSize(40);
                    p.setFakeBoldText(true);
                    c.drawText("Sửa", itemView.getLeft() + width / 2, itemView.getTop() + height / 2 + 10, p);

                } else if (dX < 0) {
                    p.setColor(ContextCompat.getColor(VocabularyListActivity.this, R.color.red));
                    RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                    c.drawRect(background, p);

                    p.setColor(Color.WHITE);
                    p.setTextSize(40);
                    p.setFakeBoldText(true);
                    c.drawText("Xóa", itemView.getRight() - width - 20, itemView.getTop() + height / 2 + 10, p);
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu_only, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setQueryHint("Tìm từ vựng...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterVocabularyList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterVocabularyList(newText);
                return true;
            }
        });

        return true;
    }

    private void filterVocabularyList(String query) {
        Cursor filteredCursor = vocabularyHelper.searchVocabulariesInCategory(categoryId, query);
        adapter.swapCursor(filteredCursor);
        int count = filteredCursor.getCount();
        tvVocabularyCount.setText("Tổng số: " + count + " từ");
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