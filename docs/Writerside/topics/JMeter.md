# JMeter

JMeter is used for performance testing of the Microservices application. This document describes the JMeter test plan configuration used for testing the application's APIs.

## Test Plan Overview

The JMeter test plan "Microservices" is designed to test the performance and reliability of various API endpoints in the microservices architecture.


## Response Assertion

The test plan includes a response assertion that verifies all responses have a status code of 200 (OK). This ensures that all API calls are successful during the test.

## Result Collectors

The test plan includes several result collectors to analyze the test results:

### Summary Report

Provides a summary of the test results, including:
- Sample count
- Error count
- Error percentage
- Average response time
- Min/Max response times
- Throughput

### Aggregate Report

Provides detailed statistics for each sampler, including:
- Average
- Median
- 90% line
- 95% line
- 99% line
- Min/Max
- Error rate
- Throughput
- KB/sec

### Graph Results

Displays a graph of the test results over time, showing:
- Response times
- Throughput
- Deviation
- Median

### View Results Tree

Displays detailed information for each request, including:
- Request data
- Response data
- Response time
- Status

## Running the Test Plan

To run this test plan:

1. Open the Microservices.jmx file in JMeter
2. Click the Run button or press Ctrl+R
3. View the results in the various result collectors

## Performance Considerations

When analyzing the test results, pay attention to:

- Average response time
- Error rate
- Throughput
- 90% and 95% response time percentiles

These metrics will help identify performance bottlenecks and areas for improvement in the microservices architecture.
