import sys
import os

args = sys.argv
args.pop(0)

if len(args) < 1:
    print("execute this file with -help and you will see some commands\n")
    exit()

if args[0] == "-help":
    print("-help\n"
          "     prints out this help\n"
          "\n"
          "-containers <containername>\n"
          "     restarts the containers with 'docker-compose up -d --no-deps --build <containername>'\n"
          "     there can be multple <containername>\n"
          "     the <containername> has to be defined in ../docker-compose.yml\n"
          "\n"
          "-startAll\n"
          "     starts all containers in ../docker-compose.yml\n"
          "     it's run with the command 'docker-compose up -d --build'\n"
          "\n"
          )
    exit()

if args[0] == "-containers":
    args.pop(0)
    if len(args) < 1:
        print("you have to specify an containername\n")
        exit()
    else:
        containersString = ""
        for arg in args:
            containersString = containersString + " " + arg
        print("restarting containers: " + containersString + "\n")
        stream = os.popen('docker-compose up -d --no-deps --build ' + containersString)
        output = stream.read()
        print(output)
        print("finished restarting containers: " + containersString + "\n")
        exit()

if args[0] == "-startAll":
    args.pop(0)
    print("starting all containers\n")
    stream = os.popen('docker-compose up -d --build')
    output = stream.read()
    print(output)
    print("finished starting all containers\n")
    exit()
