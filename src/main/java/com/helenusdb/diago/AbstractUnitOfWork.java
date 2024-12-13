package com.helenusdb.diago;

/**
 * A base UnitOfWork implementation that tracks changes to entities (clean, new, dirty, deleted)
 * and enables nested UnitOfWorks. Extend this class to provide a concrete implementation that
 * performs the actual database operations (e.g. CassandraUnitOfWork) during commit() and rollback().
 * 
 * @author Todd Fredrich
 * @since Dec 12, 2024
 * @see CassandraUnitOfWork
 * @see UnitOfWork
 */
import java.util.ArrayList;
import java.util.List;

import com.helenusdb.core.Identifiable;
import com.helenusdb.core.Identifier;

public abstract class AbstractUnitOfWork
implements UnitOfWork
{
    private List<AbstractUnitOfWork> children;
    private ChangeSet changeSet = new ChangeSet();
    private boolean isRoot = true;

	/**
	 * Registers a new entity that doesn't exist in the database and needs to be
	 * persisted during the transaction.
	 * 
	 * NOTE: Entities MUST be fully-populated across all identifier properties before
	 * registering them.
	 *
	 * @param entity the new entity to register.
	 */
	public AbstractUnitOfWork registerNew(Identifiable entity)
	{
		changeSet.registerNew(entity);
		return this;
	}

	/**
	 * Registers an entity that has been updated during the transaction.
	 *
	 * @param entity the entity in its dirty state (after update).
	 */
	public AbstractUnitOfWork registerDirty(Identifiable entity)
	{
		changeSet.registerDirty(entity);
		return this;
	}

	/**
	 * Registers an entity for removal during the transaction.
	 *
	 * @param entity the entity in its clean state (before removal).
	 */
	public AbstractUnitOfWork registerDeleted(Identifiable entity)
	{
		changeSet.registerDeleted(entity);
		return this;
	}

	/**
	 * Registers an entity as clean, freshly-read from the database. These objects are used
	 * to determine deltas between dirty objects during commit().
	 * 
	 * NOTE: this method does NOT perform any copy operations so updating the object will
	 * change the copy that is registered as clean, making registration useless. Copy your
	 * own objects either before registering them as clean or before mutating them.
	 */
	public AbstractUnitOfWork registerClean(Identifiable entity)
	{
		changeSet.registerClean(entity);
		return this;
	}

	public Identifiable readClean(Identifier id)
	{
		return changeSet.findClean(id);
	}

	public AbstractUnitOfWork addChild(AbstractUnitOfWork child)
	{
		if (children == null)
        {
            children = new ArrayList<>();
        }

		child.setRoot(false);
		children.add(child);
		return this;
	}

	private void setRoot(boolean isRoot)
	{
		this.isRoot = isRoot;
	}

	public boolean isRoot()
	{
		return isRoot;
	}
}
