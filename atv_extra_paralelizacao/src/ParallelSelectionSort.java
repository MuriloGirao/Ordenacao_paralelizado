class ParallelSelectionSort {
    public static void sort(int[] arr, int threads) {
        if (threads <= 1 || arr.length <= 1000) {
            SequentialSelectionSort.sort(arr);
        } else {
            int chunkSize = arr.length / threads;
            Thread[] threadArray = new Thread[threads];

            for (int t = 0; t < threads; t++) {
                int start = t * chunkSize;
                int end = (t == threads - 1) ? arr.length : start + chunkSize;
                threadArray[t] = new Thread(() -> {
                    for (int i = start; i < end - 1; i++) {
                        int minIndex = i;
                        for (int j = i + 1; j < arr.length; j++) {
                            if (arr[j] < arr[minIndex]) minIndex = j;
                        }
                        int temp = arr[minIndex];
                        arr[minIndex] = arr[i];
                        arr[i] = temp;
                    }
                });
                threadArray[t].start();
            }

            try {
                for (Thread thread : threadArray) {
                    thread.join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}