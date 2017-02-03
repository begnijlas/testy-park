package pl.uek.symulatorParkingu;
import javax.swing.JPanel;

class StacjaBenzynowa extends Parking {
	public StacjaBenzynowa(String nazwa, int x, int y, int szerokosc, int wysokosc, int limitStanowisk, int limitKolumn,
			int odstep, JPanel panel) {
		
		super(nazwa, x, y, szerokosc, wysokosc, limitStanowisk, limitKolumn, odstep, panel);
		
		ustawSygnalizator(x - 30, y - 30);
	}

}