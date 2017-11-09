#! /bin/bash

export COLUMNS
export LINES

CLASS_PATH=out/artifacts/org_json_json_Release/json-20171018.jar:out/production/LogicalClocks
java -cp $CLASS_PATH edu.gvsu.cis.cis656.client.Client $1
