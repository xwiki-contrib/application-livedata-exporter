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
package org.xwiki.contrib.livedata.exporter;

import java.io.File;
import java.io.IOException;

import org.xwiki.component.annotation.Role;
import org.xwiki.livedata.LiveData;
import org.xwiki.livedata.LiveDataConfiguration;
import org.xwiki.livedata.LiveDataException;
import org.xwiki.stability.Unstable;

/**
 * Live Data export writer, should be named after the format it implements.
 *
 * @version $Id$
 * @since 1.0
 */
@Role
@Unstable
public interface LiveDataExportWriter extends AutoCloseable
{
    /**
     * Initialize the export for writing data with the given configuration.
     *
     * @param outputFile the file to write the export to
     * @param configuration the Live Data configuration to get names and visible properties from
     * @throws LiveDataException if the initialization failed
     */
    void initialize(File outputFile, LiveDataConfiguration configuration) throws LiveDataException;

    /**
     * Write the given data.
     * <p>
     * Might be called many times, the implementation is free to write data immediately or aggregate data and write it
     * only later, e.g., on {@link #close()}.
     *
     * @param data the data to write
     * @throws LiveDataException if writing the data failed
     */
    void write(LiveData data) throws LiveDataException;

    @Override
    void close() throws LiveDataException, IOException;
}
