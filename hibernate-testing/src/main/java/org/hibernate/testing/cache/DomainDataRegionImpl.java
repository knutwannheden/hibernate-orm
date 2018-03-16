/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.testing.cache;

import org.hibernate.cache.cfg.spi.CollectionDataCachingConfig;
import org.hibernate.cache.cfg.spi.DomainDataRegionBuildingContext;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.cfg.spi.EntityDataCachingConfig;
import org.hibernate.cache.cfg.spi.NaturalIdDataCachingConfig;
import org.hibernate.cache.spi.support.AbstractDomainDataRegion;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.cache.spi.support.CollectionNonStrictReadWriteAccess;
import org.hibernate.cache.spi.support.CollectionReadOnlyAccess;
import org.hibernate.cache.spi.support.CollectionReadWriteAccess;
import org.hibernate.cache.spi.support.EntityNonStrictReadWriteAccess;
import org.hibernate.cache.spi.support.EntityReadOnlyAccess;
import org.hibernate.cache.spi.support.EntityReadWriteAccess;
import org.hibernate.cache.spi.support.NaturalIdNonStrictReadWriteAccess;
import org.hibernate.cache.spi.support.NaturalIdReadOnlyAccess;
import org.hibernate.cache.spi.support.NaturalIdReadWriteAccess;
import org.hibernate.metamodel.model.domain.NavigableRole;

import org.jboss.logging.Logger;

/**
 * @author Steve Ebersole
 */
public class DomainDataRegionImpl extends AbstractDomainDataRegion {
	private static final Logger log = Logger.getLogger( DomainDataRegionImpl.class );

	private final CacheKeysFactory effectiveKeysFactory;

	public DomainDataRegionImpl(
			DomainDataRegionConfig regionConfig,
			CachingRegionFactory regionFactory,
			DomainDataRegionBuildingContext buildingContext) {
		super( regionConfig, regionFactory, new DomainDataStorageAccessImpl(), buildingContext );

		this.effectiveKeysFactory = buildingContext.getEnforcedCacheKeysFactory() != null
				? buildingContext.getEnforcedCacheKeysFactory()
				: regionFactory.getCacheKeysFactory();
	}

	public CacheKeysFactory getEffectiveKeysFactory() {
		return effectiveKeysFactory;
	}


	@Override
	public EntityDataAccess generateEntityAccess(EntityDataCachingConfig entityAccessConfig) {
		final NavigableRole namedEntityRole = entityAccessConfig.getNavigableRole();
		final AccessType accessType = entityAccessConfig.getAccessType();

		log.debugf( "Generating entity cache access [%s] : %s", accessType.getExternalName(), namedEntityRole );

		switch ( accessType ) {
			case READ_ONLY: {
				return new EntityReadOnlyAccess(
						this,
						effectiveKeysFactory,
						new DomainDataStorageAccessImpl(),
						entityAccessConfig
				);
			}
			case READ_WRITE: {
				return new EntityReadWriteAccess(
						this,
						effectiveKeysFactory,
						new DomainDataStorageAccessImpl(),
						entityAccessConfig
				);
			}
			case NONSTRICT_READ_WRITE: {
				return new EntityNonStrictReadWriteAccess(
						this,
						effectiveKeysFactory,
						new DomainDataStorageAccessImpl(),
						entityAccessConfig
				);
			}
			case TRANSACTIONAL: {
				return new EntityTransactionalAccess(
						this,
						effectiveKeysFactory,
						new DomainDataStorageAccessImpl(),
						entityAccessConfig
				);
			}
			default: {
				throw new IllegalArgumentException( "Unrecognized cache AccessType - " + accessType );
			}
		}
	}

	@Override
	public NaturalIdDataAccess generateNaturalIdAccess(NaturalIdDataCachingConfig naturalIdDataCachingConfig) {
		final NavigableRole namedEntityRole = naturalIdDataCachingConfig.getNavigableRole();
		final AccessType accessType = naturalIdDataCachingConfig.getAccessType();

		log.debugf( "Generating entity natural-id access [%s] : %s", accessType.getExternalName(), namedEntityRole );

		switch ( accessType ) {
			case READ_ONLY: {
				return new NaturalIdReadOnlyAccess(
						this,
						effectiveKeysFactory,
						new DomainDataStorageAccessImpl(),
						naturalIdDataCachingConfig
				);
			}
			case READ_WRITE: {
				return new NaturalIdReadWriteAccess(
						this,
						effectiveKeysFactory,
						new DomainDataStorageAccessImpl(),
						naturalIdDataCachingConfig
				);
			}
			case NONSTRICT_READ_WRITE: {
				return new NaturalIdNonStrictReadWriteAccess(
						this,
						effectiveKeysFactory,
						new DomainDataStorageAccessImpl(),
						naturalIdDataCachingConfig
				);
			}
			case TRANSACTIONAL: {
				return new NaturalIdTransactionalAccess(
						this,
						effectiveKeysFactory,
						new DomainDataStorageAccessImpl(),
						naturalIdDataCachingConfig
				);
			}
			default: {
				throw new IllegalArgumentException( "Unrecognized cache AccessType - " + accessType );
			}
		}
	}

	@Override
	public CollectionDataAccess generateCollectionAccess(CollectionDataCachingConfig config) {
		final NavigableRole namedCollectionRole = config.getNavigableRole();

		log.debugf( "Generating collection cache access : %s", namedCollectionRole );

		switch ( config.getAccessType() ) {
			case READ_ONLY: {
				return new CollectionReadOnlyAccess(
						this,
						effectiveKeysFactory,
						new DomainDataStorageAccessImpl(),
						config
				);
			}
			case READ_WRITE: {
				return new CollectionReadWriteAccess(
						this,
						effectiveKeysFactory,
						new DomainDataStorageAccessImpl(),
						config
				);
			}
			case NONSTRICT_READ_WRITE: {
				return new CollectionNonStrictReadWriteAccess(
						this,
						effectiveKeysFactory,
						new DomainDataStorageAccessImpl(),
						config
				);
			}
			case TRANSACTIONAL: {
				return new CollectionTransactionAccess(
						this,
						effectiveKeysFactory,
						new DomainDataStorageAccessImpl(),
						config
				);
			}
			default: {
				throw new IllegalArgumentException( "Unrecognized cache AccessType - " + config.getAccessType() );
			}
		}
	}
}
