package com.retificarenova.exception;

/**
 * Exceção de Negócio da Aplicação
 * TODO: Usar para exceções específicas do domínio
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

}
