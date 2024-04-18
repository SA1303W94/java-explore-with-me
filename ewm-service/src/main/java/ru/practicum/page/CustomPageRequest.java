package ru.practicum.page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class CustomPageRequest extends PageRequest {
    public CustomPageRequest(int from, int size) {
        super(from / size, size, Sort.unsorted());
    }
}