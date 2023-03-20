package my.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CreditApplication {
    private static final Logger log = LogManager.getLogger(CreditApplication.class);
    public static final String REGEX_FOR_LAST_NAME = "[A-Za-z]{2,}([-]{1}[a-zA-Z]+)?";
    public static final String REGEX_FOR_FIRST_NAME_SURNAME = "[A-Za-z]{2,}";
    public static final String REGEX_FOR_PASSPORT = "[A-Za-z]{2}[0-9]{7}";
    public static final int ACCEPTED_AGE = 18;
    private String errMessage = "";
    private String firstName;
    private String lastName;
    private String surname;
    private String passNumber;
    private String dateOfBirth;
    private String amount;
    private String term;
    private Integer age;

    public CreditApplication(String firstName, String lastName, String surname, String passNumber, String dateOfBirth, String amount, String term) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.passNumber = passNumber;
        this.amount = amount;
        this.term = term;
    }

    public boolean dataValidation() {
        if (isEmptyField()) {
            log.debug("Empty field received");
            errMessage = "Empty field received";
            return false;
        }
        if (isWrongNameField()) {
            log.debug("Wrong string field");
            errMessage = "Wrong first name, last name or surname: " + firstName + lastName + surname;
            return false;
        }
        if (isWrongPassNumber()) {
            log.debug("Wrong passport number");
            errMessage = "Wrong passport number: " + passNumber;
            return false;
        }
        if (isWrongNumberField()) {
            log.debug("Wrong number field");
            errMessage = "Wrong amount or term";
            return false;
        }
        if (isWrongDate()) {
            log.debug("Wrong date");
            errMessage = "Wrong date. " + errMessage;
            return false;
        }
        return true;
    }

    private boolean isEmptyField() {
        return firstName.isEmpty() | lastName.isEmpty() | surname.isEmpty() | dateOfBirth.isEmpty()
                | passNumber.isEmpty() | amount.isEmpty() | term.isEmpty();
    }

    private boolean isWrongNumberField() {
        Integer intAmount;
        Integer intTerm;
        try {
            intAmount = Integer.valueOf(amount);
            intTerm = Integer.valueOf(term);
        } catch (NumberFormatException e) {
            return true;
        }
        return (intAmount <= 0) | (intTerm <= 0);
    }

    private boolean isWrongNameField() {
        return !(lastName.matches(REGEX_FOR_LAST_NAME)&&firstName.matches(REGEX_FOR_FIRST_NAME_SURNAME)&&surname.matches(REGEX_FOR_FIRST_NAME_SURNAME));
    }

    private boolean isWrongPassNumber() {
        return !passNumber.matches(REGEX_FOR_PASSPORT);
    }
    private boolean isWrongDate() {
        LocalDate birthDay = null;
        try {
            birthDay = LocalDate.parse(dateOfBirth, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format");
            errMessage = "Invalid date format for birth day = " + dateOfBirth;
            return true;
        }
        if (!birthDay.isBefore(LocalDate.now())) {
            System.out.println("BirthDay must be in the past");
            errMessage = "Birth day must be in the past: " + dateOfBirth;
            return true;
        }
        calculateAge(birthDay);
        if (age < ACCEPTED_AGE) {
            System.out.println("You are too yong to get a credit");
            errMessage = "You are too yong to get a credit";
            return true;
        }
        return false;
    }

    private void calculateAge(LocalDate birthDay) {
        this.age = Integer.valueOf(Period.between(birthDay, LocalDate.now()).getYears());
    }

    @Override
    public String toString() {
        return "CreditApplication{" +
                "errMessage='" + errMessage + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", surname='" + surname + '\'' +
                ", passNumber='" + passNumber + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", amount=" + amount +
                ", term=" + term +
                '}';
    }

    public String getErrMessage() {
        return errMessage;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassNumber() {
        return passNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getAmount() {
        return amount;
    }

    public String getTerm() {
        return term;
    }
    public String getSurname() {
        return surname;
    }

    public Integer getAge() {
        return age;
    }
}
