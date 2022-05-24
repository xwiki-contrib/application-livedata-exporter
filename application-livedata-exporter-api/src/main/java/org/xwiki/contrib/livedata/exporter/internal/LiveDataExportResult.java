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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.xwiki.cache.DisposableCacheValue;
import org.xwiki.user.UserReference;

/**
 * Stores the result of a {@link LiveDataExportJob}.
 *
 * @version $Id$
 */
public class LiveDataExportResult implements DisposableCacheValue
{
    private final File file;

    private final UserReference userReference;

    /**
     * Initialize the export result.
     *
     * @param file the file where the result has been stored
     * @param userReference the user that started the export
     */
    public LiveDataExportResult(File file, UserReference userReference)
    {
        this.file = file;
        this.userReference = userReference;
    }

    /**
     * @return the exported file
     */
    public File getFile()
    {
        return file;
    }

    /**
     * @return the user that started the export
     */
    public UserReference getUserReference()
    {
        return userReference;
    }

    @Override
    public void dispose()
    {
        getFile().delete();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LiveDataExportResult that = (LiveDataExportResult) o;

        return new EqualsBuilder().append(getFile(), that.getFile())
            .append(getUserReference(), that.getUserReference()).isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37).append(getFile()).append(getUserReference()).toHashCode();
    }
}
