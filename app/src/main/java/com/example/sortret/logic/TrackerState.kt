package com.example.sortret.logic

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.roundToInt

enum class StartShift {
    MORNING, EVENING
}

data class DayDoseEntry(
    val morningDose: Float,
    val morningTaken: Boolean,
    val morningManual: Boolean,
    val eveningDose: Float,
    val eveningTaken: Boolean,
    val eveningManual: Boolean,
    val morningCount20: Int? = null,
    val morningCount10: Int? = null,
    val eveningCount20: Int? = null,
    val eveningCount10: Int? = null
) {
    fun serialize(): String {
        val base = "$morningDose,$morningTaken,$morningManual,$eveningDose,$eveningTaken,$eveningManual"
        if (morningCount20 != null || morningCount10 != null || eveningCount20 != null || eveningCount10 != null) {
            return "$base,${morningCount20 ?: -1},${morningCount10 ?: -1},${eveningCount20 ?: -1},${eveningCount10 ?: -1}"
        }
        return base
    }

    companion object {
        fun deserialize(s: String): DayDoseEntry? {
            val parts = s.split(",")
            if (parts.size < 6) return null
            val morningDose = parts[0].toFloatOrNull() ?: 0f
            val morningTaken = parts[1].toBoolean()
            val morningManual = parts[2].toBoolean()
            val eveningDose = parts[3].toFloatOrNull() ?: 0f
            val eveningTaken = parts[4].toBoolean()
            val eveningManual = parts[5].toBoolean()
            
            var m20: Int? = null
            var m10: Int? = null
            var e20: Int? = null
            var e10: Int? = null
            if (parts.size >= 10) {
                m20 = parts[6].toIntOrNull()?.takeIf { it >= 0 }
                m10 = parts[7].toIntOrNull()?.takeIf { it >= 0 }
                e20 = parts[8].toIntOrNull()?.takeIf { it >= 0 }
                e10 = parts[9].toIntOrNull()?.takeIf { it >= 0 }
            }
            return DayDoseEntry(
                morningDose = morningDose,
                morningTaken = morningTaken,
                morningManual = morningManual,
                eveningDose = eveningDose,
                eveningTaken = eveningTaken,
                eveningManual = eveningManual,
                morningCount20 = m20,
                morningCount10 = m10,
                eveningCount20 = e20,
                eveningCount10 = e10
            )
        }
    }
}

data class DosePeriod(
    val startDate: LocalDate,
    val morningDose: Float,
    val eveningDose: Float
) {
    fun serialize(): String {
        return "$startDate,$morningDose,$eveningDose"
    }

    companion object {
        fun deserialize(s: String): DosePeriod? {
            val parts = s.split(",")
            if (parts.size < 3) return null
            val date = runCatching { LocalDate.parse(parts[0]) }.getOrNull() ?: return null
            return DosePeriod(
                startDate = date,
                morningDose = parts[1].toFloatOrNull() ?: 0f,
                eveningDose = parts[2].toFloatOrNull() ?: 0f
            )
        }
    }
}

private object TrackerDefaults {
    const val CORNER_RADIUS = 15f
    const val REFRACTION_HEIGHT = 30f
    const val REFRACTION_OFFSET = 50f
    const val BLUR_RADIUS = 2.5f
    const val DISPERSION = 0.5f
    const val TINT_ALPHA = 0f
    const val GLASS_CONTRAST = 0f
    const val GLASS_WHITE_POINT = 0f
    const val GLASS_CHROMA_MULTIPLIER = 1f
    const val BACKGROUND_PALETTE_INDEX = 0
    const val BACKGROUND_ANIMATION_SPEED = 1f
    const val STATIC_BACKGROUND_URI = ""
    const val STATIC_BACKGROUND_ACCENT_ARGB = 0
    const val LAST_OBSERVED_CUMULATIVE_DOSE = -1f
    const val TABLET_STOCK_SYNC_AT = ""
    const val WEIGHT = 68f
    const val TARGET_TOTAL_DOSE = 8160f
    const val TABLET_COUNT_20 = 0
    const val TABLET_COUNT_10 = 0
    const val MORNING_DOSE = 20f
    const val EVENING_DOSE = 30f

    val GLASS_COLOR = Color.White
    val START_DATE: LocalDate = LocalDate.of(2026, 3, 16)
    val START_SHIFT: StartShift = StartShift.EVENING
    val MORNING_TIME: LocalTime = LocalTime.of(9, 0)
    val EVENING_TIME: LocalTime = LocalTime.of(19, 0)
}

