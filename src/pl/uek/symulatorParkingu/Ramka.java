package pl.uek.symulatorParkingu;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Ramka extends JPanel {

	private final int WYSOKOSC_Y = 800;
	private final int SZEROKOSC_X = 1300;
	private int predkosc = 1;
	private int dlugoscSamochodu = 30;
	private int odstep = dlugoscSamochodu / 2;
	private int opoznienie = 5;
	private int poczatkowyX = 20;
	private int poczatkowyY = 1000;
	private int czasPauzy = 10;
	private int indeks = 1;
	private int liczbaSamochodow = 40;
	private Timer timer;
	private Parking parking;
	private StacjaBenzynowa stacja;
	private List<Samochod> samochody;

	public Ramka() {
		stworzTimer();
		stworzPanelGorny();
		stworzPanelPaskow();
		samochody = stworzListeSamochodow();
	}

	private void stworzTimer() {
		timer = new Timer(opoznienie, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (Samochod samochod : samochody) {
					samochod.jedz();
					samochod.zmniejszPauze();
					repaint();

				}

			}
		});
	}

	private void stworzPanelGorny() {

		JButton start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timer.start();

			}
		});

		JButton dodaj = new JButton("Dodaj samochod");
		dodaj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				samochody.add(new Samochod(0, poczatkowyX, poczatkowyY, predkosc, indeks++,
						((int) (Math.random() * (10000 - 5000)) + 5000), 10000, dlugoscSamochodu, parking, stacja));

			}
		});

		JButton przyspiesz = new JButton("+");
		przyspiesz.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (opoznienie == 1)
					przyspiesz.setEnabled(false);
				timer.setDelay(opoznienie--);

			}
		});

		JButton zwolnij = new JButton("-");
		zwolnij.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				timer.setDelay(opoznienie++);
				przyspiesz.setEnabled(true);
			}
		});

		JPanel panelGorny = new JPanel();
		JLabel wybierzPredkosc = new JLabel("Zmień prędkość:  ");
		panelGorny.add(start);
		panelGorny.add(dodaj);
		panelGorny.add(wybierzPredkosc);
		panelGorny.add(zwolnij);
		panelGorny.add(przyspiesz);

		setLayout(new BorderLayout());
		add(panelGorny, BorderLayout.PAGE_START);
	}

	private void stworzPanelPaskow() {
		JPanel panelPaskow = new JPanel();
		parking = new Parking("Parking", 10, 90, 230, 200, 20, 5, odstep, panelPaskow);
		stacja = new StacjaBenzynowa("Stacja benzynowa", 1010, 210, 100, 150, 6, 1, odstep, panelPaskow);
		panelPaskow.setLayout(null);
		panelPaskow.setBackground(new Color(0, 0, 0, 0));
		add(panelPaskow);
	}

	private List<Samochod> stworzListeSamochodow() {
		List<Samochod> listaSamochodow = new ArrayList<>();
		for (int i = 1; i <= liczbaSamochodow; i++) {
			listaSamochodow.add(new Samochod(czasPauzy, poczatkowyX, poczatkowyY, predkosc, indeks++,
					((int) (Math.random() * (10000 - 5000)) + 5000), 10000, dlugoscSamochodu, parking, stacja));
		}
		return listaSamochodow;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(new Color(246,238,232)); 
		g.fillRect(0, 0, SZEROKOSC_X, WYSOKOSC_Y);
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

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.add(new Ramka());
				frame.pack();
				frame.setTitle("Symulacja parkingu - Karolina Tokarz, Wojciech Rzemieniuk");
				frame.setResizable(false);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}
}
