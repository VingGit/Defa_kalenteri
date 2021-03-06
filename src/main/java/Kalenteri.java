import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Kalenteri {

    private LocalDate pvm;
    private HashMap juhlat;
    private ArrayList<Tapahtuma> tapahtumat;
    private ArrayList<Tehtava> tehtavat;
    private ArrayList<Merkinta> muistutukset;
    // Seuraavat kaksi vuosinäkymän tulostusta varten.
    public static int eteenTaakseKuukausia;
    private LocalDate pvm2;

    public Kalenteri() {
        this.pvm = LocalDate.now();
        this.juhlat = new HashMap<LocalDate, String>();
        Juhlapyhat.asetaJuhlat(juhlat, pvm.getYear());

        // Yritetään hakea tallennettuja tapahtumia tiedostostosta TapahtumaData. Jos tiedostoa ei ole, luodaa uusi tyhjä lista. TapahtumaData tiedosto luodaan kun ohjelma suljetaan.
        try {
            FileInputStream fis = new FileInputStream("TapahtumaData");
            ObjectInputStream ois = new ObjectInputStream(fis);

            this.tapahtumat = (ArrayList) ois.readObject();

            ois.close();
            fis.close();

        } catch (Exception e) {
            this.tapahtumat = new ArrayList<>();
        }

        // Yritetään hakea tallennettuja tehtäviä tiedostostosta TehtavaData. Jos tiedostoa ei ole, luodaa uusi tyhjä lista. TehtavaData tiedosto luodaan kun ohjelma suljetaan.
        try {
            FileInputStream fis = new FileInputStream("TehtavaData");
            ObjectInputStream ois = new ObjectInputStream(fis);

            this.tehtavat = (ArrayList) ois.readObject();

            ois.close();
            fis.close();

        } catch (Exception e) {
            this.tehtavat = new ArrayList<>();
        }

        // Yritetään hakea tallennettuja muistutuksi tiedostostosta MuistutusData. Jos tiedostoa ei ole, luodaa uusi tyhjä lista. MuistutusData tiedosto luodaan kun ohjelma suljetaan.
        try {
            FileInputStream fis = new FileInputStream("MuistutusData");
            ObjectInputStream ois = new ObjectInputStream(fis);

            this.muistutukset = (ArrayList) ois.readObject();

            for (Merkinta m : this.muistutukset) {

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = m.muistutusAika.format(formatter);
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(formattedDateTime);

                Timer timer = new Timer();
                timer.schedule(m, date);
            }

            ois.close();
            fis.close();

        } catch (Exception e) {
            this.muistutukset = new ArrayList<>();
        }

        this.pvm2 = LocalDate.now();
        eteenTaakseKuukausia = pvm2.getMonthValue();
    }

    /**___________________TAPAHTUMIIN/TEHTÄVIIN/JUHLIIN LIITTYVÄT METODIT______________________________________
     * Näillä metodeilla voidaan käsitellä kalenterin listoja.
     */

    public LocalDate annaPvm() {
        return this.pvm;
    }

    public void lisaaTapahtuma(Tapahtuma tapahtuma) {
        this.tapahtumat.add(tapahtuma);
    }

    public void lisaaTehtava(Tehtava tehtava) {
        this.tehtavat.add(tehtava);
    }

    public void lisaaMuistutus(Merkinta merkinta) {
        this.muistutukset.add(merkinta);
    }
    
    public boolean poistaTapahtuma(String poistettavanNimi) {
        return this.tapahtumat.removeIf(i -> poistettavanNimi.equals(i.annaNimi()));
    }

    public boolean poistaTehtava(String poistettavanNimi) {
        return this.tehtavat.removeIf(i -> this.pvm.isEqual(i.annaAjankohta().toLocalDate()) && poistettavanNimi.equals(i.annaNimi()));
    }

    public ArrayList<Tapahtuma> annaTapahtumaLista() {
        return this.tapahtumat;
    }

    public ArrayList<Tehtava> annaTehtavaLista() {
        return this.tehtavat;
    }

    public ArrayList<Merkinta> annaMuistutusLista() {
        return this.muistutukset;
    }

    public boolean poistaPaivanTapahtumat() {
        return this.tapahtumat.removeIf(i ->
                (this.pvm.isAfter(i.annaAjankohta().toLocalDate())  &&  (this.pvm.isBefore(i.annaLopetus().toLocalDate()))  ||
                        this.pvm.isEqual(i.annaAjankohta().toLocalDate())  ||
                        this.pvm.isEqual(i.annaLopetus().toLocalDate())));
    }

    public boolean poistaPaivanTehtavat() {
        return this.tehtavat.removeIf(i -> this.pvm.isEqual(i.annaAjankohta().toLocalDate()));
    }

    public boolean onkoTapahtumia() {
        for (Tapahtuma t : this.tapahtumat) {
            if ( this.pvm.isAfter(t.annaAjankohta().toLocalDate())  &&  (this.pvm.isBefore(t.annaLopetus().toLocalDate()))  ||
                 this.pvm.isEqual(t.annaAjankohta().toLocalDate())  ||
                 this.pvm.isEqual(t.annaLopetus().toLocalDate()))  {
                 return true;
            }
        }
        return false;
    }

    public boolean onkoTehtavia() {
        for (Tehtava t : this.tehtavat) {
            if (this.pvm.isEqual(t.annaAjankohta().toLocalDate())) {
                return true;
            }
        }
        return false;
    }

    public boolean onkoJuhla() {
        Iterator it = juhlat.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if ( this.pvm.equals(pair.getKey()) ) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Tapahtuma> annaKuukaudenTapahtumat() {
        ArrayList<Tapahtuma> kuukaudenTapahtumat = new ArrayList<>();
        for (Tapahtuma t : this.tapahtumat) {
            if (t.annaAjankohta().getMonthValue() == this.pvm.getMonthValue() && t.annaAjankohta().getYear() == this.pvm.getYear()) {
                kuukaudenTapahtumat.add(t);
            }
        }
        return kuukaudenTapahtumat;
    }

    public ArrayList<Tehtava> annaKuukaudenTehtavat() {
        ArrayList<Tehtava> kuukaudenTehtavat = new ArrayList<>();
        for (Tehtava t : this.tehtavat) {

            if (t.annaAjankohta().getMonthValue() == this.pvm.getMonthValue() && t.annaAjankohta().getYear() == this.pvm.getYear()) {
                kuukaudenTehtavat.add(t);
            }
        }
        return kuukaudenTehtavat;
    }

    /**_______________________LIIKKUMISMETODIT___________________________________
     * Metodit muuttavat päivämäärää, sekä osa tarkistaa muuttuiko vuosi.
     * Jos vuosi mmuuttui, asetetaan juhlapäivät uudelleen.
     */

    public void liikuVasemmalle() {
        if (this.pvm.getDayOfMonth() == 1) {
            this.pvm = this.pvm.plusDays(7 - this.pvm.getDayOfWeek().getValue());
            return;
        }

        if (this.pvm.getDayOfWeek().getValue() == 1) {
            if (this.pvm.getDayOfMonth() + 6 > this.pvm.lengthOfMonth()) {
                this.pvm = this.pvm.plusDays((this.pvm.lengthOfMonth() - this.pvm.getDayOfMonth()));
            } else {
                this.pvm = this.pvm.plusDays(6);
            }
            return;
        }

        this.pvm = this.pvm.minusDays(1);
    }
    
    public void liikuOikealle() {
        if (this.pvm.getDayOfMonth() == this.pvm.lengthOfMonth()) {
            this.pvm = this.pvm.minusDays(this.pvm.getDayOfWeek().getValue() - 1);
            return;
        }

        if (this.pvm.getDayOfWeek().getValue() == 7) {
            if (this.pvm.getDayOfMonth() < 7) {
                this.pvm = this.pvm.minusDays(this.pvm.getDayOfMonth() - 1);
            } else {
                this.pvm = this.pvm.minusDays(6);
            }
            return;
        }

        this.pvm = this.pvm.plusDays(1);
    }

    public void liikuYlos() {
        if (this.pvm.getDayOfMonth() <= 7) {
            if (this.pvm.lengthOfMonth() == 31 && this.pvm.getDayOfMonth() <= 3 ||
                this.pvm.lengthOfMonth() == 30 && this.pvm.getDayOfMonth() <= 2 ||
                this.pvm.lengthOfMonth() == 29 && this.pvm.getDayOfMonth() == 1) {
                this.pvm = this.pvm.plusDays(28);
                return;
            }

            this.pvm = this.pvm.plusDays(21);
            return;
        }

        this.pvm = this.pvm.minusDays(7);
    }

    public void liikuAlas() {
        if (this.pvm.lengthOfMonth() == 31 && this.pvm.getDayOfMonth() >= 25) {
            if (this.pvm.getDayOfMonth() >= 29) {
                this.pvm = this.pvm.minusDays(28);
                return;
            }

            this.pvm = this.pvm.minusDays(21);
            return;
        }

        if (this.pvm.lengthOfMonth() == 30 && this.pvm.getDayOfMonth() >= 24) {
            if (this.pvm.getDayOfMonth() >= 29) {
                this.pvm = this.pvm.minusDays(28);
                return;
            }

            this.pvm = this.pvm.minusDays(21);
            return;
        }

        if (this.pvm.lengthOfMonth() == 29 && this.pvm.getDayOfMonth() >= 23) {
            if (this.pvm.getDayOfMonth() >= 29) {
                this.pvm = this.pvm.minusDays(28);
                return;
            }

            this.pvm = this.pvm.minusDays(21);
            return;
        }

        this.pvm = this.pvm.plusDays(7);
    }
    
    public void seuraavaKuukausi() {
        int vuosiTallenna = this.pvm.getYear();
        this.pvm = this.pvm.plusMonths(1);
        if (vuosiTallenna != this.pvm.getYear()) {
            this.juhlat.clear();
            Juhlapyhat.asetaJuhlat(this.juhlat, this.pvm.getYear());
        }
        if (eteenTaakseKuukausia < 13){
            eteenTaakseKuukausia++;
        }
        if (eteenTaakseKuukausia == 13){
            eteenTaakseKuukausia = 1;
        }
    }
    
    public void edellinenKuukausi() {
        int vuosiTallenna = this.pvm.getYear();
        this.pvm = this.pvm.minusMonths(1);
        if (vuosiTallenna != this.pvm.getYear()) {
            this.juhlat.clear();
            Juhlapyhat.asetaJuhlat(this.juhlat, this.pvm.getYear());
        }
        if(eteenTaakseKuukausia > 0){
            eteenTaakseKuukausia--;
        }
        if(eteenTaakseKuukausia == 0){

            eteenTaakseKuukausia = 12;
        }
    }

    public void seuraavaVuosi() {
        this.pvm = this.pvm.plusYears(1);
        this.juhlat.clear();
        Juhlapyhat.asetaJuhlat(this.juhlat, this.pvm.getYear());
    }
    public void edellinenVuosi() {

        this.pvm = this.pvm.minusYears(1);
        this.juhlat.clear();
        Juhlapyhat.asetaJuhlat(this.juhlat, this.pvm.getYear());
    }

    public void asetaPaivamaaraa(int paiva, int kuukausi, int vuosi) {
        int vuosiTallenna = this.pvm.getYear();
        this.pvm = LocalDate.of(vuosi, kuukausi, paiva);
        if(vuosiTallenna != this.pvm.getYear()) {
            this.juhlat.clear();
            Juhlapyhat.asetaJuhlat(this.juhlat, this.pvm.getYear());
        }
    }

    public void asetaPaivamaaraa(LocalDate pvm) {
        int vuosiTallenna = this.pvm.getYear();
        this.pvm = pvm;
        if(vuosiTallenna != this.pvm.getYear()) {
            this.juhlat.clear();
            Juhlapyhat.asetaJuhlat(this.juhlat, this.pvm.getYear());
        }
    }

    public void nykyinenPaiva() {
        int vuosiTallenna = this.pvm.getYear();
        this.pvm = LocalDate.now();
        if (vuosiTallenna != this.pvm.getYear()) {
            this.juhlat.clear();
            Juhlapyhat.asetaJuhlat(this.juhlat, this.pvm.getYear());
        }
    }

    /**_________________________________TULOSTUSMETODIT__________________________________________________
     */

    public void tulostaTervehdysJaKello() {
        LocalDate pvm = LocalDate.now();
        String aika = LocalTime.now().truncatedTo(ChronoUnit.MINUTES).toString();

        // Koneen käyttäjä merkkijonoksi, muutetaan ensimmäinen kirjain isoksi.
        String kayttaja = System.getProperty("user.name");
        char nimenEkaKirjain = Character.toUpperCase(kayttaja.charAt(0));
        String kayttajaEkaIso = nimenEkaKirjain + kayttaja.substring(1);

        // Tervehditään käyttäjää, kerrotaan viikonpäivä, päiväys sekä kellonaika.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.y");
        System.out.println("             Hei " + kayttajaEkaIso + "!");
        System.out.println("     T\u00E4n\u00E4\u00E4n on " + Viikonpaivat.annaViikonpaiva(pvm.getDayOfWeek().getValue()) + " " + pvm.format(formatter));
        System.out.println("               " + aika);
    }

    public void tulostaVuosiNakyma() {
        KalenteriNakyma.tulostaVuosi(this.pvm.getDayOfMonth(), this.pvm.getMonthValue(), this.pvm.getYear(), this.pvm.getDayOfWeek(), this.juhlat, this.tapahtumat, this.tehtavat);
    }

    public void tulostaKuukausiNakyma() {
        KalenteriNakyma.tulostaKuukausi( this.pvm.getDayOfMonth(), this.pvm.getMonthValue(), this.pvm.getYear(), this.pvm.getDayOfWeek(), this.juhlat, this.tapahtumat, this.tehtavat,true) ;
    }

    public void tulostaPaivaNakyma() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.y");
        System.out.println();
        System.out.println("                " + this.pvm.format(formatter));
        System.out.print("            ");
        tulostaJuhlapaiva();
        System.out.println();

        System.out.println("  ------------ Tapahtumat ----------- ");

        if (!onkoTapahtumia()) {
            System.out.println("    Ei tapahtumia");
            System.out.println();
        }

        if (onkoTapahtumia()) {
            for (Tapahtuma t : this.tapahtumat) {
                if (this.pvm.isAfter(t.annaAjankohta().toLocalDate())  &&  (this.pvm.isBefore(t.annaLopetus().toLocalDate()))  ||
                        this.pvm.isEqual(t.annaAjankohta().toLocalDate())  ||
                        this.pvm.isEqual(t.annaLopetus().toLocalDate()))  {

                    System.out.print(Varit.GREEN);
                    System.out.println("  " + t.toString());
                    System.out.print(Varit.RESET);
                    System.out.println();
                }
            }
        }

        System.out.println("  ------------ Teht\u00E4v\u00E4t ------------ ");

        if (!onkoTehtavia()) {
            System.out.println("    Ei teht\u00E4vi\u00E4");
            System.out.println();
        }

        if (onkoTehtavia()) {
            for (Tehtava t : this.tehtavat) {
                if (this.pvm.isEqual(t.annaAjankohta().toLocalDate())) {
                    System.out.print(Varit.GREEN);
                    System.out.println("  " + t.toString());
                    System.out.print(Varit.RESET);
                    System.out.println();
                }
            }
        }
    }

    public void tulostaJuhlapaiva() {
        LocalDate today = LocalDate.of(this.pvm.getYear(), this.pvm.getMonth(), this.pvm.getDayOfMonth());
        Iterator it = juhlat.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if ( today.equals(pair.getKey()) ) {
                System.out.print(Varit.RED);
                System.out.println("   " + pair.getValue());
                System.out.print(Varit.RESET);
            }
        }
    }

    public void tulostaMerkinnat() {
        System.out.println("  ------ p\u00E4iv\u00E4n merkinn\u00E4t ------");

        tulostaJuhlapaiva();

        if (!onkoTapahtumia() && !onkoTehtavia() && !onkoJuhla()) {
            System.out.println("   Ei merkint\u00F6j\u00E4");
            return;
        }

        for (Tapahtuma t : this.tapahtumat) {
            if (this.pvm.isAfter(t.annaAjankohta().toLocalDate())  &&  (this.pvm.isBefore(t.annaLopetus().toLocalDate()))  ||
                this.pvm.isEqual(t.annaAjankohta().toLocalDate())  ||
                this.pvm.isEqual(t.annaLopetus().toLocalDate()))  {

                System.out.print(Varit.GREEN);
                System.out.println("   " + t.toStringLyhyt());
                System.out.print(Varit.RESET);
            }
        }

        for (Tehtava t : this.tehtavat) {
            if (this.pvm.isEqual(t.annaAjankohta().toLocalDate())) {
                System.out.print(Varit.GREEN);
                System.out.println("   " + t.toStringLyhyt());
                System.out.print(Varit.RESET);
            }
        }
    }

    public void tulostaMuistutukset() {
        System.out.println("  --- " + Kuukaudet.annaKuukausi(this.pvm.getMonthValue()) + "n muistutukset ---");

        if (annaKuukaudenTapahtumat().isEmpty() && annaKuukaudenTehtavat().isEmpty()) {
            System.out.println("   Ei muistettavaa, chill :)");
            return;
        }

        boolean onkoMuistutusPaalla = false;
        for (Tapahtuma t : annaKuukaudenTapahtumat()) {
            if (t.onkoMuistutus()) {
                System.out.println("   " + t.annaNimi() + ", " + t.annaAjankohta().getDayOfMonth() + ". p\u00E4iv\u00E4");
                onkoMuistutusPaalla = true;
            }
        }

        for (Tehtava t : annaKuukaudenTehtavat()) {
            t.toString();
            if (t.onkoMuistutus()) {
                System.out.println("   " + t.annaNimi() + ", " + t.annaAjankohta().getDayOfMonth() + ". p\u00E4iv\u00E4");
                onkoMuistutusPaalla = true;
            }
        }

        if (!onkoMuistutusPaalla) {
            System.out.println("   Ei muistettavaa, chill :)");
        }
    }
}