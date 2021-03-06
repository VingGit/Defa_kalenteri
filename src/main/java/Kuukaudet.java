public abstract class Kuukaudet {
    
    private static final String[] KUUKAUDET = {"", "tammikuu", "helmikuu", "maaliskuu", "huhtikuu", "toukokuu", "kes\u00E4kuu", "hein\u00E4kuu", "elokuu", "syyskuu", "lokakuu", "marraskuu", "joulukuu"};
    private static final int[] KUUKAUSINUMEROT = {0,1,2,3,4,5,6,7,8,9,10,11,12};
    // huom! helmikuuhun lisättävä päivä lisää jos on karkausvuosi!
    private static final int[] PAIVIEN_LKM = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    
    public static String annaKuukausi(int kk) {
        return KUUKAUDET[kk];
    }
    public static int annaKuukausiNumero(int kk) {
        return KUUKAUSINUMEROT[kk];
    }
    public static int annaPaiviaKuukaudessaLkm(int kk) {
        return PAIVIEN_LKM[kk];
    }
}
