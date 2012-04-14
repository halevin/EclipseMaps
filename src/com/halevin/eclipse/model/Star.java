package com.halevin.eclipse.model;


public class Star {
	
	public SurfaceElement surface_elements[][] = new SurfaceElement[181][91];
	private double u_lambda, fillingrate, maxP, dark;
	public final int number;
	private boolean sphere;

	public Star(int num, boolean sp, double fr, double ul, double d)
	{
		number = num;
		sphere = sp;
		u_lambda = ul;
		fillingrate = fr;
		dark = d;
	}

	public Star(int num, boolean sp, double fr, double ul)
	{
		number = num;
		sphere = sp;
		u_lambda = ul;
		fillingrate = fr;
		dark = 1;
	}

	public Star(int num, boolean sp, double fr)
	{
		number = num;
		sphere = sp;
		u_lambda = 0.38;
		fillingrate = fr;
		dark = 1;
	}

	public Star(int num, boolean sp)
	{
		number = num;
		sphere = sp;
		u_lambda = 0.38;
		fillingrate = 1.0;
		dark = 1;
	}
	
	public Star(int num)
	{
		number = num;
		sphere = false;
		u_lambda = 0.38;
		fillingrate = 1;
		dark = 1;
	}
	
	
	
	public void setSurfaceElement(int i, int j, SurfaceElement se)
	{
		surface_elements[i][j] = se; 
	}

	public SurfaceElement getSurfaceElement(int i, int j)
	{
		return new SurfaceElement(surface_elements[i][j]); 
	}
	
	public void set_maxP(double a)
	{
		maxP = a;
	}
	
	public double get_u_lambda()
	{
		return u_lambda;
	}
	
	public double get_fillingrate()
	{
		return fillingrate;
	}
	
	public double get_maxP()
	{
		return maxP;
	}
	
	public double get_dark()
	{
		return dark;
	}
	
	public boolean isSphere()
	{
		return sphere;
	}
	
}
