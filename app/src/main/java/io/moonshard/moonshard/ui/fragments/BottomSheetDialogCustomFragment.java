package io.moonshard.moonshard.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import moxy.MvpAppCompatDialogFragment;

public class BottomSheetDialogCustomFragment extends MvpAppCompatDialogFragment {

    /**
     * Tracks if we are waiting for a dismissAllowingStateLoss or a regular dismiss once the
     * BottomSheet is hidden and onStateChanged() is called.
     */
    private boolean waitingForDismissAllowingStateLoss;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(getContext(), getTheme());
    }

    @Override
    public void dismiss() {
        if (!tryDismissWithAnimation(false)) {
            super.dismiss();
        }
    }

    @Override
    public void dismissAllowingStateLoss() {
        if (!tryDismissWithAnimation(true)) {
            super.dismissAllowingStateLoss();
        }
    }

    /**
     * Tries to dismiss the dialog fragment with the bottom sheet animation. Returns true if possible,
     * false otherwise.
     */
    private boolean tryDismissWithAnimation(boolean allowingStateLoss) {
        Dialog baseDialog = getDialog();
        if (baseDialog instanceof BottomSheetDialog) {
            BottomSheetDialog dialog = (BottomSheetDialog) baseDialog;
            BottomSheetBehavior<?> behavior = dialog.getBehavior();
            if (behavior.isHideable() && dialog.getDismissWithAnimation()) {
                dismissWithAnimation(behavior, allowingStateLoss);
                return true;
            }
        }

        return false;
    }

    private void dismissWithAnimation(
            @NonNull BottomSheetBehavior<?> behavior, boolean allowingStateLoss) {
        waitingForDismissAllowingStateLoss = allowingStateLoss;

        if (behavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            dismissAfterAnimation();
        } else {
            if (getDialog() instanceof BottomSheetDialog) {
               // ((BottomSheetDialog) getDialog()).removeDefaultCallback();
            }
            behavior.addBottomSheetCallback(new BottomSheetDismissCallback());
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    private void dismissAfterAnimation() {
        if (waitingForDismissAllowingStateLoss) {
            super.dismissAllowingStateLoss();
        } else {
            super.dismiss();
        }
    }



    private class BottomSheetDismissCallback extends BottomSheetBehavior.BottomSheetCallback {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismissAfterAnimation();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
    }
}
