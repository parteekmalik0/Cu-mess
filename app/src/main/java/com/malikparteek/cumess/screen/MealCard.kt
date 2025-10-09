package com.malikparteek.cumess.screen

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import androidx.compose.ui.text.style.TextAlign

@Composable
fun MealCard(
    mealName: String,
    items: List<String>?,
    imageRes: Int,
    isLoading: Boolean = false,
    isToday: Boolean = true
) {
    val isDarkTheme = isSystemInDarkTheme()
    val cardColor = if (isDarkTheme) Color(0xFF2C2C2C) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val headingColor = if (isDarkTheme) Color.LightGray else Color.Gray

    val contentText = when {
        isLoading -> "Loading..."
        items != null && items.isNotEmpty() -> items.joinToString(", ")
        else -> "No meal found ..."
    }

    val timeRange = getMealTimeRange(mealName)
    val now = LocalTime.now()

    val progress = timeRange?.let { (start, end) ->
        when {
            now.isBefore(start) -> 0f
            now.isAfter(end) -> 1f
            else -> {
                val total = Duration.between(start, end).toMinutes().toFloat()
                val elapsed = Duration.between(start, now).toMinutes().toFloat()
                (elapsed / total).coerceIn(0f, 1f)
            }
        }
    } ?: 0f

    val statusText = timeRange?.let { (start, end) ->
        when {
            now.isBefore(start) -> "Starts at ${start.format(DateTimeFormatter.ofPattern("hh:mm a"))}"
            now.isAfter(end) -> "Closed"
            else -> {
                val duration = Duration.between(now, end)
                val hours = duration.toHours()
                val minutes = duration.minusHours(hours).toMinutes()
                "${hours} hour${if (hours != 1L) "s" else ""} and ${minutes} min${if (minutes != 1L) "s" else ""} left"
            }
        }
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 3.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = mealName,
                color = headingColor,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "$mealName Image",
                    modifier = Modifier.size(70.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = contentText,
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )
            }

            if (isToday && !isLoading && timeRange != null) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = statusText ?: "",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.End
                    )
                }

            }
        }
    }
}

private fun getMealTimeRange(mealName: String): Pair<LocalTime, LocalTime>? {
    val day = LocalDate.now().dayOfWeek

    val isWeekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY

    return when (mealName) {
        "Breakfast" -> {
            if (isWeekend) LocalTime.of(8, 0) to LocalTime.of(9, 30) // Weekend timing
            else LocalTime.of(7, 30) to LocalTime.of(9, 0)             // Weekday timing
        }
        "Lunch" -> {
            if (isWeekend) LocalTime.of(12, 30) to LocalTime.of(14, 0)
            else LocalTime.of(12, 0) to LocalTime.of(13, 45)
        }
        "Snacks (Boys)", "Snacks (Girls)" -> {
            if (isWeekend) LocalTime.of(16, 30) to LocalTime.of(17, 16)
            else LocalTime.of(16, 30) to LocalTime.of(17, 16)
        }
        "Dinner", "Dinner (South Indian)" -> {
            if (isWeekend) LocalTime.of(19, 30) to LocalTime.of(21, 0)
            else LocalTime.of(19, 30) to LocalTime.of(21, 0)
        }
        else -> null
    }
}