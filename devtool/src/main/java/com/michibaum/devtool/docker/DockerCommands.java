package com.michibaum.devtool.docker;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;


@ShellComponent
@RequiredArgsConstructor
public class DockerCommands {

    private final DockerService dockerService;

    @ShellMethod("Show Docker Containers")
    public String dockerContainers() {
        return dockerService.listDockerContainers();
    }

}
