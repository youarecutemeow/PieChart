package wangxin.example.com.piechartlib;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import java.util.Random;

/**
 * Created by wangxin on 17-12-12.
 */

public class Piece  {
    private static final String TAG = "Piece";
    private String name;
    private int weight;
    private int mColor;
    //开始角度
    private float start;
    //横扫角度
    private float sweep;


    public Piece(int weight, int color,String name){
        if(weight == 0)
            throw new IllegalArgumentException("The weight cant`be 0");

        this.weight = weight;
        this.mColor = color;
        this.name = name;
    }
    public Piece(int weight,String name){
        if(weight == 0)
            throw new IllegalArgumentException("The weight cant`be 0");
        this.weight = weight;
        //Random random = new Random(System.currentTimeMillis());
        int r = (int)(Math.random()*255);
        int g = (int)(Math.random()*255);
        int b = (int)(Math.random()*255);
        Log.d(TAG, "Piece: r g b w"+r+" "+g+" "+b+" "+weight);
        this.mColor = Color.rgb(r,g,b);
        this.name = name;
    }
    public int getWeight(){
        return weight;
    }
    public int getColor(){
        return mColor;
    }
    public void setStart(float start){
        this.start = start;
    }
    public void setSweep(float sweep){
        this.sweep = sweep;
    }
    public float getStart(){
        return start;
    }
    public float getSweep(){
        return sweep;
    }
    public String getName(){
        return name;
    }
}
