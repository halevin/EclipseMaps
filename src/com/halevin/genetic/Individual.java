package com.halevin.genetic;

public class Individual {
	private int chromo_l;
	private int n_parameters;
	private int[] chromo;
	private double[] ranges_max, ranges_min;
	private double fitting;
	private double entropy;
	private boolean modified;

	public double[] parameters;

	
	public Individual(int l, int n, double[] ranges_max, double[] ranges_min)
			throws Exception {
		chromo_l = l;
		n_parameters = n;
		if (chromo_l * n_parameters < Integer.MAX_VALUE) {
			chromo = new int[chromo_l * n_parameters];
		} else {
			throw new Exception();
		}
		parameters = new double[n_parameters];
		modified = true;
		this.ranges_max = ranges_max;
		this.ranges_min = ranges_min;

		for (int i = 0; i < chromo.length; i++) {
			chromo[i] = (int) (Math.random() * 10 - 0.000001);
		}

		encode();
	}

	public int getChromo_l() {
		return chromo_l;
	}

	public int getnParams() {
		return n_parameters;
	}

	public void setFitting(double fit) {
		fitting = fit;
	}

	public double getFitting() {
		return fitting;
	}

	public void setEntropy(double ent) {
		entropy = ent;
	}

	public double getEntropy() {
		return entropy;
	}

	
	public void setChromo_at(int i, int j) {
		chromo[i] = j;
	}

	public int getChromo_at(int i) {
		return chromo[i];
	}

	public void setModified(boolean mo) {
		modified = mo;
	}

	public boolean getModified() {
		return modified;
	}

	public void encode() {
		int i, j;
		double tmp;
		for (j = 0; j < n_parameters; j++) {
			tmp = 0;
			for (i = chromo_l - 1; i >= 0; i--) {
				tmp += (double) chromo[j * chromo_l + (chromo_l - i - 1)]
						* Math.pow(10, i);
			}
			tmp /= Math.pow(10, chromo_l);
			parameters[j] = ranges_min[j] + tmp
					* (ranges_max[j] - ranges_min[j]);
		}
	}

	public void mkentropy() {
		int npoints = (n_parameters - 3) / 2;
		double tmp, tmp1, res = 0, m;
		double[][] distm = new double[npoints][npoints];
		int i, j, l, kk, ll;

		for (kk = 0; kk < npoints; kk++)
			for (ll = 0; ll < npoints; ll++) {
				tmp = parameters[kk * 2] * Math.cos(parameters[kk * 2 + 1])
						- parameters[ll * 2] * Math.cos(parameters[ll * 2 + 1]);
				tmp1 = tmp * tmp;
				tmp = parameters[kk * 2] * Math.sin(parameters[kk * 2 + 1])
						- parameters[ll * 2] * Math.sin(parameters[ll * 2 + 1]);
				distm[kk][ll] = (tmp1 + tmp * tmp);
			}

		for (kk = 0; kk < npoints; kk++) {

			for (i = npoints - 1; i > 0; i--) {
				m = distm[kk][i];
				l = 0;
				for (j = 1; j <= i; j++)
					if (distm[kk][j] > m) {
						m = distm[kk][j];
						l = j;
					}
				distm[kk][l] = distm[kk][i];
				distm[kk][i] = m;
			}

			for (i = 0; i < 10; i++)
				res += distm[kk][i];
		}

		setEntropy(res / npoints);
	}

	public void code() {
		int i, j, k;
		double tmp, dtmp;
		for (j = 0; j < n_parameters; j++) {
			tmp = parameters[j];
			dtmp = (tmp - ranges_min[j]) / (ranges_max[j] - ranges_min[j]);
			for (i = 0; i <= chromo_l - 1; i++) {
				tmp = 0;
				for (k = 0; k < i; i++)
					tmp += chromo[i * chromo_l + k] * Math.pow(10, i - k);
				chromo[chromo_l * j + i] = (int) (dtmp * Math.pow(10, i + 1))
						- (int) tmp;
			}
		}

	}

	int compare(Individual gena) // crossover
	{
		int j;

		for (j = 0; j < chromo_l * n_parameters; j++) {
			if (this.chromo[j] != gena.chromo[j]) {
				return 1;
			}
			;
		}

		return 0;
	}

}
