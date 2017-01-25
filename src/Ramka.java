import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.plaf.ProgressBarUI;

class MiejsceParkingowe {
	private int numer;
	private int pozycjaX;
	private int pozycjaY;
	private boolean czyZajete = false;

	public MiejsceParkingowe(int numer, int pozycjaX, int pozycjaY) {
		this.numer = numer;
		this.pozycjaX = pozycjaX;
		this.pozycjaY = pozycjaY;
	}

	public void zajmijMiejsce() {
		czyZajete = true;
	}

	public void zwolnijMiejsce() {
		czyZajete = false;

	}

	public boolean sprawdzMiejsce() {
		if (czyZajete == false)
			return true;
		else
			return false;
	}

	public int zwrocX() {
		return pozycjaX;
	}

	public int zwrocY() {
		return pozycjaY;
	}

	public int zwrocNumer() {
		return numer;
	}
}

class Parking extends Thread {
	private final int X;
	private final int Y;
	JProgressBar pasek = new JProgressBar();
	private int liczbaSamochodow = 0;
	private static int limitParkingu = 30;
	JLabel liczbaLabel = new JLabel("");
	JLabel limitLabel = new JLabel("/ " + Integer.toString(limitParkingu));
	int limitWierszy = 2;
	int limitKolumn = 5;
	MiejsceParkingowe[] miejscaParkingowe = new MiejsceParkingowe[limitParkingu];
	private static final Semaphore semaforParkingu = new Semaphore(limitParkingu, true);

	public Parking(int x, int y) {
		this.X = x;
		this.Y = y;
		this.setDaemon(true);
		this.start();
		stworzMiejsca();
	}

	private void stworzMiejsca() {
		int j = 0;
		int wiersz = 0;
		for (int i = 0; i <= limitParkingu - 1; i++) {
			if (j > limitKolumn) {
				j = 0;
				wiersz++;
			}
			miejscaParkingowe[i] = new MiejsceParkingowe(i, X + (j * 35), Y + 15 + wiersz * 35);
			j++;

		}
	}

	public synchronized int zajmijMiejsce() {
		for (MiejsceParkingowe p : miejscaParkingowe) {
			if (p.sprawdzMiejsce()) {
				p.zajmijMiejsce();
				liczbaSamochodow++;
				return p.zwrocNumer();
			}
			

		}
		return -1;

	}
	
	

	public synchronized boolean sprawdzZajetosc() {
		if (semaforParkingu.tryAcquire())
			return true;
		else
			return false;
	}

	public int miejsceX(int numer) {
		return miejscaParkingowe[numer].zwrocX();
	}

	public int miejsceY(int numer) {
		return miejscaParkingowe[numer].zwrocY();
	}

	public synchronized void zwolnijMiejsce(int numer) {

		miejscaParkingowe[numer].zwolnijMiejsce();
		liczbaSamochodow--;
		semaforParkingu.release();

	}

	public Semaphore zwrocSemafor() {
		return semaforParkingu;
	}

