# BattleGrid
A clone of "Megaman Battle Network", a TGC/TPS/RTS hybrid released in the early 2000's.

### GETTING STARTED
Download the "bg-1.0.rar" file, unzip the folder on any desktop with Java 7+, and double click the "bg-1.0.jar" file.

### HOW TO PLAY
1. Click anywhere on welcome screen to start a match
2. Select your difficulty level (AI)
3. Draw phase: select the order of your card hand. Try to maximise damage and / or range. Water -> Elec = 2x, Grass -> Fire = 2x.
4. Navigate the grid, attacking the enemy when he is in range to be hit (you have a range indicator at your feet). Dodge enemy when he is in range to attack.
5. Redraw a new hand every 15 seconds, a max of 6 times.
6. Win if AI hp reaches 0.
7. Lose if your HP reaches 0, or you draw 6 times (no more cards in deck.)

### WHATS IMPLEMENTED
- Player Controller
- Basic AI Controllers
- Game Board
- UI
- Basic Graphics & Animations

### TODO
- Refactor code for added modularity (so more enemies can be implemented)
- Refactor game board into "tiles" rather than one game board (so we can implement alternative tiles, ex. ice)
- Improve heuristics algorithm for AI
- Implement Megaman Battle Network Sprites & Animations
- Bullet projectiles instead of hitscan
- Implement actual MMBN assets rather than generic Megaman Assets (they are all spritesheets)

### CONTRIBUTE
If you think you can improve the game, engine or graphics please feel free to make a branch followed by a pull request! 

### SOURCES
- Player & AI: Old Megaman sprites (CAPCOM)
- Music: Megaman Battle Network (CAPCOM)
- Background Screens: Assorted Megaman Sprites Edited into BG tiles (CAPCOM)

### LEGAL
This is an in-progress open-source FAN GAME, not to be released commercially for profit. 

### SCREENSHOTS
![Welcome Screen](http://i.imgur.com/uMHu5G3.png)
![Difficulty](http://i.imgur.com/MHpuOrY.png)
![Draw Phase](http://i.imgur.com/9Rvm70E.png)
![Battle Phase](http://i.imgur.com/zAWq6PR.png)
![Game Over](http://i.imgur.com/9iqE1vJ.png)
