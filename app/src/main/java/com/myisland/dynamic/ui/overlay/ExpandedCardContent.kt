package com.myisland.dynamic.ui.overlay

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myisland.dynamic.data.*
import com.myisland.dynamic.ui.theme.VibrantGreen
import com.myisland.dynamic.utils.IslandPaletteColors

@Composable
fun ExpandedCardContent(
    mediaState: MediaState,
    notification: NotificationItem?,
    chargingState: ChargingState,
    callState: CallState = CallState(),
    timerState: TimerState = TimerState(),
    bluetoothState: BluetoothDeviceState = BluetoothDeviceState(),
    paletteColors: IslandPaletteColors = IslandPaletteColors(),
    showQuickActionMenu: Boolean = false,
    onPlayPauseToggle: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onDismiss: () -> Unit,
    onEndCall: () -> Unit = {},
    onToggleMuteCall: () -> Unit = {},
    onAddTimerMinute: () -> Unit = {},
    onSendQuickReply: (String) -> Unit = {},
    onToggleTorch: () -> Unit = {},
    onToggleAudioOutput: () -> Unit = {},
    onMuteActiveApp: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (showQuickActionMenu) {
            ExpandedQuickActionPaletteView(
                onToggleTorch = onToggleTorch,
                onToggleAudioOutput = onToggleAudioOutput,
                onMuteActiveApp = onMuteActiveApp,
                onDismiss = onDismiss
            )
        } else if (callState.isRinging || callState.isActiveCall) {
            ExpandedCallView(
                callState = callState,
                onEndCall = onEndCall,
                onToggleMute = onToggleMuteCall
            )
        } else if (timerState.isRunning) {
            ExpandedTimerView(
                timerState = timerState,
                onAddMinute = onAddTimerMinute,
                onDismiss = onDismiss
            )
        } else if (bluetoothState.isConnected) {
            ExpandedBluetoothView(bluetoothState = bluetoothState)
        } else if (mediaState.isPlaying || mediaState.title.isNotBlank()) {
            ExpandedMediaView(
                mediaState = mediaState,
                paletteColors = paletteColors,
                onPlayPauseToggle = onPlayPauseToggle,
                onSkipNext = onSkipNext,
                onSkipPrevious = onSkipPrevious
            )
        } else if (notification != null) {
            ExpandedNotificationView(
                notification = notification,
                onDismiss = onDismiss,
                onSendReply = onSendQuickReply
            )
        } else if (chargingState.isCharging) {
            ExpandedChargingView(chargingState = chargingState)
        } else {
            ExpandedDefaultView()
        }
    }
}

@Composable
fun ExpandedQuickActionPaletteView(
    onToggleTorch: () -> Unit,
    onToggleAudioOutput: () -> Unit,
    onMuteActiveApp: () -> Unit,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(
                onClick = {
                    onToggleAudioOutput()
                    onDismiss()
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0984E3))
            ) {
                Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Speaker", tint = Color.White)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "Audio Out", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(
                onClick = {
                    onToggleTorch()
                    onDismiss()
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFDCB6E))
            ) {
                Icon(imageVector = Icons.Default.FlashlightOn, contentDescription = "Flashlight", tint = Color.Black)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "Torch", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(
                onClick = {
                    onMuteActiveApp()
                    onDismiss()
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6C5CE7))
            ) {
                Icon(imageVector = Icons.Default.NotificationsOff, contentDescription = "Mute App", tint = Color.White)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "Mute App", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD63031))
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close Menu", tint = Color.White)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "Close", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun ExpandedCallView(
    callState: CallState,
    onEndCall: () -> Unit,
    onToggleMute: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2D3436)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Contact Avatar",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = callState.callerName.ifBlank { "Mobile Call" },
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val mins = callState.callDurationSeconds / 60
                val secs = callState.callDurationSeconds % 60
                Text(
                    text = if (callState.isRinging) "Incoming Call..." else String.format("%02d:%02d", mins, secs),
                    color = if (callState.isRinging) Color(0xFFFF7675) else VibrantGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            IconButton(
                onClick = onToggleMute,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (callState.isMuted) Color(0xFFE17055) else Color(0xFF636E72))
            ) {
                Icon(
                    imageVector = if (callState.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Mute",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            IconButton(
                onClick = onEndCall,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD63031))
            ) {
                Icon(
                    imageVector = Icons.Default.CallEnd,
                    contentDescription = "End Call",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun ExpandedTimerView(
    timerState: TimerState,
    onAddMinute: () -> Unit,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFDCB6E).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "Timer",
                    tint = Color(0xFFFDCB6E),
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = timerState.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                val mins = timerState.remainingSeconds / 60
                val secs = timerState.remainingSeconds % 60
                Text(
                    text = String.format("%02d:%02d", mins, secs),
                    color = Color(0xFFFDCB6E),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onAddMinute,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0984E3)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "+1 min", fontSize = 12.sp, color = Color.White)
            }

            IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun ExpandedBluetoothView(bluetoothState: BluetoothDeviceState) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Headphones,
            contentDescription = "Bluetooth Connected",
            tint = Color(0xFF74B9FF),
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = "Connected Accessory",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
            Text(
                text = bluetoothState.deviceName,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ExpandedMediaView(
    mediaState: MediaState,
    paletteColors: IslandPaletteColors = IslandPaletteColors(),
    onPlayPauseToggle: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (mediaState.albumArt != null) {
                Image(
                    bitmap = mediaState.albumArt.asImageBitmap(),
                    contentDescription = "Album Cover",
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(paletteColors.mutedBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Music",
                        tint = paletteColors.vibrantAccent,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mediaState.title.ifBlank { "Unknown Track" },
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = mediaState.artist.ifBlank { "Unknown Artist" },
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        val progress = if (mediaState.durationMs > 0) {
            (mediaState.positionMs.toFloat() / mediaState.durationMs.toFloat()).coerceIn(0f, 1f)
        } else 0f

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = paletteColors.vibrantAccent,
            trackColor = Color.White.copy(alpha = 0.2f)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onSkipPrevious) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(paletteColors.vibrantAccent)
                    .clickable { onPlayPauseToggle() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (mediaState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color.Black,
                    modifier = Modifier.size(26.dp)
                )
            }

            IconButton(onClick = onSkipNext) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun ExpandedNotificationView(
    notification: NotificationItem,
    onDismiss: () -> Unit,
    onSendReply: (String) -> Unit = {}
) {
    var replyText by remember { mutableStateOf("") }
    var isReplying by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (notification.icon != null) {
                Image(
                    bitmap = notification.icon.asImageBitmap(),
                    contentDescription = "App Icon",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0984E3)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notification",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.appName,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = notification.title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (isReplying) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    placeholder = { Text("Type reply...", fontSize = 12.sp, color = Color.Gray) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF22222E),
                        unfocusedContainerColor = Color(0xFF16161E)
                    ),
                    singleLine = true
                )
                IconButton(
                    onClick = {
                        if (replyText.isNotBlank()) {
                            onSendReply(replyText)
                            isReplying = false
                            replyText = ""
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color(0xFF00B894))
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.message,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { isReplying = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0984E3)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Reply", fontSize = 11.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ExpandedChargingView(chargingState: ChargingState) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Bolt,
            contentDescription = "Charging",
            tint = VibrantGreen,
            modifier = Modifier.size(42.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = "Charging Battery",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${chargingState.batteryLevel}% Charged",
                color = VibrantGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ExpandedDefaultView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "MyIsland • Active Multi-Session",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
