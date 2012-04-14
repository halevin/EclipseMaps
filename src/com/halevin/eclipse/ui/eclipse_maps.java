package com.halevin.eclipse.ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Formatter;
import java.util.StringTokenizer;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import com.halevin.eclipse.model.BinarySystem;
import com.halevin.eclipse.model.EclipseModel;
import com.halevin.eclipse.model.Options;
import com.halevin.eclipse.model.SpaceVector;
import com.halevin.eclipse.model.Spline;
import com.halevin.genetic.DataSet;
import com.halevin.genetic.Population;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class eclipse_maps extends JFrame {

	double hi_m, eb_m = 0;
	final boolean light = true;
	public static Options opset;
	public static BinarySystem BS;
	private static final long serialVersionUID = 1L;
	private JDesktopPane m_desktop;
	DataSet data, model, points, fitness, entropy, map, residuals, lobe;
	PlotPanel panel, panel2, panel3, panel4, panel6, panel7;
	JLabel 	DiskFluxValue, SecFluxValue, ConstantFluxValue, L1Value, ShadowValue;
	MapPanel panel5;
	String log;
	TextArea TA;
	boolean next, dataloaded=false;
	Spline Spl;
	Population pop;
	
	public eclipse_maps() {
		super();
		m_desktop = new JDesktopPane();
		m_desktop.putClientProperty("JDesktopPane.dragMode", "outline");
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;
		setSize((int) (screenWidth), (int) (screenHeight));
		if (light) setTitle("EMap Light v. 0.9 beta");
		else
			setTitle("EMap Pro v. 0.9 beta");
		setIconImage(new ImageIcon("ic.gif", "").getImage());
		// setDefaultLookAndFeelDecorated(isOpaque());
		data = new DataSet();
		model = new DataSet();
		points = new DataSet();
		fitness = new DataSet();
		entropy = new DataSet();
		map = new DataSet();
		residuals = new DataSet();
		lobe = new DataSet();
		log = new String();
		next = false;
		Spl = new Spline();
		map.data = new double[201][201];


		
		final JFileChooser openFileChooser = new JFileChooser();
		final JFileChooser saveFilesChooser = new JFileChooser();
		final JFileChooser saveMapFilesChooser = new JFileChooser();
		saveFilesChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		openFileChooser.setCurrentDirectory(new File("."));

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu menu = new JMenu("File");
		menuBar.add(menu);

		JMenuItem openItem = new JMenuItem("Open file");
		menu.add(openItem);
		openItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (openFileChooser.showOpenDialog(eclipse_maps.this) == JFileChooser.APPROVE_OPTION) {
					try {
						BufferedReader in = new BufferedReader(new FileReader(
								openFileChooser.getSelectedFile()));
						String line;
						data.data_points = 0;
						points.data_points = opset.getPoints();
						StringTokenizer st;
						while ((line = in.readLine()) != null)
							data.data_points++;
						data.data = new double[data.data_points][7];
						model.data = new double[data.data_points][2];
						residuals.data = new double[data.data_points][2];
						points.data = new double[points.data_points][2];
						points.setXMax(1.5);
						points.setYMax(0.5);
						points.setXMin(0.5);
						points.setYMin(-0.5);
						model.data_points = data.data_points;
						lobe.data_points = 360*180;
						residuals.data_points = data.data_points;
						fitness.data = new double[opset.getIterations()][2];
						entropy.data = new double[opset.getIterations()][2];
						lobe.data = new double[360*180][2];
						in = new BufferedReader(new FileReader(openFileChooser
								.getSelectedFile()));
						int count = 0;
						String tok;
						
						String[] opsss = {"Magnitudes","Intensities"};
						int response = JOptionPane.showOptionDialog(m_desktop, "Choose data type", "", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opsss, "");
						
						
						
						while ((line = in.readLine()) != null) {
							if (line.isEmpty())
								continue;
							st = new StringTokenizer(line, " ");
							try {
								tok = st.nextToken();
								if (tok.isEmpty())
									continue;
								data.data[count][0] = Double.parseDouble(tok);
								data.data[count][1] = Double.parseDouble(st
										.nextToken());
								if (response==0)
								data.data[count][1]=Math.pow(10,-0.4*data.data[count][1]);
								
								model.data[count][0] = data.data[count][0];
								model.data[count][1] = 0;
								residuals.data[count][0] = data.data[count][0];
								residuals.data[count][1] = 0;
								lobe.data[count][0] = 0;
								lobe.data[count][1] = 0;
								if (st.hasMoreTokens()) {
									data.data[count][2] = Double.parseDouble(st
											.nextToken());
								} else {
									data.data[count][2] = Math
											.sqrt(data.data[count][1]);
								}
							} catch (Exception e) {
								System.out.println(e.toString());
							}

							count++;
						}
						data.calculate_bounds();
						dataFrame(data);
						dataloaded=true;
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				;
			}
		});

		JMenuItem optionsItem = new JMenuItem("Options");
		menu.add(optionsItem);
		optionsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String[] colHeads = { "Parameter", "Value" };
				Object[][] tabledata = {
						{ "Iterations", opset.getIterations() },
						{ "Models", opset.getModels() },
						{ "Report frequency", opset.getReportFrequency() },
						{ "Base mutation rate", opset.getBase_mutation_rate() },
						{ "Pressure", opset.getPressure() },
						{ "Entropy", opset.getEntropy() },
						{ "Spline width", opset.getSpline_width() },
						{ "Genes", opset.getGenes() },
						{ "Points", opset.getPoints() },
						{ "Catastrophic regime", opset.getCatastrophic_regime() },
						{ "Elite mutation", opset.getElite_mutation() },

						{ "Inclination", opset.getInclination() },
						{ "Mu", opset.getMu() },

						{ "Disc flux maximum", opset.getFflumi_max() },
						{ "Disc flux minimum", opset.getFflumi_min() },
						{ "Secondary star flux maximum",
								opset.getStarlumi_max() },
						{ "Secondary star flux minimum",
								opset.getStarlumi_min() },
						{ "Constant flux term maximum",
								opset.getLumi_zero_max() },
						{ "Constant flux term minimum",
								opset.getLumi_zero_min() }, 
						{ "UBVRI band",
									opset.getBand()}, 
						{ "Interstellar extinction",
									opset.getExtinction()}, 
						{ "Distance, pc",
									opset.getDistance()}, 
						{ "Orbital period",
									opset.getPeriod()} 
				};

				newFrame(colHeads, tabledata);
			}
		});

		JMenuItem saveOpItem = new JMenuItem("Save options");
		menu.add(saveOpItem);
		saveOpItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				opset.writeOptions();
			}
		});

		JMenuItem MapOpItem = new JMenuItem("Show map window");
		menu.add(MapOpItem);
		MapOpItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (dataloaded) {
				map.data[0][0]=0;
				map.data_points = 0;
				map.calculate_bounds();
				mapFrame(map);}
				else{
					JOptionPane.showMessageDialog(m_desktop, "Please import data!");
				}
			}
		});

		if (!light) {
		
		JMenuItem LobeOpItem = new JMenuItem("Show lobe window");
		menu.add(LobeOpItem);
		LobeOpItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (dataloaded) {
				lobeFrame(lobe);}
				else{
					JOptionPane.showMessageDialog(m_desktop, "Please import data!");
				}
			}
		});
		
		
		JMenuItem ExportOpItem = new JMenuItem("Export results");
		menu.add(ExportOpItem);
		ExportOpItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (!next) {
					if (saveFilesChooser.showSaveDialog(m_desktop) == JFileChooser.APPROVE_OPTION) {
						
						saveData(saveFilesChooser.getSelectedFile().getAbsolutePath().toString()+"\\");
						
					}
				}
				else{
					JOptionPane.showMessageDialog(m_desktop, "Please, stop iterations to export data!");
				}
			}
		});
		}
		
		JMenuItem ExportMapItem = new JMenuItem("Export eclipse map");
		menu.add(ExportMapItem);
		ExportMapItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (panel5!=null){
				if (panel5.isVisible())
					if (saveMapFilesChooser.showSaveDialog(m_desktop) == JFileChooser.APPROVE_OPTION) 
						saveMap(saveMapFilesChooser.getSelectedFile().getAbsolutePath().toString());
				}else{
					JOptionPane.showMessageDialog(m_desktop, "Open map window first!");
				}		
			}
		});
		
		
		commandFrame();
		logFrame();

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(m_desktop, BorderLayout.CENTER);

	}

	private void saveData(String path){
		double x,y;
		
		double[] pars = pop.getParameters(0);

		File fil = new File(path+"best_par.dat");
		try {
			OutputStream fos = new FileOutputStream(fil);
			fos.write(("L1 radius = "+BS.getL1R()+"\n").getBytes());
			fos.write(("shadow radius = "+BS.getR_shadow()+"\n").getBytes());
			fos.write(("fflumi = "+pars[opset.getPoints()*2]+"\n").getBytes());
			fos.write(("lumi_zero = "+pars[opset.getPoints()*2+2]+"\n").getBytes());
			fos.write(("starlumi = "+pars[opset.getPoints()*2+1]+"\n").getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("FNO exception");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO exception");
		}

		File fil1 = new File(path+"datamod.dat");
		try {
			OutputStream fos = new FileOutputStream(fil1);
			for (int i=0;i<data.data_points;i++){
			fos.write((data.data[i][0]+" "+data.data[i][1]+" "+model.data[i][1]+" "+(data.data[i][1]-model.data[i][1])+" "+data.data[i][2]+" "+(pars[opset.getPoints()*2+1]*data.data[i][3])+"\n").getBytes());
			}
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("FNO exception");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO exception");
		}

		File fil2 = new File(path+"inter.dat");
		try {
			OutputStream fos = new FileOutputStream(fil2);
			for (int i=0;i<data.data_points;i++) fos.write((data.data[i][0]+" "+data.data[i][1]+"\n").getBytes());
			for (int i=0;i<data.data_points;i++) fos.write((data.data[i][0]+" "+model.data[i][1]+"\n").getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("FNO exception");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO exception");
		}

		File fil3 = new File(path+"res.dat");
		try {
			OutputStream fos = new FileOutputStream(fil3);
			for (int i=0;i<opset.getGenes();i++){ 
				pars = pop.getParameters(i);
			for (int j=0;j<opset.getPoints()*2+3;j++)
			fos.write((pars[j]+"\n").getBytes());
			}
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("FNO exception");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO exception");
		}

		File fil4 = new File(path+"mapi.dat");
		try {
			OutputStream fos = new FileOutputStream(fil4);
			pars = pop.getParameters(0);
			for (int i=0;i<opset.getPoints();i++){
			x = 1.0+pars[i*2]*Math.cos(pars[i*2+1]);	
			y = pars[i*2]*Math.sin(pars[i*2+1]);
			if ((x*x+y*y)<BS.getR_shadow()*BS.getR_shadow()) fos.write((x+" "+y+"\n").getBytes());
			}
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("FNO exception");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO exception");
		}

		
		
		
		
		
		double tmp, tmp1;
		int k;
		SpaceVector aa,p;
		aa = new SpaceVector();
		p = new SpaceVector();

		for (int ii=0;ii<201;ii++)
		{
		for (int jj=0;jj<201;jj++)
		{
		tmp=0;
		p.x = 1.0+(double)ii/200.0-0.5;
		p.y = (double)jj/200.0-0.5;
		p.z = 0;

		for (k=0;k<opset.getPoints();k++)
		{
		aa.x=points.data[k][0];
		aa.y=points.data[k][1];
		aa.z = 0;

		tmp1 =  Math.sqrt((p.Extract(aa)).square())/opset.getSpline_width();
		if (tmp1 < 3.0) {tmp+=Spl.arr[(int)(100.0*tmp1)];}
		}
		map.data[ii][jj]=pars[opset.getPoints()*2]*tmp;
		}
		}
		
		
		File fil5 = new File(path+"radiation.dat");
		try {
			OutputStream fos = new FileOutputStream(fil5);
			pars = pop.getParameters(0);
			for(int i=0;i<200;i++)
				for(int j=0;j<200;j++)
				{
				x = (double)i/200.0+0.5;	
				y = (double)i/200.0-0.5;	
				fos.write((x+" "+y+" "+map.data[i][j]+"\n").getBytes());	
				}
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("FNO exception");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO exception");
		}
		
	}

	private void saveMap(String path){
		
		FileOutputStream fos;
		try {
			int x,y;
			float z,zero;
			zero = (new Double(0.0)).floatValue();
			BufferedImage bi = (BufferedImage)createImage(400, 400); 
			Graphics2D big = bi.createGraphics(); 
	        big.setStroke(new BasicStroke(8.0f)); 

	        
	        
			double ii,jj;
			for (int i=0; i<200;i++)
			for (int j=0; j<200;j++){
				ii=i;jj=j;
				x=(new Double(ii/200.0*400)).intValue();
				y=(new Double(jj/200.0*400)).intValue();
				z=(new Double(0.05+0.9*map.data[i][j]/map.getXMax())).floatValue();
				if (z<=0) z=(new Double(0.001)).floatValue();
				if (z>=1) z=(new Double(0.999)).floatValue();
					big.setColor(new Color(zero, zero, z));
					big.fillOval(x, y, 400/65, 400/65);
				
				}

				big.setColor(Color.GRAY);
				if (light) 
					big.drawString("EMap Light v. 0.9 beta, � Alexandros Chalevin", 10, 400-10);
				else
					big.drawString("EMap Pro v. 0.9 beta, � Alexandros Chalevin", 10, 400-10);
			
			
			fos = new FileOutputStream(path+".jpg");
			JPEGImageEncoder jie = JPEGCodec.createJPEGEncoder(fos);
			jie.encode(bi);
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}	
	
	
	
	public void commandFrame() {
		final JInternalFrame jifMain = new JInternalFrame("Main", false, false, true,
				true);
		jifMain.setBounds(0, 0, 300, 100);
		jifMain.setLayout(new FlowLayout());

		jifMain.setFrameIcon(new ImageIcon("ic.gif", ""));

		final JButton startCalc = new JButton("Start calculations");
		startCalc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

				if (dataloaded) {
				if (!next) {
				next = true;	
				startCalc.setText("Stop calculations");	
				new NewThread("t1");
				} else {
					startCalc.setText("Start calculations");
					next = false;
				}
				} else
				{
					JOptionPane.showMessageDialog(m_desktop, "Please import data!");
				}
			}
		});


		final JPanel panel1 = new JPanel();
		panel1.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		panel1.setPreferredSize(new Dimension(jifMain.getWidth()-30, jifMain.getHeight()-40));

		panel1.add(startCalc);


		
		jifMain.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent ce){
				panel1.setSize(new Dimension(jifMain.getWidth()-30, jifMain.getHeight()-50));
				panel1.setLocation(10, 10);
			}
			public void componentMoved(ComponentEvent ce){
			}
			public void componentShown(ComponentEvent ce){
			}
			public void componentHidden(ComponentEvent ce){
			}
			
			
		}
		
		);
		
		
		
		jifMain.getContentPane().add(panel1);

		
		m_desktop.add(jifMain);
		jifMain.show();
		

	}


	public void infoFrame() {
		final JInternalFrame jifInfo = new JInternalFrame("Info", false, false, true,
				true);
		jifInfo.setBounds(0, 500, 300, 200);
		jifInfo.setLayout(new FlowLayout());

		jifInfo.setFrameIcon(new ImageIcon("ic.gif", ""));


		final JPanel panel8 = new JPanel();
		panel8.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		panel8.setPreferredSize(new Dimension(jifInfo.getWidth()-30, jifInfo.getHeight()-40));
		panel8.setLayout(new GridLayout(5,2));

		double[] pars = pop.getParameters(0);

		JLabel DiskFlux = new JLabel("Disk flux");
		DiskFlux.setFont(new Font("SansSerif", Font.BOLD, 14));
		Formatter fmt2 = new Formatter();
		fmt2.format("%e", pars[opset.getPoints()*2]);
		DiskFluxValue = new JLabel(fmt2.toString());
		DiskFluxValue.setFont(new Font("SansSerif", Font.BOLD, 14));

		JLabel SecFlux = new JLabel("Secondary flux");
		SecFlux.setFont(new Font("SansSerif", Font.BOLD, 14));
		Formatter fmt3 = new Formatter();
		fmt3.format("%e", pars[opset.getPoints()*2+1]);
		SecFluxValue = new JLabel(fmt3.toString());
		SecFluxValue.setFont(new Font("SansSerif", Font.BOLD, 14));
		
		JLabel ConstantFlux = new JLabel("Constant flux");
		ConstantFlux.setFont(new Font("SansSerif", Font.BOLD, 14));
		Formatter fmt4 = new Formatter();
		fmt4.format("%e", pars[opset.getPoints()*2+2]);
		ConstantFluxValue = new JLabel(fmt4.toString());
		ConstantFluxValue.setFont(new Font("SansSerif", Font.BOLD, 14));

		JLabel L1 = new JLabel("L1 radius");
		L1.setFont(new Font("SansSerif", Font.BOLD, 14));
		Formatter fmt = new Formatter();
		fmt.format("%e", BS.getL1R());
		L1Value = new JLabel(fmt.toString());
		L1Value.setFont(new Font("SansSerif", Font.BOLD, 14));
		
		JLabel Shadow = new JLabel("Shadow radius");
		Shadow.setFont(new Font("SansSerif", Font.BOLD, 14));
		Formatter fmt1 = new Formatter();
		fmt1.format("%e", BS.getR_shadow());
		ShadowValue = new JLabel(fmt1.toString());
		ShadowValue.setFont(new Font("SansSerif", Font.BOLD, 14));
		
		panel8.add(DiskFlux);
		panel8.add(DiskFluxValue);

		panel8.add(SecFlux);
		panel8.add(SecFluxValue);
		
		panel8.add(ConstantFlux);
		panel8.add(ConstantFluxValue);
		
		panel8.add(L1);
		panel8.add(L1Value);
		
		panel8.add(Shadow);
		panel8.add(ShadowValue);

		jifInfo.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent ce){
				panel8.setSize(new Dimension(jifInfo.getWidth()-30, jifInfo.getHeight()-50));
				panel8.setLocation(10, 10);
			}
			public void componentMoved(ComponentEvent ce){
			}
			public void componentShown(ComponentEvent ce){
			}
			public void componentHidden(ComponentEvent ce){
			}
			
			
		}
		
		);
		
		
		
		jifInfo.getContentPane().add(panel8);

		
		m_desktop.add(jifInfo);
		jifInfo.show();
		

	}
	
	
	
	public void logFrame() {
		final JInternalFrame jifLog = new JInternalFrame("Log", false, false, true,
				true);
		jifLog.setBounds(0, 100, 300, 400);
		jifLog.setLayout(new FlowLayout());
		jifLog.setFrameIcon(new ImageIcon("ic.gif", ""));
		jifLog.setMaximizable(false);

		final JPanel panel1 = new JPanel();
		panel1.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		panel1.setPreferredSize(new Dimension(290, 360));

		TA = new TextArea(log);
		TA.setPreferredSize(new Dimension(jifLog.getWidth()-40, jifLog.getHeight()-60));
		TA.setFont(new Font("SansSerif", Font.BOLD, 14));
		panel1.add(TA);

		jifLog.getContentPane().add(panel1);

		m_desktop.add(jifLog);
		jifLog.show();

	}

	public void pointsFrame(DataSet datas) {
		final JInternalFrame jifPoints = new JInternalFrame("Points (xy binary rotations frame coordinates)", true, true,
				true, true);
		jifPoints.setBounds(800, 100, 400, 400);
		jifPoints.setLayout(new FlowLayout());
		jifPoints.setFrameIcon(new ImageIcon("ic.gif", ""));

		panel2 = new PlotPanel(400, 400, datas, true);
		panel2.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		panel2.setPreferredSize(new Dimension(350, 350));

		jifPoints.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent ce){
				panel2.setSize(new Dimension(jifPoints.getWidth()-30, jifPoints.getHeight()-50));
				panel2.setLocation(10, 10);
			}
			public void componentMoved(ComponentEvent ce){
			}
			public void componentShown(ComponentEvent ce){
			}
			public void componentHidden(ComponentEvent ce){
			}
		}
		);
		
		
			jifPoints.getContentPane().add(panel2);

		
		m_desktop.add(jifPoints);
		jifPoints.show();

	}

	public void dataFrame(DataSet datas) {
		final JInternalFrame jifData = new JInternalFrame("Data (flux vs phase)", true, false, true,
				true);
		jifData.setBounds(300, 100, 500, 400);
		jifData.setLayout(new FlowLayout());
		jifData.setFrameIcon(new ImageIcon("ic.gif", ""));

		panel = new PlotPanel(700, 400, datas, true);
		panel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		panel.setPreferredSize(new Dimension(450, 350));

		
		jifData.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent ce){
				panel.setSize(new Dimension(jifData.getWidth()-30, jifData.getHeight()-50));
				panel.setLocation(10, 10);
			}
			public void componentMoved(ComponentEvent ce){
			}
			public void componentShown(ComponentEvent ce){
			}
			public void componentHidden(ComponentEvent ce){
			}
		}
		);
		
		
		jifData.getContentPane().add(panel);

		m_desktop.add(jifData);
		jifData.show();

	}

	public void dataFrame(DataSet datas, DataSet data1) {
		final JInternalFrame jifData = new JInternalFrame("Data (flux vs phase)", true, false, true,
				true);
		jifData.setBounds(300, 100, 500, 400);
		jifData.setLayout(new FlowLayout());
		jifData.setFrameIcon(new ImageIcon("ic.gif", ""));

		panel = new PlotPanel(700, 400, datas, data1, true);
		panel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		panel.setPreferredSize(new Dimension(450, 350));

		
		jifData.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent ce){
				panel.setSize(new Dimension(jifData.getWidth()-30, jifData.getHeight()-50));
				panel.setLocation(10, 10);
			}
			public void componentMoved(ComponentEvent ce){
			}
			public void componentShown(ComponentEvent ce){
			}
			public void componentHidden(ComponentEvent ce){
			}
		}
		);
		
		
		jifData.getContentPane().add(panel);

		m_desktop.add(jifData);
		jifData.show();

	}

	
	public void fitnessFrame(DataSet datas) {
		final JInternalFrame jifFitness = new JInternalFrame("log 10 of the best fitness factor", true, false, true,
				true);
		jifFitness.setBounds(300, 0, 500, 100);
		jifFitness.setLayout(new FlowLayout());
		jifFitness.setFrameIcon(new ImageIcon("ic.gif", ""));

		panel3 = new PlotPanel(500, 100, datas, false, true);
		panel3.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		panel3.setPreferredSize(new Dimension(450, 60));

		
		jifFitness.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent ce){
				panel3.setSize(new Dimension(jifFitness.getWidth()-20, jifFitness.getHeight()-40));
				panel3.setLocation(10, 10);
			}
			public void componentMoved(ComponentEvent ce){
			}
			public void componentShown(ComponentEvent ce){
			}
			public void componentHidden(ComponentEvent ce){
			}
		}
		);
		
		
		jifFitness.getContentPane().add(panel3);

		m_desktop.add(jifFitness);
		jifFitness.show();

	}

	
	public void entropyFrame(DataSet datas) {
		final JInternalFrame jifEntropy = new JInternalFrame("Entropy evolution", true, false, true,
				true);
		jifEntropy.setBounds(800, 0, 400, 100);
		jifEntropy.setLayout(new FlowLayout());
		jifEntropy.setFrameIcon(new ImageIcon("ic.gif", ""));

		panel4 = new PlotPanel(400, 100, datas, false, true);
		panel4.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		panel4.setPreferredSize(new Dimension(350, 60));

		
		jifEntropy.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent ce){
				panel4.setSize(new Dimension(jifEntropy.getWidth()-20, jifEntropy.getHeight()-40));
				panel4.setLocation(10, 10);
			}
			public void componentMoved(ComponentEvent ce){
			}
			public void componentShown(ComponentEvent ce){
			}
			public void componentHidden(ComponentEvent ce){
			}
		}
		);
		
		
		jifEntropy.getContentPane().add(panel4);

		m_desktop.add(jifEntropy);
		jifEntropy.show();

	}

	public void mapFrame(DataSet datas) {
		final JInternalFrame jifMap = new JInternalFrame("Accretion disc 2D map", true, true, true,
				true);
		jifMap.setBounds(800, 100, 400, 400);
		jifMap.setLayout(new FlowLayout());
		jifMap.setFrameIcon(new ImageIcon("ic.gif", ""));

		
		
		panel5 = new MapPanel(400, 400, datas);
		panel5.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		panel5.setPreferredSize(new Dimension(350, 350));

		
		jifMap.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent ce){
				panel5.setSize(new Dimension(jifMap.getWidth()-30, jifMap.getHeight()-30));
				panel5.setLocation(10, 10);
			}
			public void componentMoved(ComponentEvent ce){
			}
			public void componentShown(ComponentEvent ce){
			}
			public void componentHidden(ComponentEvent ce){
			}
		}
		);
		
		
		jifMap.getContentPane().add(panel5);

		m_desktop.add(jifMap);
		jifMap.show();

	}
	
	public void residualsFrame(DataSet datas) {
		final JInternalFrame jifResiduals = new JInternalFrame("Model residuals", true, false, true,
				true);
		jifResiduals.setBounds(300, 500, 500, 200);
		jifResiduals.setLayout(new FlowLayout());
		jifResiduals.setFrameIcon(new ImageIcon("ic.gif", ""));

		panel6 = new PlotPanel(500, 200, datas, false, true);
		panel6.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		panel6.setPreferredSize(new Dimension(450, 160));

		
		jifResiduals.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent ce){
				panel6.setSize(new Dimension(jifResiduals.getWidth()-20, jifResiduals.getHeight()-40));
				panel6.setLocation(10, 10);
			}
			public void componentMoved(ComponentEvent ce){
			}
			public void componentShown(ComponentEvent ce){
			}
			public void componentHidden(ComponentEvent ce){
			}
		}
		);
		
		
		jifResiduals.getContentPane().add(panel6);

		m_desktop.add(jifResiduals);
		jifResiduals.show();

	}

	public void lobeFrame(DataSet datas) {
		final JInternalFrame lobeFrame = new JInternalFrame("Roche lobe", true, true, true,
				true);
		lobeFrame.setBounds(300, 500, 400, 400);
		lobeFrame.setLayout(new FlowLayout());
		lobeFrame.setFrameIcon(new ImageIcon("ic.gif", ""));

		panel7 = new PlotPanel(500, 200, datas, true, false);
		panel7.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		panel7.setPreferredSize(new Dimension(450, 160));

		
		lobeFrame.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent ce){
				panel7.setSize(new Dimension(lobeFrame.getWidth()-20, lobeFrame.getHeight()-40));
				panel7.setLocation(10, 10);
			}
			public void componentMoved(ComponentEvent ce){
			}
			public void componentShown(ComponentEvent ce){
			}
			public void componentHidden(ComponentEvent ce){
			}
		}
		);
		
		
		lobeFrame.getContentPane().add(panel7);

		m_desktop.add(lobeFrame);
		lobeFrame.show();

	}

	
	
	public void newFrame(String[] colHeads, Object[][] tabledata) {
		JInternalFrame jif = new JInternalFrame("Options", true, true, true,
				true);
		jif.setBounds(10, 10, 300, 355);
		jif.setFrameIcon(new ImageIcon("ic.gif", ""));

		JTable tab = new JTable(tabledata, colHeads);
		TableColumnModel model = tab.getColumnModel();
		TableColumn col1 = model.getColumn(0);
		col1.setPreferredWidth(150);

		JScrollPane jsp = new JScrollPane(tab);
		jif.getContentPane().add(jsp);

		m_desktop.add(jif);

		jif.show();

		final TableModel tm;

		tm = tab.getModel();
		tm.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent tme) {
				if (tme.getType() == TableModelEvent.UPDATE) {
					String val = (String) tm.getValueAt(tme.getFirstRow(), 1);
					try {
						switch (tme.getFirstRow()) {
						case 0:
							opset.setIterations(Integer.parseInt(val));
							break;
						case 1:
							opset.setModels(Integer.parseInt(val));
							break;
						case 2:
							opset.setReportFrequency(Integer.parseInt(val));
							break;
						case 3:
							opset.setBase_mutation_rate(Double
											.parseDouble(val));
							if (next) pop.setMutationRate(Double
									.parseDouble(val));
							break;
						case 4:
							opset.setPressure(Double.parseDouble(val));
							if (next) pop.setPressure(Double
									.parseDouble(val));
							break;
						case 5:
							opset.setEntropy(Double.parseDouble(val));
							break;
						case 6:
							opset.setSpline_width(Double.parseDouble(val));
							break;
						case 7:
							opset.setGenes(Integer.parseInt(val));
							break;
						case 8:
							opset.setPoints(Integer.parseInt(val));
							break;
						case 9:
							opset.setCatastrophic_regime(Integer.parseInt(val));
							break;
						case 10:
							opset.setElite_mutation(Integer.parseInt(val));
							if (next) pop.setMutationFrequency(Integer.parseInt(val));
							break;

						case 11:
							opset.setInclination(Double.parseDouble(val));
							break;
						case 12:
							opset.setMu(Double.parseDouble(val));
							break;

						case 13:
							opset.setFflumi_max(Double.parseDouble(val));
							break;
						case 14:
							opset.setFflumi_min(Double.parseDouble(val));
							break;
						case 15:
							opset.setStarlumi_max(Double.parseDouble(val));
							break;
						case 16:
							opset.setStarlumi_min(Double.parseDouble(val));
							break;
						case 17:
							opset.setLumi_zero_max(Double.parseDouble(val));
							break;
						case 18:
							opset.setLumi_zero_min(Double.parseDouble(val));
							break;

						case 19:
							String s = new String(val.trim());
							if (s=="U") opset.setBand(0);
							if (s=="B") opset.setBand(1);
							if (s=="V") opset.setBand(2);
							if (s=="R") opset.setBand(3);
							if (s=="I") opset.setBand(4);
							break;

						case 20:
							opset.setExtinction(Double.parseDouble(val));
							break;
							
						case 21:
							opset.setDistance(Double.parseDouble(val));
							break;

						case 22:
							opset.setPeriod(Double.parseDouble(val));
							break;
						}
					} catch (Exception e) {
						System.out.println(e.getLocalizedMessage());
					}
				}
			}
		});

	}

	class NewThread implements Runnable {
		String name;
		Thread t;

		NewThread(String threadname) {
			name = threadname;
			t = new Thread(this, name);
			t.start();
		}

		public void run() {

			double pmin[] = new double[opset.getPoints() * 2 + 3];
			double pmax[] = new double[opset.getPoints() * 2 + 3];
			BS = new BinarySystem(opset.getMu(), opset.getInclination());
			for (int i = 0; i < opset.getPoints(); i++) {
				pmin[i * 2] = 0.01;
				pmax[i * 2] = (1.0 - BS.getL1R());
				if (BS.getR_shadow() < 1.0) {
					pmin[i * 2 + 1] = 0.00001 + Math.PI / 2.0;
					pmax[i * 2 + 1] = 3.0 * Math.PI / 2.0 - 0.00001;
				} else {
					pmin[i * 2 + 1] = 0.00001;
					pmax[i * 2 + 1] = 2.0 * Math.PI - 0.00001;
				}
			}

			pmin[opset.getPoints() * 2] = opset.getFflumi_min(); // ����������
																	// �����
			pmax[opset.getPoints() * 2] = opset.getFflumi_max();

			pmin[opset.getPoints() * 2 + 1] = opset.getStarlumi_min(); // �������������
																		// ������
			pmax[opset.getPoints() * 2 + 1] = opset.getStarlumi_max();

			pmin[opset.getPoints() * 2 + 2] = opset.getLumi_zero_min(); // Scale-hight
			pmax[opset.getPoints() * 2 + 2] = opset.getLumi_zero_max();

			pop = new Population(opset.getGenes(),	opset.getPoints() * 2 + 3, 6, opset.getPressure(), opset.getBase_mutation_rate(), pmin, pmax, opset.getElite_mutation(), opset.getEntropy());
			EclipseModel EM = new EclipseModel(data, BS, opset);
			dataFrame(data, model);
			pointsFrame(points);
			residualsFrame(residuals);
			infoFrame();

			
			
			int kk = 0;
			for (int i=0; i<180; i++) 
			{
				for (int j=0; j<90; j++) {
				lobe.data[kk][0]=BS.stars[0].getSurfaceElement(i, j).position.x;
				lobe.data[kk][1]=BS.stars[0].getSurfaceElement(i, j).position.z;
				lobe.data[kk+180*90][0]=BS.stars[0].getSurfaceElement(i, j).position.x;
				lobe.data[kk+180*90][1]=-BS.stars[0].getSurfaceElement(i, j).position.z;
				kk++;
			}
			}
			lobe.data_points = 360*90;
			lobe.calculate_bounds();
			
			
			fitness.data[0][0]=0;
			fitness.data[0][1]=0;
			fitness.calculate_bounds();
			fitness.data_points = 0;
			fitnessFrame(fitness);

			
			
			if (opset.getEntropy()!=0){
			entropy.data[0][0]=0;
			entropy.data[0][1]=0;
			entropy.calculate_bounds();
			entropy.data_points = 0;
			entropyFrame(entropy);
			}
			
			
			for (int model_no = 1; model_no <= opset.getModels(); model_no++) {

				pop.hi_2(EM);
				for (int i = 0; i < opset.getIterations(); i++) {

					pop.sort();

					if (!next) {
						break;
					}
					
					Formatter fmt = new Formatter();
					if (opset.getEntropy()==0)
						fmt.format("it %d hi^2 %e\n", i, pop
								.getFitting(0));
						else
						fmt.format("it %d hi^2 %e f/ent %e\n", i, pop
//								.getFitting(0), pop.getEntropy(0));
							.getFitting(0), (0.01+pop.getEntropy(0))/opset.getEntropy());

					
					fitness.data[i][0]=i;
					fitness.data_points = i;
					fitness.data[i][1]=Math.log10(pop.getFitting(0));
					fitness.calculate_bounds();
					
					if (opset.getEntropy()!=0){
					entropy.data[i][0]=i;
					entropy.data[i][1]=pop.getEntropy(0);
					entropy.data_points = i;
					entropy.calculate_bounds();}

					map.data_points = 200;
					
					
					TA.append(fmt.toString());
					double[] pars = pop.getParameters(0);

//					if (pop.getEntropy(0)==0)
//					{
//						for (int k = 0; k < pars.length; k++) System.out.println(pars[k]);
//					}
					
					for (int k = 0; k < data.data_points; k++) {
						model.data[k][0] = data.data[k][0];
						model.data[k][1] = EM.model(k, data.data[k][0], pars);
						residuals.data[k][0] = data.data[k][0];
						residuals.data[k][1] = data.data[k][1]-model.data[k][1];
					}
					residuals.calculate_bounds();
					for (int k = 0; k < points.data_points; k++) {
						points.data[k][0] = 1.0 + pars[k * 2]
								* Math.cos(pars[k * 2 + 1]);
						points.data[k][1] = pars[k * 2]
								* Math.sin(pars[k * 2 + 1]);
					}	
					panel.repaint();
					panel2.repaint();
					panel3.repaint();
					panel6.repaint();

					Formatter fmt1 = new Formatter();
					fmt1.format("%e", pars[opset.getPoints()*2]);
					DiskFluxValue.setText(fmt1.toString());
					Formatter fmt2 = new Formatter();
					fmt2.format("%e", pars[opset.getPoints()*2+1]);
					SecFluxValue.setText(fmt2.toString());
					Formatter fmt3 = new Formatter();
					fmt3.format("%e", pars[opset.getPoints()*2+2]);
					ConstantFluxValue.setText(fmt3.toString());

					if (opset.getEntropy()!=0) panel4.repaint();


					if (i%opset.getReportFrequency()==0)
					if (panel5!=null) 
					if (panel5.isShowing()){	
						double tmp, tmp1;
						int k;
						SpaceVector aa,p;
						aa = new SpaceVector();
						p = new SpaceVector();

						for (int ii=0;ii<201;ii++)
						{
						for (int jj=0;jj<201;jj++)
						{
						tmp=0;
						p.x = 1.0+(double)ii/200.0-0.5;
						p.y = (double)jj/200.0-0.5;
						p.z = 0;

						for (k=0;k<opset.getPoints();k++)
						{
						aa.x=points.data[k][0];
						aa.y=points.data[k][1];
						aa.z = 0;

						tmp1 =  Math.sqrt((p.Extract(aa)).square())/opset.getSpline_width();
						if (tmp1 < 3.0) {tmp+=Spl.arr[(int)(100.0*tmp1)];}
						}
						map.data[ii][jj]=pars[opset.getPoints()*2]*tmp;
						}
						}
						map.calculate2D_bounds();
						panel5.repaint();
					}
					
					pop.setMutationFrequency(opset.getElite_mutation());
					pop.next_generation();
					pop.hi_2(EM);


				}
			}
		}

	}

