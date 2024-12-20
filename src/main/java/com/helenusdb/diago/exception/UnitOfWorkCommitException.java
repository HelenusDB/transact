package com.helenusdb.diago.exception;

import com.helenusdb.core.exception.HelenusdbException;

public class UnitOfWorkCommitException
extends HelenusdbException
{
	private static final long serialVersionUID = -6123684196945185514L;

	public UnitOfWorkCommitException() {
		super();
	}

	public UnitOfWorkCommitException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnitOfWorkCommitException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnitOfWorkCommitException(String message) {
		super(message);
	}

	public UnitOfWorkCommitException(Throwable cause) {
		super(cause);
	}
}
