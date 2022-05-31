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

import org.xwiki.job.AbstractRequest;
import org.xwiki.livedata.LiveDataConfiguration;

/**
 * Live Data export request.
 *
 * @version $Id: 43d67a62d87e41a648b7df7eff67a4c977368213 $
 */
public class LiveDataExportRequest extends AbstractRequest
{
    private static final String PROPERTY_NAMESPACE = "namespace";

    private static final String PROPERTY_FORMAT = "format";

    private static final String PROPERTY_CONFIGURATION = "configuration";

    /**
     * Construct a new export request with the given Live Data configuration.
     *
     * @param format the format of the export
     * @param namespace the namespace in which the Live Data source should be found
     * @param configuration the configuration for the Live Data
     */
    public LiveDataExportRequest(String format, String namespace, LiveDataConfiguration configuration)
    {
        setFormat(format);
        setNamespace(namespace);
        setConfiguration(configuration);
    }

    /**
     * @param format see {@link #getFormat()}
     */
    public void setFormat(String format)
    {
        setProperty(PROPERTY_FORMAT, format);
    }

    /**
     * @return the format of the export, e.g., csv
     */
    public String getFormat()
    {
        return getProperty(PROPERTY_FORMAT);
    }

    /**
     * @param query see {@link #getConfiguration()}
     */
    public void setConfiguration(LiveDataConfiguration query)
    {
        setProperty(PROPERTY_CONFIGURATION, query);
    }

    /**
     * @return the Live Data configuration
     */
    public LiveDataConfiguration getConfiguration()
    {
        return getProperty(PROPERTY_CONFIGURATION);
    }

    /**
     * @param namespace see {@link #getNamespace()}
     */
    public void setNamespace(String namespace)
    {
        setProperty(PROPERTY_NAMESPACE, namespace);
    }

    /**
     * @return the namespace where the source shall be found
     */
    public String getNamespace()
    {
        return getProperty(PROPERTY_NAMESPACE);
    }
}
