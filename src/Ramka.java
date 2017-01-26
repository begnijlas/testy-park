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

	private static final int Y_HEIGHT = 1000;
	private static final int X_WIDTH = 1600;
	private int predkosc = 1;
	private List<Samochod> samochody;
	private Timer timer;
	int x = 20;
	int y = 1000;
	int czasPauzy = 10;
	int indeks = 1;
	JPanel panel;
	Parking parking = new Parking(10, 50,"Parking");
	StacjaBenzynowa stacja = new StacjaBenzynowa(1300,200,"Stacja benzynowa");

	public Ramka() {

		samochody = stworzListeSamochodow();
		timer = new Timer(5, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (Samochod samochod : samochody) {
					samochod.move();
					samochod.zmniejszPauze();
					repaint();

				}
			}
		});
		JButton start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timer.start();

			}
		});
		/*
		  JButton reset = new JButton("Reset"); reset.addActionListener(new
		  ActionListener() { public void actionPerformed(ActionEvent e) {
		  samochody = stworzListeSamochodow(); indeks = 1; timer.restart();
		  
		  } });
		 */

		panel = new JPanel();
		JPanel panelPaskow = new JPanel();
		parking.stworzPasek(panelPaskow);
		stacja.stworzPasek(panelPaskow);
		panel.add(start);
		//panel.add(reset);
		panelPaskow.setLayout(null);
		setLayout(new BorderLayout());
		panelPaskow.setBackground(new Color(0, 0, 0, 0));

		add(panel, BorderLayout.PAGE_START);
		add(panelPaskow);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		parking.rysuj(g);
		stacja.rysuj(g);
		for (Samochod samochod : samochody) {
			samochod.rysuj(g);
		

		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(X_WIDTH, Y_HEIGHT);
	}

	private List<Samochod> stworzListeSamochodow() {
		List<Samochod> list = new ArrayList<>();
		for (int i = 0; i <= 30; i++) {
			list.add(new Samochod(czasPauzy, x, y, predkosc, indeks++, parking,stacja));
		}
		return list;
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
