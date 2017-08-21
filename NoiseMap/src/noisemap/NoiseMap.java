/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package noisemap;

import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.canvas.*;
import javafx.scene.layout.Pane;

/**
 *
 * @author Ryan
 */
public class NoiseMap extends Application {
    
    final Canvas canvas = new Canvas();
    final double waterLevel = 0.08;
    final int numOfClimateSimSteps = 50;
    
    OpenSimplexNoise noise;//Used to generate height map
    MapPoint map[][];//The map data
    double zoomX,zoomY;
    
    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        //Cavnas settings
        canvas.setHeight(800);
        canvas.setWidth(1300);
        canvas.setLayoutX(0);
        canvas.setLayoutY(0);
        root.getChildren().add(canvas);
        //End Canvas settings
        
        //Generate Map button
        Button generate = new Button();
        generate.setLayoutX(0);
        generate.setLayoutY(800);
        generate.setText("Generate");
        generate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    generateMap();
                } catch (InterruptedException ex) {
                    Logger.getLogger(NoiseMap.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        root.getChildren().add(generate);
        //End of Generate Map button
        
        //Dislpay height map button
        Button dHeight = new Button();
        dHeight.setLayoutX(65);
        dHeight.setLayoutY(800);
        dHeight.setText("Display Height Map");
        dHeight.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                displayHeightMap();
            }
        });
        root.getChildren().add(dHeight);
        //End of display heighrt map button
        
        //Display land water button
        Button landWaterBtn = new Button();
        landWaterBtn.setLayoutX(190);
        landWaterBtn.setLayoutY(800);
        landWaterBtn.setText("Display land and water");
        landWaterBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                displayLandWater();
            }
        });
        root.getChildren().add(landWaterBtn);
        //End of display land water button
        
        //Display heat map button
        Button heatMapBtn = new Button();
        heatMapBtn.setLayoutX(320);
        heatMapBtn.setLayoutY(800);
        heatMapBtn.setText("Display heat map");
        heatMapBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                displayHeatMap();
            }
        });
        root.getChildren().add(heatMapBtn);
        //End of display heat map button
        
        //Alt Display heat map button
        Button altHeatMapBtn = new Button();
        altHeatMapBtn.setLayoutX(320);
        altHeatMapBtn.setLayoutY(825);
        altHeatMapBtn.setText("Alt display heat map");
        altHeatMapBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                altDisplayHeatMap();
            }
        });
        root.getChildren().add(altHeatMapBtn);
        //End of alt display heat map button
        
        //Display moisture map button
        Button moistMapBtn = new Button();
        moistMapBtn.setLayoutX(430);
        moistMapBtn.setLayoutY(800);
        moistMapBtn.setText("Display moisture map");
        moistMapBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                displayMoistureMap();
            }
        });
        root.getChildren().add(moistMapBtn);
        //End of display moisture map button
        
        //Display moisture map button
        Button displayBiomeBtn = new Button();
        displayBiomeBtn.setLayoutX(580);
        displayBiomeBtn.setLayoutY(800);
        displayBiomeBtn.setText("Display biome map");
        displayBiomeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                displayBiomes();
            }
        });
        root.getChildren().add(displayBiomeBtn);
        //End of display moisture map button
        
        Scene scene = new Scene(root, 1920, 1080);
        
        primaryStage.setResizable(true);
        primaryStage.setTitle("NoiseMap");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        try {
            generateMap();
        } catch (InterruptedException ex) {
            Logger.getLogger(NoiseMap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void generateMap() throws InterruptedException{
        Random rand = new Random();
        long seed = rand.nextLong();
        
        int sizeX=(int)canvas.getWidth();
        int sizeY=(int)canvas.getHeight();
        map= new MapPoint[sizeX][sizeY];
        initializeMapPoints();
        getHeightMap("World","Simplex",sizeY,sizeX, seed);
        generateBiomes(seed);
        displayBiomes();
    }//generateMap
    
    public void displayBiomes(){
        Color c;
        String biome;
        int s=0;//Shading
        GraphicsContext gc =  canvas.getGraphicsContext2D();
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){
                
                if(x!=0){
                    if(map[x][y].height<map[x-1][y].height){
                        s = 30;
                    }else{
                        s=0;
                    }
                }
                if(map[x][y].height<=waterLevel){
                    if((map[x][y].heat)>.5){
                        c = Color.rgb(0,0,255);//Water
                    }else{
                        c = Color.rgb(255, 255, 255);//Glacier
                    }
                }else if(map[x][y].heat<=.6){
                    if(map[x][y].moisture<=0.1){
                        c=Color.rgb(175-s,179-s,181-s);//Rock/Tundra
                    }else{
                        c=Color.rgb(255-s,255-s,255-s);//Snow
                    }
                }else if(map[x][y].heat<=.7){
                   if(map[x][y].moisture<=0.2){
                       // c=Color.rgb(224-s,207-s,96-s);//Sand
                        //c=Color.rgb(56-s,104-s,62-s);//Artic plant
                        c=Color.rgb(89-s,165-s,92-s);//Forest
                    }else{
                        c=Color.rgb(89-s,165-s,92-s);//Forest
                    } 
                }else{
                    if(map[x][y].moisture<=0.2){
                        if(map[x][y].heat<.8){
                            c=Color.rgb(89-s,165-s,92-s);//Forest
                        }else{
                            c=Color.rgb(224-s,207-s,96-s);//Sand
                        }
                    }else{
                        c=Color.rgb(36-s,132-s,38-s);//Jungle
                    }
                }
                
                gc.setFill(c);
                gc.fillRect(x,y,1,1);
            }
        }
        
    }//displayBiomes
    
    public String getBiome(int heat, int moisture){
        String[][] biomeType ={
            //Coldest Colder Cold        Hot      Hottter  Hottest
            {"ICE","TUNDRA","GRASSLAND","DESERT","DESERT","DESERT"},
            {"ICE","TUNDRA","GRASSLAND","DESERT","DESERT","DESERT"},
            {"ICE","TUNDRA","WOODLAND","WOODLAND","SAVANNA","SAVANNA"},
            {"ICE","TUNDRA","BOREALFOREST","WOODLAND","SAVANNA","SAVANNA"},
            {"ICE","TUNDRA","BOREALFOREST","SEASONALFOREST","TROPICALRAINFOREST","TROPICALRAINFOREST"},
            {"ICE","TUNDRA","BOREALFOREST","TEMPERATRAINFOREST","TROPICALRAINFOREST","TROPICALRAINFOREST"}
        };
        return biomeType[heat][moisture];  
    }//getBiome
    public void displayMoistureMap(){
        Color c;
        GraphicsContext gc =  canvas.getGraphicsContext2D();
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){
                c = Color.rgb((int)(255-(map[x][y].moisture*255)),(int) (255-(map[x][y].moisture*255)),255);
                gc.setFill(c);
                gc.fillRect(x,y,1,1);
            }
        }
    }//displayMoistureMap
    
    public void displayHeatMap(){
        Color c;
        GraphicsContext gc =  canvas.getGraphicsContext2D();
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){
                if((map[x][y].heat*7)<=(1)){
                    c = Color.rgb(255, 0, 255);
                }else if(map[x][y].heat*7<=(2)){
                    c = Color.rgb(0, 0, 255);
                }else if(map[x][y].heat*7<=(3)){
                    c= Color.rgb(0, 255, 255);
                }else if(map[x][y].heat*7<=(4)){
                    c = Color.rgb(0,255,0);
                }else if(map[x][y].heat*7<=(5)){
                    c = Color.rgb(255,255,0);
                }else if(map[x][y].heat*7<=(6)){
                    c = Color.rgb(255,150,0);
                }else{
                    c=Color.rgb(255,0,0);
                }
                //c = Color.rgb((int) (map[x][y].heat*255),(int) (map[x][y].heat*255),(int) (map[x][y].heat*255));

                gc.setFill(c);
                gc.fillRect(x,y,1,1);
            }
        }
    }
    
    public void altDisplayHeatMap(){
        Color c;
        GraphicsContext gc =  canvas.getGraphicsContext2D();
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){
                c = Color.rgb((int) (map[x][y].heat*255),255,0);
                gc.setFill(c);
                gc.fillRect(x,y,1,1);
            }
        }
    }
    
    
    public void displayLandWater(){
        Color c;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, map.length, map[0].length);
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){
                if(map[x][y].height <=waterLevel){
                    c = Color.rgb(11,69,163);
                }else{
                    c = Color.rgb(255,255,255);
                }
                gc.setFill(c);
                gc.fillRect(x,y,1,1);
                
            }
        }
    }
    
    public void displayHeightMap(){
        Color c;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, map.length, map[0].length);
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){
                c = Color.rgb((int) (map[x][y].height*255),(int) (map[x][y].height*255),(int) (map[x][y].height*255));

                gc.setFill(c);
                gc.fillRect(x,y,1,1);
                
            }
        }
        
    }//displayHeightMap
    
    public MapPoint[][] initializeMapPoints(){
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){
                map[x][y]=new MapPoint(x,y);
            }
        }
        return map;
    }//initializeMapPoints
    
    public MapPoint[][] getHeightMap(String type,String method,int sizeY,int sizeX ,long seed){
        double[][] values;
        int xPos;
        int yPos;
        
        Random rand = new Random();

            noise  = new OpenSimplexNoise(seed);
            xPos=rand.nextInt(((map.length-(sizeX/3))-(0))+1)+0;
            yPos=rand.nextInt(((map[0].length-(sizeX/3))-(0))+1)+0;
            xPos =0;
            yPos=0;
            values = generateContinents(sizeY,sizeX,0,sizeY/(rand.nextInt(10-4+1)+4));
            map = addValues(map,values,xPos,yPos);

        
        return map;
    }//getHeightMap

    public MapPoint[][] addValues(MapPoint[][] map, double[][] values,int xPos, int yPos){
       
        for(int x = 0 ; x<values.length;x++){
            for(int y = 0;y<values[0].length;y++){
                if(false/*map[x+xPos][y+yPos].height==0*/){
                    map[x+xPos][y+yPos].height = values[x][y];
                }else{
                    map[x+xPos][y+yPos].height = (map[x+xPos][y+yPos].height+(0.5*values[x][y]))/1.5;
                }
                
            }
        }
        return map;
    }
    public double[][] generateContinents(int height, int width,int mOffSetX,int mOffSetY){
        zoomX =width/10;
        zoomY = height/10;
        double d;
        double a=0.6;//Pushes everything up
        double b=.1;//pushes edges down
        double c=.375;//rate of drop off
        Random rand = new Random();
        int middleX=(width/2)-((rand.nextInt(3)-1)*mOffSetX);
        int middleY=(height/2)-((rand.nextInt(3)-1)*mOffSetY);
        
        double[][] values = new double[width][height];
        double e;
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                e = (Math.abs(noise.eval(x/zoomX,y/zoomY))+Math.abs(.5*(noise.eval(2*x/zoomX,2*y/zoomY)))+Math.abs(.25*(noise.eval(3*x/zoomX,3*y/zoomY))))/1.75;
                
                if(e<0){
                    e=0;
                }
                e = Math.pow(e,.8);
                d=Math.sqrt(Math.pow(x-middleX,2)+Math.pow(y-middleY,2));
                e = e+a*1-b*Math.pow(d,c);
                
                if(e<0){
                    e=0;
                }else if(e>1){
                    e=1;
                }
                values[x][y] = e;
            }
        }
        
        return values;
    }
    
    public double[][] genratePoles(int height, int width){
        zoomX =width;
        zoomY = height;
        double[][] values = new double[width][height];
        double e;
        int c;
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){
                e = (Math.abs(noise.eval(x/zoomX,y/zoomY))+(.5*(noise.eval(2*x/zoomX,2*y/zoomY)))+(.25*(noise.eval(3*x/zoomX,3*y/zoomY))))/1.75;
                if(e<0){
                    e=0;
                }
                values[x][y] = Math.pow(e,1.5) ;
            }
        }
        return values;
    }
    
    public double[][] generateIslands(int height,int width){
        zoomX =width;
        zoomY = height;
        double[][] values = new double[width][height];
        double e;
        int c;
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){
                e = (Math.abs(noise.eval(x/zoomX,y/zoomY))+(.5*(noise.eval(2*x/zoomX,2*y/zoomY)))+(.25*(noise.eval(3*x/zoomX,3*y/zoomY))))/1.75;
                if(e<0){
                    e=0;
                }
                values[x][y] = Math.pow(e,1.5) ;
            }
        }
        return values;
    }//generateIslands
    
    public void generateBiomes(long seed) throws InterruptedException{
        InitialHeatMap heatMap = new InitialHeatMap(map, waterLevel,numOfClimateSimSteps);
        InitialMoistureMap moistureMap = new InitialMoistureMap(map,waterLevel,seed,zoomX,zoomY,numOfClimateSimSteps);
        heatMap.start();
        moistureMap.start();
        heatMap.join();
        moistureMap.join();
        applyHeatMoistureMaps(heatMap.values, moistureMap.values);
    }//generateBiomes
    
    public void applyHeatMoistureMaps(double[][] heat, double[][] moisture){
        for(int x=0;x<map.length;x++){
          for(int y=0;y<map[0].length;y++){
              map[x][y].heat = heat[x][y];
              map[x][y].moisture = moisture[x][y];
          }
        }
    }//applyHeatMOistureMaps
    
    public static void main(String[] args) {
        launch(args);
    }
    
   
}

