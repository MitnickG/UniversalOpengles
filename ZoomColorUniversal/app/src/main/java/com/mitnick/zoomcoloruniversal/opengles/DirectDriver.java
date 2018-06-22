package com.mitnick.zoomcoloruniversal.opengles;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import com.mitnick.zoomcoloruniversal.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Mitnick.Guo on 2017/6/16.
 */
public class DirectDriver {
    private static String TAG="~gyh";
    private int textureId;
    private Context context;

    private static float[] vertexCoords={
            1,-1,
            -1,-1,
            -1,1,
            1,1
    };
    //顶点缩放
    public static void setVertexLevel(float level){
        vertexCoords=new float[]{
                level,-level,
                -level,-level,
                -level,level,
                level,level
        };
    }
    //纹理坐标缩放
    public static void setTxtureCoords(float level){
        level=1/level;
        Log.d(TAG, "setTxtureCoords: level::"+level);
        textureCoords=new float[]{
                level,level,
                0,level,
                0,0,
                level,0
        };
    }
    private static float[] textureCoords={
         /*  //小米
           1,0,
            1,1,
            0,1,
            0,0*/
            /*
            //7寸测试放大
            0.1f,0.1f,
            0,0.1f,
            0,0,
            0.1f,0*/
            //7寸
            1,1,
            0,1,
            0,0,
            1,0

    };
    private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices// 顶点的绘制顺序,显示的颠倒与绘画顺序没关系
    int mProgram;
    FloatBuffer vertexBuffer;
    FloatBuffer textureBuffer;
    private ShortBuffer drawListBuffer;
    public DirectDriver(int textureId, Context context) {
        this.textureId=textureId;
        this.context=context;

        ByteBuffer bb= ByteBuffer.allocateDirect(vertexCoords.length*4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertexCoords);
        vertexBuffer.position(0);

        ByteBuffer tbb= ByteBuffer.allocateDirect(textureCoords.length*4);
        tbb.order(ByteOrder.nativeOrder());
        textureBuffer = tbb.asFloatBuffer();
        textureBuffer.put(textureCoords);
        textureBuffer.position(0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        mProgram = ShaderUtil.createProgram(context.getResources(), "myfilter/half_color_vertex.sh", "myfilter/half_color_fragment.sh");
    }
    private static final int COORDS_PER_VERTEX=2;
    public void draw(int selectColorMode){
        GLES20.glUseProgram(mProgram);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,textureId);
        int vPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        int inputTextureCoordinate = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");

        int selectColorModeHandle = GLES20.glGetUniformLocation(mProgram, "selectColorMode");
        GLES20.glUniform1i(selectColorModeHandle,selectColorMode);
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glEnableVertexAttribArray(inputTextureCoordinate);
        GLES20.glVertexAttribPointer(vPosition,COORDS_PER_VERTEX, GLES20.GL_FLOAT,false,COORDS_PER_VERTEX*4,vertexBuffer);
        GLES20.glVertexAttribPointer(inputTextureCoordinate,COORDS_PER_VERTEX, GLES20.GL_FLOAT,false,COORDS_PER_VERTEX*4,textureBuffer);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);//必须要，不然没有图像显示
        GLES20.glDisableVertexAttribArray(vPosition);
        GLES20.glDisableVertexAttribArray(inputTextureCoordinate);
    }
    /*
* 设置阈值最大最小默认值
* */
    float lerpMaxFloat=100.f;
    float lerpMinFloat=50.f;
    public void draw(int selectColorMode,  float[] mMVPMatrix,float[] mTextureMatrix,float maxLerp,float minLerp){
//        GLES20.com
        GLES20.glUseProgram(mProgram);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,textureId);
        //获取变换矩阵vMatrix成员句柄
       int mMatrixHandler= GLES20.glGetUniformLocation(mProgram,"vMatrix");
        //vTextureMat
       int vTextureMatHandler= GLES20.glGetUniformLocation(mProgram,"vTextureMat");

        int vPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        int inputTextureCoordinate = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");

        int selectColorModeHandle = GLES20.glGetUniformLocation(mProgram, "selectColorMode");
        int _LerpMaxFloat = GLES20.glGetUniformLocation(mProgram, "_LerpMaxFloat");
        int _LerpMinFloat = GLES20.glGetUniformLocation(mProgram, "_LerpMinFloat");
        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandler,1,false,mMVPMatrix,0);
        //指定mTextureMatrix的值
        GLES20.glUniformMatrix4fv(vTextureMatHandler,1,false,mTextureMatrix,0);

        GLES20.glUniform1i(selectColorModeHandle,selectColorMode);
        GLES20.glUniform1f(_LerpMaxFloat,maxLerp);//1111
        GLES20.glUniform1f(_LerpMinFloat,minLerp);//
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glEnableVertexAttribArray(inputTextureCoordinate);
        GLES20.glVertexAttribPointer(vPosition,COORDS_PER_VERTEX, GLES20.GL_FLOAT,false,COORDS_PER_VERTEX*4,vertexBuffer);
        GLES20.glVertexAttribPointer(inputTextureCoordinate,COORDS_PER_VERTEX, GLES20.GL_FLOAT,false,COORDS_PER_VERTEX*4,textureBuffer);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);//必须要，不然没有图像显示
        GLES20.glDisableVertexAttribArray(vPosition);
        GLES20.glDisableVertexAttribArray(inputTextureCoordinate);
    }
}
