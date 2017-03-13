class SceneRenderer {
  int sceneNum = 1; // Index of scene to render
  SceneRenderer(int x) {
    sceneNum = x;
  }
  
  void drawScene() {
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
  void drawSceneOne(){ // Cube river (colors = voice frequency)
    stroke(0);
    strokeWeight(1);
    timer += millis();
   
    fill(255);
    
    float bw = fft.getBandWidth();
    int low = round(85.0 / bw);
    float high = round(4000.0 / bw);
    
    int freq = 0;
    float ampMax = 0;
    for (int i = low; i < high; i++) { // Find frequency with highest amplitude in the human vocal range
      if (ampMax < fft.getBand(i)) {freq = i; ampMax = fft.getBand(i);}
    }
    
    if (ampMax < threshold) {freq = round((high + low / 2.0));}
    
    colorMode(HSB, 100);
     
    float hcalc = ((100.0/(high-low)) * (freq));
    
    if (hcalc > lasth) { t+=(0.032*ampMax*0.1);} 
    else {t-=0.032;}
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
      rotateX(millis()/1000.0);
      rotateY(millis()/900.0);
      stroke(0);
      fill(t, s, 100);
      box(100);
      popMatrix();
    }
    
    if (cubes.size() < 1600 && timer > 50000) {
      timer = 0;
      cubes.add(new PVector(random(-4000,4000), 450, -6000));
    }
    
    noStroke();
    noFill();
    colorMode(RGB, 256);
  }
  
  
  // SCENE 2
  boolean l = true;
  boolean o = true;
  int h = 100;
  int xx = 0;
  
  void drawSceneTwo(){ // Point Sphere
    background(0);
    translate(0, -100, xx);
    rotateX(millis()/1200.0);
    rotateY(millis()/900.0);
    rotateZ(millis()/500.0);
    float mul = 300;
    float nscale = 150;
    int radius = 100;
    float w = 7;
    if (beat.isOnset()) {
      mul = 300;
      nscale = 150;
      w += 5;
    } else {
      if (nscale > 100) nscale *= 0.9;
      mul += 0.5;
      w *= .6;
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
         float lt = radians(lat*3);  
         float ln = radians(lng*3);  

         float ns = noise(lt * mul / 100, ln * mul / 100 + millis());

         float x = (radius + ns * nscale) * cos(lt) * cos(ln);
         float y = (radius + ns * nscale) * sin(lt) * (-1);
         float z = (radius + ns * nscale) * cos(lt) * sin(ln);
        
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
    noFill();
    colorMode(RGB, 256);
  }
  
  // SCENE 3
  void drawSceneThree() {
    background(0);
    directionalLight(255, 255, 255, -1, 0, -1);
    
    translate(0, -100, -400);
    
//    stroke(255);
    fill(255);
//    sphere(100);
    
//    rotateX(millis()/1200.0);
//    rotateY(millis()/900.0);
//    rotateZ(millis()/500.0);
    float mul = 1;
    float nscale = 20;
    int radius = 5;
    float w = 5;
/*    if (beat.isOnset()) {
      mul = 300;
      nscale = 250;
      w += 5;
    }*/
    
    if (l) {h--;}
    else {h++;}
    
    if (h == 100 || h == 0) {l = !l;}
    
    if (o) {xx-=3;}
    else {xx+=3;}
    
    if (xx == 600 || xx == -600) {o = !o;}
    

         strokeWeight(20);
         colorMode(HSB, 100);
         stroke(h, 100, 100);
         noStroke();
//         noSmooth();
         fill(h, 100, 100);
//         point(x, y, z);
//         line(0, -100, -400, x, y, z); 

      
        sphereDetail(1);
//        translate(x, y, z);
//        sphere(10);
//         box(x, y, z);
//      ArrayList<PVector> boxes = new ArrayList<PVector>();

      for (int i = 0; i < 50; i++) {
        float x = random(-25, 25);
        float y = random(-125, -75);
        float z = random(-425, -375);
        int size = 10;
        
        if (beat.isOnset()) {
          x = random(-400, 400);
          y = random(-500, 300);
          z = random(-400, 0);
          size = 100;
        }
//        line(0, -100, -400, x, y, z);
        translate(x, y, z);
        box(size);
      }
         
         noStroke();
 //     }
 //   }
    noStroke();
    noFill();
    colorMode(RGB, 256);
  }
  
  // SCENE 4
  void drawSceneFour() {
    colorMode(HSB, 100);
    background(0, 0, 0);
    
//    float radius = 100; // CHECK THIS
//    float circumference = 2.0f * PI * radius;
//    float anglePerBand = circumference / radians(360);

//    float anglePerBand = asin((circumference/fft.avgSize())/radius);
    
//    stroke(100);
    int w = width/fft.avgSize();
    
//    float w = circumference/fft.avgSize();
    
//    float w = tan(anglePerBand) * radius;
    
    strokeWeight(w);
    for (int i = 0; i < fft.avgSize(); i++) {
      stroke(random(100), 100, 100);
      line((i*w) - 500, height/2 + fft.getAvg(i)*4 - 500, -300, (i*w) - 500, height/2 - fft.getAvg(i)*4 - 500, -300);
      line((i*w) - 500, height/2 + fft.getAvg(i)*4 - 500, 300, (i*w) - 500, height/2 - fft.getAvg(i)*4 - 500, 300);
//      line(-300, height/2 + fft.getAvg(i)*4 - 500, (i*w) - 500, -300, height/2 - fft.getAvg(i)*4 - 500, (i*w) - 500);
//      line(300, height/2 + fft.getAvg(i)*4 - 500, (i*w) - 500, 300, height/2 - fft.getAvg(i)*4 - 500, (i*w) - 500);

//      float x = radius*cos(anglePerBand);
//      float z = radius*sin(anglePerBand);
//      line(x*i, height/2 + fft.getAvg(i)*4 - 500, z, x*i, height/2 - fft.getAvg(i)*4 - 500, z);
/*      stroke(0, 0, 100);
      beginShape();
      vertex((i*w) - 300, height/2 + fft.getAvg(i)*4 - 300, -400);
      stroke(random(100), 100, 100);
      vertex((i*w) - 300, height/2 - fft.getAvg(i)*4 - 300, -400);
      endShape();*/
    }
    noStroke();
    noFill();
    colorMode(RGB, 256);
  }
}
