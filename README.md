Content of the project
======================

The project contains the following files:

- ___jars/junit-4.10.jar___: JUnit jar to execute the test suite;

- ___Makefile___: makefile to use the project;

- ___README.md___: this file;

- ___src/CLISimulator.java___: code source of the command line interface simulator;

- ___src/MapCrdt.java___: code of the map CRDT;

- ___src/MapCrdtLogEntry.java___: code of the log entry used in the map CRDT;

- ___src/MapCrdtTest.java___: code of the test suite.


Makefile rules
==============

The makefile rules are as follows:

- 'make' to compile the project: classes are put into the src/ directory;

- 'make test' to launch the test suite;

- 'make demo' to launch the command line interface simulation;

- 'make docs' to create the javadoc: documentation will be put into the docs/ directory.


Description of the state-based map CRDT
=======================================

The assumptions are as follows:

- we have a unique global source of time;

- each replica has a unique identifier;

- keys and values are strings.

Thanks to the unique global source of time we are able to determine a total order for the different
operations. If two operations have the same date, then the one that happened on the replica with the
smallest id is considered as first.

The concurrency semantics of the map CRDT is as follows:

- PUT/PUT conflicts (i.e., PUT operations for the same key) are solved using a last-writer-wins
policy (LWW) (i.e., keeping the value of the last PUT (according to the total order defined above).

- PUT/DELETE conflicts are solved using an add-wins policy (i.e., the DELETE operation has no effect
in the presence of a concurrent PUT).


Implementation
==============

Logs are stored in each replica. For each key there is a log entry corresponding to the last
operation registered on this key. A log entry is a tuple composed of: the type of the operation, its
timestamp, the replica's id where the operation has been register first, the value associated to the
operation. There is two types of operations: PUT and DELETE. For the case of DELETE the timestamp
and replica's id are the one from the PUT that this operation has deleted, associated value is then
forgotten and contains NULL.

All the following operations are deterministic:
 
- ___put(key, value)___: put updates (or creates if the key has not yet an entry) the entry
corresponding to the key with current timestamp, current replica's id, and the desired value.

- ___delete(key)___: delete has no effects if the key does not exist or last operation for the key
is a DELETE. Otherwise, the log entry is updated: the operation type becomes a DELETE and the
associated value is forgotten. The timestamp and replica's id (the one from the PUT we are currently
deleted) are kept.

- ___get(key) -> value___: get returns NULL if the key has no entry in the logs or if last operation
is a DELETE. Otherwise, the associated value is returned.

- ___merge(other_replica_state)___: merge takes an other replica's state and updates the local one
based on operations logged in the other replica. For each entry in the other logs the question will
be: should we replace the local log entry (the one corresponding to the same key) with the other
one? There are 4 cases:
	- There is no log entry in the local logs, then the other log entry is added to the local logs.
	- Last operation is a PUT both in local and other logs, then LWW politics is used to solved the
     conflicts.
	- Last operation is a PUT in either local or other logs and a DELETE in the second one, DELETE
     wins only if the PUT that it had deleted was winner using LWW w.r.t. the current PUT.
	- Last operation is a DELETE in both local and other logs, then the DELETE that was applied on
     the LWW PUT wins.


Note on concurrency
===================

I have considered that multiple threads can operate a replica. For instance, if we have a pool of
workers waiting for clients' requests it is possible that the replica is operated by multiple
workers at the same time. So map CRDT object is in mutual exclusion. Even if it is used in a
sequential context, I do not think that it will result in degraded the performance.
