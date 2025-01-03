# env

The Services running in docker need environment variables. These variables are mostly secrets.
The variables are in in the .env files and are given to the containers. To know what variables have to be set there is a env-template file with all variables and placeholder values.
For local development you sometimes also need secrets. Create a .local-env file in the root directory (it is git ignored) and add it to the run config as env file.
