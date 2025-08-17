package com.example.learningenglishapplication.Vocabulary;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;

import com.example.learningenglishapplication.R;
import com.example.learningenglishapplication.Data.model.Vocabulary;
import com.example.learningenglishapplication.Data.DatabaseHelper;
import com.example.learningenglishapplication.Data.DataHelper.VocabularyDataHelper;
import com.example.learningenglishapplication.Data.DataHelper.StatisticsDataHelper;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class FlashcardActivity extends AppCompatActivity {

    private List<Vocabulary> vocabularyList;
    private int currentCardIndex = 0;
    private long currentCategoryId;

    private static final int REQUEST_CODE_EDIT_VOCABULARY = 100;

    private FrameLayout flashcardContainer;
    private FrameLayout cardFront, cardBack;
    private TextView tvFlashcardWord, tvFlashcardPronunciation, tvFlashcardMeaning;
    private ImageView ivFlashcardImage;
    private ImageButton btnPlayAudio;

    private ImageView ivFavoriteFront, ivEditFront;
    private ImageView ivFavoriteBack, ivEditBack;

    private Button btnPrev, btnNext;
    private Button btnAgain, btnGood, btnEasy;

    private Vocabulary currentVocab;

    private TextToSpeech textToSpeech;
    private MediaPlayer mediaPlayer;

    private Animator flipOutAnimator;
    private Animator flipInAnimator;
    private boolean isFront = true;
    private boolean isFlipping = false;

    private GestureDetectorCompat gestureDetector;
    private final int SWIPE_THRESHOLD = 150;
    private final int SWIPE_VELOCITY_THRESHOLD = 150;

    private DatabaseHelper databaseHelper;
    private VocabularyDataHelper vocabularyDataHelper;
    private StatisticsDataHelper statisticsDataHelper;

    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        databaseHelper = new DatabaseHelper(this);
        vocabularyDataHelper = new VocabularyDataHelper(databaseHelper);
        statisticsDataHelper = new StatisticsDataHelper(this);

        userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getLong("userId", -1);

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        vocabularyList = (List<Vocabulary>) intent.getSerializableExtra("VOCAB_LIST");
        currentCategoryId = intent.getLongExtra("CATEGORY_ID", -1);

        if (vocabularyList == null || vocabularyList.isEmpty()) {
            Toast.makeText(this, "Không có từ vựng để ôn tập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        flashcardContainer = findViewById(R.id.fl_flashcard_container);
        cardFront = findViewById(R.id.ll_card_front);
        cardBack = findViewById(R.id.ll_card_back);
        tvFlashcardWord = findViewById(R.id.tv_flashcard_word);
        tvFlashcardPronunciation = findViewById(R.id.tv_flashcard_pronunciation);
        tvFlashcardMeaning = findViewById(R.id.tv_flashcard_meaning);
        ivFlashcardImage = findViewById(R.id.iv_flashcard_image);
        btnPlayAudio = findViewById(R.id.btn_play_audio);
        btnPrev = findViewById(R.id.btn_prev_card);
        btnNext = findViewById(R.id.btn_next_card);
        Button btnComplete = findViewById(R.id.btn_complete);
        btnComplete.setVisibility(View.GONE);

        btnAgain = findViewById(R.id.btn_again);
        btnGood = findViewById(R.id.btn_good);
        btnEasy = findViewById(R.id.btn_easy);

        ivFavoriteFront = findViewById(R.id.iv_favorite_front);
        ivEditFront = findViewById(R.id.iv_edit_front);
        ivFavoriteBack = findViewById(R.id.iv_favorite_back);
        ivEditBack = findViewById(R.id.iv_edit_back);

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
            }
        });

        loadAnimations();
        showCurrentCard();

        flashcardContainer.setOnClickListener(v -> flipCard());
        btnNext.setOnClickListener(v -> showNextCard());
        btnPrev.setOnClickListener(v -> showPreviousCard());
        btnPlayAudio.setOnClickListener(v -> playAudio());
        btnAgain.setOnClickListener(v -> handleAnswer(0));
        btnGood.setOnClickListener(v -> handleAnswer(1));
        btnEasy.setOnClickListener(v -> handleAnswer(2));

        gestureDetector = new GestureDetectorCompat(this, new MyGestureListener());
        flashcardContainer.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                resetCardState();
            }
            return true;
        });
    }

    private void loadAnimations() {
        float scale = getResources().getDisplayMetrics().density;
        flashcardContainer.setCameraDistance(8000 * scale);

        flipOutAnimator = AnimatorInflater.loadAnimator(this, R.animator.card_flip_out);
        flipInAnimator = AnimatorInflater.loadAnimator(this, R.animator.card_flip_in);

        flipOutAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isFront) {
                    cardFront.setVisibility(View.GONE);
                    cardBack.setVisibility(View.VISIBLE);
                } else {
                    cardBack.setVisibility(View.GONE);
                    cardFront.setVisibility(View.VISIBLE);
                }
                flipInAnimator.start();
                isFront = !isFront;
                isFlipping = false;
            }
        });
    }

    private void showCurrentCard() {
        if (!isFront) {
            isFront = true;
            cardBack.setVisibility(View.GONE);
            cardFront.setVisibility(View.VISIBLE);
        }

        if (currentCardIndex < vocabularyList.size()) {
            currentVocab = vocabularyList.get(currentCardIndex);
            tvFlashcardWord.setText(currentVocab.getWord());
            tvFlashcardPronunciation.setText(currentVocab.getPronunciation());
            tvFlashcardMeaning.setText(currentVocab.getMeaning());

            if (currentVocab.getImageUri() != null && !currentVocab.getImageUri().isEmpty()) {
                ivFlashcardImage.setVisibility(View.VISIBLE);
                ivFlashcardImage.setImageURI(Uri.parse(currentVocab.getImageUri()));
            } else {
                ivFlashcardImage.setVisibility(View.GONE);
            }

            updateFavoriteIcons(currentVocab.isFavorite());

            View.OnClickListener favoriteClickListener = v -> {
                boolean isCurrentlyFavorite = currentVocab.isFavorite();
                int newFavoriteStatus = isCurrentlyFavorite ? 0 : 1;

                vocabularyDataHelper.updateFavoriteStatus(currentVocab.getId(), newFavoriteStatus);
                currentVocab.setFavorite(!isCurrentlyFavorite);

                // Chỉ cập nhật biểu tượng yêu thích
                updateFavoriteIcons(currentVocab.isFavorite());
            };
            ivFavoriteFront.setOnClickListener(favoriteClickListener);
            ivFavoriteBack.setOnClickListener(favoriteClickListener);

            View.OnClickListener editClickListener = v -> {
                Intent editIntent = new Intent(FlashcardActivity.this, AddEditVocabularyActivity.class);
                editIntent.putExtra("VOCAB_ID", currentVocab.getId());
                editIntent.putExtra("CATEGORY_ID", currentCategoryId);
                startActivityForResult(editIntent, REQUEST_CODE_EDIT_VOCABULARY);
            };
            ivEditFront.setOnClickListener(editClickListener);
            ivEditBack.setOnClickListener(editClickListener);
        } else {
            Toast.makeText(this, "Bạn đã hoàn thành ôn tập!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateFavoriteIcons(boolean isFavorite) {
        if (isFavorite) {
            ivFavoriteFront.setImageResource(R.drawable.ic_favorite);
            ivFavoriteFront.setColorFilter(ContextCompat.getColor(this, R.color.red));
            ivFavoriteBack.setImageResource(R.drawable.ic_favorite);
            ivFavoriteBack.setColorFilter(ContextCompat.getColor(this, R.color.red));
        } else {
            ivFavoriteFront.setImageResource(R.drawable.ic_favorite_border);
            ivFavoriteFront.setColorFilter(null);
            ivFavoriteBack.setImageResource(R.drawable.ic_favorite_border);
            ivFavoriteBack.setColorFilter(null);
        }
    }

    private void playAudio() {
        if (currentVocab == null) return;
        String audio = currentVocab.getAudioUri();
        if (audio != null && !audio.isEmpty()) {
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }
                mediaPlayer = MediaPlayer.create(this, Uri.parse(audio));
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Không thể phát âm thanh", Toast.LENGTH_SHORT).show();
            }
        } else if (textToSpeech != null) {
            textToSpeech.speak(currentVocab.getWord(), TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void showNextCard() {
        if (currentCardIndex < vocabularyList.size() - 1) {
            currentCardIndex++;
            showCurrentCard();
        } else {
            Toast.makeText(this, "Bạn đã hoàn thành ôn tập!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showPreviousCard() {
        if (currentCardIndex > 0) {
            currentCardIndex--;
            showCurrentCard();
        } else {
            Toast.makeText(this, "Bạn đang ở từ đầu tiên!", Toast.LENGTH_SHORT).show();
        }
    }

    private void flipCard() {
        if (isFlipping) return;
        isFlipping = true;

        flipOutAnimator.setTarget(isFront ? cardFront : cardBack);
        flipInAnimator.setTarget(isFront ? cardBack : cardFront);
        flipOutAnimator.start();
    }

    private void markCardAsLearned(Vocabulary vocab) {
        vocabularyDataHelper.markVocabularyAsLearned(vocab.getId());
        statisticsDataHelper.logWordLearned(userId);
    }

    private void markCardAsUnlearned(Vocabulary vocab) {
        vocabularyDataHelper.markVocabularyAsUnlearned(vocab.getId());
        Toast.makeText(this, "Đã bỏ qua từ này", Toast.LENGTH_SHORT).show();
    }

    private long calculateNextReview(int box) {
        long now = System.currentTimeMillis();
        switch (box) {
            case 1:
                return now + 60 * 1000; // 1 minute
            case 2:
                return now + 5 * 60 * 1000; // 5 minutes
            case 3:
                return now + 60 * 60 * 1000; // 1 hour
            case 4:
                return now + 24 * 60 * 60 * 1000; // 1 day
            case 5:
                return now + 7 * 24 * 60 * 60 * 1000; // 1 week
            default:
                return now;
        }
    }

    private void handleAnswer(int quality) {
        if (currentCardIndex < vocabularyList.size()) {
            Vocabulary vocab = vocabularyList.get(currentCardIndex);
            int box = vocab.getBox();
            if (quality == 0) {
                box = 1;
                markCardAsUnlearned(vocab);
            } else if (quality == 1) {
                box = Math.min(5, box + 1);
                markCardAsLearned(vocab);
            } else {
                box = Math.min(5, box + 2);
                markCardAsLearned(vocab);
            }
            long nextReview = calculateNextReview(box);
            vocab.setBox(box);
            vocab.setNextReview(nextReview);
            vocabularyDataHelper.updateReviewSchedule(vocab.getId(), box, nextReview);

            currentCardIndex++;
            if (currentCardIndex < vocabularyList.size()) {
                showCurrentCard();
            } else {
                Toast.makeText(this, "Bạn đã hoàn thành ôn tập!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void handleSwipe(float diffX) {
        if (currentCardIndex < vocabularyList.size()) {
            if (diffX < 0) {
                handleAnswer(1);
            } else {
                handleAnswer(0);
            }
        }
        resetCardState();
    }

    private void handleDrag(float deltaX) {
        float newTranslationX = flashcardContainer.getTranslationX() + deltaX;
        flashcardContainer.setTranslationX(newTranslationX);

        FrameLayout currentCardView = isFront ? cardFront : cardBack;

        if (newTranslationX > 0) {
            currentCardView.setBackgroundResource(R.drawable.card_border_red);
        } else if (newTranslationX < 0) {
            currentCardView.setBackgroundResource(R.drawable.card_border_green);
        } else {
            resetCardBorderColor();
        }
    }

    private void resetCardState() {
        flashcardContainer.animate().translationX(0).setDuration(200).start();
        resetCardBorderColor();
    }

    private void resetCardBorderColor() {
        cardFront.setBackgroundResource(R.drawable.item_background);
        cardBack.setBackgroundResource(R.drawable.item_background);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_VOCABULARY && resultCode == RESULT_OK) {
            long editedVocabId = data.getLongExtra("UPDATED_VOCAB_ID", -1);
            if (editedVocabId != -1) {
                Vocabulary updatedVocab = vocabularyDataHelper.getVocabularyById(editedVocabId);
                if (updatedVocab != null && currentCardIndex < vocabularyList.size()) {
                    vocabularyList.set(currentCardIndex, updatedVocab);
                    showCurrentCard();
                    Toast.makeText(this, "Đã cập nhật từ vựng thành công!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Lỗi khi cập nhật từ vựng.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            flipCard();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                handleDrag(-distanceX);
                return true;
            }
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();

            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                handleSwipe(diffX);
            } else {
                resetCardState();
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }
}