# poker
# Poker Backend
Backend for an online, multiplayer, browser-based, poker game using Angular and Spring Boot.

## Basic Setup for Local Development:
1. Download and install MongoDB Server.
    * The community edition of Mongo should be fine.
    * Link: https://www.mongodb.com/download-center/community
    * Recommended to install as a "service" - it will ask you if you want to do this during the installation.
    * Recommended to install Mongo Compass as well. It is not necessary and there are other tools that do the same thing, but it's a useful tool to have if you've got nothing else and it is lightweight and easy to use.
    * Link: https://www.mongodb.com/download-center/compass
2. Install Postman to help with testing endpoints.
3. Recommended to use Intellij for server development. 
    * You can get the full version for free with a student license.
    * Obtaining a student lisence is simple and fast, just use your university email and you will be approved almost instantly.
    * Once you have your student lisence, download the ultimate version of Intellij and after you install you can login with the account you created.
    * Link: https://www.jetbrains.com/community/education/#students
4. This project will auto format any code you submit using Google Java Format in order to keep the code style as consistent as possible. 
    * The code style used by Google Java Format is different than the default code style used by Intellij. 
    * It is recommended that you install the "Google Java Format Intellij plugin" and also use the "Intellij Java Google Style file" so the IDE will use the correct tab size, and the "Reformat Code" feature will work correctly.
    * Instructions for installing the plugin, a download link to the style file and instructions for importing it can all be found at the Google Java Format repository.
    * Link: https://github.com/google/google-java-format
5. Clone the repo. 
    * Recommended to create a directory for both the frontend and backend and clone both projects into this directory.

## Short-Term Goals
* I want to get a simplified game up and running. 
* The "game" I have in mind for the short-term, will simply select a player at random and proclaim them the winner.
* The point here, is to first focus on all the infrastructure necessary to facilitate a more complex game, such a NL Hold 'em. 
* Once all this infrastructure is in place, the game logic can become the primary focus of development.
* This infrastucture consists of things like user registration and login, the ability to create and start games, etc...

## Long-Term Goals
* This will be the backend for an online multiplayer poker game.
* We have another project for the frontend, but this backend could be used by multiple clients if anyone wants to create another client.
* People will be able to create accounts and play poker with their friends using the app.
* We won't handle money, but will facilitate betting that is enforced by the players (i.e. they can create a $10 tournament, but it's up to them to make sure everyone who plays actually pays).

## Working on the Project:
* **DO NOT** work directly on the develop (or master) branch. 
   * Any work you do **MUST** be done on your own branch.
   * You should create a new branch for each issue you work on.
   * Once you're done your task, submit a pull request and only merge the branch you've worked on to the develop branch after it's approved.
* **DO NOT** merge anything to the develop (or master) branch until you create a pull request and it is approved.
   * If you do not know how to create a pull request, ask someone or google it (it's very simple).
* You can work on any of the issues found in the "To Do" section of the project board. 
* If there is something that needs to get done but no issue has been created, then create the issue yourself and add it to the project board.
* When you start working on something, move the associated issue to the "In Progress" section so that everyone knows the issue is being worked on.
* Feel free to update issues, add requirements, add issues, etc... Just try to keep things organized.
* You should be able to associate your pull requests with an issue and once your request is merged, the issue should automatically be moved to the "Done" section. If this doesn't happen for some reason, then move it manually so everyone knows the issue is finished.

