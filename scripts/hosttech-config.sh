#!/bin/bash

# Configuration file for hosttech API credentials
# This file should be sourced by the certbot hook scripts

# Replace with your actual API token
export HOSTTECH_API_TOKEN="YOUR_HOSTTECH_API_TOKEN"

# API URL
export HOSTTECH_API_URL="https://api.ns1.hosttech.eu/api/user/v1"

# DNS propagation wait time in seconds
export DNS_WAIT_TIME=60