class InitialHeatMap extends Thread{
    double[][] values; 
    MapPoint[][] map;
    double waterLevel;
    int steps;
    InitialHeatMap(MapPoint[][] m, double w,int s){
        this.map = m;
        this.waterLevel = w;
        this.steps =(int)(s*3);
    }
    @Override
    public void run(){
      double distance;
      double temperature;
      double middleCoord = map[0].length/2;
      double dWeight = 0.5;//Importance of height and weight for temperature
      double hWeight = 0.5;
      double h;
      values = new double[map.length][map[0].length];
      for(int x=0;x<map.length;x++){
          for(int y=0;y<map[0].length;y++){
              distance =(middleCoord-Math.abs(middleCoord-y));
              h = map[x][y].height;
              if(h<=waterLevel){
                  h=waterLevel;
              }
              temperature= (((distance/middleCoord)*dWeight)+hWeight)-(h*hWeight);//Keeps the hieght just as important as distance from equator for temperature
              /*temperature = temperature-0.5;
              if(temperature<0){
                  temperature = 0;
              }*/
              values[x][y] = temperature;
          }
      }
      simulate();
    }
    public void simulate(){
        double totalNHeat;
        double avgHeat;
        int count;
        double e;
        for(int i=0;i<steps;i++){
            for(int x=0;x<map.length;x++){
                for(int y=0;y<map[0].length;y++){
                    totalNHeat = 0;
                    count = 0;
                    if((x-1)>=0&&(y-1)>=0){//Check upper left
                        totalNHeat =totalNHeat+values[x-1][y-1];
                        count =count+1;
                    }
                    if((x-1)>=0){//Check left
                        totalNHeat =totalNHeat+values[x-1][y];
                        count =count+1;
                    }
                    if((x-1)>=0&&(y+1)<map[0].length){//Check lower left
                        totalNHeat =totalNHeat+values[x-1][y+1];
                        count =count+1;
                    }
                    if((y-1)>=0){//Check upper
                        totalNHeat =totalNHeat+values[x][y-1];
                        count =count+1;
                    }
                    if((y+1)<map[0].length){//Check down
                        totalNHeat =totalNHeat+values[x][y+1];
                        count =count+1;
                    }
                    if((x+1)<map.length&&(y-1)>=0){//Check upper right
                        totalNHeat =totalNHeat+values[x+1][y-1];
                        count =count+1;
                    }
                    if((x+1)<map.length){//Check right
                        totalNHeat =totalNHeat+values[x+1][y];
                        count =count+1;
                    }
                    if((x+1)<map.length&&(y+1)<map[0].length){//Check lower right
                        totalNHeat =totalNHeat+values[x+1][y];
                        count =count+1;
                    }

                    avgHeat = totalNHeat/count;
                    e=0;
                    if(map[x][y].height>=.6){
                        e=e-.0005;
                    }
                    if(y<map[0].length*0.05||y>map[0].length*0.95){
                        e=e-.0005;
                    }
                    if(((values[x][y]+avgHeat)/2)+e>1){
                        e=1;
                    }else if(((values[x][y]+avgHeat)/2)+e<0){
                        e=0;
                    }else{
                        e=((values[x][y]+avgHeat)/2)+e;
                    }

                    values[x][y]= e;
                }
            }
        }
    }//simulate
    
}

