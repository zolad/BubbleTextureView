package com.zolad.bubbletextureview.base;

import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.Log;


public class GLBubbleGeometry {

    private float[] oringinleftTop = new float[2];
    private float[] oringinRightTop = new float[2];
    private float[] oringinleftTopR = new float[2];
    private float[] oringinRightTopR = new float[2];


    private float[] tgleft1 = new float[2];
    private float[] tgleft2 = new float[2];
    private float[] tgleft3 = new float[2];
    private float[] tgright1 = new float[2];
    private float[] tgright2 = new float[2];
    private float[] tgright3 = new float[2];


    private float[] leftTop = new float[2];
    private float[] leftBottom = new float[2];
    private float[] topLeft = new float[2];
    private float[] topRight = new float[2];
    private float[] rightTop = new float[2];
    private float[] rightBottom = new float[2];
    private float[] bottomLeft = new float[2];
    private float[] bottomRight = new float[2];

    private float[] innerTopLeft = new float[2];
    private float[] innerTopRight = new float[2];
    private float[] innerBottomRight = new float[2];
    private float[] innerBottomLeft = new float[2];

    private float[] topLeftRadius = new float[2];
    private float[] topRightRadius = new float[2];
    private float[] bottomRightRadius = new float[2];
    private float[] bottomLeftRadius = new float[2];

    public GeometryArrays generateVertexData(RectF radius, RectF viewPortGLBounds,
                                             Point viewPortPxSize, float arrowSize, float arrowOffsetFromCenter,boolean arrowDirection) {
        return generateVertexData(radius, viewPortGLBounds, viewPortPxSize, 0f, arrowSize, arrowOffsetFromCenter,arrowDirection);
    }

