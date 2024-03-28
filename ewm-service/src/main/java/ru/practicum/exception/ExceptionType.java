package ru.practicum.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionType {

    CATEGORY_NOT_FOUND("Категория с id = %s не найдена"),
    EMAIL_ALREADY_EXISTS("Пользователь с email = %s уже существует"),
    USER_NOT_FOUND("Пользователь с id = %s не найден"),
    EVENT_NOT_FOUND("Событие с id = %s не найдено"),
    EVENT_UNAVAILABLE_FOR_EDITING("События можно изменять более чем за 2 часа до наступления события и не должны быть опубликованы"),
    REQUEST_STATUS_NOT_PENDING("Запрос с id = %s должен иметь статус PENDING"),
    REACHED_PARTICIPANT_LIMIT("Достигнут предел участников"),
    REQUEST_NOT_FOUND("Запрос с id = %s не найден"),
    EVENT_ALREADY_PUBLISHED("Нельзя отклонить публикацию уже опубликованного события с id = %s"),
    EVENT_MUST_BE_PENDING("Нельзя публиковать событие со статусом, отличным от PENDING"),
    EVENT_UNAVAILABLE_FOR_EDITING_ADMIN("События можно изменять только если дата события на 1 час или более меньше даты публикации"),
    CATEGORY_HAS_EVENTS("Категория с id = %s содержит события"),
    REQUEST_DUPLICATE("Запрос с идентификатором запроса = %s и идентификатором события = %s уже существует"),
    EVENT_INITIATED_BY_REQUESTER("Нельзя создавать запросы от инициатора события"),
    REQUEST_FOR_UNPUBLISHED_EVENT("Запросы для неопубликованных событий не принимаются"),
    REQUEST_ALREADY_CANCELED("Запрос с id = %s уже отменен"),
    COMPILATION_NOT_FOUND("Компиляция с id = %s не найдена"),
    STATE_NOT_FOUND("Состояние публикации не найдено для действия события = %s"),
    START_DATE_IN_PAST("Дата начала события = %s не может быть в прошлом");

    private final String value;
}