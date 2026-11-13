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
package org.xwiki.contrib.livedata.exporter.test.ui;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.xwiki.contrib.livedata.exporter.test.po.LiveDataExport;
import org.xwiki.contrib.livedata.exporter.test.po.LiveDataExporterPanel;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.WikiReference;
import org.xwiki.test.docker.junit5.UITest;
import org.xwiki.test.docker.junit5.WikisSource;
import org.xwiki.test.ui.TestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Functional tests of the export of a Live Data.
 *
 * @version $Id$
 */
@UITest
class LiveDataExportIT
{
    private static final String LIVE_DATA_ID = "exported";

    private static final String MATCHING_TITLE = "matching page";

    /**
     * Export a Live Data whose filter comes from the macro parameters, on two subwikis that each have the application
     * installed.
     */
    @ParameterizedTest
    @WikisSource(value = 2, mainWiki = false, extensions = {
        // Provides the XWiki.LiveTableResults page that the liveTable source of the Live Data uses.
        "org.xwiki.platform:xwiki-platform-livetable-ui/${platform.version}",
        "org.xwiki.contrib:application-livedata-exporter-ui"
    })
    @Order(1)
    void exportOnSeveralWikis(WikiReference wiki, TestUtils setup) throws Exception
    {
        setup.loginAsSuperAdmin();
        // The export result can only be downloaded by the user who started the export.
        setup.setDefaultCredentials(TestUtils.SUPER_ADMIN_CREDENTIALS);

        String wikiName = wiki.getName();
        setup.rest().savePage(new DocumentReference(wikiName, "Matching", "WebHome"), "",
            wikiName + " " + MATCHING_TITLE);
        setup.rest().savePage(new DocumentReference(wikiName, "Other", "WebHome"), "", wikiName + " other page");
        DocumentReference liveDataPage = new DocumentReference(wikiName, "Export", "WebHome");
        // The filter of the macro parameters has no operator.
        setup.rest().savePage(liveDataPage, "{{liveData id=\"" + LIVE_DATA_ID
            + "\" properties=\"doc.title,doc.location\" source=\"liveTable\""
            + " sourceParameters=\"translationPrefix=platform.index.\" filters=\"doc.location=Matching\"/}}", "");
        // The URL of a page of a subwiki has to be built since TestUtils#gotoPage(EntityReference) ignores the wiki.
        setup.gotoPage(setup.getBaseBinURL(wikiName) + "view/Export/");

        LiveDataExport export = LiveDataExporterPanel.open(LIVE_DATA_ID).startExport().waitUntilFinished();
        assertEquals("Properties\nTitle, Location\nFilters\nLocation: Matching\nSorting\nTitle in ascending order",
            export.getDescription());

        String baseURL = StringUtils.removeEnd(setup.rest().getBaseURL(), "rest");
        String csv = setup.getString(baseURL, "rest/liveData/export/csv", Map.of("jobId", export.getJobId()));
        // Only the page of the current wiki matching the filter is exported.
        assertEquals(1, StringUtils.countMatches(csv, MATCHING_TITLE), csv);
        assertTrue(csv.contains(wikiName + " " + MATCHING_TITLE), csv);
    }
}