    /**
     * Generates a {@link GeometryArrays} object with arrays containing the resulting geometry
     * vertices and the corresponding triangle indexes.
     *
     * @param radius           the corner radius of each corner. left is topLeft, top is topRight, right is
     *                         rightBottom and bottom is leftBottom.
     * @param viewPortGLBounds the bounds of the GL viewport in GL scalar units.
     * @param viewPortPxSize   the size of the view port in pixels.
     * @param z                the z coordinate for the z-plane geometry.
     * @return an object with the resulting geometry.
     */
    public GeometryArrays generateVertexData(RectF radius, RectF viewPortGLBounds,
                                             Point viewPortPxSize, float z, float arrowSize, float arrowOffsetFromCenter,boolean arrowDirection) {
        final float x0 = viewPortGLBounds.left + (arrowDirection ? arrowSize : 0);
        final float x1 = viewPortGLBounds.right - (arrowDirection ? 0 : arrowSize);
        final float y0 = viewPortGLBounds.bottom;
        final float y1 = viewPortGLBounds.top;

        final float leftTopRadius = radius.left;
        final float rightTopRadius = radius.top;
        final float rightBottomRadius = radius.right;
        final float leftBottomRadius = radius.bottom;


        topLeftRadius[0] = leftTopRadius / viewPortPxSize.x * viewPortGLBounds.width();
        topLeftRadius[1] = leftTopRadius / viewPortPxSize.y * -viewPortGLBounds.height();
        topRightRadius[0] = rightTopRadius / viewPortPxSize.x * viewPortGLBounds.width();
        topRightRadius[1] = rightTopRadius / viewPortPxSize.y * -viewPortGLBounds.height();
        bottomRightRadius[0] = rightBottomRadius / viewPortPxSize.x * viewPortGLBounds.width();
        bottomRightRadius[1] = rightBottomRadius / viewPortPxSize.y * -viewPortGLBounds.height();
        bottomLeftRadius[0] = leftBottomRadius / viewPortPxSize.x * viewPortGLBounds.width();
        bottomLeftRadius[1] = leftBottomRadius / viewPortPxSize.y * -viewPortGLBounds.height();

        leftTop[0] = x0;
        leftTop[1] = y1 - topLeftRadius[1];
        leftBottom[0] = x0;
        leftBottom[1] = y0 + bottomLeftRadius[1];
        topLeft[0] = x0 + topLeftRadius[0];
        topLeft[1] = y1;
        topRight[0] = x1 - topRightRadius[0];
        topRight[1] = y1;
        rightTop[0] = x1;
        rightTop[1] = y1 - topRightRadius[1];
        rightBottom[0] = x1;
        rightBottom[1] = y0 + bottomRightRadius[1];
        bottomLeft[0] = x0 + bottomLeftRadius[0];
        bottomLeft[1] = y0;
        bottomRight[0] = x1 - bottomRightRadius[0];
        bottomRight[1] = y0;

        innerTopLeft[0] = topLeft[0];
        innerTopLeft[1] = leftTop[1];
        innerTopRight[0] = topRight[0];
        innerTopRight[1] = rightTop[1];
        innerBottomLeft[0] = bottomLeft[0];
        innerBottomLeft[1] = leftBottom[1];
        innerBottomRight[0] = bottomRight[0];
        innerBottomRight[1] = rightBottom[1];


        oringinleftTop[0] = x0;
        oringinleftTop[1] = y1;
        oringinleftTopR[0] = innerTopLeft[0];
        oringinleftTopR[1] = y1;

        oringinRightTop[0] = x1;
        oringinRightTop[1] = y1;
        oringinRightTopR[0] = innerTopRight[0];
        oringinRightTopR[1] = y1;

        float ySpace = Math.abs(arrowSize * (float) ((double) viewPortPxSize.x / viewPortPxSize.y));
        float offset = arrowOffsetFromCenter;

        tgleft1[0] = viewPortGLBounds.left;
        tgleft1[1] = ((y1 + y0) / 2.0f)+offset;
        tgleft2[0] = oringinleftTop[0];
        tgleft2[1] = ((y1 + y0) / 2.0f) + ySpace / 2f +offset;
        tgleft3[0] = oringinleftTop[0];
        tgleft3[1] = ((y1 + y0) / 2.0f) - ySpace / 2f+offset;

        tgright1[0] = viewPortGLBounds.right;
        tgright1[1] = ((y1 + y0) / 2.0f)+offset;
        tgright2[0] = oringinRightTop[0];
        tgright2[1] = ((y1 + y0) / 2.0f) + ySpace / 2f+offset;
        tgright3[0] = oringinRightTop[0];
        tgright3[1] = ((y1 + y0) / 2.0f) - ySpace / 2f+offset;



        // Each vertex has 5 floats (xyz + uv)
        // 5 squares (each has 4 vertices)
        // 4 rounded corners (each has X triangles, each triangle has 3 vertices)
        final int trianglesPerCorner = 6;
        final int floatsPerRoundedCorner = (trianglesPerCorner + 2) * 5;
        final int floatsPerSquare = 4 * 5;
        final int shortsPerTriangle = 3;
        final int shortsPerSquare = 2 * shortsPerTriangle;
        final int verticesSize = 5 * floatsPerSquare + 4 * floatsPerRoundedCorner + 1 * 3 * 5;
        final int indicesSize = 5 * shortsPerSquare + 4 * trianglesPerCorner * shortsPerTriangle + 1 * shortsPerTriangle ;
        final float[] vertices = new float[verticesSize];
        final short[] indices = new short[indicesSize];
        final GeometryArrays geoArrays = new GeometryArrays(vertices, indices);

        // Inner center rect
        addRect(geoArrays, new float[][]{
                innerTopLeft, innerTopRight, innerBottomLeft, innerBottomRight
        }, viewPortGLBounds, z);
        geoArrays.verticesOffset += floatsPerSquare;
        geoArrays.indicesOffset += shortsPerSquare;


        //  if (!arrowDirection) {
        // Left rect
        addRect(geoArrays, new float[][]{
                leftTop, innerTopLeft, leftBottom, innerBottomLeft
        }, viewPortGLBounds, z);
        geoArrays.verticesOffset += floatsPerSquare;
        geoArrays.indicesOffset += shortsPerSquare;
//        } else {
//
//            // Left rect
//            addRect(geoArrays, new float[][]{
//                    oringinleftTop, oringinleftTopR, leftBottom, innerBottomLeft
//            }, viewPortGLBounds, z);
//            geoArrays.verticesOffset += floatsPerSquare;
//            geoArrays.indicesOffset += shortsPerSquare;
//
//        }

        //    if (arrowDirection) {
        // Right rect

        addRect(geoArrays, new float[][]{
                innerTopRight, rightTop, innerBottomRight, rightBottom
        }, viewPortGLBounds, z);
        geoArrays.verticesOffset += floatsPerSquare;
        geoArrays.indicesOffset += shortsPerSquare;
//        } else {
//
//            addRect(geoArrays, new float[][]{
//                    oringinRightTopR, oringinRightTop, innerBottomRight, rightBottom
//            }, viewPortGLBounds, z);
//            geoArrays.verticesOffset += floatsPerSquare;
//            geoArrays.indicesOffset += shortsPerSquare;
//
//
//        }
        // Top rect
        addRect(geoArrays, new float[][]{
                topLeft, innerTopLeft, topRight, innerTopRight
        }, viewPortGLBounds, z);
        geoArrays.verticesOffset += floatsPerSquare;
        geoArrays.indicesOffset += shortsPerSquare;

        // Bottom rect
        addRect(geoArrays, new float[][]{
                innerBottomLeft, bottomLeft, innerBottomRight, bottomRight
        }, viewPortGLBounds, z);
        geoArrays.verticesOffset += floatsPerSquare;
        geoArrays.indicesOffset += shortsPerSquare;

//        // These assume uniform corners (i.e. same radius on both axis)
//        // Top left corner

        addRoundedCorner(geoArrays, innerTopLeft, topLeftRadius, (float) Math.PI,
                (float) (Math.PI / 2.0), trianglesPerCorner, viewPortGLBounds, z);
        geoArrays.verticesOffset += floatsPerRoundedCorner;
        geoArrays.indicesOffset += trianglesPerCorner * shortsPerTriangle;


        // Top right corner

        addRoundedCorner(geoArrays, innerTopRight, topRightRadius, (float) (Math.PI / 2), 0f,
                trianglesPerCorner, viewPortGLBounds, z);
        geoArrays.verticesOffset += floatsPerRoundedCorner;
        geoArrays.indicesOffset += trianglesPerCorner * shortsPerTriangle;

        // Bottom right corner
        addRoundedCorner(geoArrays, innerBottomRight, bottomRightRadius, (float) (Math.PI * 3.0 / 2.0),
                (float) Math.PI * 2, trianglesPerCorner, viewPortGLBounds, z);
        geoArrays.verticesOffset += floatsPerRoundedCorner;
        geoArrays.indicesOffset += trianglesPerCorner * shortsPerTriangle;

        // Bottom left corner
        addRoundedCorner(geoArrays, innerBottomLeft, bottomLeftRadius, (float) Math.PI,
                (float) (Math.PI * 3.0 / 2.0), trianglesPerCorner, viewPortGLBounds, z);
        geoArrays.verticesOffset += floatsPerRoundedCorner;
        geoArrays.indicesOffset += trianglesPerCorner * shortsPerTriangle;

        if (arrowDirection) {


            addTriangle(geoArrays, tgleft1, tgleft2, tgleft3, viewPortGLBounds, z);

            geoArrays.verticesOffset += 3 * 5;
            geoArrays.indicesOffset += 1 * shortsPerTriangle;
        } else {
            addTriangle(geoArrays, tgright1, tgright2, tgright3, viewPortGLBounds, z);

            geoArrays.verticesOffset += 3 * 5;
            geoArrays.indicesOffset += 1 * shortsPerTriangle;
        }

        return new GeometryArrays(vertices, indices);
    }

