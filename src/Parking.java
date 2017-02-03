import java.awt.Color;
import java.awt.Graphics;
import java.util.concurrent.Semaphore;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

class Parking extends Thread {
	private final int wspolrzednaX,wspolrzednaY,szerokosc,wysokosc;
	private int liczbaSamochodow = 0;
	private int limitStanowisk,limitKolumn,sygnalizatorX,sygnalizatorY;
	private String nazwa;
	private JProgressBar pasekPostepu;
	private JLabel iloscSamochodow,limitSamochodow;
	private MiejsceParkingowe[] miejscaParkingowe;
	private final Semaphore semaforParkingu;
	private boolean czerwoneSwiatlo=false;

	public Parking( String nazwa,int x, int y,int szerokosc,int wysokosc , int limitStanowisk, int limitKolumn,int odstep,JPanel panel) {
		this.wspolrzednaX = x;
		this.wspolrzednaY = y;
		this.szerokosc=szerokosc;
		this.wysokosc=wysokosc;
		this.nazwa = nazwa;
		this.limitStanowisk=limitStanowisk;
		this.limitKolumn=limitKolumn;
		
		
		pasekPostepu = new JProgressBar();
		iloscSamochodow = new JLabel("");
		limitSamochodow = new JLabel("/ " + Integer.toString(limitStanowisk));
		semaforParkingu = new Semaphore(limitStanowisk, true);
		
		stworzMiejsca(odstep);
		stworzPasek(panel);
		ustawSygnalizator(wspolrzednaX+55, wspolrzednaY+wysokosc+50);
		this.setDaemon(true);
		this.start();
	}
	
	protected void ustawSygnalizator(int x,int y){
		sygnalizatorX=x;
		sygnalizatorY=y;
	}

	private void stworzMiejsca(int odstep) {
		miejscaParkingowe = new MiejsceParkingowe[limitStanowisk];
		int kolumna = 0;
		int wiersz = 0;
		for (int numerMiejsca = 0; numerMiejsca <= limitStanowisk - 1; numerMiejsca++) {
			if (kolumna > limitKolumn || kolumna * (30 + odstep) > szerokosc - 10) {
				kolumna = 0;
				wiersz++;
			}
			miejscaParkingowe[numerMiejsca] = new MiejsceParkingowe(numerMiejsca, wspolrzednaX + (kolumna * (30 + odstep)) + 10, wspolrzednaY + 10 + wiersz *( 35+odstep));
			kolumna++;
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
			pasekPostepu.setValue(liczbaSamochodow);
			iloscSamochodow.setText(Integer.toString(liczbaSamochodow));
			if(liczbaSamochodow<limitStanowisk)czerwoneSwiatlo=false;
			else czerwoneSwiatlo=true;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void stworzPasek(JPanel panel) {
		
		
		pasekPostepu.setValue(0);
		pasekPostepu.setMaximum(limitStanowisk);
		pasekPostepu.setMinimum(0);
		pasekPostepu.setBounds(wspolrzednaX, wspolrzednaY - 50, 150, 20);
		pasekPostepu.setStringPainted(true);
		
		iloscSamochodow.setBounds(wspolrzednaX + 160, wspolrzednaY - 50, 25, 10);
		limitSamochodow.setBounds(wspolrzednaX + 180, wspolrzednaY - 50, 35, 10);
		
		panel.add(pasekPostepu);
		panel.add(iloscSamochodow);
		panel.add(limitSamochodow);
	}
	


	public void rysuj(Graphics g) {
		g.setColor(Color.GREEN);
		g.fillRect(wspolrzednaX, wspolrzednaY, szerokosc, wysokosc);
		g.setColor(Color.BLACK);
		g.drawString(nazwa, wspolrzednaX+10, wspolrzednaY+30);
		g.setColor(Color.GRAY);
		g.fillRect(sygnalizatorX-10,sygnalizatorY, 35, 60);
		if(czerwoneSwiatlo){
			g.setColor(Color.RED);
			g.fillOval(sygnalizatorX, sygnalizatorY, 20, 20);
		}else{
			g.setColor(Color.GREEN);
			g.fillOval(sygnalizatorX, sygnalizatorY + 30, 20, 20);
		}

	}

}