# Poker

This application consists of two separate projects, one is an Angular project, the other is a 
Spring Boot project. Both projects are currently in the early development phase. This readme will
be updated as development continues.

## Setup
Each project requires a specific setup. Follow the instructions below to get started.

It is strongly recommended to use Intellij 2020 for both server and client development. 
* You can get the ultimate version for free with a student license.
* Obtaining a student licence is simple and fast, just use your university email, and you will 
be approved almost instantly.
* Once you have your student licence, download the ultimate version of Intellij and after you 
install you can log in with the account you created.
* Link: https://www.jetbrains.com/community/education/#students
* Recommended to use git bash for your terminal in Intellij.

### Intellij Setup

_You should hold off on opening the project in Intellij until you've installed the necessary
software (instructions and links below)._

1. Clone the repository by opening git bash and running the command 
`git clone https://github.com/jon-gourley/poker.git` in whichever directory you wish the
project files to reside.

2. After installing the necessary software, and cloning the repo, open the poker directory in 
Intellij.

3. Intellij should immediately recognize there is a maven build script present in the project files. 
Check the Intellij event log if you miss the notification on the bottom right about the maven
build script. Intellij should set everything up for the server project by clicking "import" on this
notification. 

4. To get Intellij to recognize the client project, you may need to go 
File->New->Module from Existing Sources, select the `./poker-client/` directory, and select create
module from existing sources. Intellij should then recognize this as an Angular project. 

5. Once you've added a module for the client project, navigate to the `./poker-client/` directory 
using git bash and run the command `npm install`. This will install all the client dependencies 
(creates your node_modules directory). Note that you need Node installed (instructions below) in
order for this command to work.

6. Finally, you can right click on `package.json` and click `show npm scripts`. This will bring up
the scripts from `package.json` which can run the client, run client unit tests, generate client
models, deploy to S3, etc... You can also run these via the terminal using commands such as 
`ng serve`, etc...

If there are any issues with Intellij recognizing either project (in particular the server), feel
free to ask for help. 

### Poker Server Setup
Note that if you don't want to do server work, there is no reason to bother with these instructions.
You can use the deployed server which is currently at http://poker-testing.ngrok.io. Just make sure
your client project knows that this is where your backend is being hosted and you don't need to
worry about any of this.

1. Download and install MongoDB Server.
    * The community edition of Mongo should be fine.
    * Link: https://www.mongodb.com/download-center/community
    * Recommended to install as a "service" - it will ask you if you want to do this during the 
    installation.
    * Recommended to install Mongo Compass as well. It is not necessary and there are other tools 
    that do the same thing, but it's a useful tool to have if you've got nothing else, and it is 
    lightweight and easy to use.
    * Link: https://www.mongodb.com/download-center/compass
2. Install Postman to help with testing server code.
3. This project will auto format any code you submit using Google Java Format in order to keep the 
code style as consistent as possible. 
    * The code style used by Google Java Format is different than the default code style used by 
    Intellij. 
    * Recommended to install the "Google Java Format Intellij plugin" and use the 
    "Intellij Java Google Style file", so the IDE will use the correct tab size, and the "Reformat 
    Code" feature will work correctly.
    * Instructions for installing the plugin, a download link to the style file and instructions 
    for importing it can all be found at the Google Java Format repository.
    * Link: https://github.com/google/google-java-format

### Poker Client
#### Basic Setup
1. In order to run the client, you will need Node.js. Get the latest version.
    * Link: https://nodejs.org/en/
2. Once node is installed, install the Angular CLI.
    * Install using the terminal command `npm install -g @angular/cli`

## General FYI About Working on this Repo
* **DO NOT** work directly on the develop (or master) branch. 
   * Any work you do **MUST** be done on your own branch.
   * You should create a new branch for each issue you work on.
   * Once you're done your task, submit a pull request and only merge the branch you've worked on 
   to the develop branch after it's approved.
* **DO NOT** merge anything to the develop (or master) branch until you create a pull request, and 
it is approved.
   * If you do not know how to create a pull request, ask someone or google it (it's very simple).
* You can work on any of the issues found in the "To Do" section of either project board. 
* If there is something that needs to get done but no issue has been created, then create the issue 
yourself and add it to the project board.
* When you start working on something, move the associated issue to the "In Progress" section so 
that everyone knows the issue is being worked on.
* Feel free to update issues, add requirements, add issues, etc... Just try to keep things organized.
* You should be able to associate your pull requests with an issue and once your request is merged, 
the issue should automatically be moved to the "Done" section. If this doesn't happen for some 
reason, then move it manually so everyone knows the issue is finished.