internal data class PersistedTrackerState(
    val cornerRadius: Float,
    val refractionHeight: Float,
    val refractionOffset: Float,
    val blurRadius: Float,
    val dispersion: Float,
    val tintAlpha: Float,
    val glassColorArgb: Int,
    val glassContrast: Float,
    val glassWhitePoint: Float,
    val glassChromaMultiplier: Float,
    val backgroundPaletteIndex: Int,
    val backgroundAnimationSpeed: Float,
    val staticBackgroundUri: String,
    val staticBackgroundAccentArgb: Int,
    val lastObservedCumulativeDose: Float,
    val tabletStockSyncAt: String,
    val weight: Float,
    val startDate: String,
    val startShift: String,
    val targetTotalDose: Float,
    val tabletCount20: Int,
    val tabletCount10: Int,
    val morningTime: String,
    val morningDose: Float,
    val eveningTime: String,
    val eveningDose: Float,
    val manualDosesJson: String,
    val dosePeriodsJson: String,
    val courseSetupCompleted: Boolean,
    val lostCount20: Int,
    val lostCount10: Int
) {
    fun saveTo(preferences: SharedPreferences) {
        preferences.edit()
            .putFloat(Keys.CORNER_RADIUS, cornerRadius)
            .putFloat(Keys.REFRACTION_HEIGHT, refractionHeight)
            .putFloat(Keys.REFRACTION_OFFSET, refractionOffset)
            .putFloat(Keys.BLUR_RADIUS, blurRadius)
            .putFloat(Keys.DISPERSION, dispersion)
            .putFloat(Keys.TINT_ALPHA, tintAlpha)
            .putInt(Keys.GLASS_COLOR, glassColorArgb)
            .putFloat(Keys.GLASS_CONTRAST, glassContrast)
            .putFloat(Keys.GLASS_WHITE_POINT, glassWhitePoint)
            .putFloat(Keys.GLASS_CHROMA_MULTIPLIER, glassChromaMultiplier)
            .putInt(Keys.BACKGROUND_PALETTE_INDEX, backgroundPaletteIndex)
            .putFloat(Keys.BACKGROUND_ANIMATION_SPEED, backgroundAnimationSpeed)
            .putString(Keys.STATIC_BACKGROUND_URI, staticBackgroundUri)
            .putInt(Keys.STATIC_BACKGROUND_ACCENT_ARGB, staticBackgroundAccentArgb)
            .putFloat(Keys.LAST_OBSERVED_CUMULATIVE_DOSE, lastObservedCumulativeDose)
            .putString(Keys.TABLET_STOCK_SYNC_AT, tabletStockSyncAt)
            .putFloat(Keys.WEIGHT, weight)
            .putString(Keys.START_DATE, startDate)
            .putString(Keys.START_SHIFT, startShift)
            .putFloat(Keys.TARGET_TOTAL_DOSE, targetTotalDose)
            .putInt(Keys.TABLET_COUNT_20, tabletCount20)
            .putInt(Keys.TABLET_COUNT_10, tabletCount10)
            .putString(Keys.MORNING_TIME, morningTime)
            .putFloat(Keys.MORNING_DOSE, morningDose)
            .putString(Keys.EVENING_TIME, eveningTime)
            .putFloat(Keys.EVENING_DOSE, eveningDose)
            .putString(Keys.MANUAL_DOSES, manualDosesJson)
            .putString(Keys.DOSE_PERIODS, dosePeriodsJson)
            .putBoolean(Keys.COURSE_SETUP_COMPLETED, courseSetupCompleted)
            .putInt(Keys.LOST_COUNT_20, lostCount20)
            .putInt(Keys.LOST_COUNT_10, lostCount10)
            .apply()
    }
}

private object Keys {
    const val PREFS_NAME = "sortret_tracker_settings"
    const val CORNER_RADIUS = "corner_radius"
    const val REFRACTION_HEIGHT = "refraction_height"
    const val REFRACTION_OFFSET = "refraction_offset"
    const val BLUR_RADIUS = "blur_radius"
    const val DISPERSION = "dispersion"
    const val TINT_ALPHA = "tint_alpha"
    const val GLASS_COLOR = "glass_color"
    const val GLASS_CONTRAST = "glass_contrast"
    const val GLASS_WHITE_POINT = "glass_white_point"
    const val GLASS_CHROMA_MULTIPLIER = "glass_chroma_multiplier"
    const val BACKGROUND_PALETTE_INDEX = "background_palette_index"
    const val BACKGROUND_ANIMATION_SPEED = "background_animation_speed"
    const val STATIC_BACKGROUND_URI = "static_background_uri"
    const val STATIC_BACKGROUND_ACCENT_ARGB = "static_background_accent_argb"
    const val LAST_OBSERVED_CUMULATIVE_DOSE = "last_observed_cumulative_dose"
    const val TABLET_STOCK_SYNC_AT = "tablet_stock_sync_at"
    const val WEIGHT = "weight"
    const val START_DATE = "start_date"
    const val START_SHIFT = "start_shift"
    const val TARGET_TOTAL_DOSE = "target_total_dose"
    const val TABLET_COUNT_20 = "tablet_count_20"
    const val TABLET_COUNT_10 = "tablet_count_10"
    const val MORNING_TIME = "morning_time"
    const val MORNING_DOSE = "morning_dose"
    const val EVENING_TIME = "evening_time"
    const val EVENING_DOSE = "evening_dose"
    const val MANUAL_DOSES = "manual_doses_json"
    const val DOSE_PERIODS = "dose_periods_json"
    const val COURSE_SETUP_COMPLETED = "course_setup_completed"
    const val LOST_COUNT_20 = "lost_count_20"
    const val LOST_COUNT_10 = "lost_count_10"
}

