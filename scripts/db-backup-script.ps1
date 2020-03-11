# -------------------------------------------------------------------------
# Script: db-backup-script.ps1
# Author: Michael Baumberger
# Last updated: 11.03.2020 09:29
# -------------------------------------------------------------------------

# Copy h2 database folder to h2-backup
Copy-Item -Path C:\lifemanagement\backend\h2 -Destination C:\lifemanagement\backend\h2-backup -Recurse

# Get the current date
$DateStamp = get-date -uformat "%Y-%m-%d_%H-%M-%S"
# Rename Folder to $DateStamp
Rename-Item -Path C:\lifemanagement\backend\h2-backup\h2 -NewName $DateStamp