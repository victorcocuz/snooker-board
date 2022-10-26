# Snooker Board
**Version 1.0.3**

Snooker Board is a simple snooker score keeping app.

## App screens and features
The app has three separate screens

### Play screen
This is the app introductory screen. 
![Screenshot_20221026-135539](https://user-images.githubusercontent.com/21157326/198106785-eb5932ca-bffe-4765-be9d-c927aaed915f.jpg | width=200)

It is where the user can choose the following game rules:
-A name for each player
-Number of frames
-Number of reds each frame should contain
-Standard foul value
-Who breaks first
After all rules are decided on the match can start.

### Match screen 
This is where the game is actually being recorded. 
![Screenshot_20221026-135647](https://user-images.githubusercontent.com/21157326/198106857-ed675a06-1e26-4e57-9156-b5837dc6efb5.jpg | width=200)

The player at the table will record his actions, which can be as follows:
-Pot a ball - the app will automatically switch between reds and colors to pot
-Safety shot
-Missed shot
-Foul - a menu will open to select which ball was fouled and what action to choose from:
--The player at the table can continue playing or force the previous player to repeat the shot
--After the foul has been recorded, if the player chooses to continue he may have a freeball. A toggle appears on the screen to record that
-Add red ball - only available after a red has been potted - in case more than one red flukes in at the same time
-Remove red ball - removes a red ball of the table. Should only be used before a foul.
-Rerack - resets the frame
-Concede frame - hands the frame to the player with the highest points
-Concede match - hands the frame to the player with most frames
-Cancel match - returns to the play screen
-Undo - each action can be undone until you get back to the beginning of the frame

This screen has the following features:
-Shows the frame and match score
-Keeeps live track of each player's highest break, pot success and number of fouls
-Highlights the player at the table
-Shows a breakdown of all actions during the current frame, showing a visual list of all breaks.

### Post Match screen
This is where the score breakdown will be shown at the end of the match.
![Screenshot_20221026-135844](https://user-images.githubusercontent.com/21157326/198106878-10c7a067-de97-48de-bb5d-30ef0bdff9a4.jpg | width=200)

The breakdown includes a list of all frames, showing for each frame:
-Frame score
-Incremental match score
-Highest break for each player
-Percentage of success shots
This also shows the total for each of the above category.

## Wish list
My dream is to bring this app to a professional level and hope that one day it can be used for players who wish to keep track of their career progress and for tournaments.

I am slowly going towards implementing:
-A better UI
-A casting button
-A score sharing button
-Login screen
-History saving
-Snooker friend finding and possibly venue finding

## Contact
I decided to create this app out of passion for snooker. I am not a developer and all I've done is by learning android and kotlin in my spare time. My resources are limited, but you would like to get involved I'd be very happy to receive any help. 

You can contact me at:
victor.cocuz@yahoo.com
