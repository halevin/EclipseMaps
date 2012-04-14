package com.halevin.eclipse.model;

import static java.lang.Math.*;

import com.halevin.genetic.DataSet;
import com.halevin.genetic.Modelable;

public class EclipseModel implements Modelable {
double sn[], cs[];
DataSet data;
BinarySystem BS;
Options opset;

	public EclipseModel(DataSet data, BinarySystem BS, Options opset){
		this.data = data;
		this.BS = BS;
		this.opset = opset;
		for (int i=0; i<data.data_points; i++){
		data.data[i][3] = BS.Lobe_Light_Curve(BS.stars[0], data.data[i][0]);
		data.data[i][4] = BS.Lobe_Light_Curve(BS.stars[1], data.data[i][0]);

		double fi = (data.data[i][0] - 0.25)*Math.PI*2.0;
		data.data[i][5] = Math.sin(fi)*BS.getCosIncl();
		data.data[i][6] = Math.cos(fi)*BS.getCosIncl();
		}
	}

	public double model(int k, double time, double[] ps){
		SpaceVector a,b,aaa;

		double mo=0, tmp;
		int i;

		b = new SpaceVector();
		b.x=1.0;
		b.y=0;
		b.z=0;

		aaa = new SpaceVector();
		aaa.x = data.data[k][5];
		aaa.y = data.data[k][6];
		aaa.z = BS.getSinIncl();

		a = new SpaceVector();

		for(i=0;i<opset.getPoints();i++)
		{
		a.x=1.0+ps[i*2]*cs[i];
		a.y=ps[i*2]*sn[i];
		a.z = 0.0;

		tmp = BS.Eclipse_Light_Curve(time, a, aaa, true);

//		if (time>0.18) 
//			System.out.println("tmp="+tmp);

		mo += tmp;
		}

//		if (k==39) {System.out.print("i40 "+data.data[k][0]+" "+mo);}
//		if (k==59) {System.out.println(" i60 "+data.data[k][0]+" "+mo);}
		
		
		mo *= ps[opset.getPoints()*2]/opset.getPoints();
		mo+=ps[opset.getPoints()*2+1]*data.data[k][3];
		mo+=ps[opset.getPoints()*2+2];

		return mo;

	}

	
	public double mkmodel(double[] pars) {
		double fitting = 0, o1, tmp, mo, o2;
		int k, l;
		sn = new double[pars.length-3];
		cs = new double[pars.length-3];
		double[] ps = pars;
		
		int limit=opset.getPoints();
		for(l=0;l<limit;l++)
		{
		sn[l]=sin(pars[2*l+1]);
		cs[l]=cos(pars[2*l+1]);
		}

		for(k=0;k<data.data_points;k++)
		{
		o1 = data.data[k][1];
		o2 = data.data[k][2];
		mo = model(k,data.data[k][0], ps);
		tmp = (o1 - mo)/o2;
		fitting += tmp*tmp;
		
		}

		return fitting;
	}	
	
}