//	public void calulate_BT(){
//		double zero_mag_level_Ang, zero_mag_level_Hz, nu;
//		final double c = 2.9979e10;
//		final double k = 1.38e-16;
//		final double h = 6.626e-27;
//		final double solar_radius = 6.96e10; 
//		final double alpha=0.5;
//		final double X = 0.5; //hydrogen part
//		final double mu_e = 2.0/(1.0+X);
//		final double G = 6.67259e-8;
//		final double sigma_s = 5.67051e-5;
//		
//		
//		
//		if (opset.getiBand()==5)
//		{
//		zero_mag_level_Ang = 1.126e-9; //erg/s/cm^2/Angstrom                                  // I-Band
//		zero_mag_level_Hz = 3.33e-19*7980*7980*zero_mag_level_Ang; //erg/s/cm^2/Hz           // I-band
//		nu = c/7.98e-5;
//		}
//
//
//		if (opset.getiBand()==4)
//		{
//		zero_mag_level_Ang = 2.177e-9; //erg/s/cm^2/Angstrom                                  // R-Band
//		zero_mag_level_Hz = 3.33e-19*6410*6410*zero_mag_level_Ang; //erg/s/cm^2/Hz           // R-band
//		nu = c/6.41e-5;
//		}
//
//
//		if (opset.getiBand()==3)
//		{
//		zero_mag_level_Ang = 3.631e-9; //erg/s/cm^2/Angstrom                                  // V-Band
//		zero_mag_level_Hz = 3.33e-19*5450*5450*zero_mag_level_Ang; //erg/s/cm^2/Hz           // V-band
//		nu = c/5.45e-5;
//		}
//
//		if (opset.getiBand()==2)
//		{
//		zero_mag_level_Ang = 6.32e-9; //erg/s/cm^2/Angstrom                                  // B-Band
//		zero_mag_level_Hz = 3.33e-19*4380*4380*zero_mag_level_Ang; //erg/s/cm^2/Hz           // B-band
//		nu = c/4.38e-5;
//		}
//
//		if (opset.getiBand()==1)
//		{
//		zero_mag_level_Ang = 4.175e-9; //erg/s/cm^2/Angstrom                                  // U-Band
//		zero_mag_level_Hz = 3.33e-19*3660*3660*zero_mag_level_Ang; //erg/s/cm^2/Hz           // U-band
//		nu = c/3.66e-5;
//		}
//
//
//
//		double distance = opset.getDistance() * 3.085678e18;  
////		double Rwd = 2.8e9/Math.po(mu_e,5.0/3.0)/pow(M1,1.0/3.0);
//
//		a = Math.pow(Math.pow(opset.getPeriod()/365.25,2)*(M1+M2),1.0/3.0)*1.49598e13;
//
//
//
//
//		double fflumi = fflumi*zero_mag_level_Hz*Math.pow(2.512,opset.getExtinction()*opset.getDistance());
//
//		fflumi *= (distance)*(distance)/Math.cos(Math.PI*(90-opset.getInclination())/180.0);
//
//		double fflumizero = fflumi;
//
//
//		printf("zero_mag_level_Hz = %e\n", zero_mag_level_Hz);
//
//
//		strcat(fn,"_mapi.dat");
//
//		in = fopen(fn,"r");
//
//		do
//		{
//		inp = fscanf(in, "%lf %lf\n", &x, &y);
//		n++;
//		} while (inp!=EOF);
//		fclose(in);
//
//		n-=1;
//
//		printf("n = %i\n", n);
//
//
//		for(i=0;i<100;i++) radial[i]=0;
//
//		for(i=0;i<200;i++) for(j=0;j<3;j++) brightness[i][j]=0;
//
//
//
//		in = fopen(fn,"r");
//
//		for(i=0;i<n;i++)
//		{
//		fscanf(in, "%lf %lf\n", &x, &y);
//		data[i][0] = x-1.0;
//		data[i][1] = y;
//
//		}
//		fclose(in);
//
//
//		for(j=0;j<100;j++)
//		for(i=0;i<n;i++)
//		{
//		r = sqrt(data[i][0]*data[i][0]+data[i][1]*data[i][1]);
//
//
//		//radial[(int)(r/resolution)] = radial[(int)(r/resolution)] + 1.0;
//
//		if ((r<step*j+width/2)&&(r>step*j-width/2)) radial[j] = radial[j] + 1.0;
//
//		}
//
//
//		if (R_shadow>1.0)
//		{
//		i=0;
//		sigma[i]=sqrt(radial[i])/(M_PI*a*a*((width/2)*(width/2)))/n;
//		}
//
//
//		for(i=1;i<100;i++) 
//		{
//		cosc = (1+step*step*i*i-R_shadow*R_shadow)/2.0/(step*i);
//		//printf("step*i  %e radial[i] %e\n", step*i,radial[i]);
//		if (cosc<=-1.0) sigma[i]=sqrt(radial[i])/(M_PI*a*a*((step*i+width/2.0)*(step*i+width/2.0) - (step*i-width/2.0)*(step*i-width/2.0)))/n;
//		if (cosc>=1.0) sigma[i]=sqrt(radial[i])/(M_PI*a*a*((step*i+width/2.0)*(step*i+width/2.0) - (step*i-width/2.0)*(step*i-width/2.0)))/n;
//		if ((cosc<1.0)&&(cosc>-1.0)) sigma[i]=M_PI/acos(cosc)*sqrt(radial[i])/(M_PI*a*a*((step*i+width/2.0)*(step*i+width/2.0) - (step*i-width/2.0)*(step*i-width/2.0)))/n;
//		//if ((cosc<1.0)&&(cosc<1.0)) sigma[i]=sqrt(radial[i])/(M_PI*a*a*((step*i+width/2)*(step*i+width/2) - (step*i-width/2)*(step*i-width/2)))/n;
//		//printf("sigma %e\n", sigma[i]);
//		}
//
//
//		for(i=1;i<100;i++) 
//		{
//		cosc = (1.0+step*step*i*i-R_shadow*R_shadow)/2.0/(step*i);
//		nn[i]=radial[i];
//		if (cosc<=-1.0) radial[i]=radial[i]/(M_PI*a*a*((step*i+width/2.0)*(step*i+width/2.0) - (step*i-width/2.0)*(step*i-width/2.0)))/n;
//		if (cosc>=1.0) radial[i]=radial[i]/(M_PI*a*a*((step*i+width/2.0)*(step*i+width/2.0) - (step*i-width/2.0)*(step*i-width/2.0)))/n;
//		if ((cosc<1.0)&&(cosc>-1.0)) radial[i]=M_PI/acos(cosc)*radial[i]/(M_PI*a*a*((step*i+width/2.0)*(step*i+width/2.0) - (step*i-width/2.0)*(step*i-width/2.0)))/n;
//		//printf("%lf %lf\n", cosc, M_PI/acos(cosc));
//
//		//if ((cosc<1.0)&&(cosc>-1.0)) printf("i %i acos %e\n", i, acos(cosc));
//		}
//		printf("Ok\n");
//
//
//		itoa(ii,prefix,10);
//
//		strcpy(fn, prefix);
//		strcat(fn,"_radial_brightness.dat");
//
//		in = fopen(fn,"r");
//
//
//		out = fopen(fn,"w");
//
//		for(i=1;i<100;i++) 
//		{
//		//if (radial[i]>0) 
//		if (step*i<L1R)
//		fprintf(out, "%lf %e %e\n", step*i, fflumi*radial[i], fflumi*sigma[i]); //erg/s/cm^2/Hz
//		}
//
//		fclose(out);
//
//		}
//
//		for(j=0;j<n;j++)
//		{
//		brightness[j][0] = 0;
//		brightness[j][1] = 0;
//		brightness[j][2] = 0;
//		}
//
//
//
//		for(i=1;i<=number;i++) 
//		{
//
//		itoa(i,prefix,10);
//
//		strcpy(fn, prefix);
//		strcat(fn,"_radial_brightness.dat");
//
//		//in = fopen(fn,"r");
//
//
//		in = fopen(fn,"r");
//		n=0;
//		do
//		{
//		inp = fscanf(in, "%lf %lf\n", &x, &y);
//		n++;
//		} while (inp!=EOF);
//		fclose(in);
//
//		n-=1;
//
//
//		in = fopen(fn,"r");
//
//		for(j=0;j<n;j++)
//		{
//		fscanf(in, "%lf %lf %ls\n", &x, &y, &z);
//		brightness[j][0] = x;
//		brightness[j][1] = brightness[j][1]+y;
//		brightness[j][2] = brightness[j][2]+y*y;
//		}
//		fclose(in);
//
//		//printf("%i %i %s\n", i, number, fn);
//		}
//
//
//		if (number==1) {number=2;};
//		//printf("%i\n", number);
//
//
//		for(j=0;j<n;j++)
//		{
//		//printf("a %e\n", brightness[j][2]-brightness[j][1]*brightness[j][1]);
//		brightness[j][2] = sqrt((brightness[j][2]-brightness[j][1]*brightness[j][1]/((double) number))/(((double) number)-1.0));
//		brightness[j][1] = brightness[j][1]/((double) number);
//		//printf("%e\n", brightness[j][1]);
//		//printf("%e\n", brightness[j][2]);
//		}
//		
//		
//		
//	}
	
	
	
	public static void main(String[] args) {
		opset = new Options("./options.xml");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		eclipse_maps frame = new eclipse_maps();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

	}

}
