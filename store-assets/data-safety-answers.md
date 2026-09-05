# Play Console — Data Safety form answers

Fill this in at **Play Console → App content → Data safety**. Every answer below
was checked against the actual code, not assumed.

Verified from the source:
- Permissions declared: `INTERNET`, `ACCESS_NETWORK_STATE`, `POST_NOTIFICATIONS` — nothing else
- SDKs: Firebase Analytics, Firebase Crashlytics, Google AdMob, Google Play Billing
- All hydration data is written to `SharedPreferences` on-device; no upload path exists

---

## Overview

| Question | Answer |
|---|---|
| Does your app collect or share any of the required user data types? | **Yes** |
| Is all of the user data collected by your app encrypted in transit? | **Yes** (the Google SDKs use HTTPS) |
| Do you provide a way for users to request that their data is deleted? | **Yes** — Settings → Reset progress deletes on-device data; uninstalling ends all collection |

> Answer "Yes" to collection because the Google SDKs collect data, even though
> your own code uploads nothing.

---

## Data types to declare

### Location → Approximate location
- Collected: **Yes** · Shared: **Yes**
- Processed ephemerally: No
- Required or optional: **Optional** (only for users who see ads)
- Purposes: **Advertising or marketing**, **Analytics**
- *Why:* AdMob and Analytics derive country-level location from IP. You never request the location permission.

### Personal info → User IDs
- Collected: **Yes** · Shared: **Yes**
- Required or optional: **Required**
- Purposes: **Advertising or marketing**, **Analytics**
- *Why:* the advertising ID (AdMob) and the Firebase app instance ID.

### App activity → App interactions
- Collected: **Yes** · Shared: **No**
- Required or optional: **Required**
- Purposes: **Analytics**
- *Why:* Firebase Analytics screen views and the events `water_add`, `goal_completed`, `subscription_dialog_open`, `onboarding_completed`, `premium_activated`, `notifications_toggle`.

### App info and performance → Crash logs
- Collected: **Yes** · Shared: **No**
- Required or optional: **Required**
- Purposes: **Analytics** (and **App functionality** if offered)
- *Why:* Crashlytics.

### App info and performance → Diagnostics
- Collected: **Yes** · Shared: **No**
- Required or optional: **Required**
- Purposes: **Analytics**
- *Why:* Crashlytics custom keys (coins, streak, daily goal, current intake, premium status) and performance data.

### Device or other IDs
- Collected: **Yes** · Shared: **Yes**
- Required or optional: **Required**
- Purposes: **Advertising or marketing**, **Analytics**

---

## Data types to declare as NOT collected

Leave every one of these unchecked — the code touches none of them:

- ❌ **Health and fitness** — the water log never leaves the device, so it is not "collected" for Data Safety purposes. There is no Google Fit or Health Connect integration.
- ❌ Name, email address, phone number, address, race, political or religious beliefs, sexual orientation
- ❌ Financial info — Google Play handles payment; your app never sees card details
- ❌ Precise location
- ❌ Contacts, calendar, photos, videos, audio, files, SMS, call logs
- ❌ Web browsing history
- ❌ Installed apps

---

## Ads and content

| Question | Answer |
|---|---|
| Does your app contain ads? | **Yes** |
| Ad format | Banner, Interstitial, Rewarded |
| Target audience | **Not directed at children** (13+) |
| Does it use advertising ID? | **Yes** — declare it; `AD_ID` is already merged into the manifest by the AdMob SDK |

## Also required elsewhere in App content

- **Privacy policy URL** — must be public. Paste the published URL, not a `/edit` link.
- **Financial features** — select "None of these": the app does not offer loans, investment, insurance or money transfer. In-app purchases are not a financial feature.
- **Data deletion** — point at Settings → Reset progress, and your email `danielsela96@gmail.com`.
- **Government apps** — No.
- **News app** — No.

---

⚠️ **Keep this in sync.** If you later add cloud sync or sign-in, the "Health and
fitness" and "Personal info → Email address" answers change. Declaring data safety
inaccurately is one of the more common causes of app suspension.
