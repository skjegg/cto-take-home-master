#!/bin/bash

# Check if curl is installed
if ! command -v curl &> /dev/null; then
    echo "Error: curl is not installed"
    exit 1
fi

# Submit the JSON file
curl -X POST \
     -H "Content-Type: application/json" \
     -d @submission.json \
     https://openings.automwrite.co.uk/apply

echo # Print newline after curl output
