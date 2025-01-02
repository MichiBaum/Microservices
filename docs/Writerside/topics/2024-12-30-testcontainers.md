# 2024-12-30 Testcontainers

Testing with h2 gives many problems like naming tables because of reserved keywords and flyways schema.
It also doesn't really represent the prod environment with mariadb. Therefore, the decision is made to use testcontainers.
This really hurts the performance of integration tests. The best solution I found is  to spin up a container for a testclass (default)
and before every test do a `flyway.clean()` & `flyway.migrate()` with a junit extensions. This ensures the database is in perfect condition for the test.
The disadvantage is that a test takes about 1 Second which is waaaayy to much even for an integration test. But I think this issue is for future me :)
Maybe the solution is to write less integration tests xD