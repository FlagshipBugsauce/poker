# Poker Backend
Backend for an online, multiplayer, browser-based, poker game using Angular and Spring Boot.

## Basic Setup:
1. Install MongoDB (install it as a "service" - it will ask you if you want to do this during the installation).
2. Clone the repo. Recommended to create a directory for both the frontend and backend and clone both projects into this directory.

## Short-Term Goals
* I want to get a simplified game up and running. 
* The "game" I have in mind for the short-term, will simply select a player at random and proclaim them the winner.
* The point here, is to first focus on all the infrastructure necessary to facilitate a more complex game, such a NL Hold 'em. Once all this infrastructure is in place, the game logic can become the primary focus of development.
* This infrastucture consists of things like user registration and login, the ability to create and start games, etc...

### TODO:

1. Configure MongoDB for Spring Boot.
2. Configure and implement security so the app uses JWTs to authenticate.
    1. Implement initialization that will create and save an admin user. This is going to involve designing a user model, which means figuring out what information we need to store for each user. Better to figure this out now than having to change it later and ending up with inconsistencies in user documents.
    2. Implement JWT utility in order to parse the token and retrieve username, etc...
    3. Add the filter to authenticate using the JWT provided in request headers.
    4. Add mongo repository and model (document) for users.
    5. Implement endpoints to authenticate and register.
3. Fix the inevitable CORS problem.
4. Implement "create game" and "start game" endpoints and the related services.
