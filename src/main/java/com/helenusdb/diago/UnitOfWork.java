package com.helenusdb.diago;

import com.helenusdb.diago.exception.UnitOfWorkCommitException;
import com.helenusdb.diago.exception.UnitOfWorkRollbackException;

/**
 * This interface provides a transactional context for managing database
 * changes. It allows registering new entities, marking entities as "dirty" or
 * "deleted", and committing or rolling back the current transaction.
 */
public interface UnitOfWork
{
	/**
	 * Commits the current transaction and saves all changes made during the session
	 * to the database.
	 */
	void commit() throws UnitOfWorkCommitException;

	/**
	 * Undoes all changes made during the current session and rolls back the
	 * transaction.
	 */
	void rollback() throws UnitOfWorkRollbackException;
}
