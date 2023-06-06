package ru.practicum.shareit.pagination;

public class Pagination {
    public static int adjustFrom(int from, int size) {
        if (from > size) {
            return from - size;
        }
        return from;
    }

    public static boolean isValid(int from, int size) {
        return from >= 0 && size > 0;
    }
}
