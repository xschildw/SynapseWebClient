#!/bin/sh
# Migrate all locations.
java -cp target/tool-migration-utility-0.9-SNAPSHOT-jar-with-dependencies.jar org.sagebionetworks.tool.migration.MigrateLocations "$@"