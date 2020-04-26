// By Vladimir Hardy
package data_engineering;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {

    /**
     * Driver function that calls other classes
     * GitHub:
     */
    public static void main(String[] args) throws IOException {

        Bgram_calculations bgc = new Bgram_calculations();
        List_of_words low = new List_of_words();

        bgc.create_bgram();
        low.word_suggestion(bgc.getAffinity_data(), bgc.getBgrams());
    }
}
