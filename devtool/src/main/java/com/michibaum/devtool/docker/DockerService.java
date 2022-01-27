package com.michibaum.devtool.docker;

import com.michibaum.devtool.core.Commands;
import com.michibaum.devtool.core.ShellWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class DockerService {

    private final ShellWrapper shellWrapper;

    public String listDockerContainers() {
        try {
            return shellWrapper.execute(Commands.DOCKER_CONTAINERS);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

}
