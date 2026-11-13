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
package org.xwiki.contrib.livedata.exporter.test.po;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.xwiki.test.ui.po.BaseElement;

/**
 * An export listed in the {@link LiveDataExporterPanel}.
 *
 * @version $Id$
 * @since 1.2
 */
public class LiveDataExport extends BaseElement
{
    private static final String JOB_ID_PARAMETER = "jobId=";

    private final WebElement container;

    /**
     * @param container the list item of the export
     */
    public LiveDataExport(WebElement container)
    {
        this.container = container;
    }

    /**
     * Wait until the export is finished and its result can be downloaded.
     *
     * @return this export
     */
    public LiveDataExport waitUntilFinished()
    {
        getDriver().waitUntilCondition(driver -> !getDownloadLink().getAttribute("class").contains("disabled"));
        return this;
    }

    /**
     * @return the description of the exported Live Data configuration (properties, filters and sorting)
     */
    public String getDescription()
    {
        return this.container.findElement(By.cssSelector(".export-description")).getText();
    }

    /**
     * @return the identifier of the export job, to download its result through the REST API
     */
    public String getJobId()
    {
        String query = URI.create(getDownloadLink().getAttribute("href")).getRawQuery();
        return Arrays.stream(query.split("&"))
            .filter(parameter -> parameter.startsWith(JOB_ID_PARAMETER))
            .map(parameter -> URLDecoder.decode(parameter.substring(JOB_ID_PARAMETER.length()),
                StandardCharsets.UTF_8))
            .findFirst()
            .orElseThrow();
    }

    private WebElement getDownloadLink()
    {
        return this.container.findElement(By.cssSelector("a.download"));
    }
}
