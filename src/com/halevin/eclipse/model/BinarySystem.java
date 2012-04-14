package com.halevin.eclipse.model;

import static java.lang.Math.*;

public class BinarySystem {
	public Star stars[] = new Star[2];
	private double incl, maxU, L1R, mu, sinincl, cosincl;
	private double R_shadow;

	public BinarySystem(double mu, double incl) {
		stars[0] = new Star(0, false);
		stars[1] = new Star(1, true, 0.38, 0.01);
		this.mu = mu;
		this.incl = PI*incl/180.0;
		sinincl = sin(this.incl);
		cosincl = cos(this.incl);

		this.maxU = L1_Potential();
		this.L1R = UDistance(0, 0, this.maxU);

		SpaceVector a = new SpaceVector();

		for (int i=0;i<2;i++)
		{
			if (i==0)
				a.x = stars[i].get_fillingrate() * this.L1R;
			else
				a.x = 1.0 - stars[1].get_fillingrate() * (1.0 - this.L1R);

			a.y = 0;
			a.z = 0;
			stars[i].set_maxP(U(a));

			
		
			
		if (!stars[i].isSphere()) {
			Build_Roche_Lobe(stars[i]);
		} else {
			Build_spherical_star(stars[i]);
		}
		}
	}

	public double getR_shadow() {
		return R_shadow;
	}

	public double getL1R() {
		return L1R;
	}

	public double getSinIncl() {
		return sinincl;
	}

	public double getCosIncl() {
		return cosincl;
	}
	
	
	private double rad(Star st, SpaceVector a) {
		if (st.number == 0)
			return Math.sqrt(a.square());
		else {
			SpaceVector b = a;
			b.x = 1.0 - a.x;
			return Math.sqrt(b.square());
		}
	}

	private double U(SpaceVector b) {
		double res;
		SpaceVector a = new SpaceVector(b);
		res = -this.mu / rad(stars[0], a) - (1.0 - this.mu) / rad(stars[1], a)
				- ((a.x - 1.0 + this.mu) * (a.x - 1.0 + this.mu) + a.y * a.y)
				/ 2.0;

		return res;
	}

	private double acceleration(SpaceVector a) {
		SpaceVector b;

		b = a.Multiply(0.001);

		return -((U(a.Append(b)) - U(a)) / Math.sqrt(b.square()));

	}

	private double L1_Potential() {
		int i, max_i, temp_i;
		SpaceVector a = new SpaceVector(0, 0, 0);
		double uu, mU;

		a.y = 0;
		a.z = 0;

		mU = -1000.0;
		max_i = 1;
		for (i = 1; i <= 99; i++) {
			a.x = i * 0.01;
			uu = U(a);
			if (mU < uu) {
				mU = uu;
				max_i = i;
			}
		}

		temp_i = 1;
		for (i = 0; i <= 200; i++) {
			a.x = (max_i - 1) * 0.01 + i * 0.0001;
			uu = U(a);
			if (mU < uu) {
				mU = uu;
				temp_i = i;
			}
		}
		max_i = temp_i;

		return mU;
	}

	private double UDistance(double theta, double fi, double pot) {

		double poten, r, step, c1, c2, c3, dp, prev_dp;

		r = 0.0001;
		step = 0.01;
		c1 = Math.cos(theta) * Math.cos(fi);
		c2 = Math.sin(theta) * Math.cos(fi);
		c3 = Math.sin(fi);
		SpaceVector a = new SpaceVector(r * c1, r * c2, r * c3);

		poten = U(a);

		dp = Math.abs(pot - poten);
		prev_dp = dp;

		while (dp > 1e-15) {
			r += step;
			a.x = r * c1;
			a.y = r * c2;
			a.z = r * c3;

			poten = U(a);

			dp = Math.abs(pot - poten);

			if (dp > prev_dp) {
				r -= 2.0 * step;
				step /= 2.0;
				a.x = r * c1;
				a.y = r * c2;
				a.z = r * c3;
				poten = U(a);
				prev_dp = Math.abs(pot - poten);
			} else
				prev_dp = dp;

		}

		return r;
	}

