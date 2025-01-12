# Covid Buster 9000
## Preview
![covid_buster_tech_stack](https://github.com/user-attachments/assets/c96b3b3c-a070-4f20-94ea-5d46b4e53d79)

![covid_buster_screens_real](https://github.com/user-attachments/assets/04f96d1b-7004-434d-9d65-ae4946dfce2f)

![covid_buster_screens](https://github.com/user-attachments/assets/38dbd5fe-bf17-4174-a2b5-4f28c56bbf63)

## Preview Video
https://www.youtube.com/watch?v=uX1R_b5_JQI

## Agile Board
* Team: https://trello.com/tsm_mobcomco2team
* Board: https://trello.com/b/bNpCPCE4/kanban

## Team
* [Dennis Briner](https://github.com/TheMen4ce)
* [Stefan Wick](https://github.com/wickdev)
* [Vladimir Brazhnik](https://github.com/4realDev)

## Docs
* [Slides](https://docs.google.com/presentation/d/1LQ77AjYsA2AsoAlaf-1ryAkffRm060hJ0MQgu7ak1nI/edit#slide=id.g723630543_3_0)
* [Video](https://drive.google.com/file/d/1889uhlroge8Ggu7UIv40mozf6lYQPhnd/view?usp=sharing) (MP4)
* [UISketches](Docs)
* [Class Diagram](https://drive.google.com/file/d/1qiJj_hMIhar-pp_2rVHpa_LUboA9ZGWx/view?usp=sharing)

## Project Overview
Our Mission is to reduce the spreading of the SARS-CoV-2 virus. The virus can be transmitted over aerosols. This can particulary occur indoors when spaces are inadequately ventilated (Source: [WHO](https://www.who.int/news-room/q-a-detail/q-a-how-is-covid-19-transmitted)). We aim to create a small device that can be placed in frequented rooms and notifies people when the air quality is bad and thus, the risk of an infection is higher. Common use cases for such a device would be schoolar classrooms, restaurants or corporate meeting rooms.

### Device
Each device should be placed in one room. This device then represents this room. The devices, as soon as connected to power, measures co2-concentration (in ppm)

#### BLE peripheral
The device broadcasts a BLE service that has one carateristic that sends the current co2-concentration in ppm and another that displays the battery status.

#### LED notification
The devices also has a built-in LED that slowly changes it's state from Green, passing Orange to Red. 

Green means: This room is well ventilated and staying in this room is supposed to be safe
Orange means: This room should be ventilated, i.e. by opening the windows
Red means: The air in this room is stale and the room must be immediately ventilated. Staying in this room is not supposed to be safe

### App
The android app has multiple functions which are independent from eachother

#### BLE central
The app is, when activated, constantly looking for peripherals. When it has detected one, it will connect automatically. This means, the smartphones owner is currently in the same room as the device. It will then
* upload the current co2-concentration for this particular room to the backend in a background service
* show the current co2-concentration and status on the user interface
* check the currenct co2-concentration and notify the user (notification and vibration) when the room should be ventilated

#### Frontend
The app also offers an overview over all sensors deployed everywhere. This gives an overview over which rooms are safe on average. This feature is always accessible (even when not connected to a device). This can be used by a facility manager to remind certain people (for example a teacher that always uses a specific classroom) to take more care to the air quality in a specific room.

#### Room Admin / Device Setup (Optional Extension)
In the MVP devices/rooms must be setup beforehand, meaning create a channel for each room in the Thingsspeak backend.

This can be simplified for the user. The admin UI could allow to create, rename, delete new rooms/channels. This would need a second backend that holds the number of known channels, their API keys and the devices service UUID. For this second backend Firebase would be a good option.

### Cloud Backend
The backend stores co2-concentrations in different rooms, reported by differnt devices over time. Due to simplicity the [Thingsspeak](thingspeak.com) works perfect for our usecase.

Each device represents a channel

Each channel has two fields:
* field1, co2-concentration in ppm, uint16 range: 0-56000 (>40k is deadly)
* field2, battery, uint8, range: 0-100

I created already a channel for the first room:
https://thingspeak.com/channels/1224181

The app can read data from the channel with HTTP GET:
`https://api.thingspeak.com/channels/<channel-id>/fields/<field-id>.json`

The app can write data to the channel like so:
`https://api.thingspeak.com/update?api_key=7G0T8JBRR8M6OAPD&<field-id>=<int value>`

Channels can also be created, whiped, deleted via API. Check:
https://thingspeak.com/account/profile

#### Why Thingspeak
Thingspeak is superiour to Firebase for our usecase since it offers some nice extra features:

- We can look at prebuild charts for our data online
- Information can be retrieved averaged over defined time periods
- Reading and writing information is very easy

## Testing & Code Quality
Most of the testing was done manually. We have automated unit tests for our peripheral and some for the Android app. Since testing was not a part of the lectures, we didn't put more time into it.

The overall quality can be accessed with Sonar Qube. We couldn't make Sonar to see the tests, thus it shows 0% coverage.

## Code
* [Android](Android)
* [Arduino](Arduino)
