package org.gradle.samples.internal.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Files;

public abstract class GenerateTestSource extends DefaultTask {
    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory();

    @TaskAction
    public void generate() {
        String content = "package org.gradle.samples;\n"
                + "\n"
                + "import org.gradle.samples.test.normalizer.FileSeparatorOutputNormalizer;\n"
                + "import org.gradle.samples.test.normalizer.JavaObjectSerializationOutputNormalizer;\n"
                + "import org.gradle.samples.test.normalizer.GradleOutputNormalizer;\n"
                + "import org.gradle.samples.test.runner.GradleSamplesRunner;\n"
                + "import org.gradle.samples.test.runner.SamplesOutputNormalizers;\n"
                + "import org.gradle.samples.test.runner.SamplesRoot;\n"
                + "import org.junit.runner.RunWith;\n"
                + "\n"
                + "@RunWith(GradleSamplesRunner.class)\n"
                + "@SamplesOutputNormalizers({\n"
                + "    JavaObjectSerializationOutputNormalizer.class,\n"
                + "    FileSeparatorOutputNormalizer.class,\n"
                + "    GradleOutputNormalizer.class\n"
                + "})\n"
                + "public class ExemplarExternalSamplesFunctionalTest {}\n";
        try {
            Files.write(getOutputDirectory().file("org/gradle/samples/ExemplarExternalSamplesFunctionalTest.java").get().getAsFile().toPath(), content.getBytes());
        } catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
