package com.halevin.genetic.test;

public class SampleModel implements com.halevin.genetic.Modelable{

	@Override
	public double mkmodel(double[] pars) {
		// TODO Auto-generated method stub
		double res1=0;
		double s=-1, ss=1;
		for (int i=0;i<pars.length;i++){
			ss*=s;
			res1+=pars[i]*ss;
		}
		double res2=0;
		for (int i=0;i<pars.length;i++){
			res2 +=pars[i];
		}
		
		return Math.abs(res1+res2);
	}

}
