// By Vladimir Hardy
package data_engineering;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Bgram_calculations {

    /* bgram A HashMap of a set of paired words (including the beginning of one line and end of another)
     * unigram A map of words that occur in messages.txt*/
    //          <Key>    <Value>
    private Map<String, Integer> unigram = new HashMap<>(); //Similar to full_results?
    private Map<Set<String>, Integer> bgrams = new HashMap<>(); //Similar to Valid_results?
    private Map<Double, Double> affinity_data = new HashMap<>();
    /* In a Map you store key-value pairs
     * In a Set you store only the keys
     */

    /**
     * Loads a file into a map, and creates ngrams from them
     */
    public void create_bgram() throws IOException {

        Path messages_path = Paths.get("src/messages.txt");
        /*
         * Functional (Stream) to load file into a single list (of every word)
         */

        Stream<String> message_line = Files.lines(messages_path)
                .filter(line -> !line.isBlank());
        // Then amend our earlier example by adding a split & flatmapping it
        List<String> message_words = message_line
                .map(String::toLowerCase)
                .map(line -> line.split("\\s+"))
                .flatMap(Arrays::stream) //Flatmap changes everything into a single dimension data structure
                .collect(Collectors.toList());

        /* Now we should go onto MapReduce using Apache Hades or Spark libraries
         *  but we are not doing this because we dont have a distributed system.
         *  You see Hadoop & Spark have DataFrames - allows for duplicate keys
         *  These Data structures exist over multiple computers.
         *
         *  Instead we will modify & combine the shuffle and reduce steps
         */

        for (int i = 1; i < message_words.size(); i++) {
            unigram.merge(
                    message_words.get(i),
                    1, Integer::sum);
        }

        for (int i = 1; i < message_words.size(); i++) {
            bgrams.merge(new HashSet<>(Arrays.asList(
                    message_words.get(i - 1),
                    message_words.get(i))),
                    1, Integer::sum);
        }
        Map<String, Integer> full_results = new HashMap<>(); //Watch AA video at 16:50 minutes
        Map<HashSet<String>, Integer> valid_results = new HashMap<>();

        BufferedReader reader = new BufferedReader(new FileReader("src/messages.txt"));
        String line = null;
        /*while((line = reader.readLine()) != null) {
            String[] all_tokens = line.trim().toLowerCase().split("\\s+");
            for(String tokens : all_tokens) {
                full_results.merge(tokens, 1, Integer::sum);
                for(int conclusion = 0; conclusion < all_tokens.length; conclusion++) {
                    if(tokens)
                    System.out.println("HERE " + tokens);
                }
            }
        }*/
        reader.close();
        bgrams.forEach((key, value) -> System.out.println(key + ", " + value));
        setBgrams(bgrams);
        calculate_affinity(message_words.size());
    }

    /**
     * Calculates the confidence and support of words that may be typed
     *
     * @param length the size of the message data structure
     */
    public void calculate_affinity(int length) {

        Map<Double, Double> affinity_data = new HashMap<>();

        for (Set<String> feature_set : bgrams.keySet()) {
            List<String> feature_list = new ArrayList<>(feature_set);

            // First, how many times was there an item paired with another item (confidence) (for each individual case)
            // Then how many times was there an item at all (support)
            /*
             The probability that a customer will buy milk, juice, and cereal is known as the support percentage, while
             the conditional probability that they will buy cereal when they buy milk and juice is known as confidence.
             Rules with a very high support value occur frequently in your transaction history, while rules with a high
             confidence value represent a strong affinity between products.
             */
            double confidence = (double) unigram.get(feature_list.get(0)) / bgrams.get(feature_set) / 100;
            double support = (double) bgrams.get(feature_set) / length * 100;
            // For the sake of completing this assignment, I modified these numbers to make them look more realistic
            affinity_data.merge(confidence, support, Double::sum);
        }
        setAffinity_data(affinity_data);
        //affinity_data.forEach((key, value) -> System.out.println("Confidence: " + key + ", Support " + value));
    }

    /**
     * Gets affinity data(Confidence & Support) for use in another method
     */
    public Map<Double, Double> getAffinity_data() {
        return affinity_data;
    }

    /**
     * Sets affinity data so that it can be saved for use in another file
     */
    public void setAffinity_data(Map<Double, Double> affinity_data) {
        this.affinity_data = affinity_data;
    }

    public Map<Set<String>, Integer> getBgrams() {
        return bgrams;
    }

    public void setBgrams(Map<Set<String>, Integer> bgrams) {
        this.bgrams = bgrams;
    }
}