class TrackerState(preferences: SharedPreferences? = null) {
    companion object {
        val MANUAL_TRACKING_START_DATE: LocalDate = LocalDate.of(2026, 5, 22)
    }

    // Liquid glass defaults
    var cornerRadius by mutableFloatStateOf(TrackerDefaults.CORNER_RADIUS)
    var refractionHeight by mutableFloatStateOf(TrackerDefaults.REFRACTION_HEIGHT)
    var refractionOffset by mutableFloatStateOf(TrackerDefaults.REFRACTION_OFFSET)
    var blurRadius by mutableFloatStateOf(TrackerDefaults.BLUR_RADIUS)
    var dispersion by mutableFloatStateOf(TrackerDefaults.DISPERSION)
    var tintAlpha by mutableFloatStateOf(TrackerDefaults.TINT_ALPHA)
    var glassColor by mutableStateOf(TrackerDefaults.GLASS_COLOR)
    var glassContrast by mutableFloatStateOf(TrackerDefaults.GLASS_CONTRAST)
    var glassWhitePoint by mutableFloatStateOf(TrackerDefaults.GLASS_WHITE_POINT)
    var glassChromaMultiplier by mutableFloatStateOf(TrackerDefaults.GLASS_CHROMA_MULTIPLIER)
    var backgroundPaletteIndex by mutableIntStateOf(TrackerDefaults.BACKGROUND_PALETTE_INDEX)
    var backgroundAnimationSpeed by mutableFloatStateOf(TrackerDefaults.BACKGROUND_ANIMATION_SPEED)
    var staticBackgroundUri by mutableStateOf(TrackerDefaults.STATIC_BACKGROUND_URI)
    var staticBackgroundAccentArgb by mutableIntStateOf(TrackerDefaults.STATIC_BACKGROUND_ACCENT_ARGB)
    var lastObservedCumulativeDose by mutableFloatStateOf(TrackerDefaults.LAST_OBSERVED_CUMULATIVE_DOSE)
    private var tabletStockSyncAt by mutableStateOf<LocalDateTime?>(null)
    var courseSetupCompleted by mutableStateOf(false)
    var lostCount20 by mutableIntStateOf(0)
    var lostCount10 by mutableIntStateOf(0)

    // User Profile
    var weight by mutableFloatStateOf(TrackerDefaults.WEIGHT)

    // Course Settings
    private val _startDate = mutableStateOf(TrackerDefaults.START_DATE)
    var startDate: LocalDate
        get() = _startDate.value
        set(value) {
            _startDate.value = value
            val first = dosePeriods.firstOrNull()
            if (first != null) {
                dosePeriods[0] = first.copy(startDate = value)
            }
        }
    var startShift by mutableStateOf(TrackerDefaults.START_SHIFT)
    var targetTotalDose by mutableFloatStateOf(TrackerDefaults.TARGET_TOTAL_DOSE)
    var tabletCount20 by mutableIntStateOf(TrackerDefaults.TABLET_COUNT_20)
    var tabletCount10 by mutableIntStateOf(TrackerDefaults.TABLET_COUNT_10)
    var morningTime by mutableStateOf(TrackerDefaults.MORNING_TIME)
    var eveningTime by mutableStateOf(TrackerDefaults.EVENING_TIME)

    private var morningDoseFallback by mutableFloatStateOf(TrackerDefaults.MORNING_DOSE)
    private var eveningDoseFallback by mutableFloatStateOf(TrackerDefaults.EVENING_DOSE)

    var morningDose: Float
        get() = dosePeriods.lastOrNull()?.morningDose ?: morningDoseFallback
        set(value) {
            val last = dosePeriods.lastOrNull()
            if (last != null) {
                val idx = dosePeriods.indexOf(last)
                dosePeriods[idx] = last.copy(morningDose = value)
            }
            morningDoseFallback = value
        }

