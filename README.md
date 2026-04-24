# Sensors
## StateSensor
1. self continent count
2. self territory count
3. self army count
4. army count of enemy with highest # of armies
5. enemy player who has the most armies
6. Asia Completion
7. North America Completion
8. South America Completion
9. Africa Completion
10. Europe Completion
11. Australia Completion
12. number of hostile territories adjacent to us
13. number of hostile territories adjacent to us (double counting removed)
14. number of our territories adjacent to enemy territories
15. avg ratio between armies in our exposed territories
16. our hand size
17. how many cards are wild
18. how many cards we can trade
## ActionSensor
1. player id
2. terminal flag
3. action type (1:Attack, 2:Fortify,3:Redeem Cards)
4. ATTACK info: #armies in attack
5. ATTACK info: #armies moving
6. ATTACK info: #army ratio
7. FORTIFY info: #armies moving   
## PlacementSensor
1. num armies already in territory in question
2. num of adjacent hostile territories(normalized)
3. num of adjacent friendly territories(normalized)
4. completion level of continent in question
5. num of remaining armies

# Reward Functions


