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

import java.util.List;
import java.util.UUID;

import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;

/**
 * Generates secure private ids for Live Data Export jobs.
 *
 * @version $Id: 2c5977bd79a9314ace2882782709d4968f27fe5a $
 */
@Component(roles = LiveDataExportJobIdGenerator.class)
@Singleton
public class LiveDataExportJobIdGenerator
{
    /**
     * @return the random id of the export job
     */
    public List<String> generateId()
    {
        // Use a UUID to guarantee uniqueness and a
        return List.of("export", "liveData", UUID.randomUUID().toString());
    }
}