    private void addTriangle(GeometryArrays geoArrays, float[] tgleft1, float[] tgleft2, float[] tgleft3, RectF viewPort, float z) {

        final float[] vertices = geoArrays.triangleVertices;
        final short[] indices = geoArrays.triangleIndices;
        final int indicesOffset = geoArrays.indicesOffset;
        final int verticesOffset = geoArrays.verticesOffset;
        int rectPointIdx = 0;
        final int currentVertexOffset = verticesOffset;
        vertices[currentVertexOffset] = tgleft1[0];
        vertices[currentVertexOffset + 1] = tgleft1[1];
        vertices[currentVertexOffset + 2] = z;

        // UV (texture mapping)
        vertices[currentVertexOffset + 3] = (tgleft1[0] - viewPort.left) / viewPort.width();
        vertices[currentVertexOffset + 4] = (tgleft1[1] - viewPort.bottom) / -viewPort.height();

        vertices[currentVertexOffset + 5] = tgleft2[0];
        vertices[currentVertexOffset + 6] = tgleft2[1];
        vertices[currentVertexOffset + 7] = z;

        // UV (texture mapping)
        vertices[currentVertexOffset + 8] = (tgleft2[0] - viewPort.left) / viewPort.width();
        vertices[currentVertexOffset + 9] = (tgleft2[1] - viewPort.bottom) / -viewPort.height();

        vertices[currentVertexOffset + 10] = tgleft3[0];
        vertices[currentVertexOffset + 11] = tgleft3[1];
        vertices[currentVertexOffset + 12] = z;

        // UV (texture mapping)
        vertices[currentVertexOffset + 13] = (tgleft3[0] - viewPort.left) / viewPort.width();
        vertices[currentVertexOffset + 14] = (tgleft3[1] - viewPort.bottom) / -viewPort.height();


        final int initialIdx = verticesOffset / 5;
        indices[indicesOffset] = (short) (initialIdx);
        indices[indicesOffset + 1] = (short) (initialIdx + 1);
        indices[indicesOffset + 2] = (short) (initialIdx + 2);

    }

