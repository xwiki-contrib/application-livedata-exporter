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

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.xwiki.livedata.test.po.LiveDataElement;
import org.xwiki.test.ui.po.BaseElement;

/**
 * The export panel that the Live Data Exporter adds to a Live Data.
 *
 * @version $Id$
 * @since 1.1.5
 */
public class LiveDataExporterPanel extends BaseElement
{
    private static final String MENU_ENTRY_NAME = "Export…";

    private static final By EXPORT_ITEMS = By.cssSelector(".export-list > li");

    private final WebElement container;

    /**
     * @param container the element of the opened panel
     * @see #open(String)
     */
    protected LiveDataExporterPanel(WebElement container)
    {
        this.container = container;
    }

    /**
     * Open the export panel of a Live Data from its menu.
     *
     * @param liveDataId the id of the Live Data
     * @return the opened export panel
     */
    public static LiveDataExporterPanel open(String liveDataId)
    {
        // Wait for the Live Data to be ready.
        new LiveDataElement(liveDataId);

        WebElement liveData = getUtil().getDriver().findElement(By.id(liveDataId));
        liveData.findElement(By.cssSelector(".livedata-dropdown-menu .dropdown-toggle")).click();
        // The exporter registers its panel asynchronously, once its JavaScript is loaded.
        getUtil().getDriver().waitUntilCondition(driver -> findMenuEntry(liveData).isPresent());
        findMenuEntry(liveData).get().click();

        WebElement container =
            liveData.findElement(By.cssSelector(".livedata-advanced-panel-exporterPanel"));
        getUtil().getDriver().waitUntilCondition(driver -> container.isDisplayed());
        return new LiveDataExporterPanel(container);
    }

    private static Optional<WebElement> findMenuEntry(WebElement liveData)
    {
        return liveData.findElements(By.cssSelector(".livedata-dropdown-menu .dropdown-menu a")).stream()
            .filter(entry -> MENU_ENTRY_NAME.equals(entry.getText().trim()))
            .findFirst();
    }

    /**
     * Start an export of the Live Data, with its current configuration.
     *
     * @return the export added to the list of exports of the panel
     */
    public LiveDataExport startExport()
    {
        int exportCount = getExports().size();
        // The start button is the only button outside the list of exports.
        this.container.findElement(By.xpath(".//button[not(ancestor::ul[contains(@class, 'export-list')])]"))
            .click();
        getDriver().waitUntilCondition(driver -> getExports().size() > exportCount);
        return new LiveDataExport(getExports().get(exportCount));
    }

    private List<WebElement> getExports()
    {
        return this.container.findElements(EXPORT_ITEMS);
    }
}
