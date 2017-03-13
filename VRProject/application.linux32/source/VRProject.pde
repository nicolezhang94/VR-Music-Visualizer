import ddf.minim.*;
import ddf.minim.analysis.*;

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

void setup() {
  noSmooth();
  size( 1920, 1080, P3D );
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

void draw() {
  analyze();
  oculus.draw();
  if (debug) { drawDebug(); }
  if (menu) {drawMenu();}
}

void analyze() {
  fft.forward(song.mix);
  beat.detect(song.mix);
}

// Draws start menu
void drawMenu() {
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
void drawDebug() {
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
  eRadius *= 0.95;
  if ( eRadius < 20 ) eRadius = 20;
  noFill();
  noStroke();
}

// Scene for OculusRift
void onDrawScene(int eye) {
  s.drawScene();
}

// Handles keyboard input
void keyPressed() {
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