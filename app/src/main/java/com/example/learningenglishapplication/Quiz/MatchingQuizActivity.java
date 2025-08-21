package com.example.learningenglishapplication.Quiz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningenglishapplication.Data.DataHelper.UserSettingDataHelper;
import com.example.learningenglishapplication.Data.model.Vocabulary;
import com.example.learningenglishapplication.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchingQuizActivity extends AppCompatActivity implements MatchingItemAdapter.OnItemClickListener {

    private TextView tvQuestionCounter, tvScore, tvInstructions;
    private ProgressBar pbQuizProgress;
    private RecyclerView rvMatchingItems;
    private Button btnNextRound;
    private ImageView btnBack;
    
    private MatchingItemAdapter adapter;
    
    private List<Vocabulary> quizQuestions;
    private List<MatchingItem> matchingItems;
    
    private int currentRound = 0;
    private int totalRounds = 0;
    private int score = 0;
    private int correctPairsInRound = 0;
    private long userId;
    private UserSettingDataHelper userSettingDataHelper;
    
    private MatchingItem selectedItem = null;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_quiz);
        
        quizQuestions = (List<Vocabulary>) getIntent().getSerializableExtra("QUIZ_QUESTIONS");
        userId = getIntent().getLongExtra("USER_ID", -1);
        userSettingDataHelper = new UserSettingDataHelper(this);
        
        // Đảm bảo danh sách từ vựng được trộn ngẫu nhiên
        Collections.shuffle(quizQuestions);
        
        // Tính toán số vòng dựa trên số lượng từ vựng (tối đa 5 cặp từ mỗi vòng)
        totalRounds = (int) Math.ceil(quizQuestions.size() / 5.0);
        
        initViews();
        setupListeners();
        loadNextRound();
    }

    private void initViews() {
        tvQuestionCounter = findViewById(R.id.tv_matching_question_counter);
        tvScore = findViewById(R.id.tv_matching_score);
        tvInstructions = findViewById(R.id.tv_matching_instructions);
        pbQuizProgress = findViewById(R.id.pb_matching_quiz_progress);
        rvMatchingItems = findViewById(R.id.rv_matching_items);
        btnNextRound = findViewById(R.id.btn_next_round);
        btnBack = findViewById(R.id.btn_back);

        // Khởi tạo RecyclerView
        rvMatchingItems.setLayoutManager(new GridLayoutManager(this, 2));
        
        matchingItems = new ArrayList<>();
        adapter = new MatchingItemAdapter(matchingItems, this);
        rvMatchingItems.setAdapter(adapter);
        
        btnNextRound.setVisibility(View.GONE);
        tvInstructions.setText("Ghép từng từ với nghĩa tương ứng bằng cách chạm vào chúng");
    }

    private void setupListeners() {
        btnNextRound.setOnClickListener(v -> {
            currentRound++;
            loadNextRound();
        });
        
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadNextRound() {
        if (currentRound >= totalRounds) {
            showResults();
            return;
        }

        // Cập nhật UI
        tvQuestionCounter.setText("Vòng " + (currentRound + 1) + "/" + totalRounds);
        tvScore.setText("Điểm: " + score);
        pbQuizProgress.setProgress((currentRound * 100) / totalRounds);
        btnNextRound.setVisibility(View.GONE);
        correctPairsInRound = 0;

        // Xóa dữ liệu cũ
        matchingItems.clear();

        // Lấy 5 từ vựng cho vòng hiện tại (hoặc ít hơn nếu là vòng cuối)
        int startIndex = currentRound * 5;
        int endIndex = Math.min(startIndex + 5, quizQuestions.size());
        List<Vocabulary> currentRoundVocabs = quizQuestions.subList(startIndex, endIndex);

        // Tạo các item cho từ và nghĩa
        for (int i = 0; i < currentRoundVocabs.size(); i++) {
            Vocabulary vocab = currentRoundVocabs.get(i);
            // Thêm từ và nghĩa với cùng một ID để ghép cặp
            matchingItems.add(new MatchingItem(vocab.getWord(), i, MatchingItem.TYPE_WORD));
            matchingItems.add(new MatchingItem(vocab.getMeaning(), i, MatchingItem.TYPE_MEANING));
        }

        // Trộn ngẫu nhiên các item để tăng độ khó
        Collections.shuffle(matchingItems);

        // Cập nhật adapter
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(MatchingItem item) {
        // Nếu item đã được ghép đúng, không làm gì cả
        if (item.isMatched()) {
            return;
        }
        
        if (selectedItem == null) {
            // Chưa có item nào được chọn, chọn item này
            selectedItem = item;
            selectedItem.setSelected(true);
            adapter.notifyDataSetChanged();
        } else {
            // Đã có item được chọn, kiểm tra xem có ghép đúng không
            if (selectedItem.getId() == item.getId() && selectedItem.getType() != item.getType()) {
                // Ghép đúng
                selectedItem.setMatched(true);
                item.setMatched(true);
                selectedItem.setSelected(false);
                
                // Tăng điểm và số cặp đúng
                score += 10;
                correctPairsInRound++;
                tvScore.setText("Điểm: " + score);
                
                // Kiểm tra xem đã hoàn thành vòng chưa
                int totalPairsInRound = matchingItems.size() / 2;
                if (correctPairsInRound >= totalPairsInRound) {
                    // Đã hoàn thành vòng
                    if (currentRound < totalRounds - 1) {
                        btnNextRound.setVisibility(View.VISIBLE);
                        tvInstructions.setText("Chúc mừng! Nhấn 'Tiếp tục' để sang vòng tiếp theo");
                    } else {
                        // Đây là vòng cuối
                        handler.postDelayed(this::showResults, 1000);
                    }
                }
            } else {
                // Ghép sai, bỏ chọn cả hai
                selectedItem.setSelected(false);
            }
            
            // Reset item đã chọn
            selectedItem = null;
            adapter.notifyDataSetChanged();
        }
    }

    private void showResults() {
        // Ghi nhận từ đã học
        for (int i = 0; i < quizQuestions.size(); i++) {
            userSettingDataHelper.logWordLearned(userId);
        }
        
        // Chuyển đến màn hình kết quả
        Intent intent = new Intent(this, QuizResultActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL_QUESTIONS", quizQuestions.size());
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        finish();
    }
    
    // Lớp MatchingItem để lưu trữ thông tin từ và nghĩa
    public static class MatchingItem {
        public static final int TYPE_WORD = 1;
        public static final int TYPE_MEANING = 2;
        
        private String text;
        private int id; // ID để ghép cặp
        private int type; // Loại: từ hoặc nghĩa
        private boolean isSelected;
        private boolean isMatched;
        
        public MatchingItem(String text, int id, int type) {
            this.text = text;
            this.id = id;
            this.type = type;
            this.isSelected = false;
            this.isMatched = false;
        }
        
        public String getText() {
            return text;
        }
        
        public int getId() {
            return id;
        }
        
        public int getType() {
            return type;
        }
        
        public boolean isSelected() {
            return isSelected;
        }
        
        public void setSelected(boolean selected) {
            isSelected = selected;
        }
        
        public boolean isMatched() {
            return isMatched;
        }
        
        public void setMatched(boolean matched) {
            isMatched = matched;
        }
    }
}