package ru.practicum.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionType {

    START_DATE_IN_PAST("Дата начала события = %s не может быть в прошлом");

    private final String value;
}