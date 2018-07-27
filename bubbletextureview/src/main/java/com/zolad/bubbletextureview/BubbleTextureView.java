package com.zolad.bubbletextureview;

import android.content.Context;
import android.util.AttributeSet;

import com.zolad.bubbletextureview.base.GLTextureView;
import com.zolad.bubbletextureview.base.MultiSampleEGLConfigChooser;
import com.zolad.bubbletextureview.interfaces.SurfaceListener;
import com.zolad.bubbletextureview.renders.BubbleGLRenderer;

public class BubbleTextureView extends GLTextureView {

    private BubbleGLRenderer bubbleGLRenderer;

    public BubbleTextureView(Context context) {
        this(context,null);
    }

    public BubbleTextureView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }


    public BubbleTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setConfigAndRenderer();
    }

    private void setConfigAndRenderer() {
        setEGLContextClientVersion(2);

        MultiSampleEGLConfigChooser chooser = new MultiSampleEGLConfigChooser();
        setEGLConfigChooser(chooser);
        bubbleGLRenderer = new BubbleGLRenderer(this);
        bubbleGLRenderer.setUsesCoverageAa(chooser.usesCoverageAa());
        setRenderer(bubbleGLRenderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        setOpaque(false);
    }

    public void setCornerRadius(float radius) {
        setCornerRadiusAndArrow(radius,0f,0f,true);
    }



    /**
     * set corner radius  and  arrow size and arrow direction
     *
     * @param radius       float,pixels,  the corner radius of each corner.
     * @param arrowSize    float,range 0f arrowSize 1.0f the size percent of arrow
     * @param arrowDirection  boolean true is left false is right, the direction of arrow
     * @param arrowOffsetFromCenter  float range  -1.0f arrowSize 1.0f arrow offset from center
     */
    public void setCornerRadiusAndArrow(float radius,float arrowSize,float arrowOffsetFromCenter,boolean arrowDirection) {
        if(arrowSize>=0 && arrowSize<1f) {
            bubbleGLRenderer.setCornerRadiusAndArrow(radius, radius, radius, radius, arrowSize,arrowOffsetFromCenter, arrowDirection);
        }
    }

    /**
     * set Arrow Direction
     * boolean  isLeft
     * */
    public void setArrowDirectionIsLeft(boolean isLeft) {
        bubbleGLRenderer.setArrowDirection(isLeft);
    }


    public void setSurfaceListner(SurfaceListener provider) {
        bubbleGLRenderer.setSurfaceListener(provider);
    }

}
