#extension GL_OES_EGL_image_external : require

//eyevoice用对绿色的识别变为红色
      precision mediump float;
      varying vec2 textureCoordinate;
      uniform samplerExternalOES s_texture;
      uniform  int selectColorMode;

       float _LerpMaxInt=100.0;
      float _LerpMinInt=50.0;

      vec3 colorGreen=vec3(0.46, 0.85,0.07);
      vec3 colorWhite=vec3(1.0, 1.0,1.0);
      vec3 colorBlack=vec3(0.0, 0.0,0.0);
      vec3 colorBlue=vec3(0.0, 0.0,1.0);
      vec3 colorYellow=vec3(1.0, 1.0,0.0);

      vec3 colorRed=vec3(1.0, 0.0,0.0);

      float lerpNum;

      vec4 lerp(vec3 colorA,vec3 colorB,float lerpNum){
          return vec4(colorA*lerpNum+colorB*(1.0-lerpNum),0.0);
      }
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
      void main() {

           vec4 tc = texture2D( s_texture, textureCoordinate );

           float r=tc.r*1.0*255.0;
           float g=tc.g*1.0*255.0;
           float b=tc.b*1.0*255.0;

           float gray = tc.r * 0.3 + tc.g * 0.59 + tc.b * 0.11;
           float gray255=gray*255.0;
           lerpNum= ((gray255-50.0)/50.0);
              if(g/b>1.0&&g/b<1.1&&g-r>35.0&&g-r<55.0){
                     gl_FragColor=vec4(colorRed, 0.0);
                    }
             else{gl_FragColor=texture2D( s_texture, textureCoordinate );}








}

