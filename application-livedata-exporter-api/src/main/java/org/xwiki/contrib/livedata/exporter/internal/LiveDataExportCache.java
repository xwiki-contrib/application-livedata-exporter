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

import org.xwiki.component.annotation.Role;

/**
 * Cache for storing Live Data results.
 *
 * @version $Id$
 */
@Role
public interface LiveDataExportCache
{
    /**
     * Gets the result for the given job id (parts joined by "/").
     *
     * @param id the id of the job
     * @return the result of the export
     */
    Optional<LiveDataExportResult> get(String id);

    /**
     * Puts the result for the given job id (parts joined by "/") into the cache.
     *
     * @param id the id of the job
     * @param data the result of the job
     */
    void put(String id, LiveDataExportResult data);
}
