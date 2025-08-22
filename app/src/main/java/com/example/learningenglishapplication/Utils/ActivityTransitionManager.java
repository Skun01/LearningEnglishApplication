package com.example.learningenglishapplication.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;

import com.example.learningenglishapplication.R;

/**
 * Lớp tiện ích để quản lý hiệu ứng chuyển tiếp giữa các màn hình
 * Cung cấp các phương thức để tạo hiệu ứng chuyển tiếp nhất quán trong toàn bộ ứng dụng
 */
public class ActivityTransitionManager {

    // Các kiểu hiệu ứng chuyển tiếp
    public static final int TRANSITION_SLIDE = 1;
    public static final int TRANSITION_FADE = 2;
    public static final int TRANSITION_ZOOM = 3;
    public static final int TRANSITION_SHARED_ELEMENT = 4;
    
    // Hiệu ứng mặc định cho ứng dụng
    public static final int DEFAULT_TRANSITION = TRANSITION_SLIDE;

    /**
     * Khởi chạy một Activity mới với hiệu ứng chuyển tiếp
     * @param currentActivity Activity hiện tại
     * @param intent Intent để khởi chạy Activity mới
     * @param transitionType Kiểu hiệu ứng chuyển tiếp
     */
    public static void startActivityWithTransition(Activity currentActivity, Intent intent, int transitionType) {
        // Thêm flag để xử lý back stack đúng cách
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
        // Áp dụng hiệu ứng chuyển tiếp
        if (transitionType == TRANSITION_SHARED_ELEMENT) {
            // Shared element transition sẽ được xử lý riêng
            currentActivity.startActivity(intent);
        } else {
            currentActivity.startActivity(intent);
            applyTransition(currentActivity, transitionType, false);
        }
    }

    /**
     * Kết thúc Activity hiện tại với hiệu ứng chuyển tiếp
     * @param activity Activity hiện tại
     * @param transitionType Kiểu hiệu ứng chuyển tiếp
     */
    public static void finishWithTransition(Activity activity, int transitionType) {
        activity.finish();
        applyTransition(activity, transitionType, true);
    }
    
    /**
     * Kết thúc Activity hiện tại với hiệu ứng chuyển tiếp mặc định
     * @param activity Activity hiện tại
     */
    public static void finishWithDefaultTransition(Activity activity) {
        finishWithTransition(activity, DEFAULT_TRANSITION);
    }

