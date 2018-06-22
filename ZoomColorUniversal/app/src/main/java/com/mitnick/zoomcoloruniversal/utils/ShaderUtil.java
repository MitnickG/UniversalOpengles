package com.mitnick.zoomcoloruniversal.utils;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Mitnick.Guo on 2017/6/16.
 */
public class ShaderUtil {
    private static String TAG="~gyh";
    private ShaderUtil() {
    }

    public static int loadShader(int shaderType,String shaderSource){
        int shader = GLES20.glCreateShader(shaderType);
        if (0!=shader) {
            GLES20.glShaderSource(shader,shaderSource);
            GLES20.glCompileShader(shader);
            int[] compiled=new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS,compiled,0);
            if (compiled[0]==0) {
                Log.d(TAG, "Could not compiled shader:"+shaderType);
                Log.d(TAG, "load shader error:"+ GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader=0;
            }
        }
        return shader;
    }
public static int createProgram(String vertexSource, String fragmentSource){
    int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
    if (vertexShader==0) {
        return 0;
    }
    int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
    if (fragmentShader==0) {
        return 0;
    }
    int program = GLES20.glCreateProgram();
    if (0!=program) {
        GLES20.glAttachShader(program,vertexShader);
        GLES20.glAttachShader(program,fragmentShader);
        GLES20.glLinkProgram(program);
        int[] linked=new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS,linked,0);
        if (linked[0]==0) {
            Log.d(TAG, "linked error:"+ GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program=0;
        }
    }
    return program;
}
    public static int createProgram(Resources res, String vertexShader, String fragmentShader){
        return createProgram(loadShaderFromAssert(vertexShader,res),loadShaderFromAssert(fragmentShader,res));
    }
    public static String loadShaderFromAssert(String sName, Resources resources){
        StringBuilder stringBuilder=new StringBuilder();
        try {
            InputStream is = resources.getAssets().open(sName);
            int ch;
            byte[] buffer=new byte[1024];
            while (-1!=(ch=is.read(buffer))){
                stringBuilder.append(new String(buffer,0,ch));
            }
        } catch (IOException e) {
            return null;
        }
        return stringBuilder.toString().replace("\\r\\n","\n");
    }
}
