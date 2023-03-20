# Credit process Automatisation


## Prerequisites

To use this project, ensure you have the following requirements:

### Camunda Cloud account

Visit camunda.io and create a Camunda Cloud account. You'll also need to create a cluster and set up client connection credentials. [Follow this tutorial](https://docs.camunda.io/docs/guides/getting-started/) for guidance, and note that you can only have one cluster at a time.

### Camunda Desktop Modeler (optional)

Download [Camunda Desktop Modeler](https://camunda.com/download/modeler/) for all the usual platforms.

### Cloned repository

Clone this repository and open it in an integrated development environment (IDE) like Visual Studio Code, Atom, Eclipse, etc.

## Utilizing the microservice

Much of this project is based on the microservice that queries an API with the name of a celebrity and returns the age of that celebrity.

Set up this microservice by taking the steps below:

1. Navigate to your preferred IDE and open the cloned repository.
2. Within the repo, open up the `application.yml` file in `src/main/resources/`.
4. Add your Camunda Cloud API details to the `application.yaml` file. Follow our guidance on [getting the API details](https://docs.camunda.io/docs/guides/getting-started/setup-client-connection-credentials/) for assistance.
5. Save your updated `application.yml` file.
6. Start up the worker by running the `DemoCamundaApplication` class. 


Now you've deployed your process and started a process instance.

## Format of input data

{
"firstName": "AAAA",

"lastName": "AAAA",

"surname": "AAAA",

"passNumber": "AANNNNNNN",

"dateOfBirth": "YYYY-MM-DD",

"amount": "N",

"term":	"N"
}

First name, last name , surname - alpha.

Last name can be doubled (like AAA-BBB).

Passport number - alphanumeric.