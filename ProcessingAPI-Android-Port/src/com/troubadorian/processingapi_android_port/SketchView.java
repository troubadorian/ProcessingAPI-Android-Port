package com.troubadorian.processingapi_android_port;

import java.util.Random;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;


/**
 * This view implements the drawing canvas.
 *
 * It handles all of the input events and drawing functions.
 */

public class SketchView extends View {
    enum PaintMode {
        Draw,
        Splat,
        Erase,
    }
    /** Colors to cycle through. */
    static final int[] COLORS = new int[] {
        Color.WHITE, Color.RED, Color.YELLOW, Color.GREEN,
        Color.CYAN, Color.BLUE, Color.MAGENTA,
    };

    /** Background color. */
    static final int BACKGROUND_COLOR = Color.BLACK;

    /** The view responsible for drawing the window. */
    SketchView mView;

    /** Is fading mode enabled? */
    boolean mFading;

    /** The index of the current color to use. */
    int mColorIndex;
    private static final int FADE_ALPHA = 0x06;
    private static final int MAX_FADE_STEPS = 256 / FADE_ALPHA + 4;
    private static final int TRACKBALL_SCALE = 10;

    private static final int SPLAT_VECTORS = 40;

    private final Random mRandom = new Random();
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private final Paint mPaint;
    private final Paint mFadePaint;
    private float mCurX;
    private float mCurY;
    private int mOldButtonState;
    private int mFadeSteps = MAX_FADE_STEPS;

    public SketchView(Context c) {
        super(c);
        setFocusable(true);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mFadePaint = new Paint();
        mFadePaint.setColor(BACKGROUND_COLOR);
        mFadePaint.setAlpha(FADE_ALPHA);
    }

    public void clear() {
        if (mCanvas != null) {
            mPaint.setColor(BACKGROUND_COLOR);
            mCanvas.drawPaint(mPaint);
            invalidate();

            mFadeSteps = MAX_FADE_STEPS;
        }
    }

