package com.example.bookapi.exception

import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidation(e: MethodArgumentNotValidException): Map<String, String> {
        val fieldErrors = e.bindingResult.fieldErrors
        val message =
            if (fieldErrors.isEmpty()) {
                "Validation failed"
            } else {
                fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage ?: "invalid"}" }
            }
        return mapOf("message" to message)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleNotReadable(e: HttpMessageNotReadableException): Map<String, String> = mapOf("message" to "リクエストの形式が正しくありません")
}