	public void run() {

		while (true) {
			pasek.setValue(liczbaSamochodow);
			liczbaLabel.setText(Integer.toString(liczbaSamochodow));
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void stworzPasek(JPanel panel) {
		pasek.setValue(0);
		pasek.setMaximum(limitParkingu);
		pasek.setMinimum(0);
		pasek.setBounds(X, Y - 50, 150, 20);
		pasek.setStringPainted(true);
		liczbaLabel.setBounds(X + 160, Y - 50, 25, 10);
		limitLabel.setBounds(X + 180, Y - 50, 35, 10);
		panel.add(pasek);
		panel.add(liczbaLabel);
		panel.add(limitLabel);
	}

	public void rysuj(Graphics g) {
		g.setColor(Color.GREEN);
		g.fillRect(X, Y, 210, 190);
		g.setColor(Color.BLACK);
		g.drawString("Parking", X + 80, Y + 100);

	}

}

class Samochod extends Thread {
	int x, y, predkosc;
	int czasPauzy = 0;
	boolean czyRysowac = false;
	boolean gora = true;
	boolean zezwolenie = true;
	int indeks;
	static int pozycja = 0;
	boolean zaparkowany = false;
	private int numerMiejsca = 0;
	boolean naParkingu = false;
	Parking parking;

	public Samochod(int czasPauzy, int x, int y, int predkosc, int indeks, Parking parking) {
		this.czasPauzy = czasPauzy * 5 * indeks;
		this.x = x;
		this.y = y;
		this.predkosc = predkosc;
		this.indeks = indeks;
		this.parking = parking;
		this.start();

	}

	public void rysuj(Graphics g) {
		if (czyRysowac) {
			g.setColor(Color.GRAY);
			g.fillRect(x, y, 30, 30);
			g.setColor(Color.RED);
			// g.fillRect(x, y, 20, 30);
			g.drawString(Integer.toString(indeks), x, y);

		}
	}

	public void move() {

		if (czyRysowac && zezwolenie) {

			if (y > 250 && gora)
				y -= predkosc;

			if (y == 250) {
				x += predkosc;
				gora = false;
			}
			if (x == 1500 && !gora) {
				y += predkosc;
			}

			if (y == 950) {
				x -= predkosc;
			}

			if (x == 20)
				gora = true;

		}

	}

	private void wjedz() {
		zezwolenie = false;
		naParkingu = true;
		numerMiejsca = parking.zajmijMiejsce();
		x = parking.miejsceX(numerMiejsca);
		y = parking.miejsceY(numerMiejsca);
	}

	private void wyjedz() {
		try {
			Thread.sleep((int) (Math.random() * (15000 - 5000)) + 5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			parking.zwolnijMiejsce(numerMiejsca);
			x = -20;
			y = -20;
		}
	}

	public void run() {
		while (true) {
			if (y < 270 && x < 30) {

				if (parking.sprawdzZajetosc()) {
					wjedz();
					wyjedz();
					return;
				} else
					zezwolenie = true;
			} else
				System.out.print("");

		}
	}

	public void zmniejszPauze() {
		if (czasPauzy <= 0) {
			czyRysowac = true;
			// System.out.println("czas pauzy" + czasPauzy);
		} else {
			czasPauzy--;
			// System.out.println("czas pauzy" + czasPauzy);
		}
	}

}

public class Ramka extends JPanel {

	private static final int Y_HEIGHT = 1000;
	private static final int X_WIDTH = 1600;
	private int predkosc = 1;
	private List<Samochod> samochody;
	private Timer timer;
	int x = 20;
	int y = 400;
	int czasPauzy = 10;
	int indeks = 1;
	JPanel panel;
	Parking parking = new Parking(10, 50);

	public Ramka() {

		samochody = stworzListeSamochodow();
		timer = new Timer(4, new ActionListener() {
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
		 * JButton reset = new JButton("Reset"); reset.addActionListener(new
		 * ActionListener() { public void actionPerformed(ActionEvent e) {
		 * samochody = stworzListeSamochodow(); indeks = 1; timer.restart();
		 * 
		 * } });
		 */

		panel = new JPanel();
		JPanel parkingPanel = new JPanel();
		parking.stworzPasek(parkingPanel);
		panel.add(start);
		// panel.add(reset);
		parkingPanel.setLayout(null);
		setLayout(new BorderLayout());
		parkingPanel.setBackground(new Color(0, 0, 0, 0));

		add(panel, BorderLayout.PAGE_START);
		add(parkingPanel);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		parking.rysuj(g);
		for (Samochod samochod : samochody) {
			samochod.rysuj(g);
			// System.out.println("rysuje");

		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(X_WIDTH, Y_HEIGHT);
	}

	private List<Samochod> stworzListeSamochodow() {
		List<Samochod> list = new ArrayList<>();
		for (int i = 0; i <= 350; i++) {
			list.add(new Samochod(czasPauzy, x, y, predkosc, indeks++, parking));
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
