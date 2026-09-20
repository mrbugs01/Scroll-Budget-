# Scroll Budget — v0.9

**Naya in v0.9 (fixes based on feedback):**
- **Vibration ab 6 second ka solid buzz hai** (pehle chhote-chhote pulses the)
- **Quick Setup card ab poori tarah gayab ho jaati hai** jab sab permissions ON ho jayein (pehle sirf text badalta tha)
- **Background genuinely vivid ho gaya** — rich violet-to-black diagonal gradient + soft glow, pehle wala bahut subtle tha
- **Claude AI hata ke Gemini laga diya** — Gemini ka **genuinely free tier** hai (Google AI Studio se key milti hai, koi credit card nahi chahiye), Claude ke saath free tier available nahi hota
- **Naya app icon** — "planet with orbit ring" design, futuristic aur premium feel, violet gradient background ke saath

**Gemini API key kahan se milegi**: [aistudio.google.com](https://aistudio.google.com) pe Google account se login karke "Get API Key" — free hai, koi payment method nahi maangta.

---



**Naya in v0.8:**
- **Poori app ab English mein hai** — saare messages, puzzles, trivia, milestone messages, UI text
- **Quick Setup fix**: permission granted hote hi wo row automatically list se hat jaati hai (koi manual refresh nahi chahiye)
- **AI Coach**: apna Claude API key daal ke AI se personalized motivation message le sakte ho (key sirf isी phone pe local store hoti hai, seedha Anthropic ko jaati hai)
- **Reminders**: apna schedule bana sakte ho — jab time aaye, phone **heavy vibrate** karega + notification aayegi
- **Milestone notifications ab background mein bhi chalti hain** — app band ho tab bhi 1h-9h ke rewards ki notification aayegi (foreground popup ki jagah)
- **Premium gradient background** aur refined card design

**Important**: AI Coach ke liye tumhara apna Claude API key chahiye — console.anthropic.com se bana sakte ho. Reminders ke liye Android "Alarms & reminders" permission bhi allow karni pad sakti hai (kuch phones settings mein manually maangte hain — agar reminder na baje, Settings → Apps → Scroll Budget → Alarms & reminders check karna).

---

# Scroll Budget — v0.7

**Naya in v0.7:** Live "Away Streak" timer main screen pe — track karta hai
kitni der ho gayi Instagram/YouTube khole hue. 9 tiers hain (1h se 9h tak):
Stone 🪨 → Silver 🥈 → Gold 🥇 → Emerald 🟢 → Sapphire 🔵 → Ruby 🔴 →
Topaz 🟠 → Amethyst 🟣 → Diamond 💎. Har naya tier unlock hote hi ek detailed
popup aata hai jisme us duration ke mental-health benefits explain hote hain.
Instagram/YouTube khologe toh streak turant reset ho jayegi.

**v0.6 se:** 2 PDF books app ke andar bundle, ek button se phone ke PDF
reader mein khulti hain.

**v0.5 se:** Trivia questions 3 options (A/B/C) mein, sahi answer random position pe.

**v0.4 se:** 30 text-based trivia questions (famous paintings, historical
figures, monuments — Mona Lisa, Alexander the Great, Taj Mahal, etc.).

**v0.3 se:** 100 alag motivational final messages, 4 math puzzle types
(addition, subtraction, multiplication, sequence).

**v0.2 se:** proper app icon, branded dark theme (purple/gold), redesigned
puzzle overlay aur main screen.

Instagram aur YouTube pe daily time budget set karta hai. Budget khatam hone
ke baad ek chhota math puzzle solve karke 10 min extend kar sakte ho
(max 3 baar/din). Uske baad ek final message aayega ki time productive kaam
mein lagao.

## Kaise chalayein

1. Android Studio mein `ScrollBudget` folder open karo (File → Open).
2. Gradle sync hone do (pehli baar thoda time lega).
3. Ek real Android phone connect karo (emulator mein Instagram/YouTube nahi
   milenge properly, isliye real device better hai) aur Run dabao.
4. App khulte hi do permissions on karo:
   - **Enable Accessibility Service** → Settings mein "Scroll Budget" dhundh
     ke ON karo
   - **Enable Overlay Permission** → "Display over other apps" allow karo
5. Ab Instagram ya YouTube kholo aur use karte raho — budget khatam hote hi
   overlay automatically aa jayega.

## Kya customize kar sakte ho

`MonitoredApps.kt` file mein:
- `dailyBudgetMillis` — har app ka daily time limit (abhi 30 min set hai)
- `EXTENSION_MILLIS` — puzzle solve karne pe kitna extra time milta hai
- `MAX_EXTENSIONS_PER_DAY` — din mein max kitni baar extend kar sakte ho
- Naya app add karna ho (jaise Twitter/X) → bas `list` mein ek naya
  `AppConfig` entry add karo, package name pata karne ke liye:
  `adb shell pm list packages | grep twitter`

## Abhi ke MVP ki limitations (aage improve karna hai)

- Puzzle sirf simple addition hai — mushkil level badhana baaki hai
- Adaptive budget logic abhi nahi hai (jo pattern dekh ke khud adjust kare)
  — abhi fixed budget + fixed extensions hai
- Koi analytics/history screen nahi hai — sirf aaj ka data track hota hai
- Samsung jaise kuch phones mein background mein Accessibility Service
  battery-optimization se band ho sakta hai — "Battery → No restrictions"
  Scroll Budget ke liye set karna padega

## Next steps (jab ye chal jaye)

1. Puzzle difficulty ko extension count ke hisaab se badhao (2nd extension
   thoda mushkil, 3rd aur mushkil)
2. Ek simple stats screen banao jo pichle 7 din ka usage graph dikhaye
3. Adaptive budget: agar roz 3 extensions use ho rahe hain, suggest karo
   ki base budget badha lo
4. iOS version ke liye alag approach chahiye hoga (Screen Time API se
   sirf blocking possible hai, custom puzzle overlay nahi — Apple allow
   nahi karta)
