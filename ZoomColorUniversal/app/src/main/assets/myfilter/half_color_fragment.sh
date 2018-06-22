#extension GL_OES_EGL_image_external : require
//ZoomColorUniversal 变色

      precision mediump float;
      varying vec2 textureCoordinate;
      uniform samplerExternalOES s_texture;//https://blog.csdn.net/lyzirving/article/details/79051437
      uniform  int selectColorMode;
        uniform float _LerpMaxFloat;
        uniform float _LerpMinFloat;
       //float _LerpMaxFloat=100.0;
      //float _LerpMinFloat=50.0;



      vec3 colorGreen=vec3(0.46, 0.85,0.07);
      vec3 colorWhite=vec3(1.0, 1.0,1.0);
      vec3 colorBlack=vec3(0.0, 0.0,0.0);
      vec3 colorBlue=vec3(0.0, 0.0,1.0);
      vec3 colorYellow=vec3(1.0, 1.0,0.0);
      vec4 tc = texture2D( s_texture, textureCoordinate );//根据纹理坐标从纹理中采集2D纹理
      float gray = tc.r * 0.3 + tc.g * 0.59 + tc.b * 0.11;
      float gray255=gray*255.0;

     //gray255=gray255+shuzu[(textureCoordinate.x*1280+1)*(textureCoordinate.y*800+1)];

      float distance_max_min=_LerpMaxFloat-_LerpMinFloat;
      float lerpNum= ((gray255-distance_max_min)/distance_max_min);


      vec4 lerp(vec3 colorA,vec3 colorB,float lerpNum){
          return vec4(colorA*lerpNum+colorB*(1.0-lerpNum),0.0);
      }
      vec4 sobel();
      void main() {
           if(gray255>_LerpMaxFloat){
                          if(selectColorMode==0){
                          gl_FragColor=texture2D( s_texture, textureCoordinate );
                          }
                          if(selectColorMode==1){
                          gl_FragColor=vec4(colorWhite, 0.0);
                          }
                          if(selectColorMode==2){
                                           gl_FragColor=vec4(colorBlack, 0.0);
                                           }
                          if(selectColorMode==3){
                                           gl_FragColor=vec4(colorWhite, 0.0);
                                           }
                          if(selectColorMode==4){
                                           gl_FragColor=vec4(colorBlue, 0.0);
                                           }
                          if(selectColorMode==5){
                                           gl_FragColor=vec4(colorYellow, 0.0);
                                           }
                          if(selectColorMode==6){
                                           gl_FragColor=vec4(colorBlack, 0.0);
                                           }
                          if(selectColorMode==7){
                                           gl_FragColor=vec4(colorGreen, 0.0);
                                           }
                          if(selectColorMode==8){
                                           gl_FragColor=vec4(colorBlack, 0.0);
                                           }
                          if(selectColorMode==9){
                                           gl_FragColor=vec4(colorYellow, 0.0);
                                           }
                          if(selectColorMode==10){
                                           gl_FragColor=vec4(colorBlue, 0.0);
                                           }
                                           if(selectColorMode==11){
                                                     gl_FragColor=sobel();
                                            }
                    }else if(gray255<_LerpMinFloat){
                         if(selectColorMode==0){
                          gl_FragColor=texture2D( s_texture, textureCoordinate );
                          }
                          if(selectColorMode==1){
                          gl_FragColor=vec4(colorBlack, 0.0);
                          }
                          if(selectColorMode==2){
                                           gl_FragColor=vec4(colorWhite, 0.0);
                                           }
                          if(selectColorMode==3){
                                           gl_FragColor=vec4(colorBlue, 0.0);
                                           }
                          if(selectColorMode==4){
                                           gl_FragColor=vec4(colorWhite, 0.0);
                                           }
                          if(selectColorMode==5){
                                           gl_FragColor=vec4(colorBlack, 0.0);
                                           }
                          if(selectColorMode==6){
                                           gl_FragColor=vec4(colorYellow, 0.0);
                                           }
                          if(selectColorMode==7){
                                           gl_FragColor=vec4(colorBlack, 0.0);
                                           }
                          if(selectColorMode==8){
                                           gl_FragColor=vec4(colorGreen, 0.0);
                                           }
                          if(selectColorMode==9){
                                           gl_FragColor=vec4(colorBlue, 0.0);
                                           }
                          if(selectColorMode==10){
                                           gl_FragColor=vec4(colorYellow, 0.0);
                                           }
                          if(selectColorMode==11){
                                           gl_FragColor=sobel();
                                           }
                    }
                    else{
                        if(selectColorMode==0){
                            gl_FragColor=texture2D( s_texture, textureCoordinate );
                           }
                        if(selectColorMode==1){
                            gl_FragColor=lerp(colorWhite,colorBlack,lerpNum);
                           }
                        if(selectColorMode==2){
                                           gl_FragColor=lerp(colorBlack,colorWhite,lerpNum);
                                           }
                          if(selectColorMode==3){
                                           gl_FragColor=lerp(colorWhite,colorBlue,lerpNum);
                                           }
                          if(selectColorMode==4){
                                           gl_FragColor=lerp(colorBlue,colorWhite,lerpNum);
                                           }
                          if(selectColorMode==5){
                                             gl_FragColor=lerp(colorYellow,colorBlack,lerpNum);
                                           }
                          if(selectColorMode==6){
                          gl_FragColor=lerp(colorBlack,colorYellow,lerpNum);

                                           }
                          if(selectColorMode==7){
                                          gl_FragColor=lerp(colorGreen,colorBlack,lerpNum);
                                           }
                          if(selectColorMode==8){
                           gl_FragColor=lerp(colorBlack,colorGreen,lerpNum);

                                           }
                          if(selectColorMode==9){
                                           gl_FragColor=lerp(colorYellow,colorBlue,lerpNum);
                                           }
                          if(selectColorMode==10){
                            gl_FragColor=lerp(colorBlue,colorYellow,lerpNum);
                                           }
                                           if(selectColorMode==11){
                                                                                                gl_FragColor=sobel();
                                                                                       }
                    }
}

