package pl.uek.symulatorParkingu;
import java.awt.Color;
import java.awt.Graphics;
import java.util.concurrent.Semaphore;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

class Parking extends Thread {
	private final int WSPOLRZEDNA_X, WSPOLRZEDNA_Y, SZEROKOSC, WYSOKOSC;
	private int liczbaSamochodow = 0;
	private int limitStanowisk, limitKolumn, sygnalizatorX, sygnalizatorY;
	private String nazwa;
	private JProgressBar pasekPostepu;
	private JLabel iloscSamochodow, limitSamochodow;
	private MiejsceParkingowe[] miejscaParkingowe;
	private final Semaphore SEMAFOR;
	private boolean czerwoneSwiatlo = false;

	public Parking(String nazwa, int x, int y, int szerokosc, int wysokosc, int limitStanowisk, int limitKolumn,
			int odstep, JPanel panel) {
		this.WSPOLRZEDNA_X = x;
		this.WSPOLRZEDNA_Y = y;
		this.SZEROKOSC = szerokosc;
		this.WYSOKOSC = wysokosc;
		this.nazwa = nazwa;
		this.limitStanowisk = limitStanowisk;
		this.limitKolumn = limitKolumn;

		pasekPostepu = new JProgressBar();
		iloscSamochodow = new JLabel("");
		limitSamochodow = new JLabel("/ " + Integer.toString(limitStanowisk));
		SEMAFOR = new Semaphore(limitStanowisk, true);

		stworzMiejsca(odstep);
		stworzPasek(panel);
		ustawSygnalizator(WSPOLRZEDNA_X + 55, WSPOLRZEDNA_Y + wysokosc + 50);
		this.setDaemon(true);
		this.start();
	}

	protected void ustawSygnalizator(int x, int y) {
		sygnalizatorX = x;
		sygnalizatorY = y;
	}

	private void stworzMiejsca(int odstep) {
		miejscaParkingowe = new MiejsceParkingowe[limitStanowisk];
		int kolumna = 0;
		int wiersz = 0;
		for (int numerMiejsca = 0; numerMiejsca <= limitStanowisk - 1; numerMiejsca++) {
			if (kolumna > limitKolumn || kolumna * (30 + odstep) > SZEROKOSC - 10) {
				kolumna = 0;
				wiersz++;
			}
			miejscaParkingowe[numerMiejsca] = new MiejsceParkingowe(numerMiejsca,
					WSPOLRZEDNA_X + (kolumna * (30 + odstep)) + 10, WSPOLRZEDNA_Y + 10 + wiersz * (35 + odstep));
			kolumna++;
		}
	}

	private void stworzPasek(JPanel panel) {

		pasekPostepu.setValue(0);
		pasekPostepu.setMaximum(limitStanowisk);
		pasekPostepu.setMinimum(0);
		pasekPostepu.setBounds(WSPOLRZEDNA_X, WSPOLRZEDNA_Y - 60, 150, 20);
		pasekPostepu.setStringPainted(true);

		iloscSamochodow.setBounds(WSPOLRZEDNA_X + 160, WSPOLRZEDNA_Y - 55, 25, 10);
		limitSamochodow.setBounds(WSPOLRZEDNA_X + 180, WSPOLRZEDNA_Y - 55, 35, 10);

		panel.add(pasekPostepu);
		panel.add(iloscSamochodow);
		panel.add(limitSamochodow);
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
		if (SEMAFOR.tryAcquire())
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
		SEMAFOR.release();

	}

	public void run() {

		while (true) {
			pasekPostepu.setValue(liczbaSamochodow);
			iloscSamochodow.setText(Integer.toString(liczbaSamochodow));
			if (liczbaSamochodow < limitStanowisk)
				czerwoneSwiatlo = false;
			else
				czerwoneSwiatlo = true;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void rysuj(Graphics g) {
		g.setColor(Color.GREEN);
		g.fillRect(WSPOLRZEDNA_X, WSPOLRZEDNA_Y, SZEROKOSC, WYSOKOSC);
		g.setColor(Color.BLACK);
		g.drawString(nazwa, WSPOLRZEDNA_X + 10, WSPOLRZEDNA_Y - 30);
		g.setColor(Color.GRAY);
		g.fillRect(sygnalizatorX - 7, sygnalizatorY, 35, 60);
		if (czerwoneSwiatlo) {
			g.setColor(Color.RED);
			g.fillOval(sygnalizatorX, sygnalizatorY, 20, 20);
		} else {
			g.setColor(Color.GREEN);
			g.fillOval(sygnalizatorX, sygnalizatorY + 30, 20, 20);
		}

	}

}