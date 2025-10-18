#!/bin/bash

echo "===== Python virtual environment must be made in ~/pvenv ====="

COMMAND="source ~/pvenv/bin/activate; ${1:-python potka/testing.py}"
WORKING_DIR="${2:-/home/szymon/WebstormProjects/pokojowka-ale-mi-sie-udalo}"
INTERVAL=5

if [ ! -d "$WORKING_DIR" ]; then
    echo "Error: Directory '$WORKING_DIR' does not exist!"
    exit 1
fi

cleanup() {
    echo ""
    echo "Script stopped."
    exit 0
}

trap cleanup SIGINT SIGTERM

echo "Starting to execute '$COMMAND' every $INTERVAL seconds..."
echo "Working directory: '$WORKING_DIR'"
echo "Press Ctrl+C to stop"

while true; do
    echo "=== $(date) ==="
    echo "Working directory: $WORKING_DIR"
    
    if (cd "$WORKING_DIR" && eval $COMMAND); then
        echo "Command executed successfully from $WORKING_DIR"
    else
        echo "Error: Command failed with exit code $?"
    fi
    
    echo "Waiting $INTERVAL seconds..."
    echo "------------------------"
    sleep $INTERVAL
done
