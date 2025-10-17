package ar.edu.unsam.algo3.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * Manejador global de excepciones para toda la aplicación.
 * Cualquier excepción que ocurra en un Controller y sea del tipo
 * BusinessException será capturada automáticamente por esta clase.
 */
@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException): ResponseEntity<Map<String, String>> {
        val body = mapOf("message" to (ex.message ?: "Error de negocio"))
        return ResponseEntity(body, HttpStatus.NOT_FOUND)
    }

    // (Opcional) Manejador genérico para cualquier otra excepción
    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<Map<String, String>> {
        val body = mapOf("message" to (ex.message ?: "Error interno del servidor"))
        return ResponseEntity(body, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
