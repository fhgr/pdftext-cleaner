package ch.htwchur.document.embeddings;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Holds
 * 
 * @author sandro
 *
 */
public class StopWords {
    // https://www.ranks.nl/stopwords/german
    public final static HashSet<String> GERMAN_STOP_WORDS = new HashSet<String>(Arrays.asList(
                    new String[] {"and", "the", "of", "to", "einer", "eine", "eines", "einem",
                                    "einen", "der", "die", "das", "dass", "daß", "du", "er", "sie",
                                    "es", "was", "wer", "wie", "", "und", "oder", "ohne", "mit",
                                    "am", "im", "in", "aus", "auf", "ist", "sein", "war", "wird",
                                    "ihr", "ihre", "ihres", "ihnen", "ihrer", "als", "für", "von",
                                    "mit", "dich", "dir", "mich", "mir", "mein", "sein", "durch",
                                    "wegen", "wird", "sich", "bei", "beim", "noch", "den", "dem",
                                    "zu", "zur", "zum", "auf", "ein", "auch", "werden", "an", "des",
                                    "sein", "sind", "vor", "nicht", "sehr", "um", "unsere", "ohne",
                                    "so", "da", "nur", "diese", "dieser", "diesem", "dieses",
                                    "nach", "über", "mehr", "hat", "bis", "uns", "unser", "unserer",
                                    "unserem", "unsers", "euch", "euers", "euer", "eurem", "ihr",
                                    "ihres", "ihrer", "ihrem", "alle", "vom"}));

    // https://www.ranks.nl/stopwords/english
    public final static HashSet<String> ENGLISH_STOP_WORDS =
                    new HashSet<String>(Arrays.asList(new String[] {"i", "me", "my", "myself", "we",
                                    "our", "ours", "ourselves", "you", "your", "yours", "yourself",
                                    "yourselves", "he", "him", "his", "himself", "she", "her",
                                    "hers", "herself", "it", "its", "itself", "they", "them",
                                    "their", "theirs", "themselves", "what", "which", "who", "whom",
                                    "this", "that", "these", "those", "am", "is", "are", "was",
                                    "were", "be", "been", "being", "have", "has", "had", "having",
                                    "do", "does", "did", "doing", "a", "an", "the", "and", "but",
                                    "if", "or", "because", "as", "until", "while", "of", "at", "by",
                                    "for", "with", "about", "against", "between", "into", "through",
                                    "during", "before", "after", "above", "below", "to", "from",
                                    "up", "down", "in", "out", "on", "off", "over", "under",
                                    "again", "further", "then", "once", "here", "there", "when",
                                    "where", "why", "how", "all", "any", "both", "each", "few",
                                    "more", "most", "other", "some", "such", "no", "nor", "not",
                                    "only", "own", "same", "so", "than", "too", "very", "s", "t",
                                    "can", "will", "just", "don", "should", "now"}));
}
