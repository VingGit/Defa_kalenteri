import java.time.format.DateTimeFormatter;

public class Tehtava extends Merkinta {

    private String muistiinpanot;

    public Tehtava() {}

    public Tehtava(String nimi) {
        super(nimi);
    }

    public void asetaMuistiinpano(String kuvaus) {
        this.muistiinpanot = kuvaus;
    }

    public String toStringLyhyt() {
        DateTimeFormatter formatter;
        formatter = DateTimeFormatter.ofPattern("HH:mm");

        String aika = this.ajankohta.format(formatter);

        return this.nimi + ", " + aika;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.Y HH:mm");

        String muistutus;
        if (this.muistutusAika != null) {
            muistutus = "Muistutetaan " + this.muistutusAika.format(formatter);
        } else {
            muistutus = "Ei muistutusta";
        }

        String muistiinpanot = this.muistiinpanot;
        if (muistiinpanot == null) {
            muistiinpanot = "-";
        }

        return this.nimi + "\n  Aika: " + this.ajankohta.format(formatter) + "\n  Muistiinpanot: " + muistiinpanot + "\n  Muistutus: " + muistutus;
    }
}