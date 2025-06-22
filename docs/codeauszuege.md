---
title: Codeauszüge
nav_order: 8
---

# Beispiel 1: Upgrade kaufen

```java
public boolean buyUpgrade(Upgrade upgrade) {
   if (score >= upgrade.getPrice()) {
      score -= upgrade.getPrice();
      upgrade.levelUp();
      return true;
   }
   return false;
}
