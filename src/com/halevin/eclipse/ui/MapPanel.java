package com.halevin.eclipse.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.halevin.genetic.DataSet;


public class MapPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	Insets ins;
	int x,y;
	float z,zero;
	DataSet inner_data;
	
	MapPanel(int w, int h, DataSet data){
		setBorder(BorderFactory.createLineBorder(Color.RED, 1));
		setPreferredSize(new Dimension(w,h));
		inner_data = data;
	}

	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		int height = getHeight();
		int width = getWidth();
		zero = (new Double(0.0)).floatValue();
		BufferedImage bi = (BufferedImage)createImage(width, height); 
		Graphics2D big = bi.createGraphics(); 
        big.setStroke(new BasicStroke(8.0f)); 
		
		ins = getInsets();

		
		double ii,jj;
		for (int i=0; i<200;i++)
		for (int j=0; j<200;j++){
			ii=i;jj=j;
			x=(new Double(ii/200.0*width)).intValue();
			y=(new Double(jj/200.0*height)).intValue();
			z=(new Double(0.05+0.9*inner_data.data[i][j]/inner_data.getXMax())).floatValue();
			if (z<=0) z=(new Double(0.001)).floatValue();
			if (z>=1) z=(new Double(0.999)).floatValue();
				big.setColor(new Color(zero, zero, z));
				big.fillOval(x, y, width/65, height/65);
			
			}

		
		g.drawImage(bi, 0, 0, this);
}

}

