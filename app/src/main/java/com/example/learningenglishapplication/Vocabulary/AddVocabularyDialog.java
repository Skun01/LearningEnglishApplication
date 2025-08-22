package com.example.learningenglishapplication.Vocabulary;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.example.learningenglishapplication.Data.DataHelper.StatisticsDataHelper;
import com.example.learningenglishapplication.Data.DataHelper.VocabularyDataHelper;
import com.example.learningenglishapplication.Data.DatabaseHelper;
import com.example.learningenglishapplication.R;
import com.google.android.material.appbar.MaterialToolbar;

public class AddVocabularyDialog extends DialogFragment {

    private EditText etWord, etMeaning;
    private Button btnSave;
    private VocabularyDataHelper vocabularyDataHelper;
    private StatisticsDataHelper statisticsDataHelper;
    private long categoryId;
    private long userId;
    private OnVocabularySavedListener listener;

    public interface OnVocabularySavedListener {
        void onVocabularySaved(long vocabId);
    }

    public static AddVocabularyDialog newInstance(long categoryId) {
        AddVocabularyDialog dialog = new AddVocabularyDialog();
        Bundle args = new Bundle();
        args.putLong("CATEGORY_ID", categoryId);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnVocabularySavedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " phải triển khai OnVocabularySavedListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        
        // Thiết lập animation
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_vocabulary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo các helper
        DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
        vocabularyDataHelper = new VocabularyDataHelper(databaseHelper);
        statisticsDataHelper = new StatisticsDataHelper(requireContext());

        // Ánh xạ các View
        etWord = view.findViewById(R.id.et_word);
        etMeaning = view.findViewById(R.id.et_meaning);
        btnSave = view.findViewById(R.id.btn_save_vocabulary);

        // Cài đặt Toolbar
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_add_vocabulary);
        toolbar.setNavigationOnClickListener(v -> dismiss());

        // Lấy dữ liệu từ Arguments và Shared Preferences
        if (getArguments() != null) {
            categoryId = getArguments().getLong("CATEGORY_ID", -1);
        }
        
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getLong("userId", -1);

        // Xử lý sự kiện click
        btnSave.setOnClickListener(v -> saveVocabulary());
    }

    private void saveVocabulary() {
        String word = etWord.getText().toString().trim();
        String meaning = etMeaning.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (word.isEmpty() || meaning.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ từ và nghĩa.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (categoryId == -1 || userId == -1) {
            Toast.makeText(requireContext(), "Thêm từ mới thất bại!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra từ vựng đã tồn tại chưa
        if (vocabularyDataHelper.isVocabularyExists(categoryId, word, meaning)) {
            Toast.makeText(requireContext(), "Từ vựng này đã tồn tại trong thể loại!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Thêm từ vựng mới (không có phiên âm)
        long returnedVocabId = vocabularyDataHelper.addVocabulary(userId, categoryId, word, "", meaning);

        if (returnedVocabId != -1) {
            statisticsDataHelper.logWordLearned(userId);
            Toast.makeText(requireContext(), "Đã thêm từ mới!", Toast.LENGTH_SHORT).show();
            listener.onVocabularySaved(returnedVocabId);
            dismiss();
        } else {
            Toast.makeText(requireContext(), "Thêm từ mới thất bại.", Toast.LENGTH_SHORT).show();
        }
    }
}