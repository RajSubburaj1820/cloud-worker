package io.camunda.getstarted;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.ZeebeClient;

import java.util.Map;
import java.util.Scanner;

import io.grpc.internal.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SampleWorker {

  private static final Logger LOG = LogManager.getLogger(SampleWorker.class);

  public static void main(String[] args) {
    try (ZeebeClient client = ZeebeClientFactory.getZeebeClient()) {
      client.newPublishMessageCommand();
      client.newWorker().jobType("emailService").handler((jobClient, job) -> {

        Map<String, Object> vars = job.getVariablesAsMap();

        Map<String, Object> app = (Map) vars.get("application");

        Map<String, Object> appl = (Map) app.get("applicant");

        String email = appl.get("email").toString();

        System.out.println("email is "+appl.get("email"));

        String reply = "{\"return\": \"Email Sent!\"}";


        if (email.contains("@")) {
          jobClient.newCompleteCommand(job.getKey()).variables(reply).send()
                  // join(); <-- This would block for the result. While this is easier-to-read code, it has limitations for parallel work.
                  // Hence, the following code leverages reactive programming. This is discussed in https://blog.bernd-ruecker.com/writing-good-workers-for-camunda-cloud-61d322cad862.
                  .whenComplete((result, exception) -> {
                    if (exception == null) {
                      LOG.info("Completed job successful with result:" + result);
                    } else {
                      LOG.error("Failed to complete job", exception);
                    }
                  });
        } else {
          jobClient.newThrowErrorCommand(job.getKey()).errorCode("Malformed email, missing domain '@'").send();
        }

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