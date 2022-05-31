/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.contrib.livedata.exporter.internal;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.xwiki.cache.Cache;
import org.xwiki.cache.CacheException;
import org.xwiki.cache.CacheManager;
import org.xwiki.cache.config.CacheConfiguration;
import org.xwiki.cache.eviction.EntryEvictionConfiguration;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Disposable;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

/**
 * Default implementation of the {@link LiveDataExportCache}.
 *
 * @version $Id$
 */
@Component
@Singleton
public class DefaultLiveDataExportCache implements LiveDataExportCache, Initializable, Disposable
{
    /**
     * Cache manager to create the cache.
     */
    @Inject
    private CacheManager cacheManager;

    /**
     * The cache instance.
     */
    private Cache<LiveDataExportResult> exportResultCache;

    @Override
    public Optional<LiveDataExportResult> get(String id)
    {
        return Optional.ofNullable(this.exportResultCache.get(id));
    }

    @Override
    public void put(String id, LiveDataExportResult data)
    {
        this.exportResultCache.set(id, data);
    }

    @Override
    public void remove(String id)
    {
        this.exportResultCache.remove(id);
    }

    @Override
    public void initialize() throws InitializationException
    {
        EntryEvictionConfiguration evictionConfiguration = new EntryEvictionConfiguration();
        evictionConfiguration.setAlgorithm(EntryEvictionConfiguration.Algorithm.LRU);
        evictionConfiguration.setTimeToLive(3600 * 8);
        CacheConfiguration configuration =
            new CacheConfiguration("org.xwiki.contrib.livedata.exporter", evictionConfiguration);
        try {
            this.exportResultCache = this.cacheManager.createNewCache(configuration);
        } catch (CacheException e) {
            throw new InitializationException("Failed to create access control cache", e);
        }
    }

    @Override
    public void dispose()
    {
        this.exportResultCache.dispose();
    }
}
