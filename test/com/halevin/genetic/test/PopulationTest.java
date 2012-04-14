package com.halevin.genetic.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.halevin.genetic.Individual;
import com.halevin.genetic.Population;

public class PopulationTest {
	Population p;
	int nind = 100; // Number of individuals
	int npars = 1000; // Number of parameters
	int chr_l = 6; // Chromosome length
	int generations=3;
	double pres = 0.1; // Selection pressure
	double base_mut = 0.01; // Base mutation rate
	int mut_per = 100; // Period of elite mutations
	double ent = 0.0; // Entropy
	double[] ranges_max = new double[npars];
	double[] ranges_min = new double[npars];
	SampleModel sm;

	@Before
	public void initialize() throws Exception {
		for (int i = 0; i < npars; i++) {
			double low = -Math.abs(Math.random());
			double high = -low;
			ranges_min[i] = low;
			ranges_max[i] = high;
		}
		p = new Population(nind, npars, chr_l, pres, base_mut, ranges_min,
				ranges_max, mut_per, ent);
		p.sort();

		sm = new SampleModel();
	}

	@Test
	public void testSort() {

		p.next_generation();
		p.hi_2(sm);
		p.sort();

		for (int j = 1; j < nind; j++) {
				assertTrue("Generation unsorted",
						(p.getFitting(j) >= p.getFitting(j-1)));
		}

	}

	@Test
	public void testOutOfRanges() {


		for (int k = 1; k<=generations; k++) {
		p.next_generation();
		p.hi_2(sm);
		p.sort();

		for (int j = 0; j < nind; j++) {

			for (int i = 0; i < npars; i++) {
				assertTrue("parameter out of ranges",
						((p.getParameters(j)[i] >= ranges_min[i]) && (p
								.getParameters(j)[i] <= ranges_max[i])));
			}
		}
		}
	}

//	@Test
//	public void testZeroDegeneration() {
//
//
//		for (int k = 1; k<=generations; k++) {
//		p.next_generation();
//		p.hi_2(sm);
//		p.sort();
//
//		for (int j = 0; j < nind; j++) {
//
//			for (int i = 0; i < npars; i++) {
//				assertTrue("parameter equal to 0",
//						(p.getParameters(j)[i] != 0));
//			}
//		}
//		}
//	}
//	
//	
	
	
	@Test
	public void testGetMutationFrequency() {
		int par = 10;
		p.setMutationFrequency(par);
		assertEquals(par, p.getMutationFrequency());
	}

	@Test
	public void testGetNMembers() {
		int par = 10;
		p.setNindividuals(par);
		assertEquals(par, p.getNindividuals());
	}

	// @Test
	// public void testPopulation() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetParameters() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetFitting() {
	// fail("Not yet implemented");
	// }

	@Test
	public void testGetEntropy() {
		int nMembers = 100;
		Population p = new Population(nMembers, 1, 1, 0.1, 0.1,
				new double[] { 1.0 }, new double[] { 1.0 }, 1, 0.1);
		double par = 0.01;
		p.setEntropy(par);
		for (int i = 0; i < nMembers; i++)
			assertEquals(par, p.getEntropy(i), 0.01);
	}

	//
	// @Test
	// public void testNext_generation() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testHi_2() {
	// fail("Not yet implemented");
	// }
	//
}
