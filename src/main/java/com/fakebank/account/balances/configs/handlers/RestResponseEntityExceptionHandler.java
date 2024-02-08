package com.fakebank.account.balances.configs.handlers;

import com.fakebank.account.balances.interactors.exceptions.AccountInactiveException;
import com.fakebank.account.balances.interactors.exceptions.InsufficientFundsException;
import com.fakebank.account.balances.interactors.exceptions.LimitExceededException;
import com.fakebank.account.balances.repositories.exceptions.AccountNotFoundException;
import com.fakebank.account.balances.repositories.exceptions.InternalServerErrorException;
import com.fakebank.account.balances.transportlayers.dtos.ErrorDto;
import com.fakebank.account.balances.transportlayers.dtos.MetaDto;
import com.fakebank.account.balances.transportlayers.dtos.ResponseErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

import static com.fakebank.account.balances.transportlayers.models.EnumErrorsBalancesTransferModel.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String ERRO_INTERNO_DO_SERVIDOR = "Erro interno do servidor.";
    public static final String OCORREU_UM_ERRO_INESPERADO_NO_SERVIDOR_CONTATE_O_ADMINISTRADOR_DO_SISTEMA
            = "Ocorreu um erro inesperado no servidor, contate o administrador do sistema.";
    public static final String CONTA_ORIGEM_INATIVA = "Conta origem inativa";
    public static final String SALDO_INSUFICIENTE_TITLE = "Saldo insuficiente.";
    public static final String VALOR_ACIMA_DO_LIMITE_TITLE = "Valor acima do limite.";

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Object> accountNotFoundExceptionHandler(AccountNotFoundException e) {
        this.logger.error(e.getMessage());
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({AccountInactiveException.class})
    public ResponseEntity<Object> accountInactiveExceptionHandler(AccountInactiveException e) {
        this.logger.error(e.getMessage());
        ErrorDto errorDto = getErrorDto(CONTA_INATIVA.name(), CONTA_ORIGEM_INATIVA, e.getMessage());
        ResponseErrorDto responseErrorDto = getResponseErrorDto(errorDto);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({InsufficientFundsException.class})
    public ResponseEntity<Object> insufficientFundsExceptionHandler(InsufficientFundsException e) {
        this.logger.error(e.getMessage());
        ErrorDto errorDto = getErrorDto(SALDO_INSUFICIENTE.name(), SALDO_INSUFICIENTE_TITLE, e.getMessage());
        ResponseErrorDto responseErrorDto = getResponseErrorDto(errorDto);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({LimitExceededException.class})
    public ResponseEntity<Object> limitExceededExceptionHandler(LimitExceededException e) {
        this.logger.error(e.getMessage());
        ErrorDto errorDto = getErrorDto(VALOR_ACIMA_LIMITE.name(), VALOR_ACIMA_DO_LIMITE_TITLE, e.getMessage());
        ResponseErrorDto responseErrorDto = getResponseErrorDto(errorDto);
        return new ResponseEntity<>(responseErrorDto, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({InternalServerErrorException.class, RuntimeException.class})
    public ResponseEntity<Object> repositoryInternalServerErrorExceptionHandler(RuntimeException e,
                                                                                WebRequest request) {
        this.logger.error("Error " + e + " path " + ((ServletWebRequest) request).getRequest().getRequestURI());
        ErrorDto errorDto = getErrorDto(INTERNAL_SERVER_ERROR.name(),
                ERRO_INTERNO_DO_SERVIDOR,
                OCORREU_UM_ERRO_INESPERADO_NO_SERVIDOR_CONTATE_O_ADMINISTRADOR_DO_SISTEMA);
        ResponseErrorDto responseErrorDto = getResponseErrorDto(errorDto);
        return new ResponseEntity<>(responseErrorDto, INTERNAL_SERVER_ERROR);
    }

    private static ErrorDto getErrorDto(String code, String title, String detail) {
        return new ErrorDto.Builder(code)
                .setTitle(title)
                .setDetail(detail)
                .build();
    }

    private static ResponseErrorDto getResponseErrorDto(ErrorDto errorDto) {
        return new ResponseErrorDto.Builder(List.of(errorDto))
                .setMeta(new MetaDto())
                .build();
    }

}
