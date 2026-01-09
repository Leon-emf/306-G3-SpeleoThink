/**
 * Classe de lancement pour l'application JavaFX.
 * Cette classe séparée est nécessaire car JavaFX 11+ nécessite
 * que la classe principale n'étende pas Application directement
 * quand JavaFX n'est pas dans le module path au démarrage.
 */
public class Launcher {
    
    public static void main(String[] args) {
        API_Robot7Links_Test.main(args);
    }
}
