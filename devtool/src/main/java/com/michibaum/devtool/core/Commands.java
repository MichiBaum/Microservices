package com.michibaum.devtool.core;

public enum Commands {

    DOCKER_CONTAINERS("docker ps -a"),
    GIT_STATUS("git status");

    private final String command;

    Commands(String command) {
        this.command = command;
    }

    public String getCommand(){
        return this.command;
    }

}
