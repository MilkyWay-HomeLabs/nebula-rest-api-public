package org.derleta.nebula.testcontainer;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.nio.file.Path;

public class FileServerTestContainer extends GenericContainer<FileServerTestContainer> {

    public FileServerTestContainer() {
        this(null);
    }

    public FileServerTestContainer(Path sharedRoot) {
        super(DockerImageName.parse("nginx:alpine"));

        withExposedPorts(80);

        if (sharedRoot != null) {
            withFileSystemBind(
                    sharedRoot.toAbsolutePath().toString(),
                    "/usr/share/nginx/html",
                    BindMode.READ_ONLY);
        }

        waitingFor(
                Wait.forHttp("/")
                        .forStatusCodeMatching(status -> status == 200 || status == 403)
        );
    }

    public String getBaseUrl() {
        return "http://" + getHost() + ":" + getMappedPort(80);
    }
}