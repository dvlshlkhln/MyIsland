package com.myisland.dynamic.ui.overlay

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myisland.dynamic.data.ChargingState
import com.myisland.dynamic.data.MediaState
import com.myisland.dynamic.data.NotificationItem
import com.myisland.dynamic.ui.theme.VibrantGreen

@Composable
fun CompactPillContent(
    mediaState: MediaState,
    notification: NotificationItem?,
    chargingState: ChargingState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Element
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.weight(1f)
        ) {
            if (mediaState.albumArt != null) {
                Image(
                    bitmap = mediaState.albumArt.asImageBitmap(),
                    contentDescription = "Album Art",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            } else if (mediaState.isPlaying) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Playing Music",
                    tint = Color(0xFFA29BFE),
                    modifier = Modifier.size(20.dp)
                )
            } else if (notification != null) {
                if (notification.icon != null) {
                    Image(
                        bitmap = notification.icon.asImageBitmap(),
                        contentDescription = "Notification Icon",
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notification",
                        tint = Color(0xFF74B9FF),
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else if (chargingState.isCharging) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Charging",
                    tint = VibrantGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Camera Cutout Clearance Area (Motorola Edge 60 Pro Center Punch Hole)
        Spacer(modifier = Modifier.width(36.dp))

        // Right Element
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.weight(1f)
        ) {
            if (mediaState.isPlaying) {
                MusicVisualizerBars()
            } else if (notification != null) {
                Text(
                    text = notification.appName.take(8),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            } else if (chargingState.isCharging) {
                Text(
                    text = "${chargingState.batteryLevel}%",
                    color = VibrantGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MusicVisualizerBars() {
    val infiniteTransition = rememberInfiniteTransition(label = "visualizer")
    
    val height1 by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "h1"
    )
    val height2 by infiniteTransition.animateFloat(
        initialValue = 16f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "h2"
    )
    val height3 by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "h3"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(20.dp)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(height1.dp)
                .background(Color(0xFF00CEC9), CircleShape)
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(height2.dp)
                .background(Color(0xFFA29BFE), CircleShape)
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(height3.dp)
                .background(Color(0xFFFD79A8), CircleShape)
        )
    }
}
