package com.halevin.genetic;

public class DataSet {
public double data[][];
private double x_min, x_max, y_min, y_max;
public int data_points;

public void calculate_bounds()
{
	x_min=data[0][0];
	x_max=data[0][0];
	y_min=data[0][1];
	y_max=data[0][1];
for (int i=0; i<data_points; i++) {
	if (data[i][0]>x_max) {x_max = data[i][0];}
	if (data[i][0]<x_min) {x_min = data[i][0];}
	if (data[i][1]>y_max) {y_max = data[i][1];}
	if (data[i][1]<y_min) {y_min = data[i][1];}
}
}

public void calculate2D_bounds()
{
	x_min=data[0][0];
	x_max=data[0][0];
for (int i=0; i<data_points; i++) 
for (int j=0; j<data_points; j++) {
	if (data[i][j]>x_max) {x_max = data[i][j];}
	if (data[i][j]<x_min) {x_min = data[i][j];}
}

}


public double getXMax(){
	return x_max;
}

public double getXMin(){
	return x_min;
}

public double getYMax(){
	return y_max;
}

public double getYMin(){
	return y_min;
}

public void setXMax(double a){
	x_max = a;
}

public void setXMin(double a){
	x_min = a;
}

public void setYMax(double a){
	y_max = a;
}

public void setYMin(double a){
	y_min = a;
}



}
