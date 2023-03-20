package my.test;


import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/")
public class StartFormRestController {
    private static final Logger log = LogManager.getLogger(StartFormRestController.class);
    public static final String BPMN_PROCESS_ID = "credit_process";
    @Autowired
    private ZeebeClient zeebe;

    @PostMapping("/start")
    public void startProcessInstance(@RequestBody Map<String, Object> variables) {
        log.info("Starting process " + BPMN_PROCESS_ID + " with variables: " + variables);

        final ProcessInstanceEvent event = zeebe.newCreateInstanceCommand()
                .bpmnProcessId(BPMN_PROCESS_ID)
                .latestVersion()
                .variables(variables)
                .send()
                .join();

        log.debug("Workflow instance created. Key: {}", event.getProcessInstanceKey());

    }

    @PostMapping("/stop")
    public void interruptProcessInstance(@RequestBody Map<String, Object> variables) {
        String passNumber = (String) variables.get("passNumber");
        String message = (String) variables.get("message");
        log.debug("Cancel application for passport number: {}", passNumber);
        zeebe.newPublishMessageCommand()
                .messageName("cancel_application")
                .correlationKey(passNumber)
                .variables(Map.of("message", message))
                .send();

        log.debug("Stop end");

    }

}