class InitialMoistureMap extends Thread{
    double[][] values;
    MapPoint[][] map;
    double waterLevel;
    long seed;
    double zoomX, zoomY;
    int steps;
    InitialMoistureMap(MapPoint[][] m, double w, long sd,double x,double y,int s){
        this.map=m;
        this.waterLevel = w;
        this.seed = sd;
        this.zoomX = x;
        this.zoomY = y;
        this.steps = s;
    }//Constructor
    @Override
    public void run(){
        double e;
        OpenSimplexNoise noise = new OpenSimplexNoise(seed+1);
        values = new double[map.length][map[0].length];
        for(int x=0;x<map.length;x++){
            for(int y=0;y<map[0].length;y++){
                e = ((((1*Math.abs(noise.eval(2*(x/zoomX/10),2*(y/zoomY/8))))+(0.5*Math.abs(noise.eval(3*(x/zoomX),3*(y/zoomY))))+(0.25*Math.abs(noise.eval(5*(x/zoomX/.5),5*(y/zoomY)))))+0.25)/2);
                if(e<0){
                    e=0;
                }
                if(map[x][y].height<=waterLevel){
                    e+=0.2;
                    if(e>1){
                        e=1;
                    }
                }
                values[x][y]=Math.pow(e,2);
                
            }
        }
        simulate();
        
    }//run
    
