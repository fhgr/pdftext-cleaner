package ch.htwchur.document.portal;

public class FilenameDateExtractor {

    /**
     * Simple month converter
     * 
     * @param month
     * @return month digit as string
     */
    private static String monthConverter(String month) {
        if (month.toLowerCase().startsWith("ja")) {
            return "01";
        }
        if (month.toLowerCase().startsWith("f")) {
            return "02";
        }
        if (month.toLowerCase().startsWith("mar")) {
            return "03";
        }
        if (month.toLowerCase().startsWith("ap")) {
            return "04";
        }
        if (month.toLowerCase().startsWith("ma")) {
            return "05";
        }
        if (month.toLowerCase().startsWith("jun")) {
            return "06";
        }
        if (month.toLowerCase().startsWith("jul")) {
            return "07";
        }
        if (month.toLowerCase().startsWith("aug")) {
            return "08";
        }
        if (month.toLowerCase().startsWith("se")) {
            return "09";
        }
        if (month.toLowerCase().startsWith("o")) {
            return "10";
        }
        if (month.toLowerCase().startsWith("n")) {
            return "11";
        }
        return "12";
    }

    /**
     * Convert day string to digit string
     * 
     * @param day
     * @return
     */
    private static String dayConverter(String day) {
        if (day.length() == 1) {
            return "0" + day;
        }
        return day;
    }

    /**
     * Extract date from filename
     * 
     * @param date
     * @return
     */
    public static String extractDateFromFilename(String date) {
        String datePart = date.split("_")[0];
        String[] dateParts = datePart.split(" ");
        String day = dateParts[0];
        String month = dateParts[1];
        String year = dateParts[2];
        return year + "-" + monthConverter(month) + "-" + dayConverter(day) + "T12:00:00.335472";
    }
}
