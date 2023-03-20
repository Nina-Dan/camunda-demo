package my.test;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@SpringBootApplication
@EnableZeebeClient
public class DemoCamundaApplication {
    private static final Logger log = LogManager.getLogger(DemoCamundaApplication.class);
    public static final String BPMN_NAME = "java-test-worker.bpmn";

    public static void main(String[] args) {

        ConfigurableApplicationContext applicationContext = SpringApplication.run(DemoCamundaApplication.class, args);

        String clusterId = applicationContext.getEnvironment().getProperty("zeebe.client.cloud.clusterId");
        String clientId = applicationContext.getEnvironment().getProperty("zeebe.client.cloud.clientId");
        String clientSecret = applicationContext.getEnvironment().getProperty("zeebe.client.cloud.clientSecret");
        String region = applicationContext.getEnvironment().getProperty("zeebe.client.cloud.region");

        ZeebeClient client = ZeebeClient.newCloudClientBuilder()
                .withClusterId(clusterId)
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .withRegion(region)
                .build();

        DeploymentEvent deployment = client.newDeployResourceCommand()// newDeployCommand()
                .addResourceFromClasspath(BPMN_NAME)
                .send()
                .join();

        log.info("Current version = {}", deployment.getProcesses().get(0).getVersion());

    }

    @JobWorker(type = "CheckInputData")
    public void CheckInputData(final JobClient client, final ActivatedJob job) {

        Map<String, Object> variablesAsMap = job.getVariablesAsMap();
        String firstName = (String) variablesAsMap.get("firstName");
        String lastName = (String) variablesAsMap.get("lastName");
        String surname = (String) variablesAsMap.get("surname");
        String passNumber = (String) variablesAsMap.get("passNumber");
        String dateOfBirth = (String) variablesAsMap.get("dateOfBirth");
        String amount = (String) variablesAsMap.get("amount");
        String term = (String) variablesAsMap.get("term");
        CreditApplication newCreditApplication = new CreditApplication(firstName, lastName, surname, passNumber, dateOfBirth, amount, term);
        log.debug("newCreditApplication = {}", newCreditApplication);

        if (newCreditApplication.dataValidation()) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("age", newCreditApplication.getAge());
            variables.put("amount", Integer.valueOf(newCreditApplication.getAmount()));
            variables.put("term", Integer.valueOf(newCreditApplication.getTerm()));
            log.debug("Variables to send: {} ", variables);
            client.newCompleteCommand(job.getKey())
                    .variables(variables)
                    .send()
                    .exceptionally((throwable -> {
                        throw new RuntimeException("Could not complete job", throwable);
                    }));
        } else {
            log.debug("Wrong input data. {}", newCreditApplication.getErrMessage());
            client.newThrowErrorCommand(job.getKey())
                    .errorCode("Not_valid_input_data")
                    .errorMessage(newCreditApplication.getErrMessage())
                    .send()
                    .exceptionally((throwable -> {
                        throw new RuntimeException("Could not complete job", throwable);
                    }));
        }

    }

    @JobWorker(type = "GetDebtFlag")
    public void GetDebtFlag(final JobClient client, final ActivatedJob job) {
        Random rd = new Random();
        Boolean isDebt = rd.nextBoolean();
        log.debug("Debt flag generated {}", isDebt);
        client.newCompleteCommand(job.getKey())
                .variables(Map.of("isDebt", isDebt))
                .send()
                .exceptionally((throwable -> {
                    throw new RuntimeException("Could not complete job", throwable);
                }));

    }
}