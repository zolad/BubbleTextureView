package com.zolad.bubbletextureview.renders;

import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.support.annotation.NonNull;
import android.util.Log;

import com.zolad.bubbletextureview.base.GLTextureView;
import com.zolad.bubbletextureview.interfaces.SurfaceListener;
import com.zolad.bubbletextureview.base.GLBubbleGeometry;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class BubbleGLRenderer implements GLTextureView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private static String TAG = BubbleGLRenderer.class.getSimpleName();

    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int SHORT_SIZE_BYTES = 2;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;

    private static final String vertexShader = "" +
            "uniform mat4 uMVPMatrix;\n" +
            "uniform mat4 uSTMatrix;\n" +
            "attribute vec4 aPosition;\n" +
            "attribute vec4 aTextureCoord;\n" +
            "varying vec2 vTextureCoord;\n" +
            "void main() {\n" +
            "  gl_Position = uMVPMatrix * aPosition;\n" +
            "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
            "}\n";

    private static final String fragmentShader = "" +
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "varying vec2 vTextureCoord;\n" +
            "uniform samplerExternalOES sTexture;\n" +
            "void main() {\n" +
            "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
            "}\n";

    private float[] mvpMatrix = new float[16];
    private float[] stMatrix = new float[16];

    private int program;
    private int textureID;
    private int mvpMatrixHandle;
    private int ustMatrixHandle;
    private int aPositionHandle;
    private int aTextureHandle;

    private static final int GL_TEXTURE_EXTERNAL_OES = 0x00008d65;

    private static final int GL_COVERAGE_BUFFER_BIT_NV = 0x8000;

    private final GLTextureView glTextureView;
    private SurfaceTexture surfaceTexture;
    private boolean updateSurface = false;

    private float[] triangleVerticesData;
    private short[] triangleIndicesData;
    private FloatBuffer triangleVertices;
    private ShortBuffer triangleIndices;
    private RectF roundRadius = new RectF();
    private GLBubbleGeometry roundedGeometry;
    private final Point viewPortSize = new Point();
    private final RectF viewPortGLBounds;
    private boolean usesCoverageAa = false;
    private boolean arrowDirectionLeft  = false;
    private SurfaceListener surfaceListener;

    public BubbleGLRenderer(@NonNull GLTextureView view) {
        this(view, new GLBubbleGeometry(), new RectF(-1, 1, 1, -1));
    }

    public BubbleGLRenderer(@NonNull GLTextureView view, @NonNull GLBubbleGeometry roundedGeometry,
                            @NonNull RectF viewPortGLBounds) {
        glTextureView = view;
        this.roundedGeometry = roundedGeometry;
        this.viewPortGLBounds = viewPortGLBounds;
        viewPortSize.set(1, 1);

        Matrix.setIdentityM(stMatrix, 0);
    }

    public void setUsesCoverageAa(boolean usesCoverageAa) {
        this.usesCoverageAa = usesCoverageAa;
    }

    private float arrowSize;
    private boolean arrowDirection;
   private  float arrowOffsetFromCenter;
    public void setCornerRadiusAndArrow(float topLeft, float topRight, float bottomRight,
                                float bottomLeft,float arrowSize,float arrowOffsetFromCenter,boolean arrowDirection) {
        roundRadius.left = topLeft;
        roundRadius.top = topRight;
        roundRadius.right = bottomRight;
        roundRadius.bottom = bottomLeft;
        this.arrowSize = arrowSize;
        this.arrowDirection = arrowDirection;
        this.arrowOffsetFromCenter = arrowOffsetFromCenter;
        if (viewPortSize.x > 1) {
            updateVertexData();
        }
    }

    public void setArrowDirection(boolean arrowDirection) {
        this.arrowDirectionLeft = arrowDirection;
    }

    private void updateVertexData() {
        final GLBubbleGeometry.GeometryArrays arrays =
                roundedGeometry.generateVertexData(roundRadius, viewPortGLBounds, viewPortSize,arrowSize,arrowOffsetFromCenter,arrowDirection);
        triangleVerticesData = arrays.triangleVertices;
        triangleIndicesData = arrays.triangleIndices;
        if (triangleVertices != null) {
            triangleVertices.clear();
        } else {
            triangleVertices = ByteBuffer.allocateDirect(triangleVerticesData.length * FLOAT_SIZE_BYTES)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
        }
        if (triangleIndices != null) {
            triangleIndices.clear();
        } else {
            triangleIndices = ByteBuffer.allocateDirect(triangleIndicesData.length * SHORT_SIZE_BYTES)
                    .order(ByteOrder.nativeOrder())
                    .asShortBuffer();
        }
        triangleVertices.put(triangleVerticesData).position(0);
        triangleIndices.put(triangleIndicesData).position(0);
    }

    public void setSurfaceListener(SurfaceListener provider) {
        surfaceListener = provider;
    }

    @Override public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        viewPortSize.set(width, height);
        updateVertexData();
    }

    @Override public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        program = createProgram(vertexShader, fragmentShader);
        if (program == 0) {
            return;
        }
        aPositionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        checkGlError("glGetAttribLocation aPosition");
        if (aPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        aTextureHandle = GLES20.glGetAttribLocation(program, "aTextureCoord");
        checkGlError("glGetAttribLocation aTextureCoord");
        if (aTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }

        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        checkGlError("glGetUniformLocation uMVPMatrix");
        if (mvpMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }

        ustMatrixHandle = GLES20.glGetUniformLocation(program, "uSTMatrix");
        checkGlError("glGetUniformLocation uSTMatrix");
        if (ustMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uSTMatrix");
        }

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        textureID = textures[0];
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureID);
        checkGlError("glBindTexture textureID");

        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        surfaceTexture = new SurfaceTexture(textureID);
        surfaceTexture.setOnFrameAvailableListener(this);
        if (surfaceListener != null) {
            surfaceListener.onSurfaceCreated(surfaceTexture);
        }

        synchronized (this) {
            updateSurface = false;
        }
    }

    @Override public void onDrawFrame(GL10 glUnused) {
        synchronized (this) {
            if (updateSurface) {
                surfaceTexture.updateTexImage();
                surfaceTexture.getTransformMatrix(stMatrix);
                updateSurface = false;
            }
        }

        GLES20.glClearColor(.0f, .0f, .0f, .0f);
        int clearMask = GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT;
        if (usesCoverageAa) {
            // Tegra weirdness
            clearMask |= GL_COVERAGE_BUFFER_BIT_NV;
        }
        GLES20.glClear(clearMask);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glUseProgram(program);
        checkGlError("glUseProgram");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureID);

        triangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(aPositionHandle, 3, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, triangleVertices);
        checkGlError("glVertexAttribPointer aPosition");
        GLES20.glEnableVertexAttribArray(aPositionHandle);
        checkGlError("glEnableVertexAttribArray aPositionHandle");

        triangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(aTextureHandle, 2, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, triangleVertices);
        checkGlError("glVertexAttribPointer aTextureHandle");
        GLES20.glEnableVertexAttribArray(aTextureHandle);
        checkGlError("glEnableVertexAttribArray aTextureHandle");

        Matrix.setIdentityM(mvpMatrix, 0);
        Matrix.scaleM(mvpMatrix, 0, 1f, 1f, 1f);
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(ustMatrixHandle, 1, false, stMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, triangleIndicesData.length, GL10.GL_UNSIGNED_SHORT,
                triangleIndices);

        checkGlError("glDrawElements");
        GLES20.glFinish();
    }

    @Override synchronized public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        updateSurface = true;
        glTextureView.requestRender();
    }

    private static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    private static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    private static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }


}
