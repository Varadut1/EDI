#!/bin/bash

# Compile the first Java file
javac udpserver.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "Compilation of YourFirstJavaFile.java was successful."
    
    # Start the first Java program in a new terminal window
    gnome-terminal -- java -cp . udpserver
else
    echo "Compilation of YourFirstJavaFile.java failed."
fi

# Sleep for a moment to allow the first terminal to open
sleep 2

# Compile the second Java file
javac client.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "Compilation of YourSecondJavaFile.java was successful."
    
    # Start the second Java program in a new terminal window
    gnome-terminal -- java -cp . client 8080
else
    echo "Compilation of YourSecondJavaFile.java failed."
fi
