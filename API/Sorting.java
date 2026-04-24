package API;

import Models.CustomList;


public class Sorting {

    public static <T extends Comparable<T>> void quickSort(CustomList<T> list) {
        if (list.size() <= 1) return;
        quickSortHelper(list, 0, list.size() - 1);
    }

    private static <T extends Comparable<T>> void quickSortHelper(CustomList<T> list, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(list, low, high);
            quickSortHelper(list, low, pivotIndex - 1);
            quickSortHelper(list, pivotIndex + 1, high);
        }
    }

    private static <T extends Comparable<T>> int partition(CustomList<T> list, int low, int high) {
        T pivot = list.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (list.get(j).compareTo(pivot) <= 0) {
                i++;
                swap(list, i, j);
            }
        }
        swap(list, i + 1, high);
        return i + 1;
    }

    private static <T> void swap(CustomList<T> list, int i, int j) {
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    /**
     * Insertion Sort - good for small lists.
     */
    public static <T extends Comparable<T>> void insertionSort(T[] array) {
        for (int i = 1; i < array.length; i++) {
            T key = array[i];
            int j = i - 1;
            while (j >= 0 && array[j].compareTo(key) > 0) {
                array[j + 1] = array[j];
                j--;
            }
            array[j + 1] = key;
        }
    }

    /**
     * Merge Sort for arrays.
     */
    public static <T extends Comparable<T>> void mergeSort(T[] array) {
        if (array.length <= 1) return;
        mergeSortHelper(array, 0, array.length - 1);
    }

    private static <T extends Comparable<T>> void mergeSortHelper(T[] array, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSortHelper(array, left, mid);
            mergeSortHelper(array, mid + 1, right);
            merge(array, left, mid, right);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> void merge(T[] array, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        T[] leftArray = (T[]) new Comparable[n1];
        T[] rightArray = (T[]) new Comparable[n2];

        for (int i = 0; i < n1; i++) leftArray[i] = array[left + i];
        for (int j = 0; j < n2; j++) rightArray[j] = array[mid + 1 + j];

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (leftArray[i].compareTo(rightArray[j]) <= 0) {
                array[k] = leftArray[i];
                i++;
            } else {
                array[k] = rightArray[j];
                j++;
            }
            k++;
        }

        while (i < n1) {
            array[k] = leftArray[i];
            i++;
            k++;
        }

        while (j < n2) {
            array[k] = rightArray[j];
            j++;
            k++;
        }
    }

    /**
     * Selection Sort - simple but O(n²).
     */
    public static <T extends Comparable<T>> void selectionSort(T[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < array.length; j++) {
                if (array[j].compareTo(array[minIndex]) < 0) {
                    minIndex = j;
                }
            }
            T temp = array[minIndex];
            array[minIndex] = array[i];
            array[i] = temp;
        }
    }
}
