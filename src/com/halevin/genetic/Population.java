package com.halevin.genetic;

public class Population {

	private int n_individuals, iteration, elite_mutation_frequency;
	private static int parameters;
	private static int chromo_l;
	private static double mutation_rate0, entropy;
	private static double pressure;
	private double mutation_rate;
	double[] ranges_min;
	double[] ranges_max;
	private Individual[] individuals, individuals_tmp;
	private double chi_mean;

	public void setMutationFrequency(int mm) {
		elite_mutation_frequency = mm;
	}

	public int getMutationFrequency() {
		return elite_mutation_frequency;
	}

	public void setMutationRate(double n) {
		mutation_rate0 = n;
	}

	public void setEntropy(double n) {
		entropy = n;
	}

	public void setPressure(double n) {
		pressure = n;
	}

	public void setNindividuals(int n) {
		n_individuals = n;
	}

	public int getNindividuals() {
		return n_individuals;
	}

	public Population(int nindividuals, int params, int cl, double press,
			double mr, double par_min_array[], double par_max_array[], int mm,
			double ent) {
		n_individuals = nindividuals;
		parameters = params;
		chromo_l = cl;
		pressure = press;
		mutation_rate0 = mr;
		individuals = new Individual[nindividuals];
		individuals_tmp = new Individual[nindividuals];
		ranges_min = par_min_array;
		ranges_max = par_max_array;
		iteration = 0;
		elite_mutation_frequency = mm;
		entropy = ent;

		for (int i = 0; i < nindividuals; i++) {
			try {
				individuals[i] = new Individual(chromo_l, parameters, ranges_max, ranges_min);
				individuals_tmp[i] = new Individual(chromo_l, parameters, ranges_max, ranges_min);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//Number of parameters or chromosome length exceeds maximal value
				e.printStackTrace();
			}
		}

	}

	public double[] getParameters(int g) {
		return individuals[g].parameters;
	}

	public double getFitting(int g) {
		return individuals[g].getFitting();
	}

	public double getEntropy(int g) {
		return individuals[g].getEntropy();
	}

	private void mutate() {
		int i, j;
		double dtmp, tmp, tmp1, ttmp, ttmp1, ftmp;

		ftmp = (chi_mean - individuals[0].getFitting()) / chi_mean;
		if (ftmp < 0.5)
			mutation_rate = mutation_rate0 * 0.3 / ftmp;
		else
			mutation_rate = mutation_rate0;

		for (i = 1; i < n_individuals; i++) {
			dtmp = Math.random();

			if (dtmp < (mutation_rate)) {
				tmp = Math.random();
				tmp1 = chromo_l * tmp;
				ttmp = Math.random();
				ttmp1 = 10 * ttmp;

				for (j = 0; j < chromo_l * parameters; j++)
					if (j == (int) tmp1) {
						individuals_tmp[i].setChromo_at(j, (int) ttmp1);
						// else
						// individuals_tmp[i].setChromo_at(j,
						// individuals[i].getChromo_at(j));

						individuals_tmp[i].setModified(true);
					}
			}

		}
	}

	private void mutate_elite(int parnumber) {
		int i, j;
		double dtmp, tmp, tmp1, ttmp, ttmp1, ftmp;

		ftmp = (chi_mean - individuals[0].getFitting()) / chi_mean;
		if (ftmp < 0.5)
			mutation_rate = mutation_rate0 * 0.5 / ftmp;
		else
			mutation_rate = mutation_rate0;

		for (i = 1; i < n_individuals; i++) {
			dtmp = Math.random();

			if (dtmp < (mutation_rate)) {
				// individuals_tmp[i].setModified(true);
				tmp = Math.random();
				tmp1 = chromo_l * tmp;
				ttmp = Math.random();
				ttmp1 = 10 * ttmp;

				for (j = 0; j < chromo_l; j++)
					if (j == (int) tmp1) {
						individuals_tmp[i].setChromo_at(chromo_l * parnumber + j,
								(int) ttmp1);
						// else
						// individuals_tmp[i].setChromo_at(chromo_l * parnumber +
						// j,individuals[i].getChromo_at(chromo_l * parnumber + j));

						individuals_tmp[i].setModified(true);
					}
			}
		}
	}

	private int select() {
		double tmp, tmp1;
		int i;
		do {
			tmp = Math.random();
			tmp1 = Math.random();
		} while (tmp < tmp1 * pressure);

		i = (int) (n_individuals * tmp1 - 0.00001);
		return i;
	}

