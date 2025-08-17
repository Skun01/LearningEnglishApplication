package com.example.learningenglishapplication.Utils;

import android.app.Activity;
import android.content.Intent;

import com.example.learningenglishapplication.R;

/**
 * Lớp tiện ích để quản lý hiệu ứng chuyển tiếp giữa các màn hình
 */
public class ActivityTransitionManager {

    // Các kiểu hiệu ứng chuyển tiếp
    public static final int TRANSITION_SLIDE = 1;
    public static final int TRANSITION_FADE = 2;
    public static final int TRANSITION_ZOOM = 3;

    /**
     * Khởi chạy một Activity mới với hiệu ứng chuyển tiếp
     * @param currentActivity Activity hiện tại
     * @param intent Intent để khởi chạy Activity mới
     * @param transitionType Kiểu hiệu ứng chuyển tiếp
     */
    public static void startActivityWithTransition(Activity currentActivity, Intent intent, int transitionType) {
        currentActivity.startActivity(intent);
        applyTransition(currentActivity, transitionType, false);
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
     * Áp dụng hiệu ứng chuyển tiếp
     * @param activity Activity hiện tại
     * @param transitionType Kiểu hiệu ứng chuyển tiếp
     * @param isReturning true nếu đang quay lại Activity trước đó, false nếu đang chuyển đến Activity mới
     */
    private static void applyTransition(Activity activity, int transitionType, boolean isReturning) {
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
        }
    }
}