#!/bin/bash

# Deployment script for Microservices
# This script automates the deployment process as described in the documentation

# Set up logging
LOG_FILE="deployment_$(date +"%Y-%m-%d_%H-%M-%S").log"
exec > >(tee -a "$LOG_FILE") 2>&1

echo "Starting deployment process at $(date)"
echo "----------------------------------------"

# Function to display usage information
function show_usage {
    echo "Usage: $0 [options]"
    echo "Options:"
    echo "  -h, --help                 Show this help message"
    echo "  -s, --services SERVICE1,SERVICE2,...  Specify services to deploy (comma-separated)"
    echo "  -b, --backup               Perform database backup before deployment"
    echo "  -p, --prune                Remove unused Docker images"
    echo ""
    echo "Example: $0 --backup --services website-service,chess-service"
}

# Function to backup databases
function backup_databases {
    echo "Backing up databases..."
    
    # Create backup directory if it doesn't exist
    mkdir -p /data/db-backup
    
    # Chess database backup
    echo "Backing up chess database..."
    docker exec microservices-chess-db-1 mysqldump -u root -p${CHESS_DB_ROOT_PASSWORD} chess | gzip -c > /data/db-backup/chess_$(date +"%Y-%m-%d_%H-%M").sql.gz
    
    # Music database backup
    echo "Backing up music database..."
    docker exec microservices-music-db-1 mysqldump -u root -p${MUSIC_DB_ROOT_PASSWORD} music | gzip -c > /data/db-backup/music_$(date +"%Y-%m-%d_%H-%M").sql.gz
    
    # Fitness database backup
    echo "Backing up fitness database..."
    docker exec microservices-fitness-db-1 mysqldump -u root -p${FITNESS_DB_ROOT_PASSWORD} fitness | gzip -c > /data/db-backup/fitness_$(date +"%Y-%m-%d_%H-%M").sql.gz
    
    # User management database backup
    echo "Backing up usermanagement database..."
    docker exec microservices-usermanagement-db-1 mysqldump -u root -p${USERMANAGEMENT_DB_ROOT_PASSWORD} usermanagement | gzip -c > /data/db-backup/usermanagement_$(date +"%Y-%m-%d_%H-%M").sql.gz
    
    # Authentication database backup
    echo "Backing up authentication database..."
    docker exec microservices-authentication-db-1 mysqldump -u root -p${AUTHENTICATION_DB_ROOT_PASSWORD} authentication | gzip -c > /data/db-backup/authentication_$(date +"%Y-%m-%d_%H-%M").sql.gz
    
    echo "Database backups completed."
}

# Function to shut down services
function shutdown_services {
    echo "Shutting down specified services: $SERVICES"
    docker compose down $SERVICES
}

# Function to pull latest changes from git
function pull_changes {
    echo "Fetching latest changes from git..."
    git fetch
    
    echo "Pulling latest changes from git..."
    git pull
    
    if [ $? -ne 0 ]; then
        echo "Error: Failed to pull changes from git. Aborting deployment."
        exit 1
    fi
}

# Function to prune unused Docker images
function prune_images {
    echo "Removing unused Docker images..."
    docker image prune -a -f
}

# Function to start services
function start_services {
    echo "Starting specified services: $SERVICES"
    docker compose up -d --no-recreate $SERVICES
}

# Parse command line arguments
DO_BACKUP=false
DO_PRUNE=false
SERVICES=""

while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_usage
            exit 0
            ;;
        -s|--services)
            SERVICES=${2//,/ }
            shift 2
            ;;
        -b|--backup)
            DO_BACKUP=true
            shift
            ;;
        -p|--prune)
            DO_PRUNE=true
            shift
            ;;
        *)
            echo "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Validate arguments
# Load environment variables
if [ -f .env ]; then
    echo "Loading environment variables from .env file..."
    source .env
else
    echo "Error: .env file not found. This file is required for database credentials."
    exit 1
fi

# Execute deployment steps
echo "Starting deployment process..."

# Step 1: Backup databases if requested
if [ "$DO_BACKUP" = true ]; then
    backup_databases
fi

# Step 2: Shut down services
shutdown_services

# Step 3: Pull latest changes
pull_changes

# Step 4: Prune images if requested
if [ "$DO_PRUNE" = true ]; then
    prune_images
fi

# Step 5: Start services
start_services

echo "----------------------------------------"
echo "Deployment completed successfully at $(date)"
echo "Deployment log saved to $LOG_FILE"