//sobel边缘检测
 vec4 sobel(){
       //给出卷积内核中各个元素对应像素相对于待处理像素的纹理坐标偏移量
          vec2 offset0=vec2(-1.0,-1.0); vec2 offset1=vec2(0.0,-1.0); vec2 offset2=vec2(1.0,-1.0);
          vec2 offset3=vec2(-1.0,0.0); vec2 offset4=vec2(0.0,0.0); vec2 offset5=vec2(1.0,0.0);
          vec2 offset6=vec2(-1.0,1.0); vec2 offset7=vec2(0.0,1.0); vec2 offset8=vec2(1.0,1.0);
          const float scaleFactor = 0.99;//给出最终求和时的加权因子(为调整亮度)
          //卷积内核中各个位置的值
          float kernelValue0 = 0.0; float kernelValue1 = 1.0; float kernelValue2 = 0.0;
          float kernelValue3 = 1.0; float kernelValue4 = -4.0; float kernelValue5 = 1.0;
          float kernelValue6 = 0.0; float kernelValue7 = 1.0; float kernelValue8 = 0.0;
          vec4 sum;//最终的颜色和
          //获取卷积内核中各个元素对应像素的颜色值
          vec4 cTemp0,cTemp1,cTemp2,cTemp3,cTemp4,cTemp5,cTemp6,cTemp7,cTemp8;
          cTemp0=texture2D(s_texture, textureCoordinate.st + offset0.xy/512.0);
          cTemp1=texture2D(s_texture, textureCoordinate.st + offset1.xy/512.0);
          cTemp2=texture2D(s_texture, textureCoordinate.st + offset2.xy/512.0);
          cTemp3=texture2D(s_texture, textureCoordinate.st + offset3.xy/512.0);
          cTemp4=texture2D(s_texture, textureCoordinate.st + offset4.xy/512.0);
          cTemp5=texture2D(s_texture, textureCoordinate.st + offset5.xy/512.0);
          cTemp6=texture2D(s_texture, textureCoordinate.st + offset6.xy/512.0);
          cTemp7=texture2D(s_texture, textureCoordinate.st + offset7.xy/512.0);
          cTemp8=texture2D(s_texture, textureCoordinate.st + offset8.xy/512.0);
          //颜色求和
          sum =kernelValue0*cTemp0+kernelValue1*cTemp1+kernelValue2*cTemp2+
               kernelValue3*cTemp3+kernelValue4*cTemp4+kernelValue5*cTemp5+
               kernelValue6*cTemp6+kernelValue7*cTemp7+kernelValue8*cTemp8;
         //gl_FragColor = sum * scaleFactor; //进行亮度加权后将最终颜色传递给管线
            return sum * scaleFactor;
          }