	private void Build_Roche_Lobe(Star star) {
		int i, j;
		double theta, fi, r, cs, mutmp, ar;
		SpaceVector a = new SpaceVector();
		SpaceVector b = new SpaceVector();
		SpaceVector c = new SpaceVector();
		SpaceVector d = new SpaceVector();
		mutmp = this.mu;

		if (star.number == 0) {
			R_shadow = UDistance(0, Math.PI / 2.0 - this.incl, star.get_maxP())
					/ Math.sin(this.incl);
		}

		
		if (star.number == 1) {
			this.mu = 1.0 - this.mu;
		}

		for (j = 0; j <= 90; j++) {
			for (i = 0; i <= 180; i++) {
				fi = Math.PI * j / 180.0;
				theta = Math.PI * i / 180.0;

				SurfaceElement se = new SurfaceElement();

				se.r = UDistance(theta, fi, star.get_maxP());
				r = se.r;

				a.x = r * Math.cos(theta) * Math.cos(fi);
				a.y = r * Math.sin(theta) * Math.cos(fi);
				a.z = r * Math.sin(fi);

				se.setPosition(a);
				star.setSurfaceElement(i, j, se);
			}
		}

		for (j = 1; j < 90; j++) {
			ar = 2.0
					* PI
					* (sin(PI * (j + 0.5) / 180.0) - sin(PI * (j - 0.5) / 180.0));
			for (i = 1; i < 180; i++) {
				b = star.surface_elements[i+1][j].position
						.Extract(star.surface_elements[i - 1][j].position);
				c = star.surface_elements[i][j + 1].position
						.Extract(star.surface_elements[i][j - 1].position);
				d = b.Multiply(c);
				star.surface_elements[i][j].setOrientation(d.unit());

				cs = star.surface_elements[i][j].orientation
						.Scalar(star.surface_elements[i][j].position)
						/ sqrt(star.surface_elements[i][j].position
								.Scalar(star.surface_elements[i][j].position));

				r = star.surface_elements[i][j].r;

				star.surface_elements[i][j].set_area(r * r * ar / 360.0 / cs);
//				System.out.println(i+"  "+j);
//				System.out.println(star.surface_elements[i+1][j].position.square());
//				System.out.println(star.surface_elements[i-1][j].position.square());
//				System.out.println("=====");
			}
		}


		for (i = 1; i < 180; i++) {
			b = star.surface_elements[i + 1][0].position
					.Extract(star.surface_elements[i - 1][0].position);
			c.x = 0;
			c.y = 0;
			c.z = 1.0;
			d = b.Multiply(c);
			star.surface_elements[i][0].setOrientation(d.unit());
			cs = star.surface_elements[i][0].orientation
					.Scalar(star.surface_elements[i][0].position)
					/ sqrt(star.surface_elements[i][0].position
							.Scalar(star.surface_elements[i][0].position));
			r = star.surface_elements[i][0].r;
			star.surface_elements[i][0].set_area(2.0 * PI * r * r
					* (sin(PI * (0.5) / 180.0) - sin(PI * (-0.5) / 180.0))
					* 1.0 / 360.0 / cs);
		}

		for (j = 1; j < 90; j++) {
			b.x = 0;
			b.y = 1.0;
			b.z = 0;
			c = star.surface_elements[0][j + 1].position
					.Extract(star.surface_elements[0][j - 1].position);
			d = b.Multiply(c);
			star.surface_elements[0][j].setOrientation(d.unit());
			cs = star.surface_elements[0][j].orientation
					.Scalar(star.surface_elements[0][j].position)
					/ sqrt(star.surface_elements[0][j].position
							.Scalar(star.surface_elements[0][j].position));
			r = star.surface_elements[0][j].r;
			star.surface_elements[0][j].set_area(2.0
					* PI
					* r
					* r
					* (sin(PI * (j + 0.5) / 180.0) - sin(PI * (j - 0.5) / 180.0))
					* 1.0 / 360.0 / cs);
		}

		for (j = 1; j < 90; j++) {
			b.x = 0;
			b.y = 1.0;
			b.z = 0;
			c = star.surface_elements[180][j + 1].position
					.Extract(star.surface_elements[180][j - 1].position);
			d = b.Multiply(c);
			star.surface_elements[180][j].setOrientation(d.unit());
			cs = star.surface_elements[180][j].orientation
					.Scalar(star.surface_elements[180][j].position)
					/ sqrt(star.surface_elements[180][j].position
							.Scalar(star.surface_elements[180][j].position));
			r = star.surface_elements[180][j].r;
			star.surface_elements[180][j].set_area(2.0
					* PI
					* r
					* r
					* (sin(PI * (j + 0.5) / 180.0) - sin(PI * (j - 0.5) / 180.0))
					* 1.0 / 360.0 / cs);
		}

		SpaceVector sv = new SpaceVector();
		sv.x = -1.0;
		sv.y = 0;
		sv.z = 0;
		star.surface_elements[0][0].setOrientation(sv);
		r = star.surface_elements[0][0].r;
		star.surface_elements[0][0].set_area(2.0 * PI * r * r
				* (sin(PI * (j + 0.5) / 180.0) - sin(PI * (j - 0.5) / 180.0))
				* 1.0 / 360.0);

		i = 180;
		j = 0;
		sv.x = 1.0;
		sv.y = 0;
		sv.z = 0;
		star.surface_elements[i][j].setOrientation(sv);
		r = star.surface_elements[i][j].r;
		star.surface_elements[i][j].set_area(2.0 * PI * r * r
				* (sin(PI * (j + 0.5) / 180.0) - sin(PI * (j - 0.5) / 180.0))
				* 1.0 / 360.0);

		sv.x = 0;
		sv.y = 0;
		sv.z = 1.0;
		for (i = 0; i <= 180; i++) {
			star.surface_elements[i][90].setOrientation(sv);
			r = star.surface_elements[0][90].r;
			star.surface_elements[i][90].set_area(2.0
					* PI
					* r
					* r
					* (sin(PI * (90 + 0.5) / 180.0) - sin(PI * (90 - 0.5)
							/ 180.0)) * 1.0 / 360.0);
		}

		if (star.number == 1) {

			for (j = 0; j <= 90; j++)
				for (i = 0; i <= 180; i++) {
					sv = star.surface_elements[i][j].position;
					sv.x = 1.0-sv.x;
					star.surface_elements[i][j].setPosition(sv);
					sv =  star.surface_elements[i][j].orientation;
					sv.x = -sv.x;
					star.surface_elements[i][j].setOrientation(sv);
					d = star.surface_elements[i][j].orientation;
					stars[1].surface_elements[i][j].setOrientation(d.unit());
				}

			this.mu = mutmp;
		}

	}

