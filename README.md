# Snooker Board
**Version 1.0.8**

Snooker Board is a simple snooker score keeping app.

## App screens and features
The app has three separate screens:

### Play screen
This is the app introductory screen. 

<img src="https://user-images.githubusercontent.com/21157326/198108585-f93fef5c-767b-416e-8b3f-089adeddc753.jpg" width="200">

It is where the user can choose the following game rules:
- A name for each player
- Number of frames
- Number of reds each frame should contain
- Standard foul value
- Who breaks first

After all rules are decided the match can start.

### Match screen 
This is where the game is actually being recorded. 

<img src="https://user-images.githubusercontent.com/21157326/198109113-93f92b9c-de27-4cf1-9fe8-36d9bf64bd10.jpg" width="200">

The player at the table will record his actions, which can be as follows:
- Pot a ball - the app will automatically switch between reds and colors to pot
- Safety shot
- Missed shot
- Foul - a menu will open to select which ball was fouled and what action to choose from:
  - The player at the table can continue playing or force the previous player to repeat the shot
  - After the foul has been recorded, if the player chooses to continue he may have a freeball. A toggle appears on the screen to record that
- Add red ball - only available after a red has been potted - in case more than one red flukes in at the same time
- Remove red ball - removes a red ball of the table. Should only be used before a foul.
- Rerack - resets the frame
- Concede frame - hands the frame to the player with the highest points
- Concede match - hands the frame to the player with most frames
- Cancel match - returns to the play screen
- Undo - each action can be undone until you get back to the beginning of the frame

This screen has the following features:
- Shows the frame and match score
- Keeps live track of each player's highest break, pot success and number of fouls
- Highlights the player at the table
- Shows a breakdown of all actions during the current frame, showing a visual list of all breaks.

### Post Match screen
This is where the score breakdown will be shown at the end of the match.

<img src="https://user-images.githubusercontent.com/21157326/198109135-cd62b8c7-fe5c-4500-b039-cdd8be0cdaa2.jpg" width="200">

The breakdown includes a list of all frames, showing for each frame:
- Frame score
- Incremental match score
- Highest break for each player
- Percentage of success shots

This also shows the total for each of the above category.

## Wish list
My dream is to bring this app to a professional level and hope that one day it can be used for players who wish to keep track of their career progress and for tournaments.

I am slowly going towards implementing:
- A better UI
- A casting button
- A score sharing button
- Login screen
- History saving
- Snooker friend finding and possibly venue finding

## Contact
I decided to create this app out of passion for snooker. I am not a developer and I've only learned android and kotlin in my spare time. My resources and time are limited so I can only push this so far. If you would like to get involved I'd be very happy to receive any help. 

You can contact me at:
victor.cocuz@yahoo.com
