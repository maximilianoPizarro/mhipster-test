package com.mycompany.myapp.config;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import org.zalando.problem.jackson.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

@Factory
public class JacksonConfiguration {

    /*
     * Support for Hibernate types in Jackson.
     */
    @Singleton
    Hibernate6Module hibernate6Module() {
        return new Hibernate6Module();
    }

    /*
     * Module for serialization/deserialization of RFC7807 Problem.
     */
    @Singleton
    ProblemModule problemModule() {
        return new ProblemModule();
    }

    /*
     * Module for serialization/deserialization of ConstraintViolationProblem.
     */
    @Singleton
    ConstraintViolationProblemModule constraintViolationProblemModule() {
        return new ConstraintViolationProblemModule();
    }
}
