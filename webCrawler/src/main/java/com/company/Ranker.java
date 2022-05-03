package com.company;

import org.bson.Document;

import java.io.IOException;
import java.util.*;

public class Ranker {
    public static DB db = new DB();
    public static List<Pair> urls;
    public static List<Pair> stemmedUrls;
    public static HashMap<String, Double> sortedMap = new HashMap<>();
    public static List<String> output = new ArrayList<String>();
    public Ranker(List<Pair> list, List<Pair> stemmed) {
        urls = list;
        stemmedUrls = stemmed;
    }

    public static void sortByValue(boolean order) {
        List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(sortedMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                if (order) {
                    return o1.getValue().compareTo(o2.getValue());
                } else {
                    return o2.getValue().compareTo(o1.getValue());
                }
            }
        });
        sortedMap = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
    }

    public static void rank(List<Pair> list, int stem) {
        for (Pair P : list) {
            for (Document object : (List<Document>) P.first) {
                String url = (String) object.get("url");
                long TotalWords = (long) db.getAttr("URLs", "url", url, "NumberOfWords");
                int TotalDocuments = (int) db.getAttr("Globals", "key", "counter", "value");
                int DF = (Integer) P.second;
                double IDF = Math.log((double) TotalDocuments / DF);
                int TF = (int) object.get("TF");
                double NormalizedTF = (double) TF / TotalWords;
                double TF_IDF = NormalizedTF * IDF;
                int weight = (int) object.get("weight");
                int popularity = (int) db.getAttr("URLs", "url", url, "popularity");
                double relevance = TF_IDF * weight;
                double Priority = (10 * relevance) + (2 * (double) popularity / TotalDocuments);
                System.out.println(url +" "+Priority);
                if (sortedMap.containsKey(url))
                    sortedMap.replace(url, Priority + sortedMap.get(url) + (5 * stem));
                else
                    sortedMap.put(url, Priority + stem);
            }
        }
        sortByValue(false);
    }

    // For testing
    public static void print() {
        for (Map.Entry<String, Double> entry : sortedMap.entrySet())
            System.out.println(entry.getKey()+" ---> "+ entry.getValue());
    }

    public static  List<String> getOutput() {
        return output;
    }

    public static void main(String[] args) throws IOException {
        //String query = WebInterface.getInput();
        queryProcessor myq = new queryProcessor("call");
        urls = myq.list;
        stemmedUrls = myq.stemmed;
        rank(urls, 2);
        rank(stemmedUrls, 1);
        print();
        for (Map.Entry<String, Double> entry : sortedMap.entrySet())
            output.add(entry.getKey());
        System.out.println(output);
    }
}
// query: Computer Engineering Cairo University
// Outer loop:
//    Iterates over the lists of each word and DF
//          Inner loop:
//              Iterates over each list and calculates priority
