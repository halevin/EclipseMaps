package com.halevin.eclipse.model;

public class Spline {
	public final double[] arr;


	public Spline() {
		arr = new double[301];
		for(int i=0;i<=300;i++) arr[i] = s3(i/100.0);
	}
	
	
	
	
	public static double s3(double q)
	{
	double m;
	double Sp=0;
	if ((q>=0)&&(q<=1)) {
	Sp = 1.0 - 3.0*q*q/2.0 + 3.0*q*q*q/4.0;
	}
	;
	if ((q>=1)&&(q<=2)) 
	{
	m = 2.0-q;
	Sp = m*m*m/4.0;
	}
	if (q>2) {
	Sp = 0.0;
	}
	;
		return Sp;
	}

	
	
	
}
