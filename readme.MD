# Mockup Project

Welcome to My Project! This is a brief description of what the project is and what it does.

## Overview

This project is based to help developers to create tests and mocks, to avoid boiler plate classes like mock builders

## Key Features

- Create mocks for your tests

## Prerequisites

- Java 11 or higher

## Example
````agsl
Map<String, Object> values = new HashMap<>();
values.put("name", "Test");
values.put("age", 42);
Person sample = Mockup.createMock(Person.class, values);
`````

````agsl
Map<String, Object> values = new HashMap<>();
values.put("name", "Test");
values.put("age", 42);
values.put("job.salary", new BigDecimal("1000.00"));
Person sample = Mockup.createMock(Person.class, values);
````

