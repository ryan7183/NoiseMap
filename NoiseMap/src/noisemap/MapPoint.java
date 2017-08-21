/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package noisemap;

import javafx.scene.paint.Color;

/**
 *
 * @author Ryan
 */
public class MapPoint {
    int x;
    int y;
    double height;
    double moisture;
    double heat;
    double pressure;
    Color color;
    
    MapPoint(int xPos,int yPos){
        x=xPos;
        y=yPos;
        height=0;
    }
}
