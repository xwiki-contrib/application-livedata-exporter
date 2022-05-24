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
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Named;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.InstantiationStrategy;
import org.xwiki.component.descriptor.ComponentInstantiationStrategy;
import org.xwiki.contrib.livedata.exporter.LiveDataExportWriter;
import org.xwiki.livedata.LiveData;
import org.xwiki.livedata.LiveDataConfiguration;
import org.xwiki.livedata.LiveDataException;
import org.xwiki.livedata.LiveDataPropertyDescriptor;

/**
 * CSV exporter for Live Data.
 *
 * @version $Id$
 */
@Component
@Named("csv")
@InstantiationStrategy(ComponentInstantiationStrategy.PER_LOOKUP)
public class LiveDataCSVExportWriter implements LiveDataExportWriter
{
    private static final String ACTIONS_COLUMN = "_actions";

    private CSVPrinter csvPrinter;

    private List<String> visibleProperties;

    @Override
    public void initialize(File outputFile, LiveDataConfiguration configuration) throws LiveDataException
    {
        try {
            this.csvPrinter = new CSVPrinter(new FileWriter(outputFile), CSVFormat.DEFAULT);

            Map<String, String> idToPropertyName = configuration.getMeta().getPropertyDescriptors().stream()
                .filter(LiveDataPropertyDescriptor::isVisible)
                .collect(Collectors.toMap(LiveDataPropertyDescriptor::getId,
                    property -> property.getName() == null ? property.getId() : property.getName()));

            this.visibleProperties = configuration.getQuery().getProperties().stream()
                .filter(Predicate.isEqual(ACTIONS_COLUMN).negate())
                .filter(idToPropertyName::containsKey)
                .collect(Collectors.toList());

            // Print header
            this.csvPrinter.printRecord(
                visibleProperties.stream()
                    .map(idToPropertyName::get)
                    .collect(Collectors.toList())
            );
        } catch (IOException e) {
            throw new LiveDataException("Opening or writing the export file failed", e);
        }
    }

    @Override
    public void write(LiveData data) throws LiveDataException
    {
        try {
            for (Map<String, Object> entry : data.getEntries()) {
                this.csvPrinter.printRecord(
                    this.visibleProperties.stream()
                        .map(entry::get)
                        .collect(Collectors.toList())
                );
            }
        } catch (IOException e) {
            throw new LiveDataException("Exporting entries failed", e);
        }
    }

    @Override
    public void close() throws IOException
    {
        this.csvPrinter.close();
    }
}
