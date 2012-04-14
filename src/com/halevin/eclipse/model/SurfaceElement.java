package com.halevin.eclipse.model;


public class SurfaceElement {
	
	public SpaceVector position, orientation;
	public double r, area, Teff, intensity;

	public SurfaceElement() {
		
	}

	
	public SurfaceElement(SurfaceElement surfaceElement) {
		position = new SpaceVector(surfaceElement.getPosition());
		orientation = new SpaceVector(surfaceElement.getOrientation());
		this.r = surfaceElement.get_r();
		this.area = surfaceElement.get_area();
		this.Teff = surfaceElement.get_Teff();
		this.intensity = surfaceElement.get_intensity();
	}

	public void setPosition(SpaceVector pos)
	{
		position = new SpaceVector(pos);
	}

	public void setOrientation(SpaceVector pos)
	{
		orientation = new SpaceVector(pos);
	}
	
	public SpaceVector getPosition()
	{
		return new SpaceVector(position);
	}
	
	public SpaceVector getOrientation()
	{
		return new SpaceVector(orientation);
	}
	
	public void set_r(double a)
	{
		r = a;
	}

	public void set_area(double a)
	{
		area = a;
	}

	public void set_Teff(double a)
	{
		Teff = a;
	}

	public void set_intensity(double a)
	{
		intensity = a;
	}
	
	double get_r()
	{
		return r;
	}

	double get_area()
	{
		return area;
	}
	
	double get_Teff()
	{
		return Teff;
	}
	
	double get_intensity()
	{
		return intensity;
	}
	
	
}
