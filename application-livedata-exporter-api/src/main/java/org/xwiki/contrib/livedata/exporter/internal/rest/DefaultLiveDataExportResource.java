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
package org.xwiki.contrib.livedata.exporter.internal.rest;

import java.io.File;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.Response;

import org.xwiki.component.annotation.Component;
import org.xwiki.context.concurrent.ContextStoreManager;
import org.xwiki.contrib.livedata.exporter.internal.LiveDataExportCacheManager;
import org.xwiki.contrib.livedata.exporter.internal.LiveDataExportJob;
import org.xwiki.contrib.livedata.exporter.internal.LiveDataExportJobIdGenerator;
import org.xwiki.contrib.livedata.exporter.internal.LiveDataExportRequest;
import org.xwiki.contrib.livedata.exporter.internal.LiveDataExportResult;
import org.xwiki.job.Job;
import org.xwiki.job.JobExecutor;
import org.xwiki.livedata.LiveDataConfiguration;
import org.xwiki.rest.XWikiResource;
import org.xwiki.rest.url.ParametrizedRestURLGenerator;
import org.xwiki.contrib.livedata.exporter.rest.LiveDataExportResource;

/**
 * Default implementation of {@link LiveDataExportResource}.
 *
 * @version $Id: e174b6896ca93d08e2afb4e6db719ac7ff2fd581 $
 */
@Component
@Named("org.xwiki.contrib.livedata.exporter.internal.rest.DefaultLiveDataExportResource")
@Singleton
public class DefaultLiveDataExportResource extends XWikiResource implements LiveDataExportResource
{
    @Inject
    private JobExecutor jobExecutor;

    @Inject
    private ContextStoreManager contextStoreManager;

    @Inject
    private LiveDataExportJobIdGenerator idGenerator;

    @Inject
    @Named("jobstatus")
    private ParametrizedRestURLGenerator<List<String>> jobStatusURLGenerator;

    @Inject
    private LiveDataExportCacheManager exportCache;

    @Override
    public Response createExportJob(String format, String namespace, LiveDataConfiguration configuration)
        throws Exception
    {
        String sessionId = this.xcontextProvider.get().getRequest().getSession().getId();
        LiveDataExportRequest jobRequest = new LiveDataExportRequest(sessionId, format, namespace, configuration);
        jobRequest.setStatusLogIsolated(false);
        jobRequest.setStatusSerialized(false);
        jobRequest.setId(this.idGenerator.generateId());
        jobRequest.setContext(this.contextStoreManager.save(this.contextStoreManager.getSupportedEntries()));
        Job job = this.jobExecutor.execute(LiveDataExportJob.JOB_TYPE, jobRequest);
        // Redirect to the job status.
        return Response.seeOther(this.jobStatusURLGenerator.getURL(job.getRequest().getId()).toURI()).build();
    }

    @Override
    public Response download(String jobId)
    {
        Optional<LiveDataExportResult> maybeResultFile = this.exportCache.getForCurrentUser(jobId);
        Response result;

        if (maybeResultFile.isPresent())
        {
            File resultFile = maybeResultFile.get().getFile();

            // TODO: this should depend on the actual export backend.
            String downloadFilename = jobId.replace("/", "_") + ".csv";
            result = Response.ok(resultFile, "text/csv")
                .header("Content-Disposition", String.format("attachment; filename=\"%s\"", downloadFilename))
                .build();
        } else {
            result = Response.status(Response.Status.NOT_FOUND).build();
        }

        return result;
    }
}
