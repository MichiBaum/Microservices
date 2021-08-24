#!/usr/bin/python

import argparse

parser = argparse.ArgumentParser(description='Building and Uploading Microservices')
parser.add_argument('username', metavar='username', help='The Username of Docker Hub to Upload images to Docker Hub')
parser.add_argument('password', metavar='password', help='The Password of Docker Hub to Upload images to Docker Hub')

argsNamespace = parser.parse_args()
args = vars(argsNamespace)
print(args['username'])
