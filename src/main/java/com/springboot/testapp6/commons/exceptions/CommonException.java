package com.springboot.testapp6.commons.exceptions;

public class CommonException extends Exception {
  private static final long serialVersionUID = -7032182229500031385L;

  public CommonException() {
    super();
  }

  public CommonException(Throwable cause) {
    super(cause);
  }

  public CommonException(String reason) {
    super(reason);
  }

  public CommonException(String reason, Throwable cause) {
    super(reason, cause);
  }
}