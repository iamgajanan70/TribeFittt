package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.MonoOutlineVariant
import com.example.ui.theme.MonoPrimary
import com.example.ui.theme.MonoSurfaceContainerHigh
import java.io.File

@Composable
fun UserAvatar(
    avatarUrl: String?,
    fullName: String?,
    size: Dp = 44.dp,
    borderWidth: Dp = 1.dp,
    borderColor: Color = Color.Unspecified,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val initialLetter = fullName?.trim()?.firstOrNull()?.uppercaseChar()?.toString() ?: "U"
    val actualBorderColor = if (borderColor != Color.Unspecified) borderColor else MonoOutlineVariant

    // Check if avatar is a Base64 string
    val base64Bitmap = remember(avatarUrl) {
        if (!avatarUrl.isNullOrBlank() && avatarUrl.startsWith("data:image")) {
            try {
                val pureBase64 = avatarUrl.substringAfter("base64,")
                val decodedBytes = Base64.decode(pureBase64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    // Check if avatar is a local file
    val localFile = remember(avatarUrl) {
        if (!avatarUrl.isNullOrBlank() && (avatarUrl.startsWith("/") || avatarUrl.startsWith("file://"))) {
            val path = avatarUrl.removePrefix("file://")
            val f = File(path)
            if (f.exists()) f else null
        } else {
            null
        }
    }

    val hasImageSource = base64Bitmap != null || localFile != null || (!avatarUrl.isNullOrBlank() && avatarUrl.startsWith("http"))

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MonoSurfaceContainerHigh)
            .border(borderWidth, actualBorderColor, CircleShape)
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            base64Bitmap != null -> {
                Image(
                    bitmap = base64Bitmap.asImageBitmap(),
                    contentDescription = "User Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            localFile != null || (!avatarUrl.isNullOrBlank() && avatarUrl.startsWith("http")) -> {
                val imageModel = localFile ?: avatarUrl
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageModel)
                        .crossfade(true)
                        .build(),
                    contentDescription = "User Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                        InitialsAvatar(fullName = fullName, initialLetter = initialLetter, size = size)
                    },
                    error = {
                        InitialsAvatar(fullName = fullName, initialLetter = initialLetter, size = size)
                    }
                )
            }
            else -> {
                InitialsAvatar(fullName = fullName, initialLetter = initialLetter, size = size)
            }
        }
    }
}

@Composable
private fun InitialsAvatar(
    fullName: String?,
    initialLetter: String,
    size: Dp
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MonoPrimary),
        contentAlignment = Alignment.Center
    ) {
        if (fullName.isNullOrBlank()) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User Avatar",
                tint = Color.White,
                modifier = Modifier.size(size * 0.55f)
            )
        } else {
            Text(
                text = initialLetter,
                fontSize = (size.value * 0.42f).sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
