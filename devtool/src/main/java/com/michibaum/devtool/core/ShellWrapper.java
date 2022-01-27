package com.michibaum.devtool.core;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class ShellWrapper {

    public static boolean isWindows(String os) {
        return os.contains("win");
    }

    public static boolean isMac(String os) {
        return os.contains("mac");
    }

    public static boolean isUnix(String os) {
        return (os.contains("nix") || os.contains("nux") || os.contains("aix"));
    }

    public static boolean isSolaris(String os) {
        return os.contains("sunos");
    }

    public String execute(Commands command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        String os = System.getProperty("os.name").toLowerCase();

        if (isWindows(os)) {
            processBuilder.command("cmd.exe", "/c", command.getCommand());
        } else if (isMac(os)) {
            throw new OsNotSupportedException();
        } else if (isUnix(os)) {
            processBuilder.command("bash", "-c", command.getCommand());
        } else if (isSolaris(os)) {
            throw new OsNotSupportedException();
        } else {
            throw new OsNotSupportedException();
        }

        Process process = processBuilder.start();
        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitVal = process.waitFor();
        if (exitVal == 0) {
            return output.toString();
        } else {
            throw new RuntimeException();
        }

    }

}
