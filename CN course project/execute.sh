#!/bin/bash

# Compile the first Java file
javac newserver.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "Compilation of newserver.java was successful."
    
    # Start the first Java program in a new terminal window
    gnome-terminal -- java -cp . newserver 8080
else
    echo "Compilation of YourFirstJavaFile.java failed."
fi

# Sleep for a moment to allow the first terminal to open
sleep 2

