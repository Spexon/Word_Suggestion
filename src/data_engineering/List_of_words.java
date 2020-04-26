// By Vladimir Hardy
package data_engineering;

import org.w3c.dom.ls.LSOutput;

import java.util.*;

public class List_of_words {

    /**
     * Build a List of possible "next words" at least 3 words in length if the support of a word pair is > 65%.
     * If not, program lists the three most common english words.
     *
     * @param data_for_messaging A map of confidence & supports from a messages file
     */
    public void word_suggestion(Map<Double, Double> data_for_messaging, Map<Set<String>, Integer> bgram) {

        Scanner scan = new Scanner(System.in);
        System.out.println("Enter a word and we'll suggest something to add next... ");
        String userInput = scan.nextLine();

        ArrayList<String> compare_words = new ArrayList<>();
        for (Set<String> feature_set : bgram.keySet()) {
            compare_words.addAll(feature_set); // building an ArrayList that can easily compare words and indexes
        }

        // Comparing if a word exists in our array, and if it does create a word pair
        if (compare_words.contains(userInput) && userInput.length() >= 3) {
            int index_of_word = compare_words.indexOf(userInput);
            String wordpair = "";
            try {
                if (compare_words.size() % index_of_word == 2) {
                    wordpair = compare_words.get(index_of_word + 1);
                } else {
                    wordpair = compare_words.get(index_of_word - 1);
                }
            } catch (ArithmeticException ex) { // 0 is even in this case
                wordpair = compare_words.get(index_of_word + 1);
            }
            System.out.println(compare_words.get(index_of_word) + ", " + wordpair);

            // Compare the word's support and print possible pairs if support >= 65%
            ArrayList<Double> compare_numbers = new ArrayList<>(data_for_messaging.values());
            if (compare_numbers.get(index_of_word) >= .65) {
                System.out.println("We recommend typing: \n* " + wordpair);
            }
        } else {
            System.out.println("No pair found, here's some common word connectors:\n" + "* the\n* this\n* of");
        }
        scan.close();
    }
}
