package com.zolad.bubbletextureview.base;
import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * EGL configuration Chooser slightly adapted from:
 *
 * https://code.google.com/p/gdc2011-android-opengl/source/browse/trunk/src/com/example/gdc11/MultisampleConfigChooser.java
 */
public class MultiSampleEGLConfigChooser implements GLTextureView.EGLConfigChooser {
    static private final String TAG = MultiSampleEGLConfigChooser.class.getSimpleName();

    private int[] value;

    private boolean usesCoverageAa = false;

    // pixel format
    private int r = 8;
    private int g = 8;
    private int b = 8;
    private int a = 8;

    private int depth = 16;
    private int stencil = 4;
    private int multisample = 4;
    private int renderableType = 4;

    public MultiSampleEGLConfigChooser() {
    }

    public MultiSampleEGLConfigChooser(int r, int g, int b, int a, int depth, int stencil,
                                       int multisample, int renderableType) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;

        this.depth = depth;
        this.stencil = stencil;
        this.multisample = multisample;
        this.renderableType = renderableType;
    }

    @Override public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
        // try to find a normal configuration first.
        int[] configSpec = {
                EGL10.EGL_RED_SIZE, r, EGL10.EGL_GREEN_SIZE, g, EGL10.EGL_BLUE_SIZE, b,
                EGL10.EGL_ALPHA_SIZE, a, EGL10.EGL_DEPTH_SIZE, depth, EGL10.EGL_STENCIL_SIZE, stencil,
                EGL10.EGL_RENDERABLE_TYPE, renderableType, EGL10.EGL_SAMPLE_BUFFERS, 1, EGL10.EGL_SAMPLES,
                multisample, EGL10.EGL_NONE
        };
        value = new int[1];
        if (!egl.eglChooseConfig(display, configSpec, null, 0, value)) {
            throw new IllegalArgumentException("eglChooseConfig failed");
        }
        int numConfigs = value[0];

        // No normal multisampling config was found. Try to create a
        // converage multisampling configuration, for the nVidia Tegra2.
        // See the EGL_NV_coverage_sample documentation.
        if (numConfigs <= 0 && multisample > 1) {
            final int EGL_COVERAGE_BUFFERS_NV = 0x30E0;
            final int EGL_COVERAGE_SAMPLES_NV = 0x30E1;

            configSpec = new int[] {
                    EGL10.EGL_RED_SIZE, r, EGL10.EGL_GREEN_SIZE, g, EGL10.EGL_BLUE_SIZE, b,
                    EGL10.EGL_ALPHA_SIZE, a, EGL10.EGL_DEPTH_SIZE, depth, EGL10.EGL_STENCIL_SIZE, stencil,
                    EGL10.EGL_RENDERABLE_TYPE, renderableType, EGL_COVERAGE_BUFFERS_NV, 1,
                    EGL_COVERAGE_SAMPLES_NV, multisample, EGL10.EGL_NONE
            };

            if (!egl.eglChooseConfig(display, configSpec, null, 0, value)) {
                throw new IllegalArgumentException("2nd eglChooseConfig failed");
            }
            numConfigs = value[0];

            // Give up, try without multisampling.
            if (numConfigs <= 0) {
                configSpec = new int[] {
                        EGL10.EGL_RED_SIZE, r, EGL10.EGL_GREEN_SIZE, g, EGL10.EGL_BLUE_SIZE, b,
                        EGL10.EGL_ALPHA_SIZE, a, EGL10.EGL_DEPTH_SIZE, depth, EGL10.EGL_STENCIL_SIZE, stencil,
                        EGL10.EGL_RENDERABLE_TYPE, renderableType, EGL10.EGL_NONE
                };

                if (!egl.eglChooseConfig(display, configSpec, null, 0, value)) {
                    throw new IllegalArgumentException("3rd eglChooseConfig failed");
                }
                numConfigs = value[0];

                if (numConfigs <= 0) {
                    throw new IllegalArgumentException("No configs match configSpec");
                }
            } else {
                usesCoverageAa = true;
                Log.i(TAG, "usesCoverageAa");
            }
        }

        // Get all matching configurations.
        EGLConfig[] configs = new EGLConfig[numConfigs];
        if (!egl.eglChooseConfig(display, configSpec, configs, numConfigs, value)) {
            throw new IllegalArgumentException("data eglChooseConfig failed");
        }

        // CAUTION! eglChooseConfigs returns configs with higher bit depth
        // first: Even though we asked for rgb565 configurations, rgb888
        // configurations are considered to be "better" and returned first.
        // You need to explicitly filter the data returned by eglChooseConfig!
        int index = -1;
        for (int i = 0; i < configs.length; ++i) {
            if (findConfigAttrib(egl, display, configs[i], EGL10.EGL_RED_SIZE, 0) == r) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            Log.i(TAG, "Did not find sane config, using first");
        }
        EGLConfig config = configs.length > 0 ? configs[index] : null;
        if (config == null) {
            throw new IllegalArgumentException("No config chosen");
        }
        return config;
    }

    private int findConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute,
                                 int defaultValue) {
        if (egl.eglGetConfigAttrib(display, config, attribute, value)) {
            return value[0];
        }
        return defaultValue;
    }

    public boolean usesCoverageAa() {
        return usesCoverageAa;
    }
}
