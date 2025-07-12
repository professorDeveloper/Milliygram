package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.ui.ActionBar.Theme;

public class ProfileHeaderQuickActions extends LinearLayout {
    public float scaleFactor = 1.0f;
    public LinearLayout[] items;
    public float originalButtonHeight;
    private float originalIconSize;
    private float originalTextSize;

    public static final int MESSAGE_ITEM = 0;
    public static final int MUTE_ITEM = 1;
    public static final int CALL_ITEM = 2;
    public static final int VIDEO_CALL_ITEM = 3;

    private int totalWeightSum = 4;

    public ProfileHeaderQuickActions(Context context) {
        super(context);
        setOrientation(LinearLayout.HORIZONTAL);
        setWeightSum(4);
        setGravity(Gravity.CENTER_HORIZONTAL);


        // Store original dimensions
        originalIconSize = AndroidUtilities.dp(30);
        originalTextSize = 14; // sp
        originalButtonHeight = 0; // Will be calculated in onLayout

        addQuickActionButtons();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = AndroidUtilities.dp(500);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        if (widthSize > maxWidth) {
            widthSize = maxWidth;
        }

        // Let super measure with updated width
        int newWidthSpec = MeasureSpec.makeMeasureSpec(widthSize, widthMode);
        super.onMeasure(newWidthSpec, heightMeasureSpec);
    }

    private void addQuickActionButtons() {
        FrameLayout.LayoutParams rowParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        rowParams.leftMargin = AndroidUtilities.dp(10);
        rowParams.rightMargin = AndroidUtilities.dp(10);
        setLayoutParams(rowParams);

        items = new LinearLayout[4];
        for (int k = 0; k < 4; k++) {
            int svgThumbId = R.raw.message;
            String title = "";
            switch (k) {
                case MESSAGE_ITEM:
                    title = "Message";
                    svgThumbId = R.raw.message;
                    break;
                case MUTE_ITEM:
                    title = "Mute";
                    svgThumbId = R.raw.mute;
                    break;
                case CALL_ITEM:
                    title = "Call";
                    svgThumbId = R.raw.call;
                    break;
                case VIDEO_CALL_ITEM:
                    title = "Video";
                    svgThumbId = R.raw.video;
                    break;
            }

            SvgHelper.SvgDrawable svgThumb = getDrawable(svgThumbId);

            LinearLayout itemBtn = new LinearLayout(getContext());
            itemBtn.setPadding(
                    AndroidUtilities.dp(5),
                    AndroidUtilities.dp(5),
                    AndroidUtilities.dp(5),
                    AndroidUtilities.dp(5)
            );
            itemBtn.setOrientation(LinearLayout.VERTICAL);
            itemBtn.setGravity(Gravity.CENTER_HORIZONTAL);
            LayoutParams buttonParams = new LayoutParams(
                    0,
                    LayoutParams.WRAP_CONTENT,
                    1
            );
            buttonParams.leftMargin = AndroidUtilities.dp(4);
            buttonParams.rightMargin = AndroidUtilities.dp(4);
            itemBtn.setLayoutParams(buttonParams);


            Drawable background = AndroidUtilities.getRoundCornerDrawable(AndroidUtilities.multiplyAlphaComponent(Theme.getColor(Theme.key_windowBackgroundWhite), 0.2f));
            Drawable ripple = Theme.createSelectorDrawable(
                    AndroidUtilities.multiplyAlphaComponent(Theme.getColor(Theme.key_windowBackgroundWhite), 0.2f),
                    Theme.RIPPLE_MASK_ROUNDRECT_6DP,
                    30
            );

            Drawable[] layers = new Drawable[]{background, ripple};
            LayerDrawable layered = new LayerDrawable(layers);
            itemBtn.setBackground(layered);

            itemBtn.setClickable(true);
            itemBtn.setFocusable(true);

            ImageView btnIcon = new ImageView(getContext());
            btnIcon.setTag("quick_action_button_icon");
            btnIcon.setImageDrawable(svgThumb);
            LayoutParams itemIconParams = new LayoutParams(
                    AndroidUtilities.dp(30),
                    AndroidUtilities.dp(30)
            );
            btnIcon.setLayoutParams(itemIconParams);

            TextView itemTitle = new TextView(getContext());
            itemTitle.setText(title);
            itemTitle.setTextColor(AndroidUtilities.multiplyAlphaComponent(Theme.getColor(Theme.key_windowBackgroundWhite), 0.8f));
            itemTitle.setTypeface(AndroidUtilities.bold());
            itemTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            itemTitle.setGravity(Gravity.CENTER);
            LayoutParams itemTitleParams = new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
            );
            itemTitleParams.topMargin = AndroidUtilities.dp(2);
            itemTitle.setLayoutParams(itemTitleParams);

            itemBtn.addView(btnIcon);
            itemBtn.addView(itemTitle);

            addView(itemBtn);
            items[k] = itemBtn;
        }
    }

    @SuppressLint("Range")
    private SvgHelper.SvgDrawable getDrawable(int resource_id) {
        SvgHelper.SvgDrawable drawable = DocumentObject.getSvgThumb(resource_id, Theme.key_windowBackgroundWhite, 1.0f);
        drawable.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        drawable.setAlpha(700);
        return drawable;
    }

    public void setIsMute(boolean enabled) {
        ImageView iv = items[MUTE_ITEM].findViewWithTag("quick_action_button_icon");
        iv.setImageDrawable(getDrawable(enabled ? R.raw.mute : R.raw.unmute));
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = Math.max(0.0f, Math.min(1.0f, scaleFactor));
        updateLayout(this.scaleFactor);
        requestLayout(); // Trigger layout update
        invalidate(); // Trigger redraw
    }

    private void updateLayout(float scale) {
        if (items == null || originalButtonHeight == 0) return;

        for (LinearLayout item : items) {
            // Update button height
            ViewGroup.LayoutParams buttonParams = item.getLayoutParams();
            buttonParams.height = (int) (originalButtonHeight * scale);
            item.setLayoutParams(buttonParams);

            // Update icon size
            ImageView icon = item.findViewWithTag("quick_action_button_icon");
            if (icon != null) {
                LayoutParams iconParams = (LayoutParams) icon.getLayoutParams();
                iconParams.width = (int) (originalIconSize * scale);
                iconParams.height = (int) (originalIconSize * scale);
                icon.setLayoutParams(iconParams);
                icon.setScaleX(scale); // Additional scaling for smooth effect
                icon.setScaleY(scale);
            }

            // Update title size and opacity
            TextView title = (TextView) item.getChildAt(1);
            if (title != null) {
                title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, originalTextSize * scale);
                title.setAlpha(scale); // Fade out as scaleFactor approaches 0
            }

        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && originalButtonHeight == 0 && items[0] != null) {
            originalButtonHeight = items[0].getHeight();
            updateLayout(scaleFactor); // Apply initial scale
        }
    }

    public void hideItem(int item_index) {
        if(items[item_index] == null) { return;}
//        totalWeightSum--;
//        setWeightSum(totalWeightSum);
        items[item_index].setVisibility(View.GONE);
    }
}