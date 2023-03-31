package io.camunda.getstarted;

import io.camunda.zeebe.client.ZeebeClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Scanner;

public class SampleLocalWorker {

  private static final Logger LOG = LogManager.getLogger(SampleLocalWorker.class);

  public static void main(String[] args) {
    try (ZeebeClient client = ZeebeClientFactory.getZeebeClient()) {

      client.newWorker().jobType("createJira").handler((jobClient, job) -> {

          jobClient.newCompleteCommand(job.getKey()).send()
                  // join(); <-- This would block for the result. While this is easier-to-read code, it has limitations for parallel work.
                  // Hence, the following code leverages reactive programming. This is discussed in https://blog.bernd-ruecker.com/writing-good-workers-for-camunda-cloud-61d322cad862.
                  .whenComplete((result, exception) -> {
                    if (exception == null) {
                      LOG.info("Completed job successful with result:" + result);
                    } else {
                      LOG.error("Failed to complete job", exception);
                    }
                  });
      }).open();

      // run until System.in receives exit command
      waitUntilSystemInput("exit");
    }
  }

  private static void waitUntilSystemInput(final String exitCode) {
    try (final Scanner scanner = new Scanner(System.in)) {
      while (scanner.hasNextLine()) {
        final String nextLine = scanner.nextLine();
        if (nextLine.contains(exitCode)) {
          return;
        }
      }
    }
  }
}