    public void fade() {
        if (mCanvas != null && mFadeSteps < MAX_FADE_STEPS) {
            mCanvas.drawPaint(mFadePaint);
            invalidate();

            mFadeSteps++;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int curW = mBitmap != null ? mBitmap.getWidth() : 0;
        int curH = mBitmap != null ? mBitmap.getHeight() : 0;
        if (curW >= w && curH >= h) {
            return;
        }

        if (curW < w) curW = w;
        if (curH < h) curH = h;

        Bitmap newBitmap = Bitmap.createBitmap(curW, curH, Bitmap.Config.ARGB_8888);
        Canvas newCanvas = new Canvas();
        newCanvas.setBitmap(newBitmap);
        if (mBitmap != null) {
            newCanvas.drawBitmap(mBitmap, 0, 0, null);
        }
        mBitmap = newBitmap;
        mCanvas = newCanvas;
        mFadeSteps = MAX_FADE_STEPS;
    }

    @Override
    protected void onDraw(Canvas canvas) {
    	canvas.drawColor(this.getResources().getColor(R.color.smog));
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        final int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            // Advance color when the trackball button is pressed.
            advanceColor();
        }

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            final int N = event.getHistorySize();
            final float scaleX = event.getXPrecision() * TRACKBALL_SCALE;
            final float scaleY = event.getYPrecision() * TRACKBALL_SCALE;
            for (int i = 0; i < N; i++) {
                moveTrackball(event.getHistoricalX(i) * scaleX,
                        event.getHistoricalY(i) * scaleY);
            }
            moveTrackball(event.getX() * scaleX, event.getY() * scaleY);
        }
        return true;
    }

    private void moveTrackball(float deltaX, float deltaY) {
        final int curW = mBitmap != null ? mBitmap.getWidth() : 0;
        final int curH = mBitmap != null ? mBitmap.getHeight() : 0;

        mCurX = Math.max(Math.min(mCurX + deltaX, curW - 1), 0);
        mCurY = Math.max(Math.min(mCurY + deltaY, curH - 1), 0);
        paint(PaintMode.Draw, mCurX, mCurY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return onTouchOrHoverEvent(event, true /*isTouch*/);
    }

    @Override
    public boolean onHoverEvent(MotionEvent event) {
        return onTouchOrHoverEvent(event, false /*isTouch*/);
    }

    private boolean onTouchOrHoverEvent(MotionEvent event, boolean isTouch) {
        final int buttonState = event.getButtonState();
        int pressedButtons = buttonState & ~mOldButtonState;
        mOldButtonState = buttonState;

        if ((pressedButtons & MotionEvent.BUTTON_SECONDARY) != 0) {
            // Advance color when the right mouse button or first stylus button
            // is pressed.
            advanceColor();
        }

        PaintMode mode;
        if ((buttonState & MotionEvent.BUTTON_TERTIARY) != 0) {
            // Splat paint when the middle mouse button or second stylus button is pressed.
            mode = PaintMode.Splat;
        } else if (isTouch || (buttonState & MotionEvent.BUTTON_PRIMARY) != 0) {
            // Draw paint when touching or if the primary button is pressed.
            mode = PaintMode.Draw;
        } else {
            // Otherwise, do not paint anything.
            return false;
        }

        final int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE
                || action == MotionEvent.ACTION_HOVER_MOVE) {
            final int N = event.getHistorySize();
            final int P = event.getPointerCount();
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < P; j++) {
                    paint(getPaintModeForTool(event.getToolType(j), mode),
                            event.getHistoricalX(j, i),
                            event.getHistoricalY(j, i),
                            event.getHistoricalPressure(j, i),
                            event.getHistoricalTouchMajor(j, i),
                            event.getHistoricalTouchMinor(j, i),
                            event.getHistoricalOrientation(j, i),
                            event.getHistoricalAxisValue(MotionEvent.AXIS_DISTANCE, j, i),
                            event.getHistoricalAxisValue(MotionEvent.AXIS_TILT, j, i));
                }
            }
            for (int j = 0; j < P; j++) {
                paint(getPaintModeForTool(event.getToolType(j), mode),
                        event.getX(j),
                        event.getY(j),
                        event.getPressure(j),
                        event.getTouchMajor(j),
                        event.getTouchMinor(j),
                        event.getOrientation(j),
                        event.getAxisValue(MotionEvent.AXIS_DISTANCE, j),
                        event.getAxisValue(MotionEvent.AXIS_TILT, j));
            }
            mCurX = event.getX();
            mCurY = event.getY();
        }
        return true;
    }

    private PaintMode getPaintModeForTool(int toolType, PaintMode defaultMode) {
        if (toolType == MotionEvent.TOOL_TYPE_ERASER) {
            return PaintMode.Erase;
        }
        return defaultMode;
    }

    private void advanceColor() {
        mColorIndex = (mColorIndex + 1) % COLORS.length;
    }

    private void paint(PaintMode mode, float x, float y) {
        paint(mode, x, y, 1.0f, 0, 0, 0, 0, 0);
    }

    private void paint(PaintMode mode, float x, float y, float pressure,
            float major, float minor, float orientation,
            float distance, float tilt) {
        if (mBitmap != null) {
            if (major <= 0 || minor <= 0) {
                // If size is not available, use a default value.
                major = minor = 16;
            }

            switch (mode) {
                case Draw:
                    mPaint.setColor(COLORS[mColorIndex]);
                    mPaint.setAlpha(Math.min((int)(pressure * 128), 255));
                    drawOval(mCanvas, x, y, major, minor, orientation, mPaint);
                    break;

                case Erase:
                    mPaint.setColor(BACKGROUND_COLOR);
                    mPaint.setAlpha(Math.min((int)(pressure * 128), 255));
                    drawOval(mCanvas, x, y, major, minor, orientation, mPaint);
                    break;

                case Splat:
                    mPaint.setColor(COLORS[mColorIndex]);
                    mPaint.setAlpha(64);
                    drawSplat(mCanvas, x, y, orientation, distance, tilt, mPaint);
                    break;
            }
        }
        mFadeSteps = 0;
        invalidate();
    }

    /**
     * Draw an oval.
     *
     * When the orienation is 0 radians, orients the major axis vertically,
     * angles less than or greater than 0 radians rotate the major axis left or right.
     */
    private final RectF mReusableOvalRect = new RectF();
    private void drawOval(Canvas canvas, float x, float y, float major, float minor,
            float orientation, Paint paint) {
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.rotate((float) (orientation * 180 / Math.PI), x, y);
        mReusableOvalRect.left = x - minor / 2;
        mReusableOvalRect.right = x + minor / 2;
        mReusableOvalRect.top = y - major / 2;
        mReusableOvalRect.bottom = y + major / 2;
        canvas.drawOval(mReusableOvalRect, paint);
        canvas.restore();
    }

    /**
     * Splatter paint in an area.
     *
     * Chooses random vectors describing the flow of paint from a round nozzle
     * across a range of a few degrees.  Then adds this vector to the direction
     * indicated by the orientation and tilt of the tool and throws paint at
     * the canvas along that vector.
     *
     * Repeats the process until a masterpiece is born.
     */
    private void drawSplat(Canvas canvas, float x, float y, float orientation,
            float distance, float tilt, Paint paint) {
        float z = distance * 2 + 10;

        // Calculate the center of the spray.
        float nx = (float) (Math.sin(orientation) * Math.sin(tilt));
        float ny = (float) (- Math.cos(orientation) * Math.sin(tilt));
        float nz = (float) Math.cos(tilt);
        if (nz < 0.05) {
            return;
        }
        float cd = z / nz;
        float cx = nx * cd;
        float cy = ny * cd;

        for (int i = 0; i < SPLAT_VECTORS; i++) {
            // Make a random 2D vector that describes the direction of a speck of paint
            // ejected by the nozzle in the nozzle's plane, assuming the tool is
            // perpendicular to the surface.
            double direction = mRandom.nextDouble() * Math.PI * 2;
            double dispersion = mRandom.nextGaussian() * 0.2;
            double vx = Math.cos(direction) * dispersion;
            double vy = Math.sin(direction) * dispersion;
            double vz = 1;

            // Apply the nozzle tilt angle.
            double temp = vy;
            vy = temp * Math.cos(tilt) - vz * Math.sin(tilt);
            vz = temp * Math.sin(tilt) + vz * Math.cos(tilt);

            // Apply the nozzle orientation angle.
            temp = vx;
            vx = temp * Math.cos(orientation) - vy * Math.sin(orientation);
            vy = temp * Math.sin(orientation) + vy * Math.cos(orientation);

            // Determine where the paint will hit the surface.
            if (vz < 0.05) {
                continue;
            }
            float pd = (float) (z / vz);
            float px = (float) (vx * pd);
            float py = (float) (vy * pd);

            // Throw some paint at this location, relative to the center of the spray.
            mCanvas.drawCircle(x + px - cx, y + py - cy, 1.0f, paint);
        }
    }
}