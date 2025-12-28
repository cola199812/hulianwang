package com.outdoor.demo.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class VideoUtils {

    public static boolean generateCover(String videoUrl, String outputPath) {
        // ffmpeg -i input.mp4 -ss 00:00:01 -vframes 1 cover.jpg
        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-i");
        command.add(videoUrl);
        command.add("-ss");
        command.add("00:00:01");
        command.add("-vframes");
        command.add("1");
        command.add(outputPath);
        command.add("-y"); // overwrite

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            // Read output to prevent blocking
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // System.out.println(line); // logging
                }
            }
            
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
