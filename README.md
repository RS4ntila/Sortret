<div align="center">

# 💊 Sortret

**Android-приложение для отслеживания курса приёма таблеток Сортрет**

[![Version](https://img.shields.io/badge/version-2.4-8264ff?style=flat-square)](.)
[![API](https://img.shields.io/badge/API-26--36-c084fc?style=flat-square)](.)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-38bdf8?style=flat-square&logo=kotlin&logoColor=white)](.)
[![Compose](https://img.shields.io/badge/Jetpack_Compose-✓-f472b6?style=flat-square)](.)

*Ведёт дни курса · считает накопление мг/кг · рисует живое жидкое стекло*

---

> ⚕️ **Важно.** Приложение не является медицинским инструментом и не заменяет врача.
> Расчёты помогают визуально отслеживать курс, но дозировки, сроки и схему приёма
> нужно согласовывать со специалистом.

</div>

---

## 📋 Параметры проекта

| Параметр | Значение |
|---|---|
| Название проекта | `sortret` |
| Package / applicationId | `com.example.sortret` |
| Версия | `2.4` · `versionCode = 24` |
| UI | Kotlin + Jetpack Compose |
| Минимальная версия Android | Android 8.0 · API 26 |
| Целевая версия Android | API 36 |
| Хранение данных | `SharedPreferences` |
| Главный экран | `TrackerScreen` |
| Доменная логика | `TrackerState` |
| Debug APK | `app/build/outputs/apk/debug/app-debug.apk` |
| Подпись | `Made by S4ntila` |

---

## ✨ Что делает приложение

`Sortret` строится вокруг четырёх блоков: **курс**, **дозировки**, **прогресс** и **визуальная оболочка**.

### 📅 Курс

| Настройка | Для чего нужна |
|---|---|
| Дата начала | От неё считается текущий день и дата окончания. |
| Вес | Расчёт накопления в мг/кг. |
| Целевая доза | Общая цель курса в миллиграммах. |
| Первый приём | Утро или вечер — влияет на математику, не только на UI. |

> Если первый приём — вечерний, приложение пропускает утреннюю дозу в первый день.
> Это реализовано в расчётной логике, а не только в интерфейсе.

### ⏰ График приёма

| Период | Настраивается |
|---|---|
| Утро | Время приёма и дозировка в мг. |
| Вечер | Время приёма и дозировка в мг. |

Суточная доза считается автоматически:

```kotlin
val dailyDose: Float
    get() = morningDose + eveningDose
```

### 📊 Прогресс

```kotlin
val progressPercent = (stats.cumulativeDose / state.targetTotalDose).coerceIn(0f, 1f)
```

| Показатель | Что означает |
|---|---|
| Процент курса | Сколько набрано от целевой общей дозы. |
| Накопленная доза | Сколько мг уже учтено в курсе. |
| Накопление на кг | мг, набранных на килограмм веса. |
| Дни курса | Прошедшие и расчётные общие дни. |
| Дата окончания | До какого числа продлится курс при текущей схеме. |

Если приложение было закрыто до времени приёма, а открыто уже после него, главный экран сначала показывает последнюю сохранённую накопленную дозу. Через короткую паузу target обновляется до актуального расчёта, и процент/мг плавно пополняются стандартной Compose-анимацией.

### 🎨 Визуальные настройки

| Настройка | Что меняет |
|---|---|
| Палитра фона | Цветовую схему живого фона: первые 4 темы в быстрых настройках и полный каталог из 14 палитр через `Расширить список`. |
| Статичная картинка | Пользовательский фон через системный выбор изображения; URI и accent-цвет картинки сохраняются в настройках приложения. |
| Скорость анимации | Темп движения blob-облаков. |
| Скругление | Радиус стеклянных карточек. |
| Высота преломления | Ширина зоны эффекта у края стекла. |
| Смещение преломления | Сила сдвига пикселей под стеклом. |
| Насыщенность | Saturation в `colorControls()` поверх backdrop. |
| Контраст | Контраст содержимого под стеклом. |
| Белая точка | Мягкая brightness-коррекция backdrop. |
| Тонировка стекла | Цветовая примесь поверхности. |
| Радиус размытия | Blur под стеклом. |
| Дисперсия | Включение chromatic aberration по краям линзы. |

---

## 🏗️ Архитектура

```
MainActivity
└── TrackerScreen
    ├── rememberTrackerState()
    │   ├── TrackerState
    │   ├── PersistedTrackerState
    │   └── SharedPreferences
    ├── AnimatedBackground / StaticImageBackground
    │   └── BackgroundPalettes
    ├── GlassCard
    │   └── Backdrop drawBackdrop + RoundedRectangle + lens
    ├── TrackerHeader / ProgressCard / StatsCards
    ├── ScheduleCard / CourseCard / TrackerIcons
    ├── CardInfoSheet
    ├── CourseSettingsSheet
    ├── ScheduleSettingsSheet
    └── VisualSettingsSheet
        ├── BackgroundPicker
        └── BackdropDialog
```

Компактный Compose-проект без отдельного ViewModel. Состояние живёт в `TrackerState`,
UI читает и изменяет его напрямую через `mutableStateOf` / `mutableFloatStateOf`.

---

## 📁 Структура файлов

```
sortret/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/sortret/
│       │   ├── MainActivity.kt
│       │   ├── logic/
│       │   │   └── TrackerState.kt
│       │   └── ui/
│       │       ├── components/
│       │       │   ├── AnimatedBackground.kt
│       │       │   ├── BackgroundPalettes.kt
│       │       │   ├── BackgroundPicker.kt
│       │       │   ├── BackdropDialog.kt
│       │       │   ├── CardInfoSheet.kt
│       │       │   ├── CourseSettingsSheet.kt
│       │       │   ├── DampedDragAnimation.kt
│       │       │   ├── DragGestureInspector.kt
│       │       │   ├── GlassCard.kt
│       │       │   ├── GlassInput.kt
│       │       │   ├── ImageBackgroundTools.kt
│       │       │   ├── LiquidSlider.kt
│       │       │   ├── ScheduleSettingsSheet.kt
│       │       │   ├── SettingsSheetChrome.kt
│       │       │   ├── SoftPress.kt
│       │       │   ├── StaticImageBackground.kt
│       │       │   ├── VisualAccents.kt
│       │       │   ├── VisualSettingsControls.kt
│       │       │   └── VisualSettingsSheet.kt
│       │       └── screens/
│       │           ├── CourseCard.kt
│       │           ├── ProgressCard.kt
│       │           ├── ScheduleCard.kt
│       │           ├── StatsCards.kt
│       │           ├── TrackerGlassCard.kt
│       │           ├── TrackerClock.kt
│       │           ├── TrackerHeader.kt
│       │           ├── TrackerIcons.kt
│       │           └── TrackerScreen.kt
│       └── res/values/
│           ├── strings.xml
│           └── styles.xml
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
└── gradle/wrapper/
```

### Ключевые файлы

| Файл | Роль |
|---|---|
| `MainActivity.kt` | Edge-to-edge, прозрачные системные панели, поддержка cutout. |
| `TrackerState.kt` | Настройки курса, расписание, visual-параметры, статистика. |
| `TrackerScreen.kt` | Главный экран-оркестратор: фон, scroll, sheets и состояние. |
| `TrackerClock.kt` | Единый минутный ticker для автообновления времени, процентов и статистики при открытом приложении. |
| `TrackerHeader.kt` | Заголовок, дата, часы и кнопка настроек. |
| `ProgressCard.kt` | Большая карточка прогресса и круговой индикатор. |
| `StatsCards.kt` | Карточки мг/кг и дней курса. |
| `ScheduleCard.kt` | Карточка графика приёма и строки утро/вечер. |
| `CourseCard.kt` | Карточка быстрых настроек курса. |
| `TrackerIcons.kt` | Canvas-иконки календаря и таблетки. |
| `CardInfoSheet.kt` | Информационные панели по нажатию на карточки. |
| `CourseSettingsSheet.kt` | Настройка даты начала, веса, целевой дозы и первого приёма. |
| `ScheduleSettingsSheet.kt` | Настройка утреннего/вечернего времени и дозировки. |
| `VisualSettingsSheet.kt` | Настройки фона и жидкого стекла. |
| `BackgroundPicker.kt` | Компактный список первых 4 тем и полный каталог фонов. |
| `BackdropDialog.kt` | Общая inline-стеклянная поверхность для каталога фонов, даты и точного времени. |
| `AnimatedBackground.kt` | Canvas-renderer живого фона: blob-формы и flare-слои. |
| `StaticImageBackground.kt` | Пользовательский статичный фон из системного picker: decode URI, crop на весь экран и мягкий readability-overlay. |
| `BackgroundPalettes.kt` | Модель `BackgroundPalette`, accent-helpers и каталог тем. |
| `GlassCard.kt` | Универсальная стеклянная карточка на `drawBackdrop`, `RoundedRectangle`, `vibrancy`, `blur`, `colorControls` и `lens`. |
| `ImageBackgroundTools.kt` | Decode статичной картинки и извлечение доминирующего accent-цвета для настроек. |
| `VisualAccents.kt` | Выбирает accent-цвета: из палитры живого фона или из статичной картинки. |
| `LiquidSlider.kt` | Собственный слайдер для настроек стекла. |
| `GlassInput.kt` | Числовой ввод дозировок, веса и целевой дозы. |

---

## 🔬 Как считается курс

Расчёт находится в `logic/TrackerState.kt`.

### Состояние курса

```kotlin
var weight          by mutableFloatStateOf(TrackerDefaults.WEIGHT)
var startDate       by mutableStateOf(TrackerDefaults.START_DATE)
var startShift      by mutableStateOf(TrackerDefaults.START_SHIFT)
var targetTotalDose by mutableFloatStateOf(TrackerDefaults.TARGET_TOTAL_DOSE)
var morningTime     by mutableStateOf(TrackerDefaults.MORNING_TIME)
var morningDose     by mutableFloatStateOf(TrackerDefaults.MORNING_DOSE)
var eveningTime     by mutableStateOf(TrackerDefaults.EVENING_TIME)
var eveningDose     by mutableFloatStateOf(TrackerDefaults.EVENING_DOSE)
```

**Значения по умолчанию:**

| Параметр | Значение |
|---|---|
| Вес | `68 кг` |
| Целевая доза | `8 160 мг` |
| Дата начала | `2026-03-16` |
| Первый приём | Вечер |
| Утро | `09:00` · `20 мг` |
| Вечер | `19:00` · `30 мг` |
| Суточная доза | `50 мг` |
| Период полувыведения | `20 часов` |

### Кумулятивная доза

Приложение проходит по каждому дню от начала до текущего и добавляет дозы, которые уже должны были быть приняты:

```kotlin
for (dayOffset in 0..daysSinceStart) {
    val date = startDate.plusDays(dayOffset.toLong())

    if (dayOffset > 0 || startShift == StartShift.MORNING) {
        val doseTime = LocalDateTime.of(date, morningTime)
        if (morningDose > 0f && !now.isBefore(doseTime)) totalDose += morningDose
    }

    val doseTime = LocalDateTime.of(date, eveningTime)
    if (eveningDose > 0f && !now.isBefore(doseTime)) totalDose += eveningDose
}
```

### Накопление на килограмм

```kotlin
cumulativePerKg = totalDose / weight
```

### Активное вещество

Помимо кумулятивной дозы считается примерное активное количество с учётом экспоненциального распада:

```kotlin
val halfLifeHours  = 20.0
val decayConstant  = ln(2.0) / halfLifeHours
currentActiveAmount += dose * exp(-decayConstant * hoursPassed)
```

> Это не медицинская модель — визуальная расчётная оценка для интерфейса.

### Дата окончания курса

```kotlin
val dailyDose  = state.dailyDose.coerceAtLeast(1f)
val courseDays = ceil(state.targetTotalDose / dailyDose).toInt().coerceAtLeast(1)
val endDate    = state.startDate.plusDays((courseDays - 1).toLong())
```

---

## 💾 Как сохраняются настройки

Все настройки хранятся в `SharedPreferences` с именем `sortret_tracker_settings`.
Сохранение сделано через Compose `snapshotFlow`:

```kotlin
LaunchedEffect(state, preferences) {
    snapshotFlow { state.toPersistedState() }
        .distinctUntilChanged()
        .collect { persistedState ->
            persistedState.saveTo(preferences)
        }
}
```

```kotlin
preferences.edit()
    .putFloat(Keys.WEIGHT, weight)
    .putString(Keys.START_DATE, startDate)
    .putFloat(Keys.TARGET_TOTAL_DOSE, targetTotalDose)
    .putString(Keys.MORNING_TIME, morningTime)
    .putFloat(Keys.MORNING_DOSE, morningDose)
    .putString(Keys.EVENING_TIME, eveningTime)
    .putFloat(Keys.EVENING_DOSE, eveningDose)
    .apply()
```

При следующем запуске `TrackerState` восстанавливает значения и безопасно парсит даты через `runCatching`.

---

## 🖥️ Главный экран

| Блок | Реализация |
|---|---|
| Заголовок | Название `Сортрет`, текущая дата и время. |
| Прогресс | Стеклянная карточка с круговой диаграммой и процентом. |
| Накопление | Карточка с мг/кг. |
| Дни курса | Текущий день и общее количество. |
| График приёма | Утреннее/вечернее время, доза, итог за сутки. |
| Настройки курса | Дата начала, вес, целевая доза. |
| Sheets | Bottom sheets для настроек и пояснений. |

---

## 🌊 Жидкое стекло — как устроено

Эффект — это не картинка, а цепочка из библиотечных Backdrop-слоёв. В версии `2.4` карточки переведены на подход из `catalog/destinations/GlassPlaygroundContent.kt`: форма задаётся через `com.kyant.shapes.RoundedRectangle`, а оптика собирается из `vibrancy()`, `blur()`, `colorControls()` и `lens()`.

1. `BackgroundPalettes` хранит модель тем, цвета и accent-helpers отдельно от renderer.
2. `AnimatedBackground` рисует Canvas каждый кадр через `withFrameNanos`, а `StaticImageBackground` может заменить его пользовательской картинкой.
3. `rememberLayerBackdrop()` захватывает слой фона как backdrop.
4. `GlassCard` применяет `drawBackdrop` к форме `RoundedRectangle`.
5. Внутри `drawBackdrop` включаются `colorControls()`, `vibrancy()`, `blur()` и `lens()`.
6. Явный `border` и `Highlight.Plain` у карточек/окон отключены, чтобы при изменении скругления не появлялись светлые артефакты по краям.

### Форма без ошибки скругления

Карточки используют общий helper:

```kotlin
internal fun sortretGlassShape(radiusDp: Float): Shape {
    return RoundedRectangle(radiusDp.coerceAtLeast(0f).dp)
}
```

Это важно для настройки `Скругление = 0 dp`: `RoundedRectangle(0.dp)` передаётся напрямую в Backdrop и библиотечный `lens()` получает реальные нулевые corner-radii. Поэтому карточка превращается в квадрат, а не остаётся визуально округлённой из-за старого кастомного shader-радиуса.

### Backdrop-цепочка карточки

```kotlin
GlassCard(
    shape = sortretGlassShape(state.cornerRadius),
    blurRadius = state.blurRadius,
    refractionHeight = state.refractionHeight,
    refractionOffset = state.refractionOffset,
    dispersion = state.dispersion,
    contrast = state.glassContrast,
    whitePoint = state.glassWhitePoint,
    chromaMultiplier = state.glassChromaMultiplier
)
```

Внутри карточки параметры применяются так:

```kotlin
drawBackdrop(
    backdrop = backdrop,
    shape = { shape },
    effects = {
        colorControls(
            brightness = whitePoint,
            contrast = 1f + contrast,
            saturation = chromaMultiplier
        )
        vibrancy()
        blur(blurRadius.dp.toPx())
        lens(
            refractionHeight = refractionHeight.dp.toPx(),
            refractionAmount = refractionOffset.dp.toPx(),
            depthEffect = true,
            chromaticAberration = dispersion > 0.01f
        )
    },
    highlight = null
)
```

`lens()` внутри библиотеки работает через RuntimeShader на Android 13+, а на более старых Android gracefully пропускает shader-преломление. Остальные слои остаются безопасными для приложения.

### Живой фон

```kotlin
LaunchedEffect(Unit) {
    var lastFrameNanos = 0L
    while (true) {
        withFrameNanos { frameNanos ->
            val delta = ((frameNanos - lastFrameNanos) / 1_000_000_000f).coerceAtMost(0.05f)
            time = (time + delta * currentSpeed / BASE_CYCLE_SECONDS) % 1f
            lastFrameNanos = frameNanos
        }
    }
}
```

| Слой | Что делает |
|---|---|
| Gradient base | Большой плавный градиент. |
| Soft flares | Мягкие световые пятна. |
| Background forms | Полупрозрачные округлые формы. |
| Background blobs | Движущиеся цветные облака. |

### Статичный фон-картинка

В настройках фона есть кнопка `Выбрать картинку`. Она открывает системный `OpenDocument` picker, приложение берёт persistable read-permission и сохраняет URI в `TrackerState.staticBackgroundUri`.

```kotlin
val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
    if (uri != null) {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        state.staticBackgroundUri = uri.toString()
    }
}
```

Если URI заполнен, главный экран вместо `AnimatedBackground` использует `StaticImageBackground`: изображение декодируется через `ImageDecoder` / `BitmapFactory`, растягивается `ContentScale.Crop` на весь экран и получает мягкий затемняющий overlay для читаемости карточек.

Дополнительно `ImageBackgroundTools` декодирует уменьшенную копию изображения, выбирает самый выразительный доминирующий цвет и сохраняет его в `staticBackgroundAccentArgb`. Этот цвет используется в `VisualAccents`, поэтому слайдеры, кнопки, панели настроек и кольцо прогресса подстраиваются под выбранную картинку.

---

## 🔧 Как устроены настройки

### Настройки курса — `CourseSettingsSheet`

| Поле | Компонент |
|---|---|
| Дата начала | Inline `BackdropDialogSurface` + `DatePicker` |
| Вес | `GlassInput` |
| Целевая доза | `GlassInput` |
| Таблетки 20 мг | `GlassInput` |
| Таблетки 10 мг | `GlassInput` |
| Первый приём | Кнопки `Утро` / `Вечер` |

### График приёма — `ScheduleSettingsSheet`

| Поле | Компонент |
|---|---|
| Утреннее время | Кнопки `−15` / `+15` и точные поля `Часы` / `Минуты` без кнопок подтверждения |
| Утренняя дозировка | `GlassInput` |
| Вечернее время | Кнопки `−15` / `+15` и точные поля `Часы` / `Минуты` без кнопок подтверждения |
| Вечерняя дозировка | `GlassInput` |

Окно точного времени применяет значение при закрытии. Если часы или минуты пустые либо выходят за диапазон `0-23` / `0-59`, старое время остаётся без изменений.

Количество таблеток `20 мг` и `10 мг` вводится как текущий остаток на руках. После наступления времени приёма приложение автоматически списывает нужные капсулы: для `20 мг` — `1 x 20 мг`, для `30 мг` — `1 x 20 мг + 1 x 10 мг`. Карточка графика приёма показывает, на сколько дней ещё хватит этого остатка по текущему расписанию.

---

## 📝 История версий

В версии `2.4` сделан структурный рефактор и ускорена система окон: крупные Compose-файлы разделены на маленькие компоненты, мёртвый highlight-код удалён, sheets/dialogs переведены на inline Backdrop-слои по примеру `catalog/DialogContent`, а стекло карточек приведено к схеме `catalog/GlassPlaygroundContent`.

| Версия | Что изменилось |
|---|---|
| `2.4` | Удалён неиспользуемый `InteractiveHighlight.kt`, `softPressClick()` теперь использует `highlightColor`, `GlassSettingsSheet` разложен на visual sheet / picker / dialog, `TrackerScreen` разделён на карточки и иконки, `AnimatedBackground` отделён от `BackgroundPalettes`, `ModalBottomSheet`/platform `Dialog` заменены на inline Backdrop-окна, добавлен ticker для автообновления статистики при открытом приложении, changelog убран из приложения и оставлен в README, добавлен sticky-предпросмотр стекла, статичный фон-картинка, авто-accent по доминирующему цвету картинки, цветное кольцо прогресса от кастомного фона, догоняющая анимация дозы после запуска, исправлено скругление `0 dp` через `RoundedRectangle`, убраны контуры у карточек и окон, удалена некорректная числовая настройка глубины линзы, точное время переведено на поля с автоприменением при закрытии, добавлен учёт остатка таблеток `20 мг` и `10 мг`. |
| `2.3` | Убрана системная overscroll-полоса при прокрутке за край, полноэкранный blur заменён на лёгкое затемнение без задержки после закрытия, changelog получил короткий заголовок `Changelog` без лишней поясняющей строки. |
| `2.2` | Добавлен changelog-диалог, кнопка `Показать еще фоны`, отдельное окно полного каталога фонов, 4 новые палитры и компактный список первых 4 тем в настройках. |
| `2.1` | Выполнено переименование package/applicationId в `com.example.sortret`, лаунчер стал `Sortret`, заголовок внутри приложения стал `Сортрет`, добавлена подпись `Made by S4ntila`, параметры стеклянных карточек вынесены в `SortretGlassCard`. |
| `2.0` | Проект переведен на `io.github.kyant0:backdrop`: карточки, панели и слайдеры используют `drawBackdrop`, `blur`, `vibrancy`, `lens` и кастомное shader-преломление. |

Система окон теперь повторяет подход из `catalog/src/main/.../DialogContent.kt`: окно не создаёт отдельный platform `Dialog`, а рисуется inline в той же Compose-сцене. Это уменьшает задержки при открытии/закрытии, не создаёт лишнее platform window и позволяет держать один лёгкий `drawBackdrop`-pass.

```kotlin
Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .background(Color.Black.copy(alpha = 0.18f))
            .clickable(onClick = onDismiss)
    )

    Box(
        modifier = Modifier.drawBackdrop(
            backdrop = backdrop,
            shape = { RoundedCornerShape(30.dp) },
            effects = {
                colorControls(brightness = 0.02f, saturation = 1.35f)
                blur(8.dp.toPx())
                lens(14.dp.toPx(), 42.dp.toPx(), depthEffect = true)
            }
        )
    ) {
        // каталог фонов, выбор даты или точное время
    }
}
```

---

## 📦 Зависимости

| Зависимость | Для чего |
|---|---|
| Android Gradle Plugin `8.7.0` | Сборка Android-приложения. |
| Kotlin `2.3.0` | Kotlin и Compose compiler plugin. |
| Gradle Wrapper `8.9` | Версия Gradle для проекта. |
| `androidx.core:core-ktx:1.12.0` | Базовые KTX-расширения. |
| `androidx.lifecycle:lifecycle-runtime-ktx:2.7.0` | Lifecycle runtime. |
| `androidx.activity:activity-compose:1.8.2` | Запуск Compose из Activity. |
| Compose BOM `2024.02.00` | Версии Compose UI и tooling-preview. |
| `material3:1.2.0` | Bottom sheets, dialogs, DatePicker, Text, IconButton. |
| `material-icons-extended:1.6.2` | Иконки. |
| `io.github.kyant0:backdrop:1.0.6` | Backdrop, blur, vibrancy, lens. |
| `io.github.kyant0:shapes:1.0.1` | Capsule shape для slider и rounded forms. |

---

## 🚀 Сборка

### Windows PowerShell

```powershell
$env:JAVA_HOME        = "C:\Program Files\Android\Android Studio\jbr"
$env:ANDROID_HOME     = "$env:LOCALAPPDATA\Android\Sdk"
$env:ANDROID_SDK_ROOT = "$env:LOCALAPPDATA\Android\Sdk"
.\gradlew.bat :app:assembleDebug
```

APK появится здесь:

```
app/build/outputs/apk/debug/app-debug.apk
```

### Установка на устройство

```powershell
$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
& $adb install -r app/build/outputs/apk/debug/app-debug.apk
& $adb shell am start -n com.example.sortret/.MainActivity
```

### Android Studio

1. Откройте папку `sortret`.
2. Убедитесь, что AS использует JDK из `C:\Program Files\Android\Android Studio\jbr`.
3. Проверьте `local.properties`:
   ```properties
   sdk.dir=C\:\\Users\\Alex\\AppData\\Local\\Android\\Sdk
   ```
4. В списке устройств выберите подключённый Pixel 9.
5. Запустите конфигурацию `app`.

---

## ⚠️ Ограничения

| Область | Комментарий |
|---|---|
| Медицина | Нужна проверка формулировок и явный disclaimer в UI. |
| Android 8–12 | Shader-преломление отключается ниже Android 13. |
| Производительность | Canvas-фон, blur и RuntimeShader могут быть тяжёлыми на слабых устройствах. |
| Тесты | Unit/UI-тестов нет. В первую очередь — `TrackerState.calculateStats()`. |
| Архитектура | При росте проекта лучше добавить ViewModel. |
| Локализация | Строки в Kotlin, не в `strings.xml`. |
| Иконка | Используется системная fallback-иконка. Для публикации нужна adaptive icon. |
| Доступность | Нужно доработать `contentDescription` и semantics. |

---

## 🗺️ Что улучшить

| Приоритет | Задача |
|---|---|
| 🔴 High | Добавить unit-тесты на расчёт курса, дозировок и даты окончания. |
| 🔴 High | Добавить явный медицинский disclaimer прямо в приложение. |
| 🔴 High | Подготовить собственную иконку `Sortret`. |
| 🟡 Medium | Вынести строки интерфейса в `strings.xml` / `values-ru`. |
| 🟡 Medium | Добавить `.gitignore` для build-артефактов и `local.properties`. |
| 🟡 Medium | Добавить ViewModel для отделения UI от расчётного состояния. |
| 🟢 Low | Добавить режим сниженной анимации для экономии батареи. |

---

<div align="center">

Made with 🖤 by **S4ntila** · `com.example.sortret` · v2.4

</div>
