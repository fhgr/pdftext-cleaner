package ch.htwchur.pdf.healer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import org.junit.jupiter.api.Test;
/**
 * 
 * @author sandro.hoerler@htwchur.ch
 *
 */
public class DocumentHandlerTest {

    private static final String EXP_HEADER_REMOVAL =
                    "Briefkastenfirmen sollen korrupten Unternehmern kein Versteck bieten. Deshalb fordert Transparency International öffentliche Register, den besseren Schutz von Whistle­blowern und die Verschärfung des schweizerischen Korruptionsstrafrechts. Ziel ist es, die Korrupten zu enttarnen, wie der Verein, der sich gegen Korruption engagiert, gestern mitgeteilt hat. In einem ersten Schritt fordert er das Parlament auf, die geplanten Geld­wäscherei-Regeln, die sogenannte Gafi-­Vorlage, nicht zu verwässern. Der Nationalrat hatte im Sommer die Empfehlungen der OECD-Expertengruppe zerzaust und die Vorlage des Bundesrates aufgeweicht. (SDA)";

    @Test
    public void testDocumentSplitting() {
        List<String> docs = DocumentHandler.splitDocuments(
                        "immer wieder Fälle, da weiss man genau, wie krumme Geschäfte abgelaufen sind. Trotzdem kann man sie nicht zur Anklage bringen, weil für eine Verurteilung – salopp ausgedrückt – zu wenig Fleisch am Knochen ist.»\n"
                                        + "\n" + "Dokument TANZ000020140903ea930001b\n" + "\n"
                                        + "\n" + "\n" + "\n" + "Schweiz\n" + "Korruption");
        assertEquals(2, docs.size());
    }

    @Test
    public void testHeaderRemoval() {
        String cleanedDoc = DocumentHandler.removeDocumentHeader("89 Wörter\n"
                        + "3 September 2014\n" + "Tages Anzeiger\n" + "TANZ\n" + "Deutsch\n"
                        + "(c) 2014 Tages Anzeiger Homepage Address:   http://www.tages-anzeiger.ch\n"
                        + "\n"
                        + EXP_HEADER_REMOVAL);
        System.out.println(cleanedDoc);
        assertEquals(EXP_HEADER_REMOVAL, cleanedDoc);
    }
    
    @Test
    public void testCopyrightSignHeaderRemoval() {
        String cleanedDoc = DocumentHandler.removeDocumentHeader("89 Wörter\n"
                        + "3 September 2014\n" + "Tages Anzeiger\n" + "TANZ\n" + "Deutsch\n"
                        + "© 2014 Tages Anzeiger Homepage Address:   http://www.tages-anzeiger.ch\n"
                        + "\n"
                        + EXP_HEADER_REMOVAL);
        System.out.println(cleanedDoc);
        assertEquals(EXP_HEADER_REMOVAL, cleanedDoc);
    }
}
