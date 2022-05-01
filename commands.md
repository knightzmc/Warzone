| Command                                      | Description                             | Permission           | Notes                                                                  |
|----------------------------------------------|-----------------------------------------|----------------------|------------------------------------------------------------------------|
| /warzone join                                | Queue your party for a game             | warzone.join         | - Player must not already be in a game<br>- Only party owner can queue |
| /warzone leave                               | Leave your current game                 | warzone.leave        | - Party must be in a game<br>- Only party owner can leave              |
| /warzone requeue                             | Leave your current game and requeue     | warzone.requeue      | - Party must be in a game<br>- Only party owner can requeue            |
| /warzone admin                               | Base command for Warzone Administration | warzone.admin        |                                                                        |
| /warzone admin reset \<player/uuid>          | Reset a player's XP and Level to 0      | warzone.admin.reset  |                                                                        |
| /warzone admin xp set \<player/uuid> <newxp> | Set a player's XP                       | warzone.admin.xp.set |                                                                        |