    var eveningDose: Float
        get() = dosePeriods.lastOrNull()?.eveningDose ?: eveningDoseFallback
        set(value) {
            val last = dosePeriods.lastOrNull()
            if (last != null) {
                val idx = dosePeriods.indexOf(last)
                dosePeriods[idx] = last.copy(eveningDose = value)
            }
            eveningDoseFallback = value
        }

    // Manual Tracking & Dose Periods
    val manualDoses = mutableStateMapOf<LocalDate, DayDoseEntry>()
    val dosePeriods = mutableStateListOf<DosePeriod>()

    init {
        preferences?.let(::restoreFromPreferences)
    }

    val dailyDose: Float
        get() = morningDose + eveningDose

    // Remaining Dynamic Tablets
    val remainingTablets20: Int
        get() = getRemainingTablets20(LocalDateTime.now())

    val remainingTablets10: Int
        get() = getRemainingTablets10(LocalDateTime.now())

    // Derived Calculations
    val halfLifeHours = 20.0
    val decayConstant = ln(2.0) / halfLifeHours

    fun getScheduledNorm(date: LocalDate): Pair<Float, Float> {
        val period = dosePeriods.sortedBy { it.startDate }
            .lastOrNull { !date.isBefore(it.startDate) }
            ?: dosePeriods.firstOrNull()
        return if (period != null) {
            Pair(period.morningDose, period.eveningDose)
        } else {
            Pair(morningDoseFallback, eveningDoseFallback)
        }
    }

    fun isDoseTaken(date: LocalDate, isMorning: Boolean, now: LocalDateTime = LocalDateTime.now()): Boolean {
        if (date.isBefore(MANUAL_TRACKING_START_DATE)) {
            return isDoseTimePassed(date, isMorning, now)
        }
        val entry = manualDoses[date]
        return if (entry != null) {
            val manual = if (isMorning) entry.morningManual else entry.eveningManual
            if (manual) {
                if (isMorning) entry.morningTaken else entry.eveningTaken
            } else {
                isDoseTimePassed(date, isMorning, now)
            }
        } else {
            isDoseTimePassed(date, isMorning, now)
        }
    }

    fun getDoseAmount(date: LocalDate, isMorning: Boolean): Float {
        if (date.isBefore(MANUAL_TRACKING_START_DATE)) {
            val norm = getScheduledNorm(date)
            return if (isMorning) norm.first else norm.second
        }
        val entry = manualDoses[date]
        if (entry != null) {
            val manual = if (isMorning) entry.morningManual else entry.eveningManual
            if (manual) {
                return if (isMorning) entry.morningDose else entry.eveningDose
            }
        }
        val norm = getScheduledNorm(date)
        return if (isMorning) norm.first else norm.second
    }

    private fun isDoseTimePassed(date: LocalDate, isMorning: Boolean, now: LocalDateTime): Boolean {
        val doseTime = if (isMorning) morningTime else eveningTime
        val doseDateTime = LocalDateTime.of(date, doseTime)
        return !now.isBefore(doseDateTime)
    }

    fun setManualDose(
        date: LocalDate,
        isMorning: Boolean,
        taken: Boolean,
        dose: Float,
        count20: Int? = null,
        count10: Int? = null
    ) {
        val norm = getScheduledNorm(date)
        val defaultMorningTaken = isDoseTimePassed(date, isMorning = true, LocalDateTime.now())
        val defaultEveningTaken = isDoseTimePassed(date, isMorning = false, LocalDateTime.now())

        val currentEntry = manualDoses[date] ?: DayDoseEntry(
            morningDose = norm.first,
            morningTaken = defaultMorningTaken,
            morningManual = false,
            eveningDose = norm.second,
            eveningTaken = defaultEveningTaken,
            eveningManual = false
        )

        val updatedEntry = if (isMorning) {
            currentEntry.copy(
                morningDose = dose,
                morningTaken = taken,
                morningManual = true,
                morningCount20 = count20,
                morningCount10 = count10
            )
        } else {
            currentEntry.copy(
                eveningDose = dose,
                eveningTaken = taken,
                eveningManual = true,
                eveningCount20 = count20,
                eveningCount10 = count10
            )
        }
        manualDoses[date] = updatedEntry
    }

    fun resetManualDose(date: LocalDate, isMorning: Boolean) {
        val currentEntry = manualDoses[date] ?: return
        val updatedEntry = if (isMorning) {
            currentEntry.copy(morningManual = false)
        } else {
            currentEntry.copy(eveningManual = false)
        }

        if (!updatedEntry.morningManual && !updatedEntry.eveningManual) {
            manualDoses.remove(date)
        } else {
            manualDoses[date] = updatedEntry
        }
    }

    fun resetManualDay(date: LocalDate) {
        manualDoses.remove(date)
    }

    fun addDosePeriod(date: LocalDate, morning: Float, evening: Float) {
        dosePeriods.removeAll { it.startDate == date }
        dosePeriods.add(DosePeriod(date, morning, evening))
        dosePeriods.sortBy { it.startDate }
    }

