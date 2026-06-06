# BDoner – Smart Blood Donation App
Goal: " Do a mini project in 4 days of Time limit "
> Note: API keys have been removed for security purposes. Please configure them manually using the steps below.

---

##  Overview
**BDoner** is an Android-based smart blood donation application that helps users quickly find nearby blood donors using GPS and intelligent donor ranking.

The application improves emergency response by combining real-time data, location services, and smart decision-making.

---

##  Features
-  Find nearby blood donors using GPS  
-  Smart donor ranking (AI-based scoring system)  
-  Direct call functionality  
-  WhatsApp integration with pre-filled message  
-  Google Maps navigation to donor location  
-  “Best Match” donor highlighting  
-  Firebase Realtime Database backend  
-  Location fallback when GPS is unavailable  

---

##  Technologies Used
- **Java (Android Development)**
- **XML (UI Design)**
- **Firebase Realtime Database**
- **Google Location Services (GPS)**
- **Haversine Formula (Distance Calculation)**
- **Gemini API (AI Assistant via Flask)**

---

##  AI Tools & Assistance
This project was developed with the support of modern AI tools to improve productivity and design quality:

- **ChatGPT (OpenAI)** – Used for logic building, debugging, and feature implementation  
- **Claude AI** – Assisted in UI/UX improvements and layout design  
- **Gemini (Google AI via Android Studio)** – Integrated as an AI assistant for medical guidance  

>  AI tools were used as development assistants. All implementation, integration, and logic were done manually.

---

##  API Key Setup (Important)

This project uses **Gemini API (via Flask backend)**.

API keys are **not included** in this repository.

###  Setup Steps

1. Open the file: app/src/main/java/com/example/bdoner/AiActivity.java
2. Replace:
```java
String API_KEY = "YOUR_API_KEY_HERE";
```

##  App Screenshots

###  Login & Registration

<p align="center">
  <img src="https://github.com/user-attachments/assets/f8d01612-7ef2-4011-8529-0182a704b7b4" width="250"/>
  <img src="https://github.com/user-attachments/assets/97357452-fe2d-4a71-a2b2-a78243893244" width="250"/>
</p>

<p align="center">
  <b>Login Page</b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <b>Registration Page</b>
</p>

---

###  Home & Navigation

<p align="center">
  <img src="https://github.com/user-attachments/assets/b500e4d0-1720-4644-ad7c-d799487a5734" width="250"/>
  <img src="https://github.com/user-attachments/assets/848d8861-6297-4ddf-8c3c-adc6162c30e1" width="250"/>
</p>

<p align="center">
  <b>Home Page</b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <b>Sidebar Navigation</b>
</p>

---

###  AI Assistant

<p align="center">
  <img src="https://github.com/user-attachments/assets/52564814-033c-41b4-86ed-1f6a38c28f30" width="250"/>
</p>

<p align="center">
  <b>AI Assistant Page</b>
</p>

---

###  Donor Features

<p align="center">
  <img src="https://github.com/user-attachments/assets/d2befaf6-fb7f-4aee-81a9-504cc89f2478" width="250"/>
  <img src="https://github.com/user-attachments/assets/fcd853d8-c9e8-46e3-a0ef-ae73fa90626c" width="250"/>
</p>

<p align="center">
  <b>Become Donor</b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <b>Find Blood</b>
</p>

---

###  Location Settings

<p align="center">
  <img src="https://github.com/user-attachments/assets/6f4724e5-542c-4e9d-a4cc-ffb8f4e9bbb9" width="250"/>
</p>

<p align="center">
  <b>Change Static Location</b>
</p>


## Challenges Faced
- Handling GPS unavailable (0,0 location issue)
- Preventing app crashes due to XML errors
- Designing efficient RecyclerView UI
- Implementing intelligent ranking logic
- Maintaining consistent UI/UX


## Other Team Members:
Dharmik
Ajay
Mahini

## Conclusion

BDoner demonstrates how mobile applications can use GPS, real-time databases, and intelligent decision-making to improve emergency blood donation systems.

🙌 Thank You
