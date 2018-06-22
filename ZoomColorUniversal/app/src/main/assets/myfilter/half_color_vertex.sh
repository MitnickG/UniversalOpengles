//矩阵变换测试用,矩阵变换测试成功
attribute vec4 vPosition;
uniform mat4 vMatrix;
uniform mat4 vTextureMat;
attribute vec2 inputTextureCoordinate;
varying vec2 textureCoordinate;
void main(){
gl_Position = vMatrix*vPosition;
 textureCoordinate=(vTextureMat * vec4(inputTextureCoordinate - vec2(0.5), 0.0,1.0)).xy + vec2(0.5);
//textureCoordinate = inputTextureCoordinate;
}