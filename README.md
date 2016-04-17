# IMPORTANT!
This was a prototype I built in about a day.
I am working on a more modular game engine to accomplish the same gameplay style.
Feel free to use this project as inspiration, or contact me if you are familiar with LibGDX / Networking or spriting and want to work on a larger-scale project with similar inspirations.

# BattleGrid
A clone of "Megaman Battle Network", a TGC/TPS/RTS hybrid released in the early 2000's.

### PREREQUISITES 
- Java 7+
- OpenGL
- 512MB RAM 

### GETTING STARTED
Download the "bg-1.0.rar" file, unzip the folder and double click the "bg-1.0.jar" file.  

### HOW TO PLAY
1. Click anywhere on welcome screen to start a match
2. Select your difficulty level (AI)
3. Select your hand order. Water -> Elec & Grass -> Fire = 2x damage. 
4. Navigate grid, attacking and dodging.
5. Redraw a new hand every 15 seconds, a max of 6 times.
6. Win if AI hp reaches 0.
7. Lose if your HP reaches 0, or you draw 6 times (no more cards in deck).

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
![Welcome Screen](http://i.imgur.com/52oushd.png)
![Difficulty](http://i.imgur.com/vCgPQ7g.png)
![Draw Phase](http://i.imgur.com/OC0kYB6.png)
![Battle Phase](http://i.imgur.com/Wbye5sZ.png)
![Game Over](http://i.imgur.com/5v4yD6V.png)
