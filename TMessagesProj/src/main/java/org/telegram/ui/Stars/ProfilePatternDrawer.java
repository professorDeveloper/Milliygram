package org.telegram.ui.Stars;

import static org.telegram.messenger.AndroidUtilities.dpf2;
import static org.telegram.messenger.Utilities.random;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import org.telegram.messenger.AndroidUtilities;

public class ProfilePatternDrawer {
    private static float moveFactor = 0.0f;

    public static void setMoveFactor(float factor) {
        moveFactor = Math.max(0.0f, Math.min(1.0f, factor)); // Clamp between 0 and 1
    }

    public static void drawProfilePatternCircle(Canvas canvas, Drawable pattern, float w, float h, float alpha, float full) {
        if (alpha <= 0.0f || pattern == null) return;

        // Canvas center
        final float centerX = w / 2.0f;
        final float centerY = h / 2.0f;

        // Define empty circle radius (20% of minimum dimension)
        final float minDimension = Math.min(w, h) / AndroidUtilities.density; // Convert to dp
        final float emptyRadius = minDimension * 0.6f; // Radius in dp

        // Number of icons (scaled by full, minimum 8, maximum 40)
        final int baseCount = 20;
        final int iconCount = Math.max(8, Math.min(40, (int) (baseCount * full)));

        // Minimum distance between icon centers (in dp)
        final float minDistance = 30.0f;

        // Vertical scaling factor to create ovals (horizontal > vertical)
        final float verticalScale = 0.7f; // Adjust to make vertical diameter smaller than horizontal

        // Predefined angles for icon positions (in radians)
        float[] targetAngles = {
                0.0f,        // 0 degrees (top)
                45.0f * (float)Math.PI / 180.0f,  // 45 degrees
                90.0f * (float)Math.PI / 180.0f,  // 90 degrees
                135.0f * (float)Math.PI / 180.0f, // 135 degrees
                180.0f * (float)Math.PI / 180.0f, // 180 degrees
                225.0f * (float)Math.PI / 180.0f, // 225 degrees
                270.0f * (float)Math.PI / 180.0f, // 270 degrees
                315.0f * (float)Math.PI / 180.0f  // 315 degrees
        };

        // Radii for two ovals
        final float innerRadius = minDimension * 0.50f;
        final float outerRadius = minDimension * 0.75f;

        // Adjust alpha and position based on moveFactor
        float adjustedAlpha = alpha * (1.0f - moveFactor);
        float innerRadiusScale = 1.0f - (moveFactor * 1.5f); // Faster movement for inner oval
        float outerRadiusScale = 1.0f - moveFactor; // Normal movement for outer oval
        float upwardOffset = moveFactor * dpf2(70.0f); // Upward movement

        // Draw inner oval
        int effectiveIconCountInner = Math.min(iconCount / 2, targetAngles.length);
        for (int i = 0; i < effectiveIconCountInner; i++) {
            float angle = targetAngles[i];
            // Adjusted coordinates for oval shape and moveFactor
            float x = (float) (innerRadius * innerRadiusScale * Math.cos(angle)); // Horizontal stretch
            float y = (float) (innerRadius * innerRadiusScale * Math.sin(angle) * verticalScale); // Vertical compression
            float size = 15.0f + random.nextFloat() * 10.0f;
            float thisAlpha = (0.25f + random.nextFloat() * 0.3f) * (1.0f - moveFactor);

            pattern.setBounds(
                    (int) (centerX + dpf2(x) - dpf2(size) / 2.0f),
                    (int) (centerY + dpf2(y) - dpf2(size) / 2.0f - upwardOffset), // Apply upward offset
                    (int) (centerX + dpf2(x) + dpf2(size) / 2.0f),
                    (int) (centerY + dpf2(y) + dpf2(size) / 2.0f - upwardOffset)  // Apply upward offset
            );
            pattern.setAlpha((int) (0xFF * adjustedAlpha * thisAlpha * full));
            pattern.draw(canvas);
        }

        // Draw outer oval
        int effectiveIconCountOuter = Math.min(iconCount - effectiveIconCountInner, targetAngles.length);
        for (int i = 0; i < effectiveIconCountOuter; i++) {
            float angle = targetAngles[i];
            // Adjusted coordinates for oval shape and moveFactor
            float x = (float) (outerRadius * outerRadiusScale * Math.cos(angle)); // Horizontal stretch
            float y = (float) (outerRadius * outerRadiusScale * Math.sin(angle) * verticalScale); // Vertical compression
            float size = 15.0f + random.nextFloat() * 10.0f;
            float thisAlpha = (0.15f + random.nextFloat() * 0.3f) * (1.0f - moveFactor);

            pattern.setBounds(
                    (int) (centerX + dpf2(x) - dpf2(size) / 2.0f),
                    (int) (centerY + dpf2(y) - dpf2(size) / 2.0f - upwardOffset), // Apply upward offset
                    (int) (centerX + dpf2(x) + dpf2(size) / 2.0f),
                    (int) (centerY + dpf2(y) + dpf2(size) / 2.0f - upwardOffset)  // Apply upward offset
            );
            pattern.setAlpha((int) (0xFF * adjustedAlpha * thisAlpha * full));
            pattern.draw(canvas);
        }
    }
}