	private void Build_spherical_star(Star star) {
		int i, j;
		double theta, fi, area, r;
		SpaceVector a;

		a = new SpaceVector();

		if (star.number == 0) {
			r = star.get_fillingrate() * this.L1R;
		} else {
			r = star.get_fillingrate() * (1.0 - this.L1R);
		}

		for (j = 0; j <= 90; j++) {
			area = 2.0
					* PI
					* r
					* r
					* (sin(PI * (j + 0.5) / 180.0) - sin(PI * (j - 0.5) / 180.0))
					* 1.0 / 360.0;
			fi = PI * j / 180.0;
			a.z = sin(fi);
			for (i = 0; i <= 180; i++) {
				theta = PI * i / 180.0;

				a.x = cos(theta) * cos(fi);
				a.y = sin(theta) * cos(fi);

				star.surface_elements[i][j] = new SurfaceElement();
				star.surface_elements[i][j].position = a.Multiply(r);
				star.surface_elements[i][j].orientation = a;
				star.surface_elements[i][j].set_area(area);
			}
		}

		if (star.number == 1)
			for (j = 0; j <= 90; j++)
				for (i = 0; i <= 180; i++) {
					a = star.surface_elements[i][j].position;
					a.x = 1.0-a.x;
					star.surface_elements[i][j].position = a;
					a = star.surface_elements[i][j].orientation;
					a.x = -a.x;
					star.surface_elements[i][j].orientation = a;
				}

	}

	public double Eclipse_Light_Curve(double phase, SpaceVector m0, SpaceVector a, boolean p) {
		double po = 0, res = 1.0;
		SpaceVector b, c;

//		if ((phase>0.18)&&p) 
//			System.out.println(phase+" m0 "+m0.toString()+"  a "+a.toString());
		
		
		if ((m0.x > this.L1R)
				&& (((phase > -0.25) && (phase < 0.25)) || (phase > 0.75))) {

			b = m0.Extract(a.Multiply((m0.Scalar(a)) / a.square()));

			
			
			double minr = stars[0].getSurfaceElement(0, 90).get_r();
			if (b.square() < minr*minr)
				{
//				if ((phase>0.18)&&p) 
//					System.out.println("(b.square() < minr*minr)");
				return 0.0;}

			po = U(b);

//			if ((phase>0.18)&&p) {
//				System.out.println("po= "+po+" maxP= "+ stars[0].get_maxP());
//				System.out.println(b.toString());
//				System.out.println("b "+Math.sqrt(b.square())+" L1R "+(this.L1R)+" r90 "+stars[0].getSurfaceElement(0, 90).get_r());}
			
			if (Math.sqrt(b.square()) < this.L1R)
			if (po < stars[0].get_maxP()) 
				{
//				if ((phase>0.18)&&p) 
//					System.out.println("(po < stars[0].get_maxP() "+(Math.sqrt(b.square()) < this.L1R));
				return 0.0;}
		}

		
		if ((m0.x < this.L1R) && (phase > 0.25) && (phase < 0.75)) {

			m0.x = m0.x - 1.0;

			b = m0.Extract(a.Multiply((m0.Scalar(a) / a.square())));

			b.x = b.x + 1.0;

			po = U(b);

			c = new SpaceVector(1.0, 0, 0);

			if ((po < stars[1].get_maxP())
					&& ((b.Extract(c).Scalar(b.Extract(c))) < (1.0 - this.L1R)
							* (1.0 - this.L1R)))
				return 0.0;
		}
		return res;
	}

