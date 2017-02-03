import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

class Samochod extends Thread {
	private int x, y, predkosc,indeks,dlugoscSamochodu;
	private int czasPauzy = 0;
	private boolean czyRysowacFigury = false;
	private boolean jedzieWgore = true;
	private boolean zezwolenie = true;
	boolean wyjazd = false;
	private Parking parking;
	private double stanBaku,pojemnoscBaku;
	StacjaBenzynowa stacja;

	public Samochod(int czasPauzy, int x, int y, int predkosc, int indeks,int stanBaku,int pojemnoscBaku,int dlugoscSamochodu ,Parking parking, StacjaBenzynowa stacja) {
		this.czasPauzy = czasPauzy * 5 * indeks;
		this.x = x;
		this.y = y + 5 * indeks;
		this.predkosc = predkosc;
		this.indeks = indeks;
		this.stanBaku=stanBaku;
		this.pojemnoscBaku=pojemnoscBaku;
		this.dlugoscSamochodu=dlugoscSamochodu;
		this.parking = parking;
		this.stacja = stacja;
		this.start();

	}

	public void rysuj(Graphics g) {
		if (czyRysowacFigury) {

			g.setColor(Color.GRAY);
			g.fillRect(x, y, dlugoscSamochodu, dlugoscSamochodu);
			g.setColor(Color.YELLOW);
			g.fillRect(x, y, dlugoscSamochodu, (int)(obliczPaliwo() * dlugoscSamochodu));
			g.setColor(Color.RED);
			
			g.drawString(Integer.toString(indeks), x + 5, y + 20);

		}
	}

	private double obliczPaliwo() {
		double procentBaku=0;
		return procentBaku = (stanBaku / pojemnoscBaku);
	}

	public void move() {

		if (czyRysowacFigury && zezwolenie) {
			stanBaku--;
			if (y > 250 && jedzieWgore)
				y -= predkosc;

			if (y == 250) {
				x += predkosc;
				jedzieWgore = false;
			}
			if (x == 1250 && !jedzieWgore) {
				y += predkosc;
			}

			if (y == 950) {
				x -= predkosc;
			}

			if (x == 20)
				jedzieWgore = true;

			if (y == 150 && x < 500 && wyjazd) {
				x += predkosc;
			} else if (x == 500 && wyjazd)
				y -= predkosc;
		}

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
			x = 1250;
			y = 400;
		} else if (p.equals(parking)) {
			try {
				Thread.sleep((int) (Math.random() * (15000 - 5000)) + 5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
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
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void run() {
		while (true) {
			if (y < 260 && x < 140) {
				if (parking.sprawdzZajetosc()) {
					wjedz(parking);
					return;
				} else
					zezwolenie = true;
			} else if (x > 1230 && (y > 240 && y < 400)) {
				if (stacja.sprawdzZajetosc()) {
					wjedz(stacja);

				}
			}else System.out.print("");
			
			
			}
		}
	

	public void zmniejszPauze() {
		if (czasPauzy <= 0) {
			czyRysowacFigury = true;
		} else {
			czasPauzy--;
		}
	}

}