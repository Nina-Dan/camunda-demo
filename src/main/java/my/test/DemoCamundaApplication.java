package my.test;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.exception.ZeebeBpmnError;
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
    public static final String ZEEBE_CLIENT_CLOUD_CLUSTER_ID = "zeebe.client.cloud.clusterId";
    public static final String ZEEBE_CLIENT_CLOUD_CLIENT_ID = "zeebe.client.cloud.clientId";
    public static final String ZEEBE_CLIENT_CLOUD_CLIENT_SECRET = "zeebe.client.cloud.clientSecret";
    public static final String ZEEBE_CLIENT_CLOUD_REGION = "zeebe.client.cloud.region";
    public static final String ERROR_CODE = "Not_valid_input_data";

    public static void main(String[] args) {

        ConfigurableApplicationContext applicationContext = SpringApplication.run(DemoCamundaApplication.class, args);

        String clusterId = applicationContext.getEnvironment().getProperty(ZEEBE_CLIENT_CLOUD_CLUSTER_ID);
        String clientId = applicationContext.getEnvironment().getProperty(ZEEBE_CLIENT_CLOUD_CLIENT_ID);
        String clientSecret = applicationContext.getEnvironment().getProperty(ZEEBE_CLIENT_CLOUD_CLIENT_SECRET);
        String region = applicationContext.getEnvironment().getProperty(ZEEBE_CLIENT_CLOUD_REGION);

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
    public Map<String, Object> CheckInputData(final JobClient client, final ActivatedJob job) {

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
            return variables;
        } else {
            log.debug("Wrong input data. {}", newCreditApplication.getErrMessage());
            throw new ZeebeBpmnError(ERROR_CODE, newCreditApplication.getErrMessage());
        }

    }

    @JobWorker(type = "GetDebtFlag")
    public Map<String, Object> GetDebtFlag(final JobClient client, final ActivatedJob job) {
        Random rd = new Random();
        Boolean isDebt = rd.nextBoolean();
        log.debug("Debt flag generated {}", isDebt);
        return Map.of("isDebt", isDebt);
    }
}