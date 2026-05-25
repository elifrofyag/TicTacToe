#!/bin/bash
############################################
# how to run
# start the server (make sure it's running on localhost:8080)
# navigate to src/test/java/org/ann and run:
# chmod +x load_test.sh
# ./load_test.sh
############################################

# Configuration
HOST="127.0.0.1"
PORT=8080
CONNECTIONS=2000

echo "Flooding $HOST:$PORT with $CONNECTIONS connections..."

# Run the loop to spawn connections
for ((i=1; i<=CONNECTIONS; i++)); do
    (
        # Open a TCP connection using bash's built-in /dev/tcp
        exec 3<>/dev/tcp/$HOST/$PORT 2>/dev/null
        if [ $? -eq 0 ]; then
            # Hold the connection open for 10 seconds to simulate an active game
            sleep 10
            # Close the connection safely
            exec 3<&-
            exec 3>&-
        fi
    ) & # The '&' sends this subshell to the background to run concurrently

    # Print progress so you know it hasn't frozen
    if (( i % 500 == 0 )); then
        echo "✅ $i connections launched..."
    fi

    # Optional: slight throttle to prevent your OS from crashing the script itself
    # sleep 0.001
done

echo "all $CONNECTIONS connection attempts launched. Waiting for them to finish..."
wait
echo "Load test complete!"