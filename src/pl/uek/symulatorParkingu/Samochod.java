package pl.uek.symulatorParkingu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

class Samochod extends Thread {
	private int x, y, predkosc, indeks, dlugoscSamochodu;
	private int czasPauzy = 0;
	private double stanBaku, pojemnoscBaku;
	private boolean czyRysowacFigury = false;
	private boolean jedzieWgore = true;
	private boolean zezwolenie = true;
	private boolean wyjazd = false;
	private boolean zakoncz = false;
	private Parking parking;
	private StacjaBenzynowa stacja;
	private int R = (int) (Math.random( )*(100-60))+60;
    	private int G = (int)(Math.random( )*(93-50))+50;
    	private int B= (int)(Math.random( )*(204-120))+120;
    	private Color pojazd = new Color(R, G, B);
	
	public Samochod(int czasPauzy, int x, int y, int predkosc, int indeks, int stanBaku, int pojemnoscBaku,
			int dlugoscSamochodu, Parking parking, StacjaBenzynowa stacja) {
		this.czasPauzy = czasPauzy * indeks * 5;
		this.x = x;
		this.y = y;
		this.predkosc = predkosc;
		this.indeks = indeks;
		this.stanBaku = stanBaku;
		this.pojemnoscBaku = pojemnoscBaku;
		this.dlugoscSamochodu = dlugoscSamochodu;
		this.parking = parking;
		this.stacja = stacja;
		this.start();
	}

	private void wjedz(Parking p) {
		zezwolenie = false;
		int numerMiejsca = p.zajmijMiejsce();
		x = p.miejsceX(numerMiejsca);
		y = p.miejsceY(numerMiejsca);

		if (p.equals(stacja)) {
			tankuj();
			wyjazd = false;
			p.zwolnijMiejsce(numerMiejsca);
			x = 1050;
			y = 350;
		} else if (p.equals(parking)) {
			try {
				Thread.sleep((int) (Math.random() * (20000 - 10000)) + 10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			p.zwolnijMiejsce(numerMiejsca);
			x = 250;
			y = 150;
			wyjazd = true;
		}
		zezwolenie = true;

	}

	private void tankuj() {
		for (int i = (int) stanBaku; i < pojemnoscBaku; i++) {
			stanBaku++;
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private double obliczPaliwo() {
		double procentBaku = 0;
		return procentBaku = (stanBaku / pojemnoscBaku);
	}

	public void rysuj(Graphics g) {
		if (czyRysowacFigury) {

			g.setColor(pojazd);
			g.fillRect(x, y, dlugoscSamochodu, dlugoscSamochodu);
			if (obliczPaliwo() * 100 > 70)
				g.setColor(new Color (134,167,112));
			else if (obliczPaliwo() * 100 <= 70 && obliczPaliwo() * 100 > 30)
				g.setColor(new Color(231,194,123));
			else
				g.setColor(new Color(180,85,80));
			g.fillRect(x, y, dlugoscSamochodu, (int) (obliczPaliwo() * dlugoscSamochodu));
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.drawString(Integer.toString(indeks), x, y + 30);

		}
	}

	public void run() {
		while (true) {

			if (y < 295 && x < 50) {
				if (parking.sprawdzZajetosc()) {
					wjedz(parking);
					return;
				} else
					zezwolenie = true;
			} else if (x > 950 && (y > 240 && y < 300)) {
				if (stacja.sprawdzZajetosc()) {
					wjedz(stacja);

				}
			} else
				System.out.print("");

			if (y == 130) {
				czyRysowacFigury = false;
				zakoncz = true;
				this.interrupt();
			}
		}
	}

	public void jedz() {

		if (czyRysowacFigury && zezwolenie) {
			stanBaku--;

			if (wyjazd) {
				if (y == 150) {
					x += predkosc;
				}
				if (x == 400)
					y -= predkosc;
			} else {
				if (y > 280 && jedzieWgore)
					y -= predkosc;

				if (y <= 290) {
					x += predkosc;
					jedzieWgore = false;
				}
				if (y == 750 && x > 20) {
					x -= predkosc;
				}
				if (x >= 970 && y < 750 && !jedzieWgore) {
					y += predkosc;
				}
				if (x == 20)
					jedzieWgore = true;
			}
		}
	}

	public void zmniejszPauze() {
		if (czasPauzy <= 0) {
			czyRysowacFigury = true;
		} else {
			czasPauzy--;
		}
	}

	public boolean usunSamochod() {
		if (zakoncz)
			return true;
		else
			return false;
	}

}