	public double Eclipse_Light_Curve(double phase, SpaceVector m0, SpaceVector a) {
		double po = 0, res = 1.0;
		SpaceVector b, c;

		
		if ((m0.x > this.L1R)
				&& (((phase > -0.25) && (phase < 0.25)) || (phase > 0.75))) {

			b = m0.Extract(a.Multiply((m0.Scalar(a)) / a.square()));

			
			double minr = stars[0].getSurfaceElement(0, 90).get_r();
			if (b.square() < minr*minr)
				return 0.0;

			po = U(b);

			if ((po < stars[0].get_maxP())
					&& (b.square() < this.L1R * this.L1R))
				return 0.0;
		}

		
		if ((m0.x < this.L1R) && (phase > 0.25) && (phase < 0.75)) {

			m0.x = m0.x - 1.0;

			b = m0.Extract(a.Multiply((m0.Scalar(a) / a.square())));

			b.x = b.x + 1.0;

			po = U(b);

			c = new SpaceVector(1.0, 0, 0);

			if ((po < stars[1].get_maxP())
					&& ((b.Extract(c).Scalar(b.Extract(c))) < (1.0 - this.L1R)
							* (1.0 - this.L1R)))
				return 0.0;
		}

		return res;
	}
	
	
	public double Lobe_Light_Curve(Star star, double phase) {
		double res = 0, cosine, fi, g0, g, dark, beta;
		int i, j;
		SpaceVector a, b;
		dark = 1;

		fi = (phase - 0.25) * PI * 2.0;

		a = new SpaceVector();

		a.x = sin(fi) * cos(this.incl);
		a.y = cos(fi) * cos(this.incl);
		a.z = sin(this.incl);


		g0 = acceleration(star.surface_elements[180][0].getPosition());

		for (j = 1; j <= 89; j++)
			for (i = 1; i <= 179; i++) {
				g = acceleration(star.surface_elements[i][j].getPosition());
				beta = 1.0;
				dark = pow(g / g0, beta * 4.0); // gravitational darkening

				cosine = a.Scalar(star.surface_elements[i][j].getOrientation());
				if (cosine > 0) {
					res += Eclipse_Light_Curve(phase,
							star.surface_elements[i][j].getPosition(), a)
							* star.surface_elements[i][j].get_area()
							* cosine
							* (1.0 - star.get_u_lambda() * (1.0 - cosine)
									* dark);
				}

				
				
				b = star.surface_elements[i][j].getOrientation();
				b.y = -b.y;
				cosine = a.Scalar(b);
				if (cosine > 0) {
					b.x = star.surface_elements[i][j].getPosition().x;
					b.y = -star.surface_elements[i][j].getPosition().y;
					b.z = star.surface_elements[i][j].getPosition().z;
					res += Eclipse_Light_Curve(phase, b, a)
							* star.surface_elements[i][j].get_area()
							* cosine
							* (1.0 - star.get_u_lambda() * (1.0 - cosine)
									* dark);
				}

				b = star.surface_elements[i][j].getOrientation();
				b.z = -b.z;
				cosine = a.Scalar(b);
				if (cosine > 0) {
					b.x = star.surface_elements[i][j].getPosition().x;
					b.y = star.surface_elements[i][j].getPosition().y;
					b.z = -star.surface_elements[i][j].getPosition().z;
					res += Eclipse_Light_Curve(phase, b, a)
							* star.surface_elements[i][j].get_area()
							* cosine
							* (1.0 - star.get_u_lambda() * (1.0 - cosine)
									* dark);
				}

				b = star.surface_elements[i][j].getOrientation();
				b.y = -b.y;
				b.z = -b.z;
				cosine = a.Scalar(b);
				if (cosine > 0) {
					b.x = star.surface_elements[i][j].getPosition().x;
					b.y = -star.surface_elements[i][j].getPosition().y;
					b.z = -star.surface_elements[i][j].getPosition().z;
					res += Eclipse_Light_Curve(phase, b, a)
							* star.surface_elements[i][j].get_area()
							* cosine
							* (1.0 - star.get_u_lambda() * (1.0 - cosine)
									* dark);
				}
			}

		j = 0;
		for (i = 1; i <= 179; i++) {
			cosine = a.Scalar(star.surface_elements[i][j].getOrientation());
			if (cosine > 0) {
				res += Eclipse_Light_Curve(phase,
						star.surface_elements[i][j].getPosition(), a)
						* star.surface_elements[i][j].get_area()
						* cosine
						* (1.0 - star.get_u_lambda() * (1.0 - cosine) * dark);
			}

			b = star.surface_elements[i][j].getOrientation();
			b.y = -b.y;
			cosine = a.Scalar(b);
			b.x = star.surface_elements[i][j].getPosition().x;
			b.y = -star.surface_elements[i][j].getPosition().y;
			b.z = star.surface_elements[i][j].getPosition().z;
			if (cosine > 0) {
				res += Eclipse_Light_Curve(phase, b, a)
						* star.surface_elements[i][j].get_area() * cosine
						* (1.0 - star.get_u_lambda() * (1.0 - cosine) * dark);
			}
		}

	
		
		for (j = 1; j <= 89; j++) {
			i = 0;
			cosine = a.Scalar(star.surface_elements[i][j].getOrientation());
			if (cosine > 0) {
				res += Eclipse_Light_Curve(phase,
						star.surface_elements[i][j].getPosition(), a)
						* star.surface_elements[i][j].get_area()
						* cosine
						* (1.0 - star.get_u_lambda() * (1.0 - cosine) * dark);
			}
			i = 180;
			cosine = a.Scalar(star.surface_elements[i][j].getOrientation());
			if (cosine > 0) {
				res += Eclipse_Light_Curve(phase,
						star.surface_elements[i][j].getPosition(), a)
						* star.surface_elements[i][j].get_area()
						* cosine
						* (1.0 - star.get_u_lambda() * (1.0 - cosine) * dark);
			}
		}
		
		

		
		for (j = 1; j <= 89; j++) {
			i = 0;
			b = star.surface_elements[i][j].getOrientation();
			b.z = -b.z;
			cosine = a.Scalar(b);
			b.x = star.surface_elements[i][j].getPosition().x;
			b.y = star.surface_elements[i][j].getPosition().y;
			b.z = -star.surface_elements[i][j].getPosition().z;
			if (cosine > 0) {
				res += Eclipse_Light_Curve(phase, b, a)
						* star.surface_elements[i][j].get_area() * cosine
						* (1.0 - star.get_u_lambda() * (1.0 - cosine) * dark);
			}

			i = 180;
			b = star.surface_elements[i][j].getOrientation();
			b.z = -b.z;
			cosine = a.Scalar(b);
			b.x = star.surface_elements[i][j].getPosition().x;
			b.y = star.surface_elements[i][j].getPosition().y;
			b.z = -star.surface_elements[i][j].getPosition().z;
			if (cosine > 0) {
				res += Eclipse_Light_Curve(phase, b, a)
						* star.surface_elements[i][j].get_area() * cosine
						* (1.0 - star.get_u_lambda() * (1.0 - cosine) * dark);
			}

		}

		cosine = a.Scalar(star.surface_elements[0][0].getOrientation());
		if (cosine > 0) {
			res += Eclipse_Light_Curve(phase,
					star.surface_elements[0][0].getPosition(), a)
					* star.surface_elements[0][0].get_area()
					* cosine
					* (1.0 - star.get_u_lambda() * (1.0 - cosine) * dark);
		}

		cosine = a.Scalar(star.surface_elements[180][0].getOrientation());
		if (cosine > 0) {
			res += Eclipse_Light_Curve(phase,
					star.surface_elements[180][0].getPosition(), a)
					* star.surface_elements[180][0].get_area()
					* cosine
					* (1.0 - star.get_u_lambda() * (1.0 - cosine) * dark);
		}

		cosine = a.Scalar(star.surface_elements[0][90].getOrientation());
		if (cosine > 0) {
			res += Eclipse_Light_Curve(phase,
					star.surface_elements[0][90].getPosition(), a)
					* star.surface_elements[0][90].get_area()
					* cosine
					* (1.0 - star.get_u_lambda() * (1.0 - cosine) * dark);
		}

		return res;
	}

}
