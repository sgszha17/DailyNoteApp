# DailyNoteApp
> **IMPORTANT NOTE:**This application is developed for assignment 2 of subject COMP90018 Mobile Computing System Programming of University of Melbourne. 

This repository contains frontend(DailyNote) and beckend(msbuild-master).
DailyNote_Android is a Android project contain sample document connecting database.
DailyNoteDB is a database server project using C#. 

There is a [demonstration is avaliable on Youtube.](https://www.youtube.com/watch?v=1C_NXLIWlnA&feature=youtu.be)

## Briefing 

* DailyNote aims to help people gather information completely in several important situations. The information may important for user to make better decisions which may affect the future and the life.

## App Structure
<img width="490" alt="app_structure2" src="https://user-images.githubusercontent.com/11943815/31317562-2e869e46-ac8f-11e7-9b3b-aaa342461c69.png">

## DailyNote -- Frontend
The frontend is developed by Andtroid Studio(https://developer.android.com/studio/index.html) using java. There are several screeshot to introduce the application.

### Icon
The icon of this application is a microphone include a notebook:

![ic_launcher](https://user-images.githubusercontent.com/11943815/31317162-f4097af6-ac87-11e7-8660-c4857154815d.png)

It indicate that the note can be "write" by microphone. 

### Login
This function allow user register and login:

![login](https://user-images.githubusercontent.com/11943815/31316989-6c1dc80c-ac84-11e7-827c-d1c8e3f23350.png)

The system is able to login automaticlly after user first login or register.

### AddNote
The aim of this project is help user take notes quickly. There are two way to help user create new notes: shake the phone or click the button.
When user shaking the phone, a small menu will jump up, the user is able click the button to create a new note:

![shake](https://user-images.githubusercontent.com/11943815/31317078-1ad65f7a-ac86-11e7-977f-e987e2f518d8.png)

This apply the accelerometer of phone. 

Besides that, the pencil button on the top left side on the screen is able open notes edit page directly:

![notes](https://user-images.githubusercontent.com/11943815/31317114-a961d95e-ac86-11e7-9df2-a12a3e96224a.png)

Then, user is allow to enter note directly by type word. However, there is a new function which is able transform voice to word using android speech recognizer（https://developer.android.com/reference/android/speech/SpeechRecognizer.html）:

![recordvoice](https://user-images.githubusercontent.com/11943815/31317261-7a77858c-ac89-11e7-9cc9-11292e1d7298.png)

This employs microphone on the phone. When saying "hello", the microphone will recording it and the system will transform the speech to words or sentences:

![speechtotext](https://user-images.githubusercontent.com/11943815/31317327-746fffce-ac8a-11e7-87bf-fa8431baca67.png)

When user clicks save the note will be stored.

![addnotesucceed](https://user-images.githubusercontent.com/11943815/31317342-b8debea2-ac8a-11e7-9243-85e98bebac15.png)

### Setting
The wheel gear button on the note list page is an access to the setting page. In setting page, user is allowed to change picture by click the picture:

![settingpic](https://user-images.githubusercontent.com/11943815/31317404-548b75b0-ac8c-11e7-906b-26675d242f3a.png)

The system allow user take picture directly or select a picture from album. User is also cut the picture. This is the utility of camera of phone.

![cutpicture](https://user-images.githubusercontent.com/11943815/31317467-429810b0-ac8d-11e7-9aa4-d25eab5ab887.png)

Then, on the setting page, user can logout their account and change to another account; clear cache of the application and clear the note content.


## msbuild-master -- Backend
This is the backend of DailyNote allow store user's information in a SQL Database, query data and delete data. It is developed based on azure service(https://azure.microsoft.com/en-au/free/) using Visual Studio(https://www.visualstudio.com/zh-hans/?rr=https%3A%2F%2Fwww.google.com.au%2F). 
There are three main document: 
* Users -- A Data Transfer Object
* UsersController -- A Table Controller
* M1 -- A First code migration document which used to construct table in database

After user register, in the frontend, a user instance will be transmitted to server and stored into database: 

![screenshots_of_userstable](https://user-images.githubusercontent.com/11943815/31317636-3a1f67b4-ac90-11e7-9d43-ff2a75adb810.JPG)

The server also has capability to deal with query.

## Authors
* Dawei Wang 
   -- daweiw@student.unimelb.edu.au
* Siyu Zhang 
   -- siyuz6@student.unimelb.edu.au
* Tong Zou
  -- tzou2@student.unimelb.edu.au

