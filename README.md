# Hydro Hero 💧

**Hydro Hero** is a gamified hydration tracking **Android** app prototype built with **Kotlin + Jetpack Compose**.  
It helps users log water, track daily progress, complete reminder milestones, and unlock fun avatars/backgrounds/effects in a shop.

## Screenshots

Add screenshots to `screenshots/` (recommended width: ~**1080px**). The README below references these filenames.

## Features

- **Hydration journey**
  - Add water (preset amounts + custom amount)
  - Progress feedback (haptics + overlay messages)
  - **Goal completion celebration** (confetti + effects)
  - Coins + streak award on daily goal completion
  - Small “Next milestone” hint on Home (25/50/75/100)

  **Screenshots**

  | Home | Add Water | Celebration |
  | --- | --- | --- |
  | !<img width="347" height="774" alt="shop" src="https://github.com/user-attachments/assets/98faea43-44a6-4a30-adf2-f63fa917441f" /> |
  | <img width="350" height="775" alt="add-water" src="https://github.com/user-attachments/assets/beffccf6-232c-478b-9219-e3e7ba1ad196" /> |
  | <img width="390" height="781" alt="celebration" src="https://github.com/user-attachments/assets/12086c91-e976-4012-816e-8220a4267185" /> |

- **Reminders (prototype UX)**
  - Preset reminders + custom reminders
  - Swipe-to-delete for custom reminders (free users: rewarded ad)
  - “Done ✅” marking (preset reminders auto-check at **25% / 50% / 75%** of the daily goal)

  | <img width="352" height="773" alt="reminders" src="https://github.com/user-attachments/assets/57336a90-5f18-48f1-9035-3c679822c1b2" /> |

- **Daily Progress**
  - Daily summary card + water log
  - Drinks count + last drink time

  | <img width="346" height="773" alt="daily-progress" src="https://github.com/user-attachments/assets/e157a920-7ab0-4500-b004-8dd8e74b8baf" /> |


- **Shop**
  - Buy/select **avatars**, **backgrounds**, **effects**
  - Premium-locked items (👑)
  - Clear “Selected” state + toast feedback

  | <img width="347" height="774" alt="shop" src="https://github.com/user-attachments/assets/86b07b48-db7e-4a42-a885-98d05a3c1751" /> |
  | <img width="349" height="774" alt="premuim-shop" src="https://github.com/user-attachments/assets/58761506-e538-43f0-8624-13d5d1b9ab9a" /> |


- **Monetization (prototype)**
  - Banner ad (free users)
  - Interstitial ad when daily goal completed (free users)
  - Subscription dialog (monthly/lifetime) including “Ad-free” messaging
    
  | <img width="327" height="114" alt="ad-banner" src="https://github.com/user-attachments/assets/e9aae7b8-9196-4e23-b0ef-437e35973820" /> |
  | <img width="357" height="783" alt="ad-popup" src="https://github.com/user-attachments/assets/50a49855-52ea-431a-b07d-3f3647fef77f" /> |
  | <img width="351" height="777" alt="ad-video" src="https://github.com/user-attachments/assets/0fe1efeb-ab7e-4dd1-a2a6-949b02338d38" /> |



## Free vs Premium (what to demo)

- **Free account**
  - Ads enabled (banner + interstitial/rewarded in the prototype flow)
  - Premium shop items show a 👑 and can’t be purchased
  - Custom reminder limit (free users have a limit; premium users are unlimited)

- **Premium account**
  - **Ad-free**
  - Premium shop items can be purchased/selected
  - Unlimited custom reminder

  | <img width="356" height="784" alt="premuim" src="https://github.com/user-attachments/assets/e61f71a8-2bb3-4416-9175-0d63779b4b5c" /> |



To switch for screenshots: open the subscription dialog from Home (⭐/👑 button) and pick a plan. Use “Cancel Monthly Subscription” to return to free (if you picked monthly).

## Prototype behavior (important)

This project is currently built as a **single-user prototype** for demos/testing:

- On app start, the app **resets** progress (intake/streak/coins/shop ownership/premium status) to a clean state.
- Default ownership after reset: **Water Drop** avatar + **No Background**.

If you want “real app” persistence later, we can remove the reset logic and keep only daily rollovers.

## Tech stack

- **Kotlin**, **Jetpack Compose**, Material 3
- Navigation Compose
- SharedPreferences (simple prototype state)
- Android Notifications (AlarmManager + BroadcastReceiver)
- AdMob (test ad unit IDs)
- Konfetti Compose (celebration/confetti)

## Requirements

- Android Studio (recent version)
- JDK 17 (recommended)
- Android SDK installed

## Run the app

1. Open the project folder in **Android Studio**
2. Let Gradle sync finish
3. Run the `app` configuration on an emulator or device

## Project structure (high level)

- `app/src/main/java/com/example/hydrohero/MainActivity.kt`: App entry + navigation + global overlays/ads
- `app/src/main/java/com/example/hydrohero/ui/screens/`: Compose screens (Home/Reminders/Shop/Settings/Daily Progress)
- `app/src/main/java/com/example/hydrohero/ui/viewmodel/WaterViewModel.kt`: App state + prototype logic
- `app/src/main/java/com/example/hydrohero/data/`: Models + `DataRepository`
- `app/src/main/java/com/example/hydrohero/notifications/`: Reminder scheduling + receiver

## Notes

- AdMob is configured with **test IDs**. Replace them with your own before any real release.
- This repo was built for a school project prototype; accuracy > production hardening.

