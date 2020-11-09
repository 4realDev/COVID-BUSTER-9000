# MSE TSM MobCom Project PROJECT_NAME

## Agile Board
* Team: https://trello.com/tsm_mobcomco2team
* Board: https://trello.com/b/bNpCPCE4/kanban

## Team
* [Dennis Briner](https://github.com/TheMen4ce)
* [Stefan Wick](https://github.com/wickdev)
* [Vladimir Brazhnik](https://github.com/4realDev)

## Docs
* [Slides](Docs/Slides.pdf) (PDF)
* [Video](Docs/Video.mp4) (MP4)

## Project Overview
Our Mission is to reduce the spreading of the SARS-CoV-2 virus. The virus can be transmitted over aresols. This can particulary occur indoors when spaces are inadequately ventilated (Source: [WHO](https://www.who.int/news-room/q-a-detail/q-a-how-is-covid-19-transmitted)). We aim to create a small device that can be placed in frequented rooms and notifies people when the air quality is bad and thus, the risk of an infection is higher. Common use cases for such a device would be schoolar classrooms, restaurants or corporate meeting rooms.

### Device
The devices, as soon as connected to power, measures co2-concentration (in ppm)

#### BLE peripheral
The device broadcasts a BLE service that has one carateristic that sends the current co2-concentration in ppm and another that displays the battery status.

#### LED notification
The devices also has a built-in LED that slowly changes it's state from Green, passing Orange to Red. 

Green means: This room is well ventilated and staying in this room is supposed to be safe
Orange means: This room should be ventilated, i.e. by opening the windows
Red means: The air in this room is stale and the room must be immediately ventilated. Staying in this room is not supposed to be safe


### App

### Backend API



## Code
* [Android](Android)
* [Arduino](Arduino)
