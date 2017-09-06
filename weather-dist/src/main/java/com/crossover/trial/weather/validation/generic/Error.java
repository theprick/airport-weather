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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Error error = (Error) o;

        if (errorCode != error.errorCode) return false;
        if (parameter != null ? !parameter.equals(error.parameter) : error.parameter != null) return false;
        return errorMessage != null ? errorMessage.equals(error.errorMessage) : error.errorMessage == null;
    }

    @Override
    public int hashCode() {
        int result = errorCode != null ? errorCode.hashCode() : 0;
        result = 31 * result + (parameter != null ? parameter.hashCode() : 0);
        result = 31 * result + (errorMessage != null ? errorMessage.hashCode() : 0);
        return result;
    }
}
