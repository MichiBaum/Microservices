#!/bin/bash

# This script is used as a manual-auth-hook for certbot to add DNS TXT records
# for domain validation using the hosttech API.

# Exit on error
set -e

# Source the configuration file
SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"
source "${SCRIPT_DIR}/hosttech-config.sh"

# Use the configuration variables
API_TOKEN="${HOSTTECH_API_TOKEN}"
API_URL="${HOSTTECH_API_URL}"

# Certbot provides these environment variables:
# CERTBOT_DOMAIN: The domain being authenticated
# CERTBOT_VALIDATION: The validation string
# CERTBOT_TOKEN: Resource name part of the HTTP-01 challenge (unused for DNS)

# Extract the domain and validation string
DOMAIN="_acme-challenge.${CERTBOT_DOMAIN}"
VALIDATION="${CERTBOT_VALIDATION}"

# For wildcard certificates, the domain might include *. which needs to be handled
if [[ "${CERTBOT_DOMAIN}" == \*.* ]]; then
    # For wildcard domains, we need to strip the *. prefix
    BASE_DOMAIN="${CERTBOT_DOMAIN#\*.}"
else
    BASE_DOMAIN="${CERTBOT_DOMAIN}"
fi

echo "Adding TXT record for ${DOMAIN} with value ${VALIDATION}"

# First, get the zone ID for the domain
ZONE_RESPONSE=$(curl -s -X GET "${API_URL}/zones" \
    -H "Authorization: Bearer ${API_TOKEN}" \
    -H "Accept: application/json")

# Extract the zone ID for our domain
ZONE_ID=$(echo "${ZONE_RESPONSE}" | grep -o '"id":[0-9]*,"name":"'"${BASE_DOMAIN}"'"' | grep -o '[0-9]*')

if [ -z "${ZONE_ID}" ]; then
    echo "Error: Could not find zone ID for domain ${BASE_DOMAIN}"
    exit 1
fi

echo "Found zone ID: ${ZONE_ID}"

# Create the TXT record
CREATE_RESPONSE=$(curl -s -X POST "${API_URL}/zones/${ZONE_ID}/records" \
    -H "Authorization: Bearer ${API_TOKEN}" \
    -H "Content-Type: application/json" \
    -H "Accept: application/json" \
    -d '{
        "type": "TXT",
        "name": "_acme-challenge",
        "text": "'"${VALIDATION}"'",
        "ttl": 600
    }')

# Check if the record was created successfully
if echo "${CREATE_RESPONSE}" | grep -q '"id":[0-9]*'; then
    RECORD_ID=$(echo "${CREATE_RESPONSE}" | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
    echo "Successfully created TXT record with ID: ${RECORD_ID}"

    # Save the record ID for cleanup
    echo "${RECORD_ID}" > "/tmp/CERTBOT_${CERTBOT_DOMAIN}_RECORD_ID"
else
    echo "Error creating TXT record: ${CREATE_RESPONSE}"
    exit 1
fi

# Wait for DNS propagation
echo "Waiting for DNS propagation (${DNS_WAIT_TIME} seconds)..."
sleep ${DNS_WAIT_TIME}

echo "DNS record should be propagated now."
