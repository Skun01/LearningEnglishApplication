package com.example.learningenglishapplication.Quiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningenglishapplication.Data.model.Vocabulary;
import com.example.learningenglishapplication.R;
import com.example.learningenglishapplication.Utils.ActivityTransitionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchingQuizActivity extends AppCompatActivity {

    private TextView tvQuestionCounter, tvScore;
    private ProgressBar pbQuizProgress;
    private RecyclerView rvWords, rvMeanings;
    private Button btnCheckMatches;
    
    private MatchingWordAdapter wordAdapter;
    private MatchingMeaningAdapter meaningAdapter;
    
    private List<Vocabulary> quizQuestions;
    private List<MatchingItem> wordItems;
    private List<MatchingItem> meaningItems;
    
    private Map<Integer, Integer> selectedMatches; // Map từ vị trí từ đến vị trí nghĩa
    
    private int currentRound = 0;
    private int totalRounds = 0;
    private int score = 0;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_quiz);
        
        quizQuestions = (List<Vocabulary>) getIntent().getSerializableExtra("QUIZ_QUESTIONS");
        
        // Đảm bảo danh sách từ vựng được trộn ngẫu nhiên
        Collections.shuffle(quizQuestions);
        
        // Tính toán số vòng dựa trên số lượng từ vựng (tối đa 4 từ mỗi vòng)
        totalRounds = (int) Math.ceil(quizQuestions.size() / 4.0);
        
        initViews();
        setupListeners();
        loadNextRound();
    }

    private void initViews() {
        tvQuestionCounter = findViewById(R.id.tv_matching_question_counter);
        tvScore = findViewById(R.id.tv_matching_score);
        pbQuizProgress = findViewById(R.id.pb_matching_quiz_progress);
        rvWords = findViewById(R.id.rv_words);
        rvMeanings = findViewById(R.id.rv_meanings);
        btnCheckMatches = findViewById(R.id.btn_check_matches);

        // Khởi tạo RecyclerViews
        rvWords.setLayoutManager(new GridLayoutManager(this, 2));
        rvMeanings.setLayoutManager(new GridLayoutManager(this, 2));
        
        selectedMatches = new HashMap<>();
        wordItems = new ArrayList<>();
        meaningItems = new ArrayList<>();
        
        wordAdapter = new MatchingWordAdapter(wordItems, position -> onWordSelected(position));
        meaningAdapter = new MatchingMeaningAdapter(meaningItems, position -> onMeaningSelected(position));
        
        rvWords.setAdapter(wordAdapter);
        rvMeanings.setAdapter(meaningAdapter);
    }

    private void setupListeners() {
        btnCheckMatches.setOnClickListener(v -> checkMatches());
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

        // Xóa dữ liệu cũ
        wordItems.clear();
        meaningItems.clear();
        selectedMatches.clear();

        // Lấy 4 từ vựng cho vòng hiện tại (hoặc ít hơn nếu là vòng cuối)
        int startIndex = currentRound * 4;
        int endIndex = Math.min(startIndex + 4, quizQuestions.size());
        List<Vocabulary> currentRoundVocabs = quizQuestions.subList(startIndex, endIndex);

        // Tạo các item cho từ và nghĩa
        for (int i = 0; i < currentRoundVocabs.size(); i++) {
            Vocabulary vocab = currentRoundVocabs.get(i);
            // Sử dụng vị trí i làm originalPosition để theo dõi cặp từ-nghĩa
            wordItems.add(new MatchingItem(vocab.getWord(), i));
            meaningItems.add(new MatchingItem(vocab.getMeaning(), i));
        }

        // Trộn ngẫu nhiên cả từ và nghĩa để tăng độ khó
        Collections.shuffle(wordItems);
        Collections.shuffle(meaningItems);

        // Cập nhật adapters
        wordAdapter.notifyDataSetChanged();
        meaningAdapter.notifyDataSetChanged();
        
        // Reset trạng thái nút kiểm tra
        btnCheckMatches.setEnabled(false);
    }

    private void onWordSelected(int position) {
        // Bỏ chọn tất cả các từ khác
        for (int i = 0; i < wordItems.size(); i++) {
            wordItems.get(i).setSelected(i == position);
        }
        wordAdapter.notifyDataSetChanged();
        
        // Kiểm tra xem có nghĩa nào đang được chọn không
        boolean hasMeaningSelected = false;
        int selectedMeaningIndex = -1;
        
        for (int i = 0; i < meaningItems.size(); i++) {
            if (meaningItems.get(i).isSelected()) {
                hasMeaningSelected = true;
                selectedMeaningIndex = i;
                break;
            }
        }
        
        // Nếu có nghĩa đang được chọn, tạo kết nối
        if (hasMeaningSelected) {
            // Lưu trữ vị trí index trong danh sách, không phải originalPosition
            selectedMatches.put(position, selectedMeaningIndex);
            
            // Bỏ chọn tất cả
            for (MatchingItem item : wordItems) {
                item.setSelected(false);
            }
            for (MatchingItem item : meaningItems) {
                item.setSelected(false);
            }
            
            wordAdapter.notifyDataSetChanged();
            meaningAdapter.notifyDataSetChanged();
            
            // Kiểm tra xem đã ghép đủ các cặp chưa
            checkIfAllMatched();
        }
    }

    private void onMeaningSelected(int position) {
        // Bỏ chọn tất cả các nghĩa khác
        for (int i = 0; i < meaningItems.size(); i++) {
            meaningItems.get(i).setSelected(i == position);
        }
        meaningAdapter.notifyDataSetChanged();
        
        // Kiểm tra xem có từ nào đang được chọn không
        boolean hasWordSelected = false;
        int selectedWordIndex = -1;
        
        for (int i = 0; i < wordItems.size(); i++) {
            if (wordItems.get(i).isSelected()) {
                hasWordSelected = true;
                selectedWordIndex = i;
                break;
            }
        }
        
        // Nếu có từ đang được chọn, tạo kết nối
        if (hasWordSelected) {
            // Lưu trữ vị trí index trong danh sách, không phải originalPosition
            selectedMatches.put(selectedWordIndex, position);
            
            // Bỏ chọn tất cả
            for (MatchingItem item : wordItems) {
                item.setSelected(false);
            }
            for (MatchingItem item : meaningItems) {
                item.setSelected(false);
            }
            
            wordAdapter.notifyDataSetChanged();
            meaningAdapter.notifyDataSetChanged();
            
            // Kiểm tra xem đã ghép đủ các cặp chưa
            checkIfAllMatched();
        }
    }

    private void checkIfAllMatched() {
        // Nếu số lượng kết nối bằng số lượng từ, cho phép kiểm tra
        btnCheckMatches.setEnabled(selectedMatches.size() == wordItems.size());
    }

    private void checkMatches() {
        int correctMatches = 0;
        
        // Kiểm tra từng cặp kết nối
        for (Map.Entry<Integer, Integer> match : selectedMatches.entrySet()) {
            int wordIndex = match.getKey();
            int meaningIndex = match.getValue();
            
            // Lấy originalPosition của từ và nghĩa để so sánh
            int wordOriginalPosition = wordItems.get(wordIndex).getOriginalPosition();
            int meaningOriginalPosition = meaningItems.get(meaningIndex).getOriginalPosition();
            
            // Nếu originalPosition giống nhau, đó là kết nối đúng
            if (wordOriginalPosition == meaningOriginalPosition) {
                correctMatches++;
                
                // Đánh dấu cặp này là đúng
                wordItems.get(wordIndex).setCorrect(true);
                meaningItems.get(meaningIndex).setCorrect(true);
            } else {
                // Đánh dấu cặp này là sai
                wordItems.get(wordIndex).setCorrect(false);
                meaningItems.get(meaningIndex).setCorrect(false);
            }
        }
        
        // Cập nhật UI để hiển thị kết quả
        wordAdapter.notifyDataSetChanged();
        meaningAdapter.notifyDataSetChanged();
        
        // Cập nhật điểm
        score += correctMatches;
        tvScore.setText("Điểm: " + score);
        
        // Hiển thị thông báo
        Toast.makeText(this, "Đúng " + correctMatches + "/" + wordItems.size() + " cặp", Toast.LENGTH_SHORT).show();
        
        // Vô hiệu hóa nút kiểm tra
        btnCheckMatches.setEnabled(false);
        
        // Đợi một chút rồi chuyển sang vòng tiếp theo
        handler.postDelayed(() -> {
            currentRound++;
            loadNextRound();
        }, 2500); // 2.5 giây để người dùng có thời gian xem kết quả
    }

    private void showResults() {
        Intent intent = new Intent(this, QuizResultActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL_QUESTIONS", quizQuestions.size());
        ActivityTransitionManager.startActivityWithTransition(this, intent, ActivityTransitionManager.TRANSITION_FADE);
        ActivityTransitionManager.finishWithTransition(this, ActivityTransitionManager.TRANSITION_FADE);
    }
    
    @Override
    public void onBackPressed() {
        ActivityTransitionManager.finishWithTransition(this, ActivityTransitionManager.TRANSITION_SLIDE);
    }
    
    // Lớp đại diện cho một item trong trò chơi matching
    public static class MatchingItem {
        private String text;
        private int originalPosition; // Vị trí gốc để kiểm tra kết nối đúng
        private boolean isSelected;
        private Boolean isCorrect; // null: chưa kiểm tra, true: đúng, false: sai
        
        public MatchingItem(String text, int originalPosition) {
            this.text = text;
            this.originalPosition = originalPosition;
            this.isSelected = false;
            this.isCorrect = null;
        }
        
        public String getText() {
            return text;
        }
        
        public int getOriginalPosition() {
            return originalPosition;
        }
        
        public boolean isSelected() {
            return isSelected;
        }
        
        public void setSelected(boolean selected) {
            isSelected = selected;
        }
        
        public Boolean isCorrect() {
            return isCorrect;
        }
        
        public void setCorrect(boolean correct) {
            isCorrect = correct;
        }
        
        public int getPosition() {
            return originalPosition;
        }
    }
    
    // Interface cho sự kiện click
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}