    /**
     * Adds the vertices of a rectangle defined by 4 corner points. The array of vertices passed
     * in must have the required length to add the geometry points (5 floats for each vertex). Also
     * the coordinates of the rect corners should already be in the view port space.
     *
     * @param geoArrays  an object containing the vertex and index data arrays and their current
     *                   offsets.
     * @param rectPoints an array of corner points defining the rectangle. index 0 is the x
     *                   coordinate and index 1 the y coordinate.
     * @param viewPort   the bounds of the current GL viewport, this is used to calculate the texture
     *                   mapping.
     * @param z          the z coordinate.
     */
    private void addRect(@NonNull GeometryArrays geoArrays, @NonNull float[][] rectPoints,
                         @NonNull RectF viewPort, float z) {
        final float[] vertices = geoArrays.triangleVertices;
        final short[] indices = geoArrays.triangleIndices;
        final int indicesOffset = geoArrays.indicesOffset;
        final int verticesOffset = geoArrays.verticesOffset;
        int rectPointIdx = 0;
        for (final float[] rectPoint : rectPoints) {
            // 5 values [xyzuv] per vertex
            final int currentVertexOffset = verticesOffset + rectPointIdx * 5;

            // XYZ (vertex space coordinates
            vertices[currentVertexOffset] = rectPoint[0];
            vertices[currentVertexOffset + 1] = rectPoint[1];
            vertices[currentVertexOffset + 2] = z;

            // UV (texture mapping)
            vertices[currentVertexOffset + 3] = (rectPoint[0] - viewPort.left) / viewPort.width();
            vertices[currentVertexOffset + 4] = (rectPoint[1] - viewPort.bottom) / -viewPort.height();

            rectPointIdx++;
        }

        // Index our triangles -- tell where each triangle vertex is
        final int initialIdx = verticesOffset / 5;
        indices[indicesOffset] = (short) (initialIdx);
        indices[indicesOffset + 1] = (short) (initialIdx + 1);
        indices[indicesOffset + 2] = (short) (initialIdx + 2);
        indices[indicesOffset + 3] = (short) (initialIdx + 1);
        indices[indicesOffset + 4] = (short) (initialIdx + 2);
        indices[indicesOffset + 5] = (short) (initialIdx + 3);
    }

