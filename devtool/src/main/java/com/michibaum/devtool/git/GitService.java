package com.michibaum.devtool.git;

import com.michibaum.devtool.core.Commands;
import com.michibaum.devtool.core.ShellWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class GitService {

    private final ShellWrapper shellWrapper;

    public String status(){
        try {
            return shellWrapper.execute(Commands.GIT_STATUS);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }


}
