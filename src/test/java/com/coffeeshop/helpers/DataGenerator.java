package com.coffeeshop.helpers;

import net.datafaker.Faker;
import java.util.Random;
import java.util.random.RandomGenerator;

public class DataGenerator {
    private final Faker faker = new Faker(new Random(RandomGenerator.getDefault().nextInt()));
    private StringBuilder builder;

    private final String[] status = {
            "NEW", "IN_PROGRESS", "COMPLETED"
    };

    public String getNameOfUserOne() {
        return faker.name().femaleFirstName();
    }

    public String getNameOfUserTwo() {
        return faker.name().femaleFirstName();
    }

    public String getStatus() {
        return status[RandomGenerator.getDefault().nextInt(status.length)];
    }

    public String getDescriptionOne() {
        return faker.text().text(10);
    }

    public String getEmailOne() {
        String email = faker.internet().emailAddress();
        builder = new StringBuilder(email);
        builder.insert(1, RandomGenerator.getDefault().nextInt());
        return builder.toString();
    }

    public String getEmailTwo() {
        String email = faker.internet().emailAddress();
        builder = new StringBuilder(email);
        builder.insert(1, RandomGenerator.getDefault().nextInt());
        return builder.toString();
    }

    public String getDescriptionTwo() {
        return faker.text().text(15);
    }

    public String getDescriptionThree() {
        return faker.text().text(12);
    }
}
