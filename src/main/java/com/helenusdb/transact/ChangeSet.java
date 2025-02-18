package com.helenusdb.transact;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.helenusdb.core.Identifiable;
import com.helenusdb.core.Identifier;

/**
 * A ChangeSet is a collection of changes to entities that have occurred during a transaction.
 * It is used to track changes to entities and to determine what operations need to be
 * performed during the commit phase of a {@link UnitOfWork}.
 * 
 * @author Todd Fredrich
 * @since Dec 12, 2024
 * @see UnitOfWork
 * @see Change
 */
public class ChangeSet
{
	// This identity map is used to keep track of entities that have changed and
	// need to be persisted during the transaction.
	private Map<Identifier, Set<Change<Identifiable>>> changesByIdentifier = new HashMap<>();
	private Map<Identifier, Identifiable> cleanEntities = new HashMap<>();

	/**
	 * Returns a stream containing all the changed entities (excluding CLEAN).
	 */
	public Stream<Change<Identifiable>> stream()
	{
	    return changesByIdentifier.values().stream().flatMap(Set::stream);
	}

	/**
	 * Registers a {@link Change) instance with the change set.
	 * 
	 * @param change the change to register.
	 * @return this ChangeSet instance.
	 */
	protected ChangeSet registerChange(Change<Identifiable> change)
	{
		if (change.isClean())
		{
			cleanEntities.put(change.getEntity().getIdentifier(), change.getEntity());
		}
		else
		{
			getChangesFor(change.getEntity()).add(change);
		}

		return this;
	}

	/**
	 * Registers a new entity that doesn't exist in the database and needs to be persisted during the transaction.
	 * 
	 * NOTE: Entities MUST be fully-populated across all identifier properties before registering them.
	 *
	 * @param entity the new entity to register.
	 * @return this ChangeSet instance.
	 */
	public ChangeSet registerNew(Identifiable entity)
	{
		return registerChange(new Change<>(entity, EntityState.NEW));
	}

	/**
	 * Registers an entity that has been updated during the transaction.
	 *
	 * @param entity the entity in its dirty state (after update).
	 * @return this ChangeSet instance.
	 */
	public ChangeSet registerDirty(Identifiable entity)
	{
		return registerChange(new Change<>(entity, EntityState.DIRTY));
	}

	/**
	 * Registers an entity for removal during the transaction.
	 *
	 * @param entity the entity to remove.
	 * @return this ChangeSet instance.
	 */
	public ChangeSet registerDeleted(Identifiable entity)
	{
		return registerChange(new Change<>(entity, EntityState.DELETED));
	}

	/**
	 * Registers an entity as clean, freshly-read from the database. These objects are used to determine deltas between
	 * dirty objects during commit().
	 * 
	 * NOTE: this method does NOT perform any copy operations so updating the object will change the copy that is
	 * registered as clean, making registration useless. Copy your own objects either before registering them as clean
	 * or before mutating them.
	 * 
	 * @param entity the entity to register as clean.
	 * @return this ChangeSet instance.
	 */
	public ChangeSet registerClean(Identifiable entity)
	{
		return registerChange(new Change<>(entity, EntityState.CLEAN));
	}

    /**
     * Clears or unregisters all previously-registered changes
     * and resets the unit of work to it's initial, empty state.
     */
    public void reset()
	{
		changesByIdentifier.clear();
		cleanEntities.clear();
	}

    /**
     * Returns a set of changes for the given entity, if present.
     * 
     * @param entity the entity to find changes for.
     * @return the set of changes for the given entity, or an empty set if none exist.
     */
	protected Set<Change<Identifiable>> getChangesFor(Identifiable entity)
	{
		Set<Change<Identifiable>> s = changesByIdentifier.get(entity.getIdentifier());
		return s != null ? s : Collections.emptySet();
	}

	/**
	 * Returns the previously-registered clean entity with the given identifier, if it exists.
	 * 
	 * @param id the identifier of the entity to find.
	 * @return the clean entity with the given identifier, or null if it doesn't exist.
	 */
	public Identifiable findClean(Identifier id)
	{
		return cleanEntities.get(id);
	}
}
