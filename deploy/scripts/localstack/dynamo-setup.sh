#!/bin/bash

# Creating DynamoDb tables
echo $(awslocal dynamodb create-table --table-name Links --attribute-definitions AttributeName=Id,AttributeType=S --key-schema AttributeName=Id,KeyType=HASH --provisioned-throughput ReadCapacityUnits=10,WriteCapacityUnits=5)

echo $(awslocal dynamodb update-time-to-live --table-name Links --time-to-live-specification Enabled=true,AttributeName=ExpireAt)