    fun removeDosePeriod(date: LocalDate) {
        val first = dosePeriods.firstOrNull()
        if (first != null && first.startDate == date) return
        dosePeriods.removeAll { it.startDate == date }
        dosePeriods.sortBy { it.startDate }
    }

    fun calculateStats(now: LocalDateTime = LocalDateTime.now()): Stats {
        var totalDose = 0f
        var currentActiveAmount = 0.0
        val daysSinceStart = daysSinceStart(now)

        if (now.toLocalDate().isBefore(startDate)) {
            return Stats(0f, 0f, 0f, 0f, 0)
        }

        forEachTakenDose(now) { dose, doseTime ->
            totalDose += dose
            val hoursPassed = ChronoUnit.MINUTES.between(doseTime, now) / 60.0
            currentActiveAmount += dose.toDouble() * exp(-decayConstant * hoursPassed)
        }

        return Stats(
            cumulativeDose = totalDose,
            cumulativePerKg = totalDose / weight,
            activeDose = currentActiveAmount.toFloat(),
            activePerKg = currentActiveAmount.toFloat() / weight,
            daysPassed = daysSinceStart + 1
        )
    }

    fun updateTabletCount20(value: Int, now: LocalDateTime = LocalDateTime.now()) {
        tabletCount20 = value.coerceAtLeast(0)
        tabletStockSyncAt = now
    }

    fun updateTabletCount10(value: Int, now: LocalDateTime = LocalDateTime.now()) {
        tabletCount10 = value.coerceAtLeast(0)
        tabletStockSyncAt = now
    }

    fun syncTabletInventory(now: LocalDateTime = LocalDateTime.now()) {}

    fun getConsumedTablets(endDate: LocalDate, is20mg: Boolean, now: LocalDateTime = LocalDateTime.now()): Int {
        var total = 0
        if (endDate.isBefore(startDate)) return 0
        
        val totalDays = ChronoUnit.DAYS.between(startDate, endDate).toInt()
        for (dayOffset in 0..totalDays) {
            val date = startDate.plusDays(dayOffset.toLong())
            
            // Morning dose
            if (!(date == startDate && startShift == StartShift.EVENING)) {
                if (isDoseTaken(date, isMorning = true, now = now)) {
                    val entry = manualDoses[date]
                    val count = if (is20mg) {
                        entry?.morningCount20 ?: tabletUsageForDose(getDoseAmount(date, isMorning = true)).count20
                    } else {
                        entry?.morningCount10 ?: tabletUsageForDose(getDoseAmount(date, isMorning = true)).count10
                    }
                    total += count
                }
            }
            
            // Evening dose
            if (isDoseTaken(date, isMorning = false, now = now)) {
                val entry = manualDoses[date]
                val count = if (is20mg) {
                    entry?.eveningCount20 ?: tabletUsageForDose(getDoseAmount(date, isMorning = false)).count20
                } else {
                    entry?.eveningCount10 ?: tabletUsageForDose(getDoseAmount(date, isMorning = false)).count10
                }
                total += count
            }
        }
        return total
    }

    fun getRemainingTablets20(now: LocalDateTime = LocalDateTime.now()): Int {
        val consumed = getConsumedTablets(now.toLocalDate(), is20mg = true, now = now)
        return (tabletCount20 - consumed - lostCount20).coerceAtLeast(0)
    }

    fun getRemainingTablets10(now: LocalDateTime = LocalDateTime.now()): Int {
        val consumed = getConsumedTablets(now.toLocalDate(), is20mg = false, now = now)
        return (tabletCount10 - consumed - lostCount10).coerceAtLeast(0)
    }

