package io.camunda.getstarted;

import io.camunda.zeebe.client.ZeebeClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SampleMicroservice {

  private static final Logger LOG = LogManager.getLogger(SampleMicroservice.class);

  public static void main(String[] args) {
    try (ZeebeClient client = ZeebeClientFactory.getZeebeClient()) {
        
      // subscribe to type
      client.newWorker().jobType("my_microservice").handler((jobClient, job) -> {

         // your code goes here
         System.out.println("You're in a microservice!");
         Map map = new HashMap<>();
         map.put("hello","goodbye");

         //client.newPublishMessageCommand().messageName("sample_start_event").correlationKey("").variables(map).send().join();

         // complete the task
         jobClient.newCompleteCommand(job.getKey()).variables(map).send()
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