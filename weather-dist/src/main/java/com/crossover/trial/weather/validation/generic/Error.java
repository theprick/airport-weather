package com.crossover.trial.weather.validation.generic;

/**
 * Created by Popescu Adrian-Dumitru on 05.09.2017.
 *
 * Encapsulates the error information related to an input parameter.
 */
public class Error {
    private ErrorCode errorCode;
    private String parameter;
    private String errorMessage;

    public Error() {
    }

    public Error(ErrorCode errorCode, String parameter, String errorMessage) {
        this.errorCode = errorCode;
        this.parameter = parameter;
        this.errorMessage = errorMessage;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