    /**
     * Adds the vertices of a number of triangles to form a rounded corner. The triangles start at
     * some center point and will sweep from a given initial angle up to a final one. The size of
     * the triangles is defined by the radius.
     * <p>
     * The array of vertices passed in must have the required length to add the geometry points
     * (5 floats for each vertex). Also the coordinates of the rect corners should already be in
     * the view port space.
     *
     * @param geoArrays an object containing the vertex and index data arrays and their current
     *                  offsets.
     * @param center    the center point where all triangles will start.
     * @param radius    the desired radius in the x and y axis, in viewport dimensions.
     * @param rads0     the initial angle.
     * @param rads1     the final angle.
     * @param triangles the amount of triangles to create.
     * @param viewPort  the bounds of the current GL viewport, this is used to calculate the texture
     *                  mapping.
     * @param z         the z coordinate.
     */
    private void addRoundedCorner(@NonNull GeometryArrays geoArrays, @NonNull float[] center,
                                  float[] radius, float rads0, float rads1, int triangles, @NonNull RectF viewPort, float z) {
        final float[] vertices = geoArrays.triangleVertices;
        final short[] indices = geoArrays.triangleIndices;
        final int verticesOffset = geoArrays.verticesOffset;
        final int indicesOffset = geoArrays.indicesOffset;
        for (int i = 0; i < triangles; i++) {
            // final int currentOffset = verticesOffset + i * 15 /* each triangle is 3 * xyzuv */;
            final int currentOffset = verticesOffset + i * 5 + (i > 0 ? 2 * 5 : 0);
            final float rads = rads0 + (rads1 - rads0) * (i / (float) triangles);
            final float radsNext = rads0 + (rads1 - rads0) * ((i + 1) / (float) triangles);
            final int triangleEdge2Offset;

            if (i == 0) {
                // XYZUV - center point
                vertices[currentOffset] = center[0];
                vertices[currentOffset + 1] = center[1];
                vertices[currentOffset + 2] = z;
                vertices[currentOffset + 3] = (vertices[currentOffset] - viewPort.left) / viewPort.width();
                vertices[currentOffset + 4] =
                        (vertices[currentOffset + 1] - viewPort.bottom) / -viewPort.height();

                // XYZUV - triangle edge 1
                vertices[currentOffset + 5] = center[0] + radius[0] * (float) Math.cos(rads);
                vertices[currentOffset + 6] = center[1] + radius[1] * (float) Math.sin(rads);
                vertices[currentOffset + 7] = z;
                vertices[currentOffset + 8] =
                        (vertices[currentOffset + 5] - viewPort.left) / viewPort.width();
                vertices[currentOffset + 9] =
                        (vertices[currentOffset + 6] - viewPort.bottom) / -viewPort.height();

                triangleEdge2Offset = 10;
            } else {
                triangleEdge2Offset = 0;
            }

            // XYZUV - triangle edge 2
            final int edge2Offset = currentOffset + triangleEdge2Offset;
            vertices[edge2Offset] = center[0] + radius[0] * (float) Math.cos(radsNext);
            vertices[edge2Offset + 1] = center[1] + radius[1] * (float) Math.sin(radsNext);
            vertices[edge2Offset + 2] = z;
            vertices[edge2Offset + 3] = (vertices[edge2Offset] - viewPort.left) / viewPort.width();
            vertices[edge2Offset + 4] =
                    (vertices[edge2Offset + 1] - viewPort.bottom) / -viewPort.height();

            // Index our triangles -- tell where each triangle vertex is
            final int initialIdx = verticesOffset / 5;
            indices[indicesOffset + i * 3] = (short) (initialIdx);
            indices[indicesOffset + i * 3 + 1] = (short) (initialIdx + i + 1);
            indices[indicesOffset + i * 3 + 2] = (short) (initialIdx + i + 2);
        }
    }

    public static class GeometryArrays {
        public float[] triangleVertices;
        public short[] triangleIndices;
        public int verticesOffset = 0;
        public int indicesOffset = 0;

        public GeometryArrays(@NonNull float[] vertices, @NonNull short[] indices) {
            triangleVertices = vertices;
            triangleIndices = indices;
        }
    }
}