    fun calculateTabletCoverageDays(now: LocalDateTime = LocalDateTime.now()): Int {
        if (dailyDose <= 0f) return 0

        var remaining20 = getRemainingTablets20(now).coerceAtLeast(0)
        var remaining10 = getRemainingTablets10(now).coerceAtLeast(0)
        val coverageStartDate = maxOf(now.toLocalDate(), startDate)
        var coveredDaysCount = 0

        for (dayOffset in 0..3650) {
            val date = coverageStartDate.plusDays(dayOffset.toLong())
            var dayFullyCovered = true
            var hasFutureDose = false

            var tempRemaining20 = remaining20
            var tempRemaining10 = remaining10

            // Check morning dose
            if (!(date == startDate && startShift == StartShift.EVENING)) {
                val morningDoseTime = LocalDateTime.of(date, morningTime)
                if (morningDoseTime.isAfter(now)) {
                    hasFutureDose = true
                    val actualDose = getDoseAmount(date, isMorning = true)
                    if (actualDose > 0f) {
                        val usage = tabletUsageForDose(actualDose)
                        if (tempRemaining20 < usage.count20 || tempRemaining10 < usage.count10) {
                            dayFullyCovered = false
                        } else {
                            tempRemaining20 -= usage.count20
                            tempRemaining10 -= usage.count10
                        }
                    }
                }
            }

            // Check evening dose
            if (dayFullyCovered) {
                val eveningDoseTime = LocalDateTime.of(date, eveningTime)
                if (eveningDoseTime.isAfter(now)) {
                    hasFutureDose = true
                    val actualDose = getDoseAmount(date, isMorning = false)
                    if (actualDose > 0f) {
                        val usage = tabletUsageForDose(actualDose)
                        if (tempRemaining20 < usage.count20 || tempRemaining10 < usage.count10) {
                            dayFullyCovered = false
                        } else {
                            tempRemaining20 -= usage.count20
                            tempRemaining10 -= usage.count10
                        }
                    }
                }
            }

            if (dayFullyCovered) {
                remaining20 = tempRemaining20
                remaining10 = tempRemaining10
                if (hasFutureDose) {
                    coveredDaysCount++
                }
            } else {
                break
            }
        }

        return coveredDaysCount
    }

    fun resetGlassDefaults() {
        cornerRadius = TrackerDefaults.CORNER_RADIUS
        refractionHeight = TrackerDefaults.REFRACTION_HEIGHT
        refractionOffset = TrackerDefaults.REFRACTION_OFFSET
        blurRadius = TrackerDefaults.BLUR_RADIUS
        dispersion = TrackerDefaults.DISPERSION
        tintAlpha = TrackerDefaults.TINT_ALPHA
        glassColor = TrackerDefaults.GLASS_COLOR
        glassContrast = TrackerDefaults.GLASS_CONTRAST
        glassWhitePoint = TrackerDefaults.GLASS_WHITE_POINT
        glassChromaMultiplier = TrackerDefaults.GLASS_CHROMA_MULTIPLIER
    }

    internal fun toPersistedState(): PersistedTrackerState {
        return PersistedTrackerState(
            cornerRadius = cornerRadius,
            refractionHeight = refractionHeight,
            refractionOffset = refractionOffset,
            blurRadius = blurRadius,
            dispersion = dispersion,
            tintAlpha = tintAlpha,
            glassColorArgb = glassColor.toArgb(),
            glassContrast = glassContrast,
            glassWhitePoint = glassWhitePoint,
            glassChromaMultiplier = glassChromaMultiplier,
            backgroundPaletteIndex = backgroundPaletteIndex,
            backgroundAnimationSpeed = backgroundAnimationSpeed,
            staticBackgroundUri = staticBackgroundUri,
            staticBackgroundAccentArgb = staticBackgroundAccentArgb,
            lastObservedCumulativeDose = lastObservedCumulativeDose,
            tabletStockSyncAt = tabletStockSyncAt?.toString().orEmpty(),
            weight = weight,
            startDate = startDate.toString(),
            startShift = startShift.name,
            targetTotalDose = targetTotalDose,
            tabletCount20 = tabletCount20,
            tabletCount10 = tabletCount10,
            morningTime = morningTime.toString(),
            morningDose = morningDose,
            eveningTime = eveningTime.toString(),
            eveningDose = eveningDose,
            manualDosesJson = serializeManualDoses(manualDoses.toMap()),
            dosePeriodsJson = serializeDosePeriods(dosePeriods.toList()),
            courseSetupCompleted = courseSetupCompleted,
            lostCount20 = lostCount20,
            lostCount10 = lostCount10
        )
    }

