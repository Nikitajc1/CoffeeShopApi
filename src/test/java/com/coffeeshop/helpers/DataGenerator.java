package com.coffeeshop.helpers;

import net.datafaker.Faker;

import java.util.Random;
import java.util.random.RandomGenerator;

public class DataGenerator {
    private final Faker faker = new Faker(new Random(0));

    private final String nameOfUserOne = faker.name().maleFirstName();
    private final String nameOfUserTwo = faker.name().femaleFirstName();
    private final String[] status = {
            "NEW", "IN_PROGRESS", "COMPLETED"
    };
    private final String descriptionOne = faker.text().text(10);
    private final String descriptionTwo = faker.text().text(15);
    private final String descriptionThree = faker.text().text(12);
    private final String emailOne = faker.internet().emailAddress();
    private final String emailTwo = faker.internet().emailAddress();

    public String getNameOfUserOne() {
        return nameOfUserOne;
    }

    public String getNameOfUserTwo() {
        return nameOfUserTwo;
    }

    public String getStatus() {
        return status[RandomGenerator.getDefault().nextInt(status.length)];
    }

    public String getDescriptionOne() {
        return descriptionOne;
    }

    public String getEmailOne() {
        return emailOne;
    }

    public String getEmailTwo() {
        return emailTwo;
    }

    public String getDescriptionTwo() {
        return descriptionTwo;
    }

    public String getDescriptionThree() {
        return descriptionThree;
    }
}
