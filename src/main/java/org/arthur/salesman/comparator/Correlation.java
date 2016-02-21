package org.arthur.salesman.comparator;

import org.arthur.salesman.model.Citation;
import org.arthur.salesman.model.Similarity;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * @author Arthur Grava (arthur at luizalabs.com).
 */
public class Correlation implements Runnable {

    private final Map<String, List<Citation>> citations;
    private final String authorId;
    private final PriorityQueue<Similarity> topK;
    private final int k;
    private final BufferedWriter writer;
    private final boolean debug;

    public Correlation(String authorId, Map<String, List<Citation>> citations, int k, BufferedWriter writer, boolean db) {
        this.citations = citations;
        this.authorId = authorId;
        this.k = k;
        this.topK = new PriorityQueue<>(k);
        this.writer = writer;
        this.debug = db;
    }

    private void execute() {
        List<Citation> current = this.citations.get(authorId);
        for (String key : citations.keySet()) {
            if (!authorId.equals(key)) {
                double score = Pearson.compare(current, citations.get(key));
                addToTopK(key, score);
            }
        }
        writeResultOnFile();
    }

    private void writeResultOnFile() {
        List<Similarity> result = new ArrayList<>(this.topK);
        Collections.sort(result);
        Collections.reverse(result);
        if (this.writer != null) {
            try {
                for (Similarity similar : result) {
                    if (this.debug) {
                        System.out.println(similar.toCsv());
                    }
                    this.writer.write(similar.toCsv());
                    this.writer.newLine();
                }
                this.writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(result);
        }
    }

    private void addToTopK(String key, double score) {
        if (this.topK.size() >= this.k) {
            Similarity lower = this.topK.peek();
            if (lower.getScore() >= score) {
                return;
            }
            this.topK.remove();
        }

        Similarity temporary = new Similarity(this.authorId, key, score);
        this.topK.add(temporary);
    }

    @Override
    public void run() {
        execute();
    }

    public Queue<Similarity> getTopK() {
        return this.topK;
    }
}
