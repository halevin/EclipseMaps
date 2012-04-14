package com.halevin.eclipse.model;

public class SpaceVector {
	
	public double x,y,z;
	
	public SpaceVector(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public SpaceVector(SpaceVector pos) {
		this.x = pos.x;
		this.y = pos.y;
		this.z = pos.z;
	}

	public SpaceVector() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	
	
	public SpaceVector Multiply(SpaceVector vec)
	{
		double x,y,z;
		x = this.y*vec.z - this.z*vec.y;
		y = this.z*vec.x - this.x*vec.z;
		z = this.x*vec.y - this.y*vec.x;
		return new SpaceVector(x,y,z);
	}
	
	public SpaceVector Multiply(double a)
	{
		double x,y,z;
		x = this.x*a;
		y = this.y*a;
		z = this.z*a;
		return new SpaceVector(x,y,z);
	}
	
	public SpaceVector Divide(double a)
	{
		double x,y,z;
		try 
		{ 
		x = this.x/a;
		y = this.y/a;
		z = this.z/a;
		}
		catch (ArithmeticException e)
		{
			System.out.println("imput parameter is zero");
			x = this.x;
			y = this.y;
			z = this.z;
		}
		return new SpaceVector(x,y,z);
	}
	
	public SpaceVector Append(SpaceVector a)
	{
		double x,y,z;
		x = this.x+a.x;
		y = this.y+a.y;
		z = this.z+a.z;
		return new SpaceVector(x,y,z);
	}

	public SpaceVector Extract(SpaceVector a)
	{
		double x,y,z;
		x = this.x-a.x;
		y = this.y-a.y;
		z = this.z-a.z;
		return new SpaceVector(x,y,z);
	}

	public double square()
	{
		double x,y,z;
		x = this.x*this.x;
		y = this.y*this.y;
		z = this.z*this.z;
		return (x+y+z);
	}
	
	public double Scalar(SpaceVector a)
	{
		return (this.x*a.x+this.y*a.y+this.z*a.z);
	}
	
	public SpaceVector unit()
	{
		return this.Divide(Math.sqrt(this.square()));
	}

	public String toString()
	{
		return "x="+this.x+" y="+this.y+" z="+this.z;
	}
	
	
}
