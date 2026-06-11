package com.example.sortret.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.sortret.ui.components.AnimatedBackground
import com.example.sortret.ui.components.CardInfoSheet
import com.example.sortret.ui.components.CardInfoType
import com.example.sortret.ui.components.CourseSettingsSheet
import com.example.sortret.ui.components.GlassSettingsSheet
import com.example.sortret.ui.components.ScheduleSettingsSheet
import com.example.sortret.ui.components.SortretNoOverscroll
import com.example.sortret.ui.components.StaticImageBackground
import com.example.sortret.ui.components.activeBackgroundPalette
import com.example.sortret.ui.components.currentCourseStage
import com.example.sortret.ui.components.extractDominantBackgroundAccentArgb
import com.example.sortret.ui.components.visualAccents
import com.example.sortret.logic.Stats
import com.example.sortret.logic.rememberTrackerState
import com.example.sortret.logic.loc
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import kotlinx.coroutines.delay
import java.time.LocalDate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import com.example.sortret.ui.components.BackdropDialogSurface
import com.example.sortret.ui.components.DosageSelector
import com.example.sortret.ui.components.softPressClick
import com.example.sortret.ui.components.formatDose
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.ceil

@Composable
fun TrackerScreen() {
    val state = rememberTrackerState()
    val context = LocalContext.current.applicationContext
    val currentDateTime = rememberTrackerNow()
    val calculatedStats = state.calculateStats(now = currentDateTime)
    val tabletCoverageDays = state.calculateTabletCoverageDays(now = currentDateTime)
    val backdrop = rememberLayerBackdrop()
    val backgroundPalette = state.activeBackgroundPalette()
    val visualAccents = state.visualAccents(backgroundPalette)
    var showGlassSettings by remember { mutableStateOf(false) }
    var showCourseSettings by remember { mutableStateOf(false) }
    var showScheduleSettings by remember { mutableStateOf(false) }
    var activeInfoSheet by remember { mutableStateOf<CardInfoType?>(null) }
    var dateToEdit by remember { mutableStateOf<LocalDate?>(null) }

    val launchObservedCumulativeDose = remember(state) { state.lastObservedCumulativeDose }
    var holdLaunchDoseAnimation by remember(state) {
        mutableStateOf(
            launchObservedCumulativeDose >= 0f &&
                launchObservedCumulativeDose < calculatedStats.cumulativeDose - 0.01f
        )
    }
    val shouldHoldLaunchDoseAnimation = holdLaunchDoseAnimation
    val targetStats = if (shouldHoldLaunchDoseAnimation) {
        calculatedStats.copy(
            cumulativeDose = launchObservedCumulativeDose,
            cumulativePerKg = launchObservedCumulativeDose / state.weight.coerceAtLeast(1f)
        )
    } else {
        calculatedStats
    }

    LaunchedEffect(calculatedStats.cumulativeDose, shouldHoldLaunchDoseAnimation) {
        if (shouldHoldLaunchDoseAnimation) {
            delay(1300)
            holdLaunchDoseAnimation = false
        }
    }

    LaunchedEffect(calculatedStats.cumulativeDose, shouldHoldLaunchDoseAnimation) {
        if (!shouldHoldLaunchDoseAnimation &&
            kotlin.math.abs(state.lastObservedCumulativeDose - calculatedStats.cumulativeDose) > 0.001f
        ) {
            state.lastObservedCumulativeDose = calculatedStats.cumulativeDose
        }
    }

    LaunchedEffect(
        currentDateTime,
        state.startDate,
        state.startShift,
        state.morningTime,
        state.morningDose,
        state.eveningTime,
        state.eveningDose
    ) {
        state.syncTabletInventory(now = currentDateTime)
    }

    val progressPercent = (targetStats.cumulativeDose / state.targetTotalDose).coerceIn(0f, 1f)
    val animatedProgressPercent by animateFloatAsState(
        targetValue = progressPercent,
        animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
        label = "courseProgressPercent"
    )
    val animatedCumulativeDose by animateFloatAsState(
        targetValue = targetStats.cumulativeDose,
        animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
        label = "cumulativeDose"
    )
    val animatedCumulativePerKg by animateFloatAsState(
        targetValue = targetStats.cumulativePerKg,
        animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
        label = "cumulativePerKg"
    )
    val animatedActiveDose by animateFloatAsState(
        targetValue = targetStats.activeDose,
        animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
        label = "activeDose"
    )
    val animatedActivePerKg by animateFloatAsState(
        targetValue = targetStats.activePerKg,
        animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
        label = "activePerKg"
    )
    val displayedStats = Stats(
        cumulativeDose = animatedCumulativeDose,
        cumulativePerKg = animatedCumulativePerKg,
        activeDose = animatedActiveDose,
        activePerKg = animatedActivePerKg,
        daysPassed = targetStats.daysPassed
    )
    val currentStage = currentCourseStage(animatedProgressPercent)
    val courseTotalDays = ceil(state.targetTotalDose / state.dailyDose.coerceAtLeast(1f)).toInt().coerceAtLeast(1)
    val displayedDate = currentDateTime.toLocalDate()
        .format(DateTimeFormatter.ofPattern(loc("d MMMM yyyy 'г.'", "d MMMM yyyy"), Locale.forLanguageTag(loc("ru", "en"))))
    val currentTimeText = currentDateTime.formatTrackerClock()
    val isAnySheetOpen = showGlassSettings || showCourseSettings || showScheduleSettings || activeInfoSheet != null || dateToEdit != null
    val backgroundScrimAlpha by animateFloatAsState(
        targetValue = if (isAnySheetOpen) 0.08f else 0f,
        animationSpec = tween(
            durationMillis = if (isAnySheetOpen) 70 else 0,
            easing = FastOutSlowInEasing
        ),
        label = "sheetBackgroundScrim"
    )
    val backgroundBlurRadius = if (isAnySheetOpen) 6.dp else 0.dp

    BackHandler(enabled = isAnySheetOpen) {
        when {
            dateToEdit != null -> dateToEdit = null
            activeInfoSheet != null -> activeInfoSheet = null
            showScheduleSettings -> showScheduleSettings = false
            showCourseSettings -> showCourseSettings = false
            showGlassSettings -> showGlassSettings = false
        }
    }

    LaunchedEffect(state.staticBackgroundUri) {
        if (state.staticBackgroundUri.isNotBlank() && state.staticBackgroundAccentArgb == 0) {
            extractDominantBackgroundAccentArgb(context, state.staticBackgroundUri)?.let { accentArgb ->
                state.staticBackgroundAccentArgb = accentArgb
            }
        }
    }

    SortretNoOverscroll {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundPalette.gradientA.first())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(backgroundBlurRadius)
            ) {
                if (state.staticBackgroundUri.isBlank()) {
                    AnimatedBackground(
                        modifier = Modifier.layerBackdrop(backdrop),
                        palette = backgroundPalette,
                        speed = state.backgroundAnimationSpeed
                    )
                } else {
                    StaticImageBackground(
                        uriString = state.staticBackgroundUri,
                        fallbackPalette = backgroundPalette,
                        modifier = Modifier.layerBackdrop(backdrop)
                    )
                }

                if (state.courseSetupCompleted) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(32.dp))
                        TrackerHeader(
                            state = state,
                            backdrop = backdrop,
                            backgroundPalette = backgroundPalette,
                            displayedDate = displayedDate,
                            currentTimeText = currentTimeText,
                            onSettingsClick = { showGlassSettings = true }
                        )

                        Spacer(modifier = Modifier.height(40.dp))
                        ProgressCard(
                            state = state,
                            backdrop = backdrop,
                            accentColor = visualAccents.primary,
                            secondaryAccent = visualAccents.secondary,
                            stats = displayedStats,
                            currentStage = currentStage,
                            progressPercent = animatedProgressPercent,
                            onClick = { activeInfoSheet = CardInfoType.InitialAccumulation }
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        StatsCards(
                            state = state,
                            backdrop = backdrop,
                            backgroundPalette = backgroundPalette,
                            stats = displayedStats,
                            courseTotalDays = courseTotalDays,
                            onSubstanceClick = { activeInfoSheet = CardInfoType.SubstanceAccumulation },
                            onCalendarClick = { activeInfoSheet = CardInfoType.CourseCalendar }
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        ScheduleCard(
                            state = state,
                            backdrop = backdrop,
                            backgroundPalette = backgroundPalette,
                            tabletCoverageDays = tabletCoverageDays,
                            onEditDate = { dateToEdit = it },
                            onClick = { showScheduleSettings = true }
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        CourseSettingsBlock(
                            state = state,
                            backdrop = backdrop,
                            onClick = { showCourseSettings = true }
                        )

                        Spacer(modifier = Modifier.height(28.dp))
                    }
                } else {
                    CourseSetupScreen(
                        state = state,
                        backdrop = backdrop,
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp)
                    )
                }

                if (backgroundScrimAlpha > 0f) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = backgroundScrimAlpha))
                    )
                }
            }

            if (showGlassSettings) {
                GlassSettingsSheet(state = state, backdrop = backdrop, onDismiss = { showGlassSettings = false })
            }
            if (showCourseSettings) {
                CourseSettingsSheet(state = state, backdrop = backdrop, onDismiss = { showCourseSettings = false })
            }
            if (showScheduleSettings) {
                ScheduleSettingsSheet(state = state, backdrop = backdrop, onDismiss = { showScheduleSettings = false })
            }
            activeInfoSheet?.let { sheetType ->
                CardInfoSheet(
                    type = sheetType,
                    state = state,
                    stats = calculatedStats,
                    backdrop = backdrop,
                    onDismiss = { activeInfoSheet = null }
                )
            }

            dateToEdit?.let { date ->
                val norm = state.getScheduledNorm(date)
                val manualEntry = state.manualDoses[date]

                var morningTaken by remember(date) {
                    mutableStateOf(
                        if (manualEntry?.morningManual == true) manualEntry.morningTaken 
                        else state.isDoseTaken(date, isMorning = true)
                    )
                }
                var morningDose by remember(date) {
                    mutableStateOf(
                        if (manualEntry?.morningManual == true) manualEntry.morningDose 
                        else norm.first
                    )
                }
                var morningIsManual by remember(date) {
                    mutableStateOf(manualEntry?.morningManual == true)
                }
                var morningCount20 by remember(date) {
                    mutableStateOf(manualEntry?.morningCount20)
                }
                var morningCount10 by remember(date) {
                    mutableStateOf(manualEntry?.morningCount10)
                }

                var eveningTaken by remember(date) {
                    mutableStateOf(
                        if (manualEntry?.eveningManual == true) manualEntry.eveningTaken 
                        else state.isDoseTaken(date, isMorning = false)
                    )
                }
                var eveningDose by remember(date) {
                    mutableStateOf(
                        if (manualEntry?.eveningManual == true) manualEntry.eveningDose 
                        else norm.second
                    )
                }
                var eveningIsManual by remember(date) {
                    mutableStateOf(manualEntry?.eveningManual == true)
                }
                var eveningCount20 by remember(date) {
                    mutableStateOf(manualEntry?.eveningCount20)
                }
                var eveningCount10 by remember(date) {
                    mutableStateOf(manualEntry?.eveningCount10)
                }

                BackdropDialogSurface(
                    title = loc("Настройка приема", "Intake Settings"),
                    subtitle = null,
                    showCloseButton = false,
                    backdrop = backdrop,
                    accentColor = visualAccents.primary,
                    secondaryAccent = visualAccents.secondary,
                    onDismiss = { dateToEdit = null }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        val relativeDayText = when (val daysDiff = java.time.temporal.ChronoUnit.DAYS.between(date, LocalDate.now())) {
                            0L -> loc("Сегодня", "Today")
                            1L -> loc("Вчера", "Yesterday")
                            2L -> loc("Позавчера", "Day before yesterday")
                            else -> {
                                if (daysDiff > 0) loc("$daysDiff дн. назад", "$daysDiff days ago")
                                else loc("${-daysDiff} дн. вперед", "${-daysDiff} days ahead")
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(16.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(visualAccents.primary.copy(alpha = 0.15f), CircleShape)
                                    .border(1.dp, visualAccents.primary.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                  Text(
                                      text = date.dayOfMonth.toString(),
                                      color = visualAccents.primary,
                                      fontSize = 14.sp,
                                      fontWeight = FontWeight.Bold
                                  )
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = date.format(DateTimeFormatter.ofPattern(loc("d MMMM yyyy", "d MMMM yyyy"), Locale.forLanguageTag(loc("ru", "en")))),
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.forLanguageTag(loc("ru", "en"))))
                                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } + " • $relativeDayText",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Morning Dose
                        if (norm.first > 0f || morningIsManual) {
                            DosageSelector(
                                label = loc("Утренний прием", "Morning dose"),
                                scheduledDose = norm.first,
                                isManual = morningIsManual,
                                taken = morningTaken,
                                currentDose = morningDose,
                                accent = visualAccents.primary,
                                initialCount20 = morningCount20,
                                initialCount10 = morningCount10,
                                onStateChange = { newIsManual, newTaken, newDose, newC20, newC10 ->
                                    morningIsManual = newIsManual
                                    morningTaken = newTaken
                                    morningDose = newDose
                                    morningCount20 = newC20
                                    morningCount10 = newC10
                                }
                            )
                        }

                        // Evening Dose
                        if (norm.second > 0f || eveningIsManual) {
                            DosageSelector(
                                label = loc("Вечерний прием", "Evening dose"),
                                scheduledDose = norm.second,
                                isManual = eveningIsManual,
                                taken = eveningTaken,
                                currentDose = eveningDose,
                                accent = visualAccents.secondary,
                                initialCount20 = eveningCount20,
                                initialCount10 = eveningCount10,
                                onStateChange = { newIsManual, newTaken, newDose, newC20, newC10 ->
                                    eveningIsManual = newIsManual
                                    eveningTaken = newTaken
                                    eveningDose = newDose
                                    eveningCount20 = newC20
                                    eveningCount10 = newC10
                                }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                                    .softPressClick(highlightColor = visualAccents.primary) {
                                        state.resetManualDay(date)
                                        dateToEdit = null
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(loc("Отмена", "Cancel"), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(50.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(listOf(visualAccents.primary, visualAccents.secondary)),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .softPressClick(highlightColor = visualAccents.secondary) {
                                        if (morningIsManual) {
                                            state.setManualDose(
                                                date = date,
                                                isMorning = true,
                                                taken = morningTaken,
                                                dose = morningDose,
                                                count20 = morningCount20,
                                                count10 = morningCount10
                                            )
                                        } else {
                                            state.resetManualDose(date, isMorning = true)
                                        }
                                        if (eveningIsManual) {
                                            state.setManualDose(
                                                date = date,
                                                isMorning = false,
                                                taken = eveningTaken,
                                                dose = eveningDose,
                                                count20 = eveningCount20,
                                                count10 = eveningCount10
                                            )
                                        } else {
                                            state.resetManualDose(date, isMorning = false)
                                        }
                                        dateToEdit = null
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(loc("Сохранить", "Save"), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
