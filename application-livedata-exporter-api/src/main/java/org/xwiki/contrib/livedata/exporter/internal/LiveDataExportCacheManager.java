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

import java.io.File;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.user.CurrentUserReference;
import org.xwiki.user.UserReference;
import org.xwiki.user.UserReferenceResolver;

/**
 * Manages the {@link LiveDataExportCache}, providing a simpler interface to add/remove entries for the current user.
 *
 * @version $Id$
 */
@Component(roles = LiveDataExportCacheManager.class)
@Singleton
public class LiveDataExportCacheManager
{
    @Inject
    private LiveDataExportCache liveDataExportCache;

    @Inject
    private UserReferenceResolver<CurrentUserReference> userReferenceResolver;

    /**
     * Puts the given file into the cache for the current context user.
     *
     * @param id the id under which the file shall be stored
     * @param file the file to store
     */
    public void putForCurrentUser(String id, File file)
    {
        this.liveDataExportCache.put(id, new LiveDataExportResult(file, getCurrentUserReference()));
    }

    /**
     * Gets the result from the cache if there is a result with the given id for the current user.
     *
     * @param id the id for which the result shall be obtained
     * @return the result if there is any for the current user, empty optional otherwise
     */
    public Optional<LiveDataExportResult> getForCurrentUser(String id)
    {
        Optional<LiveDataExportResult> result = this.liveDataExportCache.get(id);
        if (result.isPresent() && Objects.equals(getCurrentUserReference(),  result.get().getUserReference())) {
            return result;
        } else {
            return Optional.empty();
        }
    }

    private UserReference getCurrentUserReference()
    {
        return this.userReferenceResolver.resolve(CurrentUserReference.INSTANCE);
    }
}
