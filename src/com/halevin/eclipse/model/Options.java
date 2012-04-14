package com.halevin.eclipse.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class Options {
	private String filename;
	private int iterations, models, rep_freq, genes, points,
			catastrophic_regime, elite_mutation, band;
	private double base_mutation_rate, pressure, entropy, spline_width,
			inclination, mu, fflumi_min, fflumi_max, starlumi_min,
			starlumi_max, lumi_zero_min, lumi_zero_max, extinction, period, distance;

	public Options(String filename) {
		this.filename = filename;
		File fil = new File(this.filename);
		if (!fil.exists()) {

			try {
				OutputStream fos = new FileOutputStream(fil);
				fos.write("<?xml version=\"1.0\" ?>\n".getBytes());
				fos.write("<options>\n".getBytes());

				fos.write("<genetic>\n".getBytes());

				fos.write("<iterations>".getBytes());
				fos.write("3000".getBytes());
				fos.write("</iterations>\n".getBytes());
				iterations=3000;

				fos.write("<models>".getBytes());
				fos.write("1".getBytes());
				fos.write("</models>\n".getBytes());
				models=1;

				fos.write("<rep_freq>".getBytes());
				fos.write("10".getBytes());
				fos.write("</rep_freq>\n".getBytes());
				rep_freq=10;

				fos.write("<base_mutation_rate>".getBytes());
				fos.write("0.3".getBytes());
				fos.write("</base_mutation_rate>\n".getBytes());
				base_mutation_rate=0.3;

				fos.write("<pressure>".getBytes());
				fos.write("0.3".getBytes());
				fos.write("</pressure>\n".getBytes());
				pressure=0.3;

				fos.write("<entropy>".getBytes());
				fos.write("0.0".getBytes());
				fos.write("</entropy>\n".getBytes());
				entropy=0.0;

				fos.write("<spline_width>".getBytes());
				fos.write("0.1".getBytes());
				fos.write("</spline_width>\n".getBytes());
				spline_width=0.1;

				fos.write("<genes>".getBytes());
				fos.write("100".getBytes());
				fos.write("</genes>\n".getBytes());
				genes=100;

				fos.write("<points>".getBytes());
				fos.write("100".getBytes());
				fos.write("</points>\n".getBytes());
				points=100;

				fos.write("<catastrophic_regime>".getBytes());
				fos.write("20".getBytes());
				fos.write("</catastrophic_regime>\n".getBytes());
				catastrophic_regime=20;

				fos.write("<elite_mutation>".getBytes());
				fos.write("10".getBytes());
				fos.write("</elite_mutation>\n".getBytes());
				elite_mutation=10;

				fos.write("</genetic>\n".getBytes());

				fos.write("<binary_system>\n".getBytes());

				fos.write("<inclination>".getBytes());
				fos.write("3".getBytes());
				fos.write("</inclination>\n".getBytes());
				inclination=3;

				fos.write("<mu>".getBytes());
				fos.write("0.3".getBytes());
				fos.write("</mu>\n".getBytes());
				mu=0.3;

				fos.write("<band>".getBytes());
				fos.write("2".getBytes());
				fos.write("</band>\n".getBytes());
				band = 2;

				fos.write("<extinction>".getBytes());
				fos.write("0.0".getBytes());
				fos.write("</extinction>\n".getBytes());
				extinction = 0.0;
				
				fos.write("<distance>".getBytes());
				fos.write("300".getBytes());
				fos.write("</distance>\n".getBytes());
				distance = 300;
				
				fos.write("<period>".getBytes());
				fos.write("0.3".getBytes());
				fos.write("</period>\n".getBytes());
				period = 0.3;
				
				fos.write("</binary_system>\n".getBytes());

				fos.write("<model_limits>\n".getBytes());

				fos.write("<fflumi max=\"2.00001\" min=\"0.00000001\" />\n".getBytes());
				fflumi_max = 2.00001;
				fflumi_min = 0.00000001;
				fos.write("<starlumi max=\"2.00001\" min=\"0.00000001\" />\n".getBytes());
				starlumi_max = 2.00001;
				starlumi_min = 0.00000001;
				fos.write("<lumi_zero max=\"2.00001\" min=\"0.00000001\" />\n".getBytes());
				lumi_zero_max = 2.00001;
				lumi_zero_min = 0.00000001;

				fos.write("</model_limits>\n".getBytes());

				fos.write("</options>\n".getBytes());
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			try {
				builder = fact.newDocumentBuilder();
				Document doc;
				doc = builder.parse(fil);

				NodeList list = doc.getElementsByTagName("iterations");
				Node thisNode = list.item(0);
				iterations = Integer.parseInt(((Text) thisNode.getFirstChild()).getData().trim());

				list = doc.getElementsByTagName("models");
				thisNode = list.item(0);
				models = Integer.parseInt(((Text) thisNode.getFirstChild()).getData().trim());

				list = doc.getElementsByTagName("rep_freq");
				thisNode = list.item(0);
				rep_freq = Integer.parseInt(((Text) thisNode.getFirstChild())
						.getData().trim());

				list = doc.getElementsByTagName("base_mutation_rate");
				thisNode = list.item(0);
				base_mutation_rate = Double.parseDouble(((Text) thisNode
						.getFirstChild()).getData().trim());

				list = doc.getElementsByTagName("pressure");
				thisNode = list.item(0);
				pressure = Double.parseDouble(((Text) thisNode.getFirstChild())
						.getData().trim());

				list = doc.getElementsByTagName("entropy");
				thisNode = list.item(0);
				entropy = Double.parseDouble(((Text) thisNode.getFirstChild())
						.getData().trim());

				list = doc.getElementsByTagName("spline_width");
				thisNode = list.item(0);
				spline_width = Double.parseDouble(((Text) thisNode
						.getFirstChild()).getData().trim());

				list = doc.getElementsByTagName("genes");
				thisNode = list.item(0);
				genes = Integer.parseInt(((Text) thisNode.getFirstChild())
						.getData().trim());
				
				list = doc.getElementsByTagName("points");
				thisNode = list.item(0);
				points = Integer.parseInt(((Text) thisNode.getFirstChild())
						.getData().trim());
				
				list = doc.getElementsByTagName("catastrophic_regime");
				thisNode = list.item(0);
				catastrophic_regime = Integer.parseInt(((Text) thisNode.getFirstChild())
						.getData().trim());
				
				list = doc.getElementsByTagName("elite_mutation");
				thisNode = list.item(0);
				elite_mutation = Integer.parseInt(((Text) thisNode.getFirstChild())
						.getData().trim());
				
				list = doc.getElementsByTagName("inclination");
				thisNode = list.item(0);
				inclination = Double.parseDouble(((Text) thisNode
						.getFirstChild()).getData().trim());

				list = doc.getElementsByTagName("mu");
				thisNode = list.item(0);
				mu = Double.parseDouble(((Text) thisNode
						.getFirstChild()).getData().trim());

				list = doc.getElementsByTagName("band");
				thisNode = list.item(0);
				band = Integer.parseInt(((Text) thisNode.getFirstChild())
						.getData().trim());

				list = doc.getElementsByTagName("extinction");
				thisNode = list.item(0);
				extinction = Double.parseDouble(((Text) thisNode
						.getFirstChild()).getData().trim());

				list = doc.getElementsByTagName("distance");
				thisNode = list.item(0);
				distance = Double.parseDouble(((Text) thisNode
						.getFirstChild()).getData().trim());

				list = doc.getElementsByTagName("period");
				thisNode = list.item(0);
				period = Double.parseDouble(((Text) thisNode
						.getFirstChild()).getData().trim());

				
				
				NamedNodeMap attr;
				Node maxAtr, minAtr;
				list = doc.getElementsByTagName("fflumi");
				thisNode = list.item(0);
				attr = thisNode.getAttributes();
				maxAtr = attr.getNamedItem("max");
				minAtr = attr.getNamedItem("min");
				fflumi_min = Double.parseDouble(((Text) minAtr.getFirstChild()).getData().trim());
				fflumi_max = Double.parseDouble(((Text) maxAtr.getFirstChild()).getData().trim());

				list = doc.getElementsByTagName("starlumi");
				thisNode = list.item(0);
				attr = thisNode.getAttributes();
				maxAtr = attr.getNamedItem("max");
				minAtr = attr.getNamedItem("min");
				starlumi_min = Double.parseDouble(((Text) minAtr.getFirstChild()).getData().trim());
				starlumi_max = Double.parseDouble(((Text) maxAtr.getFirstChild()).getData().trim());

				list = doc.getElementsByTagName("lumi_zero");
				thisNode = list.item(0);
				attr = thisNode.getAttributes();
				maxAtr = attr.getNamedItem("max");
				minAtr = attr.getNamedItem("min");
				lumi_zero_min = Double.parseDouble(((Text) minAtr.getFirstChild()).getData().trim());
				lumi_zero_max = Double.parseDouble(((Text) maxAtr.getFirstChild()).getData().trim());

			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

public void writeOptions() {
	File fil = new File(filename);
		try {
			OutputStream fos = new FileOutputStream(fil);
			fos.write("<?xml version=\"1.0\" ?>\n".getBytes());
			fos.write("<options>\n".getBytes());

			fos.write("<genetic>\n".getBytes());

			fos.write("<iterations>".getBytes());
			fos.write((((Integer)this.iterations).toString()).getBytes());
			fos.write("</iterations>\n".getBytes());

			fos.write("<models>".getBytes());
			fos.write((((Integer)this.models).toString()).getBytes());
			fos.write("</models>\n".getBytes());

			fos.write("<rep_freq>".getBytes());
			fos.write((((Integer)this.rep_freq).toString()).getBytes());
			fos.write("</rep_freq>\n".getBytes());

			fos.write("<base_mutation_rate>".getBytes());
			fos.write((((Double)this.base_mutation_rate).toString()).getBytes());
			fos.write("</base_mutation_rate>\n".getBytes());

			fos.write("<pressure>".getBytes());
			fos.write((((Double)this.pressure).toString()).getBytes());
			fos.write("</pressure>\n".getBytes());

			fos.write("<entropy>".getBytes());
			fos.write((((Double)this.entropy).toString()).getBytes());
			fos.write("</entropy>\n".getBytes());

			fos.write("<spline_width>".getBytes());
			fos.write((((Double)this.spline_width).toString()).getBytes());
			fos.write("</spline_width>\n".getBytes());

			fos.write("<genes>".getBytes());
			fos.write((((Integer)this.genes).toString()).getBytes());
			fos.write("</genes>\n".getBytes());

			fos.write("<points>".getBytes());
			fos.write((((Integer)this.points).toString()).getBytes());
			fos.write("</points>\n".getBytes());

			fos.write("<catastrophic_regime>\n".getBytes());
			fos.write((((Integer)this.catastrophic_regime).toString()).getBytes());
			fos.write("</catastrophic_regime>\n".getBytes());

			fos.write("<elite_mutation>".getBytes());
			fos.write((((Integer)this.elite_mutation).toString()).getBytes());
			fos.write("</elite_mutation>\n".getBytes());

			fos.write("</genetic>\n".getBytes());

			fos.write("<binary_system>\n".getBytes());

			fos.write("<inclination>".getBytes());
			fos.write((((Double)this.inclination).toString()).getBytes());
			fos.write("</inclination>\n".getBytes());

			fos.write("<mu>".getBytes());
			fos.write((((Double)this.mu).toString()).getBytes());
			fos.write("</mu>\n".getBytes());

			fos.write("<band>".getBytes());
			fos.write((((Integer)this.band).toString()).getBytes());
			fos.write("</band>\n".getBytes());

			fos.write("<extinction>".getBytes());
			fos.write((((Double)this.extinction).toString()).getBytes());
			fos.write("</extinction>\n".getBytes());

			fos.write("<distance>".getBytes());
			fos.write((((Double)this.distance).toString()).getBytes());
			fos.write("</distance>\n".getBytes());

			fos.write("<period>".getBytes());
			fos.write((((Double)this.period).toString()).getBytes());
			fos.write("</period>\n".getBytes());

			fos.write("</binary_system>\n".getBytes());

			fos.write("<model_limits>\n".getBytes());
			
			String fflumis = new String("<fflumi max=\""+((Double)this.fflumi_max).toString()+"\" min=\""+((Double)this.fflumi_min).toString()+"\" />");  
			fos.write(fflumis.getBytes());
			
			String starlumis = new String("<starlumi max=\""+((Double)this.starlumi_max).toString()+"\" min=\""+((Double)this.starlumi_min).toString()+"\" />");  
			fos.write(starlumis.getBytes());
			
			String lumi_zeros = new String("<lumi_zero max=\""+((Double)this.lumi_zero_max).toString()+"\" min=\""+((Double)this.lumi_zero_min).toString()+"\" />");  
			fos.write(lumi_zeros.getBytes());
			
			fos.write("</model_limits>\n".getBytes());

			fos.write("</options>\n".getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("FNO exception");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO exception");
		}
	}	

public void setLumi_zero(double min, double max)
{
try{
	lumi_zero_min = min;
	lumi_zero_max = max;
}
catch (Exception e){};
}

public void setLumi_zero_min(double min)
{
try{
	lumi_zero_min = min;
}
catch (Exception e){};
}



public void setLumi_zero_max(double max)
{
try{
	lumi_zero_max = max;
}
catch (Exception e){};
}


public void setStarlumi(double min, double max)
{
try{
	starlumi_min = min;
	starlumi_max = max;
}
catch (Exception e){};
}

public void setStarlumi_min(double min)
{
try{
	starlumi_min = min;
}
catch (Exception e){};
}



public void setStarlumi_max(double max)
{
try{
	starlumi_max = max;
}
catch (Exception e){};
}


public void setFflumi(double min, double max)
{
try{
	fflumi_min = min;
	fflumi_max = max;
}
catch (Exception e){};
}

public void setFflumi_min(double min)
{
try{
	fflumi_min = min;
}
catch (Exception e){};
}

public void setFflumi_max(double max)
{
try{
	fflumi_max = max;
}
catch (Exception e){};
}


public void setMu(double par)
{
try{
	mu = par;
}
catch (Exception e){};
}

public void setInclination(double par)
{
try{
	inclination = par;
}
catch (Exception e){};
}

public void setBand(int par)
{
try{
	band = par;
}
catch (Exception e){};
}

public void setExtinction(double par)
{
try{
	extinction = par;
}
catch (Exception e){};
}

public void setDistance(double par)
{
try{
	distance = par;
}
catch (Exception e){};
}

public void setPeriod(double par)
{
try{
	period = par;
}
catch (Exception e){};
}

public void setSpline_width(double par)
{
try{
	spline_width = par;
}
catch (Exception e){};
}

public void setEntropy(double par)
{
try{
	entropy = par;
}
catch (Exception e){};
}

public void setPressure(double par)
{
try{
	pressure = par;
}
catch (Exception e){};
}

public void setBase_mutation_rate(double par)
{
try{
	base_mutation_rate = par;
}
catch (Exception e){};
}

public void setElite_mutation(int par)
{
try{
	elite_mutation = par;
}
catch (Exception e){};
}

public void setCatastrophic_regime(int par)
{
try{
	catastrophic_regime = par;
}
catch (Exception e){};
}

public void setPoints(int par)
{
try{
	points = par;
}
catch (Exception e){};
}

public void setGenes(int par)
{
try{
	genes = par;
}
catch (Exception e){};
}

public void setIterations(int par)
{
try{
	iterations = par;
}
catch (Exception e){};
}

public void setModels(int par)
{
try{
	models = par;
}
catch (Exception e){};
}

public void setReportFrequency(int par)
{
try{
	rep_freq = par;
}
catch (Exception e){};
}

	
public double getLumi_zero_min()
{
	return lumi_zero_min;
}

public double getLumi_zero_max()
{
	return lumi_zero_max;
}

public double getStarlumi_min()
{
	return starlumi_min;
}

public double getStarlumi_max()
{
	return starlumi_max;
}

public double getFflumi_min()
{
	return fflumi_min;
}

public double getFflumi_max()
{
	return fflumi_max;
}

public double getMu()
{
	return mu;
}

public String getBand()
{
	switch (band){
	case 0: return new String("U");
	case 1: return new String("B");
	case 2: return new String("V");
	case 3: return new String("R");
	case 4: return new String("I");
	default: return new String("V");
	}
}

public int getiBand()
{
	return band;
}


public double getExtinction()
{
	return extinction;
}

public double getDistance()
{
	return distance;
}

public double getPeriod()
{
	return period;
}

public double getInclination()
{
	return inclination;
}

public double getSpline_width()
{
	return spline_width;
}

public double getEntropy()
{
	return entropy;
}

public double getPressure()
{
	return pressure;
}

public double getBase_mutation_rate()
{
	return base_mutation_rate;
}

public int getElite_mutation()
{
	return elite_mutation;
}

public int getCatastrophic_regime()
{
	return catastrophic_regime;
}

public int getPoints()
{
    return points;
}

public int getGenes()
{
	return genes;
}

public int getIterations()
{
	return iterations;
}

public int getModels()
{
	return models;
}

public int getReportFrequency()
{
	return rep_freq;
}
	
	
}
