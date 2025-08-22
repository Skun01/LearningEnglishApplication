package com.example.learningenglishapplication.Quiz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningenglishapplication.Data.DataHelper.UserSettingDataHelper;
import com.example.learningenglishapplication.Data.model.Vocabulary;
import com.example.learningenglishapplication.R;
import com.example.learningenglishapplication.Utils.ActivityTransitionManager;
import com.example.learningenglishapplication.Utils.BaseChildActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchingQuizActivity extends BaseChildActivity implements MatchingItemAdapter.OnItemClickListener {

    private TextView tvQuestionCounter, tvScore, tvInstructions, tvTimer;
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
    private int totalPairsInRound = 0;
    private long userId;
    private UserSettingDataHelper userSettingDataHelper;
    
    private MatchingItem selectedItem = null;
    private MatchingItem lastIncorrectMatch = null;
    private final Handler handler = new Handler();
    
    // Biến đếm thời gian
    private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private final Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_quiz);
        
        try {
            // Thiết lập toolbar với tiêu đề "Quiz Ghép Cặp"
            setupToolbar(getString(R.string.matching_quiz_title));
            
            // Kiểm tra và lấy dữ liệu từ Intent
            if (getIntent() != null && getIntent().hasExtra("QUIZ_QUESTIONS")) {
                try {
                    Object serializedObject = getIntent().getSerializableExtra("QUIZ_QUESTIONS");
                    if (serializedObject instanceof List<?>) {
                        List<?> list = (List<?>) serializedObject;
                        if (!list.isEmpty() && list.get(0) instanceof Vocabulary) {
                            quizQuestions = (List<Vocabulary>) list;
                        } else {
                            throw new ClassCastException("Dữ liệu không phải danh sách Vocabulary");
                        }
                    } else {
                        throw new ClassCastException("Dữ liệu không phải danh sách");
                    }
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Lỗi định dạng dữ liệu bài kiểm tra", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                
                userId = getIntent().getLongExtra("USER_ID", -1);
                
                if (quizQuestions == null || quizQuestions.isEmpty()) {
                    Toast.makeText(this, "Không có câu hỏi cho bài kiểm tra này", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                
                userSettingDataHelper = new UserSettingDataHelper(this);
                
                // Đảm bảo danh sách từ vựng được trộn ngẫu nhiên
                Collections.shuffle(quizQuestions);
                
                // Tính toán số vòng dựa trên số lượng từ vựng (tối đa 5 cặp từ mỗi vòng)
                totalRounds = (int) Math.ceil(quizQuestions.size() / 5.0);
                
                initViews();
                setupListeners();
                loadNextRound();
            } else {
                Toast.makeText(this, "Không thể tải dữ liệu bài kiểm tra", Toast.LENGTH_LONG).show();
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Đã xảy ra lỗi khi khởi tạo bài kiểm tra", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initViews() {
        tvQuestionCounter = findViewById(R.id.tv_matching_question_counter);
        tvScore = findViewById(R.id.tv_matching_score);
        tvInstructions = findViewById(R.id.tv_matching_instructions);
        pbQuizProgress = findViewById(R.id.pb_matching_quiz_progress);
        rvMatchingItems = findViewById(R.id.rv_matching_items);
        btnNextRound = findViewById(R.id.btn_next_round);
        btnBack = findViewById(R.id.btn_back);
        tvTimer = findViewById(R.id.tv_timer);

        // Khởi tạo RecyclerView
        rvMatchingItems.setLayoutManager(new GridLayoutManager(this, 2));
        
        matchingItems = new ArrayList<>();
        adapter = new MatchingItemAdapter(matchingItems, this);
        rvMatchingItems.setAdapter(adapter);
        
        btnNextRound.setVisibility(View.GONE);
        tvInstructions.setText("Ghép từng từ với nghĩa tương ứng bằng cách chạm vào chúng");
        
        // Khởi tạo timer
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
                int seconds = (int) (timeInMilliseconds / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                tvTimer.setText(String.format("%d:%02d", minutes, seconds));
                timerHandler.postDelayed(this, 1000);
            }
        };
    }

    private void setupListeners() {
        btnNextRound.setOnClickListener(v -> {
            currentRound++;
            loadNextRound();
        });
        
        btnBack.setOnClickListener(v -> {
            // Dừng timer trước khi thoát
            timerHandler.removeCallbacks(timerRunnable);
            onBackPressed();
        });
    }

    private void loadNextRound() {
        try {
            if (currentRound >= totalRounds) {
                showResults();
                return;
            }

            // Bắt đầu đếm thời gian nếu là vòng đầu tiên
            if (currentRound == 0) {
                startTime = SystemClock.uptimeMillis();
                timerHandler.postDelayed(timerRunnable, 0);
            }

            // Xóa dữ liệu cũ
            matchingItems.clear();
            correctPairsInRound = 0;
            lastIncorrectMatch = null;
            selectedItem = null;

            // Kiểm tra danh sách câu hỏi
            if (quizQuestions == null || quizQuestions.isEmpty()) {
                Toast.makeText(this, "Không có câu hỏi cho bài kiểm tra này", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            try {
                // Lấy 5 từ vựng cho vòng hiện tại (hoặc ít hơn nếu là vòng cuối)
                int startIndex = currentRound * 5;
                if (startIndex >= quizQuestions.size()) {
                    Toast.makeText(this, "Lỗi tính toán vòng câu hỏi", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                
                int endIndex = Math.min(startIndex + 5, quizQuestions.size());
                List<Vocabulary> currentRoundVocabs = quizQuestions.subList(startIndex, endIndex);

                // Tạo các item cho từ và nghĩa
                for (int i = 0; i < currentRoundVocabs.size(); i++) {
                    Vocabulary vocab = currentRoundVocabs.get(i);
                    if (vocab != null && vocab.getWord() != null && vocab.getMeaning() != null) {
                        // Thêm từ và nghĩa với cùng một ID để ghép cặp
                        matchingItems.add(new MatchingItem(vocab.getWord(), i, MatchingItem.TYPE_WORD));
                        matchingItems.add(new MatchingItem(vocab.getMeaning(), i, MatchingItem.TYPE_MEANING));
                    } else {
                        Toast.makeText(this, "Phát hiện từ vựng không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi khi tải dữ liệu từ vựng: " + e.getMessage(), Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            // Kiểm tra xem có item nào được tạo không
            if (matchingItems.isEmpty()) {
                Toast.makeText(this, "Không thể tạo câu hỏi cho vòng này", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            // Trộn ngẫu nhiên các item để tăng độ khó
            Collections.shuffle(matchingItems);
            
            // Cập nhật số cặp trong vòng hiện tại
            totalPairsInRound = matchingItems.size() / 2; // Mỗi cặp gồm 1 từ và 1 nghĩa

            // Cập nhật UI
            tvQuestionCounter.setText(correctPairsInRound + "/" + totalPairsInRound);
            tvScore.setText("Điểm: " + score);
            pbQuizProgress.setProgress((correctPairsInRound * 100) / totalPairsInRound);
            btnNextRound.setVisibility(View.GONE);

            // Cập nhật adapter
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Đã xảy ra lỗi khi tải câu hỏi", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onItemClick(MatchingItem item) {
        try {
            // Kiểm tra item có hợp lệ không
            if (item == null) {
                Toast.makeText(this, "Lỗi: Item không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Nếu item đã được ghép đúng, không làm gì cả
            if (item.isMatched()) {
                return;
            }
            
            // Xóa trạng thái ghép sai trước đó
            if (lastIncorrectMatch != null) {
                lastIncorrectMatch.setIncorrect(false);
                lastIncorrectMatch = null;
            }
            
            if (selectedItem == null) {
                // Chưa có item nào được chọn, chọn item này
                selectedItem = item;
                selectedItem.setSelected(true);
                adapter.notifyDataSetChanged();
            } else {
                // Kiểm tra xem item được chọn có phải là item hiện tại không
                if (selectedItem == item) {
                    // Bỏ chọn item nếu người dùng nhấn lại
                    selectedItem.setSelected(false);
                    selectedItem = null;
                    adapter.notifyDataSetChanged();
                    return;
                }
                
                // Đã có item được chọn, kiểm tra xem có ghép đúng không
                if (selectedItem.getId() == item.getId() && selectedItem.getType() != item.getType()) {
                    // Ghép đúng
                    selectedItem.setMatched(true);
                    item.setMatched(true);
                    selectedItem.setSelected(false);
                    
                    // Tăng điểm và số cặp đúng
                    score += 10;
                    correctPairsInRound++;
                    
                    // Cập nhật UI
                    if (tvScore != null) {
                        tvScore.setText("Điểm: " + score);
                    }
                    
                    // Cập nhật hiển thị số cặp đã ghép đúng
                    if (tvQuestionCounter != null && pbQuizProgress != null) {
                        tvQuestionCounter.setText(correctPairsInRound + "/" + totalPairsInRound);
                        pbQuizProgress.setProgress((correctPairsInRound * 100) / totalPairsInRound);
                    }
                    
                    // Kiểm tra xem đã hoàn thành vòng chưa
                    if (correctPairsInRound >= totalPairsInRound) {
                        // Dừng timer
                        timerHandler.removeCallbacks(timerRunnable);
                        
                        // Đã hoàn thành vòng
                        if (currentRound < totalRounds - 1) {
                            if (btnNextRound != null && tvInstructions != null) {
                                btnNextRound.setVisibility(View.VISIBLE);
                                tvInstructions.setText("Chúc mừng! Nhấn 'Tiếp tục' để sang vòng tiếp theo");
                            }
                        } else {
                            // Đây là vòng cuối
                            handler.postDelayed(this::showResults, 1000);
                        }
                    }
                } else {
                    // Ghép sai, đánh dấu cả hai item là ghép sai
                    selectedItem.setIncorrect(true);
                    item.setIncorrect(true);
                    lastIncorrectMatch = item; // Lưu lại item ghép sai gần nhất
                    selectedItem.setSelected(false);
                }
                
                // Reset item đã chọn
                selectedItem = null;
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MatchingQuizActivity.this, "Đã xảy ra lỗi khi xử lý lựa chọn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            // Reset trạng thái
            if (selectedItem != null) {
                selectedItem.setSelected(false);
                selectedItem = null;
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void showResults() {
        try {
            // Dừng timer
            timerHandler.removeCallbacks(timerRunnable);
            
            // Kiểm tra dữ liệu trước khi xử lý
            if (quizQuestions == null) {
                Toast.makeText(this, "Dữ liệu bài kiểm tra không hợp lệ", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            
            // Ghi nhận từ đã học
            if (userSettingDataHelper != null && userId > 0) {
                for (int i = 0; i < quizQuestions.size(); i++) {
                    userSettingDataHelper.logWordLearned(userId);
                }
            }
            
            // Tính thời gian hoàn thành (đổi từ milliseconds sang seconds)
            int completionTimeSeconds = (int) (timeInMilliseconds / 1000);
            
            try {
                // Chuyển đến màn hình kết quả ghép từ
                Intent intent = new Intent(this, MatchingQuizResultActivity.class);
                intent.putExtra("SCORE", score);
                intent.putExtra("TOTAL_QUESTIONS", quizQuestions.size());
                intent.putExtra("COMPLETION_TIME", completionTimeSeconds);
                intent.putExtra("USER_ID", userId);
                
                // Sử dụng ActivityTransitionManager để khởi chạy activity với hiệu ứng chuyển đổi
                try {
                    // Tạo shared element transition cho score
                    View scoreView = findViewById(R.id.tv_matching_score);
                    if (scoreView != null) {
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                this,
                                Pair.create(scoreView, "score_transition")
                        );
                        startActivity(intent, options.toBundle());
                    } else {
                        // Nếu không tìm thấy view, khởi chạy bình thường
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // Nếu không thể sử dụng transition, khởi chạy bình thường
                    startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Không thể mở màn hình kết quả: " + e.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Đã xảy ra lỗi khi hiển thị kết quả", Toast.LENGTH_LONG).show();
            // Trở về màn hình trước đó nếu có lỗi
            finish();
        }
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
        private boolean isIncorrect; // Trạng thái ghép sai
        
        public MatchingItem(String text, int id, int type) {
            this.text = text;
            this.id = id;
            this.type = type;
            this.isSelected = false;
            this.isMatched = false;
            this.isIncorrect = false;
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
        
        public boolean isIncorrect() {
            return isIncorrect;
        }
        
        public void setIncorrect(boolean incorrect) {
            isIncorrect = incorrect;
        }
    }
    
    @Override
    public void onBackPressed() {
        // Dừng timer trước khi thoát
        timerHandler.removeCallbacks(timerRunnable);
        ActivityTransitionManager.finishWithDefaultTransition(this);
    }
}