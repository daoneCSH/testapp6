package com.springboot.testapp6.commons.exceptions;

public class CommonRuntimeException extends RuntimeException {
  private static final long serialVersionUID = -7032182229500031385L;

  public CommonRuntimeException() {
    super();
  }

  public CommonRuntimeException(Throwable cause) {
    super(cause);
  }

  public CommonRuntimeException(String reason) {
    super(reason);
  }

  public CommonRuntimeException(String reason, Throwable cause) {
    super(reason, cause);
  }
}