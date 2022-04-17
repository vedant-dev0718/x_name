package com.daasuu.gpuv.player;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import com.daasuu.gpuv.egl.GlConfigChooser;
import com.daasuu.gpuv.egl.GlContextFactory;
import com.daasuu.gpuv.egl.filter.GlFilter;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;


public class GPUPlayerView extends GLSurfaceView implements Player.Listener {

    private final static String TAG = GPUPlayerView.class.getSimpleName();

    private final GPUPlayerRenderer renderer;
    private SimpleExoPlayer player;

    private float videoAspect = 1f;
    private PlayerScaleType playerScaleType = PlayerScaleType.RESIZE_FIT_WIDTH;

    public GPUPlayerView(Context context) {
        this(context, null);
    }

    public GPUPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextFactory(new GlContextFactory());
        setEGLConfigChooser(new GlConfigChooser(false));

        renderer = new GPUPlayerRenderer(this);
        setRenderer(renderer);

    }

    public GPUPlayerView setSimpleExoPlayer(SimpleExoPlayer player) {
        if (this.player != null) {
            this.player.release();
            this.player = null;
        }
        this.player = player;
        this.player.addListener(this);
        this.renderer.setSimpleExoPlayer(player);
        return this;
    }

    public void setGlFilter(GlFilter glFilter) {
        renderer.setGlFilter(glFilter);
    }

    public void setPlayerScaleType(PlayerScaleType playerScaleType) {
        this.playerScaleType = playerScaleType;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        int viewWidth = measuredWidth;
        int viewHeight = measuredHeight;

        switch (playerScaleType) {
            case RESIZE_FIT_WIDTH:
                viewHeight = (int) (measuredWidth / videoAspect);
                break;
            case RESIZE_FIT_HEIGHT:
                viewWidth = (int) (measuredHeight * videoAspect);
                break;
        }

        // Log.d(TAG, "onMeasure viewWidth = " + viewWidth + " viewHeight = " + viewHeight);

        setMeasuredDimension(viewWidth, viewHeight);

    }

    @Override
    public void onPause() {
        super.onPause();
        renderer.release();
    }

    //////////////////////////////////////////////////////////////////////////
    // SimpleExoPlayer.VideoListener
    @Deprecated
    @SuppressWarnings("deprecation")
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        // Log.d(TAG, "width = " + width + " height = " + height + " unappliedRotationDegrees = " + unappliedRotationDegrees + " pixelWidthHeightRatio = " + pixelWidthHeightRatio);
        videoAspect = ((float) width / height) * pixelWidthHeightRatio;
        // Log.d(TAG, "videoAspect = " + videoAspect);
        requestLayout();
    }
    @Deprecated
    @SuppressWarnings("deprecation")
    public void onRenderedFirstFrame() {
        // do nothing
    }
}