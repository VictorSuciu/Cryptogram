# Cryptogram
This program attempts to crack substitution ciphers.

It usually solves one ~40 character cipher in under 10 seconds, but results vary a lot. Cryptograms of 20-60 characters took from 1-10 seconds to solve with outliers taking minutes. Solves caesars ciphers instantly.

Works in console with input/output.txt files. Uses serialized hashmap .txt file as well.

<br>

<br>

##Important Note

This program requires a LOT of memory due to the massive data structure it builds.
I have found that 7000 megabytes of heap space barely suffices and it probably will not finish building.
To run successfully, you will likely need a machine with at least 16 gigabytes of RAM.