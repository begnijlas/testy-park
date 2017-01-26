import java.awt.Color;
import java.awt.Graphics;
import java.util.concurrent.Semaphore;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

class Parking extends Thread {
	private String nazwa="";
	private final int X;
	private final int Y;
	JProgressBar pasek = new JProgressBar();
	private int liczbaSamochodow = 0;
	private static int limitParkingu = 10;
	JLabel liczbaLabel = new JLabel("");
	JLabel limitLabel = new JLabel("/ " + Integer.toString(limitParkingu));
	int limitWierszy = 2;
	int limitKolumn = 5;
	MiejsceParkingowe[] miejscaParkingowe = new MiejsceParkingowe[limitParkingu];
	private  final Semaphore semaforParkingu = new Semaphore(limitParkingu, true);

	public Parking(int x, int y,String nazwa) {
		this.nazwa=nazwa;
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
		g.drawString(nazwa, X + 80, Y + 100);

	}

}