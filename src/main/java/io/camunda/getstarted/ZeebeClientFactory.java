package io.camunda.getstarted;

import io.camunda.zeebe.client.ZeebeClient;


public class ZeebeClientFactory {

  public static ZeebeClient getZeebeClient() {

    //return ZeebeClient.newClientBuilder().gatewayAddress("127.0.0.1:26500").usePlaintext().build();

    return ZeebeClient.newCloudClientBuilder()
        .withClusterId("45cf5a2e-cee0-462f-b676-7c4330a9ce8a")
        .withClientId("64GOzVW~gJSykbl~aFlnfhf7418UBIvG")
        .withClientSecret("GHMFhQqOnydO0Vkaw7sfkKx-pwp05wPAgh973x1eCrLJxhR2bfJeNexVChMMGgi3")
        .withRegion("bru-2")
        .build();

  
  }

}
