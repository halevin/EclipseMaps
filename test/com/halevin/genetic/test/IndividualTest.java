package com.halevin.genetic.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.halevin.genetic.Individual;

public class IndividualTest {

	@Test
	public void testEncode() {
		
		int n_parameters = 10000;
		int chromo_l = 10;
		double[] ranges_max = new double[n_parameters], ranges_min = new double[n_parameters];
		for (int i=0; i<n_parameters; i++) {
			double low = -Math.abs(Math.random());
			double high = -low;
			ranges_min[i] = low;
			ranges_max[i] = high;
		}
		
		try {
			Individual g = new Individual(chromo_l, n_parameters, ranges_max, ranges_min);
			g.encode();
			
			for (int i=0; i<n_parameters; i++) {
				assertTrue("parameter out of boundary",((g.parameters[i]>=ranges_min[i])&&(g.parameters[i]<=ranges_max[i])));
			}
		
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
