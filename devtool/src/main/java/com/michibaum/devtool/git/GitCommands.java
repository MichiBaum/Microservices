package com.michibaum.devtool.git;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@RequiredArgsConstructor
public class GitCommands {

    private final GitService gitService;

    @ShellMethod("Show Git Status")
    public String gitStatus() {
        return gitService.status();
    }

}
