# Visual Watch

Timer visivo per terapie, pensato prima per Android. Scegli o crea un preset (durata e soglia di avviso), avvia e lascia l'app aperta: il quadrante mostra il tempo restante, diventa ambra alla soglia definita e mantiene lo schermo acceso **solo durante una sessione**. Il disegno viene aggiornato una volta al secondo per contenere i consumi. Il selettore **TEMA** salva la scelta fra Salvia, Oceano, Tramonto, Lavanda e Mono.

## Modalità ridotta e One UI

Il pulsante **MINI TIMER** usa Picture-in-Picture Android: rimane in un piccolo riquadro sopra le altre app, con il cronometro funzionante. In PiP l'interfaccia diventa un riquadro stretto attorno al quadrante, con superficie traslucida, bordo luminoso e trattamento *liquid glass*. Android non garantisce finestre PiP realmente trasparenti su tutti i launcher/dispositivi, perciò questa soluzione mantiene sempre leggibilità e contrasto. Per risparmiare batteria, ferma sempre la sessione quando non serve.

Le “pill” di One UI sono un'integrazione proprietaria Samsung e non espongono una API pubblica utilizzabile da app terze; l'app è quindi progettata con PiP standard e icona adaptive/monocromatica, compatibili con Material You/Monet e launcher Samsung. Una futura integrazione Live Updates potrà essere aggiunta quando l'API Android e One UI sarà disponibile in modo stabile.

## Build locale

Serve Android SDK Platform 35. Esegui `gradle :app:assembleDebug`. La GitHub Action produce un APK a ogni push e pubblica l'APK di release quando viene creato un tag `v*`.
