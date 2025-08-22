package com.example.learningenglishapplication.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningenglishapplication.Home.HomeActivity;
import com.example.learningenglishapplication.Quiz.QuizSetupActivity;
import com.example.learningenglishapplication.R;
import com.example.learningenglishapplication.category.CategoryManagementActivity;
import com.google.android.material.navigation.NavigationBarView;

/**
 * Lớp cơ sở cho các Activity chính có bottom navigation
 * Cung cấp các phương thức chung để quản lý bottom navigation và chuyển đổi giữa các activity chính
 */
public abstract class BaseMainActivity extends AppCompatActivity {

    protected NavigationBarView bottomNavigation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Thiết lập bottom navigation với item được chọn
     * @param selectedItemId ID của item được chọn
     */
    protected void setupBottomNavigation(int selectedItemId) {
        bottomNavigation = findViewById(R.id.bottom_navigation);
        
        // Đảm bảo rằng item được chọn đúng
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(selectedItemId);
        }

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Chuyển đến activity tương ứng với item được chọn
            if (itemId == R.id.nav_home) {
                if (this instanceof HomeActivity) {
                    return true;
                }
                ActivityTransitionManager.startMainActivityWithoutAnimation(this, HomeActivity.class);
                return true;
            } else if (itemId == R.id.nav_categories) {
                if (this instanceof CategoryManagementActivity) {
                    return true;
                }
                ActivityTransitionManager.startMainActivityWithoutAnimation(this, CategoryManagementActivity.class);
                return true;
            } else if (itemId == R.id.nav_quiz) {
                if (this instanceof QuizSetupActivity) {
                    return true;
                }
                ActivityTransitionManager.startMainActivityWithoutAnimation(this, QuizSetupActivity.class);
                return true;
            }
            return false;
        });
    }
    
    @Override
    public void onBackPressed() {
        // Nếu không phải là HomeActivity, quay về HomeActivity
        if (!(this instanceof HomeActivity)) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            ActivityTransitionManager.applyTransition(this, ActivityTransitionManager.TRANSITION_NONE, true);
        } else {
            super.onBackPressed();
        }
    }
}