#!/bin/bash

# This script is used as a manual-cleanup-hook for certbot to remove DNS TXT records
# after domain validation using the hosttech API.

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

# For wildcard certificates, the domain might include *. which needs to be handled
if [[ "${CERTBOT_DOMAIN}" == \*.* ]]; then
    # For wildcard domains, we need to strip the *. prefix
    BASE_DOMAIN="${CERTBOT_DOMAIN#\*.}"
else
    BASE_DOMAIN="${CERTBOT_DOMAIN}"
fi

# Get the record ID saved by the auth hook
RECORD_ID_FILE="/tmp/CERTBOT_${CERTBOT_DOMAIN}_RECORD_ID"

if [ ! -f "${RECORD_ID_FILE}" ]; then
    echo "Error: Record ID file not found: ${RECORD_ID_FILE}"
    exit 1
fi

RECORD_ID=$(cat "${RECORD_ID_FILE}")

echo "Removing TXT record with ID ${RECORD_ID} for domain ${CERTBOT_DOMAIN}"

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

# Delete the TXT record
DELETE_RESPONSE=$(curl -s -X DELETE "${API_URL}/zones/${ZONE_ID}/records/${RECORD_ID}" \
    -H "Authorization: Bearer ${API_TOKEN}" \
    -H "Accept: application/json")

# Check if the record was deleted successfully
if [ -z "${DELETE_RESPONSE}" ]; then
    echo "Successfully deleted TXT record with ID: ${RECORD_ID}"
    # Remove the temporary file
    rm -f "${RECORD_ID_FILE}"
else
    echo "Error deleting TXT record: ${DELETE_RESPONSE}"
    exit 1
fi
