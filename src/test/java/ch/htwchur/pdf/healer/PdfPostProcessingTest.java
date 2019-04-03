package ch.htwchur.pdf.healer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PdfPostProcessingTest {

    private static final String TEST_SPLITTED_WORD =
                    "ich bin pe-\n ter. Ich bin seit jahren \nam Lehr- \nstuhl in Darm-\nstadt. Nun bin ich wie- der da.";
    private static final String TEST_SPLITTED_WORD_EXP =
                    "ich bin peter. Ich bin seit jahren \nam Lehrstuhl in Darmstadt. Nun bin ich wieder da.";


    @Test
    public void mergeSplittedWordsTest() {
        String result = PdfPostProcessing.mergeSplittedWords(TEST_SPLITTED_WORD);
        assertEquals(TEST_SPLITTED_WORD_EXP, result);
    }

    private static final String TEST_INWORD_UPPERCASES =
                    "Ich bin Peter Maffai und lebe nun unter gleichgesinnten. NunJedoch bin ich nicht mehr. Ich bin ab sofort inÖsterreich tätig.";
    private static final String TEST_INWORD_UPPERCASES_EXP =
                    "Ich bin Peter Maffai und lebe nun unter gleichgesinnten. Nun Jedoch bin ich nicht mehr. Ich bin ab sofort in Österreich tätig.";

    @Test
    public void splittWordsWithInwordUppercaseLetters() {
        String result = PdfPostProcessing
                        .splitAccitentiallyMergedWordsWhenInwordUppercase(TEST_INWORD_UPPERCASES);
        assertEquals(TEST_INWORD_UPPERCASES_EXP, result);
    }

    private static final String MERGE_WHITESPACE_OF_WORDS_TEST = "G e s c h ä f t s  B e r i c h t";
    private static final String MERGE_WHITESPACE_OF_WORDS_TEST_2 =
                    "Peter  p a n  wohnt in  N e v e r l a n d. M i c h a e l  Jackson hat ebenfalls  g e b a u t.";
    private static final String MERGE_WHITESPACE_OF_WORDS_TEST_3 =
                    "M i C H a e L  janson ist  p E T e R.";
    private static final String MERGE_WHITESPACE_OF_WORDS_TEST_4 = "SH A R EHOLDER S’  LET T ER";

    private static final String EXP_MERGE_WHITESPACE_OF_WORDS_TEST = "Geschäfts Bericht";
    private static final String EXP_MERGE_WHITESPACE_OF_WORDS_TEST_2 =
                    "Peter pan wohnt in Neverland. Michael Jackson hat ebenfalls gebaut.";
    private static final String EXP_MERGE_WHITESPACE_OF_WORDS_TEST_3 = "Michael janson ist peter.";
    private static final String EXP_MERGE_WHITESPACE_OF_WORDS_TEST_4 = "Shareholder S’ Letter";

    @Test
    public void mergeWhitespacesLettersOfAWordTest() {
        String result = PdfPostProcessing
                        .mergeWhitespacesLettersOfAWord(MERGE_WHITESPACE_OF_WORDS_TEST);
        assertEquals(EXP_MERGE_WHITESPACE_OF_WORDS_TEST, result);
        result = PdfPostProcessing.mergeWhitespacesLettersOfAWord(MERGE_WHITESPACE_OF_WORDS_TEST_2);
        assertEquals(EXP_MERGE_WHITESPACE_OF_WORDS_TEST_2, result);
        result = PdfPostProcessing.mergeWhitespacesLettersOfAWord(MERGE_WHITESPACE_OF_WORDS_TEST_3);
        assertEquals(EXP_MERGE_WHITESPACE_OF_WORDS_TEST_3, result);
        result = PdfPostProcessing.mergeWhitespacesLettersOfAWord(MERGE_WHITESPACE_OF_WORDS_TEST_4);
        assertEquals(EXP_MERGE_WHITESPACE_OF_WORDS_TEST_4, result);
    }

    @Test
    public void mergeNewlinesIfNoPunctationOccursTest() {
        String result = PdfPostProcessing.mergeNewlinesIfNoPunctationOccurs(
                        "Ich bin peter. Ich habe eine linie. ich\n" + "die ich. nicht machen will\n"
                                        + "daher bin ich so.\n" + "Nanana do.");
        log.info(result);
        assertEquals("Ich bin peter. Ich habe eine linie. ich die ich. nicht machen will daher bin ich so.\n"
                        + "Nanana do.", result);
    }
}
