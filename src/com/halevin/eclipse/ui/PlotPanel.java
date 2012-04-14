package com.halevin.eclipse.ui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.halevin.genetic.DataSet;

public class PlotPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	Insets ins;
	int x,y, x1, y1;
	DataSet inner_data, inner_data1;
	Boolean inverted, data1initialized=false, line=false;
	
	PlotPanel(int w, int h, DataSet data){
		setBorder(BorderFactory.createLineBorder(Color.RED, 1));
		setPreferredSize(new Dimension(w,h));
		inner_data = data;
		inverted = false;
	}

	PlotPanel(int w, int h, DataSet data, boolean inverted){
		setBorder(BorderFactory.createLineBorder(Color.RED, 1));
		setPreferredSize(new Dimension(w,h));
		inner_data = data;
		this.inverted = inverted;
	}

	PlotPanel(int w, int h, DataSet data, boolean inverted, boolean line){
		setBorder(BorderFactory.createLineBorder(Color.RED, 1));
		setPreferredSize(new Dimension(w,h));
		inner_data = data;
		this.inverted = inverted;
		this.line = line;
	}

	PlotPanel(int w, int h, DataSet data, DataSet data1, boolean inverted){
		setBorder(BorderFactory.createLineBorder(Color.RED, 1));
		setPreferredSize(new Dimension(w,h));
		inner_data = data;
		inner_data1 = data1;
		data1initialized = true;
		this.inverted = inverted;
	}
	
	PlotPanel(int w, int h, DataSet data, DataSet data1, boolean inverted, boolean line){
		setBorder(BorderFactory.createLineBorder(Color.RED, 1));
		setPreferredSize(new Dimension(w,h));
		inner_data = data;
		inner_data1 = data1;
		data1initialized = true;
		this.inverted = inverted;
		this.line = line;
	}
	
	
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		int height = getHeight();
		int width = getWidth();
		double xmax, xmin, ymax, ymin;
		
			xmax = inner_data.getXMax();
			xmin = inner_data.getXMin();
			ymax = inner_data.getYMax();
			ymin = inner_data.getYMin();
		
		ins = getInsets();

		double st = Math.pow(10,Math.floor(Math.log10(xmax-xmin)));
		double first = Math.ceil(xmin/st)*st;

		for (double xx=first; xx<xmax; xx+=st){
		g.setColor(Color.GRAY);
		x=(new Double((xx-xmin)/(xmax-xmin)*width)).intValue();
		g.drawLine(x, 0, x, height);
		g.drawString((new Double(xx)).toString(), x+2, height-10);
		}

		st = Math.pow(10,Math.floor(Math.log10(ymax-ymin)));
		first = Math.ceil(ymin/st)*st;

		for (double xx=first; xx<ymax; xx+=st){
		g.setColor(Color.GRAY);
//		if (!inverted)
		x=(new Double((xx-ymin)/(ymax-ymin)*height)).intValue();
//		else
//		x=(new Double((xx-ymin)/(ymin-ymax)*height)).intValue();

		g.drawLine(0, x, width, x);
		}
		
		
		
		if (!line){
		for (int i=0; i<inner_data.data_points;i++){
			x=(new Double((inner_data.data[i][0]-xmin)/(xmax-xmin)*width)).intValue();
			if (!inverted)
			{y=(new Double((inner_data.data[i][1]-ymin)/(ymax-ymin)*height)).intValue();}
			else
			{y=(new Double((inner_data.data[i][1]-ymax)/(ymin-ymax)*height)).intValue();}
			g.setColor(Color.BLUE);
			g.drawOval(x, y, 4, 4);
		}}else{
			for (int i=1; i<inner_data.data_points;i++){
				x1=(new Double((inner_data.data[i-1][0]-xmin)/(xmax-xmin)*width)).intValue();
				x=(new Double((inner_data.data[i][0]-xmin)/(xmax-xmin)*width)).intValue();
				if (!inverted)
				{
					y1=(new Double((inner_data.data[i-1][1]-ymin)/(ymax-ymin)*height)).intValue();
					y=(new Double((inner_data.data[i][1]-ymin)/(ymax-ymin)*height)).intValue();
				}
				else
				{
					y1=(new Double((inner_data.data[i-1][1]-ymax)/(ymin-ymax)*height)).intValue();
					y=(new Double((inner_data.data[i][1]-ymax)/(ymin-ymax)*height)).intValue();
				}
				g.setColor(Color.BLUE);
				g.drawLine(x1, y1, x, y);
		}};

		g.setColor(Color.RED);
		if (data1initialized)
		for (int i=1; i<inner_data1.data_points;i++){
			x1=(new Double((inner_data1.data[i-1][0]-xmin)/(xmax-xmin)*width)).intValue();
			x=(new Double((inner_data1.data[i][0]-xmin)/(xmax-xmin)*width)).intValue();
			if (!inverted)
			{
				y1=(new Double((inner_data1.data[i-1][1]-ymin)/(ymax-ymin)*height)).intValue();
				y=(new Double((inner_data1.data[i][1]-ymin)/(ymax-ymin)*height)).intValue();
			}
			else
			{
				y1=(new Double((inner_data1.data[i-1][1]-ymax)/(ymin-ymax)*height)).intValue();
				y=(new Double((inner_data1.data[i][1]-ymax)/(ymin-ymax)*height)).intValue();
				}
//			g.drawOval(x, y, 4, 4);
			g.drawLine(x1, y1, x, y);
		}

		
		
	}
}

