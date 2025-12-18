/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api_robot7links_test.wrk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author AudergonV01
 */
public class WrkIO {

    private final GsonBuilder builder;
    private final Gson g;

    public WrkIO() {
        builder = new GsonBuilder();
        g = builder.create();
    }

    public boolean objToJson(Serializable o, String path) {
        boolean ok = false;
        String json = toGson(o);
        if (json != null) {
            ArrayList<String> lignes = new ArrayList<>();
            lignes.add(json);
            ok = writeTextFile(path, lignes);
        }
        return ok;
    }

    public Object jsonToObj(String path, Class c) {
        Object o = null;
        ArrayList<String> lignes = readTextFile(path);
        if (lignes != null && !lignes.isEmpty()) {
            String json = lignes.get(0);
            o = toObject(json, c);
        }
        return o;
    }

    /**
     * Lit et retourne l'ensemble des lignes présentes dans le fichier texte
     * spécifié. Cette méthode s'assure qu'aucune ligne ne soit retournée en cas
     * de problème(s) lors de la lecture, de manière à ce qu'on puisse s'en
     * rendre compte.
     *
     * @param filepath le chemin complet du fichier texte à lire
     *
     * @return l'ensemble des lignes du fichier texte, ou null en cas de
     * problème(s) rencontré(s)
     */
    public ArrayList<String> readTextFile(String filepath) {
        ArrayList<String> resultat = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(filepath), "UTF-8"));

            resultat = new ArrayList<>();

            String ligne;
            while ((ligne = br.readLine()) != null) {
                resultat.add(ligne);
            }

            br.close();
            br = null;
        } catch (IOException e) {
            resultat = null;
        } finally {
            // Toujours fermer le fichier si pas déjà fait !
            if (br != null) {
                try {
                    br.close();
                    br = null;
                } catch (IOException ioe2) {
                    // On peut l'ignorer, le cas est déjà traité
                }
            }
        }
        return resultat;
    }

    /**
     * Sauvegarde l'ensemble des chaînes de caractères passées en argument dans
     * le fichier texte spécifié. Cette méthode s'assure que le fichier texte
     * produit soit complet. En cas de problème(s), il sera supprimé.
     *
     * @param filepath le chemin complet sur le fichier texte à utiliser
     * @param linesToWrite l'ensemble des chaînes de caractères à écrire dans le
     * fichier texte
     *
     * @return vrai si et seulement si l'intégralité de la sauvegarde s'est
     * correctement effectuée
     */
    private boolean writeTextFile(String filepath, ArrayList<String> linesToWrite) {
        boolean resultat = false;
        if (linesToWrite != null) {    // S'il n'y a rien à faire on ne fait rien (pas même effacer le fichier) !
            BufferedWriter bw = null;
            try {
                // L'ancien fichier sera remplacé par le nouveau contenu, même si vide
                bw = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(filepath, false), "UTF-8"));
                for (String ligne : linesToWrite) {
                    if (ligne != null) {
                        bw.write(ligne);
                        bw.newLine();
                    }
                }
                bw.flush();
                bw.close();
                bw = null;
                resultat = true;    // Si on est ici c'est que tout roule !
            } catch (Exception e) {
            } finally {
                // Toujours fermer le fichier si pas déjà fait !
                if (bw != null) {
                    try {
                        bw.close();
                        bw = null;
                    } catch (IOException ioe2) {
                        // On peut l'ignorer, le cas est déjà traité
                    }
                }
                // Si l'écriture a échoué d'une façon où d'une autre, ne pas laisser un fichier incomplet
                if (!resultat) {
                    try {
                        new File(filepath).delete();
                    } catch (Exception e) {
                    }
                }
            }
        }
        return resultat;
    }

    /**
     * Transforme un object en json (JavaScript Object Notation), sous forme de
     * chaîne de caractères.
     *
     * @param o L'objet à transormer
     * @return La chaîne de caractère contenant le json
     */
    public String toGson(Object o) {
        return g.toJson(o);
    }

    /**
     * Transforme une chaîne de caractères contenant un json en Objet Java. En
     * cas d'erreur, ou si le json est invalide, rend null.
     *
     * @param json La chaîne de caractères contenant le json
     * @param cl La classe de l'objet
     * @return L'objet issu du json
     */
    public Object toObject(String json, Class cl) {
        Object obj = null;
        try {
            obj = g.fromJson(json, cl);
        } catch (JsonSyntaxException ex) {
            System.err.println("Erreur lors de la conversion du gson en objet : " + ex.getMessage());
        }
        return obj;
    }

}
