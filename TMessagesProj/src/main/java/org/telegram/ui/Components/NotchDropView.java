package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import org.telegram.messenger.AndroidUtilities;

public class NotchDropView extends View {
    private FrameLayout.LayoutParams notchViewParams;
    public Path path;
    private Bitmap contentBitmap;
    private int notchViewWidth = 85;

    float heightFactor = 0;
    private float verticalControlFactor=0,
                  verticalControlFactor2=0,
                  horizontalControlFactor=0;

    public NotchDropView(Context context) {
        super(context);

        notchViewParams = new FrameLayout.LayoutParams(
                AndroidUtilities.dp(notchViewWidth),
                AndroidUtilities.dp((int)(notchViewWidth*0.555))
        );
        notchViewParams.gravity = Gravity.TOP|Gravity.CENTER_HORIZONTAL;
        notchViewParams.topMargin = 0;
        setLayoutParams(notchViewParams);

        init();
    }

    public NotchDropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setContentBitmap(Bitmap bitmap) {
        this.contentBitmap = bitmap;
        invalidate();
    }

    private void init() {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        path = new Path();
        path.reset();

        float w = getWidth();
        float h = getHeight();

        float scaleX = w / 90f;
        float scaleY = h / 50f;


        path.moveTo(0f * scaleX, 0f * scaleY);


        if(heightFactor <= 0.3){
            verticalControlFactor = heightFactor / 0.3f;
        }
        if(heightFactor <= 0.3){
            horizontalControlFactor = heightFactor / 0.3f;
        }
        if(horizontalControlFactor>0.9){
            horizontalControlFactor = 1;
        }
        if(verticalControlFactor>0.9){
            verticalControlFactor = 1;
        }
        if(heightFactor >= 0.17){
            verticalControlFactor2 = (heightFactor-0.17f) / 0.83f;
        }else{
            verticalControlFactor2 = 0;
        }
        if(verticalControlFactor2>0.9){
            verticalControlFactor2 = 1;
        }

        this.setTranslationY(-verticalControlFactor2*h*1.2f);

        // C10 0 17 5 20 20
        path.cubicTo(
                AndroidUtilities.lerp(80f*scaleX, 10f*scaleX, horizontalControlFactor), 0f * scaleY,
                17f * scaleX, 5f * scaleY,
                20f * scaleX, AndroidUtilities.lerp(30f*scaleY, 20f*scaleY, verticalControlFactor)
        );

        // C26 50 64 50 70 20
        path.cubicTo(
                26f * scaleX, 50f * scaleY,
                64f * scaleX, 50f * scaleY,
                70f * scaleX, AndroidUtilities.lerp(30f*scaleY, 20f*scaleY, verticalControlFactor)
        );

        // C73 5 80 0 90 0
        path.cubicTo(
                73f * scaleX, 5f * scaleY,
                AndroidUtilities.lerp(10f*scaleX, 80f*scaleX, horizontalControlFactor), 0f * scaleY,
                90f * scaleX, 0f * scaleY
        );

        canvas.save();
        canvas.clipPath(path);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        if(contentBitmap != null)
            canvas.drawBitmap(contentBitmap, null, new Rect(0, 0, getWidth(), getHeight()), paint);
        canvas.restore();
    }


    public void setAnimatorFactor(float y) {
        heightFactor = y;
        invalidate();
    }
}