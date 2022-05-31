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
package org.xwiki.contrib.livedata.exporter.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.xwiki.livedata.LiveDataConfiguration;
import org.xwiki.livedata.LiveDataSource;
import org.xwiki.rest.XWikiRestException;
import org.xwiki.stability.Unstable;

/**
 * Provides the possibility to export entries from an existing {@link LiveDataSource}.
 *
 * @version $Id: e299a5b18c86ab223b87cd62e8df2d37dbe41c37 $
 * @since 1.0
 */
@Path("/liveData/export/{format}")
@Unstable
public interface LiveDataExportResource
{
    /**
     * Creates an export job for the given query that corresponds to the entries resource.
     *
     * @param format the format of the export file
     * @param namespace the component manager name-space where to look for {@link LiveDataSource} implementations;
     *     if not specified then the context / current name-space is used
     * @param configuration the Live Data query
     * @return the newly created job
     * @throws Exception if creating the export job fails
     */
    @POST
    Response createExportJob(
        @PathParam("format") String format,
        @QueryParam("namespace") @DefaultValue("") String namespace,
        LiveDataConfiguration configuration
    ) throws Exception;

    /**
     * Download the result of the given export job.
     *
     * @param jobId the id of the job
     * @return a redirect to the download
     * @throws XWikiRestException when the download cannot be found or the user must not access it
     */
    @GET
    Response download(@QueryParam("jobId") String jobId) throws XWikiRestException;

    /**
     * Cancel or delete the given export job.
     *
     * @param jobId the id of the job
     * @return if the export has been found and cancelled/deleted
     * @throws XWikiRestException when the download cannot be found or the user must not access it
     */
    @DELETE
    Response delete(@QueryParam("jobId") String jobId) throws XWikiRestException;
}
