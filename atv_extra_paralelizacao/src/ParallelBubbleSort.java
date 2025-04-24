import java.util.concurrent.*;

public class ParallelBubbleSort {
    public static void sort(int[] arr, int numThreads) throws InterruptedException {
        int n = arr.length;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < n; i++) {
            int phase = i % 2;
            CountDownLatch latch = new CountDownLatch(numThreads);

            for (int t = 0; t < numThreads; t++) {
                int start = t * (n / numThreads);
                int end = (t == numThreads - 1) ? n - 1 : (t + 1) * (n / numThreads) - 1;

                final int finalStart = Math.max(start, phase);
                final int finalEnd = end;

                executor.execute(() -> {
                    for (int j = finalStart; j < finalEnd; j += 2) {
                        if (arr[j] > arr[j + 1]) {
                            int temp = arr[j];
                            arr[j] = arr[j + 1];
                            arr[j + 1] = temp;
                        }
                    }
                    latch.countDown();
                });
            }

            latch.await();
        }

        executor.shutdown();
    }
}
