package org.gradle.docs.samples.internal;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.docs.internal.components.RenderableContentComponent;
import org.gradle.docs.internal.components.TestableRenderedContentLinksComponent;
import org.gradle.docs.internal.components.ViewableContentComponent;
import org.gradle.docs.internal.components.NamedDocumentationComponent;
import org.gradle.docs.samples.SampleSummary;

import javax.inject.Inject;

import static org.gradle.docs.internal.StringUtils.capitalize;

public abstract class SampleContentBinary extends NamedDocumentationComponent implements ViewableContentComponent, TestableRenderedContentLinksComponent, RenderableContentComponent {
    @Inject
    public SampleContentBinary(String name) {
        super(name);
    }

    public abstract Property<String> getDisplayName();

    public abstract DirectoryProperty getSampleDirectory();

    public abstract Property<String> getBaseName();

    public abstract Property<String> getRenderedPermalink();

    public abstract Property<String> getSourcePermalink();

    public abstract RegularFileProperty getIndexPageFile();

    public abstract RegularFileProperty getRenderedIndexPageFile();

    public abstract Property<SampleSummary> getSummary();

    public abstract DirectoryProperty getSampleInstallDirectory();

    @Override
    public String getViewTaskName() {
        return "view" + capitalize(getName()) + "Sample";
    }

    @Override
    public String getCheckLinksTaskName() {
        return "check" + capitalize(getName()) + "SampleLinks";
    }

    public abstract RegularFileProperty getSourcePageFile();

    public abstract RegularFileProperty getInstalledIndexPageFile();

    public abstract Property<String> getGradleVersion();
}