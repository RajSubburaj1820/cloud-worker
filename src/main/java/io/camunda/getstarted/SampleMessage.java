package io.camunda.getstarted;

import io.camunda.zeebe.client.ZeebeClient;

import java.util.HashMap;
import java.util.Map;


public class SampleMessage {
    public static void main(String[] args) {

        Map map = new HashMap<>();
        map.put("hello","goodbye");

        ZeebeClient client = ZeebeClientFactory.getZeebeClient();
        client.newPublishMessageCommand().messageName("sampleMessage").correlationKey("123").variables(map).send().join();

    }
}
