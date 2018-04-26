package it.sephiroth.android.library.bottomnavigation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import static android.util.Log.INFO;
import static android.util.Log.WARN;
import static it.sephiroth.android.library.bottomnavigation.MiscUtils.log;

/**
 * Created by crugnola on 11/2/16.
 * BottomNavigation
 */
@SuppressWarnings ("unused")
public class FloatingActionButtonBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {
    private static final String TAG = FloatingActionButtonBehavior.class.getSimpleName();
    private int navigationBarHeight = 0;

    public FloatingActionButtonBehavior() {
        super();
    }

    public FloatingActionButtonBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onAttachedToLayoutParams(@NonNull final CoordinatorLayout.LayoutParams lp) {
        // super.onAttachedToLayoutParams(lp);
    }

    @Override
    public boolean layoutDependsOn(final CoordinatorLayout parent, final FloatingActionButton child, final View dependency) {
        if (BottomNavigation.class.isInstance(dependency)) {
            return true;
        } else if (Snackbar.SnackbarLayout.class.isInstance(dependency)) {
            return true;
        }
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(
        final CoordinatorLayout parent, final FloatingActionButton child, final View dependency) {
        log(TAG, INFO, "onDependentViewChanged: " + dependency);

        final List<View> list = parent.getDependencies(child);
        int bottomMargin = ((ViewGroup.MarginLayoutParams) child.getLayoutParams()).bottomMargin;

        float t = 0;
        float t2 = 0;
        float t3 = 0;
        boolean result = false;

        for (View dep : list) {
            if (Snackbar.SnackbarLayout.class.isInstance(dep)) {
                t += dep.getTranslationY() - dep.getHeight();
                result = true;
            } else if (BottomNavigation.class.isInstance(dep)) {
                BottomNavigation navigation = (BottomNavigation) dep;
                t2 = navigation.getTranslationY() - navigation.getHeight() + bottomMargin;
                t += t2;
                result = true;

                if (navigationBarHeight > 0) {
                    if (!navigation.isExpanded()) {
                        child.hide();
                    } else {
                        child.show();
                    }
                }
            }
        }

        if (navigationBarHeight > 0 && t2 < 0) {
            t = Math.min(t2, t + navigationBarHeight);
        }

        child.setTranslationY(t);
        return result;
    }

    @Override
    public void onDependentViewRemoved(
        final CoordinatorLayout parent, final FloatingActionButton child, final View dependency) {
        super.onDependentViewRemoved(parent, child, dependency);
        log(TAG, WARN, "onDependentViewRemoved: " + dependency);
    }

    public void setNavigationBarHeight(final int height) {
        log(TAG, INFO, "setNavigationBarHeight: %d", height);
        this.navigationBarHeight = height;
    }
}
