
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