	private void crossover() // crossover
	{
		int i, j, i1 = 1, i2 = 1, ic;
		double tmp;

		for (i = 1; i < (int) n_individuals / 2; i++) {

			i1 = select();

			do {
				i2 = select();
			} while (i2 == i1);

			tmp = Math.random();
			ic = (int) ((int) (chromo_l * parameters - 2) * tmp);

			for (j = 0; j <= ic; j++) {
				individuals_tmp[i * 2 - 1].setChromo_at(j,
						individuals[i1].getChromo_at(j));
				individuals_tmp[i * 2].setChromo_at(j, individuals[i2].getChromo_at(j));
			}

			for (j = ic + 1; j < chromo_l * parameters; j++) {
				individuals_tmp[i * 2 - 1].setChromo_at(j,
						individuals[i2].getChromo_at(j));
				individuals_tmp[i * 2].setChromo_at(j, individuals[i1].getChromo_at(j));
			}

			if (individuals[i1].getFitting() != individuals[i2].getFitting()) {
				individuals_tmp[i * 2 - 1].setModified(true);
				individuals_tmp[i * 2].setModified(true);

			} else {
				individuals_tmp[i * 2 - 1].setModified(false);
				individuals_tmp[i * 2].setModified(false);
			}
		}

		for (j = 0; j < chromo_l; j++) {
			individuals_tmp[n_individuals - 1].setChromo_at(j,
					individuals[n_individuals - 1].getChromo_at(j));
		}
		individuals_tmp[n_individuals - 1].setFitting(individuals[n_individuals - 1]
				.getFitting());
		individuals[n_individuals - 1].setModified(false);
	}

	public void sort() // sorting
	{
		int i, j, l, k;
		boolean md;
		double m, me = 0;
		Individual tmp;
		try {
			tmp = new Individual(chromo_l, parameters, ranges_max, ranges_min);
			for (i = n_individuals - 1; i > 0; i--) {
				m = individuals[0].getFitting();
				md = individuals[0].getModified();
				if (entropy != 0)
					me = individuals[0].getEntropy();
				for (k = 0; k < chromo_l * parameters; k++)
					tmp.setChromo_at(k, individuals[0].getChromo_at(k));
				l = 0;
				for (j = 1; j <= i; j++)
					if (individuals[j].getFitting() > m) {
						m = individuals[j].getFitting();
						md = individuals[j].getModified();
						if (entropy != 0)
							me = individuals[j].getEntropy();
						for (k = 0; k < chromo_l * parameters; k++)
							tmp.setChromo_at(k, individuals[j].getChromo_at(k));
						l = j;
					}
				individuals[l].setFitting(individuals[i].getFitting());
				individuals[l].setModified(individuals[i].getModified());
				if (entropy != 0)
					individuals[l].setEntropy(individuals[i].getEntropy());
				for (k = 0; k < chromo_l * parameters; k++)
					individuals[l].setChromo_at(k, individuals[i].getChromo_at(k));
				individuals[i].setFitting(m);
				individuals[i].setModified(md);
				if (entropy != 0)
					individuals[i].setEntropy(me);
				for (k = 0; k < chromo_l * parameters; k++)
					individuals[i].setChromo_at(k, tmp.getChromo_at(k));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//Number of parameters or chromosome length exceeds maximal value
			e.printStackTrace();
		}
	}

	public void next_generation() {
		int i;

		iteration++;

		for (i = 0; i < n_individuals; i++)
			individuals[i].encode();

		for (i = 0; i < n_individuals; i++) {
			for (int j = 0; j < parameters; j++)
				individuals_tmp[i].parameters[j] = individuals[i].parameters[j];
			for (int k = 0; k < chromo_l * parameters; k++)
				individuals_tmp[i].setChromo_at(k, individuals[i].getChromo_at(k));
			individuals_tmp[i].setFitting(individuals[i].getFitting());
			if (entropy != 0)
				individuals_tmp[i].setEntropy(individuals[i].getEntropy());
			individuals_tmp[i].setModified(false);
		}

		crossover();
		mutate();

		if (iteration % elite_mutation_frequency == 0) {
			mutate_elite(parameters - 1); // mutating
			mutate_elite(parameters - 2); // mutating
			mutate_elite(parameters - 3); // mutating
		}

		for (i = 0; i < n_individuals; i++) {
			individuals_tmp[i].encode();
			if (entropy != 0)
				individuals_tmp[i].getEntropy();
		}

		for (i = 0; i < n_individuals; i++) {
			for (int j = 0; j < parameters; j++)
				individuals[i].parameters[j] = individuals_tmp[i].parameters[j];
			for (int k = 0; k < chromo_l * parameters; k++)
				individuals[i].setChromo_at(k, individuals_tmp[i].getChromo_at(k));
			individuals[i].setFitting(individuals_tmp[i].getFitting());
			individuals[i].setModified(individuals_tmp[i].getModified());
			if (entropy != 0)
				individuals[i].setEntropy(individuals_tmp[i].getEntropy());
		}

	}

	public void hi_2(Modelable model) {
		int i, j;
		double[] pars = new double[parameters];
		double tmp;

		for (i = 0; i < parameters; i++)
			pars[i] = individuals[0].parameters[i];

		if (entropy != 0) {
			individuals[0].mkentropy();
			tmp = individuals[0].getEntropy();
		} else
			tmp = 0;

		individuals[0].setFitting(model.mkmodel(pars)
				* (1.0 + entropy / (0.01 + tmp)));

		for (j = 1; j < n_individuals; j++) {

			if (individuals[j].getModified()) {
				for (i = 0; i < parameters; i++)
					pars[i] = individuals[j].parameters[i];

				if (entropy != 0) {
					individuals[j].mkentropy();
					tmp = individuals[j].getEntropy();
				} else
					tmp = 0;

				individuals[j].setFitting(model.mkmodel(pars)
						* (1.0 + entropy / (0.01 + tmp)));
			}
		}

	}


}