    public void simulate(){
        double totalNMoist;
        double avgMoist;
        int count;
        double e;
        for(int i=0;i<steps;i++){
            for(int x=0;x<map.length;x++){
                for(int y=0;y<map[0].length;y++){
                    totalNMoist = 0;
                    count = 0;
                    if((x-1)>=0&&(y-1)>=0){//Check upper left
                        totalNMoist =totalNMoist+values[x-1][y-1];
                        count =count+1;
                    }
                    if((x-1)>=0){//Check left
                        totalNMoist =totalNMoist+values[x-1][y];
                        count =count+1;
                    }
                    if((x-1)>=0&&(y+1)<map[0].length){//Check lower left
                        totalNMoist =totalNMoist+values[x-1][y+1];
                        count =count+1;
                    }
                    if((y-1)>=0){//Check upper
                        totalNMoist =totalNMoist+values[x][y-1];
                        count =count+1;
                    }
                    if((y+1)<map[0].length){//Check down
                        totalNMoist =totalNMoist+values[x][y+1];
                        count =count+1;
                    }
                    if((x+1)<map.length&&(y-1)>=0){//Check upper right
                        totalNMoist =totalNMoist+values[x+1][y-1];
                        count =count+1;
                    }
                    if((x+1)<map.length){//Check right
                        totalNMoist =totalNMoist+values[x+1][y];
                        count =count+1;
                    }
                    if((x+1)<map.length&&(y+1)<map[0].length){//Check lower right
                        totalNMoist =totalNMoist+values[x+1][y];
                        count =count+1;
                    }

                    avgMoist = totalNMoist/count;
                    e=0;
                    /*if(avgMoist<values[x][y]){
                        e= -0.1;
                    }else{
                        e=0.1;
                    }*/

                    if(((values[x][y]+avgMoist)/2)+e>1){
                        e=1;
                    }else if(((values[x][y]+avgMoist)/2)+e<0){
                        e=0;
                    }else{
                        e=((values[x][y]+avgMoist)/2)+e;
                    }
                    values[x][y]= e;
                }
            }
        }
    }//simulate
}