    /**
     * Áp dụng hiệu ứng chuyển tiếp
     * @param activity Activity hiện tại
     * @param transitionType Kiểu hiệu ứng chuyển tiếp
     * @param isReturning true nếu đang quay lại Activity trước đó, false nếu đang chuyển đến Activity mới
     */
    public static void applyTransition(Activity activity, int transitionType, boolean isReturning) {
        switch (transitionType) {
            case TRANSITION_SLIDE:
                if (isReturning) {
                    activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                } else {
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
            case TRANSITION_FADE:
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case TRANSITION_ZOOM:
                activity.overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                break;
            default:
                // Mặc định sử dụng hiệu ứng slide
                if (isReturning) {
                    activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                } else {
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
        }
    }
    
    /**
     * Khởi chạy một Activity mới với hiệu ứng chuyển tiếp mặc định
     * @param currentActivity Activity hiện tại
     * @param targetActivityClass Class của Activity đích
     */
    public static void startActivityWithDefaultTransition(Activity currentActivity, Class<?> targetActivityClass) {
        Intent intent = new Intent(currentActivity, targetActivityClass);
        startActivityWithTransition(currentActivity, intent, DEFAULT_TRANSITION);
    }
    
    /**
     * Khởi chạy một Activity mới với hiệu ứng chuyển tiếp mặc định
     * @param currentActivity Activity hiện tại
     * @param intent Intent để khởi chạy Activity mới
     */
    public static void startActivityWithDefaultTransition(Activity currentActivity, Intent intent) {
        startActivityWithTransition(currentActivity, intent, DEFAULT_TRANSITION);
    }
    
    /**
     * Khởi chạy một Activity mới với hiệu ứng chuyển tiếp slide
     * @param currentActivity Activity hiện tại
     * @param targetActivityClass Class của Activity đích
     */
    public static void startActivityWithSlideTransition(Activity currentActivity, Class<?> targetActivityClass) {
        Intent intent = new Intent(currentActivity, targetActivityClass);
        startActivityWithTransition(currentActivity, intent, TRANSITION_SLIDE);
    }
    
    /**
     * Khởi chạy một Activity mới với hiệu ứng chuyển tiếp fade
     * @param currentActivity Activity hiện tại
     * @param targetActivityClass Class của Activity đích
     */
    public static void startActivityWithFadeTransition(Activity currentActivity, Class<?> targetActivityClass) {
        Intent intent = new Intent(currentActivity, targetActivityClass);
        startActivityWithTransition(currentActivity, intent, TRANSITION_FADE);
    }
    
    /**
     * Khởi chạy một Activity mới với hiệu ứng chuyển tiếp zoom
     * @param currentActivity Activity hiện tại
     * @param targetActivityClass Class của Activity đích
     */
    public static void startActivityWithZoomTransition(Activity currentActivity, Class<?> targetActivityClass) {
        Intent intent = new Intent(currentActivity, targetActivityClass);
        startActivityWithTransition(currentActivity, intent, TRANSITION_ZOOM);
    }
    
    /**
     * Khởi chạy một Activity mới với hiệu ứng chuyển tiếp shared element
     * @param currentActivity Activity hiện tại
     * @param targetActivityClass Class của Activity đích
     * @param sharedElement View được chia sẻ giữa hai activity
     * @param sharedElementName Tên của shared element
     */
    public static void startActivityWithSharedElementTransition(
            @NonNull Activity currentActivity,
            @NonNull Class<?> targetActivityClass,
            @NonNull View sharedElement,
            @NonNull String sharedElementName) {
        Intent intent = new Intent(currentActivity, targetActivityClass);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                currentActivity, sharedElement, sharedElementName);
        currentActivity.startActivity(intent, options.toBundle());
    }
    
    /**
     * Khởi chạy một Activity mới với hiệu ứng chuyển tiếp shared element sử dụng Intent
     * @param currentActivity Activity hiện tại
     * @param intent Intent để khởi chạy Activity mới
     * @param sharedElement View được chia sẻ giữa hai activity
     * @param sharedElementName Tên của shared element
     */
    public static void startActivityWithSharedElementTransition(
            @NonNull Activity currentActivity,
            @NonNull Intent intent,
            @NonNull View sharedElement,
            @NonNull String sharedElementName) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                currentActivity, sharedElement, sharedElementName);
        currentActivity.startActivity(intent, options.toBundle());
    }
    
    /**
     * Khởi chạy một Activity mới với dữ liệu và hiệu ứng chuyển tiếp mặc định
     * @param currentActivity Activity hiện tại
     * @param targetActivityClass Class của Activity đích
     * @param extras Bundle chứa dữ liệu cần truyền
     */
    public static void startActivityWithExtras(
            @NonNull Activity currentActivity,
            @NonNull Class<?> targetActivityClass,
            @NonNull Bundle extras) {
        Intent intent = new Intent(currentActivity, targetActivityClass);
        intent.putExtras(extras);
        startActivityWithTransition(currentActivity, intent, DEFAULT_TRANSITION);
    }
    
    /**
     * Khởi chạy một Activity mới với Intent và hiệu ứng chuyển tiếp mặc định
     * @param currentActivity Activity hiện tại
     * @param intent Intent để khởi chạy Activity mới
     * @param extras Bundle chứa dữ liệu cần truyền
     */
    public static void startActivityWithExtras(
            @NonNull Activity currentActivity,
            @NonNull Intent intent,
            @NonNull Bundle extras) {
        intent.putExtras(extras);
        currentActivity.startActivity(intent);
        applyTransition(currentActivity, DEFAULT_TRANSITION, false);
    }
}