    private fun restoreFromPreferences(preferences: SharedPreferences) {
        cornerRadius = preferences.getFloat(Keys.CORNER_RADIUS, TrackerDefaults.CORNER_RADIUS)
        refractionHeight = preferences.getFloat(Keys.REFRACTION_HEIGHT, TrackerDefaults.REFRACTION_HEIGHT)
        refractionOffset = preferences.getFloat(Keys.REFRACTION_OFFSET, TrackerDefaults.REFRACTION_OFFSET)
        blurRadius = preferences.getFloat(Keys.BLUR_RADIUS, TrackerDefaults.BLUR_RADIUS)
        dispersion = preferences.getFloat(Keys.DISPERSION, TrackerDefaults.DISPERSION)
        tintAlpha = preferences.getFloat(Keys.TINT_ALPHA, TrackerDefaults.TINT_ALPHA)
        glassColor = Color(preferences.getInt(Keys.GLASS_COLOR, TrackerDefaults.GLASS_COLOR.toArgb()))
        glassContrast = preferences.getFloat(Keys.GLASS_CONTRAST, TrackerDefaults.GLASS_CONTRAST)
        glassWhitePoint = preferences.getFloat(Keys.GLASS_WHITE_POINT, TrackerDefaults.GLASS_WHITE_POINT)
        glassChromaMultiplier = preferences.getFloat(
            Keys.GLASS_CHROMA_MULTIPLIER,
            TrackerDefaults.GLASS_CHROMA_MULTIPLIER
        )
        backgroundPaletteIndex = preferences.getInt(
            Keys.BACKGROUND_PALETTE_INDEX,
            TrackerDefaults.BACKGROUND_PALETTE_INDEX
        )
        backgroundAnimationSpeed = preferences.getFloat(
            Keys.BACKGROUND_ANIMATION_SPEED,
            TrackerDefaults.BACKGROUND_ANIMATION_SPEED
        )
        staticBackgroundUri = preferences.getString(
            Keys.STATIC_BACKGROUND_URI,
            TrackerDefaults.STATIC_BACKGROUND_URI
        ).orEmpty()
        staticBackgroundAccentArgb = preferences.getInt(
            Keys.STATIC_BACKGROUND_ACCENT_ARGB,
            TrackerDefaults.STATIC_BACKGROUND_ACCENT_ARGB
        )
        lastObservedCumulativeDose = preferences.getFloat(
            Keys.LAST_OBSERVED_CUMULATIVE_DOSE,
            TrackerDefaults.LAST_OBSERVED_CUMULATIVE_DOSE
        )
        tabletStockSyncAt = preferences.getString(Keys.TABLET_STOCK_SYNC_AT, TrackerDefaults.TABLET_STOCK_SYNC_AT)
            ?.takeIf { it.isNotBlank() }
            ?.let { runCatching { LocalDateTime.parse(it) }.getOrNull() }
        weight = preferences.getFloat(Keys.WEIGHT, TrackerDefaults.WEIGHT)
        startDate = preferences.getString(Keys.START_DATE, null)
            ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
            ?: TrackerDefaults.START_DATE
        startShift = preferences.getString(Keys.START_SHIFT, TrackerDefaults.START_SHIFT.name)
            ?.let { runCatching { StartShift.valueOf(it) }.getOrNull() }
            ?: TrackerDefaults.START_SHIFT
        targetTotalDose = preferences.getFloat(Keys.TARGET_TOTAL_DOSE, TrackerDefaults.TARGET_TOTAL_DOSE)
        tabletCount20 = preferences.getInt(Keys.TABLET_COUNT_20, TrackerDefaults.TABLET_COUNT_20)
        tabletCount10 = preferences.getInt(Keys.TABLET_COUNT_10, TrackerDefaults.TABLET_COUNT_10)
        morningTime = preferences.getString(Keys.MORNING_TIME, null)
            ?.let { runCatching { LocalTime.parse(it) }.getOrNull() }
            ?: TrackerDefaults.MORNING_TIME
        morningDoseFallback = preferences.getFloat(Keys.MORNING_DOSE, TrackerDefaults.MORNING_DOSE)
        eveningTime = preferences.getString(Keys.EVENING_TIME, null)
            ?.let { runCatching { LocalTime.parse(it) }.getOrNull() }
            ?: TrackerDefaults.EVENING_TIME
        eveningDoseFallback = preferences.getFloat(Keys.EVENING_DOSE, TrackerDefaults.EVENING_DOSE)

        val manualDosesStr = preferences.getString(Keys.MANUAL_DOSES, "").orEmpty()
        manualDoses.clear()
        manualDoses.putAll(deserializeManualDoses(manualDosesStr))

        val dosePeriodsStr = preferences.getString(Keys.DOSE_PERIODS, "").orEmpty()
        dosePeriods.clear()
        val parsedPeriods = deserializeDosePeriods(dosePeriodsStr)
        if (parsedPeriods.isEmpty()) {
            dosePeriods.add(DosePeriod(startDate, morningDoseFallback, eveningDoseFallback))
        } else {
            dosePeriods.addAll(parsedPeriods)
        }

        val hasStartDate = preferences.contains(Keys.START_DATE)
        courseSetupCompleted = preferences.getBoolean(Keys.COURSE_SETUP_COMPLETED, hasStartDate)
        lostCount20 = preferences.getInt(Keys.LOST_COUNT_20, 0)
        lostCount10 = preferences.getInt(Keys.LOST_COUNT_10, 0)
    }

    private fun daysSinceStart(now: LocalDateTime): Int {
        return ChronoUnit.DAYS.between(startDate, now.toLocalDate()).toInt()
    }

