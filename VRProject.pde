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

boolean debug = false;   // Toggles Debug mode to see FFT chart
boolean menu = true;     // Toggles Menu display
boolean started = false; // Determines if song is playing

int selector = 1;

int visualizerMode = 1;
int currentSong = 1;

void setup() {
  noSmooth();
  size( 1920, 1080, OPENGL);
  minim = new Minim(this);
  oculus = new OculusRift(this);
  oculus.enableHeadTracking();
  song = minim.loadFile("example5.mp3");
  in = minim.getLineIn(Minim.STEREO, 512);
  beat = new BeatDetect();
  ellipseMode(RADIUS);
  eRadius = 20;
  
  // others not needed for initialization
  fft = new FFT(song.bufferSize(), song.sampleRate());
  
  s = new SceneRenderer(visualizerMode); 
}

void draw() {
  colorMode(RGB, 255);
  analyze();
  oculus.draw();
  if (debug) { drawDebug(); }
}

void analyze() {
  if (selector == 2) {
    fft.forward(in.mix);  
  } else {
    fft.forward(song.mix);
    beat.detect(song.mix);
  }
}

// Draws start menu
void drawMenu() {
  colorMode(RGB, 255);
  pushMatrix();
  stroke(255);
  fill(255);
  textSize(200);
  text("START", 400, 500, -4000); 
  text("MICROPHONE", 400, 800, -4000); 
  text("DEBUG", 400, 1100, -4000); 
  text(frameRate, 1100, 1100, -4000);
  text("Visualizer selection: " + visualizerMode, 400, 1400, -4000);
  text("Song selection: " + currentSong, 400, 1700, -4000);
  if (selector == 1) { text("*", 300, 500, -4000);}
  if (selector == 2) { text("*", 300, 800, -4000);}
  if (selector == 3) { text("*", 300, 1100, -4000);}
  if (selector == 4) { text("*", 300, 1400, -4000);}
  if (selector == 5) { text("*", 300, 1700, -4000);}
  noStroke();
  noFill();
  popMatrix();
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
  textMode(SHAPE);
  
  background(0);
  
  if (started) {
    pushMatrix();
    s.drawScene();
    popMatrix();
  }
  if (menu) {
    drawMenu();
  }
  if (!song.isPlaying() && selector == 1 && menu == false) {
    menu = true;
    started = false;
  }
}

// Handles keyboard input
void keyPressed() {
  if(menu) {
    if (key == CODED) {
      if (keyCode == UP) { // move menu cursor up
        if (selector == 1) {
          selector = 5;
        } else {
          selector--;
        }
      }
      if (keyCode == DOWN) { // move menu cursor down
        if (selector == 5) {
          selector = 1;
        } else {
          selector++;
        }
      }
      if (keyCode == LEFT) {
        if (selector == 4) {
          if (visualizerMode == 1) {
            visualizerMode = 4;
          } else {
            visualizerMode--;
          }
        } else if (selector == 5) {
          if (currentSong == 1) {
            currentSong = 7;
          } else {
            currentSong--;
          }
        }
      }
      if (keyCode == RIGHT) {
        if (selector == 4) {
          if (visualizerMode == 4) {
            visualizerMode = 1;
          } else {
            visualizerMode++;
          }
        } else if (selector == 5) {
          if (currentSong == 7) {
            currentSong = 1;
          } else {
            currentSong++;
          }
        }
      }
    }
    if (key == ' ' && selector == 1) { // Toggle menu mode on and off and starts simulation
      menu = false;
      started = true;
      
      fft = new FFT(song.bufferSize(), song.sampleRate());
      fft.logAverages(60,7);
      fft.forward(song.mix);
      beat.detect(song.mix);
      
      s = new SceneRenderer(visualizerMode);
      
      song = minim.loadFile("example" + str(currentSong) + ".mp3");
      song.play();
    }
    if (key == ' ' && selector == 2) { // Toggle menu mode on and off and starts microphone simulation
      menu = false;
      started = true;
      
      fft = new FFT(in.bufferSize(), in.sampleRate());
      fft.logAverages(60,7);
      fft.forward(in.mix);
      beat.detect(in.mix);
      
      s = new SceneRenderer(visualizerMode);
    }
    if (key == ' ' && selector == 3) { // Toggle debug mode on and off
      debug = !debug; 
    }
  }
  
  if (key == BACKSPACE && menu == false && started == true) {
    menu = true;
    started = false;
    song.pause();
    song.rewind();
  }
}
