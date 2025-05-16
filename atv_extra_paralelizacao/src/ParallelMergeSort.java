import java.util.Arrays;
class ParallelMergeSort {
    private static final int THRESHOLD = 10000;

    public static void sort(int[] arr, int threads) {
        if (threads <= 1 || arr.length <= THRESHOLD) {
            SequentialMergeSort.sort(arr);
        } else {
            int mid = arr.length / 2;
            int[] left = Arrays.copyOfRange(arr, 0, mid);
            int[] right = Arrays.copyOfRange(arr, mid, arr.length);

            Thread leftThread = new Thread(() -> sort(left, threads / 2));
            Thread rightThread = new Thread(() -> sort(right, threads / 2));

            leftThread.start();
            rightThread.start();
            try {
                leftThread.join();
                rightThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            merge(arr, left, right);
        }
    }

    private static void merge(int[] arr, int[] left, int[] right) {
        int i = 0, j = 0, k = 0;
        while (i < left.length && j < right.length) {
            if (left[i] <= right[j]) arr[k++] = left[i++];
            else arr[k++] = right[j++];
        }
        while (i < left.length) arr[k++] = left[i++];
        while (j < right.length) arr[k++] = right[j++];
    }
}