package org.arthur.salesman.runner;

import org.arthur.salesman.comparator.Correlation;
import org.arthur.salesman.model.Citation;
import org.arthur.salesman.reader.CitationReader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Arthur Grava (arthur at luizalabs.com).
 */
public class SimilarityComparator {

    private int coreSize;
    private int maxSize;
    private boolean debug;

    /**
     * @param coreThreads   - minimum number of threads running
     * @param maxThreads    - maximum number of threads running
     * @param debug         - is it running in debug mode
     */
    public SimilarityComparator(int coreThreads, int maxThreads, boolean debug) {
        this.coreSize = coreThreads;
        this.maxSize = maxThreads;
        this.debug = debug;
    }

    /**
     * Execute all needed process to calculate the similarity among users
     *
     * @param filePath
     * @param targetPath
     * @param topK
     */
    public void execute(String filePath, String targetPath, int topK, int begin, int end) throws IOException {
        BufferedWriter br = null;
        try {
            System.out.printf(
                    "Starting to run program, parameters are: input \"%s\", target \"%s\", core %d, max %d, k %d, debug %s, begin %d, end %d\n",
                    filePath, targetPath, coreSize, maxSize, topK, (debug + ""), begin, end
            );

            Map<String, List<Citation>> citations = CitationReader.readFile(filePath);
            citations.size();
            ThreadPoolExecutor executor = new ThreadPoolExecutor(coreSize, maxSize, 1, TimeUnit.MILLISECONDS, new
                    ArrayBlockingQueue<Runnable>(1));

            br = new BufferedWriter(new FileWriter(targetPath));

            System.out.println("Comparison will definitely start now");

            Set<String> keys = citations.keySet();

            if (keys.size() > begin && keys.size() <= end) {
                int count = 0;
                for (String keyA : keys) {
                    if (count >= begin && count <= end) {
                        boolean running = false;
                        while (!running) {
                            try {
                                int n = executor.getMaximumPoolSize() - executor.getActiveCount();
                                if (n <= 1) {
                                    Thread.sleep(50);
                                    continue;
                                }
                                executor.execute(new Correlation(keyA, citations, topK, br, debug));
                                running = true;
                            } catch (RejectedExecutionException e) {
                                // do nothing
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    count++;
                }
            }

            while (executor.getActiveCount() > 1) {
                Thread.sleep(100);
            }

            executor.shutdown();
            executor.awaitTermination(100, TimeUnit.MILLISECONDS);

            while (!executor.isTerminated()) {
                Thread.sleep(100);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(br != null) {
                br.flush();
                br.close();
            }
            System.exit(0);
        }
    }
}
