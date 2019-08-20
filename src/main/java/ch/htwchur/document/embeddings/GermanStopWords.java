package ch.htwchur.document.embeddings;

import java.util.Arrays;
import java.util.HashSet;

public class GermanStopWords {

    public final static HashSet<String> GERMAN_STOP_WORDS = new HashSet<String>(Arrays.asList(
                    new String[] {"and", "the", "of", "to", "einer", "eine", "eines", "einem",
                                    "einen", "der", "die", "das", "dass", "daß", "du", "er", "sie",
                                    "es", "was", "wer", "wie", "wir", "und", "oder", "ohne", "mit",
                                    "am", "im", "in", "aus", "auf", "ist", "sein", "war", "wird",
                                    "ihr", "ihre", "ihres", "ihnen", "ihrer", "als", "für", "von",
                                    "mit", "dich", "dir", "mich", "mir", "mein", "sein", "kein",
                                    "durch", "wegen", "wird", "sich", "bei", "beim", "noch", "den",
                                    "dem", "zu", "zur", "zum", "auf", "ein", "auch", "werden", "an",
                                    "des", "sein", "sind", "vor", "nicht", "sehr", "um", "unsere",
                                    "ohne", "so", "da", "nur", "diese", "dieser", "diesem",
                                    "dieses", "nach", "über", "mehr", "hat", "bis", "uns", "unser",
                                    "unserer", "unserem", "unsers", "euch", "euers", "euer",
                                    "eurem", "ihr", "ihres", "ihrer", "ihrem", "alle", "vom"}));
}
