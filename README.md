# Reach Hitboxes (Minecraft Fabric 1.16.5)

Client-side mod, Combat Hitboxes jaisa. Dusre players/mobs ke hitboxes mote aur coloured dikhte hain, aur jis entity ko tum crosshair se target kar rahe ho (reach ke andar) uska hitbox colour badal leta hai (default laal).

- Hitbox line ki motai (width) badal sakte ho
- Har part ka alag colour aur ON/OFF: main box, eye-height box, look line
- Target colour: jab entity reach ke andar crosshair pe ho
- Vanilla F3+B ki tarah deewar ke peeche ke hitbox nahi dikhte (depth test), aur invisible/spectator entities skip hote hain
- Agar server "reduced debug info" laga de to hitboxes band ho jate hain
- Sirf apne client pe chahiye, server pe install nahi karna. Fabric API chahiye (tumhare paas hai).

Note: F3+B (vanilla hitboxes) ko **OFF** rakho, warna dono saath dikhenge. Ye mod apne alag hitboxes draw karta hai.

## IMPORTANT

Ye zip ya iska koi folder `mods` folder mein mat daalna. Mods folder mein sirf `.jar` file jati hai. Pehle neeche ke steps se `.jar` banao.

## Jar banana (GitHub Actions, kuch install nahi karna)

1. Is zip ko **Extract All** karo (right click karke), taaki ek normal folder ban jaye.
2. github.com pe free account banao, phir **New repository** dabake `reachhitboxes` naam ka repo banao.
3. Repo ke page pe **uploading an existing file** link dabao. Extract kiye hue folder ke **andar ki saari cheezein** (`src` folder, `.github` folder, `build.gradle`, `gradle.properties`, `settings.gradle`, `.gitignore`, `README.md`) drag karke daal do. Phir **Commit changes** dabao.
4. Upar **Actions** tab kholo. Agar "enable workflows" jaisa button dikhe to dabao. "Build mod" chalega (na chale to usse chuno aur **Run workflow** dabao).
5. 3 se 6 minute baad green tick aayega. Us run pe click karo, neeche **Artifacts** mein `reachhitboxes-jar` download karo.
6. Wo download ek zip hoga. Use extract karo, andar `reachhitboxes-fabric-1.16.5-1.0.0.jar` milegi. Yahi file `.minecraft/mods` mein daalni hai.

Agar build fail ho to run ke andar ka red error text copy karke mujhe bhej do.

## Use karna

- `H` dabao = mod ON/OFF (Controls mein "Reach Hitboxes" category se key badal sakte ho)
- Settings: `.minecraft/config/reachhitboxes.properties` (pehli baar game chalane pe ban jati hai, change ke baad game restart karo)
  - `lineWidth`: line ki motai
  - `playersOnly`: true karoge to sirf players ke hitbox dikhenge
  - `boxEnabled` / `boxColor`: main hitbox
  - `targetEnabled` / `targetColor`: crosshair wale entity ka colour
  - `eyeHeightEnabled` / `eyeHeightColor`, `lookVectorEnabled` / `lookVectorColor`
  - Colours hex mein, jaise `#FF0000`

## Apne PC pe banana (optional)

JDK 17 aur Gradle 7.3.3 install karo, phir project folder mein `gradle build` chalao. Jar `build/libs/` mein milegi.
