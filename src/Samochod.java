import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

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
	boolean wyjazd = false;
	Parking parking;
	Rectangle samochod;
	double stanBaku = 6000;
	double pojemnoscBaku = 6000;
	double procentBaku;
	StacjaBenzynowa stacja;

	public Samochod(int czasPauzy, int x, int y, int predkosc, int indeks, Parking parking, StacjaBenzynowa stacja) {
		this.czasPauzy = czasPauzy * 5 * indeks;
		this.x = x;
		this.y = y + 5 * indeks;
		this.predkosc = predkosc;
		this.indeks = indeks;
		this.parking = parking;
		this.stacja = stacja;
		samochod = new Rectangle(this.x, this.y, 30, 30);
		this.start();

	}

	public void rysuj(Graphics g) {
		if (czyRysowac) {

			g.setColor(Color.GRAY);
			samochod.setLocation(x, y);
			Graphics2D g2d = (Graphics2D) g;
			g2d.fill(samochod);
			// g.fillRect(x, y, 30, 30);
			g.setColor(Color.YELLOW);
			g.fillRect(x, y, 30, (int) obliczPaliwo());
			g.setColor(Color.RED);
			// g.fillRect(x, y, 20, 30);
			g.drawString(Integer.toString(indeks), x + 5, y + 20);

		}
	}

	private double obliczPaliwo() {
		return procentBaku = (stanBaku / pojemnoscBaku * 100) / 3;
	}

	public void move() {

		if (czyRysowac && zezwolenie) {
			stanBaku--;
			if (y > 250 && gora)
				y -= predkosc;

			if (y == 250) {
				x += predkosc;
				gora = false;
			}
			if (x == 1250 && !gora) {
				y += predkosc;
			}

			if (y == 950) {
				x -= predkosc;
			}

			if (x == 20)
				gora = true;

			if (y == 150 && x < 500 && wyjazd) {
				x += predkosc;
			} else if (x == 500 && wyjazd)
				y -= predkosc;
		}

	}

	private void wjedzNaParking() {
		zezwolenie = false;
		numerMiejsca = parking.zajmijMiejsce();
		x = parking.miejsceX(numerMiejsca);
		y = parking.miejsceY(numerMiejsca);
		try {
			Thread.sleep((int) (Math.random() * (15000 - 5000)) + 5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			parking.zwolnijMiejsce(numerMiejsca);
			x = 250;
			y = 150;
			wyjazd = true;
			zezwolenie = true;
		}
	}

	private void wjedzNaStacje() {
		zezwolenie = false;
		numerMiejsca = stacja.zajmijMiejsce();
		x = stacja.miejsceX(numerMiejsca);
		y = stacja.miejsceY(numerMiejsca);
		for(int i=(int)stanBaku;i<pojemnoscBaku;i++){
			stanBaku++;
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
			stacja.zwolnijMiejsce(numerMiejsca);
			x = 1250;
			y = 400;
			zezwolenie = true;
		}
	

	public void run() {
		while (true) {
			if (y < 260 &&x <140) {
				if (parking.sprawdzZajetosc()) {
					wjedzNaParking();
					return;
				} else
					zezwolenie = true;
			}else System.out.print("");
			
			if(x>1230 && (y>240 && y< 400)){
				if(stacja.sprawdzZajetosc()){
					wjedzNaStacje();
					
				}
			}


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