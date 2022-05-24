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
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.contrib.livedata.exporter.LiveDataExportWriter;
import org.xwiki.environment.Environment;
import org.xwiki.job.AbstractJob;
import org.xwiki.job.DefaultJobStatus;
import org.xwiki.livedata.LiveData;
import org.xwiki.livedata.LiveDataConfiguration;
import org.xwiki.livedata.LiveDataEntryStore;
import org.xwiki.livedata.LiveDataException;
import org.xwiki.livedata.LiveDataQuery;
import org.xwiki.livedata.LiveDataSource;
import org.xwiki.livedata.LiveDataSourceManager;

/**
 * Live Data export job.
 *
 * @version $Id: 9a89768a4a2c472daf904a7d89e8bd5e1a80550d $
 */
@Component
@Named(LiveDataExportJob.JOB_TYPE)
public class LiveDataExportJob extends AbstractJob<LiveDataExportRequest, DefaultJobStatus<LiveDataExportRequest>>
{
    /**
     * Name of the job type.
     */
    public static final String JOB_TYPE = "LiveDataExportJob";

    private static final int PAGINATION_LIMIT = 100;

    @Inject
    private LiveDataSourceManager liveDataSourceManager;

    @Inject
    private LiveDataConfigurationRequestResolver configurationResolver;

    @Inject
    private LiveDataExportCacheManager exportCache;

    @Inject
    private Environment environment;

    @Override
    public String getType()
    {
        return JOB_TYPE;
    }

    @Override
    protected void runInternal() throws LiveDataException
    {
        LiveDataConfiguration configuration = this.configurationResolver.resolve(getRequest());
        LiveDataQuery query = configuration.getQuery();
        Optional<LiveDataSource> source =
            this.liveDataSourceManager.get(query.getSource(), this.getRequest().getNamespace());
        if (source.isEmpty()) {
            throw new LiveDataException("Live Data source not found");
        }

        LiveDataEntryStore entryStore = source.get().getEntries();

        boolean success = false;
        File outputFile = null;

        try (LiveDataExportWriter exportWriter = this.componentManager.getInstance(LiveDataExportWriter.class,
            getRequest().getFormat())) {
            startProgress(configuration.getData().getCount());

            outputFile = generateUniqueTemporaryFile();
            exportWriter.initialize(outputFile, configuration);

            query.setLimit(PAGINATION_LIMIT);
            long offset = 0;
            LiveData data;
            do {
                // TODO: add a progress message.
                this.progressManager.startStep(this);

                query.setOffset(offset);
                data = entryStore.get(query);

                exportWriter.write(data);

                offset += PAGINATION_LIMIT;
            } while (offset < data.getCount() && !this.getStatus().isCanceled());

            if (offset >= data.getCount()) {
                String jobId = String.join("/", getRequest().getId());
                this.exportCache.putForCurrentUser(jobId, outputFile);
                success = true;
            }
        } catch (IOException | ComponentLookupException e) {
            throw new LiveDataException(e);
        } finally {
            this.progressManager.popLevelProgress(this);
            if (!success && outputFile != null) {
                try {
                    Files.deleteIfExists(outputFile.toPath());
                } catch (IOException | SecurityException e) {
                    this.logger.warn("Failed cleaning up the failed export", e);
                }
            }
        }
    }

    private void startProgress(long count)
    {
        // Round up the number of pages we need.
        int paginationSteps = (int) (count + PAGINATION_LIMIT - 1) / PAGINATION_LIMIT;
        if (paginationSteps > 0) {
            this.progressManager.pushLevelProgress(paginationSteps, this);
        } else {
            this.progressManager.pushLevelProgress(this);
        }
    }

    /**
     * @return the unique (thread-safe) temporary file to be used to put the export result in
     */
    private File generateUniqueTemporaryFile() throws LiveDataException
    {
        File liveDataDirectory = new File(this.environment.getTemporaryDirectory(), "liveDataExport");
        if (!liveDataDirectory.mkdirs() && !liveDataDirectory.exists()) {
            throw new LiveDataException("Could not create output directory.");
        }

        return new File(liveDataDirectory, "export-" + UUID.randomUUID() + ".tmp");
    }

    @Override
    protected DefaultJobStatus<LiveDataExportRequest> createNewStatus(LiveDataExportRequest request)
    {
        DefaultJobStatus<LiveDataExportRequest> status = super.createNewStatus(request);
        status.setCancelable(true);
        return status;
    }
}
