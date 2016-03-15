package org.arthur.salesman.coauthors;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * For this class to work, it is really important that the file is a CSV with the first position of row being the author
 * and the second one being the article he published
 *
 * Created by tutu on 2/10/16.
 */
public class CoauthorsSeeker {

    private Map<String, Map<String, Integer>> coauthorship;
    private Map<String, Set<String>> articlesAuthors;
    private String path;
    private String separator;

    public CoauthorsSeeker(String path, int numberOfAuthors, int numberOfArticles, String separator) {
        this.coauthorship = new HashMap<>(numberOfAuthors);
        this.articlesAuthors = new HashMap<>(numberOfArticles);
        this.path = path;
        this.separator = separator;
    }

    public void createNewCoauthorshipList(String... authors) {
        for (String author : authors) {
            if (!this.coauthorship.containsKey(author)) {
                this.coauthorship.put(author, new HashMap<String, Integer>());
            }
        }
    }

    /*
    TODO - Dar um peso a mais quando os autores citaram outros artigos em conjunto, somar 1 por exemplo
     */
    private void addCoauthor(String a, String b) {
        Map<String, Integer> temp = this.coauthorship.get(a);

        int value = 1;
        if (temp.containsKey(b)) {
            value += temp.get(b);
        }
        temp.put(b, value);
    }

    private void addCoauthors(String a, String b) {
        createNewCoauthorshipList(a, b);
        addCoauthor(a, b);
        addCoauthor(b, a);
    }

    public void calculate(boolean isDebug) throws IOException {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(new File(this.path)));

            String line;
            long count = 0;
            while ((line = br.readLine()) != null) {
                String[] params = line.split(this.separator);

                String article = params[1].trim().toLowerCase().replace("\"", "");
                String[] authors = params[2].trim().toLowerCase().replace("\"", "").split(";");

                if (!article.isEmpty()) {
                    count++;
                    for (String ele : authors) {
                        String author = ele.trim();
                        if (!this.articlesAuthors.containsKey(article)) {
                            Set<String> init = new HashSet<>();
                            init.add(author);
                            this.articlesAuthors.put(article, init);
                        } else {
                            Set<String> authorsSet = this.articlesAuthors.get(article);

                            if (!authorsSet.contains(author)) {
                                for (String key : authorsSet) {
                                    addCoauthors(author, key);
                                }
                                authorsSet.add(author);
                            }
                        }
                    }
                    if (isDebug) {
                        System.out.println("(" + count + ") done: " + article);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    public Map<String, Map<String, Integer>> getCoauthorship() {
        return coauthorship;
    }

    public Map<String, Set<String>> getArticlesAuthors() {
        return articlesAuthors;
    }
}
