import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;


import javax.swing.JButton;
import javax.swing.JFrame;

import javax.swing.JPanel;

import javax.swing.SwingUtilities;
import javax.swing.Timer;



@SuppressWarnings("serial")
public class Ramka extends JPanel {

	private static final int WYSOKOSC_Y = 800;
	private static final int SZEROKOSC_X = 1300;
	private int predkosc = 1;
	private List<Samochod> samochody;
	private Timer timer;
	private int poczatkowyX = 20;
	private int poczatkowyY = 1000;
	private int czasPauzy = 10;
	private int indeks = 1;
	private Parking parking;
	private StacjaBenzynowa stacja;
	private int dlugoscSamochodu = 30;
	private int odstep = dlugoscSamochodu /2;
	public Ramka() {
		stworzTimer();
		stworzPanelGorny();
		stworzPanelPaskow();
		samochody = stworzListeSamochodow();	
	}

	private void stworzTimer(){
		timer = new Timer(5, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (Samochod samochod : samochody) {
					samochod.move();
					samochod.zmniejszPauze();
					repaint();
				}
			}
		});
	}
	
	private void stworzPanelGorny(){
		JButton start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timer.start();

			}
		});
		
		JPanel panelGorny = new JPanel();
		panelGorny.add(start);
		setLayout(new BorderLayout());
		add(panelGorny, BorderLayout.PAGE_START);
	}
	
	private void stworzPanelPaskow(){
		JPanel panelPaskow = new JPanel();
		parking = new Parking("Parking",10, 50,230,190,20,5,odstep,panelPaskow);
		stacja = new StacjaBenzynowa("Stacja benzynowa",1000,200,130,150,6,1,odstep,panelPaskow);
		panelPaskow.setLayout(null);
		panelPaskow.setBackground(new Color(0, 0, 0, 0));
		add(panelPaskow);
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		g.fillRect(0,0, SZEROKOSC_X, WYSOKOSC_Y);
		parking.rysuj(g);
		stacja.rysuj(g);
		for (Samochod samochod : samochody) {
			samochod.rysuj(g);
		

		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(SZEROKOSC_X, WYSOKOSC_Y);
	}

	private List<Samochod> stworzListeSamochodow() {
		List<Samochod> listaSamochodow = new ArrayList<>();
		for (int i = 0; i <= 30; i++) {
			listaSamochodow.add(new Samochod(czasPauzy, poczatkowyX, poczatkowyY, predkosc, indeks++, ((int)(Math.random()*(10000-5000))+5000), 10000, dlugoscSamochodu, parking,stacja));
		}
		return listaSamochodow;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.add(new Ramka());
				frame.pack();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}
}
