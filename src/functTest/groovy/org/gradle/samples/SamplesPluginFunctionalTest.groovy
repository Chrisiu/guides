package org.gradle.samples

import org.gradle.testkit.runner.BuildResult

import java.nio.file.Files

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class SamplesPluginFunctionalTest extends AbstractSampleFunctionalSpec {
    def "demonstrate publishing samples to directory"() {
        makeSingleProject()
        writeSampleUnderTest()
        buildFile << """
def publishTask = tasks.register("publishSamples", Copy) {
    // TODO: The `gradle-samples` directory is an implementation detail
    from("build/gradle-samples")
    into("build/docs/samples")
}

tasks.assemble.dependsOn publishTask
"""

        when:
        def result = build('assemble')

        then:
        result.task(":generateSampleIndex").outcome == SUCCESS
        result.task(":asciidocSampleIndex").outcome == SUCCESS
        result.task(":assemble").outcome == SUCCESS
        assertSampleTasksExecutedAndNotSkipped(result)
        groovyDslZipFile.exists()
        kotlinDslZipFile.exists()
        getGroovyDslZipFile(buildDirectoryRelativePath: "docs/samples").exists()
        getKotlinDslZipFile(buildDirectoryRelativePath: "docs/samples").exists()
        new File(projectDir, "build/docs/samples/demo/index.html").exists()
        new File(projectDir, "build/docs/samples/index.html").exists()
    }

    def "can generate content for the sample"() {
        makeSingleProject()
        writeSampleUnderTest()
        buildFile << '''
samples.configureEach { sample ->
    def generatorTask = tasks.register("generateContentFor${sample.name.capitalize()}Sample") {
        outputs.dir(layout.buildDirectory.dir("sample-contents/${sample.name}"))
        doLast {
            layout.buildDirectory.dir("sample-contents/${sample.name}/gradle.properties").get().asFile.text = "foo.bar = foobar\\n"
        }
    }
    sample.archiveContent.from(files(generatorTask))
}
'''

        when:
        def result = build("assembleDemoSample")

        then:
        assertSampleTasksExecutedAndNotSkipped(result)
        result.task(":generateContentForDemoSample").outcome == SUCCESS
        assertZipHasContent(groovyDslZipFile, "gradlew", "gradlew.bat", "gradle.properties", "gradle/wrapper/gradle-wrapper.properties", "gradle/wrapper/gradle-wrapper.jar", "README.adoc", "build.gradle", "settings.gradle")
        assertZipHasContent(kotlinDslZipFile, "gradlew", "gradlew.bat", "gradle.properties", "gradle/wrapper/gradle-wrapper.properties", "gradle/wrapper/gradle-wrapper.jar", "README.adoc", "build.gradle.kts", "settings.gradle.kts")
    }

    def "can have two sample with different naming convention"() {
        buildFile << """
            plugins {
                id 'org.gradle.samples'
            }

            samples {
                "foo-bar"
                "fooBar"
            }
        """
        writeGroovyDslSample("src/samples/foo-bar")
        writeKotlinDslSample("src/samples/foo-bar")
        writeGroovyDslSample("src/samples/fooBar")
        writeKotlinDslSample("src/samples/fooBar")

        when:
        build("help")

        then:
        noExceptionThrown()
    }

    def "fails when settings.gradle.kts is missing from Kotlin DSL sample"() {
        makeSingleProject()
        writeGroovyDslSample("src/samples/demo")
        Files.move(new File(temporaryFolder.root, "src/samples/demo/groovy").toPath(), new File(temporaryFolder.root, "src/samples/demo/kotlin").toPath())

        when:
        def result = buildAndFail("assemble")

        then:
        result.output.contains("Execution failed for task ':installDemoKotlinDslSample'.")
        result.output.contains("Sample 'demo' for Kotlin DSL is invalid due to missing 'settings.gradle.kts' file.")
    }

    def "fails when settings.gradle is missing from Groovy DSL sample"() {
        makeSingleProject()
        writeKotlinDslSample("src/samples/demo")
        Files.move(new File(temporaryFolder.root, "src/samples/demo/kotlin").toPath(), new File(temporaryFolder.root, "src/samples/demo/groovy").toPath())

        when:
        def result = buildAndFail("assemble")

        then:
        result.output.contains("Execution failed for task ':installDemoGroovyDslSample'.")
        result.output.contains("Sample 'demo' for Groovy DSL is invalid due to missing 'settings.gradle' file.")
    }

    def "can call sample dsl configuration multiple time"() {
        makeSingleProject()
        writeSampleUnderTest()
        buildFile << """
            ${sampleUnderTestDsl} {
                withGroovyDsl()
                withGroovyDsl()
                withKotlinDsl()
                withKotlinDsl()
            }
        """

        when:
        build('help')

        then:
        noExceptionThrown()
    }

    // TODO: Allow preprocess build script files before zipping (remove tags, see NOTE1) or including them in rendered output (remove tags and license)
    //   NOTE1: We can remove the license from all the files and add a LICENSE file at the root of the sample

    protected void makeSingleProject() {
        buildFile << """
            plugins {
                id 'org.gradle.samples'
            }

            samples {
                demo
            }
        """
    }

    protected void writeSampleUnderTest() {
        writeSampleContentToDirectory('src/samples/demo') << """
ifndef::env-github[]
- link:{zip-base-file-name}-groovy-dsl.zip[Download Groovy DSL ZIP]
- link:{zip-base-file-name}-kotlin-dsl.zip[Download Kotlin DSL ZIP]
endif::[]
"""
        writeGroovyDslSample("src/samples/demo");
        writeKotlinDslSample("src/samples/demo")
    }

    protected static void assertSampleTasksExecutedAndNotSkipped(BuildResult result) {
        assertBothDslSampleTasksExecutedAndNotSkipped(result);
    }
}