    private inline fun forEachTakenDose(
        now: LocalDateTime,
        onTakenDose: (dose: Float, doseTime: LocalDateTime) -> Unit
    ) {
        if (now.toLocalDate().isBefore(startDate)) return

        for (dayOffset in 0..daysSinceStart(now)) {
            val date = startDate.plusDays(dayOffset.toLong())

            if (dayOffset > 0 || startShift == StartShift.MORNING) {
                val morningDoseTime = LocalDateTime.of(date, morningTime)
                if (isDoseTaken(date, isMorning = true, now = now)) {
                    val actualDose = getDoseAmount(date, isMorning = true)
                    if (actualDose > 0f) {
                        onTakenDose(actualDose, morningDoseTime)
                    }
                }
            }

            val eveningDoseTime = LocalDateTime.of(date, eveningTime)
            if (isDoseTaken(date, isMorning = false, now = now)) {
                val actualDose = getDoseAmount(date, isMorning = false)
                if (actualDose > 0f) {
                    onTakenDose(actualDose, eveningDoseTime)
                }
            }
        }
    }

    private inline fun forEachDoseBetween(
        startExclusive: LocalDateTime,
        endInclusive: LocalDateTime,
        onTakenDose: (dose: Float, doseTime: LocalDateTime) -> Unit
    ) {
        if (!endInclusive.isAfter(startExclusive) || endInclusive.toLocalDate().isBefore(startDate)) return

        val firstDate = maxOf(startDate, startExclusive.toLocalDate())
        val lastDate = endInclusive.toLocalDate()
        val totalDays = ChronoUnit.DAYS.between(firstDate, lastDate).toInt()

        for (dayOffset in 0..totalDays) {
            val date = firstDate.plusDays(dayOffset.toLong())

            if (!(date == startDate && startShift == StartShift.EVENING)) {
                val morningDoseTime = LocalDateTime.of(date, morningTime)
                if (morningDoseTime.isAfter(startExclusive) && !morningDoseTime.isAfter(endInclusive)) {
                    if (isDoseTaken(date, isMorning = true, now = endInclusive)) {
                        val actualDose = getDoseAmount(date, isMorning = true)
                        if (actualDose > 0f) {
                            onTakenDose(actualDose, morningDoseTime)
                        }
                    }
                }
            }

            val eveningDoseTime = LocalDateTime.of(date, eveningTime)
            if (eveningDoseTime.isAfter(startExclusive) && !eveningDoseTime.isAfter(endInclusive)) {
                if (isDoseTaken(date, isMorning = false, now = endInclusive)) {
                    val actualDose = getDoseAmount(date, isMorning = false)
                    if (actualDose > 0f) {
                        onTakenDose(actualDose, eveningDoseTime)
                    }
                }
            }
        }
    }

    private fun serializeManualDoses(map: Map<LocalDate, DayDoseEntry>): String {
        return map.entries.joinToString(";") { "${it.key}:${it.value.serialize()}" }
    }

    private fun deserializeManualDoses(s: String): Map<LocalDate, DayDoseEntry> {
        if (s.isBlank()) return emptyMap()
        val map = mutableMapOf<LocalDate, DayDoseEntry>()
        s.split(";").forEach { entryStr ->
            val parts = entryStr.split(":", limit = 2)
            if (parts.size == 2) {
                val date = runCatching { LocalDate.parse(parts[0]) }.getOrNull()
                val entry = DayDoseEntry.deserialize(parts[1])
                if (date != null && entry != null) {
                    map[date] = entry
                }
            }
        }
        return map
    }

    private fun serializeDosePeriods(list: List<DosePeriod>): String {
        return list.joinToString(";") { it.serialize() }
    }

    private fun deserializeDosePeriods(s: String): List<DosePeriod> {
        if (s.isBlank()) return emptyList()
        return s.split(";").mapNotNull { DosePeriod.deserialize(it) }.sortedBy { it.startDate }
    }

    private fun tabletUsageForDose(dose: Float): TabletDoseUsage {
        val normalizedDose = ((dose.coerceAtLeast(0f) / 10f).roundToInt()).coerceAtLeast(0) * 10
        return TabletDoseUsage(
            count20 = normalizedDose / 20,
            count10 = (normalizedDose % 20) / 10
        )
    }
}

data class Stats(
    val cumulativeDose: Float,
    val cumulativePerKg: Float,
    val activeDose: Float,
    val activePerKg: Float,
    val daysPassed: Int
)

private data class TabletDoseUsage(
    val count20: Int,
    val count10: Int
)

@Composable
fun rememberTrackerState(): TrackerState {
    val context = LocalContext.current.applicationContext
    val preferences = remember(context) {
        context.getSharedPreferences(Keys.PREFS_NAME, Context.MODE_PRIVATE)
    }
    val state = remember(preferences) { TrackerState(preferences) }

    LaunchedEffect(state, preferences) {
        snapshotFlow { state.toPersistedState() }
            .distinctUntilChanged()
            .collect { persistedState ->
                persistedState.saveTo(preferences)
            }
    }

    return state
}
