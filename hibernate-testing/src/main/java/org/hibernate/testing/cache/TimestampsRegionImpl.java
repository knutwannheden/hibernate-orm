/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.testing.cache;

import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.cache.spi.support.AbstractRegion;

/**
 * @author Steve Ebersole
 */
public class TimestampsRegionImpl extends AbstractRegion implements TimestampsRegion {
	public TimestampsRegionImpl(
			String name,
			RegionFactory regionFactory) {
		super( name, regionFactory, new StorageAcccessImpl() );
	}
}
