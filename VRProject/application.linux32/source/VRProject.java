import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import ddf.minim.analysis.*; 
import com.oculusvr.capi.Hmd; 
import com.oculusvr.capi.OvrQuaternionf; 
import com.oculusvr.capi.OvrVector3f; 
import com.oculusvr.capi.TrackingState; 
import java.lang.reflect.Method; 

import com.sun.jna.*; 
import com.sun.jna.ptr.*; 
import com.sun.jna.win32.*; 
import no.hials.vr.*; 
import com.oculusvr.capi.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class VRProject extends PApplet {




Minim minim;
AudioInput in;
AudioPlayer song;
FFT fft;
BeatDetect beat;
float eRadius;
SceneRenderer s;

OculusRift oculus;

boolean debug = false; // Toggles Debug mode to see FFT chart
boolean menu = true;
int selector = 1;

public void setup() {
  
  
  minim = new Minim(this);
  oculus = new OculusRift(this);
  oculus.enableHeadTracking();
  song = minim.loadFile("example3.mp3");
  song.play();
  beat = new BeatDetect();
  ellipseMode(RADIUS);
  eRadius = 20;
  fft = new FFT(song.bufferSize(), song.sampleRate());
  
  s = new SceneRenderer(1); 
}

public void draw() {
  analyze();
  oculus.draw();
  if (debug) { drawDebug(); }
  if (menu) {drawMenu();}
}

public void analyze() {
  fft.forward(song.mix);
  beat.detect(song.mix);
}

// Draws start menu
public void drawMenu() {
  stroke(255);
  fill(255);
  textSize(32);
  text("START", 480, 500); text("START", 1440, 500);
  text("MICROPHONE", 480, 550); text("MICROPHONE", 1440, 550); 
  text("DEBUG", 480, 600); text("DEBUG", 1440, 600); 
  text(frameRate, 480, 650); text(frameRate, 1440, 650); 
  
  if (selector == 1) { text("*", 460, 505); text("*", 1420, 505); }
  if (selector == 2) { text("*", 460, 555); text("*", 1420, 555); }
  if (selector == 3) { text("*", 460, 605); text("*", 1420, 605); } 
  noStroke();
  noFill();
}

// Draws FFT debug stuff
public void drawDebug() {
  stroke(255, 0, 0, 128);
  // Heightmap and waveform
  for(int i = 0; i < fft.specSize(); i++) {
    line(i, height, i, height - fft.getBand(i)*4);
  }
  stroke(255);

  for(int i = 0; i < song.left.size() - 1; i++) {
    line(i, 50 + song.left.get(i)*50, i+1, 50 + song.left.get(i+1)*50);
    line(i, 150 + song.right.get(i)*50, i+1, 150 + song.right.get(i+1)*50);
  }
  //Beat Detection
  float a = map(eRadius, 20, 80, 60, 255);
  fill(60, 255, 0, a);
  if ( beat.isOnset() ) eRadius = 80;
  ellipse(width/2, height/2, eRadius, eRadius);
  eRadius *= 0.95f;
  if ( eRadius < 20 ) eRadius = 20;
  noFill();
  noStroke();
}

// Scene for OculusRift
public void onDrawScene(int eye) {
  s.drawScene();
}

// Handles keyboard input
public void keyPressed() {
  if (key == CODED) {
     if (keyCode == UP) { // move menu cursor up
       if (selector == 1) selector = 3;
       else selector--;
     }
     if (keyCode == DOWN) { // move menu cursor down
       if (selector == 3) selector = 1;
       else selector++;
     }
  }
  if (key == ' ' && selector == 3) { // Toggle debug mode on and off
        debug = !debug; 
  }
  if (key == ' ' && selector == 1) { // Toggle menu mode on and off and starts simulation
        menu = !menu; 
  }
  
}

/*
 Copyright (c) 2015, Sunao Hashimoto All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, 
 this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, 
 this list of conditions and the following disclaimer in the documentation 
 and/or other materials provided with the distribution.
 * Neither the name of the kougaku-navi nor the names of its contributors 
 may be used to endorse or promote products derived from this software 
 without specific prior written permission.
 
 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 Thanks a lot for the following codes.
 
 jherico/jovr
 https://github.com/jherico/jovr
 
 JOVR \u2013 Java bindings for Oculus Rift SDK 0.4.2.0 | Laht's blog
 http://laht.info/jovr-java-bindings-for-oculus-rift-sdk-0-4-0/
 
 ixd-hof/Processing
 https://github.com/ixd-hof/Processing/tree/master/Examples/Oculus%20Rift/OculusRift_Basic
 
 xohm/SimpleOculusRift
 https://github.com/xohm/SimpleOculusRift
 
 mactkg/pg_jack_p3d.pde
 https://gist.github.com/mactkg/66f99c9563c6a043e14e
 
 Solved: Using a Quaternion and Vector to construct a camera view matrix
 https://social.msdn.microsoft.com/Forums/en-US/ec92a231-2dbf-4f3e-b7f5-0a4d9ea4cae2 
 */







class OculusRift {
  
  // Parameters for DK2
  private final int oculus_width  = 1920; // for DK2
  private final int oculus_height = 1080; // for DK2
  private final float fov_deg     = 100;  // for DK2
  private final float z_near      = 10;
  private final float z_far       = 100000;

  private final float scaleFactor   = 2.11f;
  private final float imageScaling  = 2.27f;
  private final int   imageShiftX   = 437;
  private final int   imageCutWidth = 0;
  private final float sensingScale  = 1000.0f;  // for millimeter

  private PApplet _parent;
  private PGraphics pg_backup;
  private Method  onDrawSceneMethod;

  private PGraphics scene;
  private PGraphics fb;
  private PShader barrel;

  private Hmd hmd;
  private boolean isUsingHeadTracking;
  private PMatrix3D headMatrix;
  private PMatrix3D correctionMatrix;

  // -------------------------------------------------------------
  // Public

  // Constructor
  public OculusRift(PApplet app) {
    _parent = app;

    int eye_width = oculus_width/2;
    int eye_height = oculus_height;    
    scene = createGraphics( eye_width, eye_height, P3D);
    fb = createGraphics(oculus_width, oculus_height, P3D);

    barrel = loadShader("barrel_frag.glsl");
    onDrawSceneMethod = getMethodRef( _parent, "onDrawScene", new Class[] {
      int.class
    }
    );

    correctionMatrix = new PMatrix3D();
    headMatrix = new PMatrix3D();
    isUsingHeadTracking = false;
  }

  // Enable head tracking
  public boolean enableHeadTracking() {
    Hmd.initialize();
    hmd = Hmd.create(0);
    if (hmd == null) {
      isUsingHeadTracking = false;
    } else {
      isUsingHeadTracking = true;
      resetHeadState();
    }
    return isUsingHeadTracking;
  }

  // Reset head state by current state.
  public void resetHeadState() {
    PMatrix3D m = getMatrixFromSensor();
    m.invert();
    correctionMatrix = m;
  }

  // Get corrected head state matrix.
  public PMatrix3D getMatrix() {
    PMatrix3D m = getMatrixFromSensor();
    m.apply(correctionMatrix);
    return m;
  }

  // Draw oculus image
  public void draw() {
    updateHeadState();

    int imageMode = _parent.g.imageMode;
    _parent.imageMode(CENTER);
    _parent.blendMode(ADD);
    _parent.background(0);

    // Render left eye
    beginScene();
    runOnDrawSceneMethod(LEFT);
    endScene();
    set_shader(LEFT);
    _parent.shader(barrel);    
    fb.beginDraw();
    fb.background(0);
    fb.image( scene, 50, 0 );    
    fb.fill(0);
    fb.rect( 0, 0, imageCutWidth, fb.height);
    fb.rect( scene.width-imageCutWidth, 0, imageCutWidth, scene.height);    
    fb.endDraw();
    _parent.image(fb, _parent.width/2 + imageShiftX, _parent.height/2, fb.width*imageScaling, fb.height*imageScaling);
    _parent.resetShader();

    // Render right eye
    beginScene();
    runOnDrawSceneMethod(RIGHT);
    endScene();
    set_shader(RIGHT);
    _parent.shader(barrel);
    fb.beginDraw();
    fb.background(0);
    fb.image( scene, scene.width-50, 0 );
    fb.fill(0);
    fb.rect( scene.width, 0, imageCutWidth, scene.height );
    fb.rect( fb.width - imageCutWidth, 0, imageCutWidth, fb.height );
    fb.endDraw();
    _parent.image(fb, _parent.width/2 - imageShiftX, _parent.height/2, fb.width*imageScaling, fb.height*imageScaling);   
    _parent.resetShader();

    _parent.blendMode(BLEND);
    _parent.imageMode(imageMode);
  }


  // -------------------------------------------------------------
  // Private

  private void updateHeadState() {
    if (!isUsingHeadTracking) return;
    headMatrix = getMatrix();
  }

  private void applyHeadState() {
    if (!isUsingHeadTracking) return;
    applyMatrix(headMatrix);
  }

  private PMatrix3D getMatrixFromSensor() {
    TrackingState sensorState = hmd.getSensorState(Hmd.getTimeInSeconds());
    OvrVector3f pos = sensorState.HeadPose.Pose.Position;
    OvrQuaternionf quat = sensorState.HeadPose.Pose.Orientation;
    return calcMatrix(pos.x, pos.y, pos.z, quat.x, quat.y, quat.z, quat.w );
  }  

  private void runOnDrawSceneMethod(int eye) {
    try {
      onDrawSceneMethod.invoke( _parent, new Object[] { 
        (int)eye
      } 
      );
    } 
    catch (Exception e) {
    }
  }

  private Method getMethodRef(Object obj, String methodName, Class[] paraList) {
    Method ret = null;
    try {
      ret = obj.getClass().getMethod(methodName, paraList);
    }
    catch (Exception e) {
    }
    return ret;
  }

  private void beginScene() {
    scene.beginDraw();
    pg_backup = _parent.g;
    _parent.g = scene;
    resetMatrix();
    perspective( radians(fov_deg), scene.width*1.0f/scene.height, z_near, z_far);
    applyHeadState();
  }

  private void endScene() {
    _parent.g = pg_backup;
    scene.endDraw();
  }

  private void set_shader(int eye) {
    float x = 0.0f;
    float y = 0.0f;
    float w = 0.5f;
    float h = 1.0f;
    float DistortionXCenterOffset = 0.25f;
    float as = w/h;

    float K0 = 1.0f;
    float K1 = 0.22f;
    float K2 = 0.24f;
    float K3 = 0.0f;

    if (eye == LEFT) {
      x = 0.0f;
      y = 0.0f;
      w = 0.5f;
      h = 1.0f;
      DistortionXCenterOffset = 0.25f;
    } else if (eye == RIGHT) {
      x = 0.5f;
      y = 0.0f;
      w = 0.5f;
      h = 1.0f;
      DistortionXCenterOffset = -0.25f;
    }

    barrel.set("LensCenter", x + (w + DistortionXCenterOffset * 0.5f)*0.5f, y + h*0.5f);
    barrel.set("ScreenCenter", x + w*0.5f, y + h*0.5f);
    barrel.set("Scale", (w/2.0f) * scaleFactor, (h/2.0f) * scaleFactor * as);
    barrel.set("ScaleIn", (2.0f/w), (2.0f/h) / as);
    barrel.set("HmdWarpParam", K0, K1, K2, K3);
  }

  private PMatrix3D calcMatrix(float px, float py, float pz, float qx, float qy, float qz, float qw) {
    PMatrix3D mat = new PMatrix3D();

    // calculate matrix terms
    float two_xSquared = 2 * qx * qx;
    float two_ySquared = 2 * qy * qy;
    float two_zSquared = 2 * qz * qz;
    float two_xy = 2 * qx * qy;
    float two_wz = 2 * qw * qz;
    float two_xz = 2 * qx * qz;
    float two_wy = 2 * qw * qy;
    float two_yz = 2 * qy * qz;
    float two_wx = 2 * qw * qx;

    // update view matrix orientation
    mat.m00 = 1 - two_ySquared - two_zSquared;
    mat.m01 = two_xy + two_wz;
    mat.m02 = two_xz - two_wy;
    mat.m10 = two_xy - two_wz;
    mat.m11 = 1 - two_xSquared - two_zSquared;
    mat.m12 = two_yz + two_wx;
    mat.m20 = two_xz + two_wy;
    mat.m21 = two_yz - two_wx;
    mat.m22 = 1 - two_xSquared - two_ySquared;

    // change right-hand to left-hand
    mat.preApply(
    1, 0, 0, 0, 
    0, -1, 0, 0, 
    0, 0, 1, 0, 
    0, 0, 0, 1
      );
    mat.scale(1, -1, 1);

    // Position    
    mat.m03 = sensingScale * pz;
    mat.m13 = sensingScale * py;
    mat.m23 = sensingScale * (-px);

    return mat;
  }
}
class SceneRenderer {
  int sceneNum = 1; // Index of scene to render
  SceneRenderer(int x) {
    sceneNum = x;
  }
  
  public void drawScene() {
    if (sceneNum == 1) { drawSceneOne();}
    if (sceneNum == 2) { drawSceneTwo();}
    if (sceneNum == 3) { drawSceneThree();}
    if (sceneNum == 4) { drawSceneFour();}
  }
  
  ArrayList<PVector> cubes = new ArrayList<PVector>();
  float lasth = 50;
  float t = 50;
  float threshold = 10;
  float timer = 0;
  
  // SCENE 1
  public void drawSceneOne(){ // Cube river (colors = voice frequency)
    timer += millis();
   
    fill(255);
    
    float bw = fft.getBandWidth();
    int low = round(85.0f / bw);
    float high = round(4000.0f / bw);
    
    int freq = 0;
    float ampMax = 0;
    for (int i = low; i < high; i++) { // Find frequency with highest amplitude in the human vocal range
      if (ampMax < fft.getBand(i)) {freq = i; ampMax = fft.getBand(i);}
    }
    
    if (ampMax < threshold) {freq = round((high + low / 2.0f));}
    
    colorMode(HSB, 100);
    
    float hcalc = ((100.0f/(high-low)) * (freq));
    
    if (hcalc > lasth) { t+=(0.032f*ampMax*0.1f);} 
    else {t-=0.032f;}
    lasth = t;
    
    float s = 100;
    
     background(100 - t , 100, 100);
    
    for (PVector c : cubes) {  //0, -250, -800 );
      pushMatrix();
      translate(0,0,0);
      if (c.z > 6000) {
          c.x = random(-4000,4000);
          c.y = 450;
          c.z = -6000;
      }
      c.z += 3;
      translate( c.x, c.y, c.z++);
      rotateX(millis()/1000.0f);
      rotateY(millis()/900.0f);
      stroke(0);
      fill(t, s, 100);
      box(100);
      popMatrix();
    }
    
    if (cubes.size() < 1600 && timer > 50000) {
      timer = 0;
      cubes.add(new PVector(random(-4000,4000), 450, -6000));
    }
    
    noFill();
    noStroke();
  }
  
  
  // SCENE 2
  boolean l = true;
  boolean o = true;
  int h = 100;
  int xx = 0;
  
  public void drawSceneTwo(){ // Point Sphere
    background(0);
    translate(0, -100, xx);
    rotateX(millis()/1200.0f);
    rotateY(millis()/900.0f);
    rotateZ(millis()/500.0f);
    float mul = 300;
    float nscale = 150;
    int radius = 100;
    float w = 7;
    if (beat.isOnset()) {
      mul = 300;
      nscale = 150;
      w += 5;
    } else {
      if (nscale > 100) nscale *= 0.9f;
      mul += 0.5f;
      w *= .6f;
      w = floor(w);
    }
    
    if (l) {h--;}
    else {h++;}
    
    if (h == 100 || h == 0) {l = !l;}
    
    if (o) {xx-=3;}
    else {xx+=3;}
    
    if (xx == 600 || xx == -600) {o = !o;}
    
    stroke(255, 255, 255);
    for(int lat = -30; lat < 30; lat++)
    {
      for(int lng = -60; lng < 60; lng++)
      {
         float _lat = radians(lat*3);  
         float _lng = radians(lng*3);  

         float n = noise(_lat * mul / 100, _lng * mul / 100 + millis());

         float x = (radius + n * nscale) * cos(_lat) * cos(_lng);
         float y = (radius + n * nscale) * sin(_lat) * (-1);
         float z = (radius + n * nscale) * cos(_lat) * sin(_lng);
        
         float s = 100;
         
         PVector distfromcamera = new PVector(0-x, 0-y, 0-z);
         if (distfromcamera.mag() > 300) {s/=4;}
         
         float b = min((w * 10), 75);
         
         strokeWeight(w);
         colorMode(HSB, 100); // HUE, SAT, BRI
         stroke(h,s,b);
         noSmooth();
         point(x, y, z);
         noStroke();
      }
    }
    noStroke();
    colorMode(RGB, 256);
  }
  
  public void drawSceneThree(){}
  public void drawSceneFour(){}
}
  public void settings() {  size( 1920, 1080, P3D );  noSmooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "VRProject" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
