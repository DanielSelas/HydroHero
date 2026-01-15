<div align="center">

# 💧 Hydro Hero
**Gamified Hydration Tracking App Prototype**

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white)

<br />

**Hydro Hero** helps users log water, track daily progress, complete reminder milestones, and unlock fun avatars/backgrounds/effects in a shop.

</div>

---
<div align="center">

### 🎥 App Demo

<video src="https://github.com/user-attachments/assets/532ab844-f5e4-4b02-8224-72b4b4bc9f47" controls="controls" width="300"></video>

</div>
---

## 📱 Features & Screenshots

### 1. Hydration Journey
Track your water intake with haptic feedback, celebrations, and milestone hints.

<table style="border: none; border-collapse: collapse;">
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/98faea43-44a6-4a30-adf2-f63fa917441f" width="220" /><br>
      <sub><b>Home Screen</b><br>Next milestone hint</sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/beffccf6-232c-478b-9219-e3e7ba1ad196" width="220" /><br>
      <sub><b>Add Water</b><br>Custom amounts</sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/12086c91-e976-4012-816e-8220a4267185" width="220" /><br>
      <sub><b>Celebration</b><br>Goal reached!</sub>
    </td>
  </tr>
</table>

### 2. Tracking & Reminders
Daily summaries and smart reminders that auto-check when you reach 25%/50%/75% of your goal.

<table style="border: none;">
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/e157a920-7ab0-4500-b004-8dd8e74b8baf" width="220" /><br>
      <sub><b>Daily Progress</b><br>Logs & Summary</sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/57336a90-5f18-48f1-9035-3c679822c1b2" width="220" /><br>
      <sub><b>Reminders</b><br>Preset & Custom</sub>
    </td>
  </tr>
</table>

### 3. Gamification Shop
Buy avatars, backgrounds, and effects. Premium items are marked with 👑.

<table style="border: none;">
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/86b07b48-db7e-4a42-a885-98d05a3c1751" width="220" /><br>
      <sub><b>Shop (Free View)</b></sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/58761506-e538-43f0-8624-13d5d1b9ab9a" width="220" /><br>
      <sub><b>Premium Items</b></sub>
    </td>
  </tr>
</table>

### 4. Monetization (Prototype)
Demonstration of Ads (Banner/Interstitial) and Premium Subscription flow.

<div align="center">
  <img src="https://github.com/user-attachments/assets/e9aae7b8-9196-4e23-b0ef-437e35973820" height="80" alt="Banner Ad" />
  <br><sub>Banner Ad Example</sub>
</div>
<br>

<table style="border: none;">
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/50a49855-52ea-431a-b07d-3f3647fef77f" width="220" /><br>
      <sub>Interstitial Ad</sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/0fe1efeb-ab7e-4dd1-a2a6-949b02338d38" width="220" /><br>
      <sub>Video Ad</sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/e61f71a8-2bb3-4416-9175-0d63779b4b5c" width="220" /><br>
      <sub>Subscription Dialog</sub>
    </td>
  </tr>
</table>

---

## 💎 Free vs Premium

| Feature | Free Account | Premium Account |
| :--- | :--- | :--- |
| **Ads** | Enabled (Banner + Interstitial) | **Ad-free** |
| **Shop Items** | Basic items only | **Unlock 👑 items** |
| **Reminders** | Limited custom reminders | **Unlimited** |

> **How to test:** Open the subscription dialog from Home (⭐/👑 button) and pick a plan. Use “Cancel Monthly Subscription” to return to free.

## 🛠️ Tech Stack

* **Language:** Kotlin
* **UI Framework:** Jetpack Compose (Material 3)
* **Navigation:** Navigation Compose
* **Local Data:** SharedPreferences (Prototype state)
* **Notifications:** AlarmManager + BroadcastReceiver
* **Monetization:** AdMob (Test IDs)
* **Effects:** Konfetti Compose

## ⚠️ Prototype Behavior Note
This project is built as a **single-user prototype**. 
* On app start, the app **resets progress** (intake, streak, coins, ownership) to ensure a clean demo state.
* **Default State:** Water Drop avatar + No Background.

## 🚀 Run the app

1.  Open the project folder in **Android Studio**.
2.  Let Gradle sync finish.
3.  Run the `app` configuration on an emulator or device.
4.  *Note: AdMob is configured with test IDs.*

---
*This repo was built for a school project prototype; accuracy > production hardening.*
