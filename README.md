# Logical clocks

# Implementation

## VectorClock

The implementation of the vector clock was pretty straightforward except for
the *update* method, which required access to the keys of both clocks and that
information was private to each of them. My solution to this issue actually
breaks the Clock interface since it now depends on a VectorClock specific
method called *getKnownKeys* which returns the set of the keys in the clock
hashmap. Both keysets are merged and then the update is done using the full set
of keys. I found another way to implement it that involved parsing the string
representation of the clock to extract the keys from it but that solution
seemed even less elegant that the one I ended up using.

## Client

The client Class is the main entrypoint to the program, it receives the
username as a parameter and sets everything in motion. It is in charge of
orchestrating and connecting all of the other components in the program, with
tasks such as sending and receiving messages to the message handler, sending
tasks to the outputWriter and receiving data from the input reader.

## MessageHandler

This is the class where all the magic happens. It has the VectorClock and the
message PriorityQueue, sends messages to the server and has thread that receives
the messages.

## OutputWriter

This class is pretty much the same as I used in Lab 2 to build the user
interface.

## InputReader

This one is also very similar